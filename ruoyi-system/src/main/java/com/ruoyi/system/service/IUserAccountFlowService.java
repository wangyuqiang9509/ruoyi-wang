package com.ruoyi.system.service;

import java.util.List;
import com.ruoyi.system.domain.UserAccountFlow;

/**
 * 用户账户流水Service接口
 * 
 * @author ruoyi
 * @date 2025-01-28
 */
public interface IUserAccountFlowService 
{
    /**
     * 查询用户账户流水
     * 
     * @param flowId 用户账户流水主键
     * @return 用户账户流水
     */
    public UserAccountFlow selectUserAccountFlowByFlowId(Long flowId);

    /**
     * 查询用户账户流水列表
     * 
     * @param userAccountFlow 用户账户流水
     * @return 用户账户流水集合
     */
    public List<UserAccountFlow> selectUserAccountFlowList(UserAccountFlow userAccountFlow);

    /**
     * 新增用户账户流水
     * 
     * @param userAccountFlow 用户账户流水
     * @return 结果
     */
    public int insertUserAccountFlow(UserAccountFlow userAccountFlow);

    /**
     * 修改用户账户流水
     * 
     * @param userAccountFlow 用户账户流水
     * @return 结果
     */
    public int updateUserAccountFlow(UserAccountFlow userAccountFlow);

    /**
     * 批量删除用户账户流水
     * 
     * @param flowIds 需要删除的用户账户流水主键集合
     * @return 结果
     */
    public int deleteUserAccountFlowByFlowIds(Long[] flowIds);

    /**
     * 删除用户账户流水信息
     * 
     * @param flowId 用户账户流水主键
     * @return 结果
     */
    public int deleteUserAccountFlowByFlowId(Long flowId);
}
