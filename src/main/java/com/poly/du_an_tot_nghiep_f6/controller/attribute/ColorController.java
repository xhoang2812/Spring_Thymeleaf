package com.poly.du_an_tot_nghiep_f6.controller.attribute;

import com.poly.du_an_tot_nghiep_f6.entity.Color;
import com.poly.du_an_tot_nghiep_f6.service.impl.ColorServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


@Controller
@RequestMapping("/mau-sac")
@RequiredArgsConstructor
public class ColorController {

    private final ColorServiceImpl colorService;


    @GetMapping("/hien-thi")
    public String hienThi(Model model,
                          @ModelAttribute("tenTonTai") String tenTonTai,
                          @ModelAttribute("createSuccess") String createSuccess,
                          @ModelAttribute("updateSuccess") String updateSuccess,
                          @ModelAttribute("data") Color color) {
        model.addAttribute("listColor", colorService.findAll());
        if (!"true".equals(tenTonTai)) {
            model.addAttribute("tenTonTai", false);
        }
        if (!"true".equals(createSuccess)) {
            model.addAttribute("createSuccess", false);
        }
        if (!"true".equals(updateSuccess)) {
            model.addAttribute("updateSuccess", false);
        }
        return "admin/color/list";
    }

    @PostMapping("/store")
    public String store(Color color,
                        RedirectAttributes redirectAttributes) {
        Color tenTonTai = colorService.findByName(color.getName());
        if (tenTonTai != null) {
            redirectAttributes.addFlashAttribute("data", color);
            redirectAttributes.addFlashAttribute("tenTonTai", true);
            return "redirect:/mau-sac/hien-thi";
        }
        colorService.add(color);
        redirectAttributes.addFlashAttribute("createSuccess", true);
        return "redirect:/mau-sac/hien-thi";
    }

    @GetMapping("/update/{id}")
    public String udpate(Model model,
                         @PathVariable int id,
                         @ModelAttribute("tenTonTai") String tenTonTai) {
        Color color = colorService.findById(id);
        if (!"true".equals(tenTonTai)) {
            model.addAttribute("tenTonTai", false);
        }
        model.addAttribute("data", color);
        return "admin/color/update";
    }

    @PostMapping("/edit/{id}")
    public String store(Color color,
                        RedirectAttributes redirectAttributes,
                        @PathVariable int id) {
        Color tenTonTai = colorService.findByName(color.getName());
        if (tenTonTai != null && !(tenTonTai.getId() == id)) {
            redirectAttributes.addFlashAttribute("data", color);
            redirectAttributes.addFlashAttribute("tenTonTai", true);
            return "redirect:/mau-sac/update/{id}";
        }
        colorService.update(color);
        redirectAttributes.addFlashAttribute("updateSuccess", true);
        return "redirect:/mau-sac/hien-thi";
    }

    @PutMapping("/change-status/{id}")
    public ResponseEntity changeStatus(@PathVariable int id) {
        colorService.updateStatus(id);
        return ResponseEntity.ok().build();
    }

}
