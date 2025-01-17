package com.poly.du_an_tot_nghiep_f6.service;

import com.poly.du_an_tot_nghiep_f6.entity.*;
import com.poly.du_an_tot_nghiep_f6.service.impl.ProductDetailServiceImpl;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.nio.charset.StandardCharsets;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class MailService {
    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private TemplateEngine templateEngine;

    @Autowired
    private ProductDetailServiceImpl productDetailService;

    @Async
    public void sendEmail(String to, String customerName, String voucherName, String code, String oldCode, int discount, Date dateStart, Date dateEnd, int minimumOrder, int maximumReduction, String template) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED,
                StandardCharsets.UTF_8.name());

        String formattedDiscount = formatDiscount(discount);

        String formattedDateStart = formatDateTime(dateStart);
        String formattedDateEnd = formatDateTime(dateEnd);

        String formattedMinimumOrder = formatCurrency(minimumOrder);
        String formattedMaximumReduction = formatCurrency(maximumReduction);

        Context context = new Context();
        context.setVariable("name", customerName);
        context.setVariable("voucherName", voucherName);
        context.setVariable("code", code);
        context.setVariable("discount", formattedDiscount);
        context.setVariable("dateStart", formattedDateStart);
        context.setVariable("dateEnd", formattedDateEnd);
        context.setVariable("minimumOrder", formattedMinimumOrder);
        context.setVariable("maximumReduction", formattedMaximumReduction);
        if (template.equals("mailUpdateVoucher")) {
            context.setVariable("oldCode", oldCode);
        }
        String htmlContent = templateEngine.process(template, context);

        helper.setTo(to);
        helper.setSubject(getSubject(template));
        helper.setText(htmlContent, true);

        // Gửi email
        mailSender.send(message);
    }

    @Async
    public void sendEmailOnline(String to, String customerName, Bill bill,
                                List<CartDetail> cartDetails, LocalDate dateBuy, String template) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED,
                StandardCharsets.UTF_8.name());

        Context context = new Context();
        context.setVariable("name", customerName);
        context.setVariable("bill", bill);
        context.setVariable("dateBuy", dateBuy);
        context.setVariable("cartDetails", cartDetails);
        String htmlContent = templateEngine.process(template, context);
        helper.setTo(to);
        helper.setSubject("Thông báo về đơn hàng");
        helper.setText(htmlContent, true);

        // Gửi email
        mailSender.send(message);
    }

    @Async
    public void sendEmailOnlineWithoutLogin(String to, String customerName, Bill bill,
                                List<BillDetail> billDetails, LocalDate dateBuy, String template) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED,
                StandardCharsets.UTF_8.name());

        Context context = new Context();
        context.setVariable("name", customerName);
        context.setVariable("bill", bill);
        context.setVariable("dateBuy", dateBuy);
        context.setVariable("billDetails", billDetails);
        String url = "http://localhost:8080/home/search-bill-online/"+bill.getId();
        context.setVariable("url", url);
        String htmlContent = templateEngine.process(template, context);
        helper.setTo(to);
        helper.setSubject("Thông báo về đơn hàng");
        helper.setText(htmlContent, true);

        // Gửi email
        mailSender.send(message);
    }

    private String formatDiscount(int discount) {
        if (discount < 100) {
            return discount + "%";
        } else {
            return NumberFormat.getInstance().format(discount) + " VND";
        }
    }

    private String formatDateTime(Date date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        LocalDateTime localDateTime = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
        return localDateTime.format(formatter);
    }

    private String formatCurrency(int amount) {
        return NumberFormat.getInstance().format(amount) + " VND";
    }

    private String getSubject(String template) {
        return switch (template) {
            case "mailApology" -> "Xin lưu ý: Voucher của bạn không còn khả dụng";
            case "mailCongratulatory" -> "🎉 Chúc mừng! Bạn đã nhận được một voucher đặc biệt!";
            default -> "Xin lưu ý: Voucher của bạn đã được cập nhật";
        };
    }

    @Async
    public void mailChuyenTien(List<TradeProduct> tradeProducts, List<BillDetail> billDetails, Trade trade, String template) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED,
                StandardCharsets.UTF_8.name());

        Context context = new Context();
        context.setVariable("name", trade.getUserInfo());
        List<TradeProductItem> tradeProductItems = new ArrayList<>();
        for(TradeProduct tradeProduct : tradeProducts){

        }
        context.setVariable("tradeProducts", tradeProducts);
        context.setVariable("billDetail", billDetails);
        context.setVariable("trade", trade);

        String htmlContent = templateEngine.process(template, context);

        helper.setTo(trade.getEmail());
        helper.setSubject("[F6 Store] Thông báo về đơn trả hàng của hoá đơn #" + trade.getBill().getId());
        helper.setText(htmlContent, true);

        // Gửi email
        mailSender.send(message);
    }

    @Async
    public void mailThongBaoHoanTien(String to,Bill bill, Trade trade, String template) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED,
                StandardCharsets.UTF_8.name());

        Context context = new Context();
        context.setVariable("bill", bill);
        context.setVariable("trade", trade);

        String htmlContent = templateEngine.process(template, context);

        helper.setTo(to);
        helper.setSubject("[F6 Store] Thông báo yêu cầu hoàn tiền của hoá đơn #" + trade.getBill().getId());
        helper.setText(htmlContent, true);

        // Gửi email
        mailSender.send(message);
    }


}
