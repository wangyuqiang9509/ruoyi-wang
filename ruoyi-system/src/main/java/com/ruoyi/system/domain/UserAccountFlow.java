package com.ruoyi.system.domain;

import java.math.BigDecimal;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 用户账户流水对象 user_account_flow
 * 
 * @author ruoyi
 * @date 2025-01-28
 */
public class UserAccountFlow extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 流水ID */
    private Long flowId;

    /** 用户ID */
    @Excel(name = "用户ID")
    private Long userId;

    /** 流水类型（1收入 2支出） */
    @Excel(name = "流水类型", readConverterExp = "1=收入,2=支出")
    private Integer flowType;

    /** 金额 */
    @Excel(name = "金额")
    private BigDecimal amount;

    /** 变动前余额 */
    @Excel(name = "变动前余额")
    private BigDecimal balanceBefore;

    /** 变动后余额 */
    @Excel(name = "变动后余额")
    private BigDecimal balanceAfter;

    /** 业务类型 */
    @Excel(name = "业务类型")
    private String businessType;

    /** 关联业务ID */
    @Excel(name = "关联业务ID")
    private Long businessId;

    /** 用户名称 */
    @Excel(name = "用户名称")
    private String userName;

    /** 用户昵称 */
    @Excel(name = "用户昵称")
    private String nickName;

    public void setFlowId(Long flowId) 
    {
        this.flowId = flowId;
    }

    public Long getFlowId() 
    {
        return flowId;
    }
    public void setUserId(Long userId) 
    {
        this.userId = userId;
    }

    public Long getUserId() 
    {
        return userId;
    }
    public void setFlowType(Integer flowType) 
    {
        this.flowType = flowType;
    }

    public Integer getFlowType() 
    {
        return flowType;
    }
    public void setAmount(BigDecimal amount) 
    {
        this.amount = amount;
    }

    public BigDecimal getAmount() 
    {
        return amount;
    }
    public void setBalanceBefore(BigDecimal balanceBefore) 
    {
        this.balanceBefore = balanceBefore;
    }

    public BigDecimal getBalanceBefore() 
    {
        return balanceBefore;
    }
    public void setBalanceAfter(BigDecimal balanceAfter) 
    {
        this.balanceAfter = balanceAfter;
    }

    public BigDecimal getBalanceAfter() 
    {
        return balanceAfter;
    }
    public void setBusinessType(String businessType) 
    {
        this.businessType = businessType;
    }

    public String getBusinessType() 
    {
        return businessType;
    }
    public void setBusinessId(Long businessId) 
    {
        this.businessId = businessId;
    }

    public Long getBusinessId() 
    {
        return businessId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("flowId", getFlowId())
            .append("userId", getUserId())
            .append("flowType", getFlowType())
            .append("amount", getAmount())
            .append("balanceBefore", getBalanceBefore())
            .append("balanceAfter", getBalanceAfter())
            .append("businessType", getBusinessType())
            .append("businessId", getBusinessId())
            .append("remark", getRemark())
            .append("createTime", getCreateTime())
            .toString();
    }
}
