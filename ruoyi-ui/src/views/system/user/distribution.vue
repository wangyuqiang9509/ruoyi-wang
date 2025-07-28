<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="用户名称" prop="userName">
        <el-input
          v-model="queryParams.userName"
          placeholder="请输入用户名称"
          clearable
          style="width: 240px"
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="手机号码" prop="phonenumber">
        <el-input
          v-model="queryParams.phonenumber"
          placeholder="请输入手机号码"
          clearable
          style="width: 240px"
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="会员等级" prop="memberLevel">
        <el-select v-model="queryParams.memberLevel" placeholder="会员等级" clearable style="width: 240px">
          <el-option key="0" label="普通用户" value="0" />
          <el-option key="1" label="普通会员" value="1" />
          <el-option key="2" label="金牌会员" value="2" />
        </el-select>
      </el-form-item>
      <el-form-item label="团队级别" prop="teamLevel">
        <el-select v-model="queryParams.teamLevel" placeholder="团队级别" clearable style="width: 240px">
          <el-option key="0" label="无" value="0" />
          <el-option key="1" label="经理" value="1" />
          <el-option key="2" label="总监" value="2" />
          <el-option key="3" label="合伙人" value="3" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button
          type="primary"
          plain
          icon="el-icon-plus"
          size="mini"
          @click="handleAdd"
          v-hasPermi="['system:user:add']"
        >新增</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="userList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="50" align="center" />
      <el-table-column label="用户编号" align="center" key="userId" prop="userId" v-if="columns[0].visible" />
      <el-table-column label="用户名称" align="center" key="userName" prop="userName" v-if="columns[1].visible" :show-overflow-tooltip="true" />
      <el-table-column label="用户昵称" align="center" key="nickName" prop="nickName" v-if="columns[2].visible" :show-overflow-tooltip="true" />
      <el-table-column label="手机号码" align="center" key="phonenumber" prop="phonenumber" v-if="columns[3].visible" width="120" />
      <el-table-column label="状态" align="center" key="status" v-if="columns[4].visible">
        <template slot-scope="scope">
          <el-switch
            v-model="scope.row.status"
            active-value="0"
            inactive-value="1"
            @change="handleStatusChange(scope.row)"
          ></el-switch>
        </template>
      </el-table-column>
      <el-table-column label="推荐人" align="center" key="referrerName" prop="referrerName" v-if="columns[5].visible" :show-overflow-tooltip="true" />
      <el-table-column label="会员等级" align="center" key="memberLevel" v-if="columns[6].visible">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.member_level" :value="scope.row.memberLevel"/>
        </template>
      </el-table-column>
      <el-table-column label="团队级别" align="center" key="teamLevel" v-if="columns[7].visible">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.team_level" :value="scope.row.teamLevel"/>
        </template>
      </el-table-column>
      <el-table-column label="账户余额" align="center" key="balance" prop="balance" v-if="columns[8].visible" width="120">
        <template slot-scope="scope">
          <span>{{ scope.row.balance || 0 }}</span>
        </template>
      </el-table-column>
      <el-table-column label="团队业绩" align="center" key="totalPerformance" prop="totalPerformance" v-if="columns[9].visible" width="120">
        <template slot-scope="scope">
          <span>{{ scope.row.totalPerformance || 0 }}</span>
        </template>
      </el-table-column>
      <el-table-column label="直推金牌" align="center" key="directGoldMembers" prop="directGoldMembers" v-if="columns[10].visible" width="100">
        <template slot-scope="scope">
          <span>{{ scope.row.directGoldMembers || 0 }}</span>
        </template>
      </el-table-column>
      <el-table-column label="创建时间" align="center" prop="createTime" v-if="columns[11].visible" width="160">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.createTime) }}</span>
        </template>
      </el-table-column>
      <el-table-column
        label="操作"
        align="center"
        width="300"
        class-name="small-padding fixed-width"
      >
        <template slot-scope="scope" v-if="scope.row.userId !== 1">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-view"
            @click="handleAccount(scope.row)"
            v-hasPermi="['system:user:query']"
          >账户管理</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-edit"
            @click="handleUpdate(scope.row)"
            v-hasPermi="['system:user:edit']"
          >修改</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-shopping-cart-2"
            @click="handleOrder(scope.row)"
            v-hasPermi="['system:user:edit']"
          >模拟下单</el-button>
          <el-dropdown size="mini" @command="(command) => handleCommand(command, scope.row)" v-hasPermi="['system:user:edit']">
            <el-button size="mini" type="text" icon="el-icon-d-arrow-right">更多</el-button>
            <el-dropdown-menu slot="dropdown">
              <el-dropdown-item command="handleReferrer" icon="el-icon-user">绑定推荐人</el-dropdown-item>
              <el-dropdown-item command="handleMember" icon="el-icon-medal">设置会员等级</el-dropdown-item>
              <el-dropdown-item command="handleLevel" icon="el-icon-trophy">升级团队级别</el-dropdown-item>
              <el-dropdown-item command="handleAgent" icon="el-icon-location">设置代理</el-dropdown-item>
            </el-dropdown-menu>
          </el-dropdown>
        </template>
      </el-table-column>
    </el-table>

    <pagination
      v-show="total>0"
      :total="total"
      :page.sync="queryParams.pageNum"
      :limit.sync="queryParams.pageSize"
      @pagination="getList"
    />

    <!-- 账户管理对话框 -->
    <el-dialog title="账户管理" :visible.sync="accountOpen" width="800px" append-to-body>
      <el-row :gutter="20">
        <el-col :span="12">
          <el-card class="box-card">
            <div slot="header" class="clearfix">
              <span>账户信息</span>
            </div>
            <div class="account-info">
              <p><strong>账户余额：</strong>{{ accountInfo.balance || 0 }} 元</p>
              <p><strong>会员等级：</strong>{{ getMemberLevelText(accountInfo.memberLevel) }}</p>
              <p><strong>团队级别：</strong>{{ getTeamLevelText(accountInfo.teamLevel) }}</p>
              <p><strong>团队业绩：</strong>{{ accountInfo.totalPerformance || 0 }} 元</p>
              <p><strong>直推金牌：</strong>{{ accountInfo.directGoldMembers || 0 }} 人</p>
              <p><strong>推荐人：</strong>{{ accountInfo.referrerName || '无' }}</p>
            </div>
          </el-card>
        </el-col>
        <el-col :span="12">
          <el-card class="box-card">
            <div slot="header" class="clearfix">
              <span>余额操作</span>
            </div>
            <el-form :model="balanceForm" :rules="balanceRules" ref="balanceForm" label-width="80px">
              <el-form-item label="操作类型" prop="type">
                <el-radio-group v-model="balanceForm.type">
                  <el-radio label="add">增加</el-radio>
                  <el-radio label="subtract">扣除</el-radio>
                </el-radio-group>
              </el-form-item>
              <el-form-item label="金额" prop="amount">
                <el-input-number v-model="balanceForm.amount" :min="0.01" :precision="2" style="width: 100%"></el-input-number>
              </el-form-item>
              <el-form-item label="备注" prop="remark">
                <el-input v-model="balanceForm.remark" type="textarea" placeholder="请输入备注"></el-input>
              </el-form-item>
              <el-form-item>
                <el-button type="primary" @click="submitBalance">确定</el-button>
                <el-button @click="resetBalance">重置</el-button>
              </el-form-item>
            </el-form>
          </el-card>
        </el-col>
      </el-row>
      
      <el-divider></el-divider>
      
      <div>
        <h4>账户流水</h4>
        <el-table :data="flowList" style="width: 100%" max-height="300">
          <el-table-column prop="createTime" label="时间" width="160">
            <template slot-scope="scope">
              <span>{{ parseTime(scope.row.createTime) }}</span>
            </template>
          </el-table-column>
          <el-table-column prop="flowType" label="类型" width="80">
            <template slot-scope="scope">
              <el-tag :type="scope.row.flowType === 1 ? 'success' : 'danger'">
                {{ scope.row.flowType === 1 ? '收入' : '支出' }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="amount" label="金额" width="100"></el-table-column>
          <el-table-column prop="balanceAfter" label="余额" width="100"></el-table-column>
          <el-table-column prop="businessType" label="业务类型" width="120"></el-table-column>
          <el-table-column prop="remark" label="备注" show-overflow-tooltip></el-table-column>
        </el-table>
      </div>
    </el-dialog>

    <!-- 其他对话框省略，实际项目中需要完整实现 -->
  </div>
</template>

<script>
import { listUser, getUser, delUser, addUser, updateUser, resetUserPwd, changeUserStatus } from "@/api/system/user";

export default {
  name: "UserDistribution",
  dicts: ['member_level', 'team_level'],
  data() {
    return {
      // 遮罩层
      loading: true,
      // 选中数组
      ids: [],
      // 非单个禁用
      single: true,
      // 非多个禁用
      multiple: true,
      // 显示搜索条件
      showSearch: true,
      // 总条数
      total: 0,
      // 用户表格数据
      userList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 账户管理对话框
      accountOpen: false,
      // 账户信息
      accountInfo: {},
      // 流水列表
      flowList: [],
      // 余额操作表单
      balanceForm: {
        userId: null,
        type: 'add',
        amount: null,
        remark: ''
      },
      // 余额操作规则
      balanceRules: {
        amount: [
          { required: true, message: "金额不能为空", trigger: "blur" }
        ],
        remark: [
          { required: true, message: "备注不能为空", trigger: "blur" }
        ]
      },
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        userName: undefined,
        phonenumber: undefined,
        status: undefined,
        memberLevel: undefined,
        teamLevel: undefined
      },
      // 列信息
      columns: [
        { key: 0, label: `用户编号`, visible: true },
        { key: 1, label: `用户名称`, visible: true },
        { key: 2, label: `用户昵称`, visible: true },
        { key: 3, label: `手机号码`, visible: true },
        { key: 4, label: `状态`, visible: true },
        { key: 5, label: `推荐人`, visible: true },
        { key: 6, label: `会员等级`, visible: true },
        { key: 7, label: `团队级别`, visible: true },
        { key: 8, label: `账户余额`, visible: true },
        { key: 9, label: `团队业绩`, visible: true },
        { key: 10, label: `直推金牌`, visible: true },
        { key: 11, label: `创建时间`, visible: true }
      ]
    };
  },
  created() {
    this.getList();
  },
  methods: {
    /** 查询用户列表 */
    getList() {
      this.loading = true;
      listUser(this.queryParams).then(response => {
        this.userList = response.rows;
        this.total = response.total;
        this.loading = false;
      });
    },
    /** 搜索按钮操作 */
    handleQuery() {
      this.queryParams.pageNum = 1;
      this.getList();
    },
    /** 重置按钮操作 */
    resetQuery() {
      this.resetForm("queryForm");
      this.handleQuery();
    },
    // 多选框选中数据
    handleSelectionChange(selection) {
      this.ids = selection.map(item => item.userId)
      this.single = selection.length !== 1
      this.multiple = !selection.length
    },
    /** 新增按钮操作 */
    handleAdd() {
      // 跳转到用户新增页面
      this.$router.push("/system/user");
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      // 跳转到用户修改页面
      this.$router.push("/system/user");
    },
    /** 账户管理 */
    handleAccount(row) {
      this.accountOpen = true;
      this.balanceForm.userId = row.userId;
      // 获取账户信息
      this.getAccountInfo(row.userId);
      // 获取流水记录
      this.getFlowList(row.userId);
    },
    /** 获取账户信息 */
    getAccountInfo(userId) {
      // 调用API获取账户信息
      // getUserAccount(userId).then(response => {
      //   this.accountInfo = response.data;
      // });
    },
    /** 获取流水列表 */
    getFlowList(userId) {
      // 调用API获取流水列表
      // getUserAccountFlow(userId).then(response => {
      //   this.flowList = response.rows;
      // });
    },
    /** 提交余额操作 */
    submitBalance() {
      this.$refs["balanceForm"].validate(valid => {
        if (valid) {
          const amount = this.balanceForm.type === 'add' ? this.balanceForm.amount : -this.balanceForm.amount;
          // 调用API更新余额
          // updateUserBalance({
          //   userId: this.balanceForm.userId,
          //   amount: amount,
          //   remark: this.balanceForm.remark
          // }).then(response => {
          //   this.$modal.msgSuccess("操作成功");
          //   this.resetBalance();
          //   this.getAccountInfo(this.balanceForm.userId);
          //   this.getFlowList(this.balanceForm.userId);
          // });
        }
      });
    },
    /** 重置余额表单 */
    resetBalance() {
      this.balanceForm = {
        userId: this.balanceForm.userId,
        type: 'add',
        amount: null,
        remark: ''
      };
      this.resetForm("balanceForm");
    },
    /** 模拟下单 */
    handleOrder(row) {
      // 实现模拟下单功能
    },
    /** 更多操作 */
    handleCommand(command, row) {
      switch (command) {
        case "handleReferrer":
          this.handleReferrer(row);
          break;
        case "handleMember":
          this.handleMember(row);
          break;
        case "handleLevel":
          this.handleLevel(row);
          break;
        case "handleAgent":
          this.handleAgent(row);
          break;
      }
    },
    /** 绑定推荐人 */
    handleReferrer(row) {
      // 实现绑定推荐人功能
    },
    /** 设置会员等级 */
    handleMember(row) {
      // 实现设置会员等级功能
    },
    /** 升级团队级别 */
    handleLevel(row) {
      // 实现升级团队级别功能
    },
    /** 设置代理 */
    handleAgent(row) {
      // 实现设置代理功能
    },
    /** 用户状态修改 */
    handleStatusChange(row) {
      let text = row.status === "0" ? "启用" : "停用";
      this.$modal.confirm('确认要"' + text + '""' + row.userName + '"用户吗？').then(function() {
        return changeUserStatus(row.userId, row.status);
      }).then(() => {
        this.$modal.msgSuccess(text + "成功");
      }).catch(function() {
        row.status = row.status === "0" ? "1" : "0";
      });
    },
    /** 获取会员等级文本 */
    getMemberLevelText(level) {
      const levels = { 0: '普通用户', 1: '普通会员', 2: '金牌会员' };
      return levels[level] || '普通用户';
    },
    /** 获取团队级别文本 */
    getTeamLevelText(level) {
      const levels = { 0: '无', 1: '经理', 2: '总监', 3: '合伙人' };
      return levels[level] || '无';
    }
  }
};
</script>

<style scoped>
.account-info p {
  margin: 10px 0;
  font-size: 14px;
}
</style>
