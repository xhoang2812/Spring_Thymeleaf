package com.poly.du_an_tot_nghiep_f6.MomoPay.models;

import com.poly.du_an_tot_nghiep_f6.MomoPay.enums.ConfirmRequestType;

public class ConfirmResponse extends Response {
    private Long amount;
    private Long transId;
    private String requestId;
    private ConfirmRequestType requestType;
}
