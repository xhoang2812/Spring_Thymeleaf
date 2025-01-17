package com.poly.du_an_tot_nghiep_f6.controller.sale;

import com.poly.du_an_tot_nghiep_f6.entity.*;
import com.poly.du_an_tot_nghiep_f6.repository.BillHistoryRepo;
import com.poly.du_an_tot_nghiep_f6.repository.CustomerRepo;
import com.poly.du_an_tot_nghiep_f6.request.CustomerSaleRequest;
import com.poly.du_an_tot_nghiep_f6.request.PayRequest;
import org.springframework.security.core.Authentication;
import com.poly.du_an_tot_nghiep_f6.response.*;
import com.poly.du_an_tot_nghiep_f6.service.*;
import com.poly.du_an_tot_nghiep_f6.service.impl.*;
import org.apache.commons.lang.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

@RestController()
@RequestMapping("/api/sale")
public class SaleControllerAPI {

    public static int cartIdSelect = 1;

    @Autowired
    AddressService addressService;

    @Autowired
    ProductDetailServiceImpl productDetailService;

    @Autowired
    CustomerRepo customerRepo;

    @Autowired
    CustomerService customerService;

    @Autowired
    GiaoCaoServiceImpl giaoCaoService;

    @Autowired
    CartInCounterServiceImpl cartService;

    @Autowired
    CartDetailInCounterServiceImpl cartDetailService;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    BillHistoryRepo billHistoryRepo;

    @Autowired
    VoucherService voucherService;

    @Autowired
    IBillService billService;

    @Autowired
    IBillDetailService billDetailService;

    @Autowired
    CategoryServiceImpl categoryService;

    @Autowired
    BrandServiceImpl brandService;

    @Autowired
    ColorServiceImpl colorService;

    @Autowired
    MaterialServiceImpl materialService;

    @Autowired
    SizeServiceImpl sizeService;

    @Autowired
    StyleServiceImpl styleService;

    @Autowired
    PaymentMethodsServiceImpl paymentMethodsService;

    @GetMapping("/get-products")
    public List<ProductDetailResponse> getProductDetails() {
        return productDetailService.findAllResponse();
    }

    @GetMapping("/filter-product")
    public List<ProductDetailResponse> getProductDetails(
            @RequestParam(required = false) Integer idSize,
            @RequestParam(required = false) Integer idColor,
            @RequestParam(required = false) Integer idStyle,
            @RequestParam(required = false) Integer idCategory,
            @RequestParam(required = false) Integer idMaterial,
            @RequestParam(required = false) Integer idBrand,
            @RequestParam(required = false) String keyWord) {
        return productDetailService.filterProduct(idSize, idColor, idStyle, idCategory, idMaterial, idBrand, keyWord);
    }

    @GetMapping("/get-customers")
    public List<CustomerSaleResponse> getCustomers(@RequestParam String keyWord) {
        return customerRepo.findCustomerResponse(keyWord);
    }

    @PostMapping("/create-customer")
    public int createCustomer(@RequestBody CustomerSaleRequest request) {
        System.out.println(request);
        try {
            Customer customer = new Customer(
                    0,
                    "KH" + RandomStringUtils.randomNumeric(4),
                    null,
                    request.getName(),
                    request.getGender(),
                    request.getPhone(),
                    request.getEmail(),
                    "kh" + RandomStringUtils.randomNumeric(4),
                    passwordEncoder.encode(RandomStringUtils.randomNumeric(8)),
                    null,
                    request.getBirthDay(),
                    new Date(),
                    new Date(),
                    null,
                    true
            );
            customer = customerRepo.save(customer);
            cartService.getCart(cartIdSelect).setCustomer(customer);
            return customer.getId();
        } catch (Exception e) {
            System.out.println("Lỗi : " + e.getMessage());
            return -1;
        }
    }

    @GetMapping("/get-product-filter")
    public List<ProductDetailResponse> getProductDetailsFilter() {
        return productDetailService.findAllResponse();
    }

    @GetMapping("/get-attribute-product")
    public List<List<?>> getAttributeProduct() {
        List<List<?>> attributeProducts = new ArrayList<>();
        attributeProducts.add(categoryService.findAll());
        attributeProducts.add(brandService.findAll());
        attributeProducts.add(colorService.findAll());
        attributeProducts.add(materialService.findAll());
        attributeProducts.add(sizeService.findAll());
        attributeProducts.add(styleService.findAll());
        return attributeProducts;
    }

    @GetMapping("/get-carts")
    public List<CartInCounter> getCarts() {
        return cartService.getCartInCounters();
    }

