package com.poly.du_an_tot_nghiep_f6.repository;

import com.poly.du_an_tot_nghiep_f6.entity.BillHistory;
import com.poly.du_an_tot_nghiep_f6.response.BillHistoryRespone;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface BillHistoryRepo extends JpaRepository<BillHistory, Integer> {
    @Query("""
        SELECT new com.poly.du_an_tot_nghiep_f6.response.BillHistoryRespone(
        h.id,
        COALESCE(e.name, 'Online'),
        COALESCE(h.dateUpdate, ''),
        h.statusOld,
        h.status,
        h.description
        )
        FROM BillHistory h
        LEFT JOIN h.employee e
        WHERE h.bill.id = ?1
        """)
    List<BillHistoryRespone> findByBillId(int billId);

    List<BillHistory> findAllByBill_Id(Integer billId);
}
