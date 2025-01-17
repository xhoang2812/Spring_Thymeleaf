package com.poly.du_an_tot_nghiep_f6.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Entity
@Table(name="bill_history")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class BillHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;

    @ManyToOne
    @JoinColumn(name = "id_employee")
    Employee employee;


    @ManyToOne
    @JoinColumn(name = "id_bill")
    Bill bill;
    @Column(name = "description", columnDefinition = "nvarchar(225)", nullable = true)
    String description;
    Integer status;
    Integer statusOld;
    Date dateUpdate= new Date();
}
