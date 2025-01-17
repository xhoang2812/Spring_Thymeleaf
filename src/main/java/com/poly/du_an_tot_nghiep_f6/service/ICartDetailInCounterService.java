package com.poly.du_an_tot_nghiep_f6.service;

import com.poly.du_an_tot_nghiep_f6.entity.Bill;
import com.poly.du_an_tot_nghiep_f6.entity.BillDetail;
import com.poly.du_an_tot_nghiep_f6.entity.CartInCounter;
import com.poly.du_an_tot_nghiep_f6.entity.CartDetailInCounter;
import com.poly.du_an_tot_nghiep_f6.response.CartDetailResponse;

import java.util.ArrayList;
import java.util.List;

public interface ICartDetailInCounterService {
    List<CartDetailInCounter> getCartDetails(int idCart);

    CartDetailInCounter getCartDetail(int idCart);

    List<CartDetailResponse> getCartDetailResponses(int idCart);

    void addCartDetail(CartDetailInCounter cartDetailInCounter, boolean status);

    void updateCartDetail(int idCardDetail, int quantity);

    void deleteCartDetail(int id,boolean status);

    void deleteAllCartDetail(CartInCounter cartInCounter, boolean status);

    List<BillDetail> getBillDetail(int idCart, Bill bill);

    List<CartDetailResponse> parseCartDetailResponse(ArrayList<CartDetailInCounter> cartDetailInCounters);
}
