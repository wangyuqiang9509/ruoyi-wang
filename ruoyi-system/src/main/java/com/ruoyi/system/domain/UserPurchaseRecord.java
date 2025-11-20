package com.ruoyi.system.domain;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 用户购买记录对象 user_purchase_record
 * 
 * @author ruoyi
 * @date 2025-01-28
 */
public class UserPurchaseRecord extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 记录ID */
    private Long recordId;

    /** 用户ID */
    @Excel(name = "用户ID")
    private Long userId;

    /** 商品ID */
    @Excel(name = "商品ID")
    private Long productId;

    /** 购买次数 */
    @Excel(name = "购买次数")
    private Integer purchaseCount;

    /** 首次购买时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "首次购买时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date firstPurchaseTime;

    /** 最后购买时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "最后购买时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date lastPurchaseTime;

    public void setRecordId(Long recordId) 
    {
        this.recordId = recordId;
    }

    public Long getRecordId() 
    {
        return recordId;
    }
    public void setUserId(Long userId) 
    {
        this.userId = userId;
    }

    public Long getUserId() 
    {
        return userId;
    }
    public void setProductId(Long productId) 
    {
        this.productId = productId;
    }

    public Long getProductId() 
    {
        return productId;
    }
    public void setPurchaseCount(Integer purchaseCount) 
    {
        this.purchaseCount = purchaseCount;
    }

    public Integer getPurchaseCount() 
    {
        return purchaseCount;
    }
    public void setFirstPurchaseTime(Date firstPurchaseTime) 
    {
        this.firstPurchaseTime = firstPurchaseTime;
    }

    public Date getFirstPurchaseTime() 
    {
        return firstPurchaseTime;
    }
    public void setLastPurchaseTime(Date lastPurchaseTime) 
    {
        this.lastPurchaseTime = lastPurchaseTime;
    }

    public Date getLastPurchaseTime() 
    {
        return lastPurchaseTime;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("recordId", getRecordId())
            .append("userId", getUserId())
            .append("productId", getProductId())
            .append("purchaseCount", getPurchaseCount())
            .append("firstPurchaseTime", getFirstPurchaseTime())
            .append("lastPurchaseTime", getLastPurchaseTime())
            .toString();
    }
}
