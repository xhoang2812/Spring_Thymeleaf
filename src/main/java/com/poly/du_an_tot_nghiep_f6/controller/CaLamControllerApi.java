package com.poly.du_an_tot_nghiep_f6.controller;

import com.poly.du_an_tot_nghiep_f6.entity.CaLam;
import com.poly.du_an_tot_nghiep_f6.entity.CaLamChiTiet;
import com.poly.du_an_tot_nghiep_f6.entity.Employee;
import com.poly.du_an_tot_nghiep_f6.repository.CaLamChiTietRepo;
import com.poly.du_an_tot_nghiep_f6.repository.CaLamRepo;
import com.poly.du_an_tot_nghiep_f6.repository.EmployeeRepo;
import com.poly.du_an_tot_nghiep_f6.request.CaLamRequest;
import com.poly.du_an_tot_nghiep_f6.request.FilterCaLamRequest;
import com.poly.du_an_tot_nghiep_f6.response.CaLamChiTietResponse;
import com.poly.du_an_tot_nghiep_f6.service.VoucherService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import java.text.ParseException;
import java.util.Date;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@CrossOrigin("*")
@RequestMapping("/api/caLam")
public class CaLamControllerApi {
    private final CaLamRepo caLamRepo;
    private final CaLamChiTietRepo calamChiTietRepo;
    private final EmployeeRepo employeeRepo;
    private final VoucherService voucherService;

    @PostMapping
    public CaLam createCalam(@RequestParam @DateTimeFormat(pattern = "d/M/yyyy") Date ngayLam) throws ParseException {
        CaLam calam = new CaLam();
        calam.setNgayLam(ngayLam);
        CaLam saveCaLam = caLamRepo.save(calam);

        for(int i = 1; i<=3; i++){
            CaLamChiTiet caLamChiTiet = new CaLamChiTiet();
            caLamChiTiet.setTenCa("Ca "+ i);
            if(i == 1){
                caLamChiTiet.setThoiGianBatDauCa("7:00 AM");
                caLamChiTiet.setThoiGianKetThucCa("12:00 PM");
            } else if (i == 2) {
                caLamChiTiet.setThoiGianBatDauCa("12:00 PM");
                caLamChiTiet.setThoiGianKetThucCa("17:00 PM");
            }else {
                caLamChiTiet.setThoiGianBatDauCa("17:00 PM");
                caLamChiTiet.setThoiGianKetThucCa("22:00 PM");
            }
            caLamChiTiet.setCalam(saveCaLam);
            caLamChiTiet.setTrangThai("Trống");
            saveCaLam.getCaLamChiTiets().add(caLamChiTiet);
            calamChiTietRepo.save(caLamChiTiet);
        }

        return saveCaLam;
    }
    @GetMapping
    public List<CaLam> getAllCaLam(){
        return caLamRepo.findAll();
    }
    @GetMapping("/employee")
    public List<Employee> getAllEmployee(){
        List<Employee> list = employeeRepo.findAll();
        List<Employee> employees = new ArrayList<>();
        for(Employee employee : list){
            if(employee.getPosition().equals("Nhân viên")){
                employees.add(employee);
            }
        }
        return employees;
    }
    @PutMapping("/{idCaLamChiTiet}/{idEmployee}")
    public CaLamChiTiet addEmployeeToCaLamChiTiet(@PathVariable Integer idCaLamChiTiet, @PathVariable Integer idEmployee){
        CaLamChiTiet newCaLamChiTiet = calamChiTietRepo.findById(idCaLamChiTiet)
                .orElseThrow(() -> new RuntimeException("Ca lam chi tiet not found"));
        Employee employee = employeeRepo.findById(idEmployee)
                .orElseThrow(() -> new RuntimeException("Employee not found"));
        System.out.println(newCaLamChiTiet.getEmployees());
        if(newCaLamChiTiet.getEmployees().isEmpty()){
            List<Employee> employees = new ArrayList<>();
            employees.add(employee);
            newCaLamChiTiet.setEmployees(employees);
        }else {
            if(newCaLamChiTiet.getEmployees().get(0).equals(employee)){
                newCaLamChiTiet.setEmployees(new ArrayList<>());
            }else {
                newCaLamChiTiet.setEmployees(new ArrayList<>());
                newCaLamChiTiet.getEmployees().add(employee);
            }
        }

        if(newCaLamChiTiet.getEmployees().isEmpty()){
            newCaLamChiTiet.setTrangThai("Trống");
        }else {
            newCaLamChiTiet.setTrangThai("Chưa nhận ca");
        }

        return calamChiTietRepo.save(newCaLamChiTiet);
    }
    @GetMapping("/caLamCt/{idCaLamChiTiet}")
    public CaLamChiTietResponse getCaLamByCLCT(@PathVariable Integer idCaLamChiTiet){
        CaLamChiTiet caLamChiTiet = calamChiTietRepo.findById(idCaLamChiTiet)
                .orElseThrow(() -> new RuntimeException("ca lam chi tiet not found"));
        CaLamChiTietResponse caLamChiTietResponse = new CaLamChiTietResponse();
        BeanUtils.copyProperties(caLamChiTiet, caLamChiTietResponse);
        return caLamChiTietResponse;
    }

