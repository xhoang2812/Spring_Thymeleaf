package com.poly.du_an_tot_nghiep_f6.repository;

import com.poly.du_an_tot_nghiep_f6.entity.Product;
import com.poly.du_an_tot_nghiep_f6.response.ProductHomeNewResponse;
import com.poly.du_an_tot_nghiep_f6.response.ProductHomeRespone;
import com.poly.du_an_tot_nghiep_f6.response.ProductResponse;
import com.poly.du_an_tot_nghiep_f6.response.ProductResponseAdmin;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepo extends JpaRepository<Product, Integer> {
    Product findByName(String name);
    Product findByCode(String code);
    List<Product> findAllByStatusEquals(Integer status);

    @Query("SELECT new ProductResponse(pr.id, pr.name, cate.name, br.name, mate.name, st.name, MAX(prdt.price)," +
            " SUM(prdt.quantity), MIN(img.url1)) " +
            "FROM Brand br " +
            "JOIN Product pr ON br.id = pr.brand.id " +
            "JOIN Style st ON st.id = pr.style.id " +
            "JOIN Category cate ON cate.id = pr.category.id " +
            "JOIN Material mate ON mate.id = pr.material.id " +
            "JOIN ProductDetail prdt ON prdt.product.id = pr.id " +
            "JOIN Image img ON img.id = prdt.image.id " +
            "JOIN Size sz ON sz.id = prdt.size.id " +
            "JOIN Color cl ON cl.id = prdt.color.id " +
            "WHERE pr.status = 1 " +
            "AND (:brandId IS NULL OR br.id = :brandId) " +
            "AND (:styleId IS NULL OR st.id = :styleId) " +
            "AND (:materialId IS NULL OR mate.id = :materialId) " +
            "AND (:sizeId IS NULL OR sz.id = :sizeId) " +
            "AND (:colorId IS NULL OR cl.id = :colorId) " +
            "AND (:categoryId IS NULL OR cate.id = :categoryId) " +
            "AND (:timTheoTen IS NULL OR pr.name LIKE CONCAT('%', :timTheoTen, '%')) " +
            "GROUP BY pr.id, pr.name, cate.name, br.name, mate.name, st.name " +
            "ORDER BY CASE WHEN :sort = 'asc' THEN MAX(prdt.price) END ASC, " +
            "CASE WHEN :sort = 'desc' THEN MAX(prdt.price) END DESC")
    Page<ProductResponse> findAllProducctResponse(Pageable pageable,
                                                  @Param("brandId") Integer brandId, @Param("styleId") Integer styleId,
                                                  @Param("materialId") Integer materialId, @Param("sizeId") Integer sizeId,
                                                  @Param("colorId") Integer colorId, @Param("categoryId") Integer categoryId,
                                                  @Param("sort") String sort, @Param("timTheoTen") String timTheoTen);


    @Query("SELECT new com.poly.du_an_tot_nghiep_f6.response.ProductResponseAdmin(pr.id, pr.name,pr.gender, cate.name, mate.name,br.name, st.name," +
            " SUM(prdt.quantity), pr.status) " +
            "FROM Brand br " +
            "JOIN Product pr ON br.id = pr.brand.id " +
            "JOIN Style st ON st.id = pr.style.id " +
            "JOIN Category cate ON cate.id = pr.category.id " +
            "JOIN Material mate ON mate.id = pr.material.id " +
            "JOIN ProductDetail prdt ON prdt.product.id = pr.id " +
            "group by pr.id, pr.name, pr.gender, cate.name, mate.name, br.name, st.name, pr.status, pr.dateCreate " +
            "order by pr.dateCreate desc")
    List<ProductResponseAdmin> findAllProducctResponse1();


    @Query("SELECT new com.poly.du_an_tot_nghiep_f6.response.ProductHomeRespone(pr.id, pr.name, SUM(prdt.quantity), count(bd.productDetail.id), max(img.url1), max(prdt.price)) " +
            "FROM  Product pr " +
            "JOIN ProductDetail prdt ON prdt.product.id = pr.id " +
            "left join BillDetail bd on prdt.id = bd.productDetail.id " +
            "join Image img on img.id = prdt.image.id " +
            "where pr.status = 1 " +
            "group by pr.id, pr.name, pr.dateCreate, pr.status " +
            "having count(bd.productDetail.id) > 0 " +
            "order by count(bd.productDetail.id) desc")
    List<ProductHomeRespone> findAllProducctResponseHomeCount(Pageable pageable);


    @Query("SELECT new com.poly.du_an_tot_nghiep_f6.response.ProductHomeNewResponse(pr.id, pr.name, SUM(prdt.quantity),max(img.url1), max(prdt.price)) " +
            "FROM  Product pr " +
            "JOIN ProductDetail prdt ON prdt.product.id = pr.id " +
            "join Image img on img.id = prdt.image.id " +
            "where pr.status = 1 " +
            "group by pr.id, pr.name, pr.dateCreate, pr.status " +
            "order by pr.dateCreate desc")
    List<ProductHomeNewResponse> findAllProducctResponseHomeNew(Pageable pageable);








    @Query("select new ProductResponse(cate.name, br.name, mate.name,st.name, pr.description, pr.gender, pr.id, pr.name)" +
            "from Brand br join Product pr on br.id = pr.brand.id " +
            "join Style st on st.id = pr.style.id " +
            "join Category cate on cate.id = pr.category.id " +
            "join Material mate on mate.id = pr.material.id " +
            "join ProductDetail prdt on prdt.product.id = pr.id " +
            "join Image img on img.id = prdt.image.id " +
            "join Size sz on sz.id = prdt.size.id " +
            "join Color cl on cl.id = prdt.color.id " +
            "where pr.status = 1 and pr.id = :id_product " +
            "group by pr.id,pr.name, cate.name, br.name, mate.name, st.name, pr.description, pr.gender")
    ProductResponse findProducctResponseByIdProdcut(@Param("id_product") Integer idProduct);


}
