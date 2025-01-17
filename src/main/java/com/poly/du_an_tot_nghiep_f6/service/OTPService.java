package com.poly.du_an_tot_nghiep_f6.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Service
public class OTPService {
    @Autowired
    private EmailService emailService;

    private final Map<String, String> otpStorage = new HashMap<>();

    public String generateAndSendOtp(String email) {
        String otp = String.valueOf(new Random().nextInt(999999));
        otpStorage.put(email, otp);

        // Logic gửi email
        try {
            sendOtpEmail(email, otp);
            return otp;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public boolean verifyOtp(String otp) {
        return otpStorage.values().remove(otp); // Xác minh OTP và xóa khỏi bộ nhớ
    }

    private void sendOtpEmail(String email, String otp) {
        System.out.println("Gửi OTP: " + otp + " tới email: " + email);
        String logoUrl = "https://res.cloudinary.com/dbe1h6ajz/image/upload/v1734018841/a6xvjpu09ly1i9lytzrl.jpg";
        String subject = "Mã OTP xác thực của bạn";

        String content = "<html>" +
                "<body style='font-family: Arial, sans-serif; color: #333;'>" +
                "<div style='max-width: 600px; margin: 0 auto; padding: 20px; border: 1px solid #ddd;'>" +
                "<div style='text-align: center;'>" +
                "<img src='" + logoUrl + "' alt='F6 Store' style='width: 150px; margin-bottom: 20px;' />" +
                "</div>" +
                "<h2 style='text-align: center; color: #5b8fd4;'>Xác thực OTP</h2>" +
                "<p>Xin chào,</p>" +
                "<p>Chúng tôi đã nhận được yêu cầu xác thực từ bạn. Dưới đây là mã OTP của bạn:</p>" +
                "<p style='font-size: 24px; font-weight: bold; text-align: center; color: #5b8fd4;'>" + otp + "</p>" +
                "<p>Vui lòng nhập mã OTP này vào ứng dụng của chúng tôi để hoàn tất xác thực.</p>" +
                "<p>Nếu bạn không yêu cầu mã OTP này, xin vui lòng bỏ qua email này.</p>" +
                "<p style='color: #5b8fd4; font-size: 14px;'>Trân trọng,<br/>Đội ngũ F6 Store</p>" +
                "</div>" +
                "</body>" +
                "</html>";

        emailService.sendEmail(email, subject, content);

    }
}

