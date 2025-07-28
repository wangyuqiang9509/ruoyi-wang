package com.ruoyi.system.mapper;

import java.util.List;
import com.ruoyi.system.domain.AgentSetting;

/**
 * 代理设置Mapper接口
 * 
 * @author ruoyi
 * @date 2025-01-28
 */
public interface AgentSettingMapper 
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
     * 根据省份和城市查询代理列表
     * 
     * @param province 省份
     * @param city 城市
     * @return 代理设置集合
     */
    public List<AgentSetting> selectAgentsByProvinceAndCity(String province, String city);

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
     * 删除代理设置
     * 
     * @param settingId 代理设置主键
     * @return 结果
     */
    public int deleteAgentSettingBySettingId(Long settingId);

    /**
     * 批量删除代理设置
     * 
     * @param settingIds 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteAgentSettingBySettingIds(Long[] settingIds);

    /**
     * 删除用户的代理设置
     * 
     * @param userId 用户ID
     * @return 结果
     */
    public int deleteAgentSettingByUserId(Long userId);
}
