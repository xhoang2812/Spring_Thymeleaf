package com.poly.du_an_tot_nghiep_f6.repository;

import com.poly.du_an_tot_nghiep_f6.entity.*;
import com.poly.du_an_tot_nghiep_f6.response.ProductDetailResponse;
import com.poly.du_an_tot_nghiep_f6.response.ProductShopDetailResponse;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductDetailRepo extends JpaRepository<ProductDetail, Integer> {

    List<ProductDetail> findAllByOrderByDateCreateDesc();

    @Query("SELECT pd FROM ProductDetail pd JOIN FETCH pd.size JOIN FETCH pd.color WHERE pd.product.id = :productId")
    List<ProductDetail> findAllByProductId(@Param("productId") Integer productId);

    @Query("select new ProductShopDetailResponse(prdt.id, prdt.price, prdt.weight, prdt.product.id," +
            "case when pmt.status like '%ang%'  and  pmt.condition = true then pmtdl.giaMoi else 0 end , " +
            "prdt.image.url1, prdt.image.url2 , prdt.image.url3, pmt.id, prdt.quantity, prdt.status ) " +
            "from ProductDetail prdt join Image img on img.id = prdt.image.id " +
            "left join PromotionDetail pmtdl on prdt.promotionDetail.id = pmtdl.id " +
            "left join Promotion pmt on pmt.id = pmtdl.promotion.id " +
            "where prdt.product.id = :id_product and prdt.size.id = :id_size and prdt.color.id = :id_color")
    ProductShopDetailResponse findProShopResByIdColorAndIdSize(@Param("id_product") Integer idProduct,
                                                               @Param("id_size") Integer idSize,
                                                               @Param("id_color") Integer idColor);

    @Transactional
    @Modifying
    @Query("update ProductDetail pd " +
            "set pd.quantity = :quantity_1, pd.price = :price_1, pd.weight = :weight_1 " +
            "where pd.id = :id_1")
    int updateByProductId2(@Param("price_1") Double price_1,
                           @Param("quantity_1") Integer quantity_1,
                           @Param("weight_1") Double weight_1,
                           @Param("id_1") Integer id_1);

    @Transactional
    @Modifying
    @Query("update ProductDetail pd " +
            "set pd.quantity = :quantity_1, pd.price = :price_1, pd.weight = :weight_1, pd.image = :image_1," +
            " pd.qrCode = :qrcode_1" +
            " where pd.id = :id_1")
    int updateByProductId(@Param("image_1") Image image,
                          @Param("price_1") Double price_1,
                          @Param("quantity_1") Integer quantity_1,
                          @Param("weight_1") Double weight_1,
                          @Param("id_1") Integer id_1,
                          @Param("qrcode_1") String qrcode_1);

    @Query("select count(pd.color.id) from ProductDetail pd where pd.product.id = :id_product1 group by pd.color.id")
    List<Integer> getIdColorCount(@Param("id_product1") Integer id_product1);

    @Transactional
    @Modifying
    @Query("update ProductDetail pd set pd.status = :status where pd.product.id = :id_product")
    int updateStatusByIdProduct(@Param("id_product") Integer id_product,
                                @Param("status") Integer status);


    @Query("""
                SELECT new com.poly.du_an_tot_nghiep_f6.response.ProductDetailResponse(
                                p.id,
                                p.price,
                                p.quantity,
                                p.productCode,
                                p.product.name,
                                p.size.name,
                                p.color.name,
                                p.product.style.name,
                                p.product.category.name,
                                p.product.material.name,
                                p.product.brand.name,
                                COALESCE(promotions.giaMoi, 0),
                                COALESCE(promotions.promotion.quantity - promotions.promotion.quantityUse, 0),
                                COALESCE(promotions.promotion.name, '') ,
                                COALESCE(promotions.promotion.status, null)
                            )
                FROM ProductDetail p
                LEFT JOIN p.promotionDetail promotions ON promotions.promotion.condition=true
                WHERE\s
                    p.status = 1
            """)
    List<ProductDetailResponse> getProductResponse();



    @Query("SELECT new com.poly.du_an_tot_nghiep_f6.response.ProductDetailResponse(" +
           "p.id, " +
           "p.price," +
           "p.quantity," +
           "p.productCode," +
           "p.product.name," +
           "p.size.name," +
           "p.color.name," +
           "p.product.style.name," +
           "p.product.category.name," +
           "p.product.material.name," +
           "p.product.brand.name," +
           "COALESCE(promotions.giaMoi, 0),\n" +
           "COALESCE(promotions.promotion.quantity - promotions.promotion.quantityUse, 0),\n" +
           "COALESCE(promotions.promotion.name, ''),\n" +
            "COALESCE(promotions.promotion.status, null)) " +
           "FROM ProductDetail p " +
           "LEFT JOIN p.promotionDetail promotions ON promotions.promotion.condition=true " +
           "WHERE p.status=1 AND p.id=?1")
    List<ProductDetailResponse> getProductResponseByID(int idProduct);


    @Query("""

            SELECT new com.poly.du_an_tot_nghiep_f6.response.ProductDetailResponse(
                  p.id,
                  p.price,
                  p.quantity,
                  p.productCode,
                  p.product.name,
                  p.size.name,
                  p.color.name,
                  p.product.style.name,
                  p.product.category.name,
                  p.product.material.name,
                  p.product.brand.name,
                  COALESCE(promotions.giaMoi, 0),
                  COALESCE(promotions.promotion.quantity - promotions.promotion.quantityUse, 0),
                  COALESCE(promotions.promotion.name, ''),
                  COALESCE(promotions.promotion.status, null)
              )
              FROM ProductDetail p
              LEFT JOIN p.promotionDetail promotions ON promotions.promotion.condition=true
              WHERE p.status = 1
                AND (p.size.id = ?1 OR ?1 = 0)
                AND (p.color.id = ?2 OR ?2 = 0)
                AND (p.product.style.id = ?3 OR ?3 = 0)
                AND (p.product.category.id = ?4 OR ?4 = 0)
                AND (p.product.material.id = ?5 OR ?5 = 0)
                AND (p.product.brand.id = ?6 OR ?6 = 0)
                AND (
                    (p.size.name LIKE %?7% OR ?7 IS NULL) OR
                    (p.color.name LIKE %?7% OR ?7 IS NULL) OR
                    (p.product.style.name LIKE %?7% OR ?7 IS NULL) OR
                    (p.product.category.name LIKE %?7% OR ?7 IS NULL) OR
                    (p.product.material.name LIKE %?7% OR ?7 IS NULL) OR
                    (p.product.brand.name LIKE %?7% OR ?7 IS NULL) OR
                    (p.product.name LIKE %?7% OR ?7 IS NULL) OR
                    (p.id =?8 OR ?8 IS NULL)
                )
              \s""")
    List<ProductDetailResponse> getProductResponseFilter(
            Integer idSize,
            Integer idColor,
            Integer idStyle,
            Integer idCategory,
            Integer idMaterial,
            Integer idBrand,
            String keyWord,
            Integer id);

    List<ProductDetail> findByProduct(Product product);

    ProductDetail findByPromotionDetail(PromotionDetail promotionDetail);


}
