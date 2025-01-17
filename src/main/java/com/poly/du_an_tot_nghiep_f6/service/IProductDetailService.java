package com.poly.du_an_tot_nghiep_f6.service;

import com.poly.du_an_tot_nghiep_f6.entity.ProductDetail;

import java.util.List;

public interface IProductDetailService {
    List<ProductDetail> findAll();
    ProductDetail findById(int id);
    void changeStatus(int id);
    List<ProductDetail> findByProductId(int productId);
    void deleteById(int id);
    ProductDetail add(ProductDetail productDetail);
    ProductDetail update(ProductDetail productDetail);
}
