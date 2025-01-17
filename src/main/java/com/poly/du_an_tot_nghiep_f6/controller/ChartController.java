package com.poly.du_an_tot_nghiep_f6.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ChartController {
    @GetMapping("/chart")
    public String chart() {
        return "admin/chart/chart";
    }
}
