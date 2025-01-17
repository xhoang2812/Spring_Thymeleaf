package com.poly.du_an_tot_nghiep_f6.service;

import com.poly.du_an_tot_nghiep_f6.entity.Style;
import java.util.List;

public interface IStyleService {
    List<Style> findByStatusEquals();

    List<Style> findAll();

    Style findById(int id);

    Style findByName(String name);

    void updateStatus(int id);

    Style add(Style size);

    Style update(Style size);
}
