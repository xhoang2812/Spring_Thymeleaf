package com.poly.du_an_tot_nghiep_f6.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Entity
@Table(name = "address")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Address {
    //Địa Chỉ
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;

    @ManyToOne
    @JoinColumn(name="id_customer")
    Customer customer;

    @Column(name = "name_recipient", columnDefinition = "NVARCHAR(225)")
    String nameRecipient;

    @Column(name = "phone")
    String phone;

    @Column(name = "province", columnDefinition = "NVARCHAR(225)")
    String province;

    @Column(name = "district", columnDefinition = "NVARCHAR(225)")
    String district;

    @Column(name = "ward", columnDefinition = "NVARCHAR(225)")
    String ward;

    @Column(name = "addrese_detail", columnDefinition = "NVARCHAR(225)")
    String addreseDetail;

    @Column(name = "created_date", updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    Date dateCreate;

    @Column(name = "updated_date")
    @Temporal(TemporalType.TIMESTAMP)
    Date dateUpdate;

    @Column(name = "is_default")
    boolean isDefault;

    @Column(name = "email", columnDefinition = "VARCHAR(225)", nullable = true)
    String email;

    @PrePersist
    protected void onCreate() {
        dateCreate = new Date(); // Thời gian hiện tại
    }

    @PreUpdate
    protected void onUpdate() {

        dateUpdate = new Date(); // Cập nhật thời gian hiện tại khi chỉnh sửa
    }

    public Address(Integer id, Customer customer, String nameRecipient, String phone, String province, String district, String ward, String addreseDetail, Date dateCreate, Date dateUpdate, boolean isDefault) {
        this.id = id;
        this.customer = customer;
        this.nameRecipient = nameRecipient;
        this.phone = phone;
        this.province = province;
        this.district = district;
        this.ward = ward;
        this.addreseDetail = addreseDetail;
        this.dateCreate = dateCreate;
        this.dateUpdate = dateUpdate;
        this.isDefault = isDefault;
    }
}
