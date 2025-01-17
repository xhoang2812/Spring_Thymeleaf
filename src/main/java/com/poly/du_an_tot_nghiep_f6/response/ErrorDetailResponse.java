package com.poly.du_an_tot_nghiep_f6.response;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.poly.du_an_tot_nghiep_f6.entity.ErrorList;
import com.poly.du_an_tot_nghiep_f6.entity.TradeProduct;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ErrorDetailResponse {
    private int id;
    private TradeProduct tradeProduct;
    private String description;
    private Date createDate;
}
