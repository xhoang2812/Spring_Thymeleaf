package com.poly.du_an_tot_nghiep_f6.repository;

import com.poly.du_an_tot_nghiep_f6.entity.CaLam;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.Date;

public interface CaLamRepo extends JpaRepository<CaLam, Integer> {
    CaLam findByNgayLam(Date date);
}
