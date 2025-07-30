package com.ruoyi.web.controller.system;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.domain.entity.SysDept;
import com.ruoyi.common.core.domain.entity.SysRole;
import com.ruoyi.common.core.domain.entity.SysUser;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.system.service.ISysDeptService;
import com.ruoyi.system.service.ISysPostService;
import com.ruoyi.system.service.ISysRoleService;
import com.ruoyi.system.service.ISysUserService;
import com.ruoyi.system.service.IDistributionService;
import com.ruoyi.system.service.IUserAccountFlowService;
import com.ruoyi.system.service.IAgentSettingService;
import com.ruoyi.system.domain.UserAccountFlow;
import com.ruoyi.system.domain.AgentSetting;
import com.ruoyi.system.domain.Product;
import com.ruoyi.system.mapper.ProductMapper;
import java.math.BigDecimal;

/**
 * 用户信息
 * 
 * @author ruoyi
 */
@RestController
@RequestMapping("/system/user")
public class SysUserController extends BaseController
{
    @Autowired
    private ISysUserService userService;

    @Autowired
    private ISysRoleService roleService;

    @Autowired
    private ISysDeptService deptService;

    @Autowired
    private IDistributionService distributionService;

    @Autowired
    private IUserAccountFlowService userAccountFlowService;

    @Autowired
    private IAgentSettingService agentSettingService;

    @Autowired
    private ProductMapper productMapper;

    @Autowired
    private ISysPostService postService;

    /**
     * 获取用户列表
     */
    @PreAuthorize("@ss.hasPermi('system:user:list')")
    @GetMapping("/list")
    public TableDataInfo list(SysUser user)
    {
        startPage();
        List<SysUser> list = userService.selectUserList(user);
        return getDataTable(list);
    }

    @Log(title = "用户管理", businessType = BusinessType.EXPORT)
    @PreAuthorize("@ss.hasPermi('system:user:export')")
    @PostMapping("/export")
    public void export(HttpServletResponse response, SysUser user)
    {
        List<SysUser> list = userService.selectUserList(user);
        ExcelUtil<SysUser> util = new ExcelUtil<SysUser>(SysUser.class);
        util.exportExcel(response, list, "用户数据");
    }

    @Log(title = "用户管理", businessType = BusinessType.IMPORT)
    @PreAuthorize("@ss.hasPermi('system:user:import')")
    @PostMapping("/importData")
    public AjaxResult importData(MultipartFile file, boolean updateSupport) throws Exception
    {
        ExcelUtil<SysUser> util = new ExcelUtil<SysUser>(SysUser.class);
        List<SysUser> userList = util.importExcel(file.getInputStream());
        String operName = getUsername();
        String message = userService.importUser(userList, updateSupport, operName);
        return success(message);
    }

    @PostMapping("/importTemplate")
    public void importTemplate(HttpServletResponse response)
    {
        ExcelUtil<SysUser> util = new ExcelUtil<SysUser>(SysUser.class);
        util.importTemplateExcel(response, "用户数据");
    }

    /**
     * 根据用户编号获取详细信息
     */
    @PreAuthorize("@ss.hasPermi('system:user:query')")
    @GetMapping(value = { "/", "/{userId}" })
    public AjaxResult getInfo(@PathVariable(value = "userId", required = false) Long userId)
    {
        AjaxResult ajax = AjaxResult.success();
        if (StringUtils.isNotNull(userId))
        {
            userService.checkUserDataScope(userId);
            SysUser sysUser = userService.selectUserById(userId);
            ajax.put(AjaxResult.DATA_TAG, sysUser);
            ajax.put("postIds", postService.selectPostListByUserId(userId));
            ajax.put("roleIds", sysUser.getRoles().stream().map(SysRole::getRoleId).collect(Collectors.toList()));
        }
        List<SysRole> roles = roleService.selectRoleAll();
        ajax.put("roles", SysUser.isAdmin(userId) ? roles : roles.stream().filter(r -> !r.isAdmin()).collect(Collectors.toList()));
        ajax.put("posts", postService.selectPostAll());
        return ajax;
    }

