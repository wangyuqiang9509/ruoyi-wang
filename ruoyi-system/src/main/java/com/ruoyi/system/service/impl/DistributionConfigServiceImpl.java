package com.ruoyi.system.service.impl;

import java.util.List;
import com.ruoyi.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.system.mapper.DistributionConfigMapper;
import com.ruoyi.system.domain.DistributionConfig;
import com.ruoyi.system.service.IDistributionConfigService;

/**
 * 分销配置Service业务层处理
 * 
 * @author ruoyi
 * @date 2025-01-28
 */
@Service
public class DistributionConfigServiceImpl implements IDistributionConfigService 
{
    @Autowired
    private DistributionConfigMapper distributionConfigMapper;

    /**
     * 查询分销配置
     * 
     * @param configId 分销配置主键
     * @return 分销配置
     */
    @Override
    public DistributionConfig selectDistributionConfigByConfigId(Long configId)
    {
        return distributionConfigMapper.selectDistributionConfigByConfigId(configId);
    }

    /**
     * 根据配置键查询分销配置
     * 
     * @param configKey 配置键
     * @return 分销配置
     */
    @Override
    public DistributionConfig selectDistributionConfigByKey(String configKey)
    {
        return distributionConfigMapper.selectDistributionConfigByKey(configKey);
    }

    /**
     * 查询分销配置列表
     * 
     * @param distributionConfig 分销配置
     * @return 分销配置
     */
    @Override
    public List<DistributionConfig> selectDistributionConfigList(DistributionConfig distributionConfig)
    {
        return distributionConfigMapper.selectDistributionConfigList(distributionConfig);
    }

    /**
     * 新增分销配置
     * 
     * @param distributionConfig 分销配置
     * @return 结果
     */
    @Override
    public int insertDistributionConfig(DistributionConfig distributionConfig)
    {
        distributionConfig.setCreateTime(DateUtils.getNowDate());
        return distributionConfigMapper.insertDistributionConfig(distributionConfig);
    }

    /**
     * 修改分销配置
     * 
     * @param distributionConfig 分销配置
     * @return 结果
     */
    @Override
    public int updateDistributionConfig(DistributionConfig distributionConfig)
    {
        distributionConfig.setUpdateTime(DateUtils.getNowDate());
        return distributionConfigMapper.updateDistributionConfig(distributionConfig);
    }

    /**
     * 批量删除分销配置
     * 
     * @param configIds 需要删除的分销配置主键
     * @return 结果
     */
    @Override
    public int deleteDistributionConfigByConfigIds(Long[] configIds)
    {
        return distributionConfigMapper.deleteDistributionConfigByConfigIds(configIds);
    }

    /**
     * 删除分销配置信息
     * 
     * @param configId 分销配置主键
     * @return 结果
     */
    @Override
    public int deleteDistributionConfigByConfigId(Long configId)
    {
        return distributionConfigMapper.deleteDistributionConfigByConfigId(configId);
    }
}
