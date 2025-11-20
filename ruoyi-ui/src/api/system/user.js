import request from '@/utils/request'
import { parseStrEmpty } from "@/utils/ruoyi";

// 查询用户列表
export function listUser(query) {
  return request({
    url: '/system/user/list',
    method: 'get',
    params: query
  })
}

// 查询用户详细
export function getUser(userId) {
  return request({
    url: '/system/user/' + parseStrEmpty(userId),
    method: 'get'
  })
}

// 新增用户
export function addUser(data) {
  return request({
    url: '/system/user',
    method: 'post',
    data: data
  })
}

// 修改用户
export function updateUser(data) {
  return request({
    url: '/system/user',
    method: 'put',
    data: data
  })
}

// 删除用户
export function delUser(userId) {
  return request({
    url: '/system/user/' + userId,
    method: 'delete'
  })
}

// 用户密码重置
export function resetUserPwd(userId, password) {
  const data = {
    userId,
    password
  }
  return request({
    url: '/system/user/resetPwd',
    method: 'put',
    data: data
  })
}

// 用户状态修改
export function changeUserStatus(userId, status) {
  const data = {
    userId,
    status
  }
  return request({
    url: '/system/user/changeStatus',
    method: 'put',
    data: data
  })
}

// 查询用户个人信息
export function getUserProfile() {
  return request({
    url: '/system/user/profile',
    method: 'get'
  })
}

// 修改用户个人信息
export function updateUserProfile(data) {
  return request({
    url: '/system/user/profile',
    method: 'put',
    data: data
  })
}

// 用户密码重置
export function updateUserPwd(oldPassword, newPassword) {
  const data = {
    oldPassword,
    newPassword
  }
  return request({
    url: '/system/user/profile/updatePwd',
    method: 'put',
    data: data
  })
}

// 用户头像上传
export function uploadAvatar(data) {
  return request({
    url: '/system/user/profile/avatar',
    method: 'post',
    headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
    data: data
  })
}

// 查询授权角色
export function getAuthRole(userId) {
  return request({
    url: '/system/user/authRole/' + userId,
    method: 'get'
  })
}

// 保存授权角色
export function updateAuthRole(data) {
  return request({
    url: '/system/user/authRole',
    method: 'put',
    params: data
  })
}

// 查询部门下拉树结构
export function deptTreeSelect() {
  return request({
    url: '/system/user/deptTree',
    method: 'get'
  })
}

// 更新用户余额
export function updateUserBalance(data) {
  return request({
    url: '/system/user/balance',
    method: 'post',
    data: data
  })
}

// 获取用户账户流水
export function getUserAccountFlow(userId) {
  return request({
    url: '/system/user/account/flow/' + userId,
    method: 'get'
  })
}

// 绑定推荐人
export function bindReferrer(data) {
  return request({
    url: '/system/user/referrer',
    method: 'post',
    data: data
  })
}

// 设置会员等级
export function updateMemberLevel(data) {
  return request({
    url: '/system/user/member',
    method: 'post',
    data: data
  })
}

// 升级团队级别
export function upgradeTeamLevel(data) {
  return request({
    url: '/system/user/level',
    method: 'post',
    data: data
  })
}

// 设置用户代理
export function setUserAgent(data) {
  return request({
    url: '/system/user/agent',
    method: 'post',
    data: data
  })
}

// 取消用户代理
export function cancelUserAgent(userId) {
  return request({
    url: '/system/user/agent/' + userId,
    method: 'delete'
  })
}

// 获取商品列表
export function getProductList() {
  return request({
    url: '/system/user/products',
    method: 'get'
  })
}

// 获取订单价格预览
export function getOrderPreview(data) {
  return request({
    url: '/system/user/order/preview',
    method: 'post',
    data: data
  })
}

// 模拟下单
export function placeOrder(data) {
  return request({
    url: '/system/user/order',
    method: 'post',
    data: data
  })
}
