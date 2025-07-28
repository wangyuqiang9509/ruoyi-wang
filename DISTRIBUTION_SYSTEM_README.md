# 分销系统实现文档

## 项目概述

基于若依框架实现的分销机制系统，包含完整的用户管理、账户管理、订单管理、分润计算等功能。

## 功能特性

### 1. 用户体系
- **普通用户**：无门槛注册，直推他人下单无佣金，第二次复购同一商品无优惠
- **普通会员**：直推奖10%，复购8折
- **金牌会员**：直推奖15%，复购8折

### 2. 团队级差晋升
- **经理**：直推4个金牌会员 + 6万元团队业绩，获得6%级差奖
- **总监**：直推5个金牌会员 + 2位不同线经理 + 49万元团队业绩，获得9%级差奖
- **合伙人**：直推6个金牌会员 + 2位不同线总监 + 200万元团队业绩，获得12%级差奖

### 3. 区域代理
- **省级代理**：该省所有订单8%分润（多人平分）
- **市级代理**：该市所有订单5%分润（多人平分）

### 4. 系统功能
- 用户管理（增删改查）
- 账户管理（余额查看、流水记录、余额操作）
- 推荐人绑定
- 会员等级设置
- 团队级别升级（需满足条件）
- 代理设置
- 模拟下单功能
- 分润自动计算和分发

## 技术架构

### 后端技术栈
- **框架**：Spring Boot + MyBatis
- **数据库**：MySQL
- **权限**：Spring Security
- **工具**：若依代码生成器

### 前端技术栈
- **框架**：Vue.js + Element UI
- **构建工具**：Webpack
- **HTTP客户端**：Axios

## 数据库设计

### 核心表结构

#### 1. 用户表扩展 (sys_user)
```sql
ALTER TABLE sys_user ADD COLUMN referrer_id BIGINT(20) DEFAULT NULL COMMENT '推荐人ID';
ALTER TABLE sys_user ADD COLUMN member_level TINYINT(1) DEFAULT 0 COMMENT '会员等级（0普通用户 1普通会员 2金牌会员）';
ALTER TABLE sys_user ADD COLUMN team_level TINYINT(1) DEFAULT 0 COMMENT '团队级别（0无 1经理 2总监 3合伙人）';
ALTER TABLE sys_user ADD COLUMN balance DECIMAL(10,2) DEFAULT 0.00 COMMENT '账户余额';
ALTER TABLE sys_user ADD COLUMN total_performance DECIMAL(12,2) DEFAULT 0.00 COMMENT '团队总业绩';
ALTER TABLE sys_user ADD COLUMN direct_gold_members INT(11) DEFAULT 0 COMMENT '直推金牌会员数量';
```

#### 2. 用户账户流水表 (user_account_flow)
- 记录所有余额变动
- 包含收入/支出类型、金额、余额变化、业务类型等

#### 3. 订单表 (orders)
- 记录订单信息
- 包含原价、实付价格、优惠金额、是否复购等

#### 4. 分润记录表 (commission_record)
- 记录所有分润明细
- 包含分润类型、比例、金额等

#### 5. 配置表 (distribution_config)
- 存储系统配置参数
- 分润比例、升级条件等可配置

#### 6. 代理设置表 (agent_setting)
- 省市代理配置
- 支持多人代理同一区域

## 核心业务逻辑

### 1. 下单流程
1. 检查用户信息和商品信息
2. 判断是否复购，计算优惠价格
3. 检查用户余额是否充足
4. 扣除用户余额，创建订单
5. 更新购买记录
6. 执行分润逻辑

### 2. 分润计算
1. **直推奖励**：根据推荐人会员等级给予相应比例佣金
2. **团队级差奖**：向上查找团队领导，按级差比例分润
3. **区域代理奖**：根据收货地址给相应区域代理分润
4. **业绩更新**：向上更新所有上级的团队业绩

### 3. 等级升级
- 自动检查升级条件
- 需要满足直推人数、团队业绩、培养下级等条件
- 支持手动升级（管理员操作）

## 项目结构

```
├── sql/                           # 数据库脚本
│   ├── ry_20250522.sql           # 原始若依数据库
│   └── distribution_system.sql    # 分销系统扩展表
├── ruoyi-system/                 # 系统模块
│   ├── domain/                   # 实体类
│   ├── mapper/                   # 数据访问层
│   ├── service/                  # 业务逻辑层
│   └── resources/mapper/         # MyBatis映射文件
├── ruoyi-admin/                  # 管理模块
│   └── controller/               # 控制器层
└── ruoyi-ui/                     # 前端界面
    └── views/system/user/        # 用户管理页面
```

## 部署说明

### 1. 数据库初始化
```sql
-- 1. 执行原始若依数据库脚本
source sql/ry_20250522.sql;

-- 2. 执行分销系统扩展脚本
source sql/distribution_system.sql;
```

### 2. 后端启动
```bash
# 进入项目根目录
cd ruoyi-wang

# 编译项目
mvn clean compile

# 启动项目
mvn spring-boot:run
```

### 3. 前端启动
```bash
# 进入前端目录
cd ruoyi-ui

# 安装依赖
npm install

# 启动开发服务器
npm run dev
```

## API接口

### 用户管理相关
- `GET /system/user/account/{userId}` - 获取用户账户信息
- `GET /system/user/account/flow/{userId}` - 获取用户流水记录
- `POST /system/user/balance` - 更新用户余额
- `POST /system/user/referrer` - 绑定推荐人
- `POST /system/user/member` - 设置会员等级
- `POST /system/user/level` - 升级团队级别
- `POST /system/user/agent` - 设置代理
- `POST /system/user/order` - 模拟下单

### 分销系统相关
- `POST /system/distribution/order` - 用户下单
- `POST /system/distribution/balance` - 余额操作
- `GET /system/distribution/flow/list` - 流水列表
- `GET /system/distribution/order/list` - 订单列表
- `GET /system/distribution/commission/list` - 分润记录列表

## 配置参数

系统支持通过数据库配置以下参数：

### 会员相关
- `member.normal.direct_rate`: 普通会员直推奖比例
- `member.gold.direct_rate`: 金牌会员直推奖比例
- `member.repurchase_discount`: 会员复购折扣

### 团队级别相关
- `level.manager.performance`: 经理级别业绩要求
- `level.manager.gold_members`: 经理级别直推金牌会员要求
- `level.manager.commission_rate`: 经理级别团队极差奖比例

### 代理相关
- `agent.province.commission_rate`: 省级代理分润比例
- `agent.city.commission_rate`: 市级代理分润比例

## 注意事项

1. **数据一致性**：所有涉及金额的操作都使用事务保证数据一致性
2. **权限控制**：基于若依框架的权限系统，确保操作安全
3. **性能优化**：大量数据查询时注意分页和索引优化
4. **扩展性**：配置参数存储在数据库中，便于后续调整
5. **测试环境**：建议在测试环境充分验证分润逻辑的正确性

## 开发团队

本项目基于若依开源框架开发，实现了完整的分销机制系统。

## 许可证

本项目遵循若依框架的开源许可证。
