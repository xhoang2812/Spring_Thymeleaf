package com.poly.du_an_tot_nghiep_f6.service.impl;


import com.poly.du_an_tot_nghiep_f6.entity.Size;
import com.poly.du_an_tot_nghiep_f6.repository.SizeRepo;
import com.poly.du_an_tot_nghiep_f6.service.ISizeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SizeServiceImpl implements ISizeService {

    private final SizeRepo sizeRepo;

    public List<Size> findAllByProductId1(Integer productId) {
        return sizeRepo.findAllByProductId(productId);
    }

    @Override
    public List<Size> findAllByStatusTrue() {
        return sizeRepo.findAllByStatusEquals();
    }

    @Override
    public List<Size> findAll() {
        return sizeRepo.findAll();
    }

    @Override
    public Size findById(int id) {
        return sizeRepo.findById(id).orElseThrow(()-> new RuntimeException("No color found with id: " + id));
    }

    @Override
    public Size findByName(String name) {
        return sizeRepo.findByName(name);
    }

    @Override
    public void updateStatus(int id) {
        Size size = findById(id);
        if (size.getStatus() == 1){
            size.setStatus(0);
        }else size.setStatus(1);
        size.setDateUpdate(new Date());
        sizeRepo.save(size);
    }

    @Override
    public Size add(Size size) {
        size.setDateCreate(new Date());
        return sizeRepo.save(size);
    }

    @Override
    public Size update(Size size) {
        return sizeRepo.findById(size.getId()).
                map(o->{
                    o.setName(size.getName());
                    o.setDateUpdate(new Date());
                    o.setStatus(size.getStatus());
                    return sizeRepo.save(o);
                }).orElseThrow(()-> new RuntimeException("Can't find color"));
    }
}
