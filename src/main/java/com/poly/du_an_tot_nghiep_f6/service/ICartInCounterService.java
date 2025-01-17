package com.poly.du_an_tot_nghiep_f6.service;

import com.poly.du_an_tot_nghiep_f6.entity.Bill;
import com.poly.du_an_tot_nghiep_f6.entity.CartInCounter;
import com.poly.du_an_tot_nghiep_f6.entity.Customer;
import com.poly.du_an_tot_nghiep_f6.entity.Employee;

public interface ICartInCounterService {

    CartInCounter getCart(int id);

    CartInCounter createCart();


    Boolean deleteCart(int id,boolean status);


    Bill getBill(int idCart, Employee employee);

    Boolean setCustomer(int idCart, Customer customer);
}
