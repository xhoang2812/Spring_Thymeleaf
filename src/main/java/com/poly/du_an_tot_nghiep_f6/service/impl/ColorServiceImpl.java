package com.poly.du_an_tot_nghiep_f6.service.impl;

import com.poly.du_an_tot_nghiep_f6.entity.Color;
import com.poly.du_an_tot_nghiep_f6.repository.ColorRepo;
import com.poly.du_an_tot_nghiep_f6.service.IColorService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ColorServiceImpl implements IColorService {

    private final ColorRepo colorRepo;

    public List<Color> findAllByProductId1(Integer productId) {
        return colorRepo.findAllByProductId(productId);
    }

    @Override
    public List<Color> findAllByStatusTrue() {
        return colorRepo.findAllByStatusEquals();
    }

    @Override
    public List<Color> findAll() {
        return colorRepo.findAll();
    }


    @Override
    public Color findById(int id) {
        return colorRepo.findById(id).orElseThrow(()-> new RuntimeException("No color found with id: " + id));
    }

    @Override
    public Color findByName(String name) {
        return colorRepo.findByName(name);
    }

    @Override
    public void updateStatus(int id) {
        Color c = colorRepo.findById(id).get();
        if (c.getStatus() == 1){
            c.setStatus(0);
        }else {
            c.setStatus(1);
        }
        c.setDateUpdate(new Date());
        colorRepo.save(c);
    }


    @Override
    public Color add(Color color) {
        color.setDateCreate(new Date());
        return colorRepo.save(color);
    }

    @Override
    public Color update(Color color) {
        return colorRepo.findById(color.getId()).
                map(o->{
                    o.setName(color.getName());
                    o.setDateUpdate(new Date());
                    o.setStatus(color.getStatus());
                    return colorRepo.save(o);
                }).orElseThrow(()-> new RuntimeException("Can't find color"));
    }
}
