package com.poly.du_an_tot_nghiep_f6.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ErrorProductDetail {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @ManyToOne
    private ProductDetail productDetail;
    @ManyToOne
    private Trade trade;
    private int quantity;
    @Column(columnDefinition = "nvarchar(max)")
    private String describe;
}
