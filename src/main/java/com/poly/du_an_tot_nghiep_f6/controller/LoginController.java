package com.poly.du_an_tot_nghiep_f6.controller;


import com.poly.du_an_tot_nghiep_f6.entity.Cart;
import com.poly.du_an_tot_nghiep_f6.entity.Customer;
import com.poly.du_an_tot_nghiep_f6.entity.Employee;
import com.poly.du_an_tot_nghiep_f6.entity.ResetToken;
import com.poly.du_an_tot_nghiep_f6.repository.CartRepo;
import com.poly.du_an_tot_nghiep_f6.repository.CustomerRepo;
import com.poly.du_an_tot_nghiep_f6.service.*;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Controller
@RequestMapping("/login")
public class LoginController {

    @Autowired
    ResetTokenService resetTokenService;

    @Autowired
    EmailService emailService;

    @Autowired
    CustomerService customerService;

    @Autowired
    CustomerRepo customerRepository;

    @Autowired
    EmployeeService employeeService;

    @Autowired
    private GiaoCaService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    EmployeeSecurityService employeeSecurityService;
    @Autowired
    private CartRepo cartRepo;

    @Autowired
    private VoucherService voucherService;

//    @GetMapping("")
//    public String Login() {
//        return "admin/login";
//    }

    @GetMapping("")
    public String getLoginPage(HttpSession session, Model model, @RequestParam(value = "error",required = false) String error, Authentication authentication){
        if (error != null) {
            model.addAttribute("error", "Vui lòng nhập đúng username và password");
            return "admin/login";
        }

        // Tbao tk bị khóa
        String accountStatus = (String) session.getAttribute("accountStatus");
        if (accountStatus != null) {
            // Thêm thông báo vào model để hiển thị trên trang login
            model.addAttribute("accountStatus", accountStatus);
            // Xóa thông báo sau khi đã hiển thị để tránh hiển thị lại
            session.removeAttribute("accountStatus");
        }

        //Tbao ca làm
        String a = (String) session.getAttribute("notification");
        if (a != null) {
            if (a.equals("true")) {
                model.addAttribute("error", "Bạn đang đăng nhập khi không phải ca làm của mình ");
            } else if (a.equals("false")) {
                // đăng nhập khi ca đang trống
                model.addAttribute("error", "Bạn đang đăng nhập khi không phải ca làm của mình ");
            }
        }
        session.removeAttribute("notification");
        return "admin/login";
    }

    //Quên mật khẩu
    @GetMapping("/forgot-password")
    public String ForgotPassword() {
        return "admin/forgotpassword";
    }

    //Lưu tạm mật khẩu mới và token vào ReserTokenService
    @PostMapping("/reset-password")
    public String resetPassword(@RequestParam("email") String email, Model model) {
        Optional<Employee> employeeOpt = employeeService.findByEmail(email);
        Optional<Customer> customerOpt = customerService.findByEmail(email);

        if (employeeOpt.isPresent()) {
            String newPassword = generateRandomPassword(); // Tạo mật khẩu mới
            String token = UUID.randomUUID().toString();  // Tạo token ngẫu nhiên
            resetTokenService.saveResetToken(email, token, newPassword, "employee"); // Lưu token và mật khẩu mới
            sendResetPasswordEmail(email, token, newPassword, "employee"); // Gửi mật khẩu mới và token
        } else if (customerOpt.isPresent()) {
            String newPassword = generateRandomPassword(); // Tạo mật khẩu mới
            String token = UUID.randomUUID().toString();  // Tạo token ngẫu nhiên
            resetTokenService.saveResetToken(email, token, newPassword, "customer"); // Lưu token và mật khẩu mới
            sendResetPasswordEmail(email, token, newPassword, "customer"); // Gửi mật khẩu mới và token
        } else {
            model.addAttribute("error", "Email không tồn tại");
            return "admin/forgotpassword";
        }

        model.addAttribute("message", "Liên kết kích hoạt và mật khẩu mới đã được gửi tới email của bạn");
        return "admin/login";
    }

    //Tạo mật khẩu ngẫu nhiên
    private String generateRandomPassword() {
        return UUID.randomUUID().toString().substring(0, 6);
    }

