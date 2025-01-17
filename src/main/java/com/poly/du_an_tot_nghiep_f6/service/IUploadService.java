package com.poly.du_an_tot_nghiep_f6.service;

import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface IUploadService {
    public List<String> saveUpLoadFile(MultipartFile[] files);
    public void deleteByImageName(String name);
}
