package com.poly.du_an_tot_nghiep_f6.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Trade {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @OneToOne
    private Bill bill;
    private int oldProductDetailMoney; //tiền hàng cũ
    private int productDetailMoney; //tiền thanh toán
    private int backProductDetailMoney; //tổng tiền trả hàng
    private int shipMoney; // tiền ship
    private int oldVoucherMoney;
    private int newVoucherMoney;
    private int payMoney; // tiền trả khách
    @Column(columnDefinition = "nvarchar(max)")
    private String description;
    @Column(columnDefinition = "nvarchar(max)")
    private String nameBank;
    @Column(columnDefinition = "nvarchar(max)")
    private String bankInfo;
    @Column(columnDefinition = "nvarchar(max)")
    private String userInfo;
    @Column(columnDefinition = "nvarchar(max)")
    private String qrInfo;
    @Column(columnDefinition = "nvarchar(max)")
    private String qrPay;
    private String email;
    private String phoneInfo;
    @ManyToOne Address address;
    private boolean status; // True => online False => offline
    private Date requestDate;
    private Date responseDate;
//    private boolean formTrade; //True => huỷ  False => success

}
