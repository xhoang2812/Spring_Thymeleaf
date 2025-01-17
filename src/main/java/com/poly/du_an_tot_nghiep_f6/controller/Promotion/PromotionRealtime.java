package com.poly.du_an_tot_nghiep_f6.controller.Promotion;

import com.poly.du_an_tot_nghiep_f6.entity.Promotion;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.concurrent.CopyOnWriteArrayList;

@RestController
@RequiredArgsConstructor
public class PromotionRealtime {
    private final CopyOnWriteArrayList<SseEmitter> emitters = new CopyOnWriteArrayList<>();
    @GetMapping("/api/stream/promotion")
    public SseEmitter streamVouchers() {
        SseEmitter emitter = new SseEmitter(Long.MAX_VALUE);
        emitters.add(emitter);
        emitter.onCompletion(() -> emitters.remove(emitter));
        emitter.onTimeout(() -> emitters.remove(emitter));
        return emitter;
    }
    public void sendPromotionUpdate(Promotion updatePromotion) {
        for (SseEmitter emitter : emitters) {
            try {
                emitter.send(SseEmitter.event()
                        .name("promotionUpdate")
                        .data(updatePromotion));
            } catch (IOException e) {
                emitters.remove(emitter);
            }
        }
    }
}
