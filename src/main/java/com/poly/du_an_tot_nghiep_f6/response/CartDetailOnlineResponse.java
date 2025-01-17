package com.poly.du_an_tot_nghiep_f6.response;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CartDetailOnlineResponse {
    @Id
    int id;
    int idCart;
    Integer idProductDetail;
    String maxImage;
    String nameProduct;
    String nameSize;
    String nameColor;
    Integer quantity;
    Double price;
    Integer priceSale;
    int quantityInStock;
    Double total;
    Double weight;
    Integer promotionID;
    boolean havePromotion;
    Integer productStatus;
    Integer productDetailStatus;



}
