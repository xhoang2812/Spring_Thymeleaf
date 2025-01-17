package com.poly.du_an_tot_nghiep_f6.controller;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class CalamController {
    @GetMapping("/calam")
    public String getCaLamPage(){
        return "admin/giaoca/calam";
    }
    @GetMapping("/checkCaLam")
    public String xemCaLamPage(Authentication authentication){
        if (authentication == null){
            return "/admin/login";
        }
        return "/admin/giaoca/xemCL";
    }
}
