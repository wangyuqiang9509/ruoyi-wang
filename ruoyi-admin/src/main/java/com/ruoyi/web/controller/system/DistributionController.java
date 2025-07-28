package com.ruoyi.web.controller.system;

import java.math.BigDecimal;
import java.util.List;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.system.domain.UserAccountFlow;
import com.ruoyi.system.domain.Orders;
import com.ruoyi.system.domain.CommissionRecord;
import com.ruoyi.system.service.IUserAccountFlowService;
import com.ruoyi.system.service.IOrdersService;
import com.ruoyi.system.service.IDistributionService;
import com.ruoyi.system.mapper.CommissionRecordMapper;

/**
 * 分销系统Controller
 * 
 * @author ruoyi
 * @date 2025-01-28
 */
@RestController
@RequestMapping("/system/distribution")
public class DistributionController extends BaseController
{
    @Autowired
    private IDistributionService distributionService;
    
    @Autowired
    private IUserAccountFlowService userAccountFlowService;
    
    @Autowired
    private IOrdersService ordersService;
    
    @Autowired
    private CommissionRecordMapper commissionRecordMapper;

    /**
     * 用户下单
     */
    @PreAuthorize("@ss.hasPermi('system:distribution:order')")
    @Log(title = "用户下单", businessType = BusinessType.INSERT)
    @PostMapping("/order")
    public AjaxResult placeOrder(@RequestBody PlaceOrderRequest request)
    {
        try {
            Orders order = distributionService.processOrder(
                request.getUserId(), 
                request.getProductId(), 
                request.getProvince(), 
                request.getCity(), 
                request.getAddress()
            );
            return success(order);
        } catch (Exception e) {
            return error(e.getMessage());
        }
    }

    /**
     * 更新用户余额
     */
    @PreAuthorize("@ss.hasPermi('system:distribution:balance')")
    @Log(title = "更新用户余额", businessType = BusinessType.UPDATE)
    @PostMapping("/balance")
    public AjaxResult updateBalance(@RequestBody UpdateBalanceRequest request)
    {
        boolean result = distributionService.updateUserBalance(
            request.getUserId(), 
            request.getAmount(), 
            request.getBusinessType(), 
            null, 
            request.getRemark()
        );
        return result ? success() : error("操作失败");
    }

    /**
     * 绑定推荐人
     */
    @PreAuthorize("@ss.hasPermi('system:distribution:referrer')")
    @Log(title = "绑定推荐人", businessType = BusinessType.UPDATE)
    @PostMapping("/referrer")
    public AjaxResult bindReferrer(@RequestBody BindReferrerRequest request)
    {
        boolean result = distributionService.bindReferrer(request.getUserId(), request.getReferrerPhone());
        return result ? success() : error("绑定失败，请检查推荐人手机号是否正确");
    }

    /**
     * 设置会员等级
     */
    @PreAuthorize("@ss.hasPermi('system:distribution:member')")
    @Log(title = "设置会员等级", businessType = BusinessType.UPDATE)
    @PostMapping("/member")
    public AjaxResult setMemberLevel(@RequestBody SetMemberLevelRequest request)
    {
        boolean result = distributionService.setMemberLevel(request.getUserId(), request.getMemberLevel());
        return result ? success() : error("设置失败");
    }

    /**
     * 升级团队级别
     */
    @PreAuthorize("@ss.hasPermi('system:distribution:level')")
    @Log(title = "升级团队级别", businessType = BusinessType.UPDATE)
    @PostMapping("/level")
    public AjaxResult upgradeLevel(@RequestBody UpgradeLevelRequest request)
    {
        boolean canUpgrade = distributionService.checkLevelUpgrade(request.getUserId(), request.getTargetLevel());
        if (!canUpgrade) {
            return error("不满足升级条件");
        }
        
        boolean result = distributionService.upgradeUserLevel(request.getUserId(), request.getTargetLevel());
        return result ? success() : error("升级失败");
    }

    /**
     * 查询用户账户流水列表
     */
    @PreAuthorize("@ss.hasPermi('system:distribution:flow')")
    @GetMapping("/flow/list")
    public TableDataInfo flowList(UserAccountFlow userAccountFlow)
    {
        startPage();
        List<UserAccountFlow> list = userAccountFlowService.selectUserAccountFlowList(userAccountFlow);
        return getDataTable(list);
    }

    /**
     * 查询订单列表
     */
    @PreAuthorize("@ss.hasPermi('system:distribution:order')")
    @GetMapping("/order/list")
    public TableDataInfo orderList(Orders orders)
    {
        startPage();
        List<Orders> list = ordersService.selectOrdersList(orders);
        return getDataTable(list);
    }

