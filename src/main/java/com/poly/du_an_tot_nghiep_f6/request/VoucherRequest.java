package com.poly.du_an_tot_nghiep_f6.request;

import com.poly.du_an_tot_nghiep_f6.entity.Customer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class VoucherRequest {
    String name;
    String code;
    int discount;
    int quantity;
    Date dateStart;
    Date dateEnd;
    int min;
    int max;
    String description;
    String condition;
    private List<Customer> customer = new ArrayList<>();
    boolean styleVoucher;
    boolean formVoucher;
    boolean status;
}
