package com.poly.du_an_tot_nghiep_f6.service;

import com.poly.du_an_tot_nghiep_f6.controller.Promotion.PromotionRealtime;
import com.poly.du_an_tot_nghiep_f6.entity.*;
import com.poly.du_an_tot_nghiep_f6.repository.ProductDetailRepo;
import com.poly.du_an_tot_nghiep_f6.repository.PromotionDetailRepo;
import com.poly.du_an_tot_nghiep_f6.repository.PromotionRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.time.LocalDateTime;

@Service
public class PromotionService {

    @Autowired
    private PromotionRepo promotionRepository;

    @Autowired
    private PromotionDetailRepo promotionDetailRepo;

    @Autowired
    private ProductDetailRepo productDetailRepo;

    @Autowired
    private PromotionProductService promotionProductService;

    @Autowired
    private PromotionRealtime promotionRealtime;

    // Lấy tất cả khuyến mãi
    public List<Promotion> getAllPromotions() {
        return promotionRepository.findAll();
    }

    // Tìm khuyến mãi theo ID
    public Optional<Promotion> findById(Integer id) {
        return promotionRepository.findById(id);
    }

    // Lấy khuyến mãi theo ID
    public Promotion getPromotionById(Integer id) {
        return promotionRepository.findById(id).orElse(null);
    }

    //Lưu khuyến mãi
    public void savePromotion(Promotion promotion, List<Integer> list) {
        promotion.setCode(UUID.randomUUID().toString().replace("-", "").substring(0, 10));
        promotion.setStatus("Chưa diễn ra");
        promotion.setDateCreate(LocalDateTime.now());
        promotion.setCondition(!promotion.isCondition());
        promotion.setCondition(true);
        promotion.setQuantityUse(0);
        Promotion savePromotion = promotionRepository.save(promotion);
        for (Integer i : list) {
            ProductDetail productDetail = productDetailRepo.findById(i)
                    .orElseThrow(() -> new RuntimeException("productDetail not found"));
            PromotionDetail promotionDetail = getPromotionDetail(promotion, savePromotion, productDetail);
            PromotionDetail savePromotionDetail = promotionDetailRepo.save(promotionDetail);
            productDetail.setPromotionDetail(savePromotionDetail);
            productDetailRepo.save(productDetail);
        }
    }

    private static PromotionDetail getPromotionDetail(Promotion promotion, Promotion savePromotion, ProductDetail productDetail) {
        PromotionDetail promotionDetail = new PromotionDetail();
        promotionDetail.setPromotion(savePromotion);
        promotionDetail.setProductDetail(productDetail);
        if (promotion.getPrice() <= 100) {
            int price = (int) (productDetail.getPrice() - (productDetail.getPrice() * ((double) promotion.getPrice() / 100)));
            promotionDetail.setGiaMoi(price);
        } else {
            promotionDetail.setGiaMoi((int) (productDetail.getPrice() - promotion.getPrice()));
        }
        return promotionDetail;
    }

    public void updatePromotion(Promotion promotion, List<Integer> list) {
        Promotion existingPromotion = promotionRepository.findById(promotion.getId())
                .orElseThrow(() -> new RuntimeException("Data not found"));
        existingPromotion.setDateUpdate(LocalDateTime.now());
        existingPromotion.setDateStart(promotion.getDateStart());
        existingPromotion.setDateEnd(promotion.getDateEnd());
        existingPromotion.setPrice(promotion.getPrice());
        existingPromotion.setName(promotion.getName());
        existingPromotion.setQuantity(promotion.getQuantity());
        existingPromotion.setValueType(promotion.getValueType());

        List<ProductDetail> oldProductDetails = new ArrayList<>();
        for (PromotionDetail promotionDetail : promotionDetailRepo.findByPromotion(existingPromotion)) {
            oldProductDetails.add(promotionDetail.getProductDetail());
        }

        List<ProductDetail> newProductDetails = new ArrayList<>();
        for (Integer i : list) {
            ProductDetail productDetail = productDetailRepo.findById(i)
                    .orElseThrow(() -> new RuntimeException("Data not found"));
            newProductDetails.add(productDetail);
        }
        List<ProductDetail> newsProductDetails = new ArrayList<>(newProductDetails);
        newProductDetails.removeAll(oldProductDetails);
        oldProductDetails.removeAll(newsProductDetails);

        for (ProductDetail productDetail : newProductDetails) {
            PromotionDetail promotionDetail = new PromotionDetail();
            promotionDetail.setProductDetail(productDetail);
            promotionDetail.setPromotion(existingPromotion);
            PromotionDetail savePromotionDetail = promotionDetailRepo.save(promotionDetail);
            productDetail.setPromotionDetail(savePromotionDetail);
            if (existingPromotion.getPrice() <= 100) {
                int price = (int) (productDetail.getPrice() - (productDetail.getPrice() * ((double) promotion.getPrice() / 100)));
                promotionDetail.setGiaMoi(price);
            } else {
                promotionDetail.setGiaMoi((int) (productDetail.getPrice() - promotion.getPrice()));
            }
            promotionDetailRepo.save(promotionDetail);
        }
        for (PromotionDetail promotionDetail : promotionDetailRepo.findByPromotion(existingPromotion)) {
            for (ProductDetail productDetail : oldProductDetails) {
                if (promotionDetail.getProductDetail().getId().equals(productDetail.getId())) {
                    productDetail.setPromotionDetail(null);
                    productDetailRepo.save(productDetail);
                    promotionDetailRepo.delete(promotionDetail);
                }
            }
        }
        promotionRepository.save(existingPromotion);
    }

    @Transactional
    public void updateStatusPromotion() {
        List<Promotion> list = promotionRepository.findAll();
        for (Promotion promotion : list) {
            if (LocalDateTime.now().isAfter(promotion.getDateStart()) && LocalDateTime.now().isBefore(promotion.getDateEnd())) {
                promotion.setStatus("Đang diễn ra");
            } else if (LocalDateTime.now().isBefore(promotion.getDateStart())) {
                promotion.setStatus("Chưa diễn ra");
            } else {
                promotion.setStatus("Đã kết thúc");
                List<PromotionDetail> promotionDetails = promotionDetailRepo.findByPromotion(promotion);
                for (PromotionDetail promotionDetail : promotionDetails) {
                    ProductDetail productDetail = productDetailRepo.findByPromotionDetail(promotionDetail);
                    if (productDetail != null) {
                        productDetail.setPromotionDetail(null);
                    }
                }
            }
            promotionRepository.save(promotion);
            promotionRealtime.sendPromotionUpdate(promotion);
        }
    }

    public void upQuantityUsePromotion(int idPromotion) {
        try {
            Promotion promotion = promotionRepository.findById(idPromotion).get();// tìm đợt km
            try{// xử lý null quantityuse
                promotion.setQuantityUse(promotion.getQuantityUse()+ 1);// promotion.getQuantityUse() bỏ vào catch
                promotionRepository.save(promotion);// lưu
            }catch (Exception e){
                promotion.setQuantityUse(1);
                promotionRepository.save(promotion);// lưu
            }
        } catch (Exception ignored) {
        }
    }

    public void downQuantityUsePromotion(int idPromotion) {
        try {
            Promotion promotion = promotionRepository.findById(idPromotion).get();
            promotion.setQuantityUse(promotion.getQuantityUse() - 1);
            promotionRepository.save(promotion);
        } catch (Exception ignored) {
        }
    }
}
