package com.poly.du_an_tot_nghiep_f6.service;

import com.poly.du_an_tot_nghiep_f6.entity.Image;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface IImageService {
    List<Image> findAllByStatusTrue();
    List<Image> findAll();
    Page<Image> findAll(Pageable pageable);
    Image findById(int id);
    Image findByCode(String code);
    void updateStatus(int id);
    Image add(Image image);
    Image update(Image image);
}
