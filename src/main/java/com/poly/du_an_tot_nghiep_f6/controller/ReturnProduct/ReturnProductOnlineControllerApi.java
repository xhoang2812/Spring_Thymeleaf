package com.poly.du_an_tot_nghiep_f6.controller.ReturnProduct;

import com.poly.du_an_tot_nghiep_f6.entity.Bill;
import com.poly.du_an_tot_nghiep_f6.entity.BillDetail;
import com.poly.du_an_tot_nghiep_f6.entity.Trade;
import com.poly.du_an_tot_nghiep_f6.entity.TradeProduct;
import com.poly.du_an_tot_nghiep_f6.repository.BillDetailRepo;
import com.poly.du_an_tot_nghiep_f6.repository.BillRepo;
import com.poly.du_an_tot_nghiep_f6.request.TradeProductRequest;
import com.poly.du_an_tot_nghiep_f6.request.TradeQuest;
import com.poly.du_an_tot_nghiep_f6.service.ReturnProductOnlineService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/home/customer-bill-management/api/returnOnline")
public class ReturnProductOnlineControllerApi {
    private final ReturnProductOnlineService returnProductOnlineService;
    private final BillRepo billRepo;
    private final BillDetailRepo billDetailRepo;

    @PostMapping("/createTradeOnline/{idBill}")
    public Trade createTradeOnline(@PathVariable Integer idBill, @RequestBody List<BillDetail> billDetails){
        try {
            Bill bill = billRepo.getReferenceById(idBill);
            List<TradeProduct> tradeProducts = new ArrayList<>();
            for(BillDetail billDetail : billDetails){
                BillDetail billDetail1 = billDetailRepo.getReferenceById(billDetail.getId());
                TradeProduct tradeProduct = new TradeProduct();
                tradeProduct.setBillDetail(billDetail1);
                tradeProduct.setQuantity(billDetail.getQuantity());
                tradeProducts.add(tradeProduct);
            }
            return returnProductOnlineService.createTradeRequest(bill, tradeProducts);
        }catch (Exception e){
            System.out.println(e.getMessage());
            return null;
        }
    }

    @GetMapping("/tradeDetail/{idBill}")
    public List<TradeProduct> showTradeDetail(@PathVariable Integer idBill){
        try {
            Bill bill = billRepo.getReferenceById(idBill);
            return returnProductOnlineService.getTradeProductByTrade(bill);
        }catch (Exception e){
            System.out.println(e.getMessage());
            return null;
        }
    }

    @PostMapping("/createTrade/{idBill}")
    public String createTrade(
            @PathVariable Integer idBill,
            @RequestBody TradeQuest tradeQuest
            ){
        System.out.println(tradeQuest);
        return null;
    }

    @GetMapping("/reloadBill/{idBill}")
    public Bill getBill(@PathVariable Integer idBill){
        return returnProductOnlineService.reloadBill(billRepo.getReferenceById(idBill));
    }



}