    /**
     * 查询分润记录列表
     */
    @PreAuthorize("@ss.hasPermi('system:distribution:commission')")
    @GetMapping("/commission/list")
    public TableDataInfo commissionList(CommissionRecord commissionRecord)
    {
        startPage();
        List<CommissionRecord> list = commissionRecordMapper.selectCommissionRecordList(commissionRecord);
        return getDataTable(list);
    }

    /**
     * 导出用户账户流水列表
     */
    @PreAuthorize("@ss.hasPermi('system:distribution:export')")
    @Log(title = "用户账户流水", businessType = BusinessType.EXPORT)
    @PostMapping("/flow/export")
    public void flowExport(HttpServletResponse response, UserAccountFlow userAccountFlow)
    {
        List<UserAccountFlow> list = userAccountFlowService.selectUserAccountFlowList(userAccountFlow);
        ExcelUtil<UserAccountFlow> util = new ExcelUtil<UserAccountFlow>(UserAccountFlow.class);
        util.exportExcel(response, list, "用户账户流水数据");
    }

    /**
     * 导出订单列表
     */
    @PreAuthorize("@ss.hasPermi('system:distribution:export')")
    @Log(title = "订单", businessType = BusinessType.EXPORT)
    @PostMapping("/order/export")
    public void orderExport(HttpServletResponse response, Orders orders)
    {
        List<Orders> list = ordersService.selectOrdersList(orders);
        ExcelUtil<Orders> util = new ExcelUtil<Orders>(Orders.class);
        util.exportExcel(response, list, "订单数据");
    }

    /**
     * 导出分润记录列表
     */
    @PreAuthorize("@ss.hasPermi('system:distribution:export')")
    @Log(title = "分润记录", businessType = BusinessType.EXPORT)
    @PostMapping("/commission/export")
    public void commissionExport(HttpServletResponse response, CommissionRecord commissionRecord)
    {
        List<CommissionRecord> list = commissionRecordMapper.selectCommissionRecordList(commissionRecord);
        ExcelUtil<CommissionRecord> util = new ExcelUtil<CommissionRecord>(CommissionRecord.class);
        util.exportExcel(response, list, "分润记录数据");
    }

    // 请求对象类
    public static class PlaceOrderRequest {
        private Long userId;
        private Long productId;
        private String province;
        private String city;
        private String address;
        
        // getters and setters
        public Long getUserId() { return userId; }
        public void setUserId(Long userId) { this.userId = userId; }
        public Long getProductId() { return productId; }
        public void setProductId(Long productId) { this.productId = productId; }
        public String getProvince() { return province; }
        public void setProvince(String province) { this.province = province; }
        public String getCity() { return city; }
        public void setCity(String city) { this.city = city; }
        public String getAddress() { return address; }
        public void setAddress(String address) { this.address = address; }
    }

    public static class UpdateBalanceRequest {
        private Long userId;
        private BigDecimal amount;
        private String businessType;
        private String remark;
        
        // getters and setters
        public Long getUserId() { return userId; }
        public void setUserId(Long userId) { this.userId = userId; }
        public BigDecimal getAmount() { return amount; }
        public void setAmount(BigDecimal amount) { this.amount = amount; }
        public String getBusinessType() { return businessType; }
        public void setBusinessType(String businessType) { this.businessType = businessType; }
        public String getRemark() { return remark; }
        public void setRemark(String remark) { this.remark = remark; }
    }

    public static class BindReferrerRequest {
        private Long userId;
        private String referrerPhone;
        
        // getters and setters
        public Long getUserId() { return userId; }
        public void setUserId(Long userId) { this.userId = userId; }
        public String getReferrerPhone() { return referrerPhone; }
        public void setReferrerPhone(String referrerPhone) { this.referrerPhone = referrerPhone; }
    }

    public static class SetMemberLevelRequest {
        private Long userId;
        private Integer memberLevel;
        
        // getters and setters
        public Long getUserId() { return userId; }
        public void setUserId(Long userId) { this.userId = userId; }
        public Integer getMemberLevel() { return memberLevel; }
        public void setMemberLevel(Integer memberLevel) { this.memberLevel = memberLevel; }
    }

    public static class UpgradeLevelRequest {
        private Long userId;
        private Integer targetLevel;
        
        // getters and setters
        public Long getUserId() { return userId; }
        public void setUserId(Long userId) { this.userId = userId; }
        public Integer getTargetLevel() { return targetLevel; }
        public void setTargetLevel(Integer targetLevel) { this.targetLevel = targetLevel; }
    }
}
