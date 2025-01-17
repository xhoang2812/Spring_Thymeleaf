package com.poly.du_an_tot_nghiep_f6.controller.attribute;

import com.poly.du_an_tot_nghiep_f6.entity.Brand;
import com.poly.du_an_tot_nghiep_f6.service.impl.BrandServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/thuong-hieu")
@RequiredArgsConstructor
public class BrandController {

    private final BrandServiceImpl brandService;

    @GetMapping("/hien-thi")
    public String hienThi(Model model,
                          @ModelAttribute("tenTonTai") String tenTonTai,
                          @ModelAttribute("createSuccess") String createSuccess,
                          @ModelAttribute("updateSuccess") String updateSuccess,
                          @ModelAttribute("data") Brand data) {
        model.addAttribute("listBrand", brandService.findAll());
        if (!"true".equals(tenTonTai)) {
            model.addAttribute("tenTonTai", false);
        }
        if (!"true".equals(createSuccess)) {
            model.addAttribute("createSuccess", false);
        }
        if (!"true".equals(updateSuccess)) {
            model.addAttribute("updateSuccess", false);
        }
        return "admin/brand/list";
    }

    @PostMapping("/store")
    public String store(Brand brand,
                        RedirectAttributes redirectAttributes) {
        Brand tenTonTai = brandService.findByName(brand.getName());
        if (tenTonTai != null) {
            redirectAttributes.addFlashAttribute("data", brand);
            redirectAttributes.addFlashAttribute("tenTonTai", true);
            return "redirect:/thuong-hieu/hien-thi";
        }
        brandService.add(brand);
        redirectAttributes.addFlashAttribute("createSuccess", true);
        return "redirect:/thuong-hieu/hien-thi";
    }

    @GetMapping("/update/{id}")
    public String udpate(Model model,
                         @PathVariable int id,
                         @ModelAttribute("tenTonTai") String tenTonTai) {
        Brand brand = brandService.findById(id);
        if (!"true".equals(tenTonTai)) {
            model.addAttribute("tenTonTai", false);
        }
        model.addAttribute("data", brand);
        return "admin/brand/update";
    }

    @PostMapping("/edit/{id}")
    public String store(Brand brand,
                        RedirectAttributes redirectAttributes,
                        @PathVariable int id) {
        Brand tenTonTai = brandService.findByName(brand.getName());
        if (tenTonTai != null && !(tenTonTai.getId() == id)) {
            redirectAttributes.addFlashAttribute("data", brand);
            redirectAttributes.addFlashAttribute("tenTonTai", true);
            return "redirect:/thuong-hieu/update/{id}";
        }
        brandService.update(brand);
        redirectAttributes.addFlashAttribute("updateSuccess", true);
        return "redirect:/thuong-hieu/hien-thi";
    }

    @PutMapping("/change-status/{id}")
    public ResponseEntity changeStatus(@PathVariable int id) {
        brandService.updateTrangThai(id);
        return ResponseEntity.ok().build();
    }


}

