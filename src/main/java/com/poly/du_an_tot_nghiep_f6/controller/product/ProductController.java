package com.poly.du_an_tot_nghiep_f6.controller.product;

import com.poly.du_an_tot_nghiep_f6.entity.*;
import com.poly.du_an_tot_nghiep_f6.service.impl.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.*;


@Controller
@RequestMapping("/san-pham")
@RequiredArgsConstructor
public class ProductController {

    private final ProductServiceImpl productService;
    private final MaterialServiceImpl materialService;
    private final StyleServiceImpl styleService;
    private final BrandServiceImpl brandService;
    private final CategoryServiceImpl categoryService;
    private final ProductDetailServiceImpl productDetailService;
    private final SizeServiceImpl sizeService;
    private final ColorServiceImpl colorService;
    private final ImageServiceImpl imageServiceImpl;
    private final UploadServiceImpl uploadService;
    private final BarCodeServiceImpl barCodeService;


    @GetMapping("/hien-thi")
    public String pagination(Model model,
                             @ModelAttribute("createSuccess") String createSuccess,
                             @ModelAttribute("updateSuccess") String updateSuccess,
                             @ModelAttribute("data") Product data) {
        model.addAttribute("listProduct", productService.findProductResponseAdmin());
        if (!"true".equals(createSuccess)) {
            model.addAttribute("createSuccess", false);
        }
        if (!"true".equals(updateSuccess)) {
            model.addAttribute("updateSuccess", false);
        }
        model.addAttribute("categories", categoryService.findAllByStatusEquals());
        model.addAttribute("materials", materialService.findAllByStatusTrue());
        model.addAttribute("brands", brandService.findAllByStatusEquals());
        model.addAttribute("styles", styleService.findByStatusEquals());
        return "admin/product/list";
    }

    @GetMapping("/create")
    public String create(Model model,
                         @ModelAttribute("tenKieuDangTonTai") String tenKieuDangTonTai,
                         @ModelAttribute("tenChatLieuTonTai") String tenChatLieuTonTai,
                         @ModelAttribute("tenThuongHieuTonTai") String tenThuongHieuTonTai,
                         @ModelAttribute("tenDangMucTonTai") String tenDangMucTonTai,
                         @ModelAttribute("tenSizeTontai") String tenSizeTontai,
                         @ModelAttribute("tenMauTonTai") String tenMauTonTai,
                         @ModelAttribute("tenTonTai") String tenTonTai,
                         @ModelAttribute("maTonTai") String maTonTai,
                         @ModelAttribute("createSuccess1") String createSuccess1,
                         @ModelAttribute("data") Product data,
                         @ModelAttribute("productVariant") ProductDetail productVariant) {
        if (!"true".equals(tenTonTai)) {
            model.addAttribute("tenTonTai", false);
        }
        if (!"true".equals(maTonTai)) {
            model.addAttribute("maTonTai", false);
        }
        if (!"true".equals(tenKieuDangTonTai)) {
            model.addAttribute("tenKieuDangTonTai", false);
        }
        if (!"true".equals(tenChatLieuTonTai)) {
            model.addAttribute("tenChatLieuTonTai", false);
        }
        if (!"true".equals(tenThuongHieuTonTai)) {
            model.addAttribute("tenThuongHieuTonTai", false);
        }
        if (!"true".equals(tenDangMucTonTai)) {
            model.addAttribute("tenDangMucTonTai", false);
        }
        if (!"true".equals(tenSizeTontai)) {
            model.addAttribute("tenSizeTontai", false);
        }
        if (!"true".equals(tenMauTonTai)) {
            model.addAttribute("tenMauTonTai", false);
        }
        if (!"true".equals(createSuccess1)) {
            model.addAttribute("createSuccess1", false);
        }
        model.addAttribute("style", new Style());
        model.addAttribute("material", new Material());
        model.addAttribute("category", new Category());
        model.addAttribute("brand", new Brand());
        model.addAttribute("size", new Size());
        model.addAttribute("color", new Color());
        model.addAttribute("listMaterial", materialService.findAllByStatusTrue());
        model.addAttribute("listStyle", styleService.findByStatusEquals());
        model.addAttribute("listBrand", brandService.findAllByStatusEquals());
        model.addAttribute("listCategory", categoryService.findAllByStatusEquals());
        model.addAttribute("listSize", sizeService.findAllByStatusTrue());
        model.addAttribute("listColor", colorService.findAllByStatusTrue());
        return "admin/product/add";
    }

