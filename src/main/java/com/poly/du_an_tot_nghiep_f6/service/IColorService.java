package com.poly.du_an_tot_nghiep_f6.service;

import com.poly.du_an_tot_nghiep_f6.entity.Color;


import java.util.List;

public interface IColorService {

    List<Color> findAllByStatusTrue();

    List<Color> findAll();

    Color findById(int id);

    Color findByName(String name);

    void updateStatus(int id);

    Color add(Color color);

    Color update(Color color);

}