    //Gửi token
    private void sendResetPasswordEmail(String email, String token, String newPassword, String userType) {
        String logoUrl = "https://res.cloudinary.com/dbe1h6ajz/image/upload/v1734018841/a6xvjpu09ly1i9lytzrl.jpg";
        String subject = "Đặt lại mật khẩu tại F6 Store";
        String content = "<html>" +
                "<body style='font-family: Arial, sans-serif; color: #333;'>" +
                "<div style='max-width: 600px; margin: 0 auto; padding: 20px; border: 1px solid #ddd;'>" +
                "<div style='text-align: center;'>" +
                "<img src='" + logoUrl + "' alt='F6 Store' style='width: 150px; margin-bottom: 20px;' />" +
                "</div>" +
                "<h2 style='text-align: center; color: #5b8fd4;'>Đặt lại mật khẩu tại F6 Store</h2>" +
                "<p>Xin chào,</p>" +
                "<p>Chúng tôi đã nhận được yêu cầu đặt lại mật khẩu cho tài khoản của bạn. Dưới đây là thông tin:</p>" +
                "<p><strong>Mật khẩu mới:</strong> " + newPassword + "</p>" +
                "<p>Vui lòng nhấn vào liên kết dưới đây để kích hoạt mật khẩu mới của bạn:</p>" +
                "<p style='text-align: center;'>" +
                "<a href='http://localhost:8080/login/activate-reset?token=" + token + "' style='display: inline-block; padding: 10px 20px; color: white; background-color: #5b8fd4; text-decoration: none; border-radius: 5px;'>Kích hoạt mật khẩu mới</a>" +
                "</p>" +
                "<p>Nếu bạn không yêu cầu đặt lại mật khẩu, xin vui lòng bỏ qua email này.</p>" +
                "<p style='color: #5b8fd4; font-size: 14px;'>Trân trọng,<br/>Đội ngũ F6 Store</p>" +
                "</div>" +
                "</body>" +
                "</html>";
        emailService.sendEmail(email, subject, content);
    }


    //Xác thực và cập nhật mật khẩu
    @GetMapping("/activate-reset")
    public String activateReset(@RequestParam("token") String token, Model model) {
        Optional<ResetToken> resetTokenOpt = resetTokenService.getResetToken(token);

        if (resetTokenOpt.isPresent()) {
            ResetToken resetToken = resetTokenOpt.get();
            String email = resetToken.getEmail();
            String newPassword = resetToken.getNewPassword(); // Mật khẩu đã gửi qua email
            String userType = resetToken.getUserType();

            if ("employee".equals(userType)) {
                Optional<Employee> employeeOpt = employeeService.findByEmail(email);
                if (employeeOpt.isPresent()) {
                    Employee employee = employeeOpt.get();
                    employee.setPassword(passwordEncoder.encode(newPassword)); // Mã hóa và lưu mật khẩu mới
                    employeeService.save(employee);
                    model.addAttribute("message", "Mật khẩu của bạn đã được kích hoạt. Vui lòng đăng nhập.");
                } else {
                    model.addAttribute("error", "Không tìm thấy tài khoản của nhân viên.");
                }
            } else if ("customer".equals(userType)) {
                Optional<Customer> customerOpt = customerService.findByEmail(email);
                if (customerOpt.isPresent()) {
                    Customer customer = customerOpt.get();
                    customer.setPassword(passwordEncoder.encode(newPassword)); // Mã hóa và lưu mật khẩu mới
                    customerService.save(customer);
                    model.addAttribute("message", "Mật khẩu của bạn đã được kích hoạt. Vui lòng đăng nhập.");
                } else {
                    model.addAttribute("error", "Không tìm thấy tài khoản khách hàng.");
                }
            }
            resetTokenService.deleteResetToken(token); // Xóa token sau khi sử dụng
        } else {
            model.addAttribute("error", "Token không hợp lệ hoặc đã hết hạn.");
        }

        return "admin/login";
    }
    //Check khi quên mk
    @GetMapping("/api/check-email")
    public ResponseEntity<Boolean> checkEmail(@RequestParam String email) {
        boolean exists = employeeService.findByEmail(email).isPresent() ||
                         customerService.findByEmail(email).isPresent();
        return ResponseEntity.ok(exists);
    }

