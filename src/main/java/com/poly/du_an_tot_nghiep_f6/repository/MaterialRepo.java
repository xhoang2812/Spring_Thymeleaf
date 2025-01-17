package com.poly.du_an_tot_nghiep_f6.repository;

import com.poly.du_an_tot_nghiep_f6.entity.Material;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MaterialRepo extends JpaRepository<Material, Integer> {
    Material findByName(String name);
    @Query("select m from Material m where m.status = 1 order by m.dateCreate desc")
    List<Material> findAllByStatusAndOrderByDateCreateDesc();
}
