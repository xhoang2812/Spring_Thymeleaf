package com.poly.du_an_tot_nghiep_f6.service.impl;

import com.poly.du_an_tot_nghiep_f6.common.Constans;
import com.poly.du_an_tot_nghiep_f6.controller.sale.SaleControllerAPI;
import com.poly.du_an_tot_nghiep_f6.entity.*;
import com.poly.du_an_tot_nghiep_f6.repository.BillDetailRepo;
import com.poly.du_an_tot_nghiep_f6.repository.VoucherRepo;
import com.poly.du_an_tot_nghiep_f6.service.CustomerService;
import com.poly.du_an_tot_nghiep_f6.service.ICartInCounterService;
import com.poly.du_an_tot_nghiep_f6.service.VoucherService;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@Service
public class CartInCounterServiceImpl implements ICartInCounterService {
    ArrayList<CartInCounter> cartInCounters = new ArrayList<>();

    @Autowired
    VoucherRepo voucherRepo;

    @Autowired
    CustomerService customerService;

    @Autowired
    private CartDetailInCounterServiceImpl cartDetailServiceImpl;

    @Getter
    private int cartCount = 1;
    @Autowired
    private BillServiceImpl billServiceImpl;
    @Autowired
    private BillDetailRepo billDetailRepo;
    @Autowired
    private VoucherService voucherService;

    public List<CartInCounter> getCartInCounters() {
        return cartInCounters;
    }

    @Override
    public CartInCounter getCart(int idCart) {
        for (CartInCounter cartInCounter : cartInCounters) {
            if (cartInCounter.getId() == idCart) {
                return cartInCounter;
            }
        }
        System.out.println("Không có giỏ hàng");
        return null;
    }

    @Override
    public CartInCounter createCart() {
        if (cartInCounters.size() < Constans.MAX_LENGHT_CART) {
            CartInCounter cartInCounter = new CartInCounter(cartCount, null, null, 0.0, null, null, false, null, true, null, null);
            cartInCounters.add(cartInCounter);
            System.out.println("Đã thêm giỏ hàng mới");
            SaleControllerAPI.cartIdSelect = cartCount;
            cartCount++;
            return cartInCounter;
        } else {
            System.out.println("Giỏ hàng đã đạt 20;");
            return null;
        }
    }

    @Override
    public Boolean deleteCart(int idCart, boolean status) {
        CartInCounter cartInCounter = getCart(idCart);
        if (status) {
            Bill bill = cartInCounter.getBillEdit();
            if (bill != null) {
                Integer idCustomerBill = cartInCounter.getBillEdit().getCustomer() != null ? cartInCounter.getBillEdit().getCustomer().getId() : null;
                Integer idVoucherBill = bill.getVoucher() != null ? bill.getVoucher().getId() : null;

                Integer idCustomerCart = cartInCounter.getCustomer() != null ? cartInCounter.getCustomer().getId() : null;
                Integer idVoucherCart = cartInCounter.getVoucher() != null ? cartInCounter.getVoucher().getId() : null;

                System.out.println("Khách hàng hoá đơn: " + idCustomerBill + " - Voucher hoá đơn: " + idVoucherBill);
                System.out.println("Khách hàng giỏ: " + idCustomerCart + " - Voucher giỏ: " + idVoucherCart);
                if (idCustomerCart != idCustomerBill || idVoucherCart != idVoucherBill) {
                    customerService.removeVoucher(idCustomerBill, idVoucherBill);
                    customerService.addVoucher(idCustomerCart, idVoucherCart);
                }
            } else {
                if (cartInCounter.getVoucher() != null) {
                    cartInCounter.getVoucher().setQuantityUsed(cartInCounter.getVoucher().getQuantityUsed() - 1);
                    voucherRepo.save(cartInCounter.getVoucher());
                }
            }
        }
        cartDetailServiceImpl.deleteAllCartDetail(cartInCounter, status);
        cartInCounters.remove(cartInCounter);
        System.out.println("Xóa thành công giỏ hàng");
        return true;
    }

    @Override
    public Bill getBill(int idCart, Employee employee) {
        CartInCounter cartInCounter = getCart(idCart);
        return new Bill(
                0,
                employee,
                cartInCounter.getCustomer(),
                cartDetailServiceImpl.getTotalPrice(idCart),
                cartInCounter.getShipPrice(),
                cartInCounter.getVoucher(),
                cartInCounter.isShip() ? cartInCounter.getAddress() : null,
                new Date(),
                cartInCounter.getDescriptionAddress(),
                cartInCounter.getPaymentMethods(),
                false,
                cartInCounter.isShip() ? 1 : 5,
                getReducedPrice(cartInCounter.getVoucher(), cartDetailServiceImpl.getTotalPrice(idCart)),
                cartInCounter.getDescriptionBill()
        );
    }