    @PostMapping("/saveListCaLam")
    public void saveListCaLam(@RequestBody List<CaLamRequest> caLamRequests) throws ParseException {
        for (CaLamRequest caLamRequest : caLamRequests) {
            boolean foundMatchingCaLam = false;
            String dateCreate = new SimpleDateFormat("dd/MM/yyyy").format(caLamRequest.getDayWork());

            // Lọc các CaLam có ngày làm việc sau ngày hiện tại
            List<CaLam> caLamsAfterToday = caLamRepo.findAll().stream()
                    .filter(caLam -> caLam.getNgayLam().after(new Date()))
                    .toList();

            // Kiểm tra xem có CaLam nào trùng ngày và ca làm không
            for (CaLam caLam : caLamsAfterToday) {
                String formattedNgayLam = new SimpleDateFormat("dd/MM/yyyy").format(caLam.getNgayLam());

                // So sánh ngày làm với ngày từ yêu cầu (định dạng dd/MM/yyyy)
                if (dateCreate.equals(formattedNgayLam)) {
                    foundMatchingCaLam = true;

                    // Tìm chi tiết ca làm phù hợp với tên ca từ yêu cầu
                    List<CaLamChiTiet> caLamChiTiets = calamChiTietRepo.findByCalam(caLam);
                    Optional<CaLamChiTiet> matchedCaLamChiTiet = caLamChiTiets.stream()
                            .filter(caLamChiTiet -> caLamChiTiet.getTenCa().equals(caLamRequest.getShiftName()))
                            .findFirst();

                    if (matchedCaLamChiTiet.isPresent()) {
                        Optional<Employee> employeeOpt = employeeRepo.findById(caLamRequest.getIdEmployee());
                        if (employeeOpt.isPresent() && !employeeOpt.get().getPosition().equals("Admin") && employeeOpt.get().isStatus()) {
                            CaLamChiTiet caLamChiTiet = matchedCaLamChiTiet.get();
                            caLamChiTiet.setEmployees(new ArrayList<>());
                            caLamChiTiet.getEmployees().add(employeeOpt.get());
                            caLamChiTiet.setTrangThai("Chưa nhận ca");
                            calamChiTietRepo.save(caLamChiTiet);
                        }
                    }
                    break;
                }
            }
            if (!foundMatchingCaLam) {
                CaLam newCaLam = createCalam(caLamRequest.getDayWork());
                List<CaLamChiTiet> caLamChiTietList = newCaLam.getCaLamChiTiets();

                for (CaLamChiTiet caLamChiTiet : caLamChiTietList) {
                    if (caLamRequest.getShiftName().equals(caLamChiTiet.getTenCa())) {
                        Optional<Employee> employeeOpt = employeeRepo.findById(caLamRequest.getIdEmployee());
                        if (employeeOpt.isPresent() && !employeeOpt.get().getPosition().equals("Admin")) {
                            caLamChiTiet.getEmployees().add(employeeOpt.get());
                            caLamChiTiet.setTrangThai("Chưa nhận ca");
                            calamChiTietRepo.save(caLamChiTiet);
                        }
                        break;
                    }
                }
            }
        }
    }
    @PostMapping("/checkCL")
    public List<CaLamChiTietResponse> caLams (
            Authentication authentication,
            @RequestBody FilterCaLamRequest filterCaLamRequest
            ){
        System.out.println(filterCaLamRequest);
        List<CaLamChiTietResponse> caLamChiTietResponses = new ArrayList<>();
        Employee employee = voucherService.currentEmployee(authentication);
        List<CaLamChiTiet> caLamChiTiets = calamChiTietRepo.findAll().stream()
                .filter(caLamChiTiet -> !caLamChiTiet.getEmployees().isEmpty() && caLamChiTiet.getEmployees().get(0).equals(employee) && caLamChiTiet.getCalam().getNgayLam().after(filterCaLamRequest.getDateStart()))
                .toList();
        caLamChiTiets = caLamChiTiets.stream()
                .filter(caLamChiTiet -> !caLamChiTiet.getEmployees().isEmpty() && caLamChiTiet.getEmployees().get(0).equals(employee) && caLamChiTiet.getCalam().getNgayLam().before(filterCaLamRequest.getDateEnd()))
                .toList();
        for(CaLamChiTiet caLamChiTiet : caLamChiTiets){
            CaLamChiTietResponse caLamChiTietResponse = new CaLamChiTietResponse();
            BeanUtils.copyProperties(caLamChiTiet, caLamChiTietResponse);
            caLamChiTietResponses.add(caLamChiTietResponse);
        }
        return caLamChiTietResponses;
    }
}
