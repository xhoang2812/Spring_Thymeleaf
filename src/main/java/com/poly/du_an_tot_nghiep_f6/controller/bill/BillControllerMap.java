package com.poly.du_an_tot_nghiep_f6.controller.bill;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class BillControllerMap {
    @RequestMapping("/bill")
    public String bill() {
        return "admin/bill/bill";
    }
}
