package com.ruoyi.common.core.domain.entity;

import java.util.Date;
import java.util.List;
import javax.validation.constraints.*;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.annotation.Excel.ColumnType;
import com.ruoyi.common.annotation.Excel.Type;
import com.ruoyi.common.annotation.Excels;
import com.ruoyi.common.core.domain.BaseEntity;
import com.ruoyi.common.xss.Xss;

/**
 * 用户对象 sys_user
 * 
 * @author ruoyi
 */
public class SysUser extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 用户ID */
    @Excel(name = "用户序号", type = Type.EXPORT, cellType = ColumnType.NUMERIC, prompt = "用户编号")
    private Long userId;

    /** 部门ID */
    @Excel(name = "部门编号", type = Type.IMPORT)
    private Long deptId;

    /** 用户账号 */
    @Excel(name = "登录名称")
    private String userName;

    /** 用户昵称 */
    @Excel(name = "用户名称")
    private String nickName;

    /** 用户邮箱 */
    @Excel(name = "用户邮箱")
    private String email;

    /** 手机号码 */
    @Excel(name = "手机号码", cellType = ColumnType.TEXT)
    private String phonenumber;

    /** 用户性别 */
    @Excel(name = "用户性别", readConverterExp = "0=男,1=女,2=未知")
    private String sex;

    /** 用户头像 */
    private String avatar;

    /** 密码 */
    private String password;

    /** 账号状态（0正常 1停用） */
    @Excel(name = "账号状态", readConverterExp = "0=正常,1=停用")
    private String status;

    /** 删除标志（0代表存在 2代表删除） */
    private String delFlag;

    /** 最后登录IP */
    @Excel(name = "最后登录IP", type = Type.EXPORT)
    private String loginIp;

    /** 最后登录时间 */
    @Excel(name = "最后登录时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss", type = Type.EXPORT)
    private Date loginDate;

    /** 密码最后更新时间 */
    private Date pwdUpdateDate;

    /** 部门对象 */
    @Excels({
        @Excel(name = "部门名称", targetAttr = "deptName", type = Type.EXPORT),
        @Excel(name = "部门负责人", targetAttr = "leader", type = Type.EXPORT)
    })
    private SysDept dept;

    /** 角色对象 */
    private List<SysRole> roles;

    /** 角色组 */
    private Long[] roleIds;

    /** 岗位组 */
    private Long[] postIds;

    /** 角色ID */
    private Long roleId;

    /** 推荐人ID */
    @Excel(name = "推荐人ID", type = Type.EXPORT)
    private Long referrerId;

    /** 会员等级（0普通用户 1普通会员 2金牌会员） */
    @Excel(name = "会员等级", readConverterExp = "0=普通用户,1=普通会员,2=金牌会员")
    private Integer memberLevel;

    /** 团队级别（0无 1经理 2总监 3合伙人） */
    @Excel(name = "团队级别", readConverterExp = "0=无,1=经理,2=总监,3=合伙人")
    private Integer teamLevel;

    /** 账户余额 */
    @Excel(name = "账户余额")
    private java.math.BigDecimal balance;

    /** 团队总业绩 */
    @Excel(name = "团队总业绩")
    private java.math.BigDecimal totalPerformance;

    /** 直推金牌会员数量 */
    @Excel(name = "直推金牌会员数量")
    private Integer directGoldMembers;

    /** 推荐人姓名 */
    @Excel(name = "推荐人姓名", type = Type.EXPORT)
    private String referrerName;

    /** 代理级别（0无代理 1省级代理 2市级代理） - 从agent_setting表获取 */
    @Excel(name = "代理级别", readConverterExp = "0=无代理,1=省级代理,2=市级代理")
    private Integer agentLevel;

    /** 代理省份 - 从agent_setting表获取 */
    private String agentProvince;

    /** 代理城市 - 从agent_setting表获取 */
    private String agentCity;

    public SysUser()
    {

    }

    public SysUser(Long userId)
    {
        this.userId = userId;
    }

    public Long getUserId()
    {
        return userId;
    }

    public void setUserId(Long userId)
    {
        this.userId = userId;
    }

    public boolean isAdmin()
    {
        return isAdmin(this.userId);
    }

    public static boolean isAdmin(Long userId)
    {
        return userId != null && 1L == userId;
    }

    public Long getDeptId()
    {
        return deptId;
    }

    public void setDeptId(Long deptId)
    {
        this.deptId = deptId;
    }

    @Xss(message = "用户昵称不能包含脚本字符")
    @Size(min = 0, max = 30, message = "用户昵称长度不能超过30个字符")
    public String getNickName()
    {
        return nickName;
    }

    public void setNickName(String nickName)
    {
        this.nickName = nickName;
    }

    @Xss(message = "用户账号不能包含脚本字符")
    @NotBlank(message = "用户账号不能为空")
    @Size(min = 0, max = 30, message = "用户账号长度不能超过30个字符")
    public String getUserName()
    {
        return userName;
    }

    public void setUserName(String userName)
    {
        this.userName = userName;
    }

    @Email(message = "邮箱格式不正确")
    @Size(min = 0, max = 50, message = "邮箱长度不能超过50个字符")
    public String getEmail()
    {
        return email;
    }

    public void setEmail(String email)
    {
        this.email = email;
    }

    @Size(min = 0, max = 11, message = "手机号码长度不能超过11个字符")
    public String getPhonenumber()
    {
        return phonenumber;
    }

    public void setPhonenumber(String phonenumber)
    {
        this.phonenumber = phonenumber;
    }

    public String getSex()
    {
        return sex;
    }

    public void setSex(String sex)
    {
        this.sex = sex;
    }

    public String getAvatar()
    {
        return avatar;
    }

    public void setAvatar(String avatar)
    {
        this.avatar = avatar;
    }

    public String getPassword()
    {
        return password;
    }

    public void setPassword(String password)
    {
        this.password = password;
    }

    public String getStatus()
    {
        return status;
    }

    public void setStatus(String status)
    {
        this.status = status;
    }

    public String getDelFlag()
    {
        return delFlag;
    }

    public void setDelFlag(String delFlag)
    {
        this.delFlag = delFlag;
    }

    public String getLoginIp()
    {
        return loginIp;
    }

    public void setLoginIp(String loginIp)
    {
        this.loginIp = loginIp;
    }

    public Date getLoginDate()
    {
        return loginDate;
    }

    public void setLoginDate(Date loginDate)
    {
        this.loginDate = loginDate;
    }

    public Date getPwdUpdateDate()
    {
        return pwdUpdateDate;
    }

    public void setPwdUpdateDate(Date pwdUpdateDate)
    {
        this.pwdUpdateDate = pwdUpdateDate;
    }

    public SysDept getDept()
    {
        return dept;
    }

    public void setDept(SysDept dept)
    {
        this.dept = dept;
    }

    public List<SysRole> getRoles()
    {
        return roles;
    }

    public void setRoles(List<SysRole> roles)
    {
        this.roles = roles;
    }

    public Long[] getRoleIds()
    {
        return roleIds;
    }

    public void setRoleIds(Long[] roleIds)
    {
        this.roleIds = roleIds;
    }

    public Long[] getPostIds()
    {
        return postIds;
    }

    public void setPostIds(Long[] postIds)
    {
        this.postIds = postIds;
    }

    public Long getRoleId()
    {
        return roleId;
    }

    public void setRoleId(Long roleId)
    {
        this.roleId = roleId;
    }

    public Long getReferrerId()
    {
        return referrerId;
    }

    public void setReferrerId(Long referrerId)
    {
        this.referrerId = referrerId;
    }

    public Integer getMemberLevel()
    {
        return memberLevel;
    }

    public void setMemberLevel(Integer memberLevel)
    {
        this.memberLevel = memberLevel;
    }

    public Integer getTeamLevel()
    {
        return teamLevel;
    }

    public void setTeamLevel(Integer teamLevel)
    {
        this.teamLevel = teamLevel;
    }

    public java.math.BigDecimal getBalance()
    {
        return balance;
    }

    public void setBalance(java.math.BigDecimal balance)
    {
        this.balance = balance;
    }

    public java.math.BigDecimal getTotalPerformance()
    {
        return totalPerformance;
    }

    public void setTotalPerformance(java.math.BigDecimal totalPerformance)
    {
        this.totalPerformance = totalPerformance;
    }

    public Integer getDirectGoldMembers()
    {
        return directGoldMembers;
    }

    public void setDirectGoldMembers(Integer directGoldMembers)
    {
        this.directGoldMembers = directGoldMembers;
    }

    public String getReferrerName()
    {
        return referrerName;
    }

    public void setReferrerName(String referrerName)
    {
        this.referrerName = referrerName;
    }

    public Integer getAgentLevel()
    {
        return agentLevel;
    }

    public void setAgentLevel(Integer agentLevel)
    {
        this.agentLevel = agentLevel;
    }

    public String getAgentProvince()
    {
        return agentProvince;
    }

    public void setAgentProvince(String agentProvince)
    {
        this.agentProvince = agentProvince;
    }

    public String getAgentCity()
    {
        return agentCity;
    }

    public void setAgentCity(String agentCity)
    {
        this.agentCity = agentCity;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("userId", getUserId())
            .append("deptId", getDeptId())
            .append("userName", getUserName())
            .append("nickName", getNickName())
            .append("email", getEmail())
            .append("phonenumber", getPhonenumber())
            .append("sex", getSex())
            .append("avatar", getAvatar())
            .append("password", getPassword())
            .append("status", getStatus())
            .append("delFlag", getDelFlag())
            .append("loginIp", getLoginIp())
            .append("loginDate", getLoginDate())
            .append("pwdUpdateDate", getPwdUpdateDate())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .append("remark", getRemark())
            .append("dept", getDept())
            .toString();
    }
}
