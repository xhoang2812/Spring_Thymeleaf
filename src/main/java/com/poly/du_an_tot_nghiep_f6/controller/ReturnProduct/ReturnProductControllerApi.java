package com.poly.du_an_tot_nghiep_f6.controller.ReturnProduct;

import com.poly.du_an_tot_nghiep_f6.entity.*;
import com.poly.du_an_tot_nghiep_f6.repository.*;
import com.poly.du_an_tot_nghiep_f6.request.TradeQuest;
import com.poly.du_an_tot_nghiep_f6.response.ErrorProductResponse;
import com.poly.du_an_tot_nghiep_f6.response.PdfAfterReturnResponse;
import com.poly.du_an_tot_nghiep_f6.response.TradeProductItemResponse;
import com.poly.du_an_tot_nghiep_f6.service.MailService;
import com.poly.du_an_tot_nghiep_f6.service.ReturnProductService;
import com.poly.du_an_tot_nghiep_f6.service.VoucherService;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.BeanUtils;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@CrossOrigin("*")
@RequestMapping("/api/returnProduct")
public class ReturnProductControllerApi {
    private final BillRepo billRepository;
    private final BillDetailRepo billDetailRepository;
    private final TradeProductRepo tradeProductRepository;
    private final ReturnProductService returnProductService;
    private final TradeRepo tradeRepository;
    private final ErrorProductDetailRepo errorProductDetailRepo;
    private final ProductDetailRepo productDetailRepo;
    private final CustomerRepo customerRepo;
    private final VoucherRepo voucherRepo;
    private final BillRepo billRepo;
    private final MailService mailService;
    private final VoucherService voucherService;
    private final BillHistoryRepo billHistoryRepo;
    private final ErrorListRepo errorListRepo;
    private final TradeProductItemRepo tradeProductItemRepo;


    @GetMapping
    public List<TradeProduct> getTradeProductsByTrade() {
        return tradeProductRepository.findAll();
    }

    @GetMapping("/{idBill}")
    public Trade getTradeById(@PathVariable Integer idBill, Authentication authentication) {
        return tradeRepository.findByBill(billRepository.getReferenceById(idBill));
//        if(authentication != null){
//            Employee employee = voucherService.currentEmployee(authentication);
//            if(employee.getPosition().equals("Admin")){
//                return tradeRepository.findByBill(billRepository.getReferenceById(idBill));
//            }
//        }
//        return null;
    }

    @GetMapping("/get/{idBill}")
    public Trade getTrade(@PathVariable Integer idBill, Authentication authentication) {
        return tradeRepository.findByBill(billRepository.getReferenceById(idBill));
    }

    @PostMapping("/returnAll/{idBill}")
    public void returnAll(@PathVariable Integer idBill){
        Bill bill = billRepository.getReferenceById(idBill);
        Trade trade = tradeRepository.findByBill(bill);
        List<BillDetail> billDetails = billDetailRepository.findByBillId(bill.getId());
        for(BillDetail billDetail : billDetails){
                TradeProduct tradeProduct = new TradeProduct();
                tradeProduct.setTrade(trade);
                tradeProduct.setBillDetail(billDetail);
                tradeProduct.setQuantity(billDetail.getQuantity());
                tradeProduct.setTotalMoney((int) (billDetail.getPrice() * billDetail.getQuantity()));
                tradeProduct.setDescription("");
                TradeProduct saveTradeProduct = tradeProductRepository.save(tradeProduct);
                returnProductService.createTradeProductItems(billDetail.getQuantity(), saveTradeProduct);
        }
        returnProductService.loadTrade(trade);
    }

