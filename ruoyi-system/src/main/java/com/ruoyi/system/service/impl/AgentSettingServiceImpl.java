package com.ruoyi.system.service.impl;

import java.util.Date;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.ruoyi.system.mapper.AgentSettingMapper;
import com.ruoyi.system.domain.AgentSetting;
import com.ruoyi.system.service.IAgentSettingService;

/**
 * 代理设置Service业务层处理
 * 
 * @author ruoyi
 * @date 2025-01-28
 */
@Service
public class AgentSettingServiceImpl implements IAgentSettingService 
{
    @Autowired
    private AgentSettingMapper agentSettingMapper;

    /**
     * 查询代理设置
     * 
     * @param settingId 代理设置主键
     * @return 代理设置
     */
    @Override
    public AgentSetting selectAgentSettingBySettingId(Long settingId)
    {
        return agentSettingMapper.selectAgentSettingBySettingId(settingId);
    }

    /**
     * 查询代理设置列表
     * 
     * @param agentSetting 代理设置
     * @return 代理设置
     */
    @Override
    public List<AgentSetting> selectAgentSettingList(AgentSetting agentSetting)
    {
        return agentSettingMapper.selectAgentSettingList(agentSetting);
    }

    /**
     * 新增代理设置
     * 
     * @param agentSetting 代理设置
     * @return 结果
     */
    @Override
    public int insertAgentSetting(AgentSetting agentSetting)
    {
        agentSetting.setCreateTime(new Date());
        return agentSettingMapper.insertAgentSetting(agentSetting);
    }

    /**
     * 修改代理设置
     * 
     * @param agentSetting 代理设置
     * @return 结果
     */
    @Override
    public int updateAgentSetting(AgentSetting agentSetting)
    {
        return agentSettingMapper.updateAgentSetting(agentSetting);
    }

    /**
     * 批量删除代理设置
     * 
     * @param settingIds 需要删除的代理设置主键
     * @return 结果
     */
    @Override
    public int deleteAgentSettingBySettingIds(Long[] settingIds)
    {
        return agentSettingMapper.deleteAgentSettingBySettingIds(settingIds);
    }

    /**
     * 删除代理设置信息
     * 
     * @param settingId 代理设置主键
     * @return 结果
     */
    @Override
    public int deleteAgentSettingBySettingId(Long settingId)
    {
        return agentSettingMapper.deleteAgentSettingBySettingId(settingId);
    }

    /**
     * 设置用户为代理
     *
     * @param userId 用户ID
     * @param agentType 代理类型（1省级 2市级）
     * @param province 省份
     * @param city 城市
     * @return 结果
     */
    @Override
    @Transactional
    public boolean setUserAgent(Long userId, Integer agentType, String province, String city)
    {
        try {
            // 先删除用户现有的代理设置
            agentSettingMapper.deleteAgentSettingByUserId(userId);

            // 创建新的代理设置
            AgentSetting agentSetting = new AgentSetting();
            agentSetting.setUserId(userId);
            agentSetting.setAgentType(agentType);
            agentSetting.setProvince(province);
            agentSetting.setCity(city);
            agentSetting.setStatus("0");
            agentSetting.setCreateTime(new Date());

            // 插入代理设置
            return agentSettingMapper.insertAgentSetting(agentSetting) > 0;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 取消用户代理设置
     *
     * @param userId 用户ID
     * @return 结果
     */
    @Override
    @Transactional
    public boolean cancelUserAgent(Long userId)
    {
        try {
            // 删除用户的代理设置记录
            return agentSettingMapper.deleteAgentSettingByUserId(userId) >= 0;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 根据省份和城市获取代理列表
     *
     * @param province 省份
     * @param city 城市
     * @return 代理列表
     */
    @Override
    public List<AgentSetting> getAgentsByLocation(String province, String city)
    {
        return agentSettingMapper.selectAgentsByProvinceAndCity(province, city);
    }

    /**
     * 根据用户ID获取代理设置
     *
     * @param userId 用户ID
     * @return 代理设置
     */
    @Override
    public AgentSetting getAgentSettingByUserId(Long userId)
    {
        return agentSettingMapper.selectAgentSettingByUserId(userId);
    }
}
