package com.poly.du_an_tot_nghiep_f6.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BillDetailOnlineResponse {
    private Integer billDetailId;
    private Integer productId;
    private String variant;
    private String productName;
    private Integer quantity;
    private String productImage;
    private Double price;
    private Double discount;
}
