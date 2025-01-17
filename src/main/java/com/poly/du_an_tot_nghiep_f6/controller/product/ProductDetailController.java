package com.poly.du_an_tot_nghiep_f6.controller.product;


import com.google.zxing.WriterException;
import com.poly.du_an_tot_nghiep_f6.entity.*;
import com.poly.du_an_tot_nghiep_f6.service.impl.*;

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
import java.util.UUID;


@Controller

@RequestMapping("/san-pham-chi-tiet")
@RequiredArgsConstructor
public class ProductDetailController {

    private final ProductDetailServiceImpl productDetailService;
    private final ProductServiceImpl productService;
    private final ColorServiceImpl colorService;
    private final SizeServiceImpl sizeService;
    private final ImageServiceImpl imageService;
    private final UploadServiceImpl uploadService;
    private final BarCodeServiceImpl barCodeService;


    @GetMapping("/hien-thi")
    public String hienThi(Model model,
                          @ModelAttribute("createSuccess") String createSuccess,
                          @ModelAttribute("updateSuccess") String updateSuccess) throws IOException, WriterException {
        List<ProductDetail> productDetailList = productDetailService.findAll();
        model.addAttribute("listProductVariant", productDetailList);

        if (!"true".equals(createSuccess)) {
            model.addAttribute("createSuccess", false);
        }
        if (!"true".equals(updateSuccess)) {
            model.addAttribute("updateSuccess", false);
        }
        model.addAttribute("sizes", sizeService.findAllByStatusTrue());
        model.addAttribute("colors", colorService.findAllByStatusTrue());
        return "admin/productDetail/list";
    }


    @GetMapping("/create")
    public String create(@ModelAttribute("data") ProductDetail data,
                         Model model,
                         @ModelAttribute("tenSizeTontai")String tenSizeTontai,
                         @ModelAttribute("tenMauTonTai") String tenMauTonTai,
                         @ModelAttribute("maAnhTonTai") String maAnhTonTai,
                         @ModelAttribute("createSuccess1") String createSuccess1) {
        if (!"true".equals(tenSizeTontai)) {
            model.addAttribute("tenSizeTontai", false);
        }
        if (!"true".equals(tenMauTonTai)) {
            model.addAttribute("tenMauTonTai", false);
        }
        if (!"true".equals(maAnhTonTai)) {
            model.addAttribute("maAnhTonTai", false);
        }
        if (!"true".equals(createSuccess1)) {
            model.addAttribute("createSuccess1", false);
        }
        model.addAttribute("color", new Color());
        model.addAttribute("size", new Size());
        model.addAttribute("image", new Image());
        model.addAttribute("listProductVariant", productDetailService.findAll());
        model.addAttribute("listProduct", productService.findAllByStatusTrue());
        model.addAttribute("listColor", colorService.findAllByStatusTrue());
        model.addAttribute("listSize", sizeService.findAllByStatusTrue());
        model.addAttribute("listImage", imageService.findAllByStatusTrue());
        return "admin/productDetail/add";
    }

    @PostMapping("/add-fastly-color")
    public String addColor(Color color, RedirectAttributes redirectAttributes) {
        Color tenTonTai = colorService.findByName(color.getName());
        if (tenTonTai != null) {
            redirectAttributes.addFlashAttribute("tenMauTonTai", true);
            return "redirect:/san-pham-chi-tiet/create";
        }
        color.setStatus(1);
        colorService.add(color);
        redirectAttributes.addFlashAttribute("createSuccess1", true);
        return "redirect:/san-pham-chi-tiet/create";
    }

