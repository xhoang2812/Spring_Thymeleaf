package com.poly.du_an_tot_nghiep_f6.repository;

import com.poly.du_an_tot_nghiep_f6.entity.CaLam;
import com.poly.du_an_tot_nghiep_f6.entity.CaLamChiTiet;
import com.poly.du_an_tot_nghiep_f6.entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CaLamChiTietRepo extends JpaRepository<CaLamChiTiet, Integer> {
    List<CaLamChiTiet> findByCalam(CaLam caLam);
    List<CaLamChiTiet> findByEmployees(List<Employee> employees);
}
