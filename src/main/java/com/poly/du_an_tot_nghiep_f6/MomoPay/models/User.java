package com.poly.du_an_tot_nghiep_f6.MomoPay.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class User {
    private String name;
    private String phoneNumber;
    private String email;
}
