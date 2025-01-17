package com.poly.du_an_tot_nghiep_f6.service.impl;

import com.poly.du_an_tot_nghiep_f6.entity.Image;
import com.poly.du_an_tot_nghiep_f6.repository.ImageRepo;
import com.poly.du_an_tot_nghiep_f6.service.IImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
@Service
@RequiredArgsConstructor
public class ImageServiceImpl implements IImageService {
    private final ImageRepo imageRepo;

    public List<Image> findAllByProductId(Integer productId) {
        return imageRepo.findAllByProdcutId(productId);
    }

    @Override
    public List<Image> findAllByStatusTrue() {
        return imageRepo.findAllByStatusEquals(1);
    }

    @Override
    public List<Image> findAll() {
        return imageRepo.findAll();
    }

    @Override
    public Page<Image> findAll(Pageable pageable) {
        return imageRepo.findAll(pageable);
    }

    @Override
    public Image findById(int id) {
        return imageRepo.findById(id).orElse(null);
    }

    @Override
    public Image findByCode(String code) {
        return imageRepo.findByCode(code);
    }

    @Override
    public void updateStatus(int id) {
        Image image = findById(id);
        if (image.getStatus() == 1){
            image.setStatus(0);
        }else image.setStatus(1);
        image.setDateUpdate(new Date());
        imageRepo.save(image);
    }


    @Override
    public Image add(Image image) {
        image.setDateCreate(new Date());
       return imageRepo.save(image);
    }

    @Override
    public Image update(Image image) {
        return imageRepo.findById(image.getId())
                .map(o->{
                    o.setDateUpdate(image.getDateCreate());
                    o.setCode(image.getCode());
                    o.setUrl1(image.getUrl1());
                    o.setUrl2(image.getUrl2());
                    o.setUrl3(image.getUrl3());
                    return imageRepo.save(o);
                }).orElseThrow(()->  new RuntimeException("Ko tìm thấy ảnh"));
    }
}