    private Integer getReducedPrice(Voucher voucher, Double totalPrice) {
        if (voucher == null) {
            return 0;
        } else {
            if (voucher.isStyleVoucher()) {
                Double reducedPrice0 = voucher.getDiscount() * totalPrice / 100;
                if (reducedPrice0 < voucher.getMaximumReduction()) {
                    return (int) (voucher.getDiscount() * totalPrice / 100);
                } else {
                    return voucher.getMaximumReduction();
                }
            } else {
                if (totalPrice < voucher.getDiscount()) {
                    return totalPrice.intValue();
                } else {
                    return voucher.getDiscount();
                }
            }
        }
    }

    @Override
    public Boolean setCustomer(int idCart, Customer customer) {
        if (customer != null) {
            for (CartInCounter cartInCounter : cartInCounters) {
                if (cartInCounter.getCustomer() != null) {
                    if (Objects.equals(cartInCounter.getCustomer().getId(), customer.getId())) {
                        System.out.println("Khách hàng đã có giỏ hàng");
                        SaleControllerAPI.cartIdSelect = cartInCounter.getId();
                        return false;
                    }
                } else {
                    if (cartInCounter.getBillEdit() != null) {
                        if (cartInCounter.getBillEdit().getCustomer() != null) {
                            if (Objects.equals(cartInCounter.getBillEdit().getCustomer().getId(), customer.getId())
                                && cartInCounter.getId() != idCart) {
                                System.out.println("Khách hàng đã có giỏ hàng");
                                SaleControllerAPI.cartIdSelect = cartInCounter.getId();
                                return false;
                            }
                        }
                    }
                }
            }
            if (!cartInCounters.isEmpty()) {
                CartInCounter cartInCounter = getCart(idCart);
                if (cartInCounter.getVoucher() != null) {
                    customerService.addVoucher(cartInCounter.getCustomer().getId(), cartInCounter.getVoucher().getId());
                    cartInCounter.setVoucher(null);
                }
            }
            getCart(idCart).setCustomer(customer);
        }
        return true;
    }

    public int renderCartToBill(Integer idBill) {
        for (CartInCounter cartInCounter : cartInCounters) {
            if (cartInCounter.getBillEdit() != null) {
                if (cartInCounter.getBillEdit().getId() == idBill) {
                    SaleControllerAPI.cartIdSelect = cartInCounter.getId();
                    return 1;
                }
            }
        }
        List<BillDetail> listProductEdit = billDetailRepo.findAllByBill_Id(idBill);
        Bill billEdit = billServiceImpl.getBill(idBill);

        CartInCounter cartInCounter = createCart();
        if (!setCustomer(cartInCounter.getId(), billEdit.getCustomer())) {
            deleteCart(cartInCounter.getId(), true);
            return -1;
        }
        cartInCounter.setBillEdit(billEdit);
        cartInCounter.setAddress(billEdit.getAddress());
        cartInCounter.setVoucher(billEdit.getVoucher());
        cartInCounter.setShip(billEdit.getShipPrice() > 0);
        cartInCounter.setDescriptionBill(billEdit.getDescriptionBill());
        cartInCounter.setDescriptionAddress(billEdit.getDescriptionShip());

        SaleControllerAPI.cartIdSelect = cartInCounter.getId();
        for (BillDetail billDetail : listProductEdit) {
            cartDetailServiceImpl.addCartDetail(new CartDetailInCounter(
                    0,
                    cartInCounter,
                    billDetail.getProductDetail(),
                    billDetail.getPromotionDetail(),
                    billDetail.getQuantity()
                    ),true
            );
        }
        return 0;
    }

    public boolean deleteVoucher(int idCart) {
        CartInCounter cartInCounter = getCart(idCart);
        Voucher voucher = voucherService.getVoucher(cartInCounter.getVoucher().getId());
        if (voucher != null) {
            voucher.setQuantityUsed(voucher.getQuantityUsed() - 1);
            //Thêm voucher vào list
            Customer customer = cartInCounter.getCustomer();
            if (customer != null) {
                List<Voucher> vouchers = customer.getVouchers();
                if (vouchers != null) {
                    vouchers.removeIf(v -> v.getId().equals(voucher.getId()));
                    vouchers.add(voucher);
                }
                customerService.save(customer);
            }
            voucherRepo.save(voucher);
            cartInCounter.setVoucher(null);
        }
        return true;
    }

    public boolean setVoucher(int idCart, Voucher voucher) {
        try {
            voucher.setQuantityUsed(voucher.getQuantityUsed() + 1);
            voucherRepo.save(voucher);
            CartInCounter cartInCounter = getCart(idCart);
            //Xóa voucher khỏi list
            Customer customer = cartInCounter.getCustomer();
            if (customer != null) {
                List<Voucher> vouchers = customer.getVouchers();
                if (vouchers != null) {
                    vouchers.removeIf(v -> v.getId().equals(voucher.getId()));
                }
                customerService.save(customer);
            }
            cartInCounter.setVoucher(voucher);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}