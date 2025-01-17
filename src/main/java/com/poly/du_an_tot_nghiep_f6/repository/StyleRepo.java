package com.poly.du_an_tot_nghiep_f6.repository;

import com.poly.du_an_tot_nghiep_f6.entity.Style;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StyleRepo extends JpaRepository<Style, Integer> {

    Style findByName(String name);

    @Query("select s from Style s where s.status = 1 order by s.dateCreate desc")
    List<Style> findAllByStatusEqualsAndOrderByDateCreateDesc();
}