    @PostMapping("/add-fastly-size")
    public String addSize(Size size, RedirectAttributes redirectAttributes) {
        Size tenTonTai = sizeService.findByName(size.getName());
        if (tenTonTai != null) {
            redirectAttributes.addFlashAttribute("tenSizeTontai", true);
            return "redirect:/san-pham-chi-tiet/create";
        }
        size.setStatus(1);
        sizeService.add(size);
        redirectAttributes.addFlashAttribute("createSuccess1", true);
        return "redirect:/san-pham-chi-tiet/create";
    }
    @PostMapping("/add-fastly-image")
    public String addImage(Image image,
                           @RequestParam("imgProduct1") MultipartFile imgProduct1,
                           @RequestParam("imgProduct2") MultipartFile imgProduct2,
                           @RequestParam("imgProduct3") MultipartFile imgProduct3,
                           RedirectAttributes redirectAttributes){
        Image maTonTai = imageService.findByCode(image.getCode());
        if (maTonTai != null) {
            redirectAttributes.addFlashAttribute("maAnhTonTai", true);
            return "redirect:/san-pham-chi-tiet/create";
        }
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
        image.setStatus(1);
        imageService.add(image);
        redirectAttributes.addFlashAttribute("createSuccess1", true);
        return "redirect:/san-pham-chi-tiet/create";
    }

    @GetMapping("/detail-by-id-product/{idProduct}")
    public String detailById(@PathVariable int idProduct, Model model) {
        model.addAttribute("sizes", sizeService.findAllByStatusTrue());
        model.addAttribute("colors", colorService.findAllByStatusTrue());
        model.addAttribute("listProductVariantByIdProduct", productDetailService.findByProductId(idProduct));
        return "admin/productDetail/variantDetail";
    }

    @GetMapping("/update/{id}")
    public String update(@PathVariable int id, Model model) {
        ProductDetail productDetail = productDetailService.findById(id);
        model.addAttribute("productVariantDetail", productDetail);
        model.addAttribute("listProduct", productService.findAllByStatusTrue());
        model.addAttribute("listColor", colorService.findAllByStatusTrue());
        model.addAttribute("listSize", sizeService.findAllByStatusTrue());
        model.addAttribute("listImage", imageService.findAllByStatusTrue());
        return "admin/productDetail/update";
    }

    @GetMapping("/detail/{id}")
    public String detail(@PathVariable int id, Model model) {
        ProductDetail productDetail = productDetailService.findById(id);
        model.addAttribute("productVariantDetail", productDetail);
        return "admin/productDetail/detail";
    }


    @PostMapping("/edit/{id}")
    public String edit(@PathVariable int id,
                       ProductDetail productDetail,
                       RedirectAttributes redirectAttributes,
                       @RequestParam("imgProduct1") MultipartFile imgProduct1,
                       @RequestParam("imgProduct2") MultipartFile imgProduct2,
                       @RequestParam("imgProduct3") MultipartFile imgProduct3) {
        if ((imgProduct1 != null && !imgProduct1.isEmpty()) ||
                (imgProduct2 != null && !imgProduct2.isEmpty()) ||
                (imgProduct3 != null && !imgProduct3.isEmpty())) {
            MultipartFile[] images = {imgProduct1, imgProduct2, imgProduct3};
            List<String> url = uploadService.saveUpLoadFile(images);
            Image image = new Image();
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
            image.setStatus(1);
            image.setCode(UUID.randomUUID().toString());
            imageService.add(image);

            productDetail.setImage(image);
            productDetailService.updateHaveImage(productDetail);
        }else{
            productDetailService.update(productDetail);
        }
        redirectAttributes.addFlashAttribute("updateSuccess", true);
        return "redirect:/san-pham-chi-tiet/hien-thi";
    }


    @PutMapping("/change-status/{id}")
    public ResponseEntity changeStatus(@PathVariable int id) {
        productDetailService.changeStatus(id);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/delete-product-variant/{id}")
    public ResponseEntity deleteProductVariant(@PathVariable int id) {
        productDetailService.deleteById(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/test/{name}")
    public ResponseEntity<ByteArrayResource> getFlimImage(@PathVariable("name") String name) {
        try {
            Path image = Path.of("uploads", name);
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

    @GetMapping("/qr/{name}")
    public ResponseEntity<ByteArrayResource> getQR(@PathVariable("name") String name) {
        try {
            Path image = Path.of("qrcode", name);
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