    //Đổi mật khẩu
    @GetMapping("/change-password")
    public String ChangePassword() {
        return "admin/changepassword";
    }

    @PostMapping("/change-password")
    public String changePassword(
            @RequestParam("username") String username,
            @RequestParam("currentPassword") String currentPassword,
            @RequestParam("newPassword") String newPassword,
            @RequestParam("confirmPassword") String confirmPassword,
            Model model) {

        // Kiểm tra trong bảng Employee
        Optional<Employee> employeeOpt = employeeService.findByUsername(username);
        if (employeeOpt.isPresent()) {
            Employee employee = employeeOpt.get();

            // Cập nhật mật khẩu mới
            employee.setPassword(passwordEncoder.encode(newPassword));
            employeeService.save(employee);

            model.addAttribute("message", "Đổi mật khẩu thành công.");
            return "admin/login";
        } else {
            // Nếu không tìm thấy trong Employee, kiểm tra trong bảng Customer
            Optional<Customer> customerOpt = customerService.findByUsername(username);
            if (customerOpt.isPresent()) {
                Customer customer = customerOpt.get();

                // Cập nhật mật khẩu mới
                customer.setPassword(passwordEncoder.encode(newPassword));
                customerService.save(customer);

                model.addAttribute("message", "Đổi mật khẩu thành công.");
                return "admin/login";
            } else {
                model.addAttribute("error", "Username không tồn tại.");
                return "admin/changepassword";
            }
        }
    }
    //Check khi đổi mk
    @GetMapping("/api/check-username")
    public ResponseEntity<Boolean> checkUsername(@RequestParam String username) {
        boolean exists = employeeService.findByUsername(username).isPresent() ||
                         customerService.findByUsername(username).isPresent();
        return ResponseEntity.ok(exists);
    }

    @GetMapping("/api/check-current-password")
    public ResponseEntity<Boolean> checkCurrentPassword(
            @RequestParam String username,
            @RequestParam String currentPassword) {

        // Kiểm tra trong bảng Employee
        Optional<Employee> employeeOpt = employeeService.findByUsername(username);
        if (employeeOpt.isPresent()) {
            Employee employee = employeeOpt.get();
            boolean matches = passwordEncoder.matches(currentPassword, employee.getPassword());
            return ResponseEntity.ok(matches);
        }

        // Kiểm tra trong bảng Customer
        Optional<Customer> customerOpt = customerService.findByUsername(username);
        if (customerOpt.isPresent()) {
            Customer customer = customerOpt.get();
            boolean matches = passwordEncoder.matches(currentPassword, customer.getPassword());
            return ResponseEntity.ok(matches);
        }

        return ResponseEntity.ok(false); // Username không tồn tại
    }


    //Đăng kí
    @GetMapping("/register")
    public String showRegistrationForm() {
        return "admin/register";
    }

    @PostMapping("/register")
    public String saveCustomer(@RequestParam String name,
                               @RequestParam String phone,
                               @RequestParam String email,
                               @RequestParam String username,
                               @RequestParam String password,
                               @RequestParam String gender,
                               @RequestParam String dateOfBirth,
                               RedirectAttributes redirectAttributes) {
        try {
            String customerCode = "KH" + (customerService.count() + 1);

            Customer customer = new Customer();
            customer.setCustomerCode(customerCode);
            customer.setName(name);
            customer.setPhone(phone);
            customer.setEmail(email);
            customer.setDateOfBirth(LocalDate.parse(dateOfBirth));
            customer.setUsername(username);
            customer.setPassword(passwordEncoder.encode(password));
            customer.setGender(gender);
            customer.setStatus(true);
            customer.setVouchers(voucherService.getVouchersCurrent());

            customerService.save(customer);
            Cart cart = new Cart();
            cart.setCustomer(customer);
            cart.setStatus(true);
            cartRepo.save(cart);

            redirectAttributes.addFlashAttribute("message", "Đăng kí thành công. Vui lòng đang nhập để sử dụng dịch vụ");

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Có lỗi xảy ra khi đăng kí!");
        }
        return "redirect:/login";
    }
}