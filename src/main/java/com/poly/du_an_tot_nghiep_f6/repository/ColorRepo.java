package com.poly.du_an_tot_nghiep_f6.repository;

import com.poly.du_an_tot_nghiep_f6.entity.Color;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ColorRepo extends JpaRepository<Color, Integer> {

    Color findByName(String name);

    @Query("select c from Color c where c.status = 1 order by c.dateCreate desc")
    List<Color> findAllByStatusEquals();

    @Query("select c " +
            "from Color c join ProductDetail p on c.id = p.color.id " +
            "where p.product.id = :id_product1 " +
            "group by c")
    List<Color> findAllByProductId(@Param("id_product1") Integer idProduct);
}
