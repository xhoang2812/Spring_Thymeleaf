package com.poly.du_an_tot_nghiep_f6.service;

import com.poly.du_an_tot_nghiep_f6.entity.BillDetail;
import com.poly.du_an_tot_nghiep_f6.entity.CartDetailInCounter;
import com.poly.du_an_tot_nghiep_f6.response.BillDetailResponse;

import java.util.List;

public interface IBillDetailService {
    Boolean saveBillDetail (List<BillDetail> billDetails);

    Boolean saveBillDetailEdit(Integer idBill, List<BillDetail> billDetailNews);

    List<BillDetailResponse> findByIdBill(int idBill);
}
