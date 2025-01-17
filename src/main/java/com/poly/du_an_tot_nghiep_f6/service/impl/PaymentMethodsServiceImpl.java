package com.poly.du_an_tot_nghiep_f6.service.impl;

import com.poly.du_an_tot_nghiep_f6.entity.PaymentMethods;
import com.poly.du_an_tot_nghiep_f6.repository.PaymentMethodsRepo;
import com.poly.du_an_tot_nghiep_f6.service.IPaymentMethodsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PaymentMethodsServiceImpl implements IPaymentMethodsService {

    @Autowired
    PaymentMethodsRepo paymentMethodsRepo;

    @Override
    public PaymentMethods getPaymentMethods(Integer id) {
        if (id != null || id <1 || id>3) {
            return paymentMethodsRepo.findById(id).get();
        }
        System.out.println("ID Phương thức thanh toán không hợp lệ");
        return null;
    }
}
