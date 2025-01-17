package com.poly.du_an_tot_nghiep_f6.controller;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LogoutController {
    @GetMapping("/somePage")  // Trang mà bạn có nút đăng xuất
    public String somePage(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        boolean isEmployee = authentication.getAuthorities().stream()
                .anyMatch(role -> role.getAuthority().equals("ROLE_EMPLOYEE"));
        model.addAttribute("isEmployee", isEmployee);
        return "somePage";  // View template có nút đăng xuất
    }
}
