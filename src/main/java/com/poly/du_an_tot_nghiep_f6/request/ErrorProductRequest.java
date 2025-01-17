package com.poly.du_an_tot_nghiep_f6.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ErrorProductRequest {
    private int id;
    private String reason;
}
