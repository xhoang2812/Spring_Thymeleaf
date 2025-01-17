package com.poly.du_an_tot_nghiep_f6.controller.sale;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class SaleControllerMap {
    @RequestMapping("/sale")
    public String sale() {
        return "admin/sale/sale";
    }
}
