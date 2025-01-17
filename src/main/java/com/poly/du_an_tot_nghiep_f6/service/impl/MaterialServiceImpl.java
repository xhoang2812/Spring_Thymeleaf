package com.poly.du_an_tot_nghiep_f6.service.impl;

import com.poly.du_an_tot_nghiep_f6.entity.Material;
import com.poly.du_an_tot_nghiep_f6.repository.MaterialRepo;
import com.poly.du_an_tot_nghiep_f6.service.IMaterialService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MaterialServiceImpl implements IMaterialService {
    private final MaterialRepo materialRepo;


    @Override
    public List<Material> findAllByStatusTrue() {
        return materialRepo.findAllByStatusAndOrderByDateCreateDesc();
    }

    @Override
    public List<Material> findAll() {
        return materialRepo.findAll();
    }


    @Override
    public Material findById(int id) {
        return materialRepo.findById(id).orElseThrow(()-> new RuntimeException("No color found with id: " + id));
    }

    @Override
    public void updateStatus(int id) {
        Material material = findById(id);
        if (material.getStatus() == 1){
            material.setStatus(0);
        }else material.setStatus(1);
        material.setDateUpdate(new Date());
        materialRepo.save(material);
    }

    @Override
    public Material findByName(String name) {
        return materialRepo.findByName(name);
    }


    @Override
    public Material add(Material material) {
        material.setDateCreate(new Date());
        return materialRepo.save(material);
    }

    @Override
    public Material update(Material material) {
        return materialRepo.findById(material.getId()).
                map(o->{
                    o.setName(material.getName());
                    o.setDateUpdate(new Date());
                    o.setStatus(material.getStatus());
                    return materialRepo.save(o);
                }).orElseThrow(()-> new RuntimeException("Can't find color"));
    }
}