    @GetMapping("/get-cart")
    public CartInCounter getCart(@RequestParam(required = false) Integer idCart) {
        return cartService.getCart(idCart);
    }

    @GetMapping("/get-voucher-for-customer")
    public List<VoucherSaleResponse> getVoucherForCustomer(@RequestParam Integer idCustomer) {
        return voucherService.getVoucherResponse(idCustomer);
    }

    @PutMapping("/set-voucher-for-cart")
    public int setVoucherForCart(@RequestParam Integer idVoucher) {
        boolean checkSet = cartService.setVoucher(cartIdSelect, voucherService.getVoucher(idVoucher));
        return checkSet ? 0 : 1;
    }

    @PutMapping("/set-ship-to-cart")
    public boolean setShip(@RequestParam int idCart) {
        cartService.getCart(idCart).setShip(!cartService.getCart(idCart).isShip());
        return true;
    }

    @PutMapping("/set-address-to-cart")
    public Address setAddressToCart(@RequestParam int idAddress) {
        Address address = addressService.findById(idAddress);
        cartService.getCart(cartIdSelect).setAddress(address);
        return address;
    }

    @GetMapping("/get-product-in-cart")
    public List<CartDetailResponse> getProductInCart(@RequestParam int idCart) {
        return cartDetailService.getCartDetailResponses(idCart);
    }

    @GetMapping("/get-cartId-select")
    public int getCartIdSelect() {
        return cartIdSelect;
    }

    @GetMapping("/get-address-for-cart")
    public List<AddressResponse> getAddressForCart() {
        int idCustomer = cartService.getCart(cartIdSelect).getCustomer().getId();
        return addressService.findByIdCustomer(idCustomer);
    }


    @GetMapping("/create-cart")
    public CartInCounter createCart() {
        return cartService.createCart();
    }

    @DeleteMapping("/remove-cart")
    public boolean removeCart(@RequestParam int idCart, @RequestParam boolean status) {
        return cartService.deleteCart(idCart, status);
    }

    @PostMapping("/add-product-to-cart")
    public int addProductToCart(@RequestParam int idCart, @RequestParam int idProduct, @RequestParam int quantity) {
        ProductDetail productDetail = productDetailService.findById(idProduct);
        if (productDetail == null) {
            return 2;
        }
        if (productDetail.getQuantity() >= quantity) {
            CartDetailInCounter cart = new CartDetailInCounter(0, cartService.getCart(idCart), productDetail, productDetail.getPromotionDetail(), quantity);
            cartDetailService.addCartDetail(cart, false);
            productDetailService.changerQuantity(idProduct, quantity, false);
            return 0;
        }
        return 1;
    }

    @DeleteMapping("/delete-product-in-cart")
    public boolean deleteProductInCart(@RequestParam int idCartDetail) {
        cartDetailService.deleteCartDetail(idCartDetail, true);
        return true;
    }

    @DeleteMapping("/delete-voucher-in-cart")
    public boolean deleteVoucherInCart(@RequestParam int idCart) {
        return cartService.deleteVoucher(idCart);
    }

    @PutMapping("/update-quantity-product-in-cart")
    public int updateQuantityProductInCart(@RequestParam int idCartDetail, @RequestParam int quantity, @RequestParam boolean status) {
        CartDetailInCounter cartDetailInCounter = cartDetailService.getCartDetail(idCartDetail);// lấy số lượng ở giỏ
        ProductDetail productDetail = productDetailService.findById(cartDetailInCounter.getProductDetail().getId());// lấy số lượng ở kho
        int totalQuantity = productDetail.getQuantity() + cartDetailInCounter.getQuantity();//  số lượng kho + giỏ hàng
        if (totalQuantity >= quantity) {//kiểm tra số lượng trong kho có đủ ko
            if (quantity >= cartDetailInCounter.getQuantity()) {// xử lý thay đổi số lượng
                productDetailService.changerQuantity(productDetail.getId(), quantity - cartDetailInCounter.getQuantity(), false);
            } else {
                productDetailService.changerQuantity(productDetail.getId(), cartDetailInCounter.getQuantity() - quantity, true);
            }
            cartDetailService.updateCartDetail(idCartDetail, quantity);// thay đổi số lượng người dùng nhập

            if (checkVoucherUpdateQuantity(cartDetailInCounter.getCartInCounter().getId())) {//check điệu kiện voucher
                if (status) {// trạng thái update
                    return 2;// trả về 2 nếu là true, ko đủ
                }
            }
            return 0;
        } else {
            return 1;
        }
    }

//    @PutMapping("/set-customer-in-cart")
//    public Customer setCustomerToCart(@RequestParam int idCart, @RequestParam int idCustomer) {
//        Customer customer = customerRepo.findById(idCustomer).get();
//        if (!cartService.setCustomer(idCart, customer)) {
//            Customer customer1 = new Customer();
//            customer1.setId(cartIdSelect);
//            return customer1;
//        }
//        ;
//        Address address;
//        try {
//            address = addressService.findByCustomerId(idCustomer).get(0);
//        } catch (Exception e) {
//            address = null;
//        }
//        cartService.getCart(cartIdSelect).setAddress(address);
//        return customer;
//    }

