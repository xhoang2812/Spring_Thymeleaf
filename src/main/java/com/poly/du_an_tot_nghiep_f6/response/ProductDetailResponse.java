package com.poly.du_an_tot_nghiep_f6.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class ProductDetailResponse {
    Integer id;
    Double price;
    Integer quantity;
    String code;
    String name;
    String size;
    String color;
    String style;
    String category;
    String material;
    String brand;
    Integer newPrice;
    Integer quantityNoneUsePromotion;
    String namePromotion;

    public ProductDetailResponse(Integer id, Double price, Integer quantity, String code, String name, String size, String color, String style, String category, String material, String brand, Integer newPrice, Integer quantityNoneUsePromotion, String namePromotion, String statusPromotion) {
        this.id = id;
        this.price = price;
        this.quantity = quantity;
        this.code = code;
        this.name = name;
        this.size = size;
        this.color = color;
        this.style = style;
        this.category = category;
        this.material = material;
        this.brand = brand;
        if (statusPromotion == null || statusPromotion.contains("Đang")) {
            this.newPrice = newPrice;
            this.quantityNoneUsePromotion = quantityNoneUsePromotion;
            this.namePromotion = namePromotion;
        } else {
            this.newPrice = 0;
            this.quantityNoneUsePromotion = 0;
            this.namePromotion = null;
        }
    }

}