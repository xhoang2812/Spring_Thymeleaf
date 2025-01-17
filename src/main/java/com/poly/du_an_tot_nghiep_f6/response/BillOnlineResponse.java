package com.poly.du_an_tot_nghiep_f6.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.AccessType;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BillOnlineResponse {
    private Integer billId;
    private Integer billStatus;
    private String name;
    private String phone;
    private String address;
    private Double total;
    private Double shippingPrice;
    private Integer discount;
    private String paymentType;
    private Integer maxGiam;
}
