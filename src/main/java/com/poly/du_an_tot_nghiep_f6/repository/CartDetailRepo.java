package com.poly.du_an_tot_nghiep_f6.repository;

import com.poly.du_an_tot_nghiep_f6.entity.CartDetail;
import com.poly.du_an_tot_nghiep_f6.response.CartDetailOnlineResponse;
import com.poly.du_an_tot_nghiep_f6.response.CartKoDangNhapRes;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CartDetailRepo extends JpaRepository<CartDetail, Integer> {
    List<CartDetail> findAllByCart_Id(Integer idCart);

    CartDetail findByCart_IdAndProductDetail_Id(Integer idCart, Integer idProductDetail);

    int countCartDetailByCart_Id(Integer idCart);

    List<CartDetail> findAllByIdIn(List<Integer> ids);

    @Query(value = "select sum(cart_detail.quantity) from cart_detail " +
            "where id_cart = :id_cart and id_product_detail = :id_product_detail", nativeQuery = true)
    Integer getQuantityCart_IdAndProductDetail_Id(@Param("id_cart") Integer idCart, @Param("id_product_detail") Integer idProductDetail);

    @Query(value = "select id_product_detail from cart_detail where id = :id", nativeQuery = true)
    Integer getProductDetail_IdById(Integer id);

    @Query(value = "select product_detail.promotion_detail_id from product_detail " +
            "where product_detail.id = :id", nativeQuery = true)
    Integer getQuantityInPromotion(@Param("id") Integer id);

    @Query(value = "select promotion.id from product_detail join promotion_detail on product_detail.promotion_detail_id = promotion_detail.id\n" +
            "join promotion on promotion_detail.id_promotion = promotion.id\n" +
            "where product_detail.id = :id and promotion.status like '%ang%' and promotion.condition = 'true'", nativeQuery = true)
    Integer getQuantityInPromotion1(@Param("id") Integer id);

    @Query(value = "select new CartDetailOnlineResponse (cd.id, c.id, pd.id, max(img.url1), p.name, cl.name, s.name," +
            " pd.quantity, pd.price, CASE WHEN pm.status like '%ang%' and pm.condition = true THEN pmd.giaMoi ELSE 0 END, cd.quantity, cd.total, pd.weight,  pm.id, cd.havePromotion, p.status, pd.status ) " +
            "from Cart c join CartDetail cd on c.id = cd.cart.id " +
            "join ProductDetail pd on cd.productDetail.id = pd.id " +
            "join Product p on p.id = pd.product.id " +
            "join Image img on img.id = pd.image.id " +
            "join Color cl on cl.id = pd.color.id " +
            "join Size s on s.id = pd.size.id  " +
            "left join PromotionDetail pmd on pmd.id = pd.promotionDetail.id " +
            "left join Promotion pm on pm.id = pmd.promotion.id  " +
            "where c.id = :idCart " +
            "group by cd.id, c.id, pd.id, p.name, cl.name, s.name, pd.quantity, pd.price, pmd.giaMoi, cd.quantity, cd.total, pm.status,pd.weight,pm.id,pm.condition,cd.havePromotion, p.status, pd.status " +
            "order by cd.id desc")
    List<CartDetailOnlineResponse> findAllCartDetailOnlineResponse(@Param("idCart") Integer idCart);

    @Query(value = "select new CartDetailOnlineResponse (cd.id, c.id, pd.id, max(img.url1), p.name, cl.name, s.name," +
            " pd.quantity, pd.price, CASE WHEN pm.status like '%ang%' and pm.condition = true THEN pmd.giaMoi ELSE 0 END, cd.quantity, cd.total, pd.weight,  pm.id,  cd.havePromotion , p.status, pd.status ) " +
            "from Cart c join CartDetail cd on c.id = cd.cart.id " +
            "join ProductDetail pd on cd.productDetail.id = pd.id " +
            "join Product p on p.id = pd.product.id " +
            "join Image img on img.id = pd.image.id " +
            "join Color cl on cl.id = pd.color.id " +
            "join Size s on s.id = pd.size.id  " +
            "full outer join PromotionDetail pmd on pmd.id = pd.promotionDetail.id " +
            "full outer join Promotion pm on pm.id = pmd.promotion.id  " +
            "where c.id = :idCart and cd.id in (:ids) " +
            "group by cd.id, c.id, pd.id, p.name, cl.name, s.name, pd.quantity, pd.price, pmd.giaMoi, cd.quantity, cd.total, pm.status,pd.weight, pm.id,pm.condition,  cd.havePromotion, p.status, pd.status ")
    List<CartDetailOnlineResponse> findAllCartDetailOnlineResponse2(@Param("idCart") Integer idCart, @Param("ids") List<Integer> ids);

    @Query(value = "select new CartDetailOnlineResponse (cd.id, c.id, pd.id, max(img.url1), p.name, cl.name, s.name," +
            " pd.quantity, pd.price, CASE WHEN pm.status like '%ang%' and pm.condition = true THEN pmd.giaMoi ELSE 0 END, cd.quantity, cd.total, pd.weight,  pm.id,  cd.havePromotion , p.status, pd.status ) " +
            "from Cart c join CartDetail cd on c.id = cd.cart.id " +
            "join ProductDetail pd on cd.productDetail.id = pd.id " +
            "join Product p on p.id = pd.product.id " +
            "join Image img on img.id = pd.image.id " +
            "join Color cl on cl.id = pd.color.id " +
            "join Size s on s.id = pd.size.id  " +
            "full outer join PromotionDetail pmd on pmd.id = pd.promotionDetail.id " +
            "full outer join Promotion pm on pm.id = pmd.promotion.id  " +
            "where c.id = :idCart and cd.productDetail.id = :ids " +
            "group by cd.id, c.id, pd.id, p.name, cl.name, s.name, pd.quantity, pd.price, pmd.giaMoi, cd.quantity, cd.total, pm.status,pd.weight,pm.id,pm.condition, cd.havePromotion, p.status, pd.status ")
    List<CartDetailOnlineResponse> findAllCartDetailOnlineResponse3(@Param("idCart") Integer idCart, @Param("ids") Integer ids);

    @Query(value = "select pd.id,\n" +
            "       max(img.url1),\n" +
            "       p.name,\n" +
            "       cl.name,\n" +
            "       s.name,\n" +
            "       pd.quantity,\n" +
            "       pd.price,\n" +
            "       CASE WHEN pm.status like '%ang%' and pm.condition = 'true' THEN pmd.gia_moi ELSE 0 END,\n" +
            "       CASE\n" +
            "           WHEN pm.status LIKE '%ang%' AND pm.condition = 'true' AND pmd.gia_moi > 0 THEN :quantity * pmd.gia_moi\n" +
            "           ELSE :quantity * pd.price\n" +
            "           END   AS total,\n" +
            "       case when :quantity > pd.quantity then pd.quantity else :quantity end as quantity_order,\n" +
            "       pd.weight,\n" +
            "       pm.id,\n" +
            "       p.status,\n" +
            "       pd.status\n" +
            "from product_detail pd\n" +
            "         join Product p on p.id = pd.id_product\n" +
            "         join Image img on img.id = pd.id_image\n" +
            "         join Color cl on cl.id = pd.id_color\n" +
            "         join Size s on s.id = pd.id_size\n" +
            "         left join promotion_detail pmd on pmd.id = pd.promotion_detail_id\n" +
            "         left join Promotion pm on pm.id = pmd.id_promotion\n" +
            "where pd.id = :ids\n" +
            "group by pd.id, p.name, cl.name, s.name, pd.quantity, pd.price, pmd.gia_moi, pm.status, pd.weight, pm.id, pm.condition,\n" +
            "         p.status, pd.status", nativeQuery = true)
    List<Object[]> findAllCartKoDangNhapByIdAndQuantity(@Param("ids") Integer ids, @Param("quantity") Integer quantity);

}
