package com.poly.du_an_tot_nghiep_f6.repository;

import com.poly.du_an_tot_nghiep_f6.entity.Voucher;
import com.poly.du_an_tot_nghiep_f6.entity.VoucherDetail;
import org.hibernate.type.descriptor.converter.spi.JpaAttributeConverter;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface VoucherDetailRepo extends JpaRepository<VoucherDetail, Integer> {
    List<VoucherDetail> findByVoucher(Voucher voucher);
}
