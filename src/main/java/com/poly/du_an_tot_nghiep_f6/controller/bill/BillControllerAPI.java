package com.poly.du_an_tot_nghiep_f6.controller.bill;

import com.poly.du_an_tot_nghiep_f6.entity.Bill;
import com.poly.du_an_tot_nghiep_f6.entity.BillDetail;
import com.poly.du_an_tot_nghiep_f6.entity.BillHistory;
import com.poly.du_an_tot_nghiep_f6.entity.Employee;
import com.poly.du_an_tot_nghiep_f6.repository.BillDetailRepo;
import com.poly.du_an_tot_nghiep_f6.repository.BillHistoryRepo;
import com.poly.du_an_tot_nghiep_f6.response.BillDetailResponse;
import com.poly.du_an_tot_nghiep_f6.response.BillHistoryRespone;
import com.poly.du_an_tot_nghiep_f6.response.BillResponse;
import com.poly.du_an_tot_nghiep_f6.service.IBillDetailService;
import com.poly.du_an_tot_nghiep_f6.service.IBillService;
import com.poly.du_an_tot_nghiep_f6.service.VoucherService;
import com.poly.du_an_tot_nghiep_f6.service.impl.GiaoCaoServiceImpl;
import com.poly.du_an_tot_nghiep_f6.service.impl.ProductDetailServiceImpl;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/bill")
public class BillControllerAPI {
    @Autowired
    IBillService billService;

    @Autowired
    IBillDetailService billDetailService;
    @Autowired
    GiaoCaoServiceImpl giaoCaoService;

    @Autowired
    VoucherService voucherService;

    @Autowired
    BillHistoryRepo billHistoryRepo;

    @Autowired
    private BillDetailRepo billDetailRepo;
    @Autowired
    ProductDetailServiceImpl productDetailService;

    @GetMapping("/get-all")
    public List<Bill> getAllBill() {
        return billService.getAll();
    }

    @GetMapping("/get-all-response")
    public List<BillResponse> getAllBillResponse() {
        return billService.getAllResponse();
    }

    @GetMapping("/get-bill")
    public Bill getBill(@RequestParam int idBill) {
        return billService.getBill(idBill);
    }

    @SneakyThrows
    @GetMapping("/get-filter")
    public List<BillResponse> getBillFilter(@RequestParam String keyWord,
                                            @RequestParam String dateStart,
                                            @RequestParam String dateEnd,
                                            @RequestParam Integer status,
                                            @RequestParam Double minPrice,
                                            @RequestParam Double maxPrice,
                                            @RequestParam Boolean sort) {
        Date timeStart;
        Date timeEnd;
        Integer idBill = null;
        try {
            idBill = Integer.parseInt(keyWord);
        } catch (NumberFormatException e) {
        }

        if (dateEnd.isEmpty()) {
            timeEnd = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm").parse("2100-01-01T00:00");
        } else {
            timeEnd = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm").parse(dateEnd);
        }
        if (dateStart.isEmpty()) {
            timeStart = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm").parse("1900-01-01T00:00");
            ;
        } else {
            timeStart = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm").parse(dateStart);
        }
        if (status == 10) {
            status = null;
        }
        return billService.getBillFilter(idBill, keyWord, timeStart, timeEnd, status, minPrice, maxPrice, sort);
    }

    @GetMapping("/get-product-in-bill")
    public List<BillDetailResponse> getProductInBill(@RequestParam int idBill) {
        return billDetailService.findByIdBill(idBill);
    }

    @PostMapping("/next_status")
    public Integer nextStatus(@RequestParam int idBill, @RequestParam String description, @RequestParam int statusOld, Authentication authentication) {
        Bill bill = billService.getBill(idBill);
        int status = bill.getStatus();
        if (status != statusOld) {
            return -3;
        }
        BillHistory billHistory = new BillHistory(
                0,
                getEmployee(authentication),
                bill,
                description,
                bill.getStatus() + 1,
                bill.getStatus(),
                new Date()
        );
        if (bill.getStatus() == 0) {
            int a = billService.checkQuantity(idBill);
            if (a != 0) {
                return a;
            } else {
                if (!billService.chargeQuantity(idBill)) {
                    return -2;
                }
            }
        }
        bill.setStatus(billHistory.getStatus());
        System.out.println("Trạng thái sau ghi chuyển " + billHistory.getStatus() + "\nTrạng thái đã lưu ID:" + bill.getId() + "-" + bill.getStatus());
        try {
            billHistoryRepo.save(billHistory);
            return -1;
        } catch (Exception e) {
            return 0;
        }
    }

    @GetMapping("get-history-bill")
    public List<BillHistoryRespone> getHistoryBill(@RequestParam int idBill) {
        return billHistoryRepo.findByBillId(idBill);
    }

    @PutMapping("/cancel_bill")
    public int cancelBill(@RequestParam int idBill, @RequestParam String description, @RequestParam int statusOld, Authentication authentication) {
        Bill bill = billService.getBill(idBill);
        int status = bill.getStatus();
        if (status != statusOld) {
            return -3;
        }
        BillHistory billHistory = new BillHistory(
                0,
                getEmployee(authentication),
                bill,
                description,
                -1,
                bill.getStatus(),
                new Date()
        );
        bill.setStatus(-1);
        if (billHistory.getStatusOld() != 0) {
            List<BillDetail> billDetails = billDetailRepo.findByBillId(idBill);
            for (BillDetail billDetail : billDetails) {
                productDetailService.changerQuantity(
                        billDetail.getProductDetail().getId(),
                        billDetail.getQuantity(),
                        true);//true tăng trong kho, false là giảm
            }
        }
        try {
            billHistoryRepo.save(billHistory);
            return 0;
        } catch (Exception e) {

            return 1;
        }

    }

    @PutMapping("/error_ship")
    public int errorShip(@RequestParam int idBill, @RequestParam String description, @RequestParam int statusOld, Authentication authentication) {
        Bill bill = billService.getBill(idBill);
        int status = bill.getStatus();
        if (status != statusOld) {
            return -3;
        }
        BillHistory billHistory = new BillHistory(
                0,
                getEmployee(authentication),
                bill,
                description,
                bill.getStatus(),
                bill.getStatus(),
                new Date()
        );
        int a = billService.checkQuantity(idBill);
        if (a != 0) {
            return a;
        } else {
            if (!billService.chargeQuantity(idBill)) {
                return -2;
            }
        }
        try {
            billHistoryRepo.save(billHistory);
            return -1;
        } catch (Exception e) {
            return 0;
        }

    }

    private Employee getEmployee(Authentication authentication) {
        Employee employee = null;
        if (authentication != null) {
            employee = voucherService.currentEmployee(authentication);
        } else {
            employee = giaoCaoService.getEmployeeAreWorking();
        }
        return employee;
    }
}
