package com.poly.du_an_tot_nghiep_f6.entity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Date;

@Entity
@Table(name = "image")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Image {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;

    @Column(name = "code",nullable = false)
    private String code;

    @Column(name = "url1",nullable = true)
    private String url1;

    @Column(name = "url2",nullable = true)
    private String url2;

    @Column(name = "url3",nullable = true)
    private String url3;

    @Column(name = "date_create",nullable = true)
    Date dateCreate;

    @Column(name = "date_update",nullable = true)
    Date dateUpdate;

    @Column(name = "status", nullable = false)
    Integer status;

}
