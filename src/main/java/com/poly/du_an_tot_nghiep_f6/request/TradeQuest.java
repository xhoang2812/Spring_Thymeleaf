package com.poly.du_an_tot_nghiep_f6.request;

import com.poly.du_an_tot_nghiep_f6.entity.Trade;
import com.poly.du_an_tot_nghiep_f6.entity.TradeProductItem;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
public class TradeQuest {
    private Trade trade;
    private List<TradeProductItem> tradeProductItems;
}
