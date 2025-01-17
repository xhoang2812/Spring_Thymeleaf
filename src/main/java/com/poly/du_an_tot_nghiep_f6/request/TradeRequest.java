package com.poly.du_an_tot_nghiep_f6.request;

import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TradeRequest {
    private String nameBank;
    private String bankInfo;
    private String userInfo;
    private String qrInfo;
}
