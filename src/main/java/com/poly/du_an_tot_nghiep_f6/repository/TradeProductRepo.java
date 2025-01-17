package com.poly.du_an_tot_nghiep_f6.repository;

import com.poly.du_an_tot_nghiep_f6.entity.BillDetail;
import com.poly.du_an_tot_nghiep_f6.entity.Trade;
import com.poly.du_an_tot_nghiep_f6.entity.TradeProduct;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TradeProductRepo extends JpaRepository<TradeProduct, Integer> {
    List<TradeProduct> findByTrade(Trade trade);
    TradeProduct findByBillDetail(BillDetail billDetail);
}
