package com.poly.du_an_tot_nghiep_f6.repository;

import com.poly.du_an_tot_nghiep_f6.entity.Brand;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BrandRepo extends JpaRepository<Brand, Integer> {
    Brand findByName(String name);
    @Query("select b from Brand b where b.status = 1 order by b.dateCreate desc")
    List<Brand> findAllByStatusEquals();
}
