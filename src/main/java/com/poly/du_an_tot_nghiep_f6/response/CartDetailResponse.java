package com.poly.du_an_tot_nghiep_f6.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CartDetailResponse {
    int id;
    String nameProduct;
    Double price;
    Double oldPrice;
    int quantity;
    String namePromotion;
    Boolean isEditQuantity;
}
