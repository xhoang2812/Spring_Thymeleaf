package com.poly.du_an_tot_nghiep_f6.service;

import com.poly.du_an_tot_nghiep_f6.entity.Size;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ISizeService {
    List<Size> findAllByStatusTrue();
    List<Size> findAll();

    Size findById(int id);

    Size findByName(String name);

    void updateStatus(int id);

    Size add(Size size);

    Size update(Size size);
}
