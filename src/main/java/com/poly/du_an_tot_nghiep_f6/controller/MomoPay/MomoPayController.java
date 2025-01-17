package com.poly.du_an_tot_nghiep_f6.controller.MomoPay;

import com.poly.du_an_tot_nghiep_f6.MomoPay.config.Environment;
import com.poly.du_an_tot_nghiep_f6.MomoPay.enums.RequestType;
import com.poly.du_an_tot_nghiep_f6.MomoPay.models.PaymentResponse;
import com.poly.du_an_tot_nghiep_f6.MomoPay.processor.CreateOrderMoMo;
import com.poly.du_an_tot_nghiep_f6.MomoPay.shared.utils.LogUtils;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin("*")
public class MomoPayController {
    @GetMapping("/paymentMomo/{idTrade}")
    public PaymentResponse payment(@RequestParam Integer money, @PathVariable Integer idTrade) throws Exception {
        LogUtils.init();
        String requestId = String.valueOf(System.currentTimeMillis());
        String orderId = String.valueOf(System.currentTimeMillis());
        Long transId = 2L;
        long amount = money;

        String orderInfo = "Thanh toán tiền ship cho trả hàng hoá đơn #2";
        String returnURL = "http://localhost:8080/returnProduct/redirectUrl?idTrade=" + idTrade;
        String notifyURL = "https://google.com.vn";

        Environment environment = Environment.selectEnv("dev");


//      Remember to change the IDs at enviroment.properties file

        /***
         * create payment with capture momo wallet
         */
        PaymentResponse captureWalletMoMoResponse = CreateOrderMoMo.process(environment, orderId, requestId, Long.toString(amount), orderInfo, returnURL, notifyURL, "", RequestType.CAPTURE_WALLET, Boolean.TRUE);
        return captureWalletMoMoResponse;
    }
}
