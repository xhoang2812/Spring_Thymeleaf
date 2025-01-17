package com.poly.du_an_tot_nghiep_f6.controller.attribute;


import com.poly.du_an_tot_nghiep_f6.entity.Category;
import com.poly.du_an_tot_nghiep_f6.service.impl.CategoryServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


@Controller
@RequestMapping("/danh-muc")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryServiceImpl categoryService;

    @GetMapping("/hien-thi")
    public String hienThi(Model model,
                          @ModelAttribute("tenTonTai") String tenTonTai,
                          @ModelAttribute("createSuccess") String createSuccess,
                          @ModelAttribute("updateSuccess") String updateSuccess,
                          @ModelAttribute("data") Category category) {
        model.addAttribute("listCategory", categoryService.findAll());
        if (!"true".equals(tenTonTai)) {
            model.addAttribute("tenTonTai", false);
        }
        if (!"true".equals(createSuccess)) {
            model.addAttribute("createSuccess", false);
        }
        if (!"true".equals(updateSuccess)) {
            model.addAttribute("updateSuccess", false);
        }
        return "admin/category/list";
    }

    @PostMapping("/store")
    public String store(Category category,
                        RedirectAttributes redirectAttributes) {
        Category tenTonTai = categoryService.findByName(category.getName());
        if (tenTonTai != null) {
            redirectAttributes.addFlashAttribute("data", category);
            redirectAttributes.addFlashAttribute("tenTonTai", true);
            return "redirect:/danh-muc/hien-thi";
        }
        categoryService.add(category);
        redirectAttributes.addFlashAttribute("createSuccess", true);
        return "redirect:/danh-muc/hien-thi";
    }

    @GetMapping("/update/{id}")
    public String udpate(Model model,
                         @PathVariable int id,
                         @ModelAttribute("tenTonTai") String tenTonTai) {
        Category category = categoryService.findById(id);
        if (!"true".equals(tenTonTai)) {
            model.addAttribute("tenTonTai", false);
        }
        model.addAttribute("data", category);
        return "admin/category/update";
    }

    @PostMapping("/edit/{id}")
    public String store(Category category,
                        RedirectAttributes redirectAttributes,
                        @PathVariable int id) {
        Category tenTonTai = categoryService.findByName(category.getName());
        if (tenTonTai != null && !(tenTonTai.getId() == id)) {
            redirectAttributes.addFlashAttribute("data", category);
            redirectAttributes.addFlashAttribute("tenTonTai", true);
            return "redirect:/danh-muc/update/{id}";
        }
        categoryService.update(category);
        redirectAttributes.addFlashAttribute("updateSuccess", true);
        return "redirect:/danh-muc/hien-thi";
    }

    @PutMapping("/change-status/{id}")
    public ResponseEntity changeStatus(@PathVariable int id) {
        categoryService.updateStatus(id);
        return ResponseEntity.ok().build();
    }

}
