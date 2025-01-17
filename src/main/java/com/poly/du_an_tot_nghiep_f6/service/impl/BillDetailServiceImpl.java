package com.poly.du_an_tot_nghiep_f6.service.impl;

import com.poly.du_an_tot_nghiep_f6.entity.BillDetail;
import com.poly.du_an_tot_nghiep_f6.entity.CartDetailInCounter;
import com.poly.du_an_tot_nghiep_f6.repository.BillDetailRepo;
import com.poly.du_an_tot_nghiep_f6.response.BillDetailResponse;
import com.poly.du_an_tot_nghiep_f6.service.IBillDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BillDetailServiceImpl implements IBillDetailService {
    @Autowired
    BillDetailRepo billDetailRepo;

    public Boolean saveBillDetail(List<BillDetail> billDetais) {
        billDetailRepo.saveAll(billDetais);
        return true;
    }

    @Override
    public Boolean saveBillDetailEdit(Integer idBill, List<BillDetail> billDetailNews) {//truyền danh sách hóa đơn cũ và mới vào
        for(BillDetail billDetail : billDetailRepo.findByBillId(idBill)) {
            billDetailRepo.delete(billDetail);
        }
        for(BillDetail billDetail : billDetailNews) {
            billDetailRepo.save(billDetail);
        }
        return true;
    }

    @Override
    public List<BillDetailResponse> findByIdBill(int idBill) {
        return billDetailRepo.findResponseByBillId(idBill);
    }
}