    /**
     * 新增用户
     */
    @PreAuthorize("@ss.hasPermi('system:user:add')")
    @Log(title = "用户管理", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@Validated @RequestBody SysUser user)
    {
        deptService.checkDeptDataScope(user.getDeptId());
        roleService.checkRoleDataScope(user.getRoleIds());
        if (!userService.checkUserNameUnique(user))
        {
            return error("新增用户'" + user.getUserName() + "'失败，登录账号已存在");
        }
        else if (StringUtils.isNotEmpty(user.getPhonenumber()) && !userService.checkPhoneUnique(user))
        {
            return error("新增用户'" + user.getUserName() + "'失败，手机号码已存在");
        }
        else if (StringUtils.isNotEmpty(user.getEmail()) && !userService.checkEmailUnique(user))
        {
            return error("新增用户'" + user.getUserName() + "'失败，邮箱账号已存在");
        }
        user.setCreateBy(getUsername());
        user.setPassword(SecurityUtils.encryptPassword(user.getPassword()));
        return toAjax(userService.insertUser(user));
    }

    /**
     * 修改用户
     */
    @PreAuthorize("@ss.hasPermi('system:user:edit')")
    @Log(title = "用户管理", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@Validated @RequestBody SysUser user)
    {
        userService.checkUserAllowed(user);
        userService.checkUserDataScope(user.getUserId());
        deptService.checkDeptDataScope(user.getDeptId());
        roleService.checkRoleDataScope(user.getRoleIds());
        if (!userService.checkUserNameUnique(user))
        {
            return error("修改用户'" + user.getUserName() + "'失败，登录账号已存在");
        }
        else if (StringUtils.isNotEmpty(user.getPhonenumber()) && !userService.checkPhoneUnique(user))
        {
            return error("修改用户'" + user.getUserName() + "'失败，手机号码已存在");
        }
        else if (StringUtils.isNotEmpty(user.getEmail()) && !userService.checkEmailUnique(user))
        {
            return error("修改用户'" + user.getUserName() + "'失败，邮箱账号已存在");
        }
        user.setUpdateBy(getUsername());
        return toAjax(userService.updateUser(user));
    }

    /**
     * 删除用户
     */
    @PreAuthorize("@ss.hasPermi('system:user:remove')")
    @Log(title = "用户管理", businessType = BusinessType.DELETE)
    @DeleteMapping("/{userIds}")
    public AjaxResult remove(@PathVariable Long[] userIds)
    {
        if (ArrayUtils.contains(userIds, getUserId()))
        {
            return error("当前用户不能删除");
        }
        return toAjax(userService.deleteUserByIds(userIds));
    }

    /**
     * 重置密码
     */
    @PreAuthorize("@ss.hasPermi('system:user:resetPwd')")
    @Log(title = "用户管理", businessType = BusinessType.UPDATE)
    @PutMapping("/resetPwd")
    public AjaxResult resetPwd(@RequestBody SysUser user)
    {
        userService.checkUserAllowed(user);
        userService.checkUserDataScope(user.getUserId());
        user.setPassword(SecurityUtils.encryptPassword(user.getPassword()));
        user.setUpdateBy(getUsername());
        return toAjax(userService.resetPwd(user));
    }

    /**
     * 状态修改
     */
    @PreAuthorize("@ss.hasPermi('system:user:edit')")
    @Log(title = "用户管理", businessType = BusinessType.UPDATE)
    @PutMapping("/changeStatus")
    public AjaxResult changeStatus(@RequestBody SysUser user)
    {
        userService.checkUserAllowed(user);
        userService.checkUserDataScope(user.getUserId());
        user.setUpdateBy(getUsername());
        return toAjax(userService.updateUserStatus(user));
    }

    /**
     * 根据用户编号获取授权角色
     */
    @PreAuthorize("@ss.hasPermi('system:user:query')")
    @GetMapping("/authRole/{userId}")
    public AjaxResult authRole(@PathVariable("userId") Long userId)
    {
        AjaxResult ajax = AjaxResult.success();
        SysUser user = userService.selectUserById(userId);
        List<SysRole> roles = roleService.selectRolesByUserId(userId);
        ajax.put("user", user);
        ajax.put("roles", SysUser.isAdmin(userId) ? roles : roles.stream().filter(r -> !r.isAdmin()).collect(Collectors.toList()));
        return ajax;
    }

