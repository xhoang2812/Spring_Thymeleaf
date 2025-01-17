package com.poly.du_an_tot_nghiep_f6.response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CartKoDangNhapRes {
    private Integer idProductDetail;
    private String img;
    private String name;
    private String nameColor;
    private String nameSize;
    private Integer quantityStock;
    private Double priceProduct;
    private int pricePromotion;
    private Double totalPrice;
    private Integer quantityBuy;
    private Double weightProduct;
    private Integer idPromotion;
    private Integer statusProduct;
    private Integer statusProductDetail;
}
