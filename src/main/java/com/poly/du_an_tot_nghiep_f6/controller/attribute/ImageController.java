package com.poly.du_an_tot_nghiep_f6.controller.attribute;


import com.poly.du_an_tot_nghiep_f6.entity.Image;
import com.poly.du_an_tot_nghiep_f6.service.impl.ImageServiceImpl;
import com.poly.du_an_tot_nghiep_f6.service.impl.UploadServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

@Controller
@RequestMapping("/hinh-anh")
@RequiredArgsConstructor
public class ImageController {
    private final ImageServiceImpl imageService;
    private final UploadServiceImpl uploadService;

    @GetMapping("/hien-thi")
    public String hienThi(Model model,
                          @ModelAttribute("maTonTai") String maTonTai,
                          @ModelAttribute("createSuccess") String createSuccess,
                          @ModelAttribute("updateSuccess") String updateSuccess,
                          @ModelAttribute("data") Image data) {
        model.addAttribute("listImage", imageService.findAll());
        if (!"true".equals(maTonTai)) {
            model.addAttribute("maTonTai", false);
        }
        if (!"true".equals(createSuccess)) {
            model.addAttribute("createSuccess", false);
        }
        if (!"true".equals(updateSuccess)) {
            model.addAttribute("updateSuccess", false);
        }
        return "admin/image/list";
    }


    @PostMapping("/store")
    public String store(@RequestParam("imgProduct1") MultipartFile imgProduct1,
                        @RequestParam("imgProduct2") MultipartFile imgProduct2,
                        @RequestParam("imgProduct3") MultipartFile imgProduct3,
                        RedirectAttributes redirectAttributes,
                        Image image) throws IOException {
        MultipartFile[] images = {imgProduct1, imgProduct2, imgProduct3};
        List<String> url = uploadService.saveUpLoadFile(images);
        for (int i = 0; i < url.size(); i++) {
            if (i == 0) {
                image.setUrl1(url.get(i) );
            }
            if (i == 1) {
                image.setUrl2(url.get(i) );
            }
            if (i == 2) {
                image.setUrl3(url.get(i)) ;
            }
        }
        Image maTonTai = imageService.findByCode(image.getCode());
        if (maTonTai != null) {
            redirectAttributes.addFlashAttribute("data", image);
            redirectAttributes.addFlashAttribute("maTonTai", true);
            return "redirect:/hinh-anh/hien-thi";
        }
        imageService.add(image);
        redirectAttributes.addFlashAttribute("createSuccess", true);
        return "redirect:/hinh-anh/hien-thi";
    }

    @GetMapping("/update/{id}")
    public String update(@PathVariable int id,
                         @ModelAttribute("maTonTai") String maTonTai,
                         Model model) {
        Image image = imageService.findById(id);
        if (!"true".equals(maTonTai)) {
            model.addAttribute("maTonTai", false);
        }
        model.addAttribute("data", image);
        return "admin/image/update";
    }

    @PostMapping("/edit/{id}")
    public String edit(@RequestParam("imgProduct1") MultipartFile imgProduct1,
                       @RequestParam("imgProduct2") MultipartFile imgProduct2,
                       @RequestParam("imgProduct3") MultipartFile imgProduct3,
                       Image image,
                       RedirectAttributes redirectAttributes,
                       @PathVariable int id) throws IOException {
        MultipartFile[] images = {imgProduct1, imgProduct2, imgProduct3};
        List<String> url = uploadService.saveUpLoadFile(images);
        for (int i = 0; i <= url.size(); i++) {
            if (i == 0) {
                image.setUrl1(url.get(i));
            }
            if (i == 1) {
                image.setUrl2(url.get(i));
            }
            if (i == 2) {
                image.setUrl3(url.get(i));
            }
        }
        Image maTonTai = imageService.findByCode(image.getCode());
        if (maTonTai != null && !(maTonTai.getId() == id)) {
            redirectAttributes.addFlashAttribute("data", image);
            redirectAttributes.addFlashAttribute("maTonTai", true);
            return "redirect:/hinh-anh/update/{id}";
        }
        Image imageDetail = imageService.findById(id);
        String anh1 = imageDetail.getUrl1();
        String anh2 = imageDetail.getUrl2();
        String anh3 = imageDetail.getUrl3();
        uploadService.deleteByImageName(anh1);
        uploadService.deleteByImageName(anh2);
        uploadService.deleteByImageName(anh3);
        imageService.update(image);
        redirectAttributes.addFlashAttribute("updateSuccess", true);
        return "redirect:/hinh-anh/hien-thi";
    }

    @PutMapping("/change-status/{id}")
    public ResponseEntity changeStatus(@PathVariable int id) {
        imageService.updateStatus(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/test/{name}")
    public ResponseEntity<ByteArrayResource> getFlimImage(@PathVariable("name")String name){
        try {
            Path image = Path.of("uploads",name);
            byte[] buffer = Files.readAllBytes(image);
            ByteArrayResource byteArrayResource = new ByteArrayResource(buffer);
            return ResponseEntity
                    .ok()
                    .contentLength(buffer.length)
                    .contentType(MediaType.parseMediaType("image/jpeg"))
                    .contentType(MediaType.parseMediaType("image/png"))
                    .contentType(MediaType.parseMediaType("image/jpg"))
                    .body(byteArrayResource);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
