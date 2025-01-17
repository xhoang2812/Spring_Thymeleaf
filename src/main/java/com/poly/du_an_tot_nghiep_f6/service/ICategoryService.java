package com.poly.du_an_tot_nghiep_f6.service;

import com.poly.du_an_tot_nghiep_f6.entity.Brand;
import com.poly.du_an_tot_nghiep_f6.entity.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ICategoryService {

    List<Category> findAllByStatusEquals();

    List<Category> findAll();

    Category findById(int id);

    Category findByName(String name);

    void updateStatus(int id);

    Category add(Category category);

    Category update(Category category);
}
