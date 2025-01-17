package com.poly.du_an_tot_nghiep_f6.service;

import com.poly.du_an_tot_nghiep_f6.entity.ResetToken;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class ResetTokenService {


    private Map<String, ResetToken> tokenStore = new HashMap<>();

    public void saveResetToken(String email, String token, String newPassword, String userType) {
        ResetToken resetToken = new ResetToken(email, token, newPassword, userType);
        tokenStore.put(token, resetToken);
    }

    public Optional<ResetToken> getResetToken(String token) {
        return Optional.ofNullable(tokenStore.get(token));
    }

    public void deleteResetToken(String token) {
        tokenStore.remove(token);
    }
}
