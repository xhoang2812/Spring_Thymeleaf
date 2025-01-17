package com.poly.du_an_tot_nghiep_f6.service;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class VoucherRealTimeService {
    private final VoucherService voucherService;
    private final PromotionService promotionService;

    @Scheduled(cron = "*/1 * * * * ?")
    @Transactional
    public void updateVoucherStatus(){
        voucherService.updateConditionVoucher();
    }

    @Scheduled(cron = "*/1 * * * * ?")
    public void updatePromotionStatus(){
        promotionService.updateStatusPromotion();
    }
}
