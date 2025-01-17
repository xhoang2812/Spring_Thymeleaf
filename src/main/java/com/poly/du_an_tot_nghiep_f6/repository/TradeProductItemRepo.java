package com.poly.du_an_tot_nghiep_f6.repository;

import com.poly.du_an_tot_nghiep_f6.entity.ProductDetail;
import com.poly.du_an_tot_nghiep_f6.entity.TradeProduct;
import com.poly.du_an_tot_nghiep_f6.entity.TradeProductItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TradeProductItemRepo extends JpaRepository<TradeProductItem, Integer> {
    List<TradeProductItem> findByTradeProduct(TradeProduct tradeProduct);
    List<TradeProductItem> findByTradeProduct_BillDetail_ProductDetail(ProductDetail productDetail);
}
