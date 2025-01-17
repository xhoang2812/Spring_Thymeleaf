package com.poly.du_an_tot_nghiep_f6.entity;
import jakarta.persistence.*;
import lombok.*;

import lombok.experimental.FieldDefaults;

import java.util.Date;

@Entity
@Table(name = "size")
@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor

@FieldDefaults(level = AccessLevel.PRIVATE)

public class Size {
    //Kích Thước
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
