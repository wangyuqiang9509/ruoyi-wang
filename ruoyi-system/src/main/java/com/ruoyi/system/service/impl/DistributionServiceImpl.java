package com.ruoyi.system.service.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ruoyi.common.core.domain.entity.SysUser;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.system.domain.AgentSetting;
import com.ruoyi.system.domain.CommissionRecord;
import com.ruoyi.system.domain.DistributionConfig;
import com.ruoyi.system.domain.Orders;
import com.ruoyi.system.domain.Product;
import com.ruoyi.system.domain.UserAccountFlow;
import com.ruoyi.system.domain.UserPurchaseRecord;
import com.ruoyi.system.mapper.AgentSettingMapper;
import com.ruoyi.system.mapper.CommissionRecordMapper;
import com.ruoyi.system.mapper.DistributionConfigMapper;
import com.ruoyi.system.mapper.OrdersMapper;
import com.ruoyi.system.mapper.ProductMapper;
import com.ruoyi.system.mapper.SysUserMapper;
import com.ruoyi.system.mapper.UserAccountFlowMapper;
import com.ruoyi.system.mapper.UserPurchaseRecordMapper;
import com.ruoyi.system.service.IDistributionService;

/**
 * 分销系统核心服务实现
 * 
 * @author ruoyi
 * @date 2025-01-28
 */
@Service
public class DistributionServiceImpl implements IDistributionService
{
    @Autowired
    private SysUserMapper userMapper;
    
    @Autowired
    private ProductMapper productMapper;
    
    @Autowired
    private OrdersMapper ordersMapper;
    
    @Autowired
    private UserAccountFlowMapper accountFlowMapper;
    
    @Autowired
    private UserPurchaseRecordMapper purchaseRecordMapper;
    
    @Autowired
    private CommissionRecordMapper commissionRecordMapper;
    
    @Autowired
    private DistributionConfigMapper configMapper;
    
    @Autowired
    private AgentSettingMapper agentSettingMapper;

    /**
     * 获取订单价格预览
     */
    @Override
    public Map<String, Object> getOrderPreview(Long userId, Long productId)
    {
        Map<String, Object> preview = new HashMap<>();

        // 1. 获取用户信息
        SysUser user = userMapper.selectUserById(userId);
        if (user == null) {
            throw new ServiceException("用户不存在");
        }

        // 2. 获取商品信息
        Product product = productMapper.selectProductByProductId(productId);
        if (product == null) {
            throw new ServiceException("商品不存在");
        }

        // 3. 检查用户购买记录，判断是否复购
        UserPurchaseRecord purchaseRecord = purchaseRecordMapper.selectUserPurchaseRecordByUserAndProduct(userId, productId);
        boolean isRepurchase = purchaseRecord != null;

        // 4. 计算价格
        BigDecimal originalPrice = product.getPrice();
        BigDecimal actualPrice = originalPrice;
        BigDecimal discountAmount = BigDecimal.ZERO;
        BigDecimal discountRate = new BigDecimal("100");

        // 如果是复购且用户是会员，享受8折优惠
        if (isRepurchase && user.getMemberLevel() != null && user.getMemberLevel() > 0) {
            discountRate = getConfigDecimalValue("member.repurchase_discount");
            actualPrice = originalPrice.multiply(discountRate.divide(new BigDecimal("100"))).setScale(2, RoundingMode.HALF_UP);
            discountAmount = originalPrice.subtract(actualPrice);
        }

        // 5. 构建预览信息
        preview.put("productId", productId);
        preview.put("productName", product.getProductName());
        preview.put("originalPrice", originalPrice);
        preview.put("actualPrice", actualPrice);
        preview.put("discountAmount", discountAmount);
        preview.put("discountRate", discountRate);
        preview.put("isRepurchase", isRepurchase);
        preview.put("memberLevel", user.getMemberLevel());
        preview.put("userBalance", user.getBalance());
        preview.put("balanceSufficient", user.getBalance() != null && user.getBalance().compareTo(actualPrice) >= 0);

        return preview;
    }

