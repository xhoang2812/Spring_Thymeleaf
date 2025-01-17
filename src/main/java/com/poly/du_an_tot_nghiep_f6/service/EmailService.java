package com.poly.du_an_tot_nghiep_f6.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

//    public void sendEmail(String to, String subject, String text) {
//        SimpleMailMessage message = new SimpleMailMessage();
//        message.setTo(to);
//        message.setSubject(subject);
//        message.setText(text);
//        mailSender.send(message);
//    }

    public void sendEmail(String to, String subject, String htmlContent) {
        MimeMessage message = mailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlContent, true);
            mailSender.send(message);
        } catch (MessagingException e) {
            // Xử lý ngoại lệ nếu có
        }
    }


    public String buildHtmlEmailContent(String username, String password) {
        String logoUrl = "https://res.cloudinary.com/dbe1h6ajz/image/upload/v1734018841/a6xvjpu09ly1i9lytzrl.jpg";
        return "<html>" +
                "<body style='font-family: Arial, sans-serif; color: #333;'>" +
                "<div style='max-width: 600px; margin: 0 auto; padding: 20px; border: 1px solid #ddd;'>" +
                "<div style='text-align: center;'>" +
                "<img src='" + logoUrl + "' alt='F6 Store' style='width: 150px; margin-bottom: 20px;' />" +
                "</div>" +
                "<h2 style='text-align: center; color: #5b8fd4;'>Thông tin đăng nhập tại F6 Store</h2>" +
                "<p>Xin chào,</p>" +
                "<p>Cảm ơn bạn đã đăng ký tài khoản tại F6 Store! Dưới đây là thông tin đăng nhập của bạn:</p>" +
                "<p><strong>Tên đăng nhập:</strong> " + username + "</p>" +
                "<p><strong>Mật khẩu:</strong> " + password + "</p>" +
                "<p>Vui lòng đăng nhập và thay đổi mật khẩu để bảo mật tài khoản của bạn.</p>" +
                "<p style='color: #5b8fd4; font-size: 14px;'>Trân trọng,<br/>Đội ngũ F6 Store</p>" +
                "</div>" +
                "</body>" +
                "</html>";
    }




}
