package com.poly.du_an_tot_nghiep_f6.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.poly.du_an_tot_nghiep_f6.entity.BillDetail;
import com.poly.du_an_tot_nghiep_f6.entity.TradeProductItem;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TradeProductRequest {
    private int billDetail;
    private String reason;
    private String videoUrl;
}
