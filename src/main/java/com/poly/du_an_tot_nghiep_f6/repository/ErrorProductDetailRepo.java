package com.poly.du_an_tot_nghiep_f6.repository;

import com.poly.du_an_tot_nghiep_f6.entity.ErrorProductDetail;
import com.poly.du_an_tot_nghiep_f6.entity.Trade;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ErrorProductDetailRepo extends JpaRepository<ErrorProductDetail, Integer> {
    List<ErrorProductDetail> findByTrade(Trade trade);
}
