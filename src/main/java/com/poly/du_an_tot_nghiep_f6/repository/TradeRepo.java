package com.poly.du_an_tot_nghiep_f6.repository;

import com.poly.du_an_tot_nghiep_f6.entity.Bill;
import com.poly.du_an_tot_nghiep_f6.entity.Trade;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TradeRepo extends JpaRepository<Trade, Integer> {
    Trade findByBill(Bill bill);
}
