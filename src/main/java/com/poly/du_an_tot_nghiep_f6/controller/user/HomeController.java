package com.poly.du_an_tot_nghiep_f6.controller.user;

import com.poly.du_an_tot_nghiep_f6.entity.*;
import com.poly.du_an_tot_nghiep_f6.repository.*;
import com.poly.du_an_tot_nghiep_f6.entity.Cart;
import com.poly.du_an_tot_nghiep_f6.entity.CartDetail;
import com.poly.du_an_tot_nghiep_f6.entity.Customer;
import com.poly.du_an_tot_nghiep_f6.entity.ProductDetail;
import com.poly.du_an_tot_nghiep_f6.request.CartKoDangNhapReq;
import com.poly.du_an_tot_nghiep_f6.response.*;
import com.poly.du_an_tot_nghiep_f6.service.*;
import com.poly.du_an_tot_nghiep_f6.service.impl.*;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;

import java.util.*;
import java.util.stream.Collectors;


@Controller
@RequestMapping("/home")
@RequiredArgsConstructor
public class HomeController {

    private final ProductServiceImpl productService;
    private final ProductDetailServiceImpl productDetailService;
    private final CategoryServiceImpl categoryService;
    private final ImageServiceImpl imageService;
    private final SizeServiceImpl sizeService;
    private final ColorServiceImpl colorService;
    private final BrandServiceImpl brandService;
    private final StyleServiceImpl styleService;
    private final MaterialServiceImpl materialService;
    private final CartServiceImpl cartService;
    private final CartDetailServiceImpl cartDetailService;
    private final CustomUserDetailService customUserDetailService;
    private final VoucherService voucherService;
    private final VNPAYService vnPayService;
    private final CustomerService customerService;
    private final BillRepo BillRepo;
    private final BillDetailRepo BillDetailRepo;
    private final ProductDetailRepo productDetailRepo;
    private final CartDetailRepo CartDetailRepo;
    @Autowired
    private UploadServiceImpl uploadService;
    @Autowired
    private AddressService addressService;
    @Autowired
    private CustomerRepo customerRepo;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private CartRepo cartRepo;
    @Autowired
    private CartDetailRepo cartDetailRepo;
    @Autowired
    private BillRepo billRepo;
    @Autowired
    private OTPService otpService;
    @Autowired
    private BillHistoryRepo billHistoryRepo;
    @Autowired
    private VoucherRepo voucherRepo;

    @ModelAttribute("tenKhachHang")
    public String getTenKhachHang() {
        Customer customer = customUserDetailService.getCustomer();
        return (customer != null) ? customer.getName() : "Đăng nhập";
    }

    @ModelAttribute("idKhachHang")
    public Integer GetidKhachHang() {
        Customer customer = customUserDetailService.getCustomer();
        return (customer != null) ? customer.getId() : null;
    }

    @GetMapping("")
    public String home(Model model) {
        Pageable pageable = PageRequest.of(0, 4);
        model.addAttribute("listProductCount", productService.findProductResponseHomeCount(pageable));
        model.addAttribute("listProductNew", productService.findProductResponseHomeNew(pageable));
        return "user/home";
    }

    @GetMapping("/shop")
    public String shop(Model model,
                       @ModelAttribute("spInactive") String spInactive) {
        if (!"true".equals(spInactive)) {
            model.addAttribute("spInactive", false);
        }
        model.addAttribute("categories", categoryService.findAllByStatusEquals());
        model.addAttribute("brands", brandService.findAllByStatusEquals());
        model.addAttribute("styles", styleService.findByStatusEquals());
        model.addAttribute("materials", materialService.findAllByStatusTrue());
        model.addAttribute("sizes", sizeService.findAllByStatusTrue());
        model.addAttribute("colors", colorService.findAllByStatusTrue());
        return "user/shop";
    }

    @GetMapping("/api/shop")
    public ResponseEntity<Page<ProductResponse>> apiShop(@RequestParam(required = false) Integer brandId,
                                                         @RequestParam(required = false) Integer styleId,
                                                         @RequestParam(required = false) Integer materialId,
                                                         @RequestParam(required = false) Integer sizeId,
                                                         @RequestParam(required = false) Integer colorId,
                                                         @RequestParam(required = false) Integer categoryId,
                                                         @RequestParam(required = false) String sort,
                                                         @RequestParam(required = false) String timTheoTen,
                                                         @RequestParam(defaultValue = "0") Integer page) {
        int size = 9;
        Pageable pageable = PageRequest.of(page, size);
        Page<ProductResponse> products = productService.findAllProductResponse(
                brandId, styleId, materialId, sizeId, colorId, categoryId, sort, timTheoTen, pageable);
        return ResponseEntity.ok(products);
    }

    @GetMapping("/shop-detail/{id}")
    public String shopDetail(@PathVariable("id") Integer id, Model model,
                             @ModelAttribute("spctInactive") String spctInactive,
                             @ModelAttribute("hetSl") String hetSl,
                             @ModelAttribute("quaSl") String quaSl) {
        if (!"true".equals(spctInactive)) {
            model.addAttribute("spctInactive", false);
        }
        if (!"true".equals(hetSl)) {
            model.addAttribute("hetSl", false);
        }
        if (!"true".equals(quaSl)) {
            model.addAttribute("quaSl", false);
        }
        Pageable pageable = PageRequest.of(0, 4);
        model.addAttribute("listProductNew", productService.findProductResponseHomeNew(pageable));
        model.addAttribute("productResponse", productService.findProductResponseById(id));
        model.addAttribute("colors", colorService.findAllByProductId1(id));
        model.addAttribute("sizes", sizeService.findAllByProductId1(id));
        model.addAttribute("images", imageService.findAllByProductId(id));
        return "user/shop_detail";
    }

    @GetMapping("/api/shop-detail")
    public ResponseEntity<?> apiShopDetail(@RequestParam Integer idSize,
                                           @RequestParam Integer idColor,
                                           @RequestParam Integer idProductResponse) {
        ProductShopDetailResponse productShopDetailResponse
                = productDetailService.ProductShopDetailResponse(idProductResponse, idColor, idSize);
        return ResponseEntity.ok(productShopDetailResponse);
    }

