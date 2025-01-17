package com.poly.du_an_tot_nghiep_f6.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class CartDetailInCounter {
    int id;
    private CartInCounter cartInCounter;
    private ProductDetail productDetail;
    private PromotionDetail promotionDetail;
    private Double price;
    private Double priceOld;
    int quantity;
    Boolean isEditQuantity = true;

    public void setPromotionDetail(PromotionDetail promotionDetail) {
        if (promotionDetail != null) {
            price = (double) promotionDetail.getGiaMoi();
            priceOld = productDetail.getPrice();
            if (promotionDetail.getPromotion().getDateEnd().isBefore(LocalDateTime.now()) ||
                !promotionDetail.getPromotion().isCondition()) {
                this.isEditQuantity = false;
            }
            this.promotionDetail = promotionDetail;
        } else {
            price = productDetail.getPrice();
            priceOld = 0.;
        }
    }

    public CartDetailInCounter(int id, CartInCounter cartInCounter, ProductDetail productDetail, PromotionDetail promotionDetail, int quantity) {
        this.id = id;
        this.cartInCounter = cartInCounter;
        this.productDetail = productDetail;
        this.quantity = quantity;
        this.promotionDetail = promotionDetail;

        if (promotionDetail != null) {
            price = (double) promotionDetail.getGiaMoi();
            priceOld = productDetail.getPrice();
            if (promotionDetail.getPromotion().getDateEnd().isBefore(LocalDateTime.now()) ||
                !promotionDetail.getPromotion().isCondition()) {
                this.isEditQuantity = false;
            }
        } else {
            price = productDetail.getPrice();
            priceOld = 0.;
        }
    }


}
