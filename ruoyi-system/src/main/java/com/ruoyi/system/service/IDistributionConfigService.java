package com.ruoyi.system.service;

import java.util.List;
import com.ruoyi.system.domain.DistributionConfig;

/**
 * 分销配置Service接口
 * 
 * @author ruoyi
 * @date 2025-01-28
 */
public interface IDistributionConfigService 
{
    /**
     * 查询分销配置
     * 
     * @param configId 分销配置主键
     * @return 分销配置
     */
    public DistributionConfig selectDistributionConfigByConfigId(Long configId);

    /**
     * 根据配置键查询分销配置
     * 
     * @param configKey 配置键
     * @return 分销配置
     */
    public DistributionConfig selectDistributionConfigByKey(String configKey);

    /**
     * 查询分销配置列表
     * 
     * @param distributionConfig 分销配置
     * @return 分销配置集合
     */
    public List<DistributionConfig> selectDistributionConfigList(DistributionConfig distributionConfig);

    /**
     * 新增分销配置
     * 
     * @param distributionConfig 分销配置
     * @return 结果
     */
    public int insertDistributionConfig(DistributionConfig distributionConfig);

    /**
     * 修改分销配置
     * 
     * @param distributionConfig 分销配置
     * @return 结果
     */
    public int updateDistributionConfig(DistributionConfig distributionConfig);

    /**
     * 批量删除分销配置
     * 
     * @param configIds 需要删除的分销配置主键集合
     * @return 结果
     */
    public int deleteDistributionConfigByConfigIds(Long[] configIds);

    /**
     * 删除分销配置信息
     * 
     * @param configId 分销配置主键
     * @return 结果
     */
    public int deleteDistributionConfigByConfigId(Long configId);
}
