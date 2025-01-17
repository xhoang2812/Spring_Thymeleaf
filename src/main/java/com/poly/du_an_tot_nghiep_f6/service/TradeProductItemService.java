package com.poly.du_an_tot_nghiep_f6.service;

import com.poly.du_an_tot_nghiep_f6.entity.ProductDetail;
import com.poly.du_an_tot_nghiep_f6.entity.TradeProduct;
import com.poly.du_an_tot_nghiep_f6.entity.TradeProductItem;
import com.poly.du_an_tot_nghiep_f6.repository.ProductDetailRepo;
import com.poly.du_an_tot_nghiep_f6.repository.TradeProductItemRepo;
import com.poly.du_an_tot_nghiep_f6.repository.TradeProductRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TradeProductItemService {
    private final TradeProductItemRepo tradeProductItemRepository;
    private final TradeProductRepo tradeProductRepository;
    private final ProductDetailRepo productDetailRepository;
//    public TradeProductItem addProductDetailToTradeProduct(TradeProductItem tradeProductItem, Integer tradeProductId){
//        TradeProduct tradeProduct = tradeProductRepository.findById(tradeProductId)
//                .orElseThrow(() -> new RuntimeException("Trade product not found"));
//        ProductDetail productDetail = productDetailRepository.findById(tradeProductItem.getProductDetail().getId())
//                .orElseThrow(() -> new RuntimeException("Product detail not found"));
//        for(TradeProductItem trade: tradeProduct.getTradeProductItems()){
//            if(trade.getProductDetail().getId().equals(productDetail.getId())){
//                trade.setQuantity(trade.getQuantity()+1);
//                trade.setTotalMoney( (int) (trade.getQuantity() * productDetail.getPrice()));
//                return tradeProductItemRepository.save(trade);
//            }
//        }
//        tradeProductItem.setProductDetail(productDetail);
//        tradeProductItem.setTradeProduct(tradeProduct);
//        tradeProductItem.setQuantity(1);
//        tradeProductItem.setTotalMoney( (int) (tradeProductItem.getQuantity() * productDetail.getPrice()));
//        TradeProductItem saveTrade = tradeProductItemRepository.save(tradeProductItem);
//        tradeProduct.getTradeProductItems().add(tradeProductItem);
//        tradeProductRepository.save(tradeProduct);
//        return saveTrade;
//    }
}
