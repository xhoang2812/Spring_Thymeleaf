package com.poly.du_an_tot_nghiep_f6.controller.ErrorProduct;

import com.poly.du_an_tot_nghiep_f6.entity.ProductDetail;
import com.poly.du_an_tot_nghiep_f6.entity.TradeProductItem;
import com.poly.du_an_tot_nghiep_f6.repository.ProductDetailRepo;
import com.poly.du_an_tot_nghiep_f6.repository.TradeProductItemRepo;
import com.poly.du_an_tot_nghiep_f6.request.ErrorProductRequest;
import com.poly.du_an_tot_nghiep_f6.response.ErrorDetailResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/errorProduct")
public class ErrorProductControllerApi {
    private final TradeProductItemRepo tradeProductItemRepo;
    private final ProductDetailRepo productDetailRepo;

    @GetMapping("/{id}")
    public List<ErrorDetailResponse> getErrorProduct(@PathVariable Integer id){
        ProductDetail productDetail = productDetailRepo.getReferenceById(id);
        List<TradeProductItem> tradeProductItems = tradeProductItemRepo
                .findByTradeProduct_BillDetail_ProductDetail
                        (productDetail).stream()
                .filter(tradeProductItem -> tradeProductItem.isStatus() && !tradeProductItem.isError())
                .toList();
        List<ErrorDetailResponse> errorDetailResponses = new ArrayList<>();
        for(TradeProductItem tradeProductItem : tradeProductItems){
            ErrorDetailResponse errorDetailResponse = new ErrorDetailResponse();
            errorDetailResponse.setId(tradeProductItem.getId());
            errorDetailResponse.setDescription(tradeProductItem.getDescription());
            errorDetailResponse.setTradeProduct(tradeProductItem.getTradeProduct());
            errorDetailResponse.setCreateDate(tradeProductItem.getCreateDate());
            errorDetailResponses.add(errorDetailResponse);
        }

        return errorDetailResponses.stream()
                .sorted((o1, o2) -> Long.compare(o2.getId(), o1.getId()))
                .collect(Collectors.toList());
    }
}
