package com.poly.du_an_tot_nghiep_f6.service.impl;

import com.poly.du_an_tot_nghiep_f6.entity.Style;
import com.poly.du_an_tot_nghiep_f6.repository.StyleRepo;
import com.poly.du_an_tot_nghiep_f6.service.IStyleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StyleServiceImpl implements IStyleService {
    private final StyleRepo styleRepo;

    @Override
    public List<Style> findByStatusEquals() {
        return styleRepo.findAllByStatusEqualsAndOrderByDateCreateDesc();
    }

    @Override
    public List<Style> findAll() {
        return styleRepo.findAll();
    }

    @Override
    public Style findById(int id) {
        return styleRepo.findById(id).orElseThrow(()-> new RuntimeException("No color found with id: " + id));
    }

    @Override
    public Style findByName(String name) {
        return styleRepo.findByName(name);
    }

    @Override
    public void updateStatus(int id) {
        Style style = findById(id);
        if (style.getStatus() == 1){
            style.setStatus(0);
        }else style.setStatus(1);
        style.setDateUpdate(new Date());
        styleRepo.save(style);
    }


    @Override
    public Style add(Style style) {
        style.setDateCreate(new Date());
        return styleRepo.save(style);
    }

    @Override
    public Style update(Style style) {
        return styleRepo.findById(style.getId()).
                map(o->{
                    o.setName(style.getName());
                    o.setDateUpdate(new Date());
                    o.setStatus(style.getStatus());
                    return styleRepo.save(o);
                }).orElseThrow(()-> new RuntimeException("Can't find color"));
    }


}