    /**
     * 用户下单处理
     */
    @Override
    @Transactional
    public Orders processOrder(Long userId, Long productId, String province, String city, String address)
    {
        // 1. 获取用户信息
        SysUser user = userMapper.selectUserById(userId);
        if (user == null) {
            throw new ServiceException("用户不存在");
        }
        
        // 2. 获取商品信息
        Product product = productMapper.selectProductByProductId(productId);
        if (product == null) {
            throw new ServiceException("商品不存在");
        }
        
        // 3. 检查用户购买记录，判断是否复购
        UserPurchaseRecord purchaseRecord = purchaseRecordMapper.selectUserPurchaseRecordByUserAndProduct(userId, productId);
        boolean isRepurchase = purchaseRecord != null;
        
        // 4. 计算价格
        BigDecimal originalPrice = product.getPrice();
        BigDecimal actualPrice = originalPrice;
        BigDecimal discountAmount = BigDecimal.ZERO;
        
        // 如果是复购且用户是会员，享受8折优惠
        if (isRepurchase && user.getMemberLevel() != null && user.getMemberLevel() > 0) {
            BigDecimal discountRate = getConfigDecimalValue("member.repurchase_discount").divide(new BigDecimal("100"));
            actualPrice = originalPrice.multiply(discountRate).setScale(2, RoundingMode.HALF_UP);
            discountAmount = originalPrice.subtract(actualPrice);
        }
        
        // 5. 检查用户余额
        if (user.getBalance() == null || user.getBalance().compareTo(actualPrice) < 0) {
            throw new ServiceException("用户余额不足");
        }
        
        // 6. 扣除用户余额
        updateUserBalance(userId, actualPrice.negate(), "下单", null, "购买商品：" + product.getProductName());
        
        // 7. 创建订单
        Orders order = new Orders();
        order.setOrderNo("ORDER" + System.currentTimeMillis() + UUID.randomUUID().toString().substring(0, 6));
        order.setUserId(userId);
        order.setProductId(productId);
        order.setProductName(product.getProductName());
        order.setOriginalPrice(originalPrice);
        order.setActualPrice(actualPrice);
        order.setDiscountAmount(discountAmount);
        order.setIsRepurchase(isRepurchase ? 1 : 0);
        order.setProvince(province);
        order.setCity(city);
        order.setAddress(address);
        order.setOrderStatus(1);
        order.setCreateTime(new Date());
        
        ordersMapper.insertOrders(order);
        
        // 8. 更新购买记录
        if (purchaseRecord == null) {
            purchaseRecord = new UserPurchaseRecord();
            purchaseRecord.setUserId(userId);
            purchaseRecord.setProductId(productId);
            purchaseRecord.setPurchaseCount(1);
            purchaseRecord.setFirstPurchaseTime(new Date());
            purchaseRecord.setLastPurchaseTime(new Date());
            purchaseRecordMapper.insertUserPurchaseRecord(purchaseRecord);
        } else {
            purchaseRecord.setPurchaseCount(purchaseRecord.getPurchaseCount() + 1);
            purchaseRecord.setLastPurchaseTime(new Date());
            purchaseRecordMapper.updateUserPurchaseRecord(purchaseRecord);
        }
        
        // 9. 分发佣金
        distributeCommission(order);
        
        return order;
    }

