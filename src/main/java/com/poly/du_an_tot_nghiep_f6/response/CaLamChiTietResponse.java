package com.poly.du_an_tot_nghiep_f6.response;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.poly.du_an_tot_nghiep_f6.entity.CaLam;
import com.poly.du_an_tot_nghiep_f6.entity.Employee;
import jakarta.persistence.Column;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CaLamChiTietResponse {
    private Integer id;
    private String tenCa;
    private CaLam calam;
    private List<Employee> employees = new ArrayList<>();
    private String thoiGianBatDauCa;
    private String thoiGianKetThucCa;
    private String trangThai;
}