    @PostMapping("/add-fastly-style")
    public String addStyle(Style style, RedirectAttributes redirectAttributes) {
        Style tenTonTai = styleService.findByName(style.getName());
        if (tenTonTai != null) {
            redirectAttributes.addFlashAttribute("tenKieuDangTonTai", true);
            return "redirect:/san-pham/create";
        }
        style.setStatus(1);
        styleService.add(style);
        redirectAttributes.addFlashAttribute("createSuccess1", true);
        return "redirect:/san-pham/create";
    }

    @PostMapping("/add-fastly-material")
    public String addmaterial(Material material, RedirectAttributes redirectAttributes) {
        Material tenTonTai = materialService.findByName(material.getName());
        if (tenTonTai != null) {
            redirectAttributes.addFlashAttribute("tenChatLieuTonTai", true);
            return "redirect:/san-pham/create";
        }
        material.setStatus(1);
        materialService.add(material);
        redirectAttributes.addFlashAttribute("createSuccess1", true);
        return "redirect:/san-pham/create";
    }

    @PostMapping("/add-fastly-brand")
    public String addStyle(Brand brand, RedirectAttributes redirectAttributes) {
        Brand tenTonTai = brandService.findByName(brand.getName());
        if (tenTonTai != null) {
            redirectAttributes.addFlashAttribute("tenThuongHieuTonTai", true);
            return "redirect:/san-pham/create";
        }
        brand.setStatus(1);
        brandService.add(brand);
        redirectAttributes.addFlashAttribute("createSuccess1", true);
        return "redirect:/san-pham/create";
    }

    @PostMapping("/add-fastly-category")
    public String addStyle(Category category, RedirectAttributes redirectAttributes) {
        Category tenTonTai = categoryService.findByName(category.getName());
        if (tenTonTai != null) {
            redirectAttributes.addFlashAttribute("tenDangMucTonTai", true);
            return "redirect:/san-pham/create";
        }
        category.setStatus(1);
        categoryService.add(category);
        redirectAttributes.addFlashAttribute("createSuccess1", true);
        return "redirect:/san-pham/create";
    }

    @PostMapping("/add-fastly-color")
    public String addColor(Color color, RedirectAttributes redirectAttributes) {
        Color tenTonTai = colorService.findByName(color.getName());
        if (tenTonTai != null) {
            redirectAttributes.addFlashAttribute("tenMauTonTai", true);
            return "redirect:/san-pham/create";
        }
        color.setStatus(1);
        colorService.add(color);
        redirectAttributes.addFlashAttribute("createSuccess1", true);
        return "redirect:/san-pham/create";
    }

    @PostMapping("/add-fastly-size")
    public String addSize(Size size, RedirectAttributes redirectAttributes) {
        Size tenTonTai = sizeService.findByName(size.getName());
        if (tenTonTai != null) {
            redirectAttributes.addFlashAttribute("tenSizeTontai", true);
            return "redirect:/san-pham/create";
        }
        size.setStatus(1);
        sizeService.add(size);
        redirectAttributes.addFlashAttribute("createSuccess1", true);
        return "redirect:/san-pham/create";
    }

