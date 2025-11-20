package com.ruoyi.system.domain;

import java.math.BigDecimal;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 分润记录对象 commission_record
 * 
 * @author ruoyi
 * @date 2025-01-28
 */
public class CommissionRecord extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 记录ID */
    private Long recordId;

    /** 订单ID */
    @Excel(name = "订单ID")
    private Long orderId;

    /** 获得分润的用户ID */
    @Excel(name = "用户ID")
    private Long userId;

    /** 分润类型 */
    @Excel(name = "分润类型")
    private String commissionType;

    /** 分润比例 */
    @Excel(name = "分润比例")
    private BigDecimal commissionRate;

    /** 分润金额 */
    @Excel(name = "分润金额")
    private BigDecimal commissionAmount;

    /** 订单金额 */
    @Excel(name = "订单金额")
    private BigDecimal orderAmount;

    /** 订单号 */
    @Excel(name = "订单号")
    private String orderNo;

    /** 用户名称 */
    @Excel(name = "用户名称")
    private String userName;

    /** 用户昵称 */
    @Excel(name = "用户昵称")
    private String nickName;

    public void setRecordId(Long recordId) 
    {
        this.recordId = recordId;
    }

    public Long getRecordId() 
    {
        return recordId;
    }
    public void setOrderId(Long orderId) 
    {
        this.orderId = orderId;
    }

    public Long getOrderId() 
    {
        return orderId;
    }
    public void setUserId(Long userId) 
    {
        this.userId = userId;
    }

    public Long getUserId() 
    {
        return userId;
    }
    public void setCommissionType(String commissionType) 
    {
        this.commissionType = commissionType;
    }

    public String getCommissionType() 
    {
        return commissionType;
    }
    public void setCommissionRate(BigDecimal commissionRate) 
    {
        this.commissionRate = commissionRate;
    }

    public BigDecimal getCommissionRate() 
    {
        return commissionRate;
    }
    public void setCommissionAmount(BigDecimal commissionAmount) 
    {
        this.commissionAmount = commissionAmount;
    }

    public BigDecimal getCommissionAmount() 
    {
        return commissionAmount;
    }
    public void setOrderAmount(BigDecimal orderAmount) 
    {
        this.orderAmount = orderAmount;
    }

    public BigDecimal getOrderAmount() 
    {
        return orderAmount;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
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
            .append("recordId", getRecordId())
            .append("orderId", getOrderId())
            .append("userId", getUserId())
            .append("commissionType", getCommissionType())
            .append("commissionRate", getCommissionRate())
            .append("commissionAmount", getCommissionAmount())
            .append("orderAmount", getOrderAmount())
            .append("createTime", getCreateTime())
            .toString();
    }
}