    /**
     * 计算并分发佣金
     */
    @Override
    @Transactional
    public void distributeCommission(Orders order)
    {
        SysUser buyer = userMapper.selectUserById(order.getUserId());
        if (buyer == null) return;
        
        // 1. 直推奖励
        if (buyer.getReferrerId() != null) {
            SysUser referrer = userMapper.selectUserById(buyer.getReferrerId());
            if (referrer != null && referrer.getMemberLevel() != null && referrer.getMemberLevel() > 0) {
                BigDecimal directRate = BigDecimal.ZERO;
                String commissionType = "";
                
                if (referrer.getMemberLevel() == 1) {
                    directRate = getConfigDecimalValue("member.normal.direct_rate");
                    commissionType = "普通会员直推奖";
                } else if (referrer.getMemberLevel() == 2) {
                    directRate = getConfigDecimalValue("member.gold.direct_rate");
                    commissionType = "金牌会员直推奖";
                }
                
                if (directRate.compareTo(BigDecimal.ZERO) > 0) {
                    BigDecimal commission = order.getActualPrice().multiply(directRate.divide(new BigDecimal("100"))).setScale(2, RoundingMode.HALF_UP);
                    
                    // 发放佣金
                    updateUserBalance(referrer.getUserId(), commission, "分润", order.getOrderId(), commissionType);
                    
                    // 记录分润
                    saveCommissionRecord(order.getOrderId(), referrer.getUserId(), commissionType, directRate, commission, order.getActualPrice());
                }
            }
        }
        
        // 2. 团队级差奖励
        distributeTeamCommission(order, buyer);
        
        // 3. 区域代理奖励
        distributeAgentCommission(order);
        
        // 4. 更新团队业绩
        updateTeamPerformance(buyer, order.getActualPrice());
    }

    /**
     * 分发团队级差奖励
     */
    private void distributeTeamCommission(Orders order, SysUser buyer)
    {
        SysUser current = buyer;
        
        // 向上查找团队领导
        while (current.getReferrerId() != null) {
            SysUser parent = userMapper.selectUserById(current.getReferrerId());
            if (parent == null || parent.getTeamLevel() == null || parent.getTeamLevel() == 0) {
                current = parent;
                continue;
            }
            
            // 计算级差奖励
            BigDecimal parentRate = getTeamCommissionRate(parent.getTeamLevel());
            BigDecimal childRate = getTeamCommissionRate(current.getTeamLevel() != null ? current.getTeamLevel() : 0);
            BigDecimal diffRate = parentRate.subtract(childRate);
            
            if (diffRate.compareTo(BigDecimal.ZERO) > 0) {
                BigDecimal commission = order.getActualPrice().multiply(diffRate.divide(new BigDecimal("100"))).setScale(2, RoundingMode.HALF_UP);
                String commissionType = getTeamLevelName(parent.getTeamLevel()) + "级差奖";
                
                // 发放佣金
                updateUserBalance(parent.getUserId(), commission, "分润", order.getOrderId(), commissionType);
                
                // 记录分润
                saveCommissionRecord(order.getOrderId(), parent.getUserId(), commissionType, diffRate, commission, order.getActualPrice());
            }
            
            current = parent;
        }
    }

    /**
     * 分发区域代理奖励
     */
    private void distributeAgentCommission(Orders order)
    {
        if (StringUtils.isEmpty(order.getProvince())) return;
        
        // 省级代理
        List<AgentSetting> provinceAgents = agentSettingMapper.selectAgentsByProvinceAndCity(order.getProvince(), null);
        if (!provinceAgents.isEmpty()) {
            BigDecimal provinceRate = getConfigDecimalValue("agent.province.commission_rate");
            BigDecimal totalCommission = order.getActualPrice().multiply(provinceRate.divide(new BigDecimal("100"))).setScale(2, RoundingMode.HALF_UP);
            BigDecimal avgCommission = totalCommission.divide(new BigDecimal(provinceAgents.size()), 2, RoundingMode.HALF_UP);
            
            for (AgentSetting agent : provinceAgents) {
                if ("0".equals(agent.getStatus())) {
                    updateUserBalance(agent.getUserId(), avgCommission, "分润", order.getOrderId(), "省级代理奖");
                    saveCommissionRecord(order.getOrderId(), agent.getUserId(), "省级代理奖", provinceRate, avgCommission, order.getActualPrice());
                }
            }
        }
        
        // 市级代理
        if (StringUtils.isNotEmpty(order.getCity())) {
            List<AgentSetting> cityAgents = agentSettingMapper.selectAgentsByProvinceAndCity(order.getProvince(), order.getCity());
            if (!cityAgents.isEmpty()) {
                BigDecimal cityRate = getConfigDecimalValue("agent.city.commission_rate");
                BigDecimal totalCommission = order.getActualPrice().multiply(cityRate.divide(new BigDecimal("100"))).setScale(2, RoundingMode.HALF_UP);
                BigDecimal avgCommission = totalCommission.divide(new BigDecimal(cityAgents.size()), 2, RoundingMode.HALF_UP);
                
                for (AgentSetting agent : cityAgents) {
                    if ("0".equals(agent.getStatus())) {
                        updateUserBalance(agent.getUserId(), avgCommission, "分润", order.getOrderId(), "市级代理奖");
                        saveCommissionRecord(order.getOrderId(), agent.getUserId(), "市级代理奖", cityRate, avgCommission, order.getActualPrice());
                    }
                }
            }
        }
    }

