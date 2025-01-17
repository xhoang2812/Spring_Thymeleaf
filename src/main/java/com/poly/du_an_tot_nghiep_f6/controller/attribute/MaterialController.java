package com.poly.du_an_tot_nghiep_f6.controller.attribute;

import com.poly.du_an_tot_nghiep_f6.entity.Material;
import com.poly.du_an_tot_nghiep_f6.service.impl.MaterialServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


@Controller
@RequestMapping("/chat-lieu")
@RequiredArgsConstructor
public class MaterialController {

    private final MaterialServiceImpl materialService;


    @GetMapping("/hien-thi")
    public String hienThi(Model model,
                          @ModelAttribute("tenTonTai") String tenTonTai,
                          @ModelAttribute("createSuccess") String createSuccess,
                          @ModelAttribute("updateSuccess") String updateSuccess,
                          @ModelAttribute("data") Material data) {
        model.addAttribute("listMaterial", materialService.findAll());
        if (!"true".equals(tenTonTai)) {
            model.addAttribute("tenTonTai", false);
        }
        if (!"true".equals(createSuccess)) {
            model.addAttribute("createSuccess", false);
        }
        if (!"true".equals(updateSuccess)) {
            model.addAttribute("updateSuccess", false);
        }
        return "admin/material/list";
    }

    @PostMapping("/store")
    public String store(Material material,
                        RedirectAttributes redirectAttributes) {
        Material tenTonTai = materialService.findByName(material.getName());
        if (tenTonTai != null) {
            redirectAttributes.addFlashAttribute("data", material);
            redirectAttributes.addFlashAttribute("tenTonTai", true);
            return "redirect:/chat-lieu/hien-thi";
        }
        materialService.add(material);
        redirectAttributes.addFlashAttribute("createSuccess", true);
        return "redirect:/chat-lieu/hien-thi";
    }

    @GetMapping("/update/{id}")
    public String udpate(Model model,
                         @PathVariable int id,
                         @ModelAttribute("tenTonTai") String tenTonTai) {
        Material material = materialService.findById(id);
        if (!"true".equals(tenTonTai)) {
            model.addAttribute("tenTonTai", false);
        }
        model.addAttribute("data", material);
        return "admin/material/update";
    }

    @PostMapping("/edit/{id}")
    public String store(Material material,
                        RedirectAttributes redirectAttributes,
                        @PathVariable int id) {
        Material tenTonTai = materialService.findByName(material.getName());
        if (tenTonTai != null && !(tenTonTai.getId() == id)) {
            redirectAttributes.addFlashAttribute("data", material);
            redirectAttributes.addFlashAttribute("tenTonTai", true);
            return "redirect:/chat-lieu/update/{id}";
        }
        materialService.update(material);
        redirectAttributes.addFlashAttribute("updateSuccess", true);
        return "redirect:/chat-lieu/hien-thi";
    }

    @PutMapping("/change-status/{id}")
    public ResponseEntity changeStatus(@PathVariable int id) {
        materialService.updateStatus(id);
        return ResponseEntity.ok().build();
    }

}
