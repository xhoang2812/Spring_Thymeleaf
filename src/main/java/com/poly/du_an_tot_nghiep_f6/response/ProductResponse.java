package com.poly.du_an_tot_nghiep_f6.response;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@Entity
public class ProductResponse implements Serializable {
    @Id
    private Integer productID;
    private String productName;
    private String productCategory;
    private String productBrand;
    private String productMaterial;
    private String productStyle;
    private Double productMaxPrice;
    private Long productSumQuantity;
    private String productImage;
    private String productDescription;
    private String productGender;
    private int productMinPrice;

    public ProductResponse(Integer productID, String productName, String productCategory, String productBrand, String productMaterial, String productStyle, Double productMaxPrice, Long productSumQuantity, String productImage ) {
        this.productID = productID;
        this.productName = productName;
        this.productCategory = productCategory;
        this.productBrand = productBrand;
        this.productMaterial = productMaterial;
        this.productStyle = productStyle;
        this.productMaxPrice = productMaxPrice;
        this.productSumQuantity = productSumQuantity;
        this.productImage = productImage;
    }

    public ProductResponse(String productCategory, String productBrand, String productMaterial, String productStyle, String productDescription, String productGender, Integer productID, String productName) {
        this.productCategory = productCategory;
        this.productBrand = productBrand;
        this.productMaterial = productMaterial;
        this.productStyle = productStyle;
        this.productDescription = productDescription;
        this.productGender = productGender;
        this.productID = productID;
        this.productName = productName;
    }
}