    /**
     * 更新团队业绩
     */
    private void updateTeamPerformance(SysUser user, BigDecimal amount)
    {
        SysUser current = user;
        
        // 向上更新所有上级的团队业绩
        while (current != null) {
            BigDecimal currentPerformance = current.getTotalPerformance() != null ? current.getTotalPerformance() : BigDecimal.ZERO;
            current.setTotalPerformance(currentPerformance.add(amount));
            userMapper.updateUser(current);
            
            if (current.getReferrerId() == null) break;
            current = userMapper.selectUserById(current.getReferrerId());
        }
    }

    /**
     * 保存分润记录
     */
    private void saveCommissionRecord(Long orderId, Long userId, String commissionType, BigDecimal rate, BigDecimal amount, BigDecimal orderAmount)
    {
        CommissionRecord record = new CommissionRecord();
        record.setOrderId(orderId);
        record.setUserId(userId);
        record.setCommissionType(commissionType);
        record.setCommissionRate(rate);
        record.setCommissionAmount(amount);
        record.setOrderAmount(orderAmount);
        record.setCreateTime(new Date());
        commissionRecordMapper.insertCommissionRecord(record);
    }

    /**
     * 获取团队级别对应的佣金比例
     */
    private BigDecimal getTeamCommissionRate(Integer teamLevel)
    {
        switch (teamLevel) {
            case 1: return getConfigDecimalValue("level.manager.commission_rate");
            case 2: return getConfigDecimalValue("level.director.commission_rate");
            case 3: return getConfigDecimalValue("level.partner.commission_rate");
            default: return BigDecimal.ZERO;
        }
    }

    /**
     * 获取团队级别名称
     */
    private String getTeamLevelName(Integer teamLevel)
    {
        switch (teamLevel) {
            case 1: return "经理";
            case 2: return "总监";
            case 3: return "合伙人";
            default: return "无";
        }
    }

