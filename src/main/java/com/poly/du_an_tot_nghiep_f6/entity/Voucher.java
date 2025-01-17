package com.poly.du_an_tot_nghiep_f6.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.Proxy;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Proxy(lazy = false)
public class Voucher {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(columnDefinition = "nvarchar(max)")
    private String name;
    private String code;
    private int discount;
    private int quantity;
    private int quantityUsed;
    private Date dateStart;
    private Date dateEnd;
    private int minimumOrder;
    private int maximumReduction;
    @Column(columnDefinition = "nvarchar(max)")
    private String description; // dang dien ra || chua dien ra || ket thuc
    @Column(columnDefinition = "nvarchar(max)")
    private String condition;
    private boolean styleVoucher;
    private boolean formVoucher; // cong khai || rieng tu
    private boolean status; // trang thai (kich hoat || da huy)

}
