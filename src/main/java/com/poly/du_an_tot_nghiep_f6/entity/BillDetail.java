package com.poly.du_an_tot_nghiep_f6.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Proxy;

@Entity
@Table(name = "bill_detail")
@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Proxy(lazy = false)
public class BillDetail {

    // Chi Tiết hóa đơn
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    int id;

    @ManyToOne
    @JoinColumn(name = "id_bill")
    Bill bill;

    @ManyToOne
    @JoinColumn(name = "id_product_detail")
    ProductDetail productDetail;

    int quantity;
    Double price;
    Double intoMoney;
    Double oldPrice;

    @ManyToOne
    @JoinColumn
    PromotionDetail promotionDetail;

    @Column(columnDefinition = "nvarchar(max)")
    String description;
    boolean status;
}