    /**
     * 用户授权角色
     */
    @PreAuthorize("@ss.hasPermi('system:user:edit')")
    @Log(title = "用户管理", businessType = BusinessType.GRANT)
    @PutMapping("/authRole")
    public AjaxResult insertAuthRole(Long userId, Long[] roleIds)
    {
        userService.checkUserDataScope(userId);
        roleService.checkRoleDataScope(roleIds);
        userService.insertUserAuth(userId, roleIds);
        return success();
    }

    /**
     * 获取部门树列表
     */
    @PreAuthorize("@ss.hasPermi('system:user:list')")
    @GetMapping("/deptTree")
    public AjaxResult deptTree(SysDept dept)
    {
        return success(deptService.selectDeptTreeList(dept));
    }

    /**
     * 查看用户账户信息
     */
    @PreAuthorize("@ss.hasPermi('system:user:query')")
    @GetMapping("/account/{userId}")
    public AjaxResult getUserAccount(@PathVariable Long userId)
    {
        userService.checkUserDataScope(userId);
        SysUser user = userService.selectUserById(userId);
        if (user == null) {
            return error("用户不存在");
        }

        AjaxResult result = AjaxResult.success();
        result.put("balance", user.getBalance() != null ? user.getBalance() : BigDecimal.ZERO);
        result.put("memberLevel", user.getMemberLevel() != null ? user.getMemberLevel() : 0);
        result.put("teamLevel", user.getTeamLevel() != null ? user.getTeamLevel() : 0);
        result.put("totalPerformance", user.getTotalPerformance() != null ? user.getTotalPerformance() : BigDecimal.ZERO);
        result.put("directGoldMembers", user.getDirectGoldMembers() != null ? user.getDirectGoldMembers() : 0);
        result.put("referrerId", user.getReferrerId());
        result.put("referrerName", user.getReferrerName());
        result.put("agentLevel", user.getAgentLevel() != null ? user.getAgentLevel() : 0);
        result.put("agentProvince", user.getAgentProvince());
        result.put("agentCity", user.getAgentCity());

        return result;
    }

    /**
     * 查看用户账户流水
     */
    @PreAuthorize("@ss.hasPermi('system:user:query')")
    @GetMapping("/account/flow/{userId}")
    public TableDataInfo getUserAccountFlow(@PathVariable Long userId)
    {
        userService.checkUserDataScope(userId);
        startPage();
        UserAccountFlow flow = new UserAccountFlow();
        flow.setUserId(userId);
        List<UserAccountFlow> list = userAccountFlowService.selectUserAccountFlowList(flow);
        return getDataTable(list);
    }

    /**
     * 更新用户余额
     */
    @PreAuthorize("@ss.hasPermi('system:user:edit')")
    @Log(title = "用户余额管理", businessType = BusinessType.UPDATE)
    @PostMapping("/balance")
    public AjaxResult updateUserBalance(@RequestBody UpdateBalanceRequest request)
    {
        userService.checkUserDataScope(request.getUserId());

        // 根据操作类型确定金额的正负
        BigDecimal amount = request.getAmount();
        if ("subtract".equals(request.getType())) {
            amount = amount.negate(); // 扣除操作，金额变为负数
        }

        boolean result = distributionService.updateUserBalance(
            request.getUserId(),
            amount,
            "管理员操作",
            null,
            request.getRemark()
        );
        return result ? success() : error("操作失败");
    }

    /**
     * 绑定推荐人
     */
    @PreAuthorize("@ss.hasPermi('system:user:edit')")
    @Log(title = "绑定推荐人", businessType = BusinessType.UPDATE)
    @PostMapping("/referrer")
    public AjaxResult bindReferrer(@RequestBody BindReferrerRequest request)
    {
        userService.checkUserDataScope(request.getUserId());
        boolean result = distributionService.bindReferrer(request.getUserId(), request.getReferrerPhone());
        return result ? success() : error("绑定失败，请检查推荐人手机号是否正确");
    }

