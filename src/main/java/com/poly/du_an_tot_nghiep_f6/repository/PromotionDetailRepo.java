package com.poly.du_an_tot_nghiep_f6.repository;

import com.poly.du_an_tot_nghiep_f6.entity.Promotion;
import com.poly.du_an_tot_nghiep_f6.entity.PromotionDetail;
import com.poly.du_an_tot_nghiep_f6.response.PromotionDetailResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PromotionDetailRepo extends JpaRepository<PromotionDetail, Integer> {

//    @Query("SELECT new com.poly.du_an_tot_nghiep_f6.response.PromotionDetailResponse("
//            + "pr.promotion.id, "
//            + "pr.promotion.code, "
//            + "pr.promotion.name, "
//            + "pr.promotion.price, "
//            + "pr.promotion.valueType,"
//            + "pr.promotion.quantity,"
//            + "pr.promotion.dateStart,"
//            + "pr.promotion.dateEnd,"
//            + "pr.promotion.dateCreate,"
//            + "pr.promotion.dateUpdate,"
//            + "pr.promotion.status,"
//            + "pr.productDetail.id) "
//            + "FROM PromotionDetail pr")
//    List<PromotionDetailResponse> findAllPromotionDetails();

    @Query("SELECT pd.productDetail.id FROM PromotionDetail pd WHERE pd.promotion.id = :promotionId")
    List<Integer> getSelectedProductIdsByPromotionId(@Param("promotionId") Integer promotionId);

//    @Query("SELECT new com.poly.du_an_tot_nghiep_f6.response.PromotionDetailResponse(" +
//            "pd.promotion.id, " +
//            "pd.promotion.code, " +
//            "pd.promotion.name, " +
//            "pd.promotion.price, " +
//            "pd.promotion.valueType, " +
//            "pd.promotion.quantity, " +
//            "pd.promotion.dateStart, " +
//            "pd.promotion.dateEnd, " +
//            "pd.promotion.dateCreate, " +
//            "pd.promotion.dateUpdate, " +
//            "pd.promotion.status, " +
//            "pd.productDetail.id) " +
//            "FROM PromotionDetail pd WHERE pd.promotion.id = :promotionId")
//    List<PromotionDetailResponse> getPromotionDetailById(@Param("promotionId") Integer promotionId);

    List<PromotionDetail> findByPromotion(Promotion promotion);
}