    @PostMapping("/api/add-to-cart")
    public ResponseEntity<?> addToCart(@RequestParam Integer idProductDetail,
                                       @RequestParam Integer quantity,
                                       @RequestParam Double price,
                                       @RequestParam Double salePrice) {

        Customer customer = customUserDetailService.getCustomer();
        ProductDetail productDetail = productDetailService.findById(idProductDetail);
        Cart cart1 = cartService.getCartDetailByIdCustomer(customer.getId());
        Map<String, String> errorResponse = new HashMap<>();
        Integer totalProductInCart = cartDetailService.getQuantityCart_IdAndProductDetail_Id(cart1.getId(), idProductDetail);
        if (totalProductInCart + quantity > productDetail.getQuantity()) {
            errorResponse.put("error", "Số lượng sản phẩm trong giỏ hàng vượt quá số lượng kho.");
            return ResponseEntity.badRequest().body(errorResponse);
        }
        if (productDetail.getStatus() == 0) {
            errorResponse.put("error", "Sản phẩm đã ngừng kinh doanh.");
            return ResponseEntity.badRequest().body(errorResponse);
        }
        if (productDetail.getProduct().getStatus() == 0) {
            errorResponse.put("error", "Danh mục sản phẩm đã ngừng kinh doanh.");
            return ResponseEntity.badRequest().body(errorResponse);
        }
        if (productDetail.getQuantity() == 0) {
            errorResponse.put("error", "Sản phẩm đã hết hàng.");
            return ResponseEntity.badRequest().body(errorResponse);
        }


        Integer quantityInPromotion = cartDetailRepo.getQuantityInPromotion(idProductDetail);
        List<CartDetail> cartDetailList = cartDetailService.findAllCartDetailByIdCart(cart1.getId());
        if (quantityInPromotion == null) {
            int count = 0;
            for (CartDetail cartDetail : cartDetailList) {
                if (cartDetail.getProductDetail().getId().equals(idProductDetail)) {
                    count++;
                }
            }
            if (count == 0) {
                cartDetailService.addNewCartDetail(quantity, productDetail, cart1, productDetail.getPrice(), false);
            } else {
                cartDetailService.updateQuantityProductExist(quantity, productDetail, cart1, productDetail.getPrice(), false);
            }
        } else {
            Promotion promotion = productDetail.getPromotionDetail() != null ? productDetail.getPromotionDetail().getPromotion() : null;
            if (promotion != null && promotion.getStatus().contains("Đang") && promotion.isCondition()) {
                int countNormal1 = 0;
                for (CartDetail cartDetail : cartDetailList) {
                    if (cartDetail.getProductDetail().getId().equals(idProductDetail)) {
                        countNormal1++;
                    }
                }
                if (countNormal1 == 0) {
                    cartDetailService.addNewCartDetail(quantity, productDetail, cart1, (double) productDetail.getPromotionDetail().getGiaMoi(), true);
                } else {
                    cartDetailService.updateQuantityProductExist(quantity, productDetail, cart1, (double) productDetail.getPromotionDetail().getGiaMoi(), true);
                }
            } else {
                int count = 0;
                for (CartDetail cartDetail : cartDetailList) {
                    if (cartDetail.getProductDetail().getId().equals(idProductDetail)) {
                        count++;
                    }
                }
                if (count == 0) {
                    cartDetailService.addNewCartDetail(quantity, productDetail, cart1, productDetail.getPrice(), false);
                } else {
                    cartDetailService.updateQuantityProductExist(quantity, productDetail, cart1, productDetail.getPrice(), false);
                }
            }

        }
        return ResponseEntity.ok(productDetail);

    }


    @PutMapping("/api/update-cart-detail")
    public ResponseEntity<?> updateCart(@RequestParam Integer idCartDetail,
                                        @RequestParam Integer quantity,
                                        @RequestParam Double price,
                                        @RequestParam Integer idProductDetail,
                                        @RequestParam Double priceSale) {
        CartDetail cartDetail = cartDetailService.findById(idCartDetail);
        Integer quantityInPromotion = cartDetailRepo.getQuantityInPromotion(idProductDetail);
        ProductDetail productDetail = productDetailService.findById(idProductDetail);
        if (quantityInPromotion == null) {
            cartDetailService.updateQuantityProductInCart(quantity, cartDetail, productDetail.getPrice(), false);
        } else {
            Promotion promotion = productDetail.getPromotionDetail() != null ? productDetail.getPromotionDetail().getPromotion() : null;
            if (promotion != null && promotion.getStatus().contains("Đang") && promotion.isCondition()) {
                cartDetailService.updateQuantityProductInCart(quantity, cartDetail, (double) productDetail.getPromotionDetail().getGiaMoi(), true);
            } else {
                cartDetailService.updateQuantityProductInCart(quantity, cartDetail, productDetail.getPrice(), false);
            }
        }
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/api/delete-cart-detail/{idCartDetail}")
    public ResponseEntity<?> deleteCart(@PathVariable Integer idCartDetail) {
        cartDetailService.deleteById(idCartDetail);
        return ResponseEntity.ok().build();
    }


    @GetMapping("/cart")
    public String cart(Model model,
                       @ModelAttribute("hetKm") String hetKm,
                       @ModelAttribute("hetSlKoKm") String heSlKoKm,
                       @ModelAttribute("spInactive") String spInactive,
                       @ModelAttribute("spctInactive") String spctInactive,
                       @ModelAttribute("hetVoucher") String hetVoucher,
                       @ModelAttribute("kmDangCo") String kmDangCo,
                       @ModelAttribute("apiVnpayLoi") String apiVnpayLoi) {
        Customer customer = customUserDetailService.getCustomer();
        if (customer != null) {
            Cart cart1 = cartService.getCartDetailByIdCustomer(customer.getId());
            List<CartDetailOnlineResponse> listCartDetail = cartDetailService.findAllCartDetailByIdCartOnline(cart1.getId());
            model.addAttribute("listCartDetail", listCartDetail);
        }
        if (!"true".equals(apiVnpayLoi)) {
            model.addAttribute("apiVnpayLoi", false);
        }
        if (!"true".equals(kmDangCo)) {
            model.addAttribute("kmDangCo", false);
        }
        if (!"true".equals(hetKm)) {
            model.addAttribute("hetKm", false);
        }
        if (!"true".equals(heSlKoKm)) {
            model.addAttribute("heSlKoKm", false);
        }
        if (!"true".equals(spInactive)) {
            model.addAttribute("spInactive", false);
        }
        if (!"true".equals(spctInactive)) {
            model.addAttribute("spctInactive", false);
        }
        if (!"true".equals(hetVoucher)) {
            model.addAttribute("hetVoucher", false);
        }
        return "user/cart";
    }

    @GetMapping("/getPP/{id}")
    public ResponseEntity<?> getVoucher(@PathVariable Integer id) {
        Customer customer = customUserDetailService.getCustomer();
        List<Voucher> vouchers = customer.getVouchers();
        List<Voucher> listVoucher = new ArrayList<>();
        for (Voucher voucher : vouchers) {
            if (voucher.getCondition().equals("Đang diễn ra") &&
                    voucher.isStatus() && voucher.getQuantity() > voucher.getQuantityUsed()) {
                listVoucher.add(voucher);
            }
        }
        return ResponseEntity.ok(listVoucher);
    }

    @GetMapping("/api/get-address")
    public ResponseEntity<?> getAddress() {
        List<Address> addresses;
        if (customUserDetailService.getCustomer() != null) {
            Customer customer = customUserDetailService.getCustomer();
            addresses = addressService.findByCustomerId(customer.getId());
        } else {
            addresses = null;
        }
        return ResponseEntity.ok(addresses);
    }

    //Thêm địa chỉ khách hàng api
    @PostMapping("/api/add-new-address")
    public ResponseEntity<?> apiAddNewAddress(
            @RequestParam String nameRecipient,
            @RequestParam(required = false) String phone,
            @RequestParam String provinceName,
            @RequestParam String districtName,
            @RequestParam String wardName,
            @RequestParam String addressDetail) {

        Customer customer = customUserDetailService.getCustomer();

        Address customerAddress = new Address();
        customerAddress.setCustomer(customer);
        customerAddress.setNameRecipient(nameRecipient);
        if (phone == null || phone.trim().isEmpty()) {
            customerAddress.setPhone(customer.getPhone());
        } else {
            customerAddress.setPhone(phone);
        }
        customerAddress.setProvince(provinceName);
        customerAddress.setDistrict(districtName);
        customerAddress.setWard(wardName);
        customerAddress.setAddreseDetail(addressDetail);
        customerAddress.setDefault(false);
        addressService.save(customerAddress);
        return ResponseEntity.ok().build();
    }

    //Xoa khac hang api
    @DeleteMapping("/api/delete-address")
    public ResponseEntity<?> deleteAddress(@RequestParam Integer id) {
        Address address = addressService.findById(id);
        if (address.isDefault()) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Không thể xóa địa chỉ mặc định.");
            return ResponseEntity.badRequest().body(errorResponse);
        } else {
            addressService.deleteEntity(address);
            return ResponseEntity.ok().build();
        }
    }

