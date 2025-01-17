package com.poly.du_an_tot_nghiep_f6.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class TradeProduct {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @OneToOne
    private BillDetail billDetail;
    @OneToMany
    private List<TradeProductItem> tradeProductItems = new ArrayList<>();
    @ManyToOne
    private Trade trade;
    private int quantity;
    private int totalMoney;
    @Column(columnDefinition = "nvarchar(max)")
    private String description;
    @Column(columnDefinition = "nvarchar(max)")
    private String videoLink;
    @Column(columnDefinition = "nvarchar(max)")
    private String reason;
    private boolean status;
}
