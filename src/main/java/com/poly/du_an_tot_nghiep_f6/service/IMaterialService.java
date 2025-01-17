package com.poly.du_an_tot_nghiep_f6.service;

import com.poly.du_an_tot_nghiep_f6.entity.Material;

import java.util.List;

public interface IMaterialService {
    List<Material> findAllByStatusTrue();

    List<Material> findAll();

    Material findById(int id);

    void updateStatus(int id);

    Material findByName(String name);

    Material add(Material material);

    Material update(Material material);
}
