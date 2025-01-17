package com.poly.du_an_tot_nghiep_f6.controller;

import org.springframework.security.core.Authentication;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;
@ControllerAdvice
public class GlobalControllerAdvice {
    @ModelAttribute
    public void addAttributes(Model model, Authentication authentication) {
        if (authentication != null) {
            boolean isEmployee = authentication.getAuthorities().stream()
                    .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_EMPLOYEE"));
            model.addAttribute("isEmployee", isEmployee);
        }
    }
}
