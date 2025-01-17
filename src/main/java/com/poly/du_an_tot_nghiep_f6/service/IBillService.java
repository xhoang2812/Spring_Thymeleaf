package com.poly.du_an_tot_nghiep_f6.service;

import com.poly.du_an_tot_nghiep_f6.entity.Bill;
import com.poly.du_an_tot_nghiep_f6.entity.Cart;
import com.poly.du_an_tot_nghiep_f6.entity.GiaoCa;
import com.poly.du_an_tot_nghiep_f6.response.BillResponse;

import java.util.Date;
import java.util.List;

public interface IBillService {

    Bill save(Bill bill);

    Bill getBill(int id);

    List<BillResponse> getAllResponse();

    List<Bill> getAll();

    int getTotalBillInShift(GiaoCa giaoCa);

    double getTotalPriceBillInShift(GiaoCa giaoCa, Integer id);

    Integer checkQuantity(Integer idBill);

    boolean chargeQuantity(Integer idBill);

    List<BillResponse> getBillFilter(Integer idBill, String keyWord, Date dateStart, Date dateEnd, Integer status, Double minPrice, Double maxPrice, Boolean sort);
}
