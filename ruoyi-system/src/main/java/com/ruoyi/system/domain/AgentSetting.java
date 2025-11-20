package com.ruoyi.system.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 代理设置对象 agent_setting
 * 
 * @author ruoyi
 * @date 2025-01-28
 */
public class AgentSetting extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 设置ID */
    private Long settingId;

    /** 用户ID */
    @Excel(name = "用户ID")
    private Long userId;

    /** 代理类型（1省级 2市级） */
    @Excel(name = "代理类型", readConverterExp = "1=省级,2=市级")
    private Integer agentType;

    /** 省份 */
    @Excel(name = "省份")
    private String province;

    /** 城市 */
    @Excel(name = "城市")
    private String city;

    /** 状态（0正常 1停用） */
    @Excel(name = "状态", readConverterExp = "0=正常,1=停用")
    private String status;

    /** 用户名称 */
    @Excel(name = "用户名称")
    private String userName;

    /** 用户昵称 */
    @Excel(name = "用户昵称")
    private String nickName;

    public void setSettingId(Long settingId) 
    {
        this.settingId = settingId;
    }

    public Long getSettingId() 
    {
        return settingId;
    }
    public void setUserId(Long userId) 
    {
        this.userId = userId;
    }

    public Long getUserId() 
    {
        return userId;
    }
    public void setAgentType(Integer agentType) 
    {
        this.agentType = agentType;
    }

    public Integer getAgentType() 
    {
        return agentType;
    }
    public void setProvince(String province) 
    {
        this.province = province;
    }

    public String getProvince() 
    {
        return province;
    }
    public void setCity(String city) 
    {
        this.city = city;
    }

    public String getCity() 
    {
        return city;
    }
    public void setStatus(String status) 
    {
        this.status = status;
    }

    public String getStatus() 
    {
        return status;
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
            .append("settingId", getSettingId())
            .append("userId", getUserId())
            .append("agentType", getAgentType())
            .append("province", getProvince())
            .append("city", getCity())
            .append("status", getStatus())
            .append("createTime", getCreateTime())
            .toString();
    }
}
