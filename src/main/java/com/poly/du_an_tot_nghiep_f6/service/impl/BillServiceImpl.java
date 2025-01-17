package com.poly.du_an_tot_nghiep_f6.service.impl;

import com.poly.du_an_tot_nghiep_f6.entity.Bill;
import com.poly.du_an_tot_nghiep_f6.entity.BillDetail;
import com.poly.du_an_tot_nghiep_f6.entity.GiaoCa;
import com.poly.du_an_tot_nghiep_f6.repository.BillRepo;
import com.poly.du_an_tot_nghiep_f6.repository.ProductDetailRepo;
import com.poly.du_an_tot_nghiep_f6.response.BillResponse;
import com.poly.du_an_tot_nghiep_f6.service.IBillService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class BillServiceImpl implements IBillService {
    @Autowired
    BillRepo billRepo;
    @Autowired
    private BillDetailServiceImpl billDetailServiceImpl;
    @Autowired
    private ProductDetailServiceImpl productDetailService;

    @Override
    public Bill save(Bill bill) {
        return billRepo.save(bill);
    }

    @Override
    public Bill getBill(int id) {
        return billRepo.getOne(id);
    }

    @Override
    public List<BillResponse> getAllResponse() {
        return billRepo.getBills();
    }

    @Override
    public List<Bill> getAll() {
        return billRepo.findAll();
    }


    @Override
    public List<BillResponse> getBillFilter(Integer idBill, String keyWord, Date dateStart, Date dateEnd, Integer status, Double minPrice, Double maxPrice, Boolean sort0) {
        Sort sort;
        if (sort0) {
            sort = Sort.by(Sort.Direction.DESC, "dateCreate");
        } else {
            sort = Sort.by(Sort.Direction.ASC, "dateCreate");
        }
        return changeToResponse(billRepo.getBillFilter(idBill, keyWord, dateStart, dateEnd, status, minPrice, maxPrice, sort));
    }

    @Override
    public int getTotalBillInShift(GiaoCa giaoCa) {
        return billRepo.totalBillInShift(giaoCa.getThoigianvaoca(), LocalDateTime.now());
    }

    @Override
    public double getTotalPriceBillInShift(GiaoCa giaoCa, Integer id) {
        if (billRepo.totalPriceBillInShift(giaoCa.getThoigianvaoca(), LocalDateTime.now(), id) != null) {
            return billRepo.totalPriceBillInShift(giaoCa.getThoigianvaoca(), LocalDateTime.now(), id);
        }
        return 0.0;
    }

    @Override
    public Integer checkQuantity(Integer idBill) {
        List<BillDetail> billDetails = billDetailServiceImpl.billDetailRepo.findAllByBill_Id(idBill);
        int index = 1;
        for (BillDetail billDetail : billDetails) {
            if (billDetail.getProductDetail().getQuantity() < billDetail.getQuantity()) {
                return index;
            }
            index++;
        }
        return 0;
    }

    public boolean chargeQuantity(Integer idBill) {
        try {
            List<BillDetail> billDetails = billDetailServiceImpl.billDetailRepo.findAllByBill_Id(idBill);
            for (BillDetail billDetail : billDetails) {
                productDetailService.changerQuantity(
                        billDetail.getProductDetail().getId(),
                        billDetail.getQuantity(),
                        false);
            }
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    private List<BillResponse> changeToResponse(List<Bill> bills) {
        List<BillResponse> billResponses = new ArrayList<>();
        for (Bill bill : bills) {
//            System.out.println("Bill " + bill.getId());
            billResponses.add(
                    new BillResponse(
                            bill.getId(),
                            bill.getCustomer() != null ? bill.getCustomer().getName() : "",
                            bill.getEmployee() != null ? bill.getEmployee().getName() : "",
                            bill.getTotalPrice(),
                            bill.getPaymentMethods().getId(),
                            bill.getVoucher(),
                            bill.getShipPrice(),
                            bill.getDateCreate(),
                            bill.getStatus(),
                            bill.getDescriptionShip(),
                            bill.getPaymentType(),
                            bill.getAddress(),
                            bill.getCustomer() != null ? bill.getCustomer().getId() : null,
                            bill.getDescriptionBill()
                    )
            );
        }
//        System.out.println("=============");
        return billResponses;
    }

}
