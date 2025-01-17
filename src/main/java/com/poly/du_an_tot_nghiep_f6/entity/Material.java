package com.poly.du_an_tot_nghiep_f6.entity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Date;

@Entity
@Table(name = "material")
@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Material {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    Integer id;

    @Column(name = "name", nullable = false,columnDefinition = "nvarchar(225)")
    String name;

    @Column(name = "date_create",nullable = true)
    Date dateCreate;

    @Column(name = "date_update",nullable = true)
    Date dateUpdate;

    @Column(name = "status", nullable = false)
    Integer status;
}
