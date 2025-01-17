package com.poly.du_an_tot_nghiep_f6.controller.ErrorProduct;

import com.poly.du_an_tot_nghiep_f6.entity.ProductDetail;
import com.poly.du_an_tot_nghiep_f6.entity.Trade;
import com.poly.du_an_tot_nghiep_f6.entity.TradeProduct;
import com.poly.du_an_tot_nghiep_f6.entity.TradeProductItem;
import com.poly.du_an_tot_nghiep_f6.repository.ErrorProductDetailRepo;
import com.poly.du_an_tot_nghiep_f6.repository.ProductDetailRepo;
import com.poly.du_an_tot_nghiep_f6.repository.TradeProductRepo;
import com.poly.du_an_tot_nghiep_f6.repository.TradeRepo;
import com.poly.du_an_tot_nghiep_f6.response.ErrResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/san-pham")
@RequiredArgsConstructor
public class ErrorProductController {
    private final ProductDetailRepo productDetailRepo;
    private final ErrorProductDetailRepo errorProductDetailRepo;
    private final TradeRepo tradeRepo;
    private final TradeProductRepo tradeProductRepo;

    @GetMapping("/error-product")
    public String getErrorProductDetails(Model model){
        List<Trade> trades = tradeRepo.findAll().stream()
                .filter(trade -> trade.getBill() != null).toList();

        List<TradeProductItem> tradeProductItems = new ArrayList<>();
        for(Trade trade : trades){
            List<TradeProduct> tradeProducts = tradeProductRepo.findByTrade(trade);
            for(TradeProduct tradeProduct : tradeProducts){
                tradeProductItems.addAll(tradeProduct.getTradeProductItems());
            }
        }
        tradeProductItems = tradeProductItems.stream()
                .filter(tradeProductItem -> tradeProductItem.isStatus() && !tradeProductItem.isError())
                .toList();

        List<ErrResponse> errResponses = new ArrayList<>();
        for (TradeProductItem tradeProductItem : tradeProductItems) {
            ErrResponse errResponse = new ErrResponse();
            errResponse.setProductDetail(tradeProductItem.getTradeProduct().getBillDetail().getProductDetail());
            errResponse.setQuantity(1);
            errResponse.setReason(tradeProductItem.getDescription());
            errResponses.add(errResponse);
        }

        Map<ProductDetail, Integer> groupedResponses = new HashMap<>();

        for (ErrResponse errResponse : errResponses) {
            ProductDetail productDetail = errResponse.getProductDetail();
            int quantity = errResponse.getQuantity();

            groupedResponses.put(productDetail, groupedResponses.getOrDefault(productDetail, 0) + quantity);
        }

        List<ErrResponse> result = new ArrayList<>();
        for (Map.Entry<ProductDetail, Integer> productEntry : groupedResponses.entrySet()) {
            ProductDetail productDetail = productEntry.getKey();
            int totalQuantity = productEntry.getValue();

            ErrResponse groupedErrResponse = new ErrResponse(
                    productDetail,
                    totalQuantity,
                    ""
            );

            result.add(groupedErrResponse);
        }
        model.addAttribute("errorProducts",result);
        return "admin/errorProduct/errorProduct";
    }
}
