package com.poly.du_an_tot_nghiep_f6.entity;
import jakarta.persistence.*;
import lombok.*;

import lombok.experimental.FieldDefaults;


import java.util.Date;
@Getter
@Setter
@Entity
@AllArgsConstructor
@NoArgsConstructor

@Table(name = "color")
@FieldDefaults(level = AccessLevel.PRIVATE)

public class Color {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    Integer id;

    @Column(name = "name", columnDefinition = "nvarchar(225)", nullable = false)
    String name;

    @Column(name = "date_create",nullable = true)
    Date dateCreate;

    @Column(name = "date_update",nullable = true)
    Date dateUpdate;

    @Column(name = "status", nullable = false)
    Integer status;
}
