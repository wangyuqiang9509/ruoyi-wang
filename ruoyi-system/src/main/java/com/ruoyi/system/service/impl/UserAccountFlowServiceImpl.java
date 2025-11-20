package com.ruoyi.system.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.system.mapper.UserAccountFlowMapper;
import com.ruoyi.system.domain.UserAccountFlow;
import com.ruoyi.system.service.IUserAccountFlowService;

/**
 * 用户账户流水Service业务层处理
 * 
 * @author ruoyi
 * @date 2025-01-28
 */
@Service
public class UserAccountFlowServiceImpl implements IUserAccountFlowService 
{
    @Autowired
    private UserAccountFlowMapper userAccountFlowMapper;

    /**
     * 查询用户账户流水
     * 
     * @param flowId 用户账户流水主键
     * @return 用户账户流水
     */
    @Override
    public UserAccountFlow selectUserAccountFlowByFlowId(Long flowId)
    {
        return userAccountFlowMapper.selectUserAccountFlowByFlowId(flowId);
    }

    /**
     * 查询用户账户流水列表
     * 
     * @param userAccountFlow 用户账户流水
     * @return 用户账户流水
     */
    @Override
    public List<UserAccountFlow> selectUserAccountFlowList(UserAccountFlow userAccountFlow)
    {
        return userAccountFlowMapper.selectUserAccountFlowList(userAccountFlow);
    }

    /**
     * 新增用户账户流水
     * 
     * @param userAccountFlow 用户账户流水
     * @return 结果
     */
    @Override
    public int insertUserAccountFlow(UserAccountFlow userAccountFlow)
    {
        return userAccountFlowMapper.insertUserAccountFlow(userAccountFlow);
    }

    /**
     * 修改用户账户流水
     * 
     * @param userAccountFlow 用户账户流水
     * @return 结果
     */
    @Override
    public int updateUserAccountFlow(UserAccountFlow userAccountFlow)
    {
        return userAccountFlowMapper.updateUserAccountFlow(userAccountFlow);
    }

    /**
     * 批量删除用户账户流水
     * 
     * @param flowIds 需要删除的用户账户流水主键
     * @return 结果
     */
    @Override
    public int deleteUserAccountFlowByFlowIds(Long[] flowIds)
    {
        return userAccountFlowMapper.deleteUserAccountFlowByFlowIds(flowIds);
    }

    /**
     * 删除用户账户流水信息
     * 
     * @param flowId 用户账户流水主键
     * @return 结果
     */
    @Override
    public int deleteUserAccountFlowByFlowId(Long flowId)
    {
        return userAccountFlowMapper.deleteUserAccountFlowByFlowId(flowId);
    }
}
