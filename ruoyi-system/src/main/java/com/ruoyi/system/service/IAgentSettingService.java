package com.ruoyi.system.service;

import java.util.List;
import com.ruoyi.system.domain.AgentSetting;

/**
 * 代理设置Service接口
 * 
 * @author ruoyi
 * @date 2025-01-28
 */
public interface IAgentSettingService 
{
    /**
     * 查询代理设置
     * 
     * @param settingId 代理设置主键
     * @return 代理设置
     */
    public AgentSetting selectAgentSettingBySettingId(Long settingId);

    /**
     * 查询代理设置列表
     * 
     * @param agentSetting 代理设置
     * @return 代理设置集合
     */
    public List<AgentSetting> selectAgentSettingList(AgentSetting agentSetting);

    /**
     * 新增代理设置
     * 
     * @param agentSetting 代理设置
     * @return 结果
     */
    public int insertAgentSetting(AgentSetting agentSetting);

    /**
     * 修改代理设置
     * 
     * @param agentSetting 代理设置
     * @return 结果
     */
    public int updateAgentSetting(AgentSetting agentSetting);

    /**
     * 批量删除代理设置
     * 
     * @param settingIds 需要删除的代理设置主键集合
     * @return 结果
     */
    public int deleteAgentSettingBySettingIds(Long[] settingIds);

    /**
     * 删除代理设置信息
     * 
     * @param settingId 代理设置主键
     * @return 结果
     */
    public int deleteAgentSettingBySettingId(Long settingId);

    /**
     * 设置用户为代理
     * 
     * @param userId 用户ID
     * @param agentType 代理类型（1省级 2市级）
     * @param province 省份
     * @param city 城市
     * @return 结果
     */
    public boolean setUserAgent(Long userId, Integer agentType, String province, String city);

    /**
     * 取消用户代理设置
     * 
     * @param userId 用户ID
     * @return 结果
     */
    public boolean cancelUserAgent(Long userId);

    /**
     * 根据省份和城市获取代理列表
     *
     * @param province 省份
     * @param city 城市
     * @return 代理列表
     */
    public List<AgentSetting> getAgentsByLocation(String province, String city);

    /**
     * 根据用户ID获取代理设置
     *
     * @param userId 用户ID
     * @return 代理设置
     */
    public AgentSetting getAgentSettingByUserId(Long userId);
}
