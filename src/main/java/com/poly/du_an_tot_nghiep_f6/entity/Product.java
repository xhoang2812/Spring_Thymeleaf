package com.poly.du_an_tot_nghiep_f6.entity;

import jakarta.persistence.*;
import lombok.*;

import lombok.experimental.FieldDefaults;


import java.util.Date;

@Entity
@Table(name = "product")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor

@FieldDefaults(level = AccessLevel.PRIVATE)
public class  Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    Integer id;

    @Column(name = "code",nullable = false)
    private String code;

    @Column(name = "name", columnDefinition = "nvarchar(225)", nullable = false)
    String name;

    @ManyToOne
    @JoinColumn(name = "id_category", referencedColumnName = "id")
    Category category;

    @ManyToOne
    @JoinColumn(name = "id_material", referencedColumnName = "id")
    Material material;

    @ManyToOne
    @JoinColumn(name = "id_brand",referencedColumnName = "id")
    Brand brand;

    @ManyToOne
    @JoinColumn(name = "id_style",referencedColumnName = "id")
    Style style;

    @Column(name = "description", columnDefinition = "nvarchar(1000)", nullable = true)
    String description;

    @Column(name = "gender", columnDefinition = "nvarchar(225)", nullable = true)
    String gender;

    @Column(name = "date_create",nullable = true)
    Date dateCreate;

    @Column(name = "date_update",nullable = true)
    Date dateUpdate;

    @Column(name = "status", nullable = true)
    Integer status;

}
