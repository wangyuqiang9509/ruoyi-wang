-- 分销系统相关表结构

-- 1. 扩展用户表，添加分销相关字段
ALTER TABLE sys_user ADD COLUMN referrer_id BIGINT(20) DEFAULT NULL COMMENT '推荐人ID';
ALTER TABLE sys_user ADD COLUMN member_level TINYINT(1) DEFAULT 0 COMMENT '会员等级（0普通用户 1普通会员 2金牌会员）';
ALTER TABLE sys_user ADD COLUMN team_level TINYINT(1) DEFAULT 0 COMMENT '团队级别（0无 1经理 2总监 3合伙人）';
ALTER TABLE sys_user ADD COLUMN balance DECIMAL(10,2) DEFAULT 0.00 COMMENT '账户余额';
ALTER TABLE sys_user ADD COLUMN total_performance DECIMAL(12,2) DEFAULT 0.00 COMMENT '团队总业绩';
ALTER TABLE sys_user ADD COLUMN direct_gold_members INT(11) DEFAULT 0 COMMENT '直推金牌会员数量';

-- 2. 用户账户流水表
DROP TABLE IF EXISTS user_account_flow;
CREATE TABLE user_account_flow (
  flow_id BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '流水ID',
  user_id BIGINT(20) NOT NULL COMMENT '用户ID',
  flow_type TINYINT(1) NOT NULL COMMENT '流水类型（1收入 2支出）',
  amount DECIMAL(10,2) NOT NULL COMMENT '金额',
  balance_before DECIMAL(10,2) NOT NULL COMMENT '变动前余额',
  balance_after DECIMAL(10,2) NOT NULL COMMENT '变动后余额',
  business_type VARCHAR(50) NOT NULL COMMENT '业务类型（充值、提现、下单、分润等）',
  business_id BIGINT(20) DEFAULT NULL COMMENT '关联业务ID',
  remark VARCHAR(500) DEFAULT NULL COMMENT '备注',
  create_time DATETIME NOT NULL COMMENT '创建时间',
  PRIMARY KEY (flow_id),
  KEY idx_user_id (user_id),
  KEY idx_create_time (create_time)
) ENGINE=InnoDB COMMENT='用户账户流水表';

-- 3. 商品表
DROP TABLE IF EXISTS product;
CREATE TABLE product (
  product_id BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '商品ID',
  product_name VARCHAR(100) NOT NULL COMMENT '商品名称',
  price DECIMAL(10,2) NOT NULL COMMENT '商品价格',
  status CHAR(1) DEFAULT '0' COMMENT '状态（0正常 1停用）',
  create_by VARCHAR(64) DEFAULT '' COMMENT '创建者',
  create_time DATETIME COMMENT '创建时间',
  update_by VARCHAR(64) DEFAULT '' COMMENT '更新者',
  update_time DATETIME COMMENT '更新时间',
  PRIMARY KEY (product_id)
) ENGINE=InnoDB COMMENT='商品表';

-- 4. 用户购买记录表（用于复购判断）
DROP TABLE IF EXISTS user_purchase_record;
CREATE TABLE user_purchase_record (
  record_id BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '记录ID',
  user_id BIGINT(20) NOT NULL COMMENT '用户ID',
  product_id BIGINT(20) NOT NULL COMMENT '商品ID',
  purchase_count INT(11) DEFAULT 1 COMMENT '购买次数',
  first_purchase_time DATETIME COMMENT '首次购买时间',
  last_purchase_time DATETIME COMMENT '最后购买时间',
  PRIMARY KEY (record_id),
  UNIQUE KEY uk_user_product (user_id, product_id)
) ENGINE=InnoDB COMMENT='用户购买记录表';

-- 5. 订单表
DROP TABLE IF EXISTS orders;
CREATE TABLE orders (
  order_id BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '订单ID',
  order_no VARCHAR(50) NOT NULL COMMENT '订单号',
  user_id BIGINT(20) NOT NULL COMMENT '用户ID',
  product_id BIGINT(20) NOT NULL COMMENT '商品ID',
  product_name VARCHAR(100) NOT NULL COMMENT '商品名称',
  original_price DECIMAL(10,2) NOT NULL COMMENT '原价',
  actual_price DECIMAL(10,2) NOT NULL COMMENT '实付价格',
  discount_amount DECIMAL(10,2) DEFAULT 0.00 COMMENT '优惠金额',
  is_repurchase TINYINT(1) DEFAULT 0 COMMENT '是否复购（0否 1是）',
  province VARCHAR(50) DEFAULT NULL COMMENT '收货省份',
  city VARCHAR(50) DEFAULT NULL COMMENT '收货城市',
  address VARCHAR(200) DEFAULT NULL COMMENT '收货地址',
  order_status TINYINT(1) DEFAULT 1 COMMENT '订单状态（1已支付 2已取消）',
  create_time DATETIME NOT NULL COMMENT '创建时间',
  PRIMARY KEY (order_id),
  UNIQUE KEY uk_order_no (order_no),
  KEY idx_user_id (user_id),
  KEY idx_create_time (create_time)
) ENGINE=InnoDB COMMENT='订单表';

