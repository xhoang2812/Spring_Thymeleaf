package com.poly.du_an_tot_nghiep_f6.service.impl;

import com.poly.du_an_tot_nghiep_f6.entity.*;
import com.poly.du_an_tot_nghiep_f6.repository.*;
import com.poly.du_an_tot_nghiep_f6.response.CartDetailOnlineResponse;
import com.poly.du_an_tot_nghiep_f6.response.CartKoDangNhapRes;
import com.poly.du_an_tot_nghiep_f6.service.ICartDetailService;
import com.poly.du_an_tot_nghiep_f6.service.MailService;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.util.ArrayList;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CartDetailServiceImpl implements ICartDetailService {

    private final CartDetailRepo cartDetailRepo;
    private final CartRepo cartRepo;
    private final PromotionDetailRepo promotionDetailRepo;
    private final BillRepo billRepo;
    private final BillDetailRepo billDetailRepo;
    private final ProductDetailRepo productDetailRepo;
    private final VoucherRepo voucherRepo;
    private final AddressRepo addressRepo;
    private final CustomerRepo customerRepo;
    private final PromotionRepo promotionRepo;
    private final BillHistoryRepo billHistoryRepo;
    private final MailService mailService;


    public List<CartDetail> findAllCartDetailByIdCart(Integer idCart) {
        return cartDetailRepo.findAllByCart_Id(idCart);
    }

    public void deleteById(Integer id) {
        cartDetailRepo.deleteById(id);
    }

    public CartDetail findById(Integer id) {
        return cartDetailRepo.findById(id).orElseThrow(() -> new RuntimeException("CartDetail not found"));
    }

    public Integer countProductInCart(Integer idCart) {
        return cartDetailRepo.countCartDetailByCart_Id(idCart);
    }

    public List<CartDetailOnlineResponse> findAllCartDetailByIdCartOnline(Integer idCart) {
        return cartDetailRepo.findAllCartDetailOnlineResponse(idCart);
    }

    public List<CartDetailOnlineResponse> findAllCartDetailByIdCartOnline2(Integer idCart, List<Integer> ids) {
        return cartDetailRepo.findAllCartDetailOnlineResponse2(idCart, ids);
    }

    public List<CartDetailOnlineResponse> findAllCartDetailByIdCartOnline3(Integer idCart, Integer ids) {
        return cartDetailRepo.findAllCartDetailOnlineResponse3(idCart, ids);
    }

    public CartKoDangNhapRes getCartKoDangNhapByIdAndQuantity(Integer ids, Integer quantity) {
        List<Object[]> resultList = cartDetailRepo.findAllCartKoDangNhapByIdAndQuantity(ids, quantity);
        CartKoDangNhapRes item = new CartKoDangNhapRes();
        for (Object[] row : resultList) {
            item.setIdProductDetail((Integer) row[0]);
            item.setImg((String) row[1]);
            item.setName((String) row[2]);
            item.setNameColor((String) row[3]);
            item.setNameSize((String) row[4]);
            item.setQuantityStock((Integer) row[5]);
            item.setPriceProduct((Double) row[6]);
            item.setPricePromotion((Integer) row[7]);
            item.setTotalPrice((Double) row[8]);
            item.setQuantityBuy((Integer) row[9]);
            item.setWeightProduct((Double) row[10]);
            item.setIdPromotion((Integer) row[11]);
            item.setStatusProduct((Integer) row[12]);
            item.setStatusProductDetail((Integer) row[13]);
        }
        return item;
    }


    public CartDetail addNewCartDetail(Integer quantity, ProductDetail productDetail, Cart cart, Double gia, boolean isPromotion) {
        CartDetail cartDetail = new CartDetail();
        cartDetail.setQuantity(quantity);
        cartDetail.setProductDetail(productDetail);
        cartDetail.setCart(cart);
        cartDetail.setPrice(gia);
        cartDetail.setTotal(gia * quantity);
        cartDetail.setHavePromotion(isPromotion);
        return cartDetailRepo.save(cartDetail);
    }

    public CartDetail updateQuantityProductExist(Integer quantity, ProductDetail productDetail, Cart cart, Double gia, boolean isPromotion) {
        CartDetail cartDetail = cartDetailRepo.findByCart_IdAndProductDetail_Id(cart.getId(), productDetail.getId());
        int soLuongCu = cartDetail.getQuantity();
        int soLuongMoi = (soLuongCu + quantity);
        cartDetail.setQuantity(soLuongMoi);
        cartDetail.setPrice(gia);
        cartDetail.setTotal(gia * soLuongMoi);
        cartDetail.setHavePromotion(isPromotion);
        return cartDetailRepo.save(cartDetail);
    }

    public CartDetail updateQuantityProductExist1(Integer quantity, ProductDetail productDetail, Cart cart, Double gia, boolean isPromotion) {
        CartDetail cartDetail = cartDetailRepo.findByCart_IdAndProductDetail_Id(cart.getId(), productDetail.getId());
        cartDetail.setQuantity(quantity);
        cartDetail.setPrice(gia);
        cartDetail.setTotal(gia * quantity);
        cartDetail.setHavePromotion(isPromotion);
        return cartDetailRepo.save(cartDetail);
    }

    public CartDetail updateQuantityProductInCart(Integer quantity, CartDetail cartDetail, Double gia, boolean havePromotion) {
        cartDetail.setQuantity(quantity);
        cartDetail.setPrice(gia);
        cartDetail.setTotal(gia * quantity);
        cartDetail.setHavePromotion(havePromotion);
        return cartDetailRepo.save(cartDetail);
    }

    public Integer getQuantityCart_IdAndProductDetail_Id(Integer idCart, Integer idProductDetail) {
        return cartDetailRepo.getQuantityCart_IdAndProductDetail_Id(idCart, idProductDetail) == null ? 0 : cartDetailRepo.getQuantityCart_IdAndProductDetail_Id(idCart, idProductDetail);
    }

//    public void deleteCartDetail(int idCart, int idProductDetail) {
//        CartDetail cartDetail = cartDetailRepo.findByCart_IdAndProductDetail_Id(idCart, idProductDetail);
//        cartDetailRepo.delete(cartDetail);
//    }

    public void checkoutSuccess(Integer idCustomer, Double total, List<Integer> idPCartDetail,
                                Integer idVoucher, Integer idAddress, Double shippingFee, int idPaymentType, int satatus, Integer tienGiam, boolean hinhThucTT) throws MessagingException {

        Bill bill = new Bill();

        Address address = addressRepo.findById(idAddress).get();

        Address address1 = new Address();
        address1.setNameRecipient(address.getNameRecipient());
        address1.setPhone(address.getPhone());
        address1.setProvince(address.getProvince());
        address1.setDistrict(address.getDistrict());
        address1.setWard(address.getWard());
        address1.setAddreseDetail(address.getAddreseDetail());
        address1.setEmail(address.getEmail());
        address1.setDateCreate(new Date());
        address1.setDefault(true);
        addressRepo.save(address1);

        PaymentMethods paymentMethods = new PaymentMethods();
        paymentMethods.setId(idPaymentType);
        Customer customer = customerRepo.findById(idCustomer).get();


        if (idVoucher != null) {
            Voucher voucher = voucherRepo.findById(idVoucher).get();
            bill.setVoucher(voucher);
            Voucher voucher1 = voucherRepo.findById(idVoucher).get();
            int soLuongDaDung = voucher1.getQuantityUsed();
            voucher1.setQuantityUsed(soLuongDaDung + 1);
            voucherRepo.save(voucher1);
        }

        //save bill
        bill.setShipPrice(shippingFee);
        bill.setCustomer(customer);
        bill.setTotalPrice(total);
        bill.setDateCreate(new Date());
        bill.setAddress(address1);
        bill.setStatus(satatus);
        bill.setPaymentType(true);
        bill.setTienGiam(tienGiam);
        bill.setPaymentMethods(paymentMethods);
        billRepo.save(bill);


        //save billDetail
        List<CartDetail> cartDetails = cartDetailRepo.findAllByIdIn(idPCartDetail);
        for (CartDetail cartDetail : cartDetails) {
            ProductDetail productDetail = productDetailRepo.findById(cartDetail.getProductDetail().getId()).get();
            BillDetail billDetail = new BillDetail();
            billDetail.setOldPrice(productDetail.getPrice());
            billDetail.setBill(bill);
            billDetail.setProductDetail(cartDetail.getProductDetail());
            billDetail.setQuantity(cartDetail.getQuantity());
            billDetail.setPrice(cartDetail.getPrice());
            billDetail.setIntoMoney(cartDetail.getTotal());
            billDetail.setStatus(true);
            Promotion promotion = productDetail.getPromotionDetail() != null ? productDetail.getPromotionDetail().getPromotion() : null;
            if (promotion != null && promotion.getStatus().contains("Đang") && promotion.isCondition()) {
                billDetail.setPromotionDetail(productDetail.getPromotionDetail());
            }

            billDetailRepo.save(billDetail);
            mailService.sendEmailOnline(customer.getEmail(), customer.getName(), bill, cartDetails, LocalDate.now(), "mailOnlinePay");
        }

        if (!hinhThucTT) {
            // Xóa sl spct
            for (CartDetail cartDetail : cartDetails) {
                ProductDetail productDetail = productDetailRepo.findById(cartDetail.getProductDetail().getId()).get();
                productDetail.setQuantity(productDetail.getQuantity() - cartDetail.getQuantity());
                productDetailRepo.save(productDetail);
            }
        }

//        // Xóa sl promotion
//        for (CartDetail cartDetail : cartDetails) {
//            if (cartDetail.isHavePromotion()) {
//                Integer quantityInPromotion = cartDetailRepo.getQuantityInPromotion(cartDetail.getProductDetail().getId());
//                if (quantityInPromotion != null) {
//                    Integer quantityInPromotion1 = cartDetailRepo.getQuantityInPromotion1(cartDetail.getProductDetail().getId());
//                    Promotion promotion = promotionRepo.findById(quantityInPromotion1).get();
//                    promotion.setQuantity(promotion.getQuantity() - 1);
//                    promotion.setQuantityUse(promotion.getQuantityUse() + 1);
//                    promotionRepo.save(promotion);
//                }
//            }
//        }


        // Xóa cartDetail
        for (Integer id : idPCartDetail) {
            CartDetail cartDetail = cartDetailRepo.findById(id).get();
            cartDetailRepo.delete(cartDetail);
        }

        //tao bill history

        BillHistory billHistory = new BillHistory();
        billHistory.setBill(bill);
        billHistory.setStatusOld(-2);
        billHistory.setStatus(0);
        billHistory.setDateUpdate(new Date());
        billHistory.setDescription("Đặt hàng lúc " + LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        System.out.println("billHistory: " + bill);
        billHistoryRepo.save(billHistory);


    }

    public void checkoutSuccessWithoutLogin(Double totalCart,
                                            List<Integer> idSanPhamChiTiet,
                                            List<Integer> quantityBuy,
                                            Integer idVoucher,
                                            Double shippingFee,
                                            Integer tienGiam,
                                            String nameRecipient,
                                            String phone,
                                            String provinceName,
                                            String districtName,
                                            String wardName,
                                            String addressDetail,
                                            String email,
                                            int idPaymentType,
                                            int satatus,
                                            boolean hinhThucTT) throws MessagingException {
        Address address = new Address();
        address.setNameRecipient(nameRecipient);
        address.setPhone(phone);
        address.setProvince(provinceName);
        address.setDistrict(districtName);
        address.setWard(wardName);
        address.setAddreseDetail(addressDetail);
        address.setEmail(email);
        address.setDateCreate(new Date());
        addressRepo.save(address);

        Bill bill = new Bill();
        PaymentMethods paymentMethods = new PaymentMethods();
        paymentMethods.setId(idPaymentType);
        if (idVoucher != null) {
            Voucher voucher = voucherRepo.findById(idVoucher).get();
            bill.setVoucher(voucher);
            Voucher voucher1 = voucherRepo.findById(idVoucher).get();
            int soLuongDaDung = voucher1.getQuantityUsed();
            voucher1.setQuantityUsed(soLuongDaDung + 1);
            voucherRepo.save(voucher1);
        }

        bill.setShipPrice(shippingFee);
        bill.setTotalPrice(totalCart);
        bill.setDateCreate(new Date());
        bill.setAddress(address);
        bill.setStatus(satatus);
        bill.setPaymentType(true);
        bill.setTienGiam(tienGiam);
        bill.setPaymentMethods(paymentMethods);
        billRepo.save(bill);
        List<BillDetail> billDetails = new ArrayList<>();
        for (int i = 0; i < idSanPhamChiTiet.size(); i++) {

            ProductDetail productDetail = productDetailRepo.findById(idSanPhamChiTiet.get(i)).get();
            Promotion promotion = productDetail.getPromotionDetail() != null ? productDetail.getPromotionDetail().getPromotion() : null;
            BillDetail billDetail = new BillDetail();
            billDetail.setOldPrice(productDetail.getPrice());
            billDetail.setBill(bill);
            billDetail.setProductDetail(productDetail);
            billDetail.setQuantity(quantityBuy.get(i));
            if (promotion != null && promotion.getStatus().contains("Đang") && promotion.isCondition()) {
                billDetail.setPrice((double) productDetail.getPromotionDetail().getGiaMoi());
                billDetail.setIntoMoney((double) (productDetail.getPromotionDetail().getGiaMoi() * quantityBuy.get(i)));
                billDetail.setPromotionDetail(productDetail.getPromotionDetail());
            } else {
                billDetail.setPrice(productDetail.getPrice());
                billDetail.setIntoMoney(productDetail.getPrice() * quantityBuy.get(i));
            }
            billDetail.setStatus(true);
            billDetailRepo.save(billDetail);

            billDetails.add(billDetail);
            if (!hinhThucTT) {
                productDetail.setQuantity(productDetail.getQuantity() - quantityBuy.get(i));
                productDetailRepo.save(productDetail);
            }
        }
        mailService.sendEmailOnlineWithoutLogin(email, nameRecipient, bill, billDetails, LocalDate.now(), "mailOnlineWithoutLogin");

        BillHistory billHistory = new BillHistory();
        billHistory.setBill(bill);
        billHistory.setStatusOld(-2);
        billHistory.setStatus(0);
        billHistory.setDateUpdate(new Date());
        billHistory.setDescription("Đặt hàng lúc " + LocalDate.now().format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        billHistoryRepo.save(billHistory);
    }
}
