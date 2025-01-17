package com.poly.du_an_tot_nghiep_f6.controller.ReturnProduct;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/returnProduct")
public class ReturnProductControllerMap {

    @GetMapping("")
    public String showReturnProductPage1(){
        return "admin/returnProduct/searchBill";
    }
    @GetMapping("/redirectUrl")
    public String showReturnProductPage(
            @RequestParam(required = false) Integer idTrade,
            @RequestParam(required = false) String message
            ){
        if(idTrade != null && message.equals("Successful.")){
            System.out.println(idTrade);
        }
        return "redirect:/returnProduct";
    }
    @GetMapping("/billReturn")
    public String returnPage(){
        return "admin/returnProduct/returnProduct";
    }
    @GetMapping("/returnProductDetail")
    public String returnProductDetail(){
        return "/admin/returnProduct/returnProductDetail";
    }
    @GetMapping("/returnMoney")
    public String returnMoney(){
        return "/admin/returnProduct/returnMoney";
    }
}
