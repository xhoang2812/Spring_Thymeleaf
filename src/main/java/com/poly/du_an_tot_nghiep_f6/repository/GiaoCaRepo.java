package com.poly.du_an_tot_nghiep_f6.repository;

import com.poly.du_an_tot_nghiep_f6.entity.Employee;
import com.poly.du_an_tot_nghiep_f6.entity.GiaoCa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface GiaoCaRepo extends JpaRepository<GiaoCa, Long> {
    GiaoCa findByMagiaoca(String magiaoca);

    @Query("SELECT g.employee FROM GiaoCa g ORDER BY g.thoigianvaoca DESC")
    public List<Employee> getEmployee();

    @Query("SELECT g FROM GiaoCa g ORDER BY g.thoigianvaoca DESC")
    public List<GiaoCa> getCaLam();
}
