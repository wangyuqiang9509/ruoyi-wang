<template>
  <div class="app-container">
    <el-row :gutter="20">
      <splitpanes :horizontal="this.$store.getters.device === 'mobile'" class="default-theme">
        <!--部门数据-->
        <pane size="16">
          <el-col>
            <div class="head-container">
              <el-input v-model="deptName" placeholder="请输入部门名称" clearable size="small" prefix-icon="el-icon-search" style="margin-bottom: 20px" />
            </div>
            <div class="head-container">
              <el-tree :data="deptOptions" :props="defaultProps" :expand-on-click-node="false" :filter-node-method="filterNode" ref="tree" node-key="id" default-expand-all highlight-current @node-click="handleNodeClick" />
            </div>
          </el-col>
        </pane>
        <!--用户数据-->
        <pane size="84">
          <el-col>
            <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
              <el-form-item label="用户名称" prop="userName">
                <el-input v-model="queryParams.userName" placeholder="请输入用户名称" clearable style="width: 240px" @keyup.enter.native="handleQuery" />
              </el-form-item>
              <el-form-item label="手机号码" prop="phonenumber">
                <el-input v-model="queryParams.phonenumber" placeholder="请输入手机号码" clearable style="width: 240px" @keyup.enter.native="handleQuery" />
              </el-form-item>
              <el-form-item label="状态" prop="status">
                <el-select v-model="queryParams.status" placeholder="用户状态" clearable style="width: 240px">
                  <el-option v-for="dict in dict.type.sys_normal_disable" :key="dict.value" :label="dict.label" :value="dict.value" />
                </el-select>
              </el-form-item>
              <el-form-item label="创建时间">
                <el-date-picker v-model="dateRange" style="width: 240px" value-format="yyyy-MM-dd" type="daterange" range-separator="-" start-placeholder="开始日期" end-placeholder="结束日期"></el-date-picker>
              </el-form-item>
              <el-form-item>
                <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
                <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
              </el-form-item>
            </el-form>

            <el-row :gutter="10" class="mb8">
              <el-col :span="1.5">
                <el-button type="primary" plain icon="el-icon-plus" size="mini" @click="handleAdd" v-hasPermi="['system:user:add']">新增</el-button>
              </el-col>
              <el-col :span="1.5">
                <el-button type="success" plain icon="el-icon-edit" size="mini" :disabled="single" @click="handleUpdate" v-hasPermi="['system:user:edit']">修改</el-button>
              </el-col>
              <el-col :span="1.5">
                <el-button type="danger" plain icon="el-icon-delete" size="mini" :disabled="multiple" @click="handleDelete" v-hasPermi="['system:user:remove']">删除</el-button>
              </el-col>
              <el-col :span="1.5">
                <el-button type="info" plain icon="el-icon-upload2" size="mini" @click="handleImport" v-hasPermi="['system:user:import']">导入</el-button>
              </el-col>
              <el-col :span="1.5">
                <el-button type="warning" plain icon="el-icon-download" size="mini" @click="handleExport" v-hasPermi="['system:user:export']">导出</el-button>
              </el-col>
              <el-col :span="1.5">
                <el-button type="primary" plain icon="el-icon-goods" size="mini" @click="handleProductManage" v-hasPermi="['system:product:list']">商品管理</el-button>
              </el-col>
              <el-col :span="1.5">
                <el-button type="success" plain icon="el-icon-setting" size="mini" @click="handleDistributionConfig" v-hasPermi="['system:distributionConfig:list']">分销配置管理</el-button>
              </el-col>
              <right-toolbar :showSearch.sync="showSearch" @queryTable="getList" :columns="columns"></right-toolbar>
            </el-row>

            <el-table v-loading="loading" :data="userList" @selection-change="handleSelectionChange">
              <el-table-column type="selection" width="50" align="center" />
              <el-table-column label="用户编号" align="center" key="userId" prop="userId" v-if="columns[0].visible" />
              <el-table-column label="用户名称" align="center" key="userName" prop="userName" v-if="columns[1].visible" :show-overflow-tooltip="true" />
              <el-table-column label="用户昵称" align="center" key="nickName" prop="nickName" v-if="columns[2].visible" :show-overflow-tooltip="true" />
              <el-table-column label="部门" align="center" key="deptName" prop="dept.deptName" v-if="columns[3].visible" :show-overflow-tooltip="true" />
              <el-table-column label="手机号码" align="center" key="phonenumber" prop="phonenumber" v-if="columns[4].visible" width="120" />
              <el-table-column label="状态" align="center" key="status" v-if="columns[5].visible">
                <template slot-scope="scope">
                  <el-switch v-model="scope.row.status" active-value="0" inactive-value="1" @change="handleStatusChange(scope.row)"></el-switch>
                </template>
              </el-table-column>
              <el-table-column label="推荐人" align="center" key="referrerName" prop="referrerName" v-if="columns[6].visible" :show-overflow-tooltip="true" />
              <el-table-column label="会员等级" align="center" key="memberLevel" v-if="columns[7].visible">
                <template slot-scope="scope">
                  <dict-tag :options="dict.type.member_level" :value="scope.row.memberLevel"/>
                </template>
              </el-table-column>
              <el-table-column label="团队级别" align="center" key="teamLevel" v-if="columns[8].visible">
                <template slot-scope="scope">
                  <dict-tag :options="dict.type.team_level" :value="scope.row.teamLevel"/>
                </template>
              </el-table-column>
              <el-table-column label="代理级别" align="center" key="agentLevel" v-if="columns[9].visible" width="150">
                <template slot-scope="scope">
                  <div>
                    <dict-tag :options="dict.type.agent_level" :value="scope.row.agentLevel"/>
                    <div v-if="scope.row.agentLevel > 0" style="font-size: 12px; color: #909399; margin-top: 2px;">
                      {{ getAgentAreaText(scope.row.agentLevel, scope.row.agentProvince, scope.row.agentCity) }}
                    </div>
                  </div>
                </template>
              </el-table-column>
              <el-table-column label="账户余额" align="center" key="balance" prop="balance" v-if="columns[10].visible" width="120">
                <template slot-scope="scope">
                  <span>{{ scope.row.balance || 0 }}</span>
                </template>
              </el-table-column>
              <el-table-column label="团队业绩" align="center" key="totalPerformance" prop="totalPerformance" v-if="columns[11].visible" width="120">
                <template slot-scope="scope">
                  <span>{{ scope.row.totalPerformance || 0 }}</span>
                </template>
              </el-table-column>
              <el-table-column label="直推金牌" align="center" key="directGoldMembers" prop="directGoldMembers" v-if="columns[12].visible" width="100">
                <template slot-scope="scope">
                  <span>{{ scope.row.directGoldMembers || 0 }}</span>
                </template>
              </el-table-column>
              <el-table-column label="创建时间" align="center" prop="createTime" v-if="columns[13].visible" width="160">
                <template slot-scope="scope">
                  <span>{{ parseTime(scope.row.createTime) }}</span>
                </template>
              </el-table-column>
              <el-table-column label="操作" align="center" width="350" class-name="small-padding fixed-width">
                <template slot-scope="scope" v-if="scope.row.userId !== 1">
                  <el-button size="mini" type="text" icon="el-icon-view" @click="handleAccount(scope.row)" v-hasPermi="['system:user:query']">账户管理</el-button>
                  <el-button
                    size="mini"
                    type="text"
                    icon="el-icon-user"
                    @click="handleReferrer(scope.row)"
                    v-hasPermi="['system:user:edit']"
                    :disabled="!!scope.row.referrerName"
                    :class="{ 'disabled-button': !!scope.row.referrerName }"
                  >
                    {{ scope.row.referrerName ? '已绑定推荐人' : '绑定推荐人' }}
                  </el-button>
                  <el-button size="mini" type="text" icon="el-icon-medal" @click="handleMember(scope.row)" v-hasPermi="['system:user:edit']">设置会员等级</el-button>
                  <el-button size="mini" type="text" icon="el-icon-trophy" @click="handleLevel(scope.row)" v-hasPermi="['system:user:edit']">升级团队级别</el-button>
                  <el-button size="mini" type="text" icon="el-icon-location" @click="handleAgent(scope.row)" v-hasPermi="['system:user:edit']">设置代理</el-button>
                  <el-button size="mini" type="text" icon="el-icon-shopping-cart-2" @click="handleOrder(scope.row)" v-hasPermi="['system:user:edit']">模拟下单</el-button>
                  <el-dropdown size="mini" @command="(command) => handleCommand(command, scope.row)" v-hasPermi="['system:user:resetPwd', 'system:user:edit']">
                    <el-button size="mini" type="text" icon="el-icon-d-arrow-right">更多</el-button>
                    <el-dropdown-menu slot="dropdown">
                      <el-dropdown-item command="handleUpdate" icon="el-icon-edit" v-hasPermi="['system:user:edit']">修改用户</el-dropdown-item>
                      <el-dropdown-item command="handleDelete" icon="el-icon-delete" v-hasPermi="['system:user:remove']">删除用户</el-dropdown-item>
                      <el-dropdown-item command="handleResetPwd" icon="el-icon-key" v-hasPermi="['system:user:resetPwd']">重置密码</el-dropdown-item>
                      <el-dropdown-item command="handleAuthRole" icon="el-icon-circle-check" v-hasPermi="['system:user:edit']">分配角色</el-dropdown-item>
                    </el-dropdown-menu>
                  </el-dropdown>
                </template>
              </el-table-column>
            </el-table>

            <pagination v-show="total > 0" :total="total" :page.sync="queryParams.pageNum" :limit.sync="queryParams.pageSize" @pagination="getList" />
          </el-col>
        </pane>
      </splitpanes>
    </el-row>

    <!-- 添加或修改用户配置对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="600px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="80px">
        <el-row>
          <el-col :span="12">
            <el-form-item label="用户昵称" prop="nickName">
              <el-input v-model="form.nickName" placeholder="请输入用户昵称" maxlength="30" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="归属部门" prop="deptId">
              <treeselect v-model="form.deptId" :options="enabledDeptOptions" :show-count="true" placeholder="请选择归属部门" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="手机号码" prop="phonenumber">
              <el-input v-model="form.phonenumber" placeholder="请输入手机号码" maxlength="11" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="邮箱" prop="email">
              <el-input v-model="form.email" placeholder="请输入邮箱" maxlength="50" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item v-if="form.userId == undefined" label="用户名称" prop="userName">
              <el-input v-model="form.userName" placeholder="请输入用户名称" maxlength="30" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item v-if="form.userId == undefined" label="用户密码" prop="password">
              <el-input v-model="form.password" placeholder="请输入用户密码" type="password" maxlength="20" show-password />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="用户性别">
              <el-select v-model="form.sex" placeholder="请选择性别">
                <el-option v-for="dict in dict.type.sys_user_sex" :key="dict.value" :label="dict.label" :value="dict.value"></el-option>
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="状态">
              <el-radio-group v-model="form.status">
                <el-radio v-for="dict in dict.type.sys_normal_disable" :key="dict.value" :label="dict.value">{{ dict.label }}</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="岗位">
              <el-select v-model="form.postIds" multiple placeholder="请选择岗位">
                <el-option v-for="item in postOptions" :key="item.postId" :label="item.postName" :value="item.postId" :disabled="item.status == 1" ></el-option>
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="角色">
              <el-select v-model="form.roleIds" multiple placeholder="请选择角色">
                <el-option v-for="item in roleOptions" :key="item.roleId" :label="item.roleName" :value="item.roleId" :disabled="item.status == 1"></el-option>
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="24">
            <el-form-item label="备注">
              <el-input v-model="form.remark" type="textarea" placeholder="请输入内容"></el-input>
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitForm">确 定</el-button>
        <el-button @click="cancel">取 消</el-button>
      </div>
    </el-dialog>

    <!-- 用户导入对话框 -->
    <el-dialog :title="upload.title" :visible.sync="upload.open" width="400px" append-to-body>
      <el-upload ref="upload" :limit="1" accept=".xlsx, .xls" :headers="upload.headers" :action="upload.url + '?updateSupport=' + upload.updateSupport" :disabled="upload.isUploading" :on-progress="handleFileUploadProgress" :on-success="handleFileSuccess" :auto-upload="false" drag>
        <i class="el-icon-upload"></i>
        <div class="el-upload__text">将文件拖到此处，或<em>点击上传</em></div>
        <div class="el-upload__tip text-center" slot="tip">
          <div class="el-upload__tip" slot="tip">
            <el-checkbox v-model="upload.updateSupport" />是否更新已经存在的用户数据
          </div>
          <span>仅允许导入xls、xlsx格式文件。</span>
          <el-link type="primary" :underline="false" style="font-size: 12px; vertical-align: baseline" @click="importTemplate">下载模板</el-link>
        </div>
      </el-upload>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitFileForm">确 定</el-button>
        <el-button @click="upload.open = false">取 消</el-button>
      </div>
    </el-dialog>

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
              <p><strong>代理区域：</strong>{{ getAgentAreaText(accountInfo.agentLevel, accountInfo.agentProvince, accountInfo.agentCity) }}</p>
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

    <!-- 模拟下单对话框 -->
    <el-dialog title="模拟下单" :visible.sync="orderOpen" width="600px" append-to-body>
      <el-form :model="orderForm" :rules="orderRules" ref="orderForm" label-width="100px">
        <el-form-item label="商品" prop="productId">
          <el-select v-model="orderForm.productId" placeholder="请选择商品" style="width: 100%" @change="handleProductChange">
            <el-option v-for="product in productList" :key="product.productId" :label="product.productName" :value="product.productId"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="商品价格">
          <el-input v-model="orderForm.productPrice" placeholder="商品价格" :disabled="true" style="width: 100%">
            <template slot="append">元</template>
          </el-input>
        </el-form-item>
        <!-- 实付款回显框 - 选择商品后显示 -->
        <el-form-item label="实付款" v-if="orderForm.actualPrice !== undefined">
          <el-input v-model="orderForm.actualPrice" placeholder="实付款金额" :disabled="true" style="width: 100%">
            <template slot="append">元</template>
          </el-input>
          <div style="color: #909399; font-size: 12px; margin-top: 4px;">
            <i class="el-icon-info"></i>
            <span v-if="orderForm.isRepurchase && orderForm.discountAmount > 0">
              复购商品享受{{ 100 - orderForm.discountRate }}%的折扣优惠，优惠{{ orderForm.discountAmount }}元
            </span>
            <span v-else-if="orderForm.isRepurchase">
              复购商品（当前用户等级无优惠）
            </span>
            <span v-else>
              首次购买此商品
            </span>
          </div>
        </el-form-item>
        <el-form-item label="收货省份" prop="province">
          <el-select v-model="orderForm.province" placeholder="请选择省份" style="width: 100%" @change="handleOrderProvinceChange">
            <el-option v-for="province in provinceList" :key="province.value" :label="province.label" :value="province.value"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="收货城市" prop="city">
          <el-select v-model="orderForm.city" placeholder="请选择城市" style="width: 100%" :disabled="!orderForm.province">
            <el-option v-for="city in orderCityList" :key="city.value" :label="city.label" :value="city.value"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="详细地址" prop="address">
          <el-input v-model="orderForm.address" placeholder="请输入详细收货地址" type="textarea" :rows="3"></el-input>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitOrder">确 定</el-button>
        <el-button @click="orderOpen = false">取 消</el-button>
      </div>
    </el-dialog>

    <!-- 绑定推荐人对话框 -->
    <el-dialog title="绑定推荐人" :visible.sync="referrerOpen" width="400px" append-to-body>
      <el-form :model="referrerForm" :rules="referrerRules" ref="referrerForm" label-width="100px">
        <el-form-item label="推荐人手机号" prop="referrerPhone">
          <el-input v-model="referrerForm.referrerPhone" placeholder="请输入推荐人手机号" maxlength="11"></el-input>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitReferrer">确 定</el-button>
        <el-button @click="referrerOpen = false">取 消</el-button>
      </div>
    </el-dialog>

    <!-- 设置会员等级对话框 -->
    <el-dialog title="设置会员等级" :visible.sync="memberLevelOpen" width="400px" append-to-body>
      <el-form :model="memberLevelForm" :rules="memberLevelRules" ref="memberLevelForm" label-width="100px">
        <el-form-item label="当前等级">
          <dict-tag :options="dict.type.member_level" :value="memberLevelForm.currentLevel"/>
        </el-form-item>
        <el-form-item label="新等级" prop="memberLevel">
          <el-select v-model="memberLevelForm.memberLevel" placeholder="请选择会员等级" style="width: 100%">
            <el-option
              v-for="dict in availableMemberLevels"
              :key="dict.value"
              :label="dict.label"
              :value="dict.value">
            </el-option>
          </el-select>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitMemberLevel">确 定</el-button>
        <el-button @click="memberLevelOpen = false">取 消</el-button>
      </div>
    </el-dialog>

    <!-- 升级团队级别对话框 -->
    <el-dialog title="升级团队级别" :visible.sync="teamLevelOpen" width="400px" append-to-body>
      <el-form :model="teamLevelForm" :rules="teamLevelRules" ref="teamLevelForm" label-width="100px">
        <el-form-item label="当前级别">
          <dict-tag :options="dict.type.team_level" :value="teamLevelForm.currentLevel"/>
        </el-form-item>
        <el-form-item label="目标级别" prop="teamLevel">
          <el-select v-model="teamLevelForm.teamLevel" placeholder="请选择团队级别" style="width: 100%">
            <el-option
              v-for="dict in dict.type.team_level"
              :key="dict.value"
              :label="dict.label"
              :value="dict.value"
              :disabled="parseInt(dict.value) <= teamLevelForm.currentLevel">
            </el-option>
          </el-select>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitTeamLevel">确 定</el-button>
        <el-button @click="teamLevelOpen = false">取 消</el-button>
      </div>
    </el-dialog>

    <!-- 设置代理对话框 -->
    <el-dialog title="设置代理" :visible.sync="agentOpen" width="500px" append-to-body>
      <el-form :model="agentForm" :rules="agentRules" ref="agentForm" label-width="100px">
        <el-form-item label="用户信息">
          <span>{{ agentForm.userName }}（{{ agentForm.nickName }}）</span>
        </el-form-item>
        <el-form-item label="代理类型" prop="agentType">
          <el-radio-group v-model="agentForm.agentType" @change="handleAgentTypeChange">
            <el-radio :label="0">取消代理</el-radio>
            <el-radio :label="1">省级代理</el-radio>
            <el-radio :label="2">市级代理</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="省份" prop="province" v-if="agentForm.agentType > 0">
          <el-select v-model="agentForm.province" placeholder="请选择省份" style="width: 100%" @change="handleAgentProvinceChange">
            <el-option
              v-for="province in provinceList"
              :key="province.value"
              :label="province.label"
              :value="province.value">
            </el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="城市" prop="city" v-if="agentForm.agentType === 2">
          <el-select v-model="agentForm.city" placeholder="请选择城市" style="width: 100%">
            <el-option
              v-for="city in cityList"
              :key="city.value"
              :label="city.label"
              :value="city.value">
            </el-option>
          </el-select>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitAgent">确 定</el-button>
        <el-button @click="agentOpen = false">取 消</el-button>
      </div>
    </el-dialog>

    <!-- 商品管理对话框 -->
    <el-dialog :title="productManage.title" :visible.sync="productManage.open" width="1200px" append-to-body>
      <div class="product-manage-container">
        <!-- 搜索区域 -->
        <el-form :model="productQueryParams" ref="productQueryForm" size="small" :inline="true" v-show="productShowSearch" label-width="68px">
          <el-form-item label="商品名称" prop="productName">
            <el-input
              v-model="productQueryParams.productName"
              placeholder="请输入商品名称"
              clearable
              @keyup.enter.native="handleProductQuery"
            />
          </el-form-item>
          <el-form-item label="状态" prop="status">
            <el-select v-model="productQueryParams.status" placeholder="商品状态" clearable>
              <el-option label="正常" value="0" />
              <el-option label="停用" value="1" />
            </el-select>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" icon="el-icon-search" size="mini" @click="handleProductQuery">搜索</el-button>
            <el-button icon="el-icon-refresh" size="mini" @click="resetProductQuery">重置</el-button>
          </el-form-item>
        </el-form>

        <!-- 操作按钮区域 -->
        <el-row :gutter="10" class="mb8">
          <el-col :span="1.5">
            <el-button
              type="primary"
              plain
              icon="el-icon-plus"
              size="mini"
              @click="handleProductAdd"
              v-hasPermi="['system:product:add']"
            >新增</el-button>
          </el-col>
          <el-col :span="1.5">
            <el-button
              type="success"
              plain
              icon="el-icon-edit"
              size="mini"
              :disabled="productSingle"
              @click="handleProductUpdate"
              v-hasPermi="['system:product:edit']"
            >修改</el-button>
          </el-col>
          <el-col :span="1.5">
            <el-button
              type="danger"
              plain
              icon="el-icon-delete"
              size="mini"
              :disabled="productMultiple"
              @click="handleProductDelete"
              v-hasPermi="['system:product:remove']"
            >删除</el-button>
          </el-col>
          <right-toolbar :showSearch.sync="productShowSearch" @queryTable="getProductList"></right-toolbar>
        </el-row>

        <!-- 商品列表 -->
        <el-table v-loading="productLoading" :data="productList" @selection-change="handleProductSelectionChange">
          <el-table-column type="selection" width="50" align="center" />
          <el-table-column label="商品ID" align="center" prop="productId" />
          <el-table-column label="商品名称" align="center" prop="productName" />
          <el-table-column label="商品价格" align="center" prop="price">
            <template slot-scope="scope">
              ¥{{ scope.row.price }}
            </template>
          </el-table-column>
          <el-table-column label="状态" align="center" prop="status">
            <template slot-scope="scope">
              <dict-tag :options="dict.type.sys_normal_disable" :value="scope.row.status"/>
            </template>
          </el-table-column>
          <el-table-column label="创建时间" align="center" prop="createTime" width="180">
            <template slot-scope="scope">
              <span>{{ parseTime(scope.row.createTime, '{y}-{m}-{d} {h}:{i}:{s}') }}</span>
            </template>
          </el-table-column>
          <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
            <template slot-scope="scope">
              <el-button
                size="mini"
                type="text"
                icon="el-icon-edit"
                @click="handleProductUpdate(scope.row)"
                v-hasPermi="['system:product:edit']"
              >修改</el-button>
              <el-button
                size="mini"
                type="text"
                icon="el-icon-delete"
                @click="handleProductDelete(scope.row)"
                v-hasPermi="['system:product:remove']"
              >删除</el-button>
            </template>
          </el-table-column>
        </el-table>

        <!-- 分页 -->
        <pagination
          v-show="productTotal>0"
          :total="productTotal"
          :page.sync="productQueryParams.pageNum"
          :limit.sync="productQueryParams.pageSize"
          @pagination="getProductManageList"
        />
      </div>
      <div slot="footer" class="dialog-footer">
        <el-button @click="productManage.open = false">关 闭</el-button>
      </div>
    </el-dialog>

    <!-- 添加或修改商品对话框 -->
    <el-dialog :title="productTitle" :visible.sync="productOpen" width="500px" append-to-body>
      <el-form ref="productForm" :model="productForm" :rules="productRules" label-width="80px">
        <el-form-item label="商品名称" prop="productName">
          <el-input v-model="productForm.productName" placeholder="请输入商品名称" />
        </el-form-item>
        <el-form-item label="商品价格" prop="price">
          <el-input-number v-model="productForm.price" :precision="2" :min="0" :max="999999.99" placeholder="请输入商品价格" />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="productForm.status">
            <el-radio label="0">正常</el-radio>
            <el-radio label="1">停用</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitProductForm">确 定</el-button>
        <el-button @click="cancelProduct">取 消</el-button>
      </div>
    </el-dialog>

    <!-- 分销配置管理对话框 -->
    <el-dialog :title="distributionConfigManage.title" :visible.sync="distributionConfigManage.open" width="1000px" append-to-body>
      <div class="distribution-config-container">
        <!-- 操作按钮区域 -->
        <el-row :gutter="10" class="mb8">
          <el-col :span="1.5">
            <el-button
              type="primary"
              plain
              icon="el-icon-plus"
              size="mini"
              @click="handleDistributionConfigAdd"
              v-hasPermi="['system:distributionConfig:add']"
            >新增</el-button>
          </el-col>
          <el-col :span="1.5">
            <el-button
              type="success"
              plain
              icon="el-icon-edit"
              size="mini"
              :disabled="distributionConfigSingle"
              @click="handleDistributionConfigUpdate"
              v-hasPermi="['system:distributionConfig:edit']"
            >修改</el-button>
          </el-col>
          <el-col :span="1.5">
            <el-button
              type="danger"
              plain
              icon="el-icon-delete"
              size="mini"
              :disabled="distributionConfigMultiple"
              @click="handleDistributionConfigDelete"
              v-hasPermi="['system:distributionConfig:remove']"
            >删除</el-button>
          </el-col>
        </el-row>

        <!-- 分销配置列表 -->
        <el-table v-loading="distributionConfigLoading" :data="distributionConfigList" @selection-change="handleDistributionConfigSelectionChange">
          <el-table-column type="selection" width="50" align="center" />
          <el-table-column label="配置ID" align="center" prop="configId" width="80" />
          <el-table-column label="配置描述" align="center" prop="configDesc" width="200" />
          <el-table-column label="配置值" align="center" prop="configValue" />
          <el-table-column label="创建时间" align="center" prop="createTime" width="180">
            <template slot-scope="scope">
              <span>{{ parseTime(scope.row.createTime, '{y}-{m}-{d} {h}:{i}:{s}') }}</span>
            </template>
          </el-table-column>
          <el-table-column label="操作" align="center" class-name="small-padding fixed-width" width="150">
            <template slot-scope="scope">
              <el-button
                size="mini"
                type="text"
                icon="el-icon-edit"
                @click="handleDistributionConfigUpdate(scope.row)"
                v-hasPermi="['system:distributionConfig:edit']"
              >修改</el-button>
              <el-button
                size="mini"
                type="text"
                icon="el-icon-delete"
                @click="handleDistributionConfigDelete(scope.row)"
                v-hasPermi="['system:distributionConfig:remove']"
              >删除</el-button>
            </template>
          </el-table-column>
        </el-table>

        <!-- 分页 -->
        <pagination
          v-show="distributionConfigTotal>0"
          :total="distributionConfigTotal"
          :page.sync="distributionConfigQueryParams.pageNum"
          :limit.sync="distributionConfigQueryParams.pageSize"
          @pagination="getDistributionConfigList"
        />
      </div>
      <div slot="footer" class="dialog-footer">
        <el-button @click="distributionConfigManage.open = false">关 闭</el-button>
      </div>
    </el-dialog>

    <!-- 添加或修改分销配置对话框 -->
    <el-dialog :title="distributionConfigTitle" :visible.sync="distributionConfigOpen" width="500px" append-to-body>
      <el-form ref="distributionConfigForm" :model="distributionConfigForm" :rules="distributionConfigRules" label-width="100px">
        <el-form-item label="配置键" prop="configKey">
          <el-input v-model="distributionConfigForm.configKey" placeholder="请输入配置键" :disabled="distributionConfigForm.configId != null" />
        </el-form-item>
        <el-form-item label="配置描述" prop="configDesc">
          <el-input v-model="distributionConfigForm.configDesc" placeholder="请输入配置描述" />
        </el-form-item>
        <el-form-item label="配置值" prop="configValue">
          <el-input v-model="distributionConfigForm.configValue" placeholder="请输入配置值" />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitDistributionConfigForm">确 定</el-button>
        <el-button @click="cancelDistributionConfig">取 消</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { listUser, getUser, delUser, addUser, updateUser, resetUserPwd, changeUserStatus, deptTreeSelect, updateUserBalance, getUserAccountFlow, bindReferrer, updateMemberLevel, upgradeTeamLevel, setUserAgent, cancelUserAgent, getProductList, getOrderPreview, placeOrder } from "@/api/system/user"
