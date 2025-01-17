package com.poly.du_an_tot_nghiep_f6.config;

import com.poly.du_an_tot_nghiep_f6.entity.CaLam;
import com.poly.du_an_tot_nghiep_f6.entity.CaLamChiTiet;
import com.poly.du_an_tot_nghiep_f6.entity.Customer;
import com.poly.du_an_tot_nghiep_f6.entity.Employee;
import com.poly.du_an_tot_nghiep_f6.repository.CaLamChiTietRepo;
import com.poly.du_an_tot_nghiep_f6.repository.CaLamRepo;
import com.poly.du_an_tot_nghiep_f6.repository.CustomerRepo;
import com.poly.du_an_tot_nghiep_f6.repository.EmployeeRepo;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Component
public class AuthenticationSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {

    @Autowired
    private EmployeeRepo employeeRepo;
    @Autowired
    private CustomerRepo customerRepo;
    @Autowired
    private CaLamRepo caLamRepo;
    @Autowired
    private CaLamChiTietRepo caLamChiTietRepo;
    @Autowired
    private HttpSession session;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws ServletException, IOException {
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_ADMIN"));
        boolean isEmployee = authentication.getAuthorities().stream()
                .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_EMPLOYEE"));
        boolean isCustomer = authentication.getAuthorities().stream()
                .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_USER"));
//        request.getSession().setAttribute("isEmployee", isEmployee);

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        System.out.println("Tài khoản: " + userDetails.getUsername() + " đã đăng nhập vào hệ thống");

        Optional<Employee> user = employeeRepo.findByUsername(userDetails.getUsername());
        Employee employee = new Employee();
        if (user.isPresent()) {
            var userObj = user.get();
            employee = userObj;
            // Kiểm tra nếu tài khoản đã bị khóa
            if (!employee.isStatus()) { // Kiểm tra nếu status = false (tài khoản bị khóa)
                System.out.println("Tài khoản nhân viên này đã bị khóa.");
                session.setAttribute("accountStatus", "Tài khoản của bạn đã bị khóa.");
                setDefaultTargetUrl("/login");
                super.onAuthenticationSuccess(request, response, authentication);
                return;
            }
        }
        // Kiểm tra trong bảng Customer nếu không tìm thấy tài khoản trong bảng Employee
        Optional<Customer> customerOpt = customerRepo.findByUsername(userDetails.getUsername());
        if (customerOpt.isPresent()) {
            Customer customer = customerOpt.get();
            if (!customer.isStatus()) { // Kiểm tra nếu status = false (tài khoản bị khóa)
                System.out.println("Tài khoản khách hàng này đã bị khóa.");
                session.setAttribute("accountStatus", "Tài khoản của bạn đã bị khóa.");
                setDefaultTargetUrl("/login");
                super.onAuthenticationSuccess(request, response, authentication);
                return;
            }
        }

        Date date = new Date();
        LocalDate localDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        CaLam currentCaLam = new CaLam();
        for (CaLam caLam : caLamRepo.findAll()) {
            LocalDate localDateNgayLam = caLam.getNgayLam().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            if (localDate.isEqual(localDateNgayLam)) {
                currentCaLam = caLam;
                break;
            }
        }
        System.out.println(currentCaLam);
        if (currentCaLam.getId() != null) {
            List<CaLamChiTiet> caLamChiTiets = caLamChiTietRepo.findByCalam(currentCaLam);

            LocalDateTime now = LocalDateTime.now();

            LocalTime startMorning = LocalTime.of(7, 0);
            LocalTime endMorning = LocalTime.of(12, 0);
            LocalTime startAfternoon = LocalTime.of(12, 0);
            LocalTime endAfternoon = LocalTime.of(17, 0);
            LocalTime startEvening = LocalTime.of(17, 0);
            LocalTime endEvening = LocalTime.of(22, 0);

            LocalTime currentTime = now.toLocalTime();
            if (currentTime.isAfter(startMorning) && currentTime.isBefore(endMorning)) {
                if (caLamChiTiets.get(0).getEmployees().isEmpty()) { //Khong co nhan vien => ca trong => null
                    setDefaultTargetUrl("/logout");
                    System.out.println("khong co nhan vien trong ca 1");
                    session.setAttribute("notification", "false");
                    setDefaultTargetUrl("/login");
                } else {
                    if (caLamChiTiets.get(0).getEmployees().get(0).equals(employee)) {
                        System.out.println("nhan vien cua ca 1");
                        setDefaultTargetUrl("/xacnhantien");
//check nhan vien dang nhap neu trung voi nhan vien trong ca => hien nhan vien ca tiep theo luon
//                        model.addAttribute("employeesPage", caLamChiTiets.get(1).getEmployees().get(0));
                    } else {
                        System.out.println("nhan vien dang nhap khong phai nhan vien cua ca 1");
                        session.setAttribute("notification", "true");
                        setDefaultTargetUrl("/login");
//                        session.removeAttribute("notification"); // Xóa thông báo sau khi hiển thị
                    }
                }
            } else if (currentTime.isAfter(startAfternoon) && currentTime.isBefore(endAfternoon)) {
                if (caLamChiTiets.get(1).getEmployees().isEmpty()) {
                    System.out.println("khong co nhan vien trong ca 2");
                    session.setAttribute("notification", "false");
                    setDefaultTargetUrl("/login");
                } else {
                    if (caLamChiTiets.get(1).getEmployees().get(0).equals(employee)) {
                        System.out.println("nhan vien cua ca 2");
                        setDefaultTargetUrl("/xacnhantien");
//                        model.addAttribute("employeesPage", caLamChiTiets.get(2).getEmployees().get(0));
                    } else {
//                        model.addAttribute("employeesPage", caLamChiTiets.get(1).getEmployees().get(0));
                        System.out.println("nhan vien dang nhap khong phai nhan vien cua ca 2");
//                        request.getSession().setAttribute("notification", "Bạn đang đăng nhập không phải ca làm của bạn");
//                        response.sendRedirect("/login");
                        session.setAttribute("notification", "true");
                        setDefaultTargetUrl("/login");
                    }
                }
            } else if (currentTime.isAfter(startEvening) && currentTime.isBefore(endEvening)) {
                if (caLamChiTiets.get(2).getEmployees().isEmpty()) {
                    System.out.println("khong co nhan vien trong ca 3");
                    session.setAttribute("notification", "false");
                    setDefaultTargetUrl("/login");

                } else {
                    if (caLamChiTiets.get(2).getEmployees().get(0).equals(employee)) {
                        System.out.println("nhan vien ca 3");
                        setDefaultTargetUrl("/xacnhantien");
                    } else {
                        System.out.println("nhan vien dang nhap khong phai nhan vien cua ca 3");
                        session.setAttribute("notification", "true");
                        setDefaultTargetUrl("/login");
                    }
                }
            } else {
                System.out.println("ngoai gio lam viec");
                session.setAttribute("notification", "false");
                setDefaultTargetUrl("/login");
            }
        } else {
            session.setAttribute("notification", "false");
            setDefaultTargetUrl("/login");
        }
        if (isAdmin) {
            System.out.println("tao la admin");
            setDefaultTargetUrl("/chart");
        } else if (isCustomer) {
            setDefaultTargetUrl("/home");
            setAlwaysUseDefaultTargetUrl(true);
        } // Buộc luôn chuyển đến URL mặc định
        super.onAuthenticationSuccess(request, response, authentication);
    }
}