    @PutMapping("/set-customer-in-cart")
    public Customer setCustomerToCart(@RequestParam int idCart, @RequestParam int idCustomer) {
        Customer customer = customerRepo.findById(idCustomer).get();
        if (!cartService.setCustomer(idCart, customer)) {
            Customer customer1 = new Customer();
            customer1.setId(cartIdSelect);
            return customer1;
        }
        if(!customer.getAddresses().isEmpty()){
            for (Address address : customer.getAddresses()) {
                if (address.isDefault()) {
                    cartService.getCart(cartIdSelect).setAddress(address);
                }
            }
        }else{
            cartService.getCart(cartIdSelect).setAddress(null);
        }
        return customer;
    }

    @DeleteMapping("/remove-customer-in-cart")
    public boolean removeCustomerToCart() {
        try {
            CartInCounter cartInCounter = cartService.getCart(cartIdSelect);
            if (cartInCounter.getVoucher() != null) {
                customerService.addVoucher(cartInCounter.getCustomer().getId(), cartInCounter.getVoucher().getId());
                cartInCounter.setVoucher(null);
            }
            cartInCounter.setCustomer(null);
            cartInCounter.setAddress(null);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    @PutMapping("/edit-bill")
    public int editBill(@RequestParam int idBill,@RequestParam int statusOld) {
        if(billService.getBill(idBill).getStatus()!=statusOld){
            return -3;
        }
        if (cartService.renderCartToBill(idBill) == -1) {
            return cartIdSelect;
        }
        return 0;
    }


    @PostMapping("/set-cartId-select")
    public boolean setCartIdSelect(@RequestParam int cartId_select) {
        cartIdSelect = cartId_select;
        return true;
    }

    @PostMapping("/pay")
    public int handlePay(@RequestParam int idCart, @RequestBody PayRequest payRequest, Authentication authentication) {
        List<CartDetailInCounter> cartDetailInCounters = cartDetailService.getCartDetails(idCart);
        try {
            if (cartDetailInCounters != null) {
                if (!cartDetailInCounters.isEmpty()) {
                    CartInCounter cartInCounter = cartService.getCart(idCart);
                    return handleShip(cartInCounter, payRequest, authentication);
                }
                return 1;//Không có giỏ sản phẩm trong giỏ sẽ return 1;
            } else {
                return 1;
            }
        } catch (Exception e) {
            System.out.println("Lỗi 3: " + e.getMessage());
            return 3;
        }
    }

    private int handleShip(CartInCounter cartInCounter, PayRequest payRequest, Authentication authentication) {
        int idCart = cartInCounter.getId();
        cartInCounter.setDescriptionAddress(payRequest.getDescribeCustomerAddress());
        cartInCounter.setDescriptionBill(payRequest.getDescribeCustomerBill());
        cartInCounter.setShipPrice(payRequest.getShipPrice());
        if (cartInCounter.getBillEdit() == null) {
            PaymentMethods paymentMethods = paymentMethodsService.getPaymentMethods(payRequest.getIdPaymentMethods());
            cartInCounter.setPaymentMethods(paymentMethods);
        }
        if (payRequest.getNameCustomer() == null && cartInCounter.isShip()) {
            return 2;//Lỗi ko có địa chỉ
        }
        if (!cartInCounter.isShip()) {//Nếu không ship thì thanh toán
            return pay(idCart, authentication);
        } else if (cartInCounter.getCustomer() == null) {// Xử lý khách lẻ có ship
            Address address = addressService.saveAddressPay(payRequest, null);
            cartInCounter.setAddress(address);
            return pay(idCart, authentication);
        } else if (checkAddress(payRequest, cartInCounter.getAddress())) {//Nếu địa chỉ trùng với địa chỉ trong database
            Address address = addressService.saveAddressPay(payRequest, null);
            cartInCounter.setAddress(address);
            return pay(idCart, authentication);
        } else if (payRequest.getSaveAddress()) {// Nếu ko trùng -> Check xem có lưu địa chỉ mới hay ko
            Address address = addressService.saveAddressPay(payRequest, cartInCounter.getCustomer());
            cartInCounter.setAddress(address);
            return pay(idCart, authentication);
        } else {
            Address address = addressService.saveAddressPay(payRequest, null);
            cartInCounter.setAddress(address);
            return pay(idCart, authentication);
        }
    }

    private boolean checkVoucherUpdateQuantity(int id) {//kiếm tra điều kiện voucher
        CartInCounter cartInCounter = cartService.getCart(id);
        Voucher voucher = cartInCounter.getVoucher();
        if (voucher != null) {//nếu có voucher thì check
            double totalPrice = cartDetailService.getTotalPrice(cartInCounter.getId());
            return totalPrice < voucher.getMinimumOrder();// nếu tổng tiền bé hơn số tiền nhỏ nhất của voucher thì trả về true;
        } else {
            return false;//không có voucher thì trả vè false
        }
    }


    private boolean checkAddress(PayRequest payRequest, Address address) {//Chech Trùng địa chỉ
        if (address == null) {
            return false;
        }
        if (!address.getNameRecipient().equals(payRequest.getNameCustomer())) {
            return false;
        }
        if (!address.getPhone().equals(payRequest.getPhoneCustomer())) {
            return false;
        }
        if (!address.getProvince().equals(payRequest.getProvince())) {
            return false;
        }
        if (!address.getDistrict().equals(payRequest.getDistrict())) {
            return false;
        }
        if (!address.getWard().equals(payRequest.getWard())) {
            return false;
        }
        if (!address.getAddreseDetail().equals(payRequest.getAddressDetailCustomer())) {
            return false;
        }
        return true;
    }


    public int pay(int idCart, Authentication authentication) {
        LocalDateTime currentDateTime = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss dd/MM/yyyy", new Locale("vi", "VN"));
        String formattedDateTime = currentDateTime.format(formatter);
        Employee employee = null;
        if (authentication != null) {
            employee = voucherService.currentEmployee(authentication);
        } else {
            employee = giaoCaoService.getEmployeeAreWorking();
        }
        if (cartService.getCart(idCart).getBillEdit() == null) {
            try {
                Bill bill = billService.save(cartService.getBill(idCart, employee));
                BillHistory billHistory = new BillHistory(0, employee, bill, "Đặt hàng lúc " + formattedDateTime, bill.getStatus(), -2, new Date());
                billHistoryRepo.save(billHistory);
                billDetailService.saveBillDetail(cartDetailService.getBillDetail(idCart, bill));
                return bill.getId();
            } catch (Exception e) {
                System.out.println("Lỗi thanh toán: " + e.getMessage());
                return 3;
            }
        } else {
            Bill billOld = cartService.getCart(idCart).getBillEdit();
            int statusInt = billService.getBill(billOld.getId()).getStatus();
            if (statusInt != 1 && statusInt != 2) {
                return -1;
            }
            Bill billNew = cartService.getBill(idCart, employee);
            int statusOld = billOld.getStatus();
            billOld.setStatus(billNew.getShipPrice() > 0 ? 1 : 5);
            billOld.setCustomer(billNew.getCustomer());
            double priceCharge = billNew.getTotalPrice() - billOld.getTotalPrice();
            String status = (priceCharge >= 0 ? ", khách cần thanh toán thêm: " + priceCharge : ", trả lại khách: " + Math.abs(priceCharge)) + " đ";
            billOld.setTotalPrice(billNew.getTotalPrice());
            billOld.setShipPrice(billNew.getShipPrice());
            billOld.setVoucher(billNew.getVoucher());
            billOld.setAddress(billNew.getShipPrice() > 0 ? billNew.getAddress() : null);
            billOld.setDescriptionBill(billNew.getDescriptionBill());
            billOld.setDescriptionShip(billNew.getDescriptionShip());
            billOld.setTienGiam(billNew.getTienGiam());
            BillHistory billHistory = new BillHistory(0, employee, billOld, "Sửa đơn lúc " + formattedDateTime + status, billOld.getStatus(), statusOld, new Date());
            billHistoryRepo.save(billHistory);
            billDetailService.saveBillDetailEdit(billOld.getId(), cartDetailService.getBillDetail(idCart, billOld));
            billService.save(billOld);
            return billOld.getId();
        }
    }

    @GetMapping("/get-all-cartdetail")
    public List<CartDetailInCounter> getCartDetailInCounters() {
        return cartDetailService.getAll();
    }
}