import { getProduct, addProduct, updateProduct, delProduct } from "@/api/system/product"
import { listDistributionConfig, getDistributionConfig, addDistributionConfig, updateDistributionConfig, delDistributionConfig } from "@/api/system/distributionConfig"
import { getToken } from "@/utils/auth"
import { parseTime } from "@/utils/ruoyi"
import { getProvinceList, getCityListByProvince } from "@/utils/province-city"
import Treeselect from "@riophae/vue-treeselect"
import "@riophae/vue-treeselect/dist/vue-treeselect.css"
import { Splitpanes, Pane } from "splitpanes"
import "splitpanes/dist/splitpanes.css"

export default {
  name: "User",
  dicts: ['sys_normal_disable', 'sys_user_sex', 'member_level', 'team_level', 'agent_level'],
  components: { Treeselect, Splitpanes, Pane },
  computed: {
    // 根据当前等级过滤可选的会员等级
    availableMemberLevels() {
      if (!this.dict.type.member_level) return [];

      const currentLevel = this.memberLevelForm.currentLevel || 0;
      return this.dict.type.member_level.filter(level => {
        return parseInt(level.value) >= currentLevel;
      });
    }
  },
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
      userList: null,
      // 弹出层标题
      title: "",
      // 所有部门树选项
      deptOptions: undefined,
      // 过滤掉已禁用部门树选项
      enabledDeptOptions: undefined,
      // 是否显示弹出层
      open: false,
      // 部门名称
      deptName: undefined,
      // 默认密码
      initPassword: undefined,
      // 日期范围
      dateRange: [],
      // 岗位选项
      postOptions: [],
      // 角色选项
      roleOptions: [],
      // 表单参数
      form: {},
      defaultProps: {
        children: "children",
        label: "label"
      },
      // 分销相关数据
      accountOpen: false,
      accountInfo: {},
      flowList: [],
      balanceForm: {
        userId: undefined,
        type: 'add',
        amount: undefined,
        remark: undefined
      },
      balanceRules: {
        type: [{ required: true, message: "操作类型不能为空", trigger: "change" }],
        amount: [{ required: true, message: "金额不能为空", trigger: "blur" }],
        remark: [{ required: true, message: "备注不能为空", trigger: "blur" }]
      },
      orderOpen: false,
      orderForm: {
        userId: undefined,
        productId: undefined,
        productPrice: undefined,
        actualPrice: undefined,
        discountAmount: undefined,
        discountRate: undefined,
        isRepurchase: false,
        province: undefined,
        city: undefined,
        address: undefined
      },
      orderRules: {
        productId: [{ required: true, message: "商品不能为空", trigger: "change" }],
        province: [{ required: true, message: "收货省份不能为空", trigger: "change" }],
        city: [{ required: true, message: "收货城市不能为空", trigger: "change" }],
        address: [{ required: true, message: "详细地址不能为空", trigger: "blur" }]
      },
      productList: [],
      orderCityList: [],
      // 绑定推荐人相关
      referrerOpen: false,
      referrerForm: {
        userId: undefined,
        referrerPhone: undefined
      },
      referrerRules: {
        referrerPhone: [
          { required: true, message: "推荐人手机号不能为空", trigger: "blur" },
          { pattern: /^1[3-9]\d{9}$/, message: "请输入正确的手机号", trigger: "blur" }
        ]
      },
      // 设置会员等级相关
      memberLevelOpen: false,
      memberLevelForm: {
        userId: undefined,
        memberLevel: undefined,
        currentLevel: undefined
      },
      memberLevelRules: {
        memberLevel: [
          { required: true, message: "会员等级不能为空", trigger: "change" },
          { validator: this.validateMemberLevel, trigger: "change" }
        ]
      },
      // 升级团队级别相关
      teamLevelOpen: false,
      teamLevelForm: {
        userId: undefined,
        teamLevel: undefined,
        currentLevel: undefined
      },
      teamLevelRules: {
        teamLevel: [
          { required: true, message: "团队级别不能为空", trigger: "change" }
        ]
      },
      // 设置代理相关
      agentOpen: false,
      agentForm: {
        userId: undefined,
        userName: '',
        nickName: '',
        agentType: 1,
        province: '',
        city: ''
      },
      agentRules: {
        agentType: [
          { required: true, message: "请选择代理类型", trigger: "change" }
        ],
        province: [
          {
            validator: (rule, value, callback) => {
              if (this.agentForm.agentType > 0 && !value) {
                callback(new Error('请选择省份'));
              } else {
                callback();
              }
            },
            trigger: "change"
          }
        ],
        city: [
          {
            validator: (rule, value, callback) => {
              if (this.agentForm.agentType === 2 && !value) {
                callback(new Error('请选择城市'));
              } else {
                callback();
              }
            },
            trigger: "change"
          }
        ]
      },
      // 省市数据
      provinceList: [],
      cityList: [],
      // 用户导入参数
      upload: {
        // 是否显示弹出层（用户导入）
        open: false,
        // 弹出层标题（用户导入）
        title: "",
        // 是否禁用上传
        isUploading: false,
        // 是否更新已经存在的用户数据
        updateSupport: 0,
        // 设置上传的请求头部
        headers: { Authorization: "Bearer " + getToken() },
        // 上传的地址
        url: process.env.VUE_APP_BASE_API + "/system/user/importData"
      },
      // 商品管理弹窗参数
      productManage: {
        // 是否显示弹出层
        open: false,
        // 弹出层标题
        title: "商品管理"
      },
      // 商品管理相关数据
      productList: [],
      productTotal: 0,
      productLoading: true,
      productShowSearch: true,
      productIds: [],
      productSingle: true,
      productMultiple: true,
      productQueryParams: {
        pageNum: 1,
        pageSize: 10,
        productName: undefined,
        status: undefined
      },
      // 商品表单参数
      productForm: {},
      productOpen: false,
      productTitle: "",
      productRules: {
        productName: [
          { required: true, message: "商品名称不能为空", trigger: "blur" }
        ],
        price: [
          { required: true, message: "商品价格不能为空", trigger: "blur" }
        ],
        status: [
          { required: true, message: "状态不能为空", trigger: "change" }
        ]
      },
      // 分销配置管理弹窗参数
      distributionConfigManage: {
        // 是否显示弹出层
        open: false,
        // 弹出层标题
        title: "分销配置管理"
      },
      // 分销配置管理相关数据
      distributionConfigList: [],
      distributionConfigTotal: 0,
      distributionConfigLoading: true,
      distributionConfigIds: [],
      distributionConfigSingle: true,
      distributionConfigMultiple: true,
      distributionConfigQueryParams: {
        pageNum: 1,
        pageSize: 10,
        configKey: undefined,
        configDesc: undefined
      },
      // 分销配置表单参数
      distributionConfigForm: {},
      distributionConfigOpen: false,
      distributionConfigTitle: "",
      distributionConfigRules: {
        configKey: [
          { required: true, message: "配置键不能为空", trigger: "blur" }
        ],
        configDesc: [
          { required: true, message: "配置描述不能为空", trigger: "blur" }
        ],
        configValue: [
          { required: true, message: "配置值不能为空", trigger: "blur" }
        ]
      },
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        userName: undefined,
        phonenumber: undefined,
        status: undefined,
        deptId: undefined,
        memberLevel: undefined,
        teamLevel: undefined
      },
      // 列信息
      columns: [
        { key: 0, label: `用户编号`, visible: true },
        { key: 1, label: `用户名称`, visible: true },
        { key: 2, label: `用户昵称`, visible: true },
        { key: 3, label: `部门`, visible: true },
        { key: 4, label: `手机号码`, visible: true },
        { key: 5, label: `状态`, visible: true },
        { key: 6, label: `推荐人`, visible: true },
        { key: 7, label: `会员等级`, visible: true },
        { key: 8, label: `团队级别`, visible: true },
        { key: 9, label: `代理级别`, visible: true },
        { key: 10, label: `账户余额`, visible: true },
        { key: 11, label: `团队业绩`, visible: true },
        { key: 12, label: `直推金牌`, visible: true },
        { key: 13, label: `创建时间`, visible: true }
      ],
      // 表单校验
      rules: {
        userName: [
          { required: true, message: "用户名称不能为空", trigger: "blur" },
          { min: 2, max: 20, message: '用户名称长度必须介于 2 和 20 之间', trigger: 'blur' }
        ],
        nickName: [
          { required: true, message: "用户昵称不能为空", trigger: "blur" }
        ],
        password: [
          { required: true, message: "用户密码不能为空", trigger: "blur" },
          { min: 5, max: 20, message: '用户密码长度必须介于 5 和 20 之间', trigger: 'blur' },
          { pattern: /^[^<>"'|\\]+$/, message: "不能包含非法字符：< > \" ' \\\ |", trigger: "blur" }
        ],
        email: [
          {
            type: "email",
            message: "请输入正确的邮箱地址",
            trigger: ["blur", "change"]
          }
        ],
        phonenumber: [
          {
            pattern: /^1[3|4|5|6|7|8|9][0-9]\d{8}$/,
            message: "请输入正确的手机号码",
            trigger: "blur"
          }
        ]
      }
    }
  },
  watch: {
    // 根据名称筛选部门树
    deptName(val) {
      this.$refs.tree.filter(val)
    }
  },
  created() {
    this.getList()
    this.getDeptTree()
    this.getConfigKey("sys.user.initPassword").then(response => {
      this.initPassword = response.msg
    })
    // 初始化省份数据
    this.provinceList = getProvinceList()
    console.log('省份列表:', this.provinceList)
    // 测试城市数据获取
    const testCities = getCityListByProvince('北京市')
    console.log('北京市的城市列表:', testCities)
  },
  methods: {
    /** 查询用户列表 */
    getList() {
      this.loading = true
      listUser(this.addDateRange(this.queryParams, this.dateRange)).then(response => {
          this.userList = response.rows
          this.total = response.total
          this.loading = false
        }
      )
    },
    /** 查询部门下拉树结构 */
    getDeptTree() {
      deptTreeSelect().then(response => {
        this.deptOptions = response.data
        this.enabledDeptOptions = this.filterDisabledDept(JSON.parse(JSON.stringify(response.data)))
      })
    },
    // 过滤禁用的部门
    filterDisabledDept(deptList) {
      return deptList.filter(dept => {
        if (dept.disabled) {
          return false
        }
        if (dept.children && dept.children.length) {
          dept.children = this.filterDisabledDept(dept.children)
        }
        return true
      })
    },
    // 筛选节点
    filterNode(value, data) {
      if (!value) return true
      return data.label.indexOf(value) !== -1
    },
    // 节点单击事件
    handleNodeClick(data) {
      this.queryParams.deptId = data.id
      this.handleQuery()
    },
    // 用户状态修改
    handleStatusChange(row) {
      let text = row.status === "0" ? "启用" : "停用"
      this.$modal.confirm('确认要"' + text + '""' + row.userName + '"用户吗？').then(function() {
        return changeUserStatus(row.userId, row.status)
      }).then(() => {
        this.$modal.msgSuccess(text + "成功")
      }).catch(function() {
        row.status = row.status === "0" ? "1" : "0"
      })
    },
    // 取消按钮
    cancel() {
      this.open = false
      this.reset()
    },
    // 表单重置
    reset() {
      this.form = {
        userId: undefined,
        deptId: undefined,
        userName: undefined,
        nickName: undefined,
        password: undefined,
        phonenumber: undefined,
        email: undefined,
        sex: undefined,
        status: "0",
        remark: undefined,
        postIds: [],
        roleIds: []
      }
      this.resetForm("form")
    },
    /** 搜索按钮操作 */
    handleQuery() {
      this.queryParams.pageNum = 1
      this.getList()
    },
    /** 重置按钮操作 */
    resetQuery() {
      this.dateRange = []
      this.resetForm("queryForm")
      this.queryParams.deptId = undefined
      this.$refs.tree.setCurrentKey(null)
      this.handleQuery()
    },
    // 多选框选中数据
    handleSelectionChange(selection) {
      this.ids = selection.map(item => item.userId)
      this.single = selection.length != 1
      this.multiple = !selection.length
    },
    // 更多操作触发
    handleCommand(command, row) {
      switch (command) {
        case "handleResetPwd":
          this.handleResetPwd(row)
          break
        case "handleAuthRole":
          this.handleAuthRole(row)
          break
        default:
          break
      }
    },
    /** 新增按钮操作 */
    handleAdd() {
      this.reset()
      getUser().then(response => {
        this.postOptions = response.posts
        this.roleOptions = response.roles
        this.open = true
        this.title = "添加用户"
        this.form.password = this.initPassword
      })
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset()
      const userId = row.userId || this.ids
      getUser(userId).then(response => {
        this.form = response.data
        this.postOptions = response.posts
        this.roleOptions = response.roles
        this.$set(this.form, "postIds", response.postIds)
        this.$set(this.form, "roleIds", response.roleIds)
        this.open = true
        this.title = "修改用户"
        this.form.password = ""
      })
    },
    /** 重置密码按钮操作 */
    handleResetPwd(row) {
      this.$prompt('请输入"' + row.userName + '"的新密码', "提示", {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
        closeOnClickModal: false,
        inputPattern: /^.{5,20}$/,
        inputErrorMessage: "用户密码长度必须介于 5 和 20 之间",
        inputValidator: (value) => {
          if (/<|>|"|'|\||\\/.test(value)) {
            return "不能包含非法字符：< > \" ' \\\ |"
          }
        },
      }).then(({ value }) => {
          resetUserPwd(row.userId, value).then(response => {
            this.$modal.msgSuccess("修改成功，新密码是：" + value)
          })
        }).catch(() => {})
    },
    /** 分配角色操作 */
    handleAuthRole: function(row) {
      const userId = row.userId
      this.$router.push("/system/user-auth/role/" + userId)
    },
    /** 提交按钮 */
    submitForm: function() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          if (this.form.userId != undefined) {
            updateUser(this.form).then(response => {
              this.$modal.msgSuccess("修改成功")
              this.open = false
              this.getList()
            })
          } else {
            addUser(this.form).then(response => {
              this.$modal.msgSuccess("新增成功")
              this.open = false
              this.getList()
            })
          }
        }
      })
    },
    /** 删除按钮操作 */
    handleDelete(row) {
      const userIds = row.userId || this.ids
      this.$modal.confirm('是否确认删除用户编号为"' + userIds + '"的数据项？').then(function() {
        return delUser(userIds)
      }).then(() => {
        this.getList()
        this.$modal.msgSuccess("删除成功")
      }).catch(() => {})
    },
    /** 导出按钮操作 */
    handleExport() {
      this.download('system/user/export', {
        ...this.queryParams
      }, `user_${new Date().getTime()}.xlsx`)
    },
    /** 导入按钮操作 */
    handleImport() {
      this.upload.title = "用户导入"
      this.upload.open = true
    },
    /** 商品管理按钮操作 */
    handleProductManage() {
      this.productManage.open = true
      this.getProductManageList()
    },
    /** 查询商品管理列表 */
    getProductManageList() {
      this.productLoading = true
      getProductList().then(response => {
        let allProducts = response.data || []

        // 前端搜索过滤
        if (this.productQueryParams.productName) {
          allProducts = allProducts.filter(product =>
            product.productName.includes(this.productQueryParams.productName)
          )
        }
        if (this.productQueryParams.status) {
          allProducts = allProducts.filter(product =>
            product.status === this.productQueryParams.status
          )
        }

        // 前端分页
        this.productTotal = allProducts.length
        const pageNum = this.productQueryParams.pageNum || 1
        const pageSize = this.productQueryParams.pageSize || 10
        const startIndex = (pageNum - 1) * pageSize
        const endIndex = startIndex + pageSize

        this.productList = allProducts.slice(startIndex, endIndex)
        this.productLoading = false
      }).catch(error => {
        console.error('获取商品列表失败:', error)
        this.$modal.msgError("获取商品列表失败")
        this.productLoading = false
      })
    },
    /** 商品搜索按钮操作 */
    handleProductQuery() {
      this.productQueryParams.pageNum = 1
      this.getProductManageList()
    },
    /** 重置商品搜索 */
    resetProductQuery() {
      this.resetForm("productQueryForm")
      this.handleProductQuery()
    },
    /** 商品多选框选中数据 */
    handleProductSelectionChange(selection) {
      this.productIds = selection.map(item => item.productId)
      this.productSingle = selection.length !== 1
      this.productMultiple = !selection.length
    },
    /** 新增商品按钮操作 */
    handleProductAdd() {
      this.resetProductForm()
      this.productOpen = true
      this.productTitle = "添加商品"
    },
    /** 取消商品操作 */
    cancelProduct() {
      this.productOpen = false
      this.resetProductForm()
    },
    /** 表单重置 */
    resetProductForm() {
      this.productForm = {
        productId: null,
        productName: null,
        price: null,
        status: "0"
      }
      this.resetForm("productForm")
    },
    /** 提交商品表单 */
    submitProductForm() {
      this.$refs["productForm"].validate(valid => {
        if (valid) {
          if (this.productForm.productId != null) {
            updateProduct(this.productForm).then(response => {
              this.$modal.msgSuccess("修改成功")
              this.productOpen = false
              this.getProductManageList()
            })
          } else {
            addProduct(this.productForm).then(response => {
              this.$modal.msgSuccess("新增成功")
              this.productOpen = false
              this.getProductManageList()
            })
          }
        }
      })
    },
    /** 修改商品按钮操作 */
    handleProductUpdate(row) {
      this.resetProductForm()
      const productId = row.productId || this.productIds
      getProduct(productId).then(response => {
        this.productForm = response.data
        this.productOpen = true
        this.productTitle = "修改商品"
      })
    },
    /** 删除商品按钮操作 */
    handleProductDelete(row) {
      const productIds = row.productId || this.productIds
      this.$modal.confirm('是否确认删除商品编号为"' + productIds + '"的数据项？').then(function() {
        return delProduct(productIds)
      }).then(() => {
        this.getProductManageList()
        this.$modal.msgSuccess("删除成功")
      }).catch(() => {})
    },
    /** 下载模板操作 */
    importTemplate() {
      this.download('system/user/importTemplate', {
      }, `user_template_${new Date().getTime()}.xlsx`)
    },
    // 文件上传中处理
    handleFileUploadProgress(event, file, fileList) {
      this.upload.isUploading = true
    },
    // 文件上传成功处理
    handleFileSuccess(response, file, fileList) {
      this.upload.open = false
      this.upload.isUploading = false
      this.$refs.upload.clearFiles()
      this.$alert("<div style='overflow: auto;overflow-x: hidden;max-height: 70vh;padding: 10px 20px 0;'>" + response.msg + "</div>", "导入结果", { dangerouslyUseHTMLString: true })
      this.getList()
    },
    // 提交上传文件
    submitFileForm() {
      this.$refs.upload.submit()
    },

    // ========== 分销相关方法 ==========

    /** 账户管理 */
    handleAccount(row) {
      this.accountOpen = true;
      this.balanceForm.userId = row.userId;
      this.accountInfo = row;
      // 获取流水记录
      this.getFlowList(row.userId);
    },

    /** 获取流水列表 */
    getFlowList(userId) {
      getUserAccountFlow(userId).then(response => {
        this.flowList = response.rows || [];
        console.log('获取到的流水数据:', this.flowList);
      }).catch(error => {
        console.error('获取流水列表失败:', error);
        this.flowList = [];
      });
    },

    /** 提交余额操作 */
    submitBalance() {
      this.$refs["balanceForm"].validate(valid => {
        if (valid) {
          console.log('提交余额操作:', this.balanceForm);
          updateUserBalance(this.balanceForm).then(response => {
            console.log('余额操作响应:', response);
            this.$modal.msgSuccess("余额操作成功");

            // 更新当前显示的账户信息
            const currentBalance = parseFloat(this.accountInfo.balance || 0);
            const operationAmount = parseFloat(this.balanceForm.amount);
            this.accountInfo.balance = this.balanceForm.type === 'add'
              ? (currentBalance + operationAmount)
              : (currentBalance - operationAmount);

            this.resetBalance();
            this.getList(); // 刷新用户列表
            this.getFlowList(this.balanceForm.userId); // 刷新流水列表
          }).catch(error => {
            console.error('余额操作失败:', error);
            console.error('错误详情:', error.response);
            this.$modal.msgError("余额操作失败: " + (error.response?.data?.msg || error.message));
          });
        }
      });
    },

    /** 重置余额表单 */
    resetBalance() {
      this.balanceForm = {
        userId: this.balanceForm.userId,
        type: 'add',
        amount: undefined,
        remark: undefined
      };
      this.resetForm("balanceForm");
    },

    /** 模拟下单 */
    handleOrder(row) {
      this.resetOrderForm();
      this.orderOpen = true;
      this.orderForm.userId = row.userId;
      // 获取商品列表
      this.getProductList();
    },

    /** 重置订单表单 */
    resetOrderForm() {
      this.orderForm = {
        userId: undefined,
        productId: undefined,
        productPrice: undefined,
        actualPrice: undefined,
        discountAmount: undefined,
        discountRate: undefined,
        isRepurchase: false,
        province: undefined,
        city: undefined,
        address: undefined
      };
      this.orderCityList = [];
      this.resetForm("orderForm");
    },

    /** 获取商品列表 */
    getProductList() {
      getProductList().then(response => {
        this.productList = response.data;
      }).catch(error => {
        console.error('获取商品列表失败:', error);
        this.$modal.msgError("获取商品列表失败");
      });
    },

    /** 商品选择变化处理 */
    handleProductChange(productId) {
      if (productId) {
        const selectedProduct = this.productList.find(product => product.productId === productId);
        if (selectedProduct) {
          this.orderForm.productPrice = selectedProduct.price;

          // 调用后端接口获取订单价格预览
          const previewData = {
            userId: this.orderForm.userId,
            productId: productId
          };

          getOrderPreview(previewData).then(response => {
            const preview = response.data;
            this.orderForm.actualPrice = preview.actualPrice;
            this.orderForm.discountAmount = preview.discountAmount;
            this.orderForm.discountRate = preview.discountRate;
            this.orderForm.isRepurchase = preview.isRepurchase;

            // 如果是复购，显示优惠信息
            if (preview.isRepurchase && preview.discountAmount > 0) {
              this.$message.success(`检测到复购商品，享受${100 - preview.discountRate}%的折扣优惠，优惠金额：${preview.discountAmount}元`);
            }
          }).catch(error => {
            console.error('获取订单预览失败:', error);

            // 检查是否是认证错误
            if (error.response && error.response.status === 401) {
              this.$message.error('登录状态已过期，请重新登录后再试');
            } else if (error.message && error.message.includes('404')) {
              this.$message.error('订单预览接口不存在，请联系管理员');
            } else {
              this.$message.error('获取订单预览失败，将使用商品原价');
            }

            // 如果预览失败，使用原价
            this.orderForm.actualPrice = selectedProduct.price;
            this.orderForm.discountAmount = 0;
            this.orderForm.discountRate = 100;
            this.orderForm.isRepurchase = false;
          });
        }
      } else {
        this.resetOrderPriceInfo();
      }
    },

    /** 重置订单价格信息 */
    resetOrderPriceInfo() {
      this.orderForm.productPrice = undefined;
      this.orderForm.actualPrice = undefined;
      this.orderForm.discountAmount = undefined;
      this.orderForm.discountRate = undefined;
      this.orderForm.isRepurchase = false;
    },

    /** 订单省份选择变化处理 */
    handleOrderProvinceChange(province) {
      console.log('订单省份变化:', province);
      this.orderForm.city = undefined;
      if (province) {
        this.orderCityList = getCityListByProvince(province);
        console.log('获取到的城市列表:', this.orderCityList);
      } else {
        this.orderCityList = [];
      }
    },

    /** 提交订单 */
    submitOrder() {
      this.$refs["orderForm"].validate(valid => {
        if (valid) {
          const orderData = {
            userId: this.orderForm.userId,
            productId: this.orderForm.productId,
            province: this.orderForm.province,
            city: this.orderForm.city,
            address: this.orderForm.address
          };

          placeOrder(orderData).then(response => {
            this.$modal.msgSuccess("下单成功");
            this.orderOpen = false;
            this.getList();
          }).catch(error => {
            console.error('下单失败:', error);
            this.$modal.msgError("下单失败: " + (error.response?.data?.msg || error.message));
          });
        }
      });
    },

    /** 处理更多操作命令 */
    handleCommand(command, row) {
      switch (command) {
        case "handleUpdate":
          this.handleUpdate(row);
          break;
        case "handleDelete":
          this.handleDelete(row);
          break;
        case "handleResetPwd":
          this.handleResetPwd(row);
          break;
        case "handleAuthRole":
          this.handleAuthRole(row);
          break;
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
      // 如果用户已经有推荐人，则不允许操作
      if (row.referrerName) {
        this.$modal.msgWarning("该用户已绑定推荐人，不可重复绑定");
        return;
      }

      this.referrerOpen = true;
      this.referrerForm.userId = row.userId;
      this.referrerForm.referrerPhone = '';
    },

    /** 提交绑定推荐人 */
    submitReferrer() {
      this.$refs["referrerForm"].validate(valid => {
        if (valid) {
          bindReferrer(this.referrerForm).then(response => {
            this.$modal.msgSuccess("绑定推荐人成功");
            this.referrerOpen = false;
            this.getList();
          }).catch(error => {
            this.$modal.msgError("绑定失败，请检查推荐人手机号是否正确");
          });
        }
      });
    },

    /** 设置会员等级 */
    handleMember(row) {
      this.memberLevelOpen = true;
      this.memberLevelForm.userId = row.userId;
      this.memberLevelForm.currentLevel = row.memberLevel;
      this.memberLevelForm.memberLevel = undefined; // 去掉默认值，让用户主动选择
    },

    /** 提交设置会员等级 */
    submitMemberLevel() {
      this.$refs["memberLevelForm"].validate(valid => {
        if (valid) {
          updateMemberLevel(this.memberLevelForm).then(response => {
            this.$modal.msgSuccess("设置会员等级成功");
            this.memberLevelOpen = false;
            this.getList();
          }).catch(error => {
            this.$modal.msgError("设置失败，请重试");
          });
        }
      });
    },

    /** 验证会员等级不能降级 */
    validateMemberLevel(rule, value, callback) {
      if (value === undefined || value === null || value === '') {
        callback();
        return;
      }

      const currentLevel = this.memberLevelForm.currentLevel || 0;
      const newLevel = parseInt(value);

      if (newLevel < currentLevel) {
        const levelNames = { 0: '普通用户', 1: '普通会员', 2: '金牌会员' };
        callback(new Error(`不能将${levelNames[currentLevel]}降级为${levelNames[newLevel]}`));
      } else {
        callback();
      }
    },

    /** 升级团队级别 */
    handleLevel(row) {
      this.teamLevelOpen = true;
      this.teamLevelForm.userId = row.userId;
      this.teamLevelForm.currentLevel = row.teamLevel || 0;
      this.teamLevelForm.teamLevel = undefined;
    },

    /** 提交升级团队级别 */
    submitTeamLevel() {
      this.$refs["teamLevelForm"].validate(valid => {
        if (valid) {
          const requestData = {
            userId: this.teamLevelForm.userId,
            targetLevel: parseInt(this.teamLevelForm.teamLevel)
          };
          upgradeTeamLevel(requestData).then(response => {
            this.$modal.msgSuccess("升级团队级别成功");
            this.teamLevelOpen = false;
            this.getList();
          }).catch(error => {
            this.$modal.msgError(error.msg || "升级失败，不满足升级条件");
          });
        }
      });
    },

    /** 设置代理 */
    handleAgent(row) {
      this.agentOpen = true;
      this.agentForm.userId = row.userId;
      this.agentForm.userName = row.userName;
      this.agentForm.nickName = row.nickName;

      // 设置当前代理信息
      this.agentForm.agentType = row.agentLevel || 0;
      this.agentForm.province = row.agentProvince || '';
      this.agentForm.city = row.agentCity || '';

      // 如果有省份信息，加载对应的城市列表
      if (this.agentForm.province) {
        this.cityList = getCityListByProvince(this.agentForm.province);
      } else {
        this.cityList = [];
      }
    },

    /** 代理类型改变 */
    handleAgentTypeChange(value) {
      this.agentForm.city = '';
      this.agentForm.province = '';
      this.cityList = [];
      // 清空相关验证规则
      if (this.$refs.agentForm) {
        this.$refs.agentForm.clearValidate(['province', 'city']);
      }
    },

    /** 代理省份改变 */
    handleAgentProvinceChange(value) {
      this.agentForm.city = '';
      this.cityList = getCityListByProvince(value);
    },

    /** 提交设置代理 */
    submitAgent() {
      this.$refs["agentForm"].validate(valid => {
        if (valid) {
          // 如果选择取消代理
          if (this.agentForm.agentType === 0) {
            this.cancelAgent(this.agentForm.userId);
            return;
          }

          // 构建请求数据
          const requestData = {
            userId: this.agentForm.userId,
            agentType: this.agentForm.agentType,
            province: this.agentForm.province,
            city: this.agentForm.agentType === 2 ? this.agentForm.city : null
          };

          // 调用API设置代理
          setUserAgent(requestData).then(response => {
            this.$modal.msgSuccess("设置代理成功");
            this.agentOpen = false;
            this.getList();
          }).catch(error => {
            this.$modal.msgError(error.msg || "设置代理失败，请重试");
          });
        }
      });
    },

    /** 取消代理 */
    cancelAgent(userId) {
      this.$modal.confirm('确认取消该用户的代理设置吗？').then(() => {
        return cancelUserAgent(userId);
      }).then(() => {
        this.$modal.msgSuccess("取消代理成功");
        this.agentOpen = false;
        this.getList();
      }).catch(error => {
        if (error !== 'cancel') {
          this.$modal.msgError(error.msg || "取消代理失败，请重试");
        }
      });
    },

    /** 获取会员等级文本 */
    getMemberLevelText(level) {
      const levelMap = { 0: '普通用户', 1: '普通会员', 2: '金牌会员' };
      return levelMap[level] || '未知';
    },

    /** 获取团队级别文本 */
    getTeamLevelText(level) {
      const levelMap = { 0: '无', 1: '经理', 2: '总监', 3: '合伙人' };
      return levelMap[level] || '未知';
    },

    /** 获取代理区域文本 */
    getAgentAreaText(agentLevel, province, city) {
      if (!agentLevel || agentLevel === 0) {
        return '无';
      }

      if (agentLevel === 1) {
        // 省级代理只显示省份
        return province || '未设置';
      } else if (agentLevel === 2) {
        // 市级代理显示省份+城市
        if (province && city) {
          return `${province} ${city}`;
        } else if (province) {
          return province;
        } else {
          return '未设置';
        }
      }

      return '未知';
    },

    /** 分销配置管理按钮操作 */
    handleDistributionConfig() {
      this.distributionConfigManage.open = true
      this.getDistributionConfigList()
    },

    /** 查询分销配置列表 */
    getDistributionConfigList() {
      this.distributionConfigLoading = true
      listDistributionConfig(this.distributionConfigQueryParams).then(response => {
        this.distributionConfigList = response.rows
        this.distributionConfigTotal = response.total
        this.distributionConfigLoading = false
      }).catch(error => {
        console.error('获取分销配置列表失败:', error)
        this.$modal.msgError("获取分销配置列表失败")
        this.distributionConfigLoading = false
      })
    },

    /** 分销配置多选框选中数据 */
    handleDistributionConfigSelectionChange(selection) {
      this.distributionConfigIds = selection.map(item => item.configId)
      this.distributionConfigSingle = selection.length !== 1
      this.distributionConfigMultiple = !selection.length
    },

    /** 新增分销配置按钮操作 */
    handleDistributionConfigAdd() {
      this.resetDistributionConfigForm()
      this.distributionConfigOpen = true
      this.distributionConfigTitle = "添加分销配置"
    },

    /** 取消分销配置操作 */
    cancelDistributionConfig() {
      this.distributionConfigOpen = false
      this.resetDistributionConfigForm()
    },

    /** 分销配置表单重置 */
    resetDistributionConfigForm() {
      this.distributionConfigForm = {
        configId: null,
        configKey: null,
        configDesc: null,
        configValue: null
      }
      this.resetForm("distributionConfigForm")
    },

    /** 提交分销配置表单 */
    submitDistributionConfigForm() {
      this.$refs["distributionConfigForm"].validate(valid => {
        if (valid) {
          if (this.distributionConfigForm.configId != null) {
            updateDistributionConfig(this.distributionConfigForm).then(response => {
              this.$modal.msgSuccess("修改成功")
              this.distributionConfigOpen = false
              this.getDistributionConfigList()
            })
          } else {
            addDistributionConfig(this.distributionConfigForm).then(response => {
              this.$modal.msgSuccess("新增成功")
              this.distributionConfigOpen = false
              this.getDistributionConfigList()
            })
          }
        }
      })
    },

    /** 修改分销配置按钮操作 */
    handleDistributionConfigUpdate(row) {
      this.resetDistributionConfigForm()
      const configId = row.configId || this.distributionConfigIds
      getDistributionConfig(configId).then(response => {
        this.distributionConfigForm = response.data
        this.distributionConfigOpen = true
        this.distributionConfigTitle = "修改分销配置"
      })
    },

    /** 删除分销配置按钮操作 */
    handleDistributionConfigDelete(row) {
      const configIds = row.configId || this.distributionConfigIds
      this.$modal.confirm('是否确认删除分销配置编号为"' + configIds + '"的数据项？').then(function() {
        return delDistributionConfig(configIds)
      }).then(() => {
        this.getDistributionConfigList()
        this.$modal.msgSuccess("删除成功")
      }).catch(() => {})
    }
  }
}
</script>

<style scoped>
.account-info p {
  margin: 10px 0;
  font-size: 14px;
}

.box-card {
  margin-bottom: 20px;
}

.el-divider {
  margin: 20px 0;
}

/* 置灰按钮样式 */
.disabled-button {
  color: #c0c4cc !important;
  cursor: not-allowed !important;
}

.disabled-button:hover {
  color: #c0c4cc !important;
}
</style>