package com.ruoyi.system.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 分销配置对象 distribution_config
 * 
 * @author ruoyi
 * @date 2025-01-28
 */
public class DistributionConfig extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 配置ID */
    private Long configId;

    /** 配置键 */
    @Excel(name = "配置键")
    private String configKey;

    /** 配置值 */
    @Excel(name = "配置值")
    private String configValue;

    /** 配置描述 */
    @Excel(name = "配置描述")
    private String configDesc;

    public void setConfigId(Long configId) 
    {
        this.configId = configId;
    }

    public Long getConfigId() 
    {
        return configId;
    }
    public void setConfigKey(String configKey) 
    {
        this.configKey = configKey;
    }

    public String getConfigKey() 
    {
        return configKey;
    }
    public void setConfigValue(String configValue) 
    {
        this.configValue = configValue;
    }

    public String getConfigValue() 
    {
        return configValue;
    }
    public void setConfigDesc(String configDesc) 
    {
        this.configDesc = configDesc;
    }

    public String getConfigDesc() 
    {
        return configDesc;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("configId", getConfigId())
            .append("configKey", getConfigKey())
            .append("configValue", getConfigValue())
            .append("configDesc", getConfigDesc())
            .append("createTime", getCreateTime())
            .append("updateTime", getUpdateTime())
            .toString();
    }
}