    @PostMapping("/store")
    public String store(Product product,
                        @ModelAttribute("productVariant") ProductDetail productVariant,
                        @RequestParam("size") List<Integer> sizes,
                        @RequestParam("color") List<Integer> colors,
                        RedirectAttributes redirectAttributes,
                        @RequestParam("price[]") List<Double> prices,
                        @RequestParam("quantity[]") List<Integer> quantities,
                        @RequestParam("weight[]") List<Double> weights,
                        @RequestParam("deleted[]") List<Boolean> deletedRows,
                        @RequestParam("img1[]") List<MultipartFile> imgs1,
                        @RequestParam("img2[]") List<MultipartFile> imgs2,
                        @RequestParam("img3[]") List<MultipartFile> imgs3) {
        Product tenTonTai = productService.findByName(product.getName());
        Product maTonTai = productService.findByCode(product.getCode());
        if (tenTonTai != null) {
            redirectAttributes.addFlashAttribute("data", product);
            redirectAttributes.addFlashAttribute("tenTonTai", true);
            return "redirect:/san-pham/create";
        }
        if (maTonTai != null) {
            redirectAttributes.addFlashAttribute("data", product);
            redirectAttributes.addFlashAttribute("maTonTai", true);
            return "redirect:/san-pham/create";
        }
        productService.add(product);
        List<Integer> idSPCurrent = new ArrayList<>();
        for (Integer color : colors) {
            Color idColor = colorService.findById(color);
            for (Integer size : sizes) {
                Size idSize = sizeService.findById(size);
                ProductDetail productDetail = new ProductDetail();
                productDetail.setSize(idSize);
                productDetail.setColor(idColor);
                productDetail.setProduct(product);
                productDetail.setStatus(1);
                productDetailService.add(productDetail);
                idSPCurrent.add(productDetail.getId());
            }
        }
        List<Integer> count = productDetailService.countIdColor(product.getId());
        Image image = null;
        for (int i = 0; i < idSPCurrent.size(); i++) {
            if (i % count.get(0) == 0){
                int index1 = i / count.get(0);
                if (imgs1.size() > index1 && imgs2.size() > index1 && imgs3.size() > index1) {
                    MultipartFile img1 = imgs1.get(index1);
                    MultipartFile img2 = imgs2.get(index1);
                    MultipartFile img3 = imgs3.get(index1);

                    MultipartFile[] images = {img1, img2, img3};
                    List<String> url = uploadService.saveUpLoadFile(images);

                    image = new Image();
                    for (int j = 0; j < url.size(); j++) {
                        if (j == 0) {
                            image.setUrl1(url.get(j));
                        }
                        if (j == 1) {
                            image.setUrl2(url.get(j));
                        }
                        if (j == 2) {
                            image.setUrl3(url.get(j));
                        }
                    }
                    image.setStatus(1);
                    image.setCode(UUID.randomUUID().toString());
                    imageServiceImpl.add(image);
                }
            }
            Integer id = idSPCurrent.get(i);
            Double price = prices.get(i);
            Integer quantity = quantities.get(i);
            Double weight = weights.get(i);
            if (price == null && quantity == null && weight == null) {
                productDetailService.deleteById(id);
            } else {
                String code = barCodeService.genertateQrCode(id, 200, 200);
                productDetailService.updateByIdAndProductId(price, quantity, weight, id, image, code);
            }
        }
        redirectAttributes.addFlashAttribute("createSuccess", true);
        return "redirect:/san-pham/hien-thi";
    }


    @GetMapping("/update/{id}")
    public String udpate(Model model,
                         @PathVariable int id,
                         @ModelAttribute("tenTonTai") String tenTonTai) {
        Product product = productService.findById(id);
        if (!"true".equals(tenTonTai)) {
            model.addAttribute("tenTonTai", false);
        }
        model.addAttribute("data", product);
        model.addAttribute("listMaterial", materialService.findAllByStatusTrue());
        model.addAttribute("listStyle", styleService.findByStatusEquals());
        model.addAttribute("listBrand", brandService.findAllByStatusEquals());
        model.addAttribute("listCategory", categoryService.findAllByStatusEquals());
        return "admin/product/update";
    }

    @PostMapping("/edit/{id}")
    public String edit(Product product,
                       RedirectAttributes redirectAttributes,
                       @PathVariable int id,
                       Model model) {
        Product tenTonTai = productService.findByName(product.getName());
        Product maTonTai = productService.findByCode(product.getCode());
        if (tenTonTai != null && !(tenTonTai.getId() == id)) {
            redirectAttributes.addFlashAttribute("data", product);
            redirectAttributes.addFlashAttribute("tenTonTai", true);
            return "redirect:/san-pham/update/{id}";
        }
        productService.update(product);
        redirectAttributes.addFlashAttribute("updateSuccess", true);
        return "redirect:/san-pham/hien-thi";
    }

    @PutMapping("/change-status/{id}")
    public ResponseEntity changeStatus(@PathVariable int id) {
        productService.updateStatus(id);
        return ResponseEntity.ok().build();
    }
}
