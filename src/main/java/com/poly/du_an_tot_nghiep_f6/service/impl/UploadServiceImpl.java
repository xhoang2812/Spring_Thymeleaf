package com.poly.du_an_tot_nghiep_f6.service.impl;

import com.poly.du_an_tot_nghiep_f6.service.IUploadService;
import org.apache.commons.io.FilenameUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Service
public class UploadServiceImpl implements IUploadService {

private final Path storageFolder = Path.of("uploads");

    private boolean isImageFile(MultipartFile file){
        String fileExtension = FilenameUtils.getExtension(file.getOriginalFilename());
        return Arrays.asList("png","jpg","jpeg","bmp","webp").contains(fileExtension.trim().toLowerCase());}

    public List<String> saveUpLoadFile(MultipartFile[] files) {
        List<String> storedFileNames = new ArrayList<>();
        for (MultipartFile file : files) {
            try {
                if (file.isEmpty()) throw new RuntimeException("Lỗi vì lưu file rỗng");
                if (!isImageFile(file)) throw new RuntimeException("Chỉ được up ảnh");
                float fileMb = (float) file.getSize() / 1_000_000;

                if (fileMb > 5.0f)
                    throw new RuntimeException("File bé hơn 5mb");

                String fileExtension = FilenameUtils.getExtension(file.getOriginalFilename());
                String generatedFileName = UUID.randomUUID().toString().replace("-", "") + "." + fileExtension;

                Path destinationFilePath = this.storageFolder.resolve(Paths.get(generatedFileName)).normalize().toAbsolutePath();

                if (!destinationFilePath.getParent().equals(this.storageFolder.toAbsolutePath()))
                    throw new RuntimeException("Ko thể lưu đc khi chọn thư muc ngoài");

                try (InputStream inputStream = file.getInputStream()) {
                    Files.copy(inputStream, destinationFilePath, StandardCopyOption.REPLACE_EXISTING);
                }

                storedFileNames.add(generatedFileName);
            } catch (IOException e) {
                throw new RuntimeException("Ko thể lưu file " + file.getOriginalFilename(), e);
            }
        }
        return storedFileNames;
    }

    public void deleteByImageName(String name) {
        Path filePath = storageFolder.resolve(name);
        try {
            Files.delete(filePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String storeFile(MultipartFile file) {
        try {
            if (file.isEmpty()) throw new RuntimeException("Failed to store empty file");
            if (!isImageFile(file)) throw new RuntimeException("You can only upload image file");
            float fileMb = (float) file.getSize() /1_000_000;

            if (fileMb>5.0f)
                throw new RuntimeException("File must be less than 5mb");

            String fileExtension = FilenameUtils.getExtension(file.getOriginalFilename());
            String generatedFileName = UUID.randomUUID().toString().replace("-","");
            generatedFileName = generatedFileName +"."+fileExtension;

            Path destinationFilePath = this.storageFolder.resolve(Paths.get(generatedFileName)).normalize().toAbsolutePath();

            if (!destinationFilePath.getParent().equals(this.storageFolder.toAbsolutePath()))
                throw new RuntimeException("Can not store file outside current directory");

            try(InputStream inputStream = file.getInputStream()){
                Files.copy(inputStream,destinationFilePath, StandardCopyOption.REPLACE_EXISTING);
            }
            return generatedFileName;
        }catch (IOException e){
            throw new RuntimeException("Can not store file ",e);
        }
    }

}