    /**
     * 更新用户余额
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateUserBalance(Long userId, BigDecimal amount, String businessType, Long businessId, String remark)
    {
        try {
            // 查询用户信息
            SysUser user = userMapper.selectUserById(userId);
            if (user == null) {
                throw new RuntimeException("用户不存在，userId: " + userId);
            }

            BigDecimal balanceBefore = user.getBalance() != null ? user.getBalance() : BigDecimal.ZERO;
            BigDecimal balanceAfter = balanceBefore.add(amount);

            // 检查余额是否足够（如果是扣除操作）
            if (amount.compareTo(BigDecimal.ZERO) < 0 && balanceAfter.compareTo(BigDecimal.ZERO) < 0) {
                throw new RuntimeException("用户余额不足，当前余额: " + balanceBefore + ", 扣除金额: " + amount.abs());
            }

            // 更新用户余额
            user.setBalance(balanceAfter);
            int updateResult = userMapper.updateUser(user);
            if (updateResult <= 0) {
                throw new RuntimeException("更新用户余额失败，影响行数: " + updateResult);
            }

            // 验证更新是否成功
            SysUser updatedUser = userMapper.selectUserById(userId);
            if (updatedUser == null || !balanceAfter.equals(updatedUser.getBalance())) {
                throw new RuntimeException("余额更新验证失败，期望: " + balanceAfter + ", 实际: " +
                    (updatedUser != null ? updatedUser.getBalance() : "null"));
            }

            // 记录流水
            UserAccountFlow flow = new UserAccountFlow();
            flow.setUserId(userId);
            flow.setFlowType(amount.compareTo(BigDecimal.ZERO) > 0 ? 1 : 2); // 1收入 2支出
            flow.setAmount(amount.abs());
            flow.setBalanceBefore(balanceBefore);
            flow.setBalanceAfter(balanceAfter);
            flow.setBusinessType(businessType);
            flow.setBusinessId(businessId);
            flow.setRemark(remark);
            flow.setCreateTime(new Date());

            int flowResult = accountFlowMapper.insertUserAccountFlow(flow);
            if (flowResult <= 0) {
                throw new RuntimeException("插入流水记录失败，影响行数: " + flowResult);
            }

            return true;
        } catch (Exception e) {
            // 记录详细错误信息
            System.err.println("更新用户余额失败 - userId: " + userId + ", amount: " + amount +
                ", businessType: " + businessType + ", error: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("更新用户余额失败: " + e.getMessage(), e);
        }
    }

    /**
     * 检查用户是否可以升级团队级别
     */
    @Override
    public boolean checkLevelUpgrade(Long userId, Integer targetLevel)
    {
        SysUser user = userMapper.selectUserById(userId);
        if (user == null) return false;

        switch (targetLevel) {
            case 1: // 经理
                return user.getDirectGoldMembers() != null && user.getDirectGoldMembers() >= getConfigIntValue("level.manager.gold_members")
                    && user.getTotalPerformance() != null && user.getTotalPerformance().compareTo(getConfigDecimalValue("level.manager.performance")) >= 0;

            case 2: // 总监
                return user.getDirectGoldMembers() != null && user.getDirectGoldMembers() >= getConfigIntValue("level.director.gold_members")
                    && user.getTotalPerformance() != null && user.getTotalPerformance().compareTo(getConfigDecimalValue("level.director.performance")) >= 0
                    && countDifferentBranchManagers(userId) >= getConfigIntValue("level.director.managers");

            case 3: // 合伙人
                return user.getDirectGoldMembers() != null && user.getDirectGoldMembers() >= getConfigIntValue("level.partner.gold_members")
                    && user.getTotalPerformance() != null && user.getTotalPerformance().compareTo(getConfigDecimalValue("level.partner.performance")) >= 0
                    && countDifferentBranchDirectors(userId) >= getConfigIntValue("level.partner.directors");

            default:
                return false;
        }
    }

    /**
     * 升级用户团队级别
     */
    @Override
    @Transactional
    public boolean upgradeUserLevel(Long userId, Integer targetLevel)
    {
        if (!checkLevelUpgrade(userId, targetLevel)) {
            return false;
        }

        SysUser user = userMapper.selectUserById(userId);
        if (user == null) return false;

        user.setTeamLevel(targetLevel);
        return userMapper.updateUser(user) > 0;
    }

    /**
     * 绑定推荐人
     */
    @Override
    @Transactional
    public boolean bindReferrer(Long userId, String referrerPhone)
    {
        if (StringUtils.isEmpty(referrerPhone)) {
            return false;
        }

        SysUser user = userMapper.selectUserById(userId);
        if (user == null) return false;

        SysUser referrer = userMapper.selectUserByUserName(referrerPhone);
        if (referrer == null) {
            // 尝试通过手机号查找
            SysUser searchUser = new SysUser();
            searchUser.setPhonenumber(referrerPhone);
            List<SysUser> users = userMapper.selectUserList(searchUser);
            if (users.isEmpty()) {
                return false;
            }
            referrer = users.get(0);
        }

        user.setReferrerId(referrer.getUserId());
        return userMapper.updateUser(user) > 0;
    }

