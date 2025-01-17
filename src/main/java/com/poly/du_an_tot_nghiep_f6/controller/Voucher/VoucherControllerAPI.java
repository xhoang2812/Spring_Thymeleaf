package com.poly.du_an_tot_nghiep_f6.controller.Voucher;

import com.poly.du_an_tot_nghiep_f6.entity.Employee;
import com.poly.du_an_tot_nghiep_f6.entity.VoucherDetail;
import com.poly.du_an_tot_nghiep_f6.response.VoucherResponse;
import com.poly.du_an_tot_nghiep_f6.service.VoucherService;
import com.poly.du_an_tot_nghiep_f6.entity.Customer;
import com.poly.du_an_tot_nghiep_f6.entity.Voucher;
import com.poly.du_an_tot_nghiep_f6.repository.CustomerRepo;
import com.poly.du_an_tot_nghiep_f6.repository.VoucherRepo;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

@RestController
@RequiredArgsConstructor
@CrossOrigin("*")
@RequestMapping("/api/vouchers")
public class VoucherControllerAPI {
    private final VoucherService voucherService;
    private final CustomerRepo customerRepo;
    private final VoucherRepo voucherRepo;

    @PostMapping
    public ResponseEntity<Voucher> createVoucher(@RequestBody VoucherResponse voucher) throws MessagingException {
        System.out.println(voucher);
        Voucher createdVoucher = voucherService.createVoucher(voucher);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdVoucher);
    }

    @GetMapping
    public List<Voucher> getAll() {
        List<Voucher> vouchers = voucherRepo.findAll();
        // Sắp xếp theo id giảm dần
        vouchers.sort(Comparator.comparing(Voucher::getId).reversed());
        return voucherRepo.findAll();
    }
//    @GetMapping
//    public List<Voucher> getFilteredVouchers(
//            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date fromDate,
//            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date toDate,
//            @RequestParam(required = false) String option) {
//
//        // Gọi service để lọc danh sách voucher
//        return voucherService.getFilteredVouchers(fromDate, toDate, option);
//    }

    @PutMapping("/{id}")
    public ResponseEntity<Voucher> updateVoucher(@PathVariable Integer id, @RequestBody VoucherResponse voucher) throws MessagingException {
        Voucher updatedVoucher = voucherService.updateVoucher(id, voucher);
        return ResponseEntity.ok(updatedVoucher);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Voucher> deleteVoucher(@PathVariable Integer id, Authentication authentication) throws MessagingException {
        if (authentication != null) {
            Employee employee = voucherService.currentEmployee(authentication);
            if (employee.getPosition().equals("Admin")) {
                return ResponseEntity.ok(voucherService.deleteVoucher(id));
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

    //    @GetMapping
//    public List<Voucher> getAllVoucher(){
//        return voucherRepo.findAll();
//    }
    @GetMapping("/{idVoucher}")
    public VoucherResponse getVoucherById(@PathVariable Integer idVoucher, Authentication authentication) throws Exception {
        if (authentication != null) {
            Employee employee = voucherService.currentEmployee(authentication);
            if (employee.getPosition().equals("Admin")) {
                return voucherService.getVoucherResponseByIdVoucher(idVoucher);
            }
        }
        return null;
    }

    @GetMapping("/userData")
    public Employee getCurrentUser(Authentication authentication) {
        if (authentication != null) {
            Employee employee = voucherService.currentEmployee(authentication);
            if (employee.getPosition().equals("Admin")) {
                return employee;
            }
        }
        return null;
    }

    @GetMapping("/get-ip")
    public String getClientIp(HttpServletRequest request) {
        String clientIp = request.getRemoteAddr();
        return "Client IP: " + clientIp;
    }

    @GetMapping("/a")
    public List<Voucher> get() {
        Customer customer = customerRepo.getReferenceById(5);
//        Voucher voucher = voucherRepo.getReferenceById(12);
//        customer.getVouchers().remove(voucher);
//        customerRepo.save(customer);
        return customer.getVouchers();
    }

    @GetMapping("/customerData")
    public List<Customer> getAllCustomer() {
        return customerRepo.findAll();
    }

//    @Autowired
//    private MailService emailService;
//
//    @GetMapping("/send-email")
//    public String sendEmail(@RequestParam("to") String to, @RequestParam("name") String name) {
//        try {
//            emailService.sendEmail(to, "Subject: Welcome!", name);
//            return "Email sent successfully!";
//        } catch (MessagingException e) {
//            return "Failed to send email!";
//        }
//    }

//    @GetMapping("/customerData")
//    public List<Customer> getAllCustomer() {
//        return customerRepo.findAll();
//    }
}
