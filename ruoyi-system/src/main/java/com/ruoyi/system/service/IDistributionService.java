package com.ruoyi.system.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import com.ruoyi.common.core.domain.entity.SysUser;
import com.ruoyi.system.domain.Orders;

/**
 * 分销系统核心服务接口
 * 
 * @author ruoyi
 * @date 2025-01-28
 */
public interface IDistributionService
{
    /**
     * 获取订单价格预览
     *
     * @param userId 用户ID
     * @param productId 商品ID
     * @return 订单价格预览信息
     */
    public Map<String, Object> getOrderPreview(Long userId, Long productId);

    /**
     * 用户下单处理
     *
     * @param userId 用户ID
     * @param productId 商品ID
     * @param province 收货省份
     * @param city 收货城市
     * @param address 收货地址
     * @return 订单信息
     */
    public Orders processOrder(Long userId, Long productId, String province, String city, String address);

    /**
     * 计算并分发佣金
     * 
     * @param order 订单信息
     */
    public void distributeCommission(Orders order);

    /**
     * 更新用户余额
     * 
     * @param userId 用户ID
     * @param amount 金额（正数为增加，负数为减少）
     * @param businessType 业务类型
     * @param businessId 关联业务ID
     * @param remark 备注
     * @return 更新结果
     */
    public boolean updateUserBalance(Long userId, BigDecimal amount, String businessType, Long businessId, String remark);

    /**
     * 检查用户是否可以升级团队级别
     * 
     * @param userId 用户ID
     * @param targetLevel 目标级别（1经理 2总监 3合伙人）
     * @return 是否可以升级
     */
    public boolean checkLevelUpgrade(Long userId, Integer targetLevel);

    /**
     * 升级用户团队级别
     * 
     * @param userId 用户ID
     * @param targetLevel 目标级别
     * @return 升级结果
     */
    public boolean upgradeUserLevel(Long userId, Integer targetLevel);

    /**
     * 绑定推荐人
     * 
     * @param userId 用户ID
     * @param referrerPhone 推荐人手机号
     * @return 绑定结果
     */
    public boolean bindReferrer(Long userId, String referrerPhone);

    /**
     * 设置用户会员等级
     * 
     * @param userId 用户ID
     * @param memberLevel 会员等级
     * @return 设置结果
     */
    public boolean setMemberLevel(Long userId, Integer memberLevel);

    /**
     * 计算用户团队业绩
     * 
     * @param userId 用户ID
     * @return 团队总业绩
     */
    public BigDecimal calculateTeamPerformance(Long userId);

    /**
     * 统计用户直推金牌会员数量
     * 
     * @param userId 用户ID
     * @return 直推金牌会员数量
     */
    public int countDirectGoldMembers(Long userId);

    /**
     * 统计用户培养的经理数量（不同分支）
     * 
     * @param userId 用户ID
     * @return 经理数量
     */
    public int countDifferentBranchManagers(Long userId);

    /**
     * 统计用户培养的总监数量（不同分支）
     * 
     * @param userId 用户ID
     * @return 总监数量
     */
    public int countDifferentBranchDirectors(Long userId);

    /**
     * 获取配置值
     * 
     * @param configKey 配置键
     * @return 配置值
     */
    public String getConfigValue(String configKey);

    /**
     * 获取配置值（整数）
     * 
     * @param configKey 配置键
     * @return 配置值
     */
    public Integer getConfigIntValue(String configKey);

    /**
     * 获取配置值（BigDecimal）
     * 
     * @param configKey 配置键
     * @return 配置值
     */
    public BigDecimal getConfigDecimalValue(String configKey);
}
