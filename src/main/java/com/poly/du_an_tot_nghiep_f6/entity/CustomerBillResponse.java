package com.poly.du_an_tot_nghiep_f6.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
@Entity
@AllArgsConstructor
public class CustomerBillResponse {
    @Id
    private int idBill;
    private String url1;
    private String nameProduct;
    private String nameSize;
    private String nameColor;
    private int quantityBillDetail;
    private double priceBillDetail;
    private double totalBill;
    private int statusBill;
    private Double shippingFee;
    private double oldPrice;
    private double tienGiam;

}
