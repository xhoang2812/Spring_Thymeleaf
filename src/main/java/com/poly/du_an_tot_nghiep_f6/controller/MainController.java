package com.poly.du_an_tot_nghiep_f6.controller;

import com.poly.du_an_tot_nghiep_f6.entity.*;
import com.poly.du_an_tot_nghiep_f6.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Controller
@RequestMapping("/")
public class MainController {

    @GetMapping("")
    public String admin() {
        return "admin/index";
    }

    @GetMapping("/addData")
    public String createData() {
//        addData();
        return "redirect:/";
    }

    @GetMapping("/noiquy")
    public String noiQuyCuaHang() {
        return "admin/noiquy";
    }

    @Autowired
    ColorRepo colorRepo;
    @Autowired
    MaterialRepo materialRepo;
    @Autowired
    CategoryRepo directoryRepo;
    @Autowired
    SizeRepo sizeRepo;
    @Autowired
    StyleRepo styleRepo;
    @Autowired
    BrandRepo trademarkRepo;
    @Autowired
    VoucherRepo voucherRepo;
    @Autowired
    CustomerRepo customerRepo;
    @Autowired
    EmployeeRepo employeeRepo;
    @Autowired
    ProductDetailRepo productDetailRepo;
    @Autowired
    BillDetailRepo billDetailRepo;
    @Autowired
    ProductRepo productRepo;
    @Autowired
    BillRepo billRepo;
    @Autowired
    TradeRepo tradeRepo;


//    public void addData() {
//        colorRepo.deleteAll();
//        materialRepo.deleteAll();
//        directoryRepo.deleteAll();
//        sizeRepo.deleteAll();
//        styleRepo.deleteAll();
//        trademarkRepo.deleteAll();
//        voucherRepo.deleteAll();
//        customerRepo.deleteAll();
//        employeeRepo.deleteAll();
//        productDetailRepo.deleteAll();
//        billDetailRepo.deleteAll();
//        productRepo.deleteAll();
//        billRepo.deleteAll();
//        tradeRepo.deleteAll();
//    }
}
