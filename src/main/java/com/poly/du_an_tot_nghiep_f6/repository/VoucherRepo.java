package com.poly.du_an_tot_nghiep_f6.repository;

import com.poly.du_an_tot_nghiep_f6.entity.Voucher;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface VoucherRepo extends JpaRepository<Voucher, Integer> {

    @Query(value = "SELECT *\n" +
            "FROM voucher\n" +
            "WHERE voucher.status = 'true'\n" +
            "  AND voucher.condition = N'Đang diễn ra'\n" +
            "  AND voucher.quantity > voucher.quantity_used \n" +
            "  AND voucher.form_voucher = 'true' order by maximum_reduction desc", nativeQuery = true)
    List<Voucher> getAllCheckOut();


}
