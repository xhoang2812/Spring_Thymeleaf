package com.poly.du_an_tot_nghiep_f6.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Objects;

@Data
@NoArgsConstructor
public class BillDetailResponse {
    private int id;
    private int billId;
    private String nameProduct;
    private int quantity;
    private Double price;
    private Double oldPrice;
    private Double intoMoney;
    private String description;
    private int idProductDetail;

    public BillDetailResponse(int id, int billId, String nameProduct, int quantity, Double price, Double oldPrice, Double intoMoney, String description, int idProductDetail) {
        this.id = id;
        this.billId = billId;
        this.nameProduct = nameProduct;
        this.quantity = quantity;
        if (Objects.equals(price, oldPrice)) {
            this.price = price;
            this.oldPrice = 0.;
        } else {
            this.price = price;
            this.oldPrice = oldPrice;
        }
        this.intoMoney = intoMoney;
        this.description = description;
        this.idProductDetail = idProductDetail;
    }
}
