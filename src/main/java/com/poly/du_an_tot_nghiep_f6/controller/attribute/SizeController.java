package com.poly.du_an_tot_nghiep_f6.controller.attribute;


import com.poly.du_an_tot_nghiep_f6.entity.Size;
import com.poly.du_an_tot_nghiep_f6.service.impl.SizeServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/kich-thuoc")
@RequiredArgsConstructor
public class SizeController {

    private final SizeServiceImpl sizeService;


    @GetMapping("/hien-thi")
    public String hienThi(Model model,
                          @ModelAttribute("tenTonTai") String tenTonTai,
                          @ModelAttribute("createSuccess") String createSuccess,
                          @ModelAttribute("updateSuccess") String updateSuccess,
                          @ModelAttribute("data") Size data) {
        model.addAttribute("listSize", sizeService.findAll());
        if (!"true".equals(tenTonTai)) {
            model.addAttribute("tenTonTai", false);
        }
        if (!"true".equals(createSuccess)) {
            model.addAttribute("createSuccess", false);
        }
        if (!"true".equals(updateSuccess)) {
            model.addAttribute("updateSuccess", false);
        }
        return "admin/size/list";
    }

    @PostMapping("/store")
    public String store(Size size,
                        RedirectAttributes redirectAttributes) {
        Size tenTonTai = sizeService.findByName(size.getName());
        if (tenTonTai != null) {
            redirectAttributes.addFlashAttribute("data", size);
            redirectAttributes.addFlashAttribute("tenTonTai", true);
            return "redirect:/kich-thuoc/hien-thi";
        }
        sizeService.add(size);
        redirectAttributes.addFlashAttribute("createSuccess", true);
        return "redirect:/kich-thuoc/hien-thi";
    }

    @GetMapping("/update/{id}")
    public String udpate(Model model,
                         @PathVariable int id,
                         @ModelAttribute("tenTonTai") String tenTonTai) {
        Size size = sizeService.findById(id);
        if (!"true".equals(tenTonTai)) {
            model.addAttribute("tenTonTai", false);
        }
        model.addAttribute("data", size);
        return "admin/size/update";
    }

    @PostMapping("/edit/{id}")
    public String store(Size size,
                        RedirectAttributes redirectAttributes,
                        @PathVariable int id) {
        Size tenTonTai = sizeService.findByName(size.getName());
        if (tenTonTai != null && !(tenTonTai.getId() == id)) {
            redirectAttributes.addFlashAttribute("data", size);
            redirectAttributes.addFlashAttribute("tenTonTai", true);
            return "redirect:/kich-thuoc/update/{id}";
        }
        sizeService.update(size);
        redirectAttributes.addFlashAttribute("updateSuccess", true);
        return "redirect:/kich-thuoc/hien-thi";
    }

    @PutMapping("/change-status/{id}")
    public ResponseEntity changeStatus(@PathVariable int id) {
        sizeService.updateStatus(id);
        return ResponseEntity.ok().build();
    }
}
