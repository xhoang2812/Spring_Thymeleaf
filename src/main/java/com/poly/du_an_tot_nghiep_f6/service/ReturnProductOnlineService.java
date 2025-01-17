package com.poly.du_an_tot_nghiep_f6.service;

import com.poly.du_an_tot_nghiep_f6.entity.Bill;
import com.poly.du_an_tot_nghiep_f6.entity.BillDetail;
import com.poly.du_an_tot_nghiep_f6.entity.Trade;
import com.poly.du_an_tot_nghiep_f6.entity.TradeProduct;
import com.poly.du_an_tot_nghiep_f6.repository.BillDetailRepo;
import com.poly.du_an_tot_nghiep_f6.repository.BillRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReturnProductOnlineService {
    private final BillRepo billRepo;
    private final BillDetailRepo billDetailRepo;

    List<Trade> trades = new ArrayList<>();
    List<TradeProduct> tradeProductsList = new ArrayList<>();



    public Trade createTradeRequest(Bill bill, List<TradeProduct> tradeProducts){
        for(Trade trade : trades){
            if(trade.getBill().equals(bill)){
                return null;
            }
        }
        int i = trades.size();
        Trade trade = new Trade();
        trade.setId(i+1);
        trade.setBill(bill);

        for(TradeProduct tradeProduct : tradeProducts) {
            TradeProduct tradeProduct1 = new TradeProduct();
            BeanUtils.copyProperties(tradeProduct, tradeProduct1);
            tradeProduct1.setTrade(trade);
            tradeProductsList.add(tradeProduct1);
        }
        trades.add(trade);
        return trade;
    }


    public List<TradeProduct> getTradeProductByTrade(Bill bill){
        List<TradeProduct> tradeProducts = new ArrayList<>();
        for(Trade trade : trades){
            if(trade.getBill().getId() == bill.getId()){
                for(TradeProduct tradeProduct : tradeProductsList){
                    if(tradeProduct.getTrade().equals(trade)){
                        tradeProducts.add(tradeProduct);
                    }
                }
            }
        }
        return tradeProducts;
    }

    public Bill reloadBill(Bill bill){
        for(Trade trade : trades){
            if(bill.getId() == trade.getBill().getId()){
                trade.setBill(bill);
            }
        }
        return bill;
    }


}