    /**
     * 设置会员等级
     */
    @PreAuthorize("@ss.hasPermi('system:user:edit')")
    @Log(title = "设置会员等级", businessType = BusinessType.UPDATE)
    @PostMapping("/member")
    public AjaxResult setMemberLevel(@RequestBody SetMemberLevelRequest request)
    {
        userService.checkUserDataScope(request.getUserId());
        boolean result = distributionService.setMemberLevel(request.getUserId(), request.getMemberLevel());
        return result ? success() : error("设置失败");
    }

    /**
     * 升级团队级别
     */
    @PreAuthorize("@ss.hasPermi('system:user:edit')")
    @Log(title = "升级团队级别", businessType = BusinessType.UPDATE)
    @PostMapping("/level")
    public AjaxResult upgradeLevel(@RequestBody UpgradeLevelRequest request)
    {
        userService.checkUserDataScope(request.getUserId());

        // 获取详细的升级条件检查结果
        String checkResult = getUpgradeCheckMessage(request.getUserId(), request.getTargetLevel());
        if (checkResult != null) {
            return error(checkResult);
        }

        boolean result = distributionService.upgradeUserLevel(request.getUserId(), request.getTargetLevel());
        return result ? success() : error("升级失败");
    }

    /**
     * 测试升级算法 - 用于验证优化后的不同分支统计逻辑
     */
    @PreAuthorize("@ss.hasPermi('system:user:list')")
    @GetMapping("/testUpgradeAlgorithm/{userId}")
    public AjaxResult testUpgradeAlgorithm(@PathVariable Long userId)
    {
        try {
            SysUser user = userService.selectUserById(userId);
            if (user == null) {
                return error("用户不存在");
            }

            // 统计不同分支的经理和总监数量
            int managerCount = distributionService.countDifferentBranchManagers(userId);
            int directorCount = distributionService.countDifferentBranchDirectors(userId);

            // 检查升级条件
            boolean canUpgradeToManager = distributionService.checkLevelUpgrade(userId, 1);
            boolean canUpgradeToDirector = distributionService.checkLevelUpgrade(userId, 2);
            boolean canUpgradeToPartner = distributionService.checkLevelUpgrade(userId, 3);

            Map<String, Object> result = new HashMap<>();
            result.put("userId", userId);
            result.put("userName", user.getUserName());
            result.put("nickName", user.getNickName());
            result.put("currentTeamLevel", user.getTeamLevel());
            result.put("differentBranchManagers", managerCount);
            result.put("differentBranchDirectors", directorCount);
            result.put("canUpgradeToManager", canUpgradeToManager);
            result.put("canUpgradeToDirector", canUpgradeToDirector);
            result.put("canUpgradeToPartner", canUpgradeToPartner);
            result.put("directGoldMembers", user.getDirectGoldMembers());
            result.put("totalPerformance", user.getTotalPerformance());

            return success(result);
        } catch (Exception e) {
            return error("测试失败：" + e.getMessage());
        }
    }

    /**
     * 设置用户代理
     */
    @PreAuthorize("@ss.hasPermi('system:user:edit')")
    @Log(title = "设置用户代理", businessType = BusinessType.UPDATE)
    @PostMapping("/agent")
    public AjaxResult setUserAgent(@RequestBody SetAgentRequest request)
    {
        userService.checkUserDataScope(request.getUserId());

        // 参数验证
        if (request.getUserId() == null) {
            return error("用户ID不能为空");
        }
        if (request.getAgentType() == null || (request.getAgentType() != 1 && request.getAgentType() != 2)) {
            return error("代理类型错误");
        }
        if (StringUtils.isEmpty(request.getProvince())) {
            return error("省份不能为空");
        }
        if (request.getAgentType() == 2 && StringUtils.isEmpty(request.getCity())) {
            return error("市级代理必须选择城市");
        }

        boolean result = agentSettingService.setUserAgent(
            request.getUserId(),
            request.getAgentType(),
            request.getProvince(),
            request.getCity()
        );

        return result ? success() : error("设置代理失败");
    }

