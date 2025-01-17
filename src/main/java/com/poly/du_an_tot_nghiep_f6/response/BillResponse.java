package com.poly.du_an_tot_nghiep_f6.response;

import com.poly.du_an_tot_nghiep_f6.entity.Address;
import com.poly.du_an_tot_nghiep_f6.entity.PaymentMethods;
import com.poly.du_an_tot_nghiep_f6.entity.Voucher;
import lombok.Data;

import java.util.Date;


@Data
public class BillResponse {
    private int id;
    private String nameCustomer;
    private String nameEmployee;
    private Double totalPrice;
    private Integer paymentMethodId;
    private Double reducedPrice;
    private Double shipPrice;
    private String dateCreate;
    private Integer status;
    private String descriptionShip;
    private Boolean paymentType;
    private String addressDetail;
    private Integer idCustomer;
    private String  recipientName;
    private String  recipientPhone;
    private String descriptionBill;

    public BillResponse(int id, String customerName, String nameEmployee, Double totalPrice, Integer paymentMethodId, Voucher voucher, Double shipPrice, Date dateCreate, int status, String descriptionShip, Boolean paymentType, Address address,Integer idCustomer, String descriptionBill) {
        this.id = id;
        this.nameEmployee = nameEmployee;
        this.totalPrice = totalPrice;
        this.paymentMethodId = paymentMethodId;
        this.shipPrice = shipPrice;
        this.dateCreate = dateCreate.toString();
        this.status = status;
        this.descriptionShip = descriptionShip;
        this.descriptionBill = descriptionBill;
        this.paymentType = paymentType;
        if (customerName == null || customerName.isEmpty()) {
            this.nameCustomer = "Khách lẻ";
        } else {
            this.nameCustomer = customerName;
        }
        if (voucher == null) {
            this.reducedPrice = 0.;
        } else {
            if (voucher.isStyleVoucher()) {
                Double reducedPrice0 = voucher.getDiscount() * totalPrice / 100;
                if (reducedPrice0 < voucher.getMaximumReduction()) {
                    this.reducedPrice = voucher.getDiscount() * totalPrice / 100;
                } else {
                    this.reducedPrice = (double) voucher.getMaximumReduction();
                }
            } else {
                if(totalPrice<voucher.getDiscount()) {
                    this.reducedPrice = totalPrice;
                }else {
                    this.reducedPrice = (double) voucher.getDiscount();
                }
            }
        }
        if (address != null) {
            this.addressDetail = address.getAddreseDetail() + " - " + address.getWard() + " - " + address.getDistrict() + " - " + address.getProvince();
            this.recipientName=address.getNameRecipient();
            this.recipientPhone=address.getPhone();
        }else{
            this.addressDetail = "";
        }
        this.idCustomer = idCustomer;
    }
}
