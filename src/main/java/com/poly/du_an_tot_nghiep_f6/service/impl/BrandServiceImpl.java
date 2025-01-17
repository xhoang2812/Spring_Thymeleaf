package com.poly.du_an_tot_nghiep_f6.service.impl;

import com.poly.du_an_tot_nghiep_f6.entity.Brand;
import com.poly.du_an_tot_nghiep_f6.repository.BrandRepo;
import com.poly.du_an_tot_nghiep_f6.service.IBrandService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BrandServiceImpl implements IBrandService {

    private final BrandRepo brandRepo;

    @Override
    public List<Brand> findAllByStatusEquals() {
        return brandRepo.findAllByStatusEquals();
    }

    @Override
    public List<Brand> findAll() {
        return brandRepo.findAll();
    }

    @Override
    public Brand findById(int id) {
        return brandRepo.findById(id).orElseThrow(() -> new RuntimeException("No color found with id: " + id));
    }


    @Override
    public Brand add(Brand brand) {
        brand.setDateCreate(new Date());
        return brandRepo.save(brand);
    }

    @Override
    public Brand update(Brand brand) {
        return brandRepo.findById(brand.getId()).
                map(o -> {
                    o.setName(brand.getName());
                    o.setDateUpdate(new Date());
                    o.setStatus(brand.getStatus());
                    return brandRepo.save(o);
                }).orElseThrow(() -> new RuntimeException("Can't find color"));
    }

    @Override
    public void updateTrangThai(int id) {
        Brand brand1 = findById(id);
        if (brand1.getStatus() == 1) {
            brand1.setStatus(0);
        } else {
            brand1.setStatus(1);
        }
        brand1.setDateUpdate(new Date());
        brandRepo.save(brand1);
    }

    @Override
    public Brand findByName(String name) {
        return brandRepo.findByName(name);
    }


}
