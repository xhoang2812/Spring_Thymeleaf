package com.poly.du_an_tot_nghiep_f6.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Entity
@Table(name = "bill")
@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Bill {
    // Hóa đơn
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    int id;
    @ManyToOne
    @JoinColumn(name = "id_employee")
    Employee employee;

    @ManyToOne
    @JoinColumn(name = "id_customer")
    Customer customer;

    Double totalPrice = 0.;
    Double shipPrice = 0.;

    @ManyToOne
    @JoinColumn(name = "id_voucher")
    Voucher voucher;

    @ManyToOne
    @JoinColumn(name = "id_address")
    Address address;

    Date dateCreate;
    String descriptionShip;

    @ManyToOne
    @JoinColumn(name = "id_payment_method")
    PaymentMethods paymentMethods;

    Boolean paymentType;
    int status;
    Integer tienGiam = 0;
    String descriptionBill;
}
