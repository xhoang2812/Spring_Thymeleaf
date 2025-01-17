package com.poly.du_an_tot_nghiep_f6.service;

import com.poly.du_an_tot_nghiep_f6.entity.*;
import com.poly.du_an_tot_nghiep_f6.repository.*;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReturnProductService {
    private final TradeProductRepo tradeProductRepository;
    private final ProductDetailRepo productDetailRepository;
    private final BillDetailRepo billDetailRepository;
    private final TradeProductItemRepo tradeProductItemRepository;
    private final TradeRepo tradeRepository;
    private final BillRepo billRepository;
    private final ErrorProductDetailRepo errorProductDetailRepo;
    private final MailService mailService;
    private final EmployeeRepo employeeRepo;
    private final BillHistoryRepo billHistoryRepository;
    private final ErrorListRepo errorListRepo;

    public List<BillDetail> getBillDetailById(Integer id){
        return billDetailRepository.findByBillId(id);
    }

    public TradeProduct checkQuantityTrade(Integer idTradeProduct, Integer quantityTrade){
        TradeProduct tradeProduct = tradeProductRepository.findById(idTradeProduct)
                .orElseThrow(() -> new RuntimeException("Trade Product not found"));
        if(tradeProduct.getBillDetail().getQuantity() >= quantityTrade){
            tradeProduct.setQuantity(quantityTrade);
            tradeProduct.setTotalMoney((int) (tradeProduct.getBillDetail().getPrice() * tradeProduct.getQuantity()));
            Trade trade = tradeProduct.getTrade();
            loadTrade(trade);
            List<ErrorProductDetail> errorProductDetails = errorProductDetailRepo.findByTrade(trade);
            if(!errorProductDetails.isEmpty()){
                for(ErrorProductDetail errorProductDetail : errorProductDetails){
                    if(tradeProduct.getBillDetail().getProductDetail().equals(errorProductDetail.getProductDetail())){
                        errorProductDetail.setQuantity(Math.min(tradeProduct.getQuantity(), errorProductDetail.getQuantity()));
                        errorProductDetailRepo.save(errorProductDetail);
                    }
                }
            }
            return tradeProductRepository.save(tradeProduct);
        }
        return null;
    }

    public TradeProduct createTradeProduct(Integer idBillDetail,Integer quantity) {
        BillDetail billDetail = billDetailRepository.getReferenceById(idBillDetail);
        Bill bill = billDetail.getBill();

        Trade trade = tradeRepository.findByBill(bill);

        TradeProduct newTradeProduct = new TradeProduct();
        List<TradeProduct> list = tradeProductRepository.findByTrade(trade);

        boolean check = false;
        for(TradeProduct tradeProduct : list){
            if(tradeProduct.getBillDetail().equals(billDetail)){
                tradeProduct.setQuantity(tradeProduct.getQuantity()+ quantity);
                tradeProduct.setTotalMoney((int) (tradeProduct.getQuantity() * billDetail.getPrice()));

                createTradeProductItems(quantity, tradeProduct);
                BeanUtils.copyProperties(tradeProduct, newTradeProduct);
                check = true;
                break;
            }
        }
        if(!check){
            newTradeProduct.setBillDetail(billDetail);
            newTradeProduct.setQuantity(quantity);
            newTradeProduct.setTotalMoney((int) (newTradeProduct.getQuantity() * billDetail.getPrice()));
            newTradeProduct.setTrade(trade);
            newTradeProduct.setDescription("");
            newTradeProduct.setReason("");
            TradeProduct saveTradeProduct = tradeProductRepository.save(newTradeProduct);
            createTradeProductItems(quantity, saveTradeProduct);
        }
        loadTrade(trade);
        return newTradeProduct;
    }

    public void createTradeProductItems(Integer quantity, TradeProduct tradeProduct) {
        List<TradeProductItem> tradeProductItems = new ArrayList<>();
        for(int i =0; i < quantity ; i++){
            TradeProductItem tradeProductItem = new TradeProductItem();
            tradeProductItem.setTradeProduct(tradeProduct);
            tradeProductItem.setDescription(errorListRepo.findAll().get(0).getErrorName());
            tradeProductItem.setError(true);
            tradeProductItems.add(tradeProductItemRepository.save(tradeProductItem));
        }
        tradeProduct.getTradeProductItems().addAll(tradeProductItems);

        tradeProductRepository.save(tradeProduct);
    }


//    public TradeProduct updateStatusTradeProduct(Integer tradeProductId,TradeProductItem tradeProductItem, boolean status){
//        TradeProduct existingTradeProduct = findTradeProductById(tradeProductId);
//        existingTradeProduct.setStatus(status);
//
//        if(status){
//            addProductDetailToTradeProduct(tradeProductItem, tradeProductId);
//            tradeProductRepository.save(existingTradeProduct);
//        }else{
//            existingTradeProduct.setTradeProductItems(new ArrayList<>());
//            tradeProductRepository.save(existingTradeProduct);
//        }
//        Trade trade = existingTradeProduct.getTrade();
//        loadTrade(trade);
//        return existingTradeProduct;
//    }
//
//    public void addProductDetailToTradeProduct(TradeProductItem tradeProductItem, Integer tradeProductId){
//        TradeProduct tradeProduct = findTradeProductById(tradeProductId);
//        ProductDetail productDetail = productDetailRepository.findById(tradeProductItem.getProductDetail().getId())
//                .orElseThrow(() -> new RuntimeException("Product detail not found"));
//
//        if(tradeProduct.getTradeProductItems().isEmpty()){
//            addTradeProductItem(tradeProductItem, tradeProduct, productDetail);
//        }else {
//            Optional<TradeProductItem> existingItem = tradeProduct.getTradeProductItems().stream()
//                    .filter(item -> item.getProductDetail().getId().equals(productDetail.getId()))
//                    .findFirst();
//            if (existingItem.isPresent()) {
//                TradeProductItem trade = existingItem.get();
//                trade.setQuantity(trade.getQuantity() + 1);
//                trade.setTotalMoney((int) (trade.getQuantity() * productDetail.getPrice()));
//                tradeProductItemRepository.save(trade);
//            } else {
//                addTradeProductItem(tradeProductItem, tradeProduct, productDetail);
//            }
//        }
//    }
//
//    public void addTradeProductItem(TradeProductItem tradeProductItem, TradeProduct tradeProduct, ProductDetail productDetail) {
//        tradeProductItem.setProductDetail(productDetail);
//        tradeProductItem.setTradeProduct(tradeProduct);
//        tradeProductItem.setQuantity(1);
//        tradeProductItem.setTotalMoney( (int) (tradeProductItem.getQuantity() * productDetail.getPrice()));
//        TradeProductItem saveTradeProductItem = tradeProductItemRepository.save(tradeProductItem);
//        tradeProduct.getTradeProductItems().add(saveTradeProductItem);
//        tradeProduct.setStatus(true);
//        tradeProductRepository.save(tradeProduct);
//    }

    public void deleteTradeProduct(Integer idTradeProduct){
        TradeProduct tradeProduct = tradeProductRepository.findById(idTradeProduct)
                .orElseThrow(() -> new RuntimeException("TradeProduct not found"));
        List<TradeProductItem> tradeProductItems = tradeProduct.getTradeProductItems();
        tradeProduct.getTradeProductItems().removeAll(tradeProductItems);
        tradeProductRepository.save(tradeProduct);
        tradeProductItemRepository.deleteAll(tradeProductItemRepository.findByTradeProduct(tradeProduct));
        tradeProductRepository.delete(tradeProduct);
        loadTrade(tradeProduct.getTrade());
    }

    public void deleteProductDetailFromTradeProduct(Integer idTradeProductItem){
        TradeProductItem tradeProductItem = tradeProductItemRepository.findById(idTradeProductItem)
                .orElseThrow(() -> new RuntimeException("Trade product item not found"));
        TradeProduct  existingTradeProduct = tradeProductItem.getTradeProduct();
        existingTradeProduct.getTradeProductItems().remove(tradeProductItem);
        tradeProductRepository.save(existingTradeProduct);
        tradeProductItemRepository.delete(tradeProductItem);
        Trade trade = existingTradeProduct.getTrade();
        loadTrade(trade);
    }

    public TradeProduct findTradeProductById(Integer tradeProductId){
        return tradeProductRepository.findById(tradeProductId)
                .orElseThrow(() -> new RuntimeException("Trade product not found"));
    }
    public void loadTrade(Trade trade){
        Bill bill = trade.getBill();

        int oldVoucher = bill.getTienGiam();
        int newVoucher = 0;

        int returnMoney = 0;
        for(TradeProduct tradeProduct : tradeProductRepository.findByTrade(trade)){
            returnMoney += tradeProduct.getTotalMoney();
        }

        int currentBillMoney = trade.getOldProductDetailMoney() - returnMoney;

        if(bill.getVoucher() != null){
            Voucher voucher = bill.getVoucher();
            if(currentBillMoney >= voucher.getMinimumOrder()){
                if(voucher.getDiscount() <= 100){
                    newVoucher = currentBillMoney/100 * voucher.getDiscount();
                    if(newVoucher > voucher.getMaximumReduction()){
                        newVoucher = voucher.getMaximumReduction();
                    }
                    trade.setPayMoney(returnMoney - (oldVoucher - newVoucher));
                }else {
                    if(currentBillMoney > voucher.getDiscount()){
                        newVoucher = voucher.getDiscount();
                        if(newVoucher > voucher.getMaximumReduction()){
                            newVoucher = voucher.getMaximumReduction();
                        }
                        trade.setPayMoney(returnMoney - (oldVoucher - newVoucher));
                    }else {
                        newVoucher = currentBillMoney;
                        trade.setPayMoney(returnMoney);
                    }
                }
            }else {
                trade.setPayMoney(returnMoney - (oldVoucher - newVoucher));
            }
        }else {
            trade.setPayMoney(returnMoney);
        }
        trade.setBackProductDetailMoney(returnMoney);
        trade.setOldVoucherMoney(oldVoucher);
        trade.setNewVoucherMoney(newVoucher);
        tradeRepository.save(trade);
    }


    public void confirmReturnProduct(Trade tradeRequest,List<TradeProductItem> tradeProductItems, Integer idBill, Employee currentEmployee) throws MessagingException {
        List<TradeProduct> tradeProductList = new ArrayList<>();

        Bill bill = billRepository.getReferenceById(idBill);
        Trade trade = tradeRepository.findByBill(bill);
        List<TradeProduct> tradeProducts = tradeProductRepository.findByTrade(trade);
        List<ErrorProductDetail> errorProductDetails = errorProductDetailRepo.findByTrade(trade);
//        trade.setResponseDate(new Date());
        trade.setNameBank(tradeRequest.getNameBank());
        trade.setBankInfo(tradeRequest.getBankInfo());
        trade.setUserInfo(tradeRequest.getUserInfo());
        trade.setQrInfo(tradeRequest.getQrInfo());
        trade.setEmail(tradeRequest.getEmail());
        trade.setPhoneInfo(tradeRequest.getPhoneInfo());
        for(TradeProduct tradeProduct : tradeProducts){
            BillDetail billDetail = tradeProduct.getBillDetail();
            billDetail.setDescription("Trả " + tradeProduct.getQuantity() + " sản phẩm");
            billDetailRepository.save(billDetail);
        }

//        for(TradeProduct tradeProduct : tradeProducts){
//            for(ErrorProductDetail errorProductDetail : errorProductDetails){
//                if(tradeProduct.getBillDetail().getProductDetail().equals(errorProductDetail.getProductDetail())){
//                    tradeProduct.setDescription("Lỗi " + errorProductDetail.getQuantity() + " sản phẩm");
//                    ProductDetail productDetail = errorProductDetail.getProductDetail();
//                    productDetail.setQuantity(productDetail.getQuantity() + tradeProduct.getQuantity() - errorProductDetail.getQuantity());
//                    tradeProductList.add(tradeProduct);
//                    break;
//                }
//            }
//        }
//        tradeProducts.removeAll(tradeProductList);
//        for(TradeProduct tradeProduct : tradeProducts){
//            ProductDetail productDetail = tradeProduct.getBillDetail().getProductDetail();
//            productDetail.setQuantity(productDetail.getQuantity() + tradeProduct.getQuantity());
//            productDetailRepository.save(productDetail);
//        }

        for(TradeProductItem tradeProductItem : tradeProductItems){
            TradeProductItem existingTradeProductItem = tradeProductItemRepository.getReferenceById(tradeProductItem.getId());
            existingTradeProductItem.setCreateDate(new Date());
            existingTradeProductItem.setError(tradeProductItem.isError());
            existingTradeProductItem.setDescription(tradeProductItem.getDescription());
            tradeProductItemRepository.save(existingTradeProductItem);
        }

//        bill.setTotalPrice(bill.getTotalPrice() - trade.getPayMoney());
        bill.setStatus(7);
        billRepository.save(bill);
        tradeRepository.save(trade);
        BillHistory billHistory = new BillHistory();
        billHistory.setBill(bill);
        billHistory.setStatus(7);
        billHistory.setStatusOld(5);
        billHistory.setEmployee(currentEmployee);
        billHistory.setDescription("Xác nhận trả hàng");
        billHistory.setDateUpdate(new Date());
        billHistoryRepository.save(billHistory);

        List<Employee> employees = employeeRepo.findAll().stream()
                .filter(employee -> employee.getPosition().equals("Admin")).toList();
        for(Employee employee : employees){
            mailService.mailThongBaoHoanTien(employee.getEmail(), bill, trade, "mailThongBaoHoanTien");
        }
    }

}