    /**
     * 设置用户会员等级
     */
    @Override
    @Transactional
    public boolean setMemberLevel(Long userId, Integer memberLevel)
    {
        SysUser user = userMapper.selectUserById(userId);
        if (user == null) return false;

        Integer oldLevel = user.getMemberLevel();

        // 验证不能降级：新等级不能低于当前等级
        if (oldLevel != null && memberLevel < oldLevel) {
            throw new RuntimeException("不能将会员等级从" + getLevelName(oldLevel) + "降级为" + getLevelName(memberLevel));
        }

        user.setMemberLevel(memberLevel);

        // 如果升级为金牌会员，需要更新推荐人的直推金牌会员数量
        if (memberLevel == 2 && (oldLevel == null || oldLevel < 2)) {
            if (user.getReferrerId() != null) {
                SysUser referrer = userMapper.selectUserById(user.getReferrerId());
                if (referrer != null) {
                    Integer count = referrer.getDirectGoldMembers() != null ? referrer.getDirectGoldMembers() : 0;
                    referrer.setDirectGoldMembers(count + 1);
                    userMapper.updateUser(referrer);
                }
            }
        }

        return userMapper.updateUser(user) > 0;
    }

    /**
     * 计算用户团队业绩
     */
    @Override
    public BigDecimal calculateTeamPerformance(Long userId)
    {
        SysUser user = userMapper.selectUserById(userId);
        if (user == null) return BigDecimal.ZERO;

        return user.getTotalPerformance() != null ? user.getTotalPerformance() : BigDecimal.ZERO;
    }

    /**
     * 统计用户直推金牌会员数量
     */
    @Override
    public int countDirectGoldMembers(Long userId)
    {
        SysUser user = userMapper.selectUserById(userId);
        if (user == null) return 0;

        return user.getDirectGoldMembers() != null ? user.getDirectGoldMembers() : 0;
    }

    /**
     * 统计用户培养的经理数量（不同分支）
     */
    @Override
    public int countDifferentBranchManagers(Long userId)
    {
        SysUser searchUser = new SysUser();
        searchUser.setReferrerId(userId);
        searchUser.setTeamLevel(1); // 经理级别
        List<SysUser> managers = userMapper.selectUserList(searchUser);
        return managers.size();
    }

    /**
     * 统计用户培养的总监数量（不同分支）
     */
    @Override
    public int countDifferentBranchDirectors(Long userId)
    {
        SysUser searchUser = new SysUser();
        searchUser.setReferrerId(userId);
        searchUser.setTeamLevel(2); // 总监级别
        List<SysUser> directors = userMapper.selectUserList(searchUser);
        return directors.size();
    }

    /**
     * 获取配置值
     */
    @Override
    public String getConfigValue(String configKey)
    {
        DistributionConfig config = configMapper.selectDistributionConfigByKey(configKey);
        return config != null ? config.getConfigValue() : "";
    }

    /**
     * 获取配置值（整数）
     */
    @Override
    public Integer getConfigIntValue(String configKey)
    {
        String value = getConfigValue(configKey);
        try {
            return StringUtils.isNotEmpty(value) ? Integer.parseInt(value) : 0;
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    /**
     * 获取配置值（BigDecimal）
     */
    @Override
    public BigDecimal getConfigDecimalValue(String configKey)
    {
        String value = getConfigValue(configKey);
        try {
            return StringUtils.isNotEmpty(value) ? new BigDecimal(value) : BigDecimal.ZERO;
        } catch (NumberFormatException e) {
            return BigDecimal.ZERO;
        }
    }

    /**
     * 获取会员等级名称
     */
    private String getLevelName(Integer level) {
        if (level == null) return "未知";
        switch (level) {
            case 0: return "普通用户";
            case 1: return "普通会员";
            case 2: return "金牌会员";
            default: return "未知";
        }
    }
}
