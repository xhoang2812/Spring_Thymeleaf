package com.poly.du_an_tot_nghiep_f6.service;

import com.poly.du_an_tot_nghiep_f6.entity.PaymentMethods;
import org.springframework.stereotype.Repository;

@Repository
public interface IPaymentMethodsService {
    PaymentMethods getPaymentMethods(Integer id);
}