    /**
     * 检查升级条件并返回详细信息
     */
    private String getUpgradeCheckMessage(Long userId, Integer targetLevel) {
        SysUser user = userService.selectUserById(userId);
        if (user == null) return "用户不存在";

        switch (targetLevel) {
            case 1: // 经理
                int managerGoldRequired = distributionService.getConfigIntValue("level.manager.gold_members");
                java.math.BigDecimal managerPerformanceRequired = distributionService.getConfigDecimalValue("level.manager.performance");

                int currentGoldMembers = user.getDirectGoldMembers() != null ? user.getDirectGoldMembers() : 0;
                java.math.BigDecimal currentPerformance = user.getTotalPerformance() != null ? user.getTotalPerformance() : java.math.BigDecimal.ZERO;

                if (currentGoldMembers < managerGoldRequired) {
                    return String.format("直推金牌会员数量不足，需要%d个，当前%d个", managerGoldRequired, currentGoldMembers);
                }
                if (currentPerformance.compareTo(managerPerformanceRequired) < 0) {
                    return String.format("团队业绩不足，需要%.2f，当前%.2f", managerPerformanceRequired, currentPerformance);
                }
                break;

            case 2: // 总监
                int directorGoldRequired = distributionService.getConfigIntValue("level.director.gold_members");
                java.math.BigDecimal directorPerformanceRequired = distributionService.getConfigDecimalValue("level.director.performance");
                int directorManagersRequired = distributionService.getConfigIntValue("level.director.managers");

                currentGoldMembers = user.getDirectGoldMembers() != null ? user.getDirectGoldMembers() : 0;
                currentPerformance = user.getTotalPerformance() != null ? user.getTotalPerformance() : java.math.BigDecimal.ZERO;
                int currentManagers = distributionService.countDifferentBranchManagers(userId);

                if (currentGoldMembers < directorGoldRequired) {
                    return String.format("直推金牌会员数量不足，需要%d个，当前%d个", directorGoldRequired, currentGoldMembers);
                }
                if (currentPerformance.compareTo(directorPerformanceRequired) < 0) {
                    return String.format("团队业绩不足，需要%.2f，当前%.2f", directorPerformanceRequired, currentPerformance);
                }
                if (currentManagers < directorManagersRequired) {
                    return String.format("培养经理数量不足，需要%d个，当前%d个", directorManagersRequired, currentManagers);
                }
                break;

            case 3: // 合伙人
                int partnerGoldRequired = distributionService.getConfigIntValue("level.partner.gold_members");
                java.math.BigDecimal partnerPerformanceRequired = distributionService.getConfigDecimalValue("level.partner.performance");
                int partnerDirectorsRequired = distributionService.getConfigIntValue("level.partner.directors");

                currentGoldMembers = user.getDirectGoldMembers() != null ? user.getDirectGoldMembers() : 0;
                currentPerformance = user.getTotalPerformance() != null ? user.getTotalPerformance() : java.math.BigDecimal.ZERO;
                int currentDirectors = distributionService.countDifferentBranchDirectors(userId);

                if (currentGoldMembers < partnerGoldRequired) {
                    return String.format("直推金牌会员数量不足，需要%d个，当前%d个", partnerGoldRequired, currentGoldMembers);
                }
                if (currentPerformance.compareTo(partnerPerformanceRequired) < 0) {
                    return String.format("团队业绩不足，需要%.2f，当前%.2f", partnerPerformanceRequired, currentPerformance);
                }
                if (currentDirectors < partnerDirectorsRequired) {
                    return String.format("培养总监数量不足，需要%d个，当前%d个", partnerDirectorsRequired, currentDirectors);
                }
                break;

            default:
                return "无效的目标级别";
        }

        return null; // 满足所有条件
    }



    /**
     * 取消代理
     */
    @PreAuthorize("@ss.hasPermi('system:user:edit')")
    @Log(title = "取消代理", businessType = BusinessType.UPDATE)
    @DeleteMapping("/agent/{userId}")
    public AjaxResult cancelAgent(@PathVariable Long userId)
    {
        userService.checkUserDataScope(userId);
        boolean result = agentSettingService.cancelUserAgent(userId);
        return result ? success() : error("取消失败");
    }

