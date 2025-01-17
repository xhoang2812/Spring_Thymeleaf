package com.poly.du_an_tot_nghiep_f6.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PayRequest {
    String nameCustomer;
    String phoneCustomer;
    String province;
    String district;
    String ward;
    String addressDetailCustomer;
    String describeCustomerAddress;
    Double shipPrice;
    Boolean saveAddress;
    Integer idPaymentMethods;
    String describeCustomerBill;
}
