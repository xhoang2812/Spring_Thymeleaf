package com.poly.du_an_tot_nghiep_f6.controller.Statistical;

import com.poly.du_an_tot_nghiep_f6.repository.BillRepo;
import com.poly.du_an_tot_nghiep_f6.response.TopSPBanChay;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequiredArgsConstructor
@CrossOrigin("*")
@RequestMapping("/api/statistical")
public class StatisticalControllerApi {
    private final BillRepo billRepo;

    //    @GetMapping
//    public List<String> calculateRevenue(
//            @RequestParam(value = "date", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
//            @RequestParam(value = "month", required = false) Integer month,
//            @RequestParam(value = "year", required = false) Integer year,
//            @RequestParam(value = "quarter", required = false) Integer quarter,
//            @RequestParam(value = "startDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
//            @RequestParam(value = "endDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate
//    ) {
//        System.out.println(month);
//        List<Object[]> revenue = billRepo.calculateRevenue(date, month, year, quarter, startDate, endDate);
//        DecimalFormat df = new DecimalFormat("#,###.##"); // ham format du lieu tu sql ve so
//        String formattedRevenue = df.format(revenue.get(0)[0]) + " VND"; // [0] la cai select dau tien [1]... la tang dan
//        String slHoaDonTC = df.format(revenue.get(0)[1]);
//        String doanhThuTaiQuay = df.format(revenue.get(0)[2]) + " VND";
//        String doanhThuOnline = df.format(revenue.get(0)[3]) + " VND";
//        String slHoaDonBiHuy = df.format(revenue.get(0)[4]);
//
//        System.out.println("Doanh thu: " + formattedRevenue);
//        System.out.println("Doanh thu tai quay: " + doanhThuTaiQuay);
//        System.out.println("Doanh thu online : " + doanhThuOnline);
//        System.out.println("Sl hoa don tc: " + slHoaDonTC);
//        System.out.println("Sl hoa don bi huy: " + slHoaDonBiHuy);
//        List<String> response = new ArrayList<>(); // luu du lieu vao day de lay du lieu ra
//        response.add(formattedRevenue);
//        response.add(slHoaDonTC);
//        response.add(doanhThuTaiQuay);
//        response.add(doanhThuOnline);
//        response.add(slHoaDonBiHuy);
//        return response;
//    }
    @GetMapping
    public List<String> calculateRevenue(
            @RequestParam(value = "date", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam(value = "month", required = false) Integer month,
            @RequestParam(value = "year", required = false) Integer year,
            @RequestParam(value = "quarter", required = false) Integer quarter,
            @RequestParam(value = "startDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(value = "endDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate
    ) {
        System.out.println(month);
        List<Object[]> revenue = billRepo.calculateRevenue(date, month, year, quarter, startDate, endDate);
        DecimalFormat df = new DecimalFormat("#,###.##"); // ham format du lieu tu sql ve so
        String formattedRevenue = df.format(revenue.get(0)[0]) + " VND"; // [0] la cai select dau tien [1]... la tang dan
        String slHoaDonTC = df.format(revenue.get(0)[1]);
        String doanhThuTaiQuay = df.format(revenue.get(0)[2]) + " VND";
        String doanhThuOnline = df.format(revenue.get(0)[3]) + " VND";
        String slHoaDonBiHuy = df.format(revenue.get(0)[4]);
        String tienTraHang = df.format(billRepo.tienTraHang(date, month, year, quarter, startDate, endDate)) + " VND";
        System.out.println("Doanh thu: " + formattedRevenue);
        System.out.println("Doanh thu tai quay: " + doanhThuTaiQuay);
        System.out.println("Doanh thu online : " + doanhThuOnline);
        System.out.println("Sl hoa don tc: " + slHoaDonTC);
        System.out.println("Sl hoa don bi huy: " + slHoaDonBiHuy);
        List<String> response = new ArrayList<>(); // luu du lieu vao day de lay du lieu ra
        response.add(formattedRevenue);
        response.add(slHoaDonTC);
        response.add(doanhThuTaiQuay);
        response.add(doanhThuOnline);
        response.add(slHoaDonBiHuy);
        response.add(tienTraHang);
        return response;
    }

    @GetMapping("/getDT")
    public List<Integer> getDt(@RequestParam Integer year) {
        return billRepo.getTotalMoney12Month(year);
    }

    @GetMapping("/getDtForMonth")
    public List<Integer> getDtForMonth(@RequestParam Integer month, @RequestParam Integer year) {
        return billRepo.getTotalMoneyForMonth(month, year);
    }

    @GetMapping("/tiledonhang")
    public List<BigDecimal> tiLeDonHang() {
        List<Object[]> tiLeDonHang = billRepo.tiLeDonHang();
        List<BigDecimal> response = new ArrayList<>();
        response.add((BigDecimal) tiLeDonHang.get(0)[0]);
        response.add((BigDecimal) tiLeDonHang.get(0)[1]);
        response.add((BigDecimal) tiLeDonHang.get(0)[2]);
        return response;
    }

    @GetMapping("/tiletrangthaidonhang")
    public List<BigDecimal> tiLeTrangThaiDonHang() {
        List<Object[]> tiLeDonHang = billRepo.tiLeTrangThaiDonHang();
        List<BigDecimal> response = new ArrayList<>();
        response.add((BigDecimal) tiLeDonHang.get(0)[0]);
        response.add((BigDecimal) tiLeDonHang.get(0)[1]);
        return response;
    }

    @GetMapping("/top5SP")
    public List<TopSPBanChay> getTopSPBanChay() {
        return billRepo.findTop5ProductDetails();
    }
}
