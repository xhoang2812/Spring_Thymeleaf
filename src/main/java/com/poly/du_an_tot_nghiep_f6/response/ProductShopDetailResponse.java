package com.poly.du_an_tot_nghiep_f6.response;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
public class ProductShopDetailResponse {
    @Id
    private Integer productDetailID;
    private Double productDetailPrice;
    private Double productDetailWeight;
    private Integer productID;
    private int productDetailSalePrice;
    private String productDetailImg1;
    private String productDetailImg2;
    private String productDetailImg3;
    private Integer promotionID;
    private Integer productDetailQuantity;
    private Integer productDetailStatus;
}
