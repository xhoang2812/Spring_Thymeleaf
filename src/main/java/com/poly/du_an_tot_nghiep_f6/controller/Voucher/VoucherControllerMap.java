package com.poly.du_an_tot_nghiep_f6.controller.Voucher;

import com.poly.du_an_tot_nghiep_f6.entity.Customer;
import com.poly.du_an_tot_nghiep_f6.entity.Voucher;
import com.poly.du_an_tot_nghiep_f6.repository.VoucherRepo;
import com.poly.du_an_tot_nghiep_f6.service.VoucherService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class VoucherControllerMap {
    private final VoucherRepo voucherRepo;
    private final VoucherService voucherService;

    @GetMapping("/voucher")
    public String voucher(){
        return "admin/voucher/voucher";
    }
    @GetMapping("/createVoucher")
    public String createVoucherPage(){
        return "admin/voucher/createVoucher";
    }
    @GetMapping("/updateVoucher")
    public String updateVoucher(){
        return "admin/voucher/updateVoucher";
    }
    @GetMapping("/voucherDetail/{idVoucher}")
    public String voucherDetail(Model model, @PathVariable Integer idVoucher){
        Voucher voucher = voucherRepo.getReferenceById(idVoucher);
        List<Customer> customers = voucherService.getOldCustomerOfVoucher(voucher);
        model.addAttribute("customers", customers);
        System.out.println(customers);
        return "admin/voucher/voucherDetail";
    }
}