    @PutMapping("/api/set-default-address")
    public ResponseEntity<?> setDefaultAddressAPI(@RequestParam Integer id) {
        Customer customer = customUserDetailService.getCustomer();
        try {
            addressService.setDefaultAddress(id, customer.getId());
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Không thể đặt địa chỉ này làm mặc định: " + e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }

    }

    //Get dia chi chi tiet api
    @GetMapping("/api/get-address-detail")
    public ResponseEntity<?> getAddressDetail(@RequestParam Integer id) {
        Address address = addressService.findById(id);
        return ResponseEntity.ok(address);
    }


    @PutMapping("/api/update-address")
    public ResponseEntity<?> updateAddress(@RequestParam Integer id,
                                           @RequestParam String nameRecipient,
                                           @RequestParam(required = false) String phone,
                                           @RequestParam String provinceName,
                                           @RequestParam String districtName,
                                           @RequestParam String wardName,
                                           @RequestParam String addressDetail) {
        Address address = addressService.findById(id);
        Customer customer = customUserDetailService.getCustomer();
        address.setNameRecipient(nameRecipient);
        if (phone == null || phone.trim().isEmpty()) {
            address.setPhone(customer.getPhone());
        } else {
            address.setPhone(phone);
        }
        address.setProvince(provinceName);
        address.setDistrict(districtName);
        address.setWard(wardName);
        address.setAddreseDetail(addressDetail);
        addressService.save(address);
        return ResponseEntity.ok().build();
    }


    @PostMapping("/check-out")
    public String checkout(Model model, @RequestParam("selectedCartIds") List<Integer> selectedCartIds, RedirectAttributes redirectAttributes) {
        System.out.println(selectedCartIds);
        Customer customer = customUserDetailService.getCustomer();
        Cart cart1 = cartService.getCartDetailByIdCustomer(customer.getId());
        int checkXemKmDaHet = 0;
        int checkXemKoKmDaHet = 0;
        int checkXemSpActive = 0;
        int checkXemSpctActive = 0;
        int checkXemKmDangCo = 0;
        for (Integer id : selectedCartIds) {
            CartDetail cartDetail = cartDetailService.findById(id);
            Integer quantityInPromotion = cartDetailRepo.getQuantityInPromotion(cartDetail.getProductDetail().getId());
            if (cartDetail.isHavePromotion()) {
                if (quantityInPromotion == null) {
                    checkXemKmDaHet++;
                    ProductDetail productDetail = productDetailService.findById(cartDetail.getProductDetail().getId());
                    cartDetail.setPrice(productDetail.getPrice());
                    cartDetail.setTotal(productDetail.getPrice() * cartDetail.getQuantity());
                    cartDetail.setHavePromotion(false);
                    cartDetailRepo.save(cartDetail);
                }
            } else {
                ProductDetail productDetail = productDetailService.findById(cartDetail.getProductDetail().getId());
                Promotion promotion = productDetail.getPromotionDetail() != null ? productDetail.getPromotionDetail().getPromotion() : null;
                if (quantityInPromotion != null && promotion != null && promotion.getStatus().contains("Đang") && promotion.isCondition()) {
                    checkXemKmDangCo++;
                    cartDetail.setPrice((double) productDetail.getPromotionDetail().getGiaMoi());
                    cartDetail.setTotal((double) (productDetail.getPromotionDetail().getGiaMoi() * cartDetail.getQuantity()));
                    cartDetail.setHavePromotion(true);
                    cartDetailRepo.save(cartDetail);
                }
            }
            Integer totalProductInCart = cartDetailService.getQuantityCart_IdAndProductDetail_Id(cart1.getId(), cartDetail.getProductDetail().getId());
            if (totalProductInCart > cartDetail.getProductDetail().getQuantity()) {
                checkXemKoKmDaHet++;
            }

            if (cartDetail.getProductDetail().getStatus() == 0) {
                checkXemSpctActive++;
            }

            if (cartDetail.getProductDetail().getProduct().getStatus() == 0) {
                checkXemSpActive++;
            }
        }

        if (checkXemKmDangCo > 0) {
            redirectAttributes.addFlashAttribute("kmDangCo", true);
            return "redirect:/home/cart";
        }

        if (checkXemKmDaHet > 0) {
            redirectAttributes.addFlashAttribute("hetKm", true);
            return "redirect:/home/cart";
        }
        if (checkXemKoKmDaHet > 0) {
            redirectAttributes.addFlashAttribute("hetSlKoKm", true);
            return "redirect:/home/cart";
        }

        if (checkXemSpActive > 0) {
            redirectAttributes.addFlashAttribute("spInactive", true);
            return "redirect:/home/cart";
        }

        if (checkXemSpctActive > 0) {
            redirectAttributes.addFlashAttribute("spctInactive", true);
            return "redirect:/home/cart";
        }

        List<CartDetailOnlineResponse> listCartDetail = cartDetailService.findAllCartDetailByIdCartOnline2(cart1.getId(), selectedCartIds);
        model.addAttribute("listCartDetail", listCartDetail);
        return "user/check_out";
    }

    @PostMapping("/buy-now")
    public String buyNow(Model model,
                         @RequestParam(required = false) Integer idProductDetail1,
                         @RequestParam(required = false) Integer quantity,
                         @RequestParam(required = false) Double price,
                         @RequestParam(required = false) Double salePrice,
                         @RequestParam Integer idProduct,
                         RedirectAttributes redirectAttributes) {
        Customer customer = customUserDetailService.getCustomer();
        if (customer == null) {
            return "redirect:/login";
        } else {
            if (!customer.isStatus()) {
                return "redirect:/login";
            }
        }
        Product product = productService.findById(idProduct);
        if (product.getStatus() == 0) {
            redirectAttributes.addFlashAttribute("spInactive", true);
            return "redirect:/home/shop";
        }

        if (quantity == 0) {
            redirectAttributes.addFlashAttribute("spctInactive", true);
            return "redirect:/home/shop-detail/" + idProduct;
        }

        if (idProductDetail1 == null || price == null || salePrice == null) {
            redirectAttributes.addFlashAttribute("spctInactive", true);
            return "redirect:/home/shop-detail/" + idProduct;
        }

        ProductDetail productDetail = productDetailService.findById(idProductDetail1);

        if (productDetail.getQuantity() == 0) {
            redirectAttributes.addFlashAttribute("hetSl", true);
            return "redirect:/home/shop-detail/" + idProduct;
        }
        if (productDetail.getQuantity() < quantity) {
            redirectAttributes.addFlashAttribute("quaSl", true);
            return "redirect:/home/shop-detail/" + idProduct;
        }
        if (productDetail.getStatus() == 0) {
            redirectAttributes.addFlashAttribute("spctInactive", true);
            return "redirect:/home/shop-detail/" + idProduct;
        }


        Cart cart1 = cartService.getCartDetailByIdCustomer(customer.getId());
        Integer quantityInPromotion = cartDetailRepo.getQuantityInPromotion(idProductDetail1);
        List<CartDetail> cartDetailList = cartDetailService.findAllCartDetailByIdCart(cart1.getId());
        ProductDetail productDetail1 = productDetailService.findById(idProductDetail1);
        if (quantityInPromotion == null) {
            int count = 0;
            for (CartDetail cartDetail : cartDetailList) {
                if (cartDetail.getProductDetail().getId().equals(idProductDetail1)) {
                    count++;
                }
            }
            if (count == 0) {
                cartDetailService.addNewCartDetail(quantity, productDetail, cart1, productDetail1.getPrice(), false);
            } else {
                cartDetailService.updateQuantityProductExist1(quantity, productDetail, cart1, productDetail1.getPrice(), false);
            }
        } else {
            Promotion promotion = productDetail.getPromotionDetail() != null ? productDetail.getPromotionDetail().getPromotion() : null;
            if (promotion != null && promotion.getStatus().contains("Đang") && promotion.isCondition()) {
                int countNormal1 = 0;
                for (CartDetail cartDetail : cartDetailList) {
                    if (cartDetail.getProductDetail().getId().equals(idProductDetail1)) {
                        countNormal1++;
                    }
                }
                if (countNormal1 == 0) {
                    cartDetailService.addNewCartDetail(quantity, productDetail, cart1, (double) productDetail1.getPromotionDetail().getGiaMoi(), true);
                } else {
                    cartDetailService.updateQuantityProductExist1(quantity, productDetail, cart1, (double) productDetail1.getPromotionDetail().getGiaMoi(), true);
                }
            } else {
                int count = 0;
                for (CartDetail cartDetail : cartDetailList) {
                    if (cartDetail.getProductDetail().getId().equals(idProductDetail1)) {
                        count++;
                    }
                }
                if (count == 0) {
                    cartDetailService.addNewCartDetail(quantity, productDetail, cart1, productDetail1.getPrice(), false);
                } else {
                    cartDetailService.updateQuantityProductExist1(quantity, productDetail, cart1, productDetail1.getPrice(), false);
                }
            }

        }
        List<CartDetailOnlineResponse> listCartDetail = cartDetailService.findAllCartDetailByIdCartOnline3(cart1.getId(), idProductDetail1);
        model.addAttribute("listCartDetail1", listCartDetail);
        return "user/check_out";
    }


    @PostMapping("/check-out/cod")
    public String checkoutSuccess(@RequestParam("totalCart") Double totalCart,
                                  @RequestParam("idCartDetail") List<Integer> idCartDetail,
                                  @RequestParam(name = "hinhThucThanhToan") boolean hinhThucThanhToan,
                                  HttpServletRequest request, HttpSession session,
                                  @RequestParam(required = false) Integer idVoucher,
                                  @RequestParam Integer idAddress,
                                  @RequestParam Double shippingFee, @RequestParam Integer tienGiam, RedirectAttributes redirectAttributes) throws
            MessagingException {
        System.out.println("payment" + hinhThucThanhToan);
        System.out.println("vnpay" + hinhThucThanhToan);
        Customer customer = customUserDetailService.getCustomer();
        if (customer == null) {
            return "redirect:/login";
        } else {
            if (!customer.isStatus()) {
                return "redirect:/login";
            }
        }
        Cart cart1 = cartService.getCartDetailByIdCustomer(customer.getId());
        int checkXemKmDaHet = 0;
        int checkXemKoKmDaHet = 0;
        int checkXemSpActive = 0;
        int checkXemSpctActive = 0;
        int checkXemKmDangCo = 0;
        if (idVoucher != null) {
            Voucher voucherCheck = voucherService.findById(idVoucher);
            if (voucherCheck.getCondition().equals("Đã kết thúc") || !voucherCheck.isStatus() || voucherCheck.getQuantity() <= voucherCheck.getQuantityUsed()) {
                redirectAttributes.addFlashAttribute("hetVoucher", true);
                return "redirect:/home/cart";
            }
        }
        for (Integer id : idCartDetail) {
            CartDetail cartDetail = cartDetailService.findById(id);
            Integer quantityInPromotion = cartDetailRepo.getQuantityInPromotion(cartDetail.getProductDetail().getId());
            if (cartDetail.isHavePromotion()) {
                if (quantityInPromotion == null) {
                    checkXemKmDaHet++;
                    ProductDetail productDetail = productDetailService.findById(cartDetail.getProductDetail().getId());
                    cartDetail.setPrice(productDetail.getPrice());
                    cartDetail.setTotal(productDetail.getPrice() * cartDetail.getQuantity());
                    cartDetail.setHavePromotion(false);
                    cartDetailRepo.save(cartDetail);
                }
            } else {
                ProductDetail productDetail = productDetailService.findById(cartDetail.getProductDetail().getId());
                Promotion promotion = productDetail.getPromotionDetail() != null ? productDetail.getPromotionDetail().getPromotion() : null;
                if (quantityInPromotion != null && promotion != null && promotion.getStatus().contains("Đang") && promotion.isCondition()) {
                    checkXemKmDangCo++;
                    cartDetail.setPrice((double) productDetail.getPromotionDetail().getGiaMoi());
                    cartDetail.setTotal((double) (productDetail.getPromotionDetail().getGiaMoi() * cartDetail.getQuantity()));
                    cartDetail.setHavePromotion(true);
                    cartDetailRepo.save(cartDetail);
                }
            }
            Integer totalProductInCart = cartDetailService.getQuantityCart_IdAndProductDetail_Id(cart1.getId(), cartDetail.getProductDetail().getId());
            if (totalProductInCart > cartDetail.getProductDetail().getQuantity()) {
                checkXemKoKmDaHet++;
            }

            if (cartDetail.getProductDetail().getStatus() == 0) {
                checkXemSpctActive++;
            }

            if (cartDetail.getProductDetail().getProduct().getStatus() == 0) {
                checkXemSpActive++;
            }
        }
        if (checkXemKmDangCo > 0) {
            redirectAttributes.addFlashAttribute("kmDangCo", true);
            return "redirect:/home/cart";
        }
        if (checkXemKmDaHet > 0) {
            redirectAttributes.addFlashAttribute("hetKm", true);
            return "redirect:/home/cart";
        }
        if (checkXemKoKmDaHet > 0) {
            redirectAttributes.addFlashAttribute("hetSlKoKm", true);
            return "redirect:/home/cart";
        }

        if (checkXemSpActive > 0) {
            redirectAttributes.addFlashAttribute("spInactive", true);
            return "redirect:/home/cart";
        }

        if (checkXemSpctActive > 0) {
            redirectAttributes.addFlashAttribute("spctInactive", true);
            return "redirect:/home/cart";
        }

        session.setAttribute("totalCart", totalCart);
        session.setAttribute("idCartDetail", idCartDetail);
        session.setAttribute("idVoucher", idVoucher);
        session.setAttribute("idAddress", idAddress);
        session.setAttribute("shippingFee", shippingFee);
        session.setAttribute("tienGiam", tienGiam);
        session.setAttribute("hinhThucThanhToan", hinhThucThanhToan);

        if (hinhThucThanhToan) {
            cartDetailService.checkoutSuccess(customer.getId(), totalCart,
                    idCartDetail, idVoucher, idAddress, shippingFee, 1, 0, tienGiam, hinhThucThanhToan);

            if (idVoucher != null) {
                Voucher voucher = voucherService.findById(idVoucher);
                if (!voucher.isFormVoucher()) {
                    Customer customer1 = customerService.findById(customer.getId());
                    List<Voucher> vouchers = customer1.getVouchers();
                    vouchers.remove(voucher);
                    customerRepo.save(customer);
                }
            }

            return "user/thank_for_order";
        } else {
            return "redirect:" + vnPayService.createVnPayPayment(request, totalCart.longValue());
        }
    }


    @GetMapping("/check-out/credit_card")
    public String returnVnPay(@RequestParam("vnp_ResponseCode") String responseCode, HttpSession session,
                              RedirectAttributes redirectAttributes) throws MessagingException {
        if (responseCode.equals("00")) {
            Customer customer = customUserDetailService.getCustomer();
            if (customer != null) {
                Double totalCart = (Double) session.getAttribute("totalCart");
                List<Integer> idCartDetail = (List<Integer>) session.getAttribute("idCartDetail");
                for (Integer id : idCartDetail){
                    CartDetail cartDetail = cartDetailService.findById(id);
                    if (cartDetail.getProductDetail().getQuantity() < cartDetail.getQuantity()){
                        redirectAttributes.addFlashAttribute("hetSlKoKm", true);
                        return "redirect:/home/cart";
                    }
                }

                Integer idVoucher = (Integer) session.getAttribute("idVoucher");
                Integer idAddress = (Integer) session.getAttribute("idAddress");
                Double shippingFee = (Double) session.getAttribute("shippingFee");
                Integer tienGiam = (Integer) session.getAttribute("tienGiam");
                Boolean hinhThucThanhToan = (Boolean) session.getAttribute("hinhThucThanhToan");
                cartDetailService.checkoutSuccess(customer.getId(), totalCart,
                        idCartDetail, idVoucher, idAddress, shippingFee, 3, 1, tienGiam, hinhThucThanhToan);

                if (idVoucher != null) {
                    Voucher voucher = voucherService.findById(idVoucher);
                    if (!voucher.isFormVoucher()) {
                        Customer customer1 = customerService.findById(customer.getId());
                        List<Voucher> vouchers = customer1.getVouchers();
                        vouchers.remove(voucher);
                        customerRepo.save(customer);
                    }
                }
            } else {
                Double totalCart = (Double) session.getAttribute("totalCart");
                List<Integer> idSanPhamChiTiet = (List<Integer>) session.getAttribute("idSanPhamChiTiet");
                List<Integer> quantityBuy = (List<Integer>) session.getAttribute("quantityBuy");
                for (int i = 0; i < idSanPhamChiTiet.size(); i++) {
                    ProductDetail productDetail = productDetailService.findById(idSanPhamChiTiet.get(i));
                    if (productDetail.getQuantity() < quantityBuy.get(i)) {
                        redirectAttributes.addFlashAttribute("hetSlKoKm", true);
                        return "redirect:/home/cart";
                    }
                }
                Integer idVoucher = (Integer) session.getAttribute("idVoucher");
                Double shippingFee = (Double) session.getAttribute("shippingFee");
                Integer tienGiam = (Integer) session.getAttribute("tienGiam");
                String nameRecipient = (String) session.getAttribute("nameRecipient");
                String phone = (String) session.getAttribute("phone");
                String provinceName = (String) session.getAttribute("provinceName");
                String districtName = (String) session.getAttribute("districtName");
                String wardName = (String) session.getAttribute("wardName");
                String addressDetail = (String) session.getAttribute("addressDetail");
                String email = (String) session.getAttribute("email");
                Boolean hinhThucThanhToan = (Boolean) session.getAttribute("hinhThucThanhToan");
                cartDetailService.checkoutSuccessWithoutLogin(totalCart,
                        idSanPhamChiTiet, quantityBuy, idVoucher, shippingFee, tienGiam,
                        nameRecipient, phone, provinceName, districtName, wardName, addressDetail, email, 3, 1, hinhThucThanhToan);
            }

            return "user/thank_for_order";
        } else {
            redirectAttributes.addFlashAttribute("apiVnpayLoi", true);
            return "redirect:/home/cart";
        }
    }

    @GetMapping("/store-policy")
    public String page404() {
        return "user/about_us";
    }

    @GetMapping("/contact")
    public String contact() {
        return "user/contact";
    }

    @GetMapping("/blog")
    public String blog() {
        return "user/blog";
    }


    @GetMapping("/customer-bill-management")
    public String customerBillManagement(Model model) {
        Customer customer = customUserDetailService.getCustomer();
        if (customer == null) {
            return "redirect:/login";
        } else {
            if (!customer.isStatus()) {
                return "redirect:/login";
            }
        }
        List<CustomerBillResponse> status0 = BillRepo.findAllCustomerBillResponse(0, customer.getId());
        Map<Integer, List<CustomerBillResponse>> listChoXacNhan = status0.stream()
                .collect(Collectors.groupingBy(CustomerBillResponse::getIdBill));
        Map<Integer, List<CustomerBillResponse>> sortedListChoXacNhan = listChoXacNhan.entrySet().stream()
                .sorted(Map.Entry.<Integer, List<CustomerBillResponse>>comparingByKey(Comparator.reverseOrder()))
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (oldValue, newValue) -> oldValue,
                        LinkedHashMap::new
                ));
        System.out.println(sortedListChoXacNhan);
        model.addAttribute("listChoXacNhan", sortedListChoXacNhan);

