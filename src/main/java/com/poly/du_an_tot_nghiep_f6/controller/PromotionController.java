package com.poly.du_an_tot_nghiep_f6.controller;

import com.poly.du_an_tot_nghiep_f6.entity.Product;
import com.poly.du_an_tot_nghiep_f6.entity.ProductDetail;
import com.poly.du_an_tot_nghiep_f6.entity.Promotion;
import com.poly.du_an_tot_nghiep_f6.entity.PromotionDetail;
import com.poly.du_an_tot_nghiep_f6.repository.ProductDetailRepo;
import com.poly.du_an_tot_nghiep_f6.repository.ProductRepo;
import com.poly.du_an_tot_nghiep_f6.repository.PromotionDetailRepo;
import com.poly.du_an_tot_nghiep_f6.repository.PromotionRepo;
import com.poly.du_an_tot_nghiep_f6.response.ProductDetailResponse;
import com.poly.du_an_tot_nghiep_f6.response.PromotionDetailRes;
import com.poly.du_an_tot_nghiep_f6.response.PromotionDetailResponse;
import com.poly.du_an_tot_nghiep_f6.response.PromotionResponse;
import com.poly.du_an_tot_nghiep_f6.service.PromotionProductService;
import com.poly.du_an_tot_nghiep_f6.service.PromotionService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Controller
@RequestMapping("/promotions")
public class PromotionController {

    @Autowired
    private PromotionService promotionService;
    @Autowired
    private PromotionProductService promotionProductService;
    @Autowired
    private PromotionDetailRepo promotionDetailRepo;
    @Autowired
    private ProductDetailRepo productDetailRepo;
    @Autowired
    private ProductRepo productRepo;
    @Autowired
    private PromotionRepo promotionRepo;


    @ModelAttribute("products")
    public List<Product> products() {
        return productRepo.findAll();
    }

    // Hiển thị danh sách khuyến mãi
    @GetMapping("")
    public String listPromotions(Model model) {
        List<Promotion> promotions = promotionService.getAllPromotions();
        List<PromotionResponse> list = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");
        for(Promotion promotion : promotions){
            PromotionResponse promotionResponse = new PromotionResponse();
            BeanUtils.copyProperties(promotion,promotionResponse);
            promotionResponse.setDateStart(promotion.getDateStart().format(formatter));
            promotionResponse.setDateEnd(promotion.getDateEnd().format(formatter));
            promotionResponse.setDateCreate(promotion.getDateCreate().format(formatter));
            if(promotion.getDateUpdate() != null){
                promotionResponse.setDateUpdate(promotion.getDateUpdate().format(formatter));
            }
            list.add(promotionResponse);
        }
        model.addAttribute("promotions", list);
        return "admin/promotion/promotion-list";
    }

    // Form tạo khuyến mãi và hiển thị bảng sản phẩm
    @GetMapping("/add")
    public String showCreateForm(Model model) {
        model.addAttribute("selectedProducts", new ArrayList<String>()); // Thêm dòng này
        model.addAttribute("promotion", new Promotion());
        List<Product> list = productRepo.findAll();
        List<Product> products = new ArrayList<>();
        for(Product product: list){
            List<ProductDetail> productDetails = productDetailRepo.findByProduct(product);
            for(ProductDetail productDetail : productDetails){
                if(productDetail.getPromotionDetail() == null){
                    products.add(product);
                    break;
                }
            }
        }
        model.addAttribute("products", products);
        return "admin/promotion/promotion-form";
    }

    @PostMapping("/add")
    public String savePromotion(
            @ModelAttribute("promotion") Promotion promotion,
            @RequestParam(name = "selectedProducts", required = false) List<Integer> selectedProductIds
    ) {
        System.out.println(selectedProductIds);
        promotionService.savePromotion(promotion, selectedProductIds);
        return "redirect:/promotions";
    }
    @PostMapping("/update")
    public String updatePromotion(
            @ModelAttribute("promotion") Promotion promotion,
            @RequestParam(name = "selectedProducts", required = false) List<Integer> selectedProductIds
    ) {
        System.out.println(selectedProductIds);
        promotionService.updatePromotion(promotion, selectedProductIds);
        return "redirect:/promotions";
    }

    @GetMapping("/edit/{id}")
    public String showPromotionDetails(@PathVariable Integer id, Model model) {
        Promotion promotion = promotionService.getPromotionById(id);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");
        PromotionResponse promotionResponse = new PromotionResponse();
        BeanUtils.copyProperties(promotion, promotionResponse);
        promotionResponse.setDateStart(promotion.getDateStart().format(formatter));
        promotionResponse.setDateEnd(promotion.getDateEnd().format(formatter));
        promotionResponse.setPrice(String.valueOf(promotion.getPrice()));
        model.addAttribute("promotion", promotionResponse);
        List<PromotionDetail> promotionDetails = promotionDetailRepo.findByPromotion(promotion);
        List<ProductDetail> promotionDetailOfPromotion = new ArrayList<>();
        for(PromotionDetail promotionDetail: promotionDetails){
            promotionDetailOfPromotion.add(promotionDetail.getProductDetail());
        }
        List<Product> list = productRepo.findAll();
        List<Product> products = new ArrayList<>();
        for(Product product: list){
            List<ProductDetail> productDetails = productDetailRepo.findByProduct(product);
            for(ProductDetail productDetail : productDetails){
                if(productDetail.getPromotionDetail() == null || productDetail.getPromotionDetail().getPromotion().equals(promotion)){
                    products.add(product);
                    break;
                }
            }
        }
        model.addAttribute("products", products);
        model.addAttribute("productDetails", promotionDetailOfPromotion);
        return "admin/promotion/promotion-edit"; // Đảm bảo file HTML tồn tại
    }

    @GetMapping("/promotionDetails/{idPromotion}")
    public String getProductDetailOfPromotion(@PathVariable Integer idPromotion, Model model){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        NumberFormat numberFormat = NumberFormat.getInstance(new Locale("vi", "VN"));
        Promotion promotion = promotionRepo.findById(idPromotion)
                .orElseThrow(() -> new RuntimeException("Data not found"));
        List<PromotionDetail> promotionDetails = promotionDetailRepo.findByPromotion(promotion);
        List<PromotionDetailRes> list = new ArrayList<>();
        for(PromotionDetail promotionDetail: promotionDetails){
            PromotionDetailRes promotionDetailRes = new PromotionDetailRes();
            promotionDetailRes.setId(promotionDetail.getId());
            promotionDetailRes.setProductDetail(promotionDetail.getProductDetail());
            promotionDetailRes.setGiaMoi(numberFormat.format(promotionDetail.getGiaMoi()) + " VND");
            promotionDetailRes.setGiaCu(numberFormat.format(promotionDetail.getProductDetail().getPrice()) + " VND");
            list.add(promotionDetailRes);
        }

        PromotionResponse promotionResponse = new PromotionResponse();
        BeanUtils.copyProperties(promotion,promotionResponse);
        promotionResponse.setDateStart(promotion.getDateStart().format(formatter));
        promotionResponse.setDateEnd(promotion.getDateEnd().format(formatter));
        if (promotion.getPrice() < 100) {
            promotionResponse.setPrice(promotion.getPrice()+"%");
        }else {
            String price = numberFormat.format(promotion.getPrice());
            promotionResponse.setPrice(price + " VND");

        }
        promotionResponse.setStatus(promotion.getStatus());

        model.addAttribute("promotion", promotionResponse);
        model.addAttribute("productDetails", list);
        return "admin/promotion/promotion-detail";
    }

}
