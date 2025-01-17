package com.poly.du_an_tot_nghiep_f6.controller.attribute;

import com.poly.du_an_tot_nghiep_f6.entity.Style;
import com.poly.du_an_tot_nghiep_f6.service.impl.StyleServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


@Controller
@RequestMapping("/kieu-dang")
@RequiredArgsConstructor
public class StyleCotroller {
    private final StyleServiceImpl styleService;


    @GetMapping("/hien-thi")
    public String hienThi(Model model,
                          @ModelAttribute("tenTonTai") String tenTonTai,
                          @ModelAttribute("createSuccess") String createSuccess,
                          @ModelAttribute("updateSuccess") String updateSuccess,
                          @ModelAttribute("data") Style data) {
        model.addAttribute("listStyle", styleService.findAll());
        if (!"true".equals(tenTonTai)) {
            model.addAttribute("tenTonTai", false);
        }
        if (!"true".equals(createSuccess)) {
            model.addAttribute("createSuccess", false);
        }
        if (!"true".equals(updateSuccess)) {
            model.addAttribute("updateSuccess", false);
        }
        return "admin/style/list";
    }

    @PostMapping("/store")
    public String store(Style style,
                        RedirectAttributes redirectAttributes) {
        Style tenTonTai = styleService.findByName(style.getName());
        if (tenTonTai != null) {
            redirectAttributes.addFlashAttribute("data", style);
            redirectAttributes.addFlashAttribute("tenTonTai", true);
            return "redirect:/kieu-dang/hien-thi";
        }
        styleService.add(style);
        redirectAttributes.addFlashAttribute("createSuccess", true);
        return "redirect:/kieu-dang/hien-thi";
    }

    @GetMapping("/update/{id}")
    public String udpate(Model model,
                         @PathVariable int id,
                         @ModelAttribute("tenTonTai") String tenTonTai) {
        Style style = styleService.findById(id);
        if (!"true".equals(tenTonTai)) {
            model.addAttribute("tenTonTai", false);
        }
        model.addAttribute("data", style);
        return "admin/style/update";
    }

    @PostMapping("/edit/{id}")
    public String store(Style style,
                        RedirectAttributes redirectAttributes,
                        @PathVariable int id) {
        Style tenTonTai = styleService.findByName(style.getName());
        if (tenTonTai != null && !(tenTonTai.getId() == id)) {
            redirectAttributes.addFlashAttribute("data", style);
            redirectAttributes.addFlashAttribute("tenTonTai", true);
            return "redirect:/kieu-dang/update/{id}";
        }
        styleService.update(style);
        redirectAttributes.addFlashAttribute("updateSuccess", true);
        return "redirect:/kieu-dang/hien-thi";
    }

    @PutMapping("/change-status/{id}")
    public ResponseEntity changeStatus(@PathVariable int id) {
        styleService.updateStatus(id);
        return ResponseEntity.ok().build();
    }
}
