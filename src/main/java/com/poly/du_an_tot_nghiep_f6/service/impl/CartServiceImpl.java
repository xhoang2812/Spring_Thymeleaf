package com.poly.du_an_tot_nghiep_f6.service.impl;

import com.poly.du_an_tot_nghiep_f6.entity.Cart;
import com.poly.du_an_tot_nghiep_f6.repository.CartRepo;
import com.poly.du_an_tot_nghiep_f6.service.ICartService;
import org.springframework.stereotype.Service;

@Service
public class CartServiceImpl implements ICartService {
    private final CartRepo cartRepo;

    public CartServiceImpl(CartRepo cartRepo) {
        this.cartRepo = cartRepo;
    }

    public Cart getCartDetailByIdCustomer(Integer idCustomer){
        return cartRepo.getCartByCustomer_Id(idCustomer);
    }
}
