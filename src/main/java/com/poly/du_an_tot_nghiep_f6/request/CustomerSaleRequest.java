package com.poly.du_an_tot_nghiep_f6.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CustomerSaleRequest {
    String name;
    String phone;
    String email;
    String gender;
    LocalDate birthDay;
}
