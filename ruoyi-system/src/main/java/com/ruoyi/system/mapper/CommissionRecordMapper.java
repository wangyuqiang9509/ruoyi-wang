package com.ruoyi.system.mapper;

import java.util.List;
import com.ruoyi.system.domain.CommissionRecord;

/**
 * 分润记录Mapper接口
 * 
 * @author ruoyi
 * @date 2025-01-28
 */
public interface CommissionRecordMapper 
{
    /**
     * 查询分润记录
     * 
     * @param recordId 分润记录主键
     * @return 分润记录
     */
    public CommissionRecord selectCommissionRecordByRecordId(Long recordId);

    /**
     * 查询分润记录列表
     * 
     * @param commissionRecord 分润记录
     * @return 分润记录集合
     */
    public List<CommissionRecord> selectCommissionRecordList(CommissionRecord commissionRecord);

    /**
     * 新增分润记录
     * 
     * @param commissionRecord 分润记录
     * @return 结果
     */
    public int insertCommissionRecord(CommissionRecord commissionRecord);

    /**
     * 修改分润记录
     * 
     * @param commissionRecord 分润记录
     * @return 结果
     */
    public int updateCommissionRecord(CommissionRecord commissionRecord);

    /**
     * 删除分润记录
     * 
     * @param recordId 分润记录主键
     * @return 结果
     */
    public int deleteCommissionRecordByRecordId(Long recordId);

    /**
     * 批量删除分润记录
     * 
     * @param recordIds 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteCommissionRecordByRecordIds(Long[] recordIds);
}
