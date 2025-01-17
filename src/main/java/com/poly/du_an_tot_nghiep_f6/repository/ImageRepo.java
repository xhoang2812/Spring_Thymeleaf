package com.poly.du_an_tot_nghiep_f6.repository;

import com.poly.du_an_tot_nghiep_f6.entity.Image;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ImageRepo extends JpaRepository<Image, Integer> {

    Image findByCode(String code);

    List<Image> findAllByStatusEquals(Integer status);

    @Query("select img " +
            "from Image img join ProductDetail p on img.id = p .image.id " +
            "where p.product.id = :id_product1 " +
            "group by img")
    List<Image> findAllByProdcutId(@Param("id_product1") Integer idProduct);
}
