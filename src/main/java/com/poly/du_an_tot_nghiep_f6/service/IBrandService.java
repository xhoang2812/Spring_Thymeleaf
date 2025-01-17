package com.poly.du_an_tot_nghiep_f6.service;

import com.poly.du_an_tot_nghiep_f6.entity.Brand;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface IBrandService {

    List<Brand> findAllByStatusEquals();

    List<Brand> findAll();

    Brand findById(int id);

    Brand findByName(String name);

    Brand add(Brand brand);

    Brand update(Brand brand);

    void updateTrangThai(int id);
}
