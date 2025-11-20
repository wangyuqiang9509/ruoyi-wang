import request from '@/utils/request'

// 查询分销配置列表
export function listDistributionConfig(query) {
  return request({
    url: '/system/distributionConfig/list',
    method: 'get',
    params: query
  })
}

// 查询分销配置详细
export function getDistributionConfig(configId) {
  return request({
    url: '/system/distributionConfig/' + configId,
    method: 'get'
  })
}

// 新增分销配置
export function addDistributionConfig(data) {
  return request({
    url: '/system/distributionConfig',
    method: 'post',
    data: data
  })
}

// 修改分销配置
export function updateDistributionConfig(data) {
  return request({
    url: '/system/distributionConfig',
    method: 'put',
    data: data
  })
}

// 删除分销配置
export function delDistributionConfig(configId) {
  return request({
    url: '/system/distributionConfig/' + configId,
    method: 'delete'
  })
}
