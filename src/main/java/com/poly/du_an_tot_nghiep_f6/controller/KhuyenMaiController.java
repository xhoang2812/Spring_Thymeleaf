package com.poly.du_an_tot_nghiep_f6.controller;

import com.poly.du_an_tot_nghiep_f6.entity.Product;
import com.poly.du_an_tot_nghiep_f6.entity.ProductDetail;
import com.poly.du_an_tot_nghiep_f6.entity.Promotion;
import com.poly.du_an_tot_nghiep_f6.entity.PromotionDetail;
import com.poly.du_an_tot_nghiep_f6.repository.ProductDetailRepo;
import com.poly.du_an_tot_nghiep_f6.repository.ProductRepo;
import com.poly.du_an_tot_nghiep_f6.repository.PromotionDetailRepo;
import com.poly.du_an_tot_nghiep_f6.repository.PromotionRepo;
import com.poly.du_an_tot_nghiep_f6.response.PromotionResponse;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

@RestController
@CrossOrigin("*")
@RequestMapping("/khuyenmai")
public class KhuyenMaiController {
    @Autowired
    private ProductDetailRepo productDetailRepo;
    @Autowired
    private ProductRepo productRepo;
    @Autowired
    private PromotionDetailRepo promotionDetailRepo;
    @Autowired
    private PromotionRepo promotionRepo;

    @GetMapping
    public List<PromotionResponse> getAllPromotion(){
        List<Promotion> promotions = promotionRepo.findAll();
        List<PromotionResponse> list = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");
        for(Promotion promotion : promotions){
            PromotionResponse promotionResponse = new PromotionResponse();
            BeanUtils.copyProperties(promotion,promotionResponse);
            promotionResponse.setDateStart(promotion.getDateStart().format(formatter));
            promotionResponse.setDateEnd(promotion.getDateEnd().format(formatter));
            promotionResponse.setDateCreate(promotion.getDateCreate().format(formatter));
            if (promotion.getPrice() < 100) {
                promotionResponse.setPrice(promotion.getPrice()+"%");
            }else {
                NumberFormat numberFormat = NumberFormat.getInstance(new Locale("vi", "VN"));
                String price = numberFormat.format(promotion.getPrice());
                promotionResponse.setPrice(price + " VND");
            }
            if(promotion.getDateUpdate() != null){
                promotionResponse.setDateUpdate(promotion.getDateUpdate().format(formatter));
            }

            list.add(promotionResponse);
        }
        return list;
    }
    @GetMapping("/getPromotion/{id}")
    public String getPromotion(@PathVariable Integer id){

        Promotion promotion = promotionRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Data not found"));
        if(promotion.getDateEnd().isBefore(LocalDateTime.now())){
            return "fail1";
        }else if(LocalDateTime.now().isAfter(promotion.getDateStart()) && LocalDateTime.now().isBefore(promotion.getDateEnd())){
            return "fail2";
        }
        return "notFail";
    }

    @GetMapping("/{id}/{idPromotion}")
    public List<ProductDetail> getProductDetails(@PathVariable int id, @PathVariable Integer idPromotion) {
        Product product = productRepo.findById(id).orElseThrow(()->new RuntimeException(""));
        List<ProductDetail> productDetails = new ArrayList<>();
        Promotion promotion = promotionRepo.findById(idPromotion)
                .orElseThrow(() -> new RuntimeException("Promotion not found"));
        List<PromotionDetail> productDetailList = promotionDetailRepo.findByPromotion(promotion).stream()
                .filter(promotionDetail -> promotionDetail.getProductDetail().getProduct().getId().equals(product.getId()))
                .toList();

        List<ProductDetail> productDetailsOfPromotion = new ArrayList<>();
        for(PromotionDetail promotionDetail : productDetailList){
            productDetailsOfPromotion.add(promotionDetail.getProductDetail());
        }
        for(ProductDetail productDetail: productDetailsOfPromotion){
            if(productDetail.getProduct().getId().equals(product.getId())){
                productDetails.add(productDetail);
            }
        }
        for(ProductDetail productDetail: productDetailRepo.findByProduct(product)){
            if(productDetail.getPromotionDetail() == null){
                productDetails.add(productDetail);
            }
        }

        return productDetails;
    }
    @GetMapping("/productDetailToAdd/{id}")
    public List<ProductDetail> getProductDetailsToAdd(@PathVariable int id) {
        Product product = productRepo.findById(id)
                .orElseThrow(()->new RuntimeException("Product not found"));
        return productDetailRepo.findByProduct(product).stream()
                .filter(productDetail -> productDetail.getPromotionDetail() == null)
                .toList();
    }
    @GetMapping("/promotionDetail/{id}")
    public List<PromotionDetail> getAllPromotionDetail(@PathVariable Integer id){
        Promotion promotion = promotionRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Promotion not found"));
        return promotionDetailRepo.findByPromotion(promotion);
    }
    @GetMapping("/promotionDetails/productDetail/{idPromotion}")
    public List<ProductDetail> getProductDetailsByPromotionDetails(
            @PathVariable Integer idPromotion
    ){
        Promotion promotion = promotionRepo.findById(idPromotion)
                .orElseThrow(() -> new RuntimeException("Promotion not found"));
        List<PromotionDetail> list = promotionDetailRepo.findByPromotion(promotion);
        List<ProductDetail> productDetails = new ArrayList<>();
        for(PromotionDetail p : list){
            productDetails.add(p.getProductDetail());
        }
        return productDetails;
    }

    @DeleteMapping("/updateStatusPromotion/{id}")
    public PromotionResponse updateStatusPromotion(@PathVariable Integer id){
        Promotion promotion = promotionRepo.getReferenceById(id);
        if(!promotion.isCondition()){
            return null;
        }else {
            promotion.setCondition(false);
            List<PromotionDetail> promotionDetails = promotionDetailRepo.findByPromotion(promotion);
            for(PromotionDetail promotionDetail : promotionDetails){
                ProductDetail productDetail = promotionDetail.getProductDetail();
                if(productDetail.getPromotionDetail() == null){
                    continue;
                }
                productDetail.setPromotionDetail(null);
                productDetailRepo.save(productDetail);
            }
        }
        Promotion existingPromotion = promotionRepo.save(promotion);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");
        PromotionResponse promotionResponse = new PromotionResponse();
        BeanUtils.copyProperties(existingPromotion,promotionResponse);
        promotionResponse.setDateStart(existingPromotion.getDateStart().format(formatter));
        promotionResponse.setDateEnd(existingPromotion.getDateEnd().format(formatter));
        promotionResponse.setDateCreate(promotion.getDateCreate().format(formatter));
        if (promotion.getPrice() < 100) {
            promotionResponse.setPrice(promotion.getPrice()+"%");
        }else {
            NumberFormat numberFormat = NumberFormat.getInstance(new Locale("vi", "VN"));
            String price = numberFormat.format(promotion.getPrice());
            promotionResponse.setPrice(price + " VND");
        }
        if(promotion.getDateUpdate() != null){
            promotionResponse.setDateUpdate(promotion.getDateUpdate().format(formatter));
        }
        return promotionResponse;
    }

    @GetMapping("/checkPrice/{id}")
    public String checkPrice(@PathVariable Integer id, @RequestParam Integer price){
        ProductDetail productDetail = productDetailRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Data not found"));
        if(price >= productDetail.getPrice()){
            return productDetail.getProduct().getName();
        }
        return "";
    }
}
