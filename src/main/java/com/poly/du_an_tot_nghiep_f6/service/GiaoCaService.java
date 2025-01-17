package com.poly.du_an_tot_nghiep_f6.service;

import com.poly.du_an_tot_nghiep_f6.entity.Employee;
import com.poly.du_an_tot_nghiep_f6.entity.GiaoCa;
import com.poly.du_an_tot_nghiep_f6.entity.Product;
import com.poly.du_an_tot_nghiep_f6.repository.EmployeeRepo;
import com.poly.du_an_tot_nghiep_f6.repository.GiaoCaRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.Random;

@Service
public class GiaoCaService {
    @Autowired
    EmployeeRepo employeeRepo;
    @Autowired
    GiaoCaRepo giaoCaRepo;
    @Autowired
    IBillService iBillService;

    public Optional<Employee> getUserDetails(String username) {
        return employeeRepo.findByUsername(username);  // Trả về Optional
    }
    public GiaoCa findById(Long id) {
        return giaoCaRepo.findById(id).orElseThrow(() -> new RuntimeException("Giao ca  không tồn tại"));
    }

    public List<GiaoCa> findAll() {
        return giaoCaRepo.findAll();
    }

    //     Phương thức để lấy thời gian hiện tại (thời gian nhận ca)
    public LocalDateTime getCurrentShiftTime() {
        return LocalDateTime.now(); // hoặc tính toán thời gian cần thiết
    }

    public String formatDateTime(LocalDateTime dateTime) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss dd-MM-yyyy");
        return dateTime.format(formatter);
    }

    public LocalDateTime getCurrentShiftTime2() {
        return LocalDateTime.now(); // hoặc tính toán thời gian cần thiết
    }

    public String formatDateTime2(LocalDateTime dateTime) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(" dd-MM-yyyy");
        return dateTime.format(formatter);
    }


    public String generateMaGiaoCa() {
        // Khởi tạo tiền tố mã giao ca
        String prefix = "GC";
        // Độ dài của phần mã ngẫu nhiên
        int randomStringLength = 20;
        // Các ký tự hợp lệ (chữ cái và số)
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        // Sử dụng SecureRandom để sinh số ngẫu nhiên an toàn hơn
        Random random = new SecureRandom();
        StringBuilder randomString = new StringBuilder(randomStringLength);
        // Sinh 20 ký tự ngẫu nhiên
        for (int i = 0; i < randomStringLength; i++) {
            randomString.append(characters.charAt(random.nextInt(characters.length())));
        }
        // Kết hợp tiền tố "GC" với chuỗi ngẫu nhiên
        return prefix + randomString.toString();
    }

    public GiaoCa addGiaoCa(GiaoCa giaoCa) {
        return giaoCaRepo.save(giaoCa);
    }

    public GiaoCa findGiaoCaByMaGC(String magiaoca) {
        return giaoCaRepo.findByMagiaoca(magiaoca);
    }


}
