package com.ruoyi.system.service;

import java.util.List;
import com.ruoyi.system.domain.Product;

/**
 * 商品Service接口
 * 
 * @author ruoyi
 * @date 2025-01-29
 */
public interface IProductService 
{
    /**
     * 查询商品
     * 
     * @param productId 商品主键
     * @return 商品
     */
    public Product selectProductByProductId(Long productId);

    /**
     * 查询商品列表
     * 
     * @param product 商品
     * @return 商品集合
     */
    public List<Product> selectProductList(Product product);

    /**
     * 新增商品
     * 
     * @param product 商品
     * @return 结果
     */
    public int insertProduct(Product product);

    /**
     * 修改商品
     * 
     * @param product 商品
     * @return 结果
     */
    public int updateProduct(Product product);

    /**
     * 批量删除商品
     * 
     * @param productIds 需要删除的商品主键集合
     * @return 结果
     */
    public int deleteProductByProductIds(Long[] productIds);

    /**
     * 删除商品信息
     * 
     * @param productId 商品主键
     * @return 结果
     */
    public int deleteProductByProductId(Long productId);

    /**
     * 校验商品名称是否唯一
     * 
     * @param product 商品信息
     * @return 结果
     */
    public boolean checkProductNameUnique(Product product);
}
