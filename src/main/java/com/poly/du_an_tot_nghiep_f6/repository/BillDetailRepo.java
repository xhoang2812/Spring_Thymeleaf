package com.poly.du_an_tot_nghiep_f6.repository;

import com.poly.du_an_tot_nghiep_f6.entity.BillDetail;
import com.poly.du_an_tot_nghiep_f6.response.BillDetailResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BillDetailRepo extends JpaRepository<BillDetail,Integer> {

    List<BillDetail> findByBillId(Integer id);

    @Query("""
            SELECT new com.poly.du_an_tot_nghiep_f6.response.BillDetailResponse(
                billdetail.id,
                billdetail.bill.id,
                CONCAT(billdetail.productDetail.product.name,"- ",billdetail.productDetail.size.name,"- ",billdetail.productDetail.color.name),
                billdetail.quantity,
                billdetail.price,
                billdetail.oldPrice,
                billdetail.intoMoney,
                billdetail.description,
                billdetail.productDetail.id
                
            ) 
            FROM BillDetail billdetail WHERE billdetail.bill.id = ?1
            """)
    List<BillDetailResponse> findResponseByBillId(int id);

    List<BillDetail> findAllByBill_Id(Integer id);

}
