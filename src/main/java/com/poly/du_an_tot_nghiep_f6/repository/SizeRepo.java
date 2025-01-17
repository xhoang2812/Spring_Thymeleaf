package com.poly.du_an_tot_nghiep_f6.repository;


import com.poly.du_an_tot_nghiep_f6.entity.Size;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SizeRepo extends JpaRepository<Size, Integer> {

     Size findByName(String name);

     @Query("select s from Size s where s.status = 1 order by s.dateCreate desc")
    List<Size> findAllByStatusEquals();

    @Query("select s " +
            "from Size s join ProductDetail p on s.id = p.size.id " +
            "where p.product.id = :id_product1 " +
            "group by s ")
    List<Size> findAllByProductId(@Param("id_product1") Integer idProduct);
}
