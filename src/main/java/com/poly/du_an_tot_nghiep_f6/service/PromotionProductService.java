package com.poly.du_an_tot_nghiep_f6.service;


import com.poly.du_an_tot_nghiep_f6.entity.Promotion;
import com.poly.du_an_tot_nghiep_f6.entity.PromotionDetail;
import com.poly.du_an_tot_nghiep_f6.repository.ProductDetailRepo;
import com.poly.du_an_tot_nghiep_f6.repository.PromotionDetailRepo;
import com.poly.du_an_tot_nghiep_f6.response.ProductDetailResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PromotionProductService {
    @Autowired
    PromotionDetailRepo promotionDetailRepo;
    @Autowired
    ProductDetailRepo productDetailRepo;

    public List<PromotionDetail> getPromotionDetails() {
        return promotionDetailRepo.findAll();
    }

    public void addProductToPromotion(Promotion promotion, int productDetailId) {
        PromotionDetail promotionDetail = new PromotionDetail();
        promotionDetail.setPromotion(promotion);
        promotionDetail.setProductDetail(productDetailRepo.findById(productDetailId).get());
        promotionDetailRepo.save(promotionDetail);
    }




}
