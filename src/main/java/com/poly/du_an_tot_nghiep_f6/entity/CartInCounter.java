package com.poly.du_an_tot_nghiep_f6.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CartInCounter {
    int id;
    Customer customer;//
    Address address;//
    Double shipPrice;
    String descriptionAddress;
    Voucher voucher;//
    boolean ship = false;
    PaymentMethods paymentMethods;
    boolean status;
    Bill billEdit;
    String descriptionBill;
}
