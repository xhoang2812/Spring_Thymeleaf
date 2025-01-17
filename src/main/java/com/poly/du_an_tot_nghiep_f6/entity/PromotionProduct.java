package com.poly.du_an_tot_nghiep_f6.entity;

import jakarta.persistence.*;
import lombok.Data;


@Entity
@Table(name = "promotion_product")
@Data
public class PromotionProduct {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "promotion_id", nullable = false)
    private Promotion promotion;

    @ManyToOne
    @JoinColumn(name = "product_detail_id", nullable = false)
    private ProductDetail productDetail;

    // Các thuộc tính khác nếu cần
    // Ví dụ: discountValue, promotionStatus, etc.

    public PromotionProduct() {}

    // Constructor, Getters, Setters
}
