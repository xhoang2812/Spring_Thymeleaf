package com.poly.du_an_tot_nghiep_f6.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CaLamChiTiet {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(columnDefinition = "nvarchar(max)")
    private String tenCa;
    @ManyToOne
    @JsonIgnore
    private CaLam calam;
    @ManyToMany(fetch = FetchType.EAGER) // loading
    private List<Employee> employees = new ArrayList<>();
    private String thoiGianBatDauCa;
    private String thoiGianKetThucCa;
    @Column(columnDefinition = "nvarchar(max)")
    private String trangThai;
}
