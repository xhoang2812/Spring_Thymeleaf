package com.poly.du_an_tot_nghiep_f6.service.impl;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class BarCodeServiceImpl {

    public String genertateQrCode(Integer id, int weight, int height) {
        if (id == null) {
            throw new IllegalArgumentException("productDetail is null");
        }
        String nameOfQrcode = UUID.randomUUID() + "-QRCODE.png";
        try {
            Path path = Paths.get("qrcode/");
            if (!Files.exists(path)) {
                Files.createDirectory(path);
            }
            String qrcodeName = path + "/" + nameOfQrcode;

            QRCodeWriter qrCodeWriter = new QRCodeWriter(); // tạo qr
            BitMatrix bitMatrix = qrCodeWriter.encode(String.valueOf(id), BarcodeFormat.QR_CODE, weight, height); // mã hóa dự liệu thành ma trận

            Path imagePath = FileSystems.getDefault().getPath(qrcodeName);
            MatrixToImageWriter.writeToPath(bitMatrix, "PNG", imagePath);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return nameOfQrcode;
    }

    public void deleteByImageName(String name) {
        Path path = Paths.get("qrcode/");
        Path filePath = path.resolve(name);
        try {
            Files.delete(filePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
