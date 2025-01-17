package com.poly.du_an_tot_nghiep_f6.service.impl;

import com.poly.du_an_tot_nghiep_f6.entity.*;
import com.poly.du_an_tot_nghiep_f6.repository.BillDetailRepo;
import com.poly.du_an_tot_nghiep_f6.response.CartDetailResponse;
import com.poly.du_an_tot_nghiep_f6.service.ICartDetailInCounterService;
import com.poly.du_an_tot_nghiep_f6.service.PromotionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class CartDetailInCounterServiceImpl implements ICartDetailInCounterService {
    private ArrayList<CartDetailInCounter> cartDetailInCounters = new ArrayList<>();

    @Autowired
    ProductDetailServiceImpl productDetailService;

    @Autowired
    PromotionService promotionService;

    private int cartDetailCount = 1;
    @Autowired
    private BillDetailRepo billDetailRepo;

    @Override
    public List<CartDetailInCounter> getCartDetails(int idCart) {
        if (!cartDetailInCounters.isEmpty()) {
            return cartDetailInCounters.stream()
                    .filter(c -> c.getCartInCounter().getId() == idCart)
                    .collect(Collectors.toList());
        } else {
            return null;
        }
    }

    public List<CartDetailInCounter> getAll() {
        return cartDetailInCounters;
    }

    @Override
    public CartDetailInCounter getCartDetail(int idCartDetail) {
        if (cartDetailInCounters.isEmpty()) {
            return null;
        } else {
            for (CartDetailInCounter cartDetailInCounter : cartDetailInCounters) {
                if (cartDetailInCounter.getId() == idCartDetail) {
                    return cartDetailInCounter;
                }
            }
        }
        return null;
    }

    @Override
    public List<CartDetailResponse> getCartDetailResponses(int idCart) {
        ArrayList<CartDetailInCounter> cartDetailInCounters = (ArrayList<CartDetailInCounter>) getCartDetails(idCart);
        if (cartDetailInCounters != null) {
            return parseCartDetailResponse(cartDetailInCounters);
        } else {
            return null;
        }
    }

    @Override
    public void addCartDetail(CartDetailInCounter cartDetailInCounter, boolean status) {
        for (CartDetailInCounter cartDetailInCounter1 : cartDetailInCounters) {
            if (cartDetailInCounter.getProductDetail().getId() == cartDetailInCounter1.getProductDetail().getId() &&
                cartDetailInCounter.getCartInCounter().getId() == cartDetailInCounter1.getCartInCounter().getId()) {// tìm kiếm sản phẩm trong giỏ
                if (cartDetailInCounter1.getPromotionDetail() != null) {// khuyễn mãi không null
                    if (cartDetailInCounter1.getPromotionDetail().getPromotion().getDateEnd().isAfter(LocalDateTime.now())
                            && cartDetailInCounter1.getPromotionDetail().getPromotion().isCondition()) {// nếu khuyến mãi còn hoạt động
                        cartDetailInCounter1.setQuantity(cartDetailInCounter1.getQuantity() + cartDetailInCounter.getQuantity());
                        return;
                    }
                } else {
                    cartDetailInCounter1.setQuantity(cartDetailInCounter1.getQuantity() + cartDetailInCounter.getQuantity());
                    return;
                }
            }
        }
        if (cartDetailInCounter.getProductDetail().getPromotionDetail() != null &&
            cartDetailInCounter.getProductDetail().getPromotionDetail().getPromotion().getStatus().contains("Đang")) {
            if (status) {
                cartDetailInCounter.setPromotionDetail(cartDetailInCounter.getPromotionDetail());
            } else {
                cartDetailInCounter.setPromotionDetail(cartDetailInCounter.getProductDetail().getPromotionDetail());
            }
        }
        cartDetailInCounter.setId(cartDetailCount);
        cartDetailCount++;
        cartDetailInCounters.add(cartDetailInCounter);
        System.out.println("Thêm sản phẩm " + cartDetailInCounter.getProductDetail().getId() + " vào giỏ " + cartDetailInCounter.getId());
    }

    @Override
    public void updateCartDetail(int idCardDetail, int quantity) {
        getCartDetail(idCardDetail).setQuantity(quantity);
    }

    @Override
    public void deleteCartDetail(int idCartDetail, boolean status) {
        CartDetailInCounter cartDetailInCounter = getCartDetail(idCartDetail);
        ProductDetail productDetail = cartDetailInCounter.getProductDetail();
        if (status) {
            productDetailService.changerQuantity(productDetail.getId(), cartDetailInCounter.getQuantity(), true);
            cartDetailInCounters.remove(cartDetailInCounter);
        }
        System.out.println("Xóa cartdetail: " + idCartDetail);
    }

    @Override
    public void deleteAllCartDetail(CartInCounter cartInCounter, boolean status) {
        if (cartInCounter.getBillEdit() == null) { // Nếu không phải hóa đơn sửa thì xóa bình thường
            List<CartDetailInCounter> list = cartDetailInCounters.stream()
                    .filter(cartDetail -> cartDetail.getCartInCounter().getId() == cartInCounter.getId())
                    .toList();
            for (CartDetailInCounter cartDetailInCounter : list) {
                deleteCartDetail(cartDetailInCounter.getId(), status);
            }
        } else { // Nếu là hóa đơn sửa thì xử lý logic
            // Lấy danh sách BillDetail từ hóa đơn sửa
            if (status) {
                // true là chỉnh số lượng, false bỏ qua
                List<BillDetail> billDetails = billDetailRepo.findByBillId(cartInCounter.getBillEdit().getId());
                List<CartDetailInCounter> cartDetails = getCartDetails(cartInCounter.getId());

                //Các sản phẩm có ở cả giỏ và bill
                for (BillDetail billDetail : billDetails) {// Duyểt các sp trong bill
                    int i = 0;
                    int size = cartDetails.size();
                    for (CartDetailInCounter cartDetailInCounter : cartDetails) {//Duyệt các sản phẩm trong giỏ
                        i++;
                        if (billDetail.getProductDetail().getId() == cartDetailInCounter.getProductDetail().getId()) {// Tìm sản phẩm trùng nhau
                            int quantityCharge = Math.abs(cartDetailInCounter.getQuantity() - billDetail.getQuantity());// Lấy số lượng chênh lệch
                            System.out.println("Số lương chênh lệch : " + quantityCharge);
                            if (quantityCharge > 0) {//Sl chênh lệch lớn hơn 0 thì mới xử lý thay đổi số lượng
                                productDetailService.changerQuantity(
                                        billDetail.getProductDetail().getId(),
                                        quantityCharge,
                                        cartDetailInCounter.getQuantity() > billDetail.getQuantity()
                                        // true tăng, false giảm => Sl trong GIỎ lớn sẽ lơn sẽ tăng lại trong kho và ngược lại.
                                );
                            }
                            cartDetails.remove(cartDetailInCounter);
                            cartDetailInCounters.remove(cartDetailInCounter);
                            break;
                        } else {
                            if (i == size) {
                                System.out.println("Không có sản phẩm trong giỏ");
                                productDetailService.changerQuantity(
                                        billDetail.getProductDetail().getId(),
                                        billDetail.getQuantity(),
                                        false
                                        // true tăng, false giảm => Sl trong GIỎ lớn sẽ lơn sẽ tăng lại trong kho và ngược lại.
                                );
                                cartDetails.remove(cartDetailInCounter);
                                cartDetailInCounters.remove(cartDetailInCounter);
                            }
                        }
                    }
                }
                for (CartDetailInCounter cartDetailInCounter : cartDetails) {
                                productDetailService.changerQuantity(
                                        cartDetailInCounter.getProductDetail().getId(),
                                        cartDetailInCounter.getQuantity(),
                                        true
                                        // true tăng, false giảm => Sl trong GIỎ lớn sẽ lơn sẽ tăng lại trong kho và ngược lại.
                                );
                }
            }
        }
    }


    @Override
    public List<BillDetail> getBillDetail(int idCart, Bill bill) {
        List<BillDetail> billDetails = new ArrayList<>();
        for (CartDetailInCounter cartDetailInCounter : getCartDetails(idCart)) {
            billDetails.add(new BillDetail(
                    0,
                    bill,
                    cartDetailInCounter.getProductDetail(),
                    cartDetailInCounter.getQuantity(),
                    cartDetailInCounter.getPrice(),
                    cartDetailInCounter.getQuantity() *
                    cartDetailInCounter.getPrice(),
                    cartDetailInCounter.getPriceOld(),
                    cartDetailInCounter.getPromotionDetail(),
                    null,
                    true
            ));
        }
        return billDetails;
    }

    public double getTotalPrice(int idCart) {
        double totalPrice = 0;
        for (CartDetailInCounter cartDetailInCounter : cartDetailInCounters) {
            if (cartDetailInCounter.getCartInCounter().getId() == idCart) {
                totalPrice += (cartDetailInCounter.getPrice() * cartDetailInCounter.getQuantity());
            }
        }
        return totalPrice;
    }

    @Override
    public List<CartDetailResponse> parseCartDetailResponse(ArrayList<CartDetailInCounter> cartDetailInCounters) {
        ArrayList<CartDetailResponse> cartDetailResponses = new ArrayList<>();
        for (CartDetailInCounter cartDetailInCounter : cartDetailInCounters) {
            String namePromotion = null;
            if (cartDetailInCounter.getPromotionDetail() != null) {
                namePromotion = cartDetailInCounter.getPromotionDetail().getPromotion().getName();
            }
            CartDetailResponse cartDetailResponse = new CartDetailResponse(
                    cartDetailInCounter.getId(),
                    cartDetailInCounter.getProductDetail().getProduct().getName() + " - " + cartDetailInCounter.getProductDetail().getSize().getName() + " - " + cartDetailInCounter.getProductDetail().getColor().getName(),
                    cartDetailInCounter.getPrice(),
                    cartDetailInCounter.getPriceOld(),
                    cartDetailInCounter.getQuantity(),
                    namePromotion,
                    cartDetailInCounter.getIsEditQuantity()
            );
            cartDetailResponses.add(cartDetailResponse);
        }
        return cartDetailResponses;
    }


}
