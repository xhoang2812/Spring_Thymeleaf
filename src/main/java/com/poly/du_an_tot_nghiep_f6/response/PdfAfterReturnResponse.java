package com.poly.du_an_tot_nghiep_f6.response;

import com.poly.du_an_tot_nghiep_f6.entity.BillDetail;
import com.poly.du_an_tot_nghiep_f6.entity.Trade;
import com.poly.du_an_tot_nghiep_f6.entity.TradeProductItem;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PdfAfterReturnResponse {
    private Trade trade;
    private List<TradeProductItemResponse> tradeProductItemResponses;
    private List<BillDetail> billDetails;
}
