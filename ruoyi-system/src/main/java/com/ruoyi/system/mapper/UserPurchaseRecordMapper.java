package com.ruoyi.system.mapper;

import java.util.List;
import com.ruoyi.system.domain.UserPurchaseRecord;

/**
 * 用户购买记录Mapper接口
 * 
 * @author ruoyi
 * @date 2025-01-28
 */
public interface UserPurchaseRecordMapper 
{
    /**
     * 查询用户购买记录
     * 
     * @param recordId 用户购买记录主键
     * @return 用户购买记录
     */
    public UserPurchaseRecord selectUserPurchaseRecordByRecordId(Long recordId);

    /**
     * 根据用户ID和商品ID查询购买记录
     * 
     * @param userId 用户ID
     * @param productId 商品ID
     * @return 用户购买记录
     */
    public UserPurchaseRecord selectUserPurchaseRecordByUserAndProduct(Long userId, Long productId);

    /**
     * 查询用户购买记录列表
     * 
     * @param userPurchaseRecord 用户购买记录
     * @return 用户购买记录集合
     */
    public List<UserPurchaseRecord> selectUserPurchaseRecordList(UserPurchaseRecord userPurchaseRecord);

    /**
     * 新增用户购买记录
     * 
     * @param userPurchaseRecord 用户购买记录
     * @return 结果
     */
    public int insertUserPurchaseRecord(UserPurchaseRecord userPurchaseRecord);

    /**
     * 修改用户购买记录
     * 
     * @param userPurchaseRecord 用户购买记录
     * @return 结果
     */
    public int updateUserPurchaseRecord(UserPurchaseRecord userPurchaseRecord);

    /**
     * 删除用户购买记录
     * 
     * @param recordId 用户购买记录主键
     * @return 结果
     */
    public int deleteUserPurchaseRecordByRecordId(Long recordId);

    /**
     * 批量删除用户购买记录
     * 
     * @param recordIds 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteUserPurchaseRecordByRecordIds(Long[] recordIds);
}
