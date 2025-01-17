package com.poly.du_an_tot_nghiep_f6.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CaLam {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "d/M/yyyy")
    private Date ngayLam;
    @OneToMany(mappedBy = "calam", cascade = CascadeType.ALL, orphanRemoval = true)
    List<CaLamChiTiet> caLamChiTiets = new ArrayList<>();
}
