package com.poly.du_an_tot_nghiep_f6.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "cart_detail")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CartDetail {
    //Giỏ hàng chi tiết
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    int id;

    @ManyToOne
    @JoinColumn(name = "id_cart")
    private Cart cart;

    @ManyToOne
    @JoinColumn(name = "id_product_detail")
    private ProductDetail productDetail;

    @Column(name = "price")
    private Double price;

    @Column(name = "quantity")
    private int quantity;

    @Column(name = "total")
    private Double total;

    @Column(name = "havePromotion")
    private boolean havePromotion;

}
