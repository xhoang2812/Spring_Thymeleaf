package com.poly.du_an_tot_nghiep_f6.controller.Voucher;

import com.poly.du_an_tot_nghiep_f6.entity.Voucher;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.concurrent.CopyOnWriteArrayList;

@RestController
@RequiredArgsConstructor
public class VoucherControllerRealTimeApi {
    private final CopyOnWriteArrayList<SseEmitter> emitters = new CopyOnWriteArrayList<>();
    @GetMapping("/api/stream/vouchers")
    public SseEmitter streamVouchers() {
        SseEmitter emitter = new SseEmitter(Long.MAX_VALUE);
        emitters.add(emitter);
        emitter.onCompletion(() -> emitters.remove(emitter));
        emitter.onTimeout(() -> emitters.remove(emitter));
        return emitter;
    }
    public void sendVoucherUpdate(Voucher updatedVoucher) {
        for (SseEmitter emitter : emitters) {
            try {
                emitter.send(SseEmitter.event()
                        .name("voucherUpdate")
                        .data(updatedVoucher));
            } catch (IOException e) {
                emitters.remove(emitter);
            }
        }
    }
}