    /**
     * 模拟下单
     */
    @PreAuthorize("@ss.hasPermi('system:user:edit')")
    @Log(title = "模拟下单", businessType = BusinessType.INSERT)
    @PostMapping("/order")
    public AjaxResult placeOrder(@RequestBody PlaceOrderRequest request)
    {
        userService.checkUserDataScope(request.getUserId());
        try {
            distributionService.processOrder(
                request.getUserId(),
                request.getProductId(),
                request.getProvince(),
                request.getCity(),
                request.getAddress()
            );
            return success();
        } catch (Exception e) {
            return error(e.getMessage());
        }
    }

    /**
     * 获取商品列表
     */
    @PreAuthorize("@ss.hasPermi('system:user:query')")
    @GetMapping("/products")
    public AjaxResult getProducts()
    {
        Product product = new Product();
        product.setStatus("0");
        List<Product> products = productMapper.selectProductList(product);
        return success(products);
    }

    /**
     * 获取订单价格预览
     */
    @PreAuthorize("@ss.hasPermi('system:user:query')")
    @PostMapping("/order/preview")
    public AjaxResult getOrderPreview(@RequestBody OrderPreviewRequest request)
    {
        userService.checkUserDataScope(request.getUserId());
        try {
            Map<String, Object> preview = distributionService.getOrderPreview(
                request.getUserId(),
                request.getProductId()
            );
            return success(preview);
        } catch (Exception e) {
            return error(e.getMessage());
        }
    }



    // 请求对象类
    public static class UpdateBalanceRequest {
        private Long userId;
        private String type; // 操作类型：add增加，subtract扣除
        private BigDecimal amount;
        private String remark;

        public Long getUserId() { return userId; }
        public void setUserId(Long userId) { this.userId = userId; }
        public String getType() { return type; }
        public void setType(String type) { this.type = type; }
        public BigDecimal getAmount() { return amount; }
        public void setAmount(BigDecimal amount) { this.amount = amount; }
        public String getRemark() { return remark; }
        public void setRemark(String remark) { this.remark = remark; }
    }

    public static class BindReferrerRequest {
        private Long userId;
        private String referrerPhone;

        public Long getUserId() { return userId; }
        public void setUserId(Long userId) { this.userId = userId; }
        public String getReferrerPhone() { return referrerPhone; }
        public void setReferrerPhone(String referrerPhone) { this.referrerPhone = referrerPhone; }
    }

    public static class SetMemberLevelRequest {
        private Long userId;
        private Integer memberLevel;

        public Long getUserId() { return userId; }
        public void setUserId(Long userId) { this.userId = userId; }
        public Integer getMemberLevel() { return memberLevel; }
        public void setMemberLevel(Integer memberLevel) { this.memberLevel = memberLevel; }
    }

    public static class UpgradeLevelRequest {
        private Long userId;
        private Integer targetLevel;

        public Long getUserId() { return userId; }
        public void setUserId(Long userId) { this.userId = userId; }
        public Integer getTargetLevel() { return targetLevel; }
        public void setTargetLevel(Integer targetLevel) { this.targetLevel = targetLevel; }
    }

    public static class SetAgentRequest {
        private Long userId;
        private Integer agentType;
        private String province;
        private String city;

        public Long getUserId() { return userId; }
        public void setUserId(Long userId) { this.userId = userId; }
        public Integer getAgentType() { return agentType; }
        public void setAgentType(Integer agentType) { this.agentType = agentType; }
        public String getProvince() { return province; }
        public void setProvince(String province) { this.province = province; }
        public String getCity() { return city; }
        public void setCity(String city) { this.city = city; }
    }

    public static class PlaceOrderRequest {
        private Long userId;
        private Long productId;
        private String province;
        private String city;
        private String address;

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

    public static class OrderPreviewRequest {
        private Long userId;
        private Long productId;

        public Long getUserId() { return userId; }
        public void setUserId(Long userId) { this.userId = userId; }
        public Long getProductId() { return productId; }
        public void setProductId(Long productId) { this.productId = productId; }
    }
}
