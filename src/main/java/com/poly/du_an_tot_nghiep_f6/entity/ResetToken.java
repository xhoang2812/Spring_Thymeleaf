package com.poly.du_an_tot_nghiep_f6.entity;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class ResetToken {
    private String email;
    private String token;
    private String newPassword;
    private String userType;
//    private LocalDateTime sentTime;


    public ResetToken(String email, String token, String newPassword, String userType) {
        this.email = email;
        this.token = token;
        this.newPassword = newPassword;
        this.userType = userType;
    }
}
