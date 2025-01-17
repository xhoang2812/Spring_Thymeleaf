package com.poly.du_an_tot_nghiep_f6.service;

import com.poly.du_an_tot_nghiep_f6.entity.Product;


import java.util.List;

public interface IProductService {
    List<Product> findAllByStatusTrue();
    List<Product> findAll();
    Product findById(int id);
    Product findByName(String name);
    Product findByCode(String code);
    void updateStatus(int id);
    Product add(Product product);
    Product update(Product product);
}