    @GetMapping("/bill/{id}")
    public Bill getBillById(@PathVariable String id) {
        try {
            boolean checkBill = billRepository.existsById(Integer.valueOf(id));
            if (checkBill) {
                Bill bill = billRepository.getReferenceById(Integer.valueOf(id));
                Trade existingTrade = tradeRepository.findByBill(bill);

//                String formatDate = new SimpleDateFormat("dd/MM/yyyy").format(bill.getDateCreate());
//                String formatDate1 = new SimpleDateFormat("dd/MM/yyyy").format(new Date());
//                System.out.println(formatDate + "--" + formatDate1);
                List<BillDetail> billDetails = billDetailRepository.findByBillId(bill.getId());
                int min = 0;
                int max = billDetails.size();
                for(BillDetail billDetail : billDetails){
                    if(billDetail.getOldPrice() > billDetail.getPrice()){
                        min += 1;
                    }
                }


                DateTimeFormatter[] formatters = {
                        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS"),
                        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SS"),
                        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.S"),
                };
                LocalDateTime dateCreatePlus7Days = getLocalDateTime(formatters, bill);

                // Lấy thời điểm hiện tại
                LocalDateTime currentDate = LocalDateTime.now();

//                if(bill.getVoucher() != null){
//                    bill.setId(-1); // have voucher => next
//                    return bill;
//                }
                if(bill.getStatus() != 5 && bill.getStatus() != 7){
                    bill.setId(-2); // bill haven't success => next
                    return bill;
                }
//                if(min == max){
//                    bill.setId(-3); // bill full promotion => next
//                    return bill;
//                }
                // So sánh

                if (currentDate.isBefore(dateCreatePlus7Days)) {
                    if (tradeRepository.findByBill(bill) == null) {
                        Trade trade = new Trade();
                        int reducedPrice= bill.getTienGiam();
                        trade.setBill(bill);
                        trade.setOldProductDetailMoney(bill.getTotalPrice().intValue());
                        trade.setProductDetailMoney((int) (bill.getTotalPrice() - reducedPrice));
                        trade.setOldVoucherMoney(bill.getTienGiam());
                        trade.setRequestDate(new Date());
                        tradeRepository.save(trade);
                    }
                }else {
                    bill.setId(-4); // bill over date => next
                }
                return bill;
            }
            return null;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

    private static @NotNull LocalDateTime getLocalDateTime(DateTimeFormatter[] formatters, Bill bill) {
        LocalDateTime dateCreate = null;
        for (DateTimeFormatter formatter : formatters) {
            try {
                dateCreate = LocalDateTime.parse(String.valueOf(bill.getDateCreate()), formatter).withNano(0);
                break;
            } catch (DateTimeParseException ignored) {

            }
        }
        assert dateCreate != null;
        return dateCreate.plusDays(7);
    }

    @GetMapping("/bill/billDetail/{idBill}")
    public List<BillDetail> getBillDetailsByIdBill(@PathVariable Integer idBill) {
        return returnProductService.getBillDetailById(idBill);
    }

    @PostMapping("/newTradeProduct/{idBillDetail}")
    public TradeProduct createTradeProduct(
            @PathVariable Integer idBillDetail,
            @RequestParam Integer quantity
    ) {
        return returnProductService.createTradeProduct(idBillDetail, quantity);
    }

//    @PostMapping("/addProductDetailToTrade/{idTradeProduct}")
//    public TradeProduct addProductDetailToTrade(
//            @RequestBody TradeProductItem tradeProductItem,
//            @PathVariable Integer idTradeProduct,
//            @RequestParam boolean status) {
//        return returnProductService.updateStatusTradeProduct(idTradeProduct, tradeProductItem, status);
//    }

    @GetMapping("/checkQuantity/{idBillDetail}")
    public boolean checkQuantity(
            @PathVariable Integer idBillDetail,
            @RequestParam Integer quantity
    ) {
        BillDetail billDetail = billDetailRepository.findById(idBillDetail)
                .orElseThrow(() -> new RuntimeException("Bill detail not found"));
        TradeProduct tradeProduct = tradeProductRepository.findByBillDetail(billDetail);
        if (tradeProduct != null) {
            int checkQuantity = billDetail.getQuantity() - tradeProduct.getQuantity();
            return checkQuantity >= quantity;
        }
        return billDetail.getQuantity() >= quantity;
    }

    @GetMapping("/checkQuantityTradeProduct/{idTradeProduct}")
    public TradeProduct checkQuantityTradeProduct(
            @PathVariable Integer idTradeProduct,
            @RequestParam Integer quantityTrade
    ) {
        return returnProductService.checkQuantityTrade(idTradeProduct, quantityTrade);
    }

    @GetMapping("/getProductTrade/{idBill}")
    public List<TradeProduct> getTradeProduct(@PathVariable Integer idBill) {
        Trade trade = tradeRepository.findByBill(billRepository.getReferenceById(idBill));
        return tradeProductRepository.findByTrade(trade);
    }

    @GetMapping("/getErrorProductDetail/{idBill}")
    public List<ErrorProductResponse> getErrorProductDetail(@PathVariable Integer idBill) {
        Trade trade = tradeRepository.findByBill(billRepository.getReferenceById(idBill));
        List<TradeProduct> tradeProducts = tradeProductRepository.findByTrade(trade);
        List<ErrorProductDetail> errorProductDetailList = errorProductDetailRepo.findByTrade(trade);
        List<ErrorProductResponse> errorProductResponses = new ArrayList<>();
        if (errorProductDetailList.isEmpty()) {
            for (TradeProduct tradeProduct : tradeProducts) {
                ErrorProductResponse errorProductResponse = new ErrorProductResponse();
                errorProductResponse.setQuantityReturn(tradeProduct.getQuantity());
                errorProductResponse.setDescribe("");
                errorProductResponse.setProductDetail(tradeProduct.getBillDetail().getProductDetail());
                errorProductResponses.add(errorProductResponse);
            }
        } else {
            for (TradeProduct tradeProduct : tradeProducts) {
                boolean check = false;
                for (ErrorProductDetail errorProductDetail : errorProductDetailList) {
                    if (tradeProduct.getBillDetail().getProductDetail().equals(errorProductDetail.getProductDetail())) {
                        ErrorProductResponse errorProductResponse = new ErrorProductResponse();
                        BeanUtils.copyProperties(errorProductDetail,errorProductResponse);
                        errorProductResponse.setQuantityReturn(tradeProduct.getQuantity());
                        errorProductResponse.setQuantityError(Math.min(tradeProduct.getQuantity(), errorProductDetail.getQuantity()));
                        errorProductResponses.add(errorProductResponse);
                        check = true;
                        break;
                    }
                }
                if (!check) {
                    ErrorProductResponse errorProductResponse = new ErrorProductResponse();
                    errorProductResponse.setQuantityReturn(tradeProduct.getQuantity());
                    errorProductResponse.setProductDetail(tradeProduct.getBillDetail().getProductDetail());
                    errorProductResponse.setDescribe(tradeProduct.getDescription());
                    errorProductResponses.add(errorProductResponse);
                }
            }
        }
        return errorProductResponses;
    }

    @GetMapping("/checkQuantityError/{idProductDetail}/{idBill}")
    public Integer getQuantityToCheck(
            @PathVariable Integer idProductDetail,
            @PathVariable Integer idBill
    ){
        Bill bill = billRepository.getReferenceById(idBill);
        Trade trade = tradeRepository.findByBill(bill);
        List<TradeProduct> tradeProducts = tradeProductRepository.findByTrade(trade);
        ProductDetail productDetail = productDetailRepo.getReferenceById(idProductDetail);
        for(TradeProduct tradeProduct : tradeProducts){
            if(tradeProduct.getBillDetail().getProductDetail().equals(productDetail)){
                return tradeProduct.getQuantity();
            }
        }
        return null;
    }

    @PostMapping("/addErrorProductDetail/{idBill}")
    public void addErrorProductDetail(@RequestBody List<ErrorProductDetail> errorProductDetails, @PathVariable Integer idBill) {
        Trade trade = tradeRepository.findByBill(billRepository.getReferenceById(idBill));
        for(ErrorProductDetail errorProductDetail : errorProductDetails){
            ProductDetail productDetail = productDetailRepo.getReferenceById(errorProductDetail.getProductDetail().getId());
            errorProductDetail.setProductDetail(productDetail);
            errorProductDetail.setTrade(trade);
            errorProductDetailRepo.save(errorProductDetail);
        }
        for(ErrorProductDetail errorProductDetail : errorProductDetailRepo.findByTrade(trade)){
            if(errorProductDetail.getQuantity() == 0){
                Optional<TradeProduct> tradeProduct = tradeProductRepository.findByTrade(trade).stream()
                        .filter(tradeProduct1 -> tradeProduct1.getBillDetail().getProductDetail().equals(errorProductDetail.getProductDetail()))
                        .findFirst();
                if(tradeProduct.isPresent()){
                    TradeProduct product = tradeProduct.get();
                    product.setDescription(errorProductDetail.getDescribe());
                    tradeProductRepository.save(product);
                }
                errorProductDetailRepo.delete(errorProductDetail);
            }
        }

    }

    @PostMapping("/confirmReturnProduct/{idBill}")
    public String confirmReturnProduct(@RequestBody TradeQuest trade, @PathVariable Integer idBill, Authentication authentication) throws MessagingException {
        if(authentication != null){
            Employee employee = voucherService.currentEmployee(authentication);
            returnProductService.confirmReturnProduct(trade.getTrade(),trade.getTradeProductItems(), idBill, employee);
            return "success";
        }else {
            return null;
        }

    }

    @DeleteMapping("/deleteProductDetailFromTradeProduct/{idTradeProductItem}")
    public void deleteProductDetailFromTradeProduct(@PathVariable Integer idTradeProductItem) {
        returnProductService.deleteProductDetailFromTradeProduct(idTradeProductItem);
    }

    @DeleteMapping("/deleteTradeProduct/{idTradeProduct}")
    public void deleteTradeProduct(@PathVariable Integer idTradeProduct) {
        returnProductService.deleteTradeProduct(idTradeProduct);
    }

    @GetMapping("/getAllErrorProductOfBill/{idBill}")
    public List<ErrorProductDetail> getAllErrorProductOfBill(@PathVariable Integer idBill){
        return errorProductDetailRepo.findByTrade(tradeRepository.findByBill(billRepository.getReferenceById(idBill)));
    }

    @GetMapping("/getPP/{id}")
    public Customer customer(@PathVariable Integer id){
        return customerRepo.getReferenceById(id);
    }

    @GetMapping("/updateDateForBill/{idBill}")
    public void update(@PathVariable Integer idBill){
        Bill bill = billRepository.getReferenceById(idBill);
        bill.setDateCreate(new Date());
        billRepository.save(bill);
    }

    @GetMapping("/getNotification")
    public List<Bill> getNotification(){
        List<Bill> bills = billRepo.findAll().stream()
                .filter(bill -> bill.getStatus() == 7)
                .toList();
        return bills;
    }

    @GetMapping("/findTradeByBillId/{idBill}")
    public Trade findBill(@PathVariable Integer idBill){
        Bill bill = billRepository.getReferenceById(idBill);
        return tradeRepository.findByBill(bill);
    }

    @GetMapping("/returnMoneySuccess/{idTrade}")
    public void returnMoneySuccess(
            @PathVariable Integer idTrade,
            @RequestParam String url,
            @RequestParam String message, Authentication authentication
    ) throws MessagingException {
        Employee employee = voucherService.currentEmployee(authentication);

        Trade trade = tradeRepository.getReferenceById(idTrade);
        trade.setResponseDate(new Date());
        trade.setQrPay(url);
        trade.setDescription(message);
        Bill bill = trade.getBill();
        bill.setTotalPrice(bill.getTotalPrice() - trade.getPayMoney());
        bill.setStatus(14);

        List<TradeProduct> tradeProducts = tradeProductRepository.findByTrade(trade);
        for(TradeProduct tradeProduct : tradeProducts){
            ProductDetail productDetail = tradeProduct.getBillDetail().getProductDetail();
            List<TradeProductItem> tradeProductItems = tradeProduct.getTradeProductItems();
            for(TradeProductItem tradeProductItem : tradeProductItems){
                if(tradeProductItem.isError()){
                    productDetail.setQuantity(productDetail.getQuantity() + 1);
                }
                tradeProductItem.setStatus(true);
                tradeProductItemRepo.save(tradeProductItem);
            }
        }

        billRepository.save(bill);
        tradeRepository.save(trade);

        BillHistory billHistory = new BillHistory();
        billHistory.setBill(bill);
        billHistory.setStatus(14);
        billHistory.setStatusOld(7);
        billHistory.setEmployee(employee);
        billHistory.setDescription("Hoàn tiền cho khách");
        billHistory.setDateUpdate(new Date());
        billHistoryRepo.save(billHistory);

        mailService.mailChuyenTien(tradeProductRepository.findByTrade(trade), billDetailRepository.findByBillId(bill.getId()), trade, "mailChuyenTien");
    }

    @DeleteMapping("/deleteTradeProductItem/{id}")
    public TradeProduct delete(@PathVariable Integer id){
        TradeProductItem tradeProductItem = tradeProductItemRepo.getReferenceById(id);
        TradeProduct tradeProduct = tradeProductItem.getTradeProduct();
        tradeProduct.getTradeProductItems().remove(tradeProductItem);
        tradeProduct.setQuantity(tradeProduct.getQuantity() - 1);
        tradeProduct.setTotalMoney((int) (tradeProduct.getQuantity() * tradeProduct.getBillDetail().getPrice()));
        if(tradeProduct.getQuantity() == 0){
            tradeProductItemRepo.delete(tradeProductItem);
            tradeProductRepository.delete(tradeProduct);
        }else {
            tradeProductRepository.save(tradeProduct);
            tradeProductItemRepo.delete(tradeProductItem);
        }
        returnProductService.loadTrade(tradeProduct.getTrade());
        return tradeProduct;
    }

    @GetMapping("/getErrorList")
    public List<ErrorList> getErrorList(){
        return errorListRepo.findAll();
    }
    @GetMapping("/getBillById/{idBill}")
    public Bill getBill(@PathVariable Integer idBill){
        return billRepository.getReferenceById(idBill);
    }

//    @DeleteMapping("/deleteTrade/{idBill}")
//    public void deleteTrade(@PathVariable Integer idBill, Authentication authentication){
//        Employee employee = voucherService.currentEmployee(authentication);
//        Bill bill = billRepository.getReferenceById(idBill);
//        Trade trade = tradeRepository.findByBill(bill);
//        List<TradeProduct> tradeProducts = tradeProductRepository.findByTrade(trade);
//        List<ErrorProductDetail> errorProductDetails = errorProductDetailRepo.findByTrade(trade);
//        List<TradeProduct> tradeProductList = new ArrayList<>(tradeProducts);
//        for(TradeProduct tradeProduct : tradeProducts){
//            for(ErrorProductDetail errorProductDetail : errorProductDetails){
//                if(tradeProduct.getBillDetail().getProductDetail().equals(errorProductDetail.getProductDetail())){
//                    ProductDetail productDetail = errorProductDetail.getProductDetail();
//                    productDetail.setQuantity(productDetail.getQuantity() - (tradeProduct.getQuantity() - errorProductDetail.getQuantity()));
//                    tradeProductList.remove(tradeProduct);
//                    break;
//                }
//            }
//        }
//        if(!tradeProductList.isEmpty()){
//            for(TradeProduct tradeProduct : tradeProductList){
//                ProductDetail productDetail = tradeProduct.getBillDetail().getProductDetail();
//                productDetail.setQuantity(productDetail.getQuantity() - tradeProduct.getQuantity());
//                productDetailRepo.save(productDetail);
//            }
//        }
//
//        errorProductDetailRepo.deleteAll(errorProductDetails);
//        tradeProductRepository.deleteAll(tradeProducts);
////        tradeProductRepository.deleteAll(tradeProducts);
//        trade.setDescription("Trả hàng cho hóa đơn #" + trade.getBill().getId());
//        trade.setBill(null);
//        bill.setStatus(5);
//
//        BillHistory billHistory = new BillHistory();
//        billHistory.setBill(bill);
//        billHistory.setStatus(5);
//        billHistory.setStatusOld(7);
//        billHistory.setEmployee(employee);
//        billHistory.setDescription("Hủy trả hàng");
//        billHistory.setDateUpdate(new Date());
//        billHistoryRepo.save(billHistory);
//
//        tradeRepository.save(trade);
//    }

    @DeleteMapping("/deleteTrade/{idBill}")
    public void deleteTrade(@PathVariable Integer idBill, Authentication authentication){
        Employee employee = voucherService.currentEmployee(authentication);
        Bill bill = billRepository.getReferenceById(idBill);
        Trade trade = tradeRepository.findByBill(bill);
        List<TradeProduct> tradeProducts = tradeProductRepository.findByTrade(trade);
        for(TradeProduct tradeProduct: tradeProducts){
            tradeProduct.setTradeProductItems(new ArrayList<>());
            tradeProductRepository.save(tradeProduct);
            List<TradeProductItem> tradeProductItems = tradeProductItemRepo.findByTradeProduct(tradeProduct);
            tradeProductItemRepo.deleteAll(tradeProductItems);
        }

        tradeProductRepository.deleteAll(tradeProducts);
//        tradeProductRepository.deleteAll(tradeProducts);
        trade.setDescription("Trả hàng cho hóa đơn #" + trade.getBill().getId());
        trade.setBill(null);
        bill.setStatus(5);

        BillHistory billHistory = new BillHistory();
        billHistory.setBill(bill);
        billHistory.setStatus(5);
        billHistory.setStatusOld(7);
        billHistory.setEmployee(employee);
        billHistory.setDescription("Hủy trả hàng");
        billHistory.setDateUpdate(new Date());
        billHistoryRepo.save(billHistory);

        tradeRepository.save(trade);
    }

    @GetMapping("/pdf/{id}")
    public PdfAfterReturnResponse getDataToPdf(@PathVariable Integer id){
        Bill bill = billRepository.getReferenceById(id);
        Trade trade = tradeRepository.findByBill(bill);
        List<TradeProductItemResponse> tradeProductItemResponses = new ArrayList<>();
        for(TradeProduct tradeProduct : tradeProductRepository.findByTrade(trade)){
            for(TradeProductItem tradeProductItem : tradeProduct.getTradeProductItems()){
                TradeProductItemResponse tradeProductItemResponse = new TradeProductItemResponse();
                tradeProductItemResponse.setProductDetail(tradeProduct.getBillDetail().getProductDetail());
                tradeProductItemResponse.setDescription(tradeProductItem.getDescription());
                tradeProductItemResponse.setCreateDate(tradeProductItem.getCreateDate());
                tradeProductItemResponse.setPrice(tradeProductItem.getTradeProduct().getBillDetail().getPrice().intValue());
                tradeProductItemResponses.add(tradeProductItemResponse);
            }
        }
        return new PdfAfterReturnResponse(trade, tradeProductItemResponses, billDetailRepository.findByBillId(bill.getId()));
    }


}