-- 6. 分润记录表
DROP TABLE IF EXISTS commission_record;
CREATE TABLE commission_record (
  record_id BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '记录ID',
  order_id BIGINT(20) NOT NULL COMMENT '订单ID',
  user_id BIGINT(20) NOT NULL COMMENT '获得分润的用户ID',
  commission_type VARCHAR(50) NOT NULL COMMENT '分润类型（直推奖、级差奖、省代理、市代理）',
  commission_rate DECIMAL(5,2) NOT NULL COMMENT '分润比例',
  commission_amount DECIMAL(10,2) NOT NULL COMMENT '分润金额',
  order_amount DECIMAL(10,2) NOT NULL COMMENT '订单金额',
  create_time DATETIME NOT NULL COMMENT '创建时间',
  PRIMARY KEY (record_id),
  KEY idx_order_id (order_id),
  KEY idx_user_id (user_id)
) ENGINE=InnoDB COMMENT='分润记录表';

-- 7. 系统配置表
DROP TABLE IF EXISTS distribution_config;
CREATE TABLE distribution_config (
  config_id BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '配置ID',
  config_key VARCHAR(100) NOT NULL COMMENT '配置键',
  config_value VARCHAR(500) NOT NULL COMMENT '配置值',
  config_desc VARCHAR(200) DEFAULT NULL COMMENT '配置描述',
  create_time DATETIME NOT NULL COMMENT '创建时间',
  update_time DATETIME COMMENT '更新时间',
  PRIMARY KEY (config_id),
  UNIQUE KEY uk_config_key (config_key)
) ENGINE=InnoDB COMMENT='分销配置表';

-- 8. 代理设置表
DROP TABLE IF EXISTS agent_setting;
CREATE TABLE agent_setting (
  setting_id BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '设置ID',
  user_id BIGINT(20) NOT NULL COMMENT '用户ID',
  agent_type TINYINT(1) NOT NULL COMMENT '代理类型（1省级 2市级）',
  province VARCHAR(50) NOT NULL COMMENT '省份',
  city VARCHAR(50) DEFAULT NULL COMMENT '城市（市级代理必填）',
  status CHAR(1) DEFAULT '0' COMMENT '状态（0正常 1停用）',
  create_time DATETIME NOT NULL COMMENT '创建时间',
  PRIMARY KEY (setting_id),
  KEY idx_user_id (user_id),
  KEY idx_province_city (province, city)
) ENGINE=InnoDB COMMENT='代理设置表';

-- 初始化配置数据
INSERT INTO distribution_config (config_key, config_value, config_desc, create_time) VALUES
('member.normal.direct_rate', '10', '普通会员直推奖比例(%)', NOW()),
('member.gold.direct_rate', '15', '金牌会员直推奖比例(%)', NOW()),
('member.repurchase_discount', '80', '会员复购折扣(%)', NOW()),
('level.manager.performance', '60000', '经理级别业绩要求', NOW()),
('level.manager.gold_members', '4', '经理级别直推金牌会员要求', NOW()),
('level.manager.commission_rate', '6', '经理级别团队极差奖比例(%)', NOW()),
('level.director.performance', '490000', '总监级别业绩要求', NOW()),
('level.director.gold_members', '5', '总监级别直推金牌会员要求', NOW()),
('level.director.managers', '2', '总监级别培养经理要求', NOW()),
('level.director.commission_rate', '9', '总监级别团队极差奖比例(%)', NOW()),
('level.partner.performance', '2000000', '合伙人级别业绩要求', NOW()),
('level.partner.gold_members', '6', '合伙人级别直推金牌会员要求', NOW()),
('level.partner.directors', '2', '合伙人级别培养总监要求', NOW()),
('level.partner.commission_rate', '12', '合伙人级别团队极差奖比例(%)', NOW()),
('agent.province.commission_rate', '8', '省级代理分润比例(%)', NOW()),
('agent.city.commission_rate', '5', '市级代理分润比例(%)', NOW());

-- 初始化商品数据
INSERT INTO product (product_name, price, create_by, create_time) VALUES
('测试商品A', 100.00, 'admin', NOW()),
('测试商品B', 200.00, 'admin', NOW()),
('测试商品C', 500.00, 'admin', NOW());