        List<CustomerBillResponse> status1 = BillRepo.findAllCustomerBillResponse(1, customer.getId());
        Map<Integer, List<CustomerBillResponse>> listChoDongGoi = status1.stream()
                .collect(Collectors.groupingBy(CustomerBillResponse::getIdBill));
        Map<Integer, List<CustomerBillResponse>> sortedListChoDongGoi = listChoDongGoi.entrySet().stream()
                .sorted(Map.Entry.<Integer, List<CustomerBillResponse>>comparingByKey(Comparator.reverseOrder()))
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (oldValue, newValue) -> oldValue,
                        LinkedHashMap::new
                ));
        model.addAttribute("listChoDongGoi", sortedListChoDongGoi);

        List<CustomerBillResponse> status2 = BillRepo.findAllCustomerBillResponse(2, customer.getId());
        Map<Integer, List<CustomerBillResponse>> listChoVanChuyen = status2.stream()
                .collect(Collectors.groupingBy(CustomerBillResponse::getIdBill));
        Map<Integer, List<CustomerBillResponse>> sortedListChoVanChuyen = listChoVanChuyen.entrySet().stream()
                .sorted(Map.Entry.<Integer, List<CustomerBillResponse>>comparingByKey(Comparator.reverseOrder()))
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (oldValue, newValue) -> oldValue,
                        LinkedHashMap::new
                ));
        model.addAttribute("listChoVanChuyen", sortedListChoVanChuyen);

        List<CustomerBillResponse> status3 = BillRepo.findAllCustomerBillResponse(3, customer.getId());
        Map<Integer, List<CustomerBillResponse>> listDangVanChuyen = status3.stream()
                .collect(Collectors.groupingBy(CustomerBillResponse::getIdBill));
        Map<Integer, List<CustomerBillResponse>> sortedListDangVanChuyen = listDangVanChuyen.entrySet().stream()
                .sorted(Map.Entry.<Integer, List<CustomerBillResponse>>comparingByKey(Comparator.reverseOrder()))
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (oldValue, newValue) -> oldValue,
                        LinkedHashMap::new
                ));
        model.addAttribute("listDangVanChuyen", sortedListDangVanChuyen);

        List<CustomerBillResponse> status4 = BillRepo.findAllCustomerBillResponse(4, customer.getId());
        Map<Integer, List<CustomerBillResponse>> listDaNhanHang = status4.stream()
                .collect(Collectors.groupingBy(CustomerBillResponse::getIdBill));
        Map<Integer, List<CustomerBillResponse>> sortedListDaNhanHang = listDaNhanHang.entrySet().stream()
                .sorted(Map.Entry.<Integer, List<CustomerBillResponse>>comparingByKey(Comparator.reverseOrder()))
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (oldValue, newValue) -> oldValue,
                        LinkedHashMap::new
                ));
        model.addAttribute("listDaNhanHang", sortedListDaNhanHang);

        List<CustomerBillResponse> status5 = BillRepo.findAllCustomerBillResponse(5, customer.getId());
        Map<Integer, List<CustomerBillResponse>> listHoanTat = status5.stream()
                .collect(Collectors.groupingBy(CustomerBillResponse::getIdBill));
        Map<Integer, List<CustomerBillResponse>> sortedListHoanTat = listHoanTat.entrySet().stream()
                .sorted(Map.Entry.<Integer, List<CustomerBillResponse>>comparingByKey(Comparator.reverseOrder()))
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (oldValue, newValue) -> oldValue,
                        LinkedHashMap::new
                ));
        model.addAttribute("listHoanTat", sortedListHoanTat);

        List<CustomerBillResponse> statusAm1 = BillRepo.findAllCustomerBillResponse(-1, customer.getId());
        Map<Integer, List<CustomerBillResponse>> listHuy = statusAm1.stream()
                .collect(Collectors.groupingBy(CustomerBillResponse::getIdBill));
        Map<Integer, List<CustomerBillResponse>> sortedListHuy = listHuy.entrySet().stream()
                .sorted(Map.Entry.<Integer, List<CustomerBillResponse>>comparingByKey(Comparator.reverseOrder()))
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (oldValue, newValue) -> oldValue,
                        LinkedHashMap::new
                ));
        model.addAttribute("listHuy", sortedListHuy);


        return "user/customerBill";
    }

    @GetMapping("/validate-huy-hoa-don")
    public ResponseEntity<?> validateHuyHoaDon(@RequestParam Integer idBill) {
        Bill bill = BillRepo.findById(idBill).get();
        if (bill.getStatus() >= 3 || bill.getStatus() == -1) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Không thể hủy đơn hàng vì trạng thái đã thay đổi, hay load lại trang");
            return ResponseEntity.badRequest().body(errorResponse);
        }
        return ResponseEntity.ok().build();
    }

    @GetMapping("/huy-hoa-don/{idBill}")
    public String huyHoaDon(@PathVariable Integer idBill, @RequestParam String reason) {
        Bill bill = BillRepo.findById(idBill).get();
        if(bill.getStatus() != 0){
            List<BillDetail> billDetails = BillDetailRepo.findAllByBill_Id(idBill);
            for (BillDetail billDetail : billDetails) {
                ProductDetail productDetail = productDetailService.findById(billDetail.getProductDetail().getId());
                productDetail.setQuantity(productDetail.getQuantity() + billDetail.getQuantity());
                productDetailRepo.save(productDetail);
            }
        }
        if(bill.getVoucher() != null){
            Voucher voucher = voucherService.findById(bill.getVoucher().getId());
            if (voucher != null) {
                if (voucher.isFormVoucher() && voucher.getCondition().equals("Đang diễn ra") &&
                        voucher.isStatus() && voucher.getQuantity() > voucher.getQuantityUsed() ){
                    voucher.setQuantityUsed(voucher.getQuantityUsed() - 1);
                    voucherRepo.save(voucher);
                }
            }
        }
        bill.setStatus(-1);
        BillRepo.save(bill);

        BillHistory billHistory = new BillHistory();
        billHistory.setBill(bill);
        billHistory.setStatus(-1);
        billHistory.setStatusOld(0);
        billHistory.setDateUpdate(new Date());
        billHistory.setDescription("Khách hủy đơn hàng vì " + reason);
        billHistoryRepo.save(billHistory);
        return "redirect:/home/customer-bill-management";
    }


    @GetMapping("/hoan-tat-hoa-don/{idBill}")
    public String hoanTatHoaDon(@PathVariable Integer idBill) {
        Bill bill = BillRepo.findById(idBill).get();
        bill.setStatus(5);
        BillRepo.save(bill);
        BillHistory billHistory = new BillHistory();
        billHistory.setBill(bill);
        billHistory.setStatus(5);
        billHistory.setStatusOld(4);
        billHistory.setDateUpdate(new Date());
        billHistory.setDescription("Hoàn tất đơn hàng");
        billHistoryRepo.save(billHistory);
        return "redirect:/home/customer-bill-management";
    }

    @GetMapping("/api/get-bill-online")
    public ResponseEntity<?> getBillOnline(@RequestParam Integer idBill) {
        Customer customer = customUserDetailService.getCustomer();
        BillOnlineResponse billDetails = billRepo.getBillOnlineByCustomerAndId(customer.getId(), idBill);
        return ResponseEntity.ok(billDetails);
    }

    @GetMapping("/api/get-bill-online-no-login")
    public ResponseEntity<?> getBillOnlineNo(@RequestParam Integer idBill) {
        BillOnlineResponse billDetails = billRepo.getBillOnlineById(idBill);
        return ResponseEntity.ok(billDetails);
    }

    @GetMapping("/api/get-bill-detail-online")
    public ResponseEntity<?> getBillDetailOnline(@RequestParam Integer idBill) {
        List<BillDetailOnlineResponse> billDetails = billRepo.getBillDetailOnlineById(idBill);
        return ResponseEntity.ok(billDetails);
    }

    @GetMapping("/api/get-bill-history")
    public ResponseEntity<?> getBillHistory(@RequestParam Integer idBill) {
        List<BillHistory> billHistories = billHistoryRepo.findAllByBill_Id(idBill);
        return ResponseEntity.ok(billHistories);
    }

    //---------------------------Buy without login---------------------------
    @GetMapping("/api/add-to-cart-without-login")
    public ResponseEntity<?> addToCartWithoutLogin(@RequestParam Integer idSpct) {
        ProductDetail productDetail = productDetailService.findById(idSpct);
        Map<String, String> errorResponse = new HashMap<>();
        if (productDetail.getQuantity() == 0) {
            errorResponse.put("error", "Sản phẩm đã hết hàng.");
            return ResponseEntity.badRequest().body(errorResponse);
        }
        if (productDetail.getStatus() == 0) {
            errorResponse.put("error", "Sản phẩm đã ngừng kinh doanh.");
            return ResponseEntity.badRequest().body(errorResponse);
        }
        if (productDetail.getProduct().getStatus() == 0) {
            errorResponse.put("error", "Danh mục sản phẩm đã ngừng kinh doanh.");
            return ResponseEntity.badRequest().body(errorResponse);
        }
        return ResponseEntity.ok().build();
    }


    @PostMapping("/api/cart-khong-dang-nhap")
    public ResponseEntity<?> apiCartKhongDangNhap(@RequestBody List<CartKoDangNhapReq> list) {
        try {
            List<CartKoDangNhapRes> listCartKoDangNhapRes = new ArrayList<>();
            for (CartKoDangNhapReq item : list) {
                listCartKoDangNhapRes.add(cartDetailService.getCartKoDangNhapByIdAndQuantity(item.getIdSpct(), item.getSoLuong()));
            }
            return ResponseEntity.ok(listCartKoDangNhapRes);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e);
        }
    }

    @GetMapping("/check-out-without-login")
    public String checkOutt(Model model,
                            @RequestParam("selectedCartIds") List<Integer> selectedIdSpcts,
                            @RequestParam List<Integer> soLuong,
                            RedirectAttributes redirectAttributes) {
        List<CartKoDangNhapRes> listCartKoDangNhapRes = new ArrayList<>();
        for (int i = 0; i < selectedIdSpcts.size(); i++) {
            ProductDetail productDetail = productDetailService.findById(selectedIdSpcts.get(i));
            if (productDetail.getQuantity() == 0) {
                redirectAttributes.addFlashAttribute("hetSlKoKm", true);
                return "redirect:/home/cart";
            } else if (productDetail.getQuantity() < soLuong.get(i)) {
                redirectAttributes.addFlashAttribute("hetSlKoKm", true);
                return "redirect:/home/cart";
            } else if (productDetail.getStatus() == 0) {
                redirectAttributes.addFlashAttribute("spctInactive", true);
                return "redirect:/home/cart";
            } else if (productDetail.getProduct().getStatus() == 0) {
                redirectAttributes.addFlashAttribute("spInactive", true);
                return "redirect:/home/cart";
            }
            listCartKoDangNhapRes.add(cartDetailService.getCartKoDangNhapByIdAndQuantity(selectedIdSpcts.get(i), soLuong.get(i)));
        }
        model.addAttribute("listCartDetail", listCartKoDangNhapRes);
        return "user/check_out_without_login";
    }

    @GetMapping("/get-voucher-without-login")
    public ResponseEntity<?> getVoucher() {
        List<Voucher> listVoucher = voucherService.getAllCheckOut();
        return ResponseEntity.ok(listVoucher);
    }

    @PostMapping("/check-out-without-login/cod")
    public String checkOutWithOutLogin(@RequestParam Double totalCart,
                                       @RequestParam("idSanPhamChiTiet") List<Integer> idSanPhamChiTiet,
                                       @RequestParam("quantityBuy") List<Integer> quantityBuy,
                                       @RequestParam(required = false) Integer idVoucher,
                                       @RequestParam Double shippingFee,
                                       @RequestParam Integer tienGiam,
                                       @RequestParam(name = "hinhThucThanhToan") boolean hinhThucThanhToan,
                                       @RequestParam String nameRecipient,
                                       @RequestParam String phone,
                                       @RequestParam String provinceName,
                                       @RequestParam String districtName,
                                       @RequestParam String wardName,
                                       @RequestParam String addressDetail,
                                       @RequestParam String email,
                                       RedirectAttributes redirectAttributes,
                                       HttpSession session,
                                       HttpServletRequest request) throws MessagingException {
        if (idVoucher != null) {
            Voucher voucherCheck = voucherService.findById(idVoucher);
            if (voucherCheck.getCondition().equals("Đã kết thúc") || !voucherCheck.isStatus() || voucherCheck.getQuantity() <= voucherCheck.getQuantityUsed()) {
                redirectAttributes.addFlashAttribute("hetVoucher", true);
                return "redirect:/home/cart";
            }
        }

        for (int i = 0; i < idSanPhamChiTiet.size(); i++) {
            ProductDetail productDetail = productDetailService.findById(idSanPhamChiTiet.get(i));
            if (productDetail.getQuantity() == 0) {
                redirectAttributes.addFlashAttribute("hetSlKoKm", true);
                return "redirect:/home/cart";
            } else if (productDetail.getQuantity() < quantityBuy.get(i)) {
                redirectAttributes.addFlashAttribute("hetSlKoKm", true);
                return "redirect:/home/cart";
            } else if (productDetail.getStatus() == 0) {
                redirectAttributes.addFlashAttribute("spctInactive", true);
                return "redirect:/home/cart";
            } else if (productDetail.getProduct().getStatus() == 0) {
                redirectAttributes.addFlashAttribute("spInactive", true);
                return "redirect:/home/cart";
            }
        }
        session.setAttribute("totalCart", totalCart);
        session.setAttribute("idSanPhamChiTiet", idSanPhamChiTiet);
        session.setAttribute("quantityBuy", quantityBuy);
        session.setAttribute("idVoucher", idVoucher);
        session.setAttribute("shippingFee", shippingFee);
        session.setAttribute("tienGiam", tienGiam);
        session.setAttribute("nameRecipient", nameRecipient);
        session.setAttribute("phone", phone);
        session.setAttribute("provinceName", provinceName);
        session.setAttribute("districtName", districtName);
        session.setAttribute("wardName", wardName);
        session.setAttribute("addressDetail", addressDetail);
        session.setAttribute("email", email);
        session.setAttribute("hinhThucThanhToan", hinhThucThanhToan);

        if (hinhThucThanhToan) {
            cartDetailService.checkoutSuccessWithoutLogin(totalCart, idSanPhamChiTiet, quantityBuy, idVoucher, shippingFee,
                    tienGiam, nameRecipient, phone, provinceName, districtName, wardName, addressDetail,
                    email, 1, 0, hinhThucThanhToan);
            return "user/thank_for_order";
        } else {
            return "redirect:" + vnPayService.createVnPayPayment(request, totalCart.longValue());
        }
    }

    @GetMapping("/search-bill-online/{idBill}")
    public String searchBillOnline(@PathVariable String idBill) {
        return "user/search_bill_by_mail";
    }


    //---------------------------Profile---------------------------
    @GetMapping("/profile")
    public String getProfile(Model model) {
        Customer customer = customUserDetailService.getCustomer();
        if (customer == null) {
            return "redirect:/login";
        } else {
            if (!customer.isStatus()) {
                return "redirect:/login";
            }
        }
        model.addAttribute("customer", customer);
        return "user/profile";
    }

    //Sửa khách hàng
    @PostMapping("editprofile/{customerId}")
    public String editProfile(@PathVariable("customerId") Integer id,
                              @RequestParam String username,
                              @RequestParam String name,
                              @RequestParam String phone,
                              @RequestParam String email,
                              @RequestParam String gender,
                              @RequestParam String dateOfBirth,
                              @RequestParam("photo") MultipartFile photo,
                              RedirectAttributes redirectAttributes) {
        try {
            Customer customer = customerService.findById(id);
            if (customer == null) {
                return "redirect:/login";
            } else {
                if (!customer.isStatus()) {
                    return "redirect:/login";
                }
            }
            customer.setUsername(username);
            customer.setName(name);
            customer.setPhone(phone);
            customer.setEmail(email);
            customer.setGender(gender);
            customer.setDateOfBirth(LocalDate.parse(dateOfBirth));

            if (!photo.isEmpty()) {
                customer.setPhoto(uploadService.storeFile(photo));
            }

            customerService.save(customer);
            redirectAttributes.addFlashAttribute("message", "Chỉnh sửa thông tin thành công!");

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Có lỗi xảy ra khi chỉnh sửa thông tin!");
        }
        return "redirect:/home/profile";
    }


    //Thêm địa chỉ khách hàng
    @PostMapping("/{customerId}/save-profile")
    public String saveAddress(@PathVariable Integer customerId,
                              @RequestParam(required = false) Integer addressId,
                              @RequestParam String nameRecipient,
                              @RequestParam String phone,
                              @RequestParam String provinceName,
                              @RequestParam String districtName,
                              @RequestParam String wardName,
                              @RequestParam String addreseDetail,
                              RedirectAttributes redirectAttributes) {
        try {
            Customer customer = customerService.findById(customerId);
            if (customer == null) {
                redirectAttributes.addFlashAttribute("error", "Khách hàng không tồn tại!");
                return "redirect:/home/profile";
            }

            Address customerAddress = new Address();

            customerAddress.setCustomer(customer);
            customerAddress.setNameRecipient(nameRecipient);
            // Kiểm tra nếu phone trống, lấy phone từ customer
            if (phone == null || phone.trim().isEmpty()) {
                customerAddress.setPhone(customer.getPhone());
            } else {
                customerAddress.setPhone(phone);
            }
            customerAddress.setProvince(provinceName);
            customerAddress.setDistrict(districtName);
            customerAddress.setWard(wardName);
            customerAddress.setAddreseDetail(addreseDetail);
            // Kiểm tra nếu khách hàng chưa có địa chỉ nào thì đặt là mặc định
            List<Address> existingAddresses = addressService.findByCustomerId(customerId);
            customerAddress.setDefault(existingAddresses.isEmpty());

            addressService.save(customerAddress);

            redirectAttributes.addFlashAttribute("message", (addressId != null) ? "Cập nhật địa chỉ thành công!" : "Thêm địa chỉ thành công!");
            return "redirect:/home/" + customerId + "/addresses";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Đã xảy ra lỗi: " + e.getMessage());
            return "redirect:/home/" + customerId + "/addresses";
        }
    }

    //Sửa địa chỉ khách hàng
    @GetMapping("/{customerId}/addresses/{addressId}")
    public String editAddress(@PathVariable Integer customerId,
                              @PathVariable Integer addressId,
                              Model model,
                              RedirectAttributes redirectAttributes) {
        try {
            Customer customer = customerService.findById(customerId);
            if (customer == null) {
                redirectAttributes.addFlashAttribute("error", "Khách hàng không tồn tại!");
                return "redirect:/home/" + customerId + "/addresses";
            }
            Address customerAddress = addressService.findById(addressId);
            if (customerAddress == null || !customerAddress.getCustomer().getId().equals(customerId)) {
                redirectAttributes.addFlashAttribute("error", "Địa chỉ không tồn tại hoặc không thuộc khách hàng này!");
                return "redirect:/home/" + customerId + "/addresses";
            }
            model.addAttribute("customer", customer);
            model.addAttribute("address", customerAddress);

            return "user/profile-addressedit";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Đã xảy ra lỗi: " + e.getMessage());
            return "redirect:/home/" + customerId + "/addresses";
        }
    }

    //Sửa địa chỉ khách hàng
    @PostMapping("/{customerId}/update-address/{addressId}")
    public String updateAddress(@PathVariable Integer customerId,
                                @PathVariable Integer addressId,
                                @RequestParam String nameRecipient,
                                @RequestParam String phone,
                                @RequestParam String provinceName,
                                @RequestParam String districtName,
                                @RequestParam String wardName,
                                @RequestParam String addreseDetail,
                                RedirectAttributes redirectAttributes) {
        try {

            Customer customer = customerService.findById(customerId);
            if (customer == null) {
                redirectAttributes.addFlashAttribute("error", "Khách hàng không tồn tại!");
                return "redirect:/home/" + customerId + "/addresses";
            }

            Address customerAddress = addressService.findById(addressId);
            if (customerAddress == null || !customerAddress.getCustomer().getId().equals(customerId)) {
                redirectAttributes.addFlashAttribute("error", "Địa chỉ không tồn tại hoặc không thuộc khách hàng này!");
                return "redirect:/home/" + customerId + "/addresses";
            }

            customerAddress.setNameRecipient(nameRecipient);
            customerAddress.setPhone(phone);
            customerAddress.setProvince(provinceName);
            customerAddress.setDistrict(districtName);
            customerAddress.setWard(wardName);
            customerAddress.setAddreseDetail(addreseDetail);
            addressService.save(customerAddress);

            redirectAttributes.addFlashAttribute("message", "Cập nhật địa chỉ thành công!");
            return "redirect:/home/" + customerId + "/addresses";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Đã xảy ra lỗi: " + e.getMessage());
            return "redirect:/home/" + customerId + "/addresses";
        }
    }

    //Load ảnh
    @GetMapping("/flim-image/{name}")
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


    //Load table địa chỉ khách hàng
    @GetMapping("/{customerId}/addresses")
    public String getAddressesByCustomerId(@PathVariable Integer customerId, Model model) {
        Customer customer = customerService.findById(customerId);
        if (customer == null) {
            model.addAttribute("error", "Không tìm thấy khách hàng!");
            return "error";
        }
        List<Address> addresses = addressService.findByCustomerId(customerId);
        model.addAttribute("customer", customer);
        model.addAttribute("addresses", addresses);
        return "user/profile-address";
    }

    //Chỉnh trạng thái địa chỉ khách hàng
    @PostMapping("/set-default-address")
    public String setDefaultAddress(@RequestParam Integer addressId, @RequestParam Integer
            customerId, RedirectAttributes redirectAttributes) {
        try {
            addressService.setDefaultAddress(addressId, customerId);
            redirectAttributes.addFlashAttribute("message", "Địa chỉ đã được đặt làm mặc định thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Không thể đặt địa chỉ này làm mặc định: " + e.getMessage());
        }
        return "redirect:/home/" + customerId + "/addresses";
    }

    // Xóa địa chỉ khách hàng
    @GetMapping("/delete-address/{customerId}/{id}")
    public String deleteAddress(@PathVariable Integer customerId, @PathVariable Integer id, RedirectAttributes
            redirectAttributes) {
        Address address = addressService.findById(id);
        if (address.isDefault()) {
            redirectAttributes.addFlashAttribute("error", "Không thể xóa địa chỉ mặc định.");
            return "redirect:/home/" + customerId + "/addresses";
        }
        addressService.delete(id);
        redirectAttributes.addFlashAttribute("message", "Xóa địa chỉ thành công!");
        return "redirect:/home/" + customerId + "/addresses";
    }

    //Đổi mật khẩu khách hàng
    @GetMapping("change-password")
    public String changepassword() {
        return "user/changepassword";
    }

    @PostMapping("/change-password")
    public ResponseEntity<?> changePassword(@RequestBody Map<String, String> request) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        String currentPassword = request.get("currentPassword");
        String newPassword = request.get("newPassword");

        if (currentPassword == null || newPassword == null) {
            return ResponseEntity.badRequest().body("Mật khẩu hiện tại và mật khẩu mới không thể bỏ trống.");
        }

        Optional<Customer> customerOpt = customerService.findByUsername(username);
        if (customerOpt.isPresent()) {
            Customer customer = customerOpt.get();

            // Kiểm tra mật khẩu hiện tại có đúng không
            if (!passwordEncoder.matches(currentPassword, customer.getPassword())) {
                return ResponseEntity.badRequest().body("Mật khẩu hiện tại không chính xác.");
            }

            // Cập nhật mật khẩu mới
            customer.setPassword(passwordEncoder.encode(newPassword));
            customerService.save(customer);

            return ResponseEntity.ok("Đổi mật khẩu thành công.");
        }

        return ResponseEntity.badRequest().body("Không tìm thấy người dùng.");
    }

    //Gửi OTP khi muốn thay đổi email
    @PostMapping("/send-otp")
    public ResponseEntity<?> sendOtp(Authentication authentication) {
        String email = customerService.getCustomerEmail(authentication.getName());
        String otp = otpService.generateAndSendOtp(email);

        if (otp == null) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Không thể gửi OTP");
        }

        return ResponseEntity.ok("OTP đã được gửi");
    }

    //Xác nhận OTP
    @PostMapping("/verify-otp")
    public ResponseEntity<?> verifyOtp(@RequestBody Map<String, String> request) {
        String otp = request.get("otp");
        boolean isValid = otpService.verifyOtp(otp);

        if (!isValid) {
            return ResponseEntity.badRequest().body("OTP không hợp lệ");
        }

        return ResponseEntity.ok(Map.of("success", true));
    }

    //Thay email mới
    @PostMapping("/update-email")
    public ResponseEntity<?> updateEmail(@RequestBody Map<String, String> request, Authentication authentication) {
        String newEmail = request.get("email");
        boolean isUpdated = customerService.updateEmail(authentication.getName(), newEmail);

        if (!isUpdated) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Không thể cập nhật email");
        }

        return ResponseEntity.ok("Email đã được cập nhật");
    }
}



