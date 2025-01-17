package com.poly.du_an_tot_nghiep_f6.service;

import com.poly.du_an_tot_nghiep_f6.controller.Voucher.VoucherControllerRealTimeApi;
import com.poly.du_an_tot_nghiep_f6.entity.*;
import com.poly.du_an_tot_nghiep_f6.repository.CustomerRepo;
import com.poly.du_an_tot_nghiep_f6.repository.EmployeeRepo;
import com.poly.du_an_tot_nghiep_f6.repository.VoucherDetailRepo;
import com.poly.du_an_tot_nghiep_f6.repository.VoucherRepo;
import com.poly.du_an_tot_nghiep_f6.response.VoucherResponse;
import com.poly.du_an_tot_nghiep_f6.response.VoucherSaleResponse;
import jakarta.mail.MessagingException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class VoucherService {

    private final VoucherRepo voucherRepository;
    private final CustomerRepo customerRepository;
    private final MailService mailService;
    private final VoucherControllerRealTimeApi voucherControllerRealTimeApi;
    private final VoucherDetailRepo voucherDetailRepository;
    private final CustomerRepo customerRepo;
    private final EmployeeRepo employeeRepo;

    public Voucher createVoucher(VoucherResponse voucherResponse) throws MessagingException {
        List<Customer> customers = voucherResponse.getCustomers().stream()
                .map(customer -> customerRepository.findById(customer.getId()).orElseThrow(() -> new RuntimeException("Customer not found")))
                .toList();
        voucherResponse.setId(0);
        Voucher voucher = new Voucher();
        BeanUtils.copyProperties(voucherResponse, voucher);
        voucher.setStatus(true);
        voucher.setCondition(Condition.UPCOMING);
        voucher.setQuantityUsed(0);
        System.out.println(voucher);
        if (!voucher.isStyleVoucher()) {
            voucher.setMaximumReduction(voucher.getDiscount());
        }
        Voucher saveVoucher = voucherRepository.save(voucher);
        if (voucher.isFormVoucher()) {
            List<Customer> ctm = customerRepository.findAll();
            for (Customer customer : ctm) {
                List<Voucher> vouchers = customer.getVouchers();
                vouchers.add(saveVoucher);
                customer.setVouchers(vouchers);
                customerRepository.save(customer);
            }
        } else {
            saveVoucher.setQuantity(customers.size());
            for (Customer customer : customers) {
                VoucherDetail voucherDetail = new VoucherDetail();
                voucherDetail.setCustomer(customer);
                voucherDetail.setVoucher(saveVoucher);
                voucherDetailRepository.save(voucherDetail);
                mailService.sendEmail(customer.getEmail(),
                        customer.getName(),
                        voucher.getName(),
                        voucher.getCode(),
                        "",
                        voucher.getDiscount(),
                        voucher.getDateStart(),
                        voucher.getDateEnd(),
                        voucher.getMinimumOrder(),
                        voucher.getMaximumReduction(),
                        "mailCongratulatory");
                List<Voucher> vouchers = customer.getVouchers();
                vouchers.add(saveVoucher);
                customer.setVouchers(vouchers);
                customerRepository.save(customer);
            }
        }
        return voucherRepository.save(saveVoucher);
    }

    public Voucher updateVoucher(Integer id, VoucherResponse updatedVoucher) throws MessagingException {
        Voucher voucher = voucherRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Voucher not found"));
        String oldCode = voucher.getCode();
        List<Customer> oldCustomers = new ArrayList<>();
        for (VoucherDetail voucherDetail : voucherDetailRepository.findByVoucher(voucher)) {
            oldCustomers.add(voucherDetail.getCustomer());
        }
        List<Customer> newCustomers = updatedVoucher.getCustomers().stream() // new customer
                .map(customer -> customerRepository.findById(customer.getId()).orElseThrow(() -> new RuntimeException("Customer not found")))
                .collect(Collectors.toList());

        if (voucher.getCondition().equals(Condition.ACTIVE)) { // if voucher is active => only update dateEnd
            voucher.setDateEnd(updatedVoucher.getDateEnd());
        } else {
            voucher.setName(updatedVoucher.getName());
            voucher.setCode(updatedVoucher.getCode());
            voucher.setDiscount(updatedVoucher.getDiscount());
            if (updatedVoucher.isFormVoucher()) { // set quantity of voucher
                voucher.setQuantity(updatedVoucher.getQuantity());
            } else {
                voucher.setQuantity(newCustomers.size());
            }
            voucher.setDateStart(updatedVoucher.getDateStart());
            voucher.setDateEnd(updatedVoucher.getDateEnd());
            voucher.setMinimumOrder(updatedVoucher.getMinimumOrder());
            if (!updatedVoucher.isStyleVoucher()) {
                voucher.setMaximumReduction(voucher.getDiscount());
            } else {
                voucher.setMaximumReduction(updatedVoucher.getMaximumReduction());
            }
            voucher.setDescription(updatedVoucher.getDescription());
            voucher.setCondition(voucher.getCondition());
            voucher.setStatus(voucher.isStatus());
            voucher.setStyleVoucher(updatedVoucher.isStyleVoucher());
            voucher.setFormVoucher(updatedVoucher.isFormVoucher());
        }
        if (!updatedVoucher.isFormVoucher()) { // if voucher is private
            List<Customer> listOldCustomers = new ArrayList<>(oldCustomers);
            List<Customer> listNewCustomers = new ArrayList<>(newCustomers);
            listNewCustomers.removeAll(oldCustomers); // new customer (give email configuration)
            listOldCustomers.removeAll(newCustomers); // old customer (give email apologize and remove customer)

            for (Customer customer : listNewCustomers) { // give email configuration
                mailService.sendEmail(customer.getEmail(),
                        customer.getName(),
                        voucher.getName(),
                        voucher.getCode(),
                        "",
                        voucher.getDiscount(),
                        voucher.getDateStart(),
                        voucher.getDateEnd(),
                        voucher.getMinimumOrder(),
                        voucher.getMaximumReduction(),
                        "mailCongratulatory");
                customer.getVouchers().add(voucher);
                customerRepository.save(customer);
                VoucherDetail voucherDetail = new VoucherDetail();
                voucherDetail.setVoucher(voucher);
                voucherDetail.setCustomer(customer);
                voucherDetailRepository.save(voucherDetail);
            }

            sendMailApologize(voucher, listOldCustomers);

            List<Customer> notRevokedCustomers = new ArrayList<>(oldCustomers);
            notRevokedCustomers.retainAll(newCustomers); // old customer (give email update voucher)
            for (Customer customer : notRevokedCustomers) {
                mailService.sendEmail(customer.getEmail(),
                        customer.getName(),
                        voucher.getName(),
                        voucher.getCode(),
                        oldCode,
                        voucher.getDiscount(),
                        voucher.getDateStart(),
                        voucher.getDateEnd(),
                        voucher.getMinimumOrder(),
                        voucher.getMaximumReduction(),
                        "mailUpdateVoucher");
            }
            List<Customer> removeCustomer = customerRepository.findAll();
            removeCustomer.removeAll(listOldCustomers);
            removeCustomer.removeAll(listNewCustomers);
            removeCustomer.removeAll(notRevokedCustomers);

            for (Customer customer : removeCustomer) {
                customer.getVouchers().remove(voucher);
            }
        } else {
            if (!oldCustomers.isEmpty()) { // voucher is public
                sendMailApologize(voucher, oldCustomers);
            }
            for (Customer customer : customerRepository.findAll()) {
                customer.getVouchers().add(voucher);
                customerRepository.save(customer);
            }
        }
        voucher.setStatus(true);
        return voucherRepository.save(voucher);
    }

    public void sendMailApologize(Voucher voucher, List<Customer> listOldCustomers) throws MessagingException {
        for (Customer customer : listOldCustomers) { // give email apologize
            mailService.sendEmail(customer.getEmail(),
                    customer.getName(),
                    voucher.getName(),
                    voucher.getCode(),
                    "",
                    voucher.getDiscount(),
                    voucher.getDateStart(),
                    voucher.getDateEnd(),
                    voucher.getMinimumOrder(),
                    voucher.getMaximumReduction(),
                    "mailApology");
            customer.getVouchers().remove(voucher);
            customerRepository.save(customer);
            List<VoucherDetail> voucherDetails = voucherDetailRepository.findByVoucher(voucher);
            for (VoucherDetail voucherDetail : voucherDetails) {
                if (voucherDetail.getCustomer().equals(customer)) {
                    voucherDetailRepository.delete(voucherDetail);
                }
            }
        }
    }

    public Voucher deleteVoucher(Integer id) throws MessagingException {
        Voucher voucher = voucherRepository.findById(id).orElseThrow(() -> new RuntimeException("Voucher not found"));
        voucher.setStatus(!voucher.isStatus());
        List<Customer> customers = new ArrayList<>();
        for (VoucherDetail voucherDetail : voucherDetailRepository.findByVoucher(voucher)) {
            customers.add(voucherDetail.getCustomer());
        }
        if (!customers.isEmpty() && !voucher.isStatus()) {
            for (Customer customer : customers) {
                mailService.sendEmail(customer.getEmail(),
                        customer.getName(),
                        voucher.getName(),
                        voucher.getCode(),
                        "",
                        voucher.getDiscount(),
                        voucher.getDateStart(),
                        voucher.getDateEnd(),
                        voucher.getMinimumOrder(),
                        voucher.getMaximumReduction(),
                        "mailApology");
            }
        } else {
            for (Customer customer : customers) {
                mailService.sendEmail(customer.getEmail(),
                        customer.getName(),
                        voucher.getName(),
                        voucher.getCode(),
                        "",
                        voucher.getDiscount(),
                        voucher.getDateStart(),
                        voucher.getDateEnd(),
                        voucher.getMinimumOrder(),
                        voucher.getMaximumReduction(),
                        "mailCongratulatory");
            }
        }
        voucherRepository.save(voucher);
        return voucher;
    }

    public VoucherResponse getVoucherResponseByIdVoucher(Integer idVoucher) throws Exception {
        Voucher voucher = voucherRepository.findById(idVoucher)
                .orElseThrow(() -> new Exception("Voucher not found with id: " + idVoucher));
        VoucherResponse voucherResponse = new VoucherResponse();
        BeanUtils.copyProperties(voucher, voucherResponse);
        if (voucher.isFormVoucher()) {
            voucherResponse.setCustomers(new ArrayList<>());
        } else {
            List<VoucherDetail> voucherDetails = voucherDetailRepository.findByVoucher(voucher);
            List<Customer> customers = new ArrayList<>();
            for (VoucherDetail voucherDetail : voucherDetails) {
                customers.add(voucherDetail.getCustomer());
            }
            voucherResponse.setCustomers(customers);
        }
        return voucherResponse;
    }

    public List<Customer> getOldCustomerOfVoucher(Voucher voucher) {
        List<Customer> customers =  new ArrayList<>();
        List<VoucherDetail> voucherDetails = voucherDetailRepository.findByVoucher(voucher);
        for(VoucherDetail voucherDetail : voucherDetails){
            customers.add(voucherDetail.getCustomer());
        }
        return customers;
    }

    @Transactional
    public void updateConditionVoucher() {
        List<Voucher> list = voucherRepository.findAll();
        for (Voucher voucher : list) {
            String dateTimeStr1 = String.valueOf(voucher.getDateStart());
            String dateTimeStr2 = String.valueOf(voucher.getDateEnd());
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.S");
            LocalDateTime dateTime1 = LocalDateTime.parse(dateTimeStr1, formatter);
            LocalDateTime dateTime2 = LocalDateTime.parse(dateTimeStr2, formatter);
            if (LocalDateTime.now().isAfter(dateTime1) && LocalDateTime.now().isBefore(dateTime2)) {
                voucher.setCondition(Condition.ACTIVE);
            } else if (LocalDateTime.now().isBefore(dateTime1)) {
                voucher.setCondition(Condition.UPCOMING);
            } else {
                voucher.setCondition(Condition.INACTIVE);
            }
            voucherRepository.save(voucher);
            voucherControllerRealTimeApi.sendVoucherUpdate(voucher);
        }
    }

    public Voucher getVoucher(Integer idVoucher) {
        return voucherRepository.getReferenceById(idVoucher);
    }


    public List<VoucherSaleResponse> getVoucherResponse(Integer idCustomer) {
        List<Voucher> vouchers;
        if (idCustomer != 0) {
            vouchers = customerRepo.findById(idCustomer).get().getVouchers();
        } else {
            vouchers = getPublicVoucher();
        }
        return parseVoucherResponse(vouchers);
    }

    private List<Voucher> getPublicVoucher() {
        return voucherRepository.findAll().stream()
                .filter(voucher -> voucher.getCondition().equals(Condition.ACTIVE) &&
                        voucher.isFormVoucher() && voucher.isStatus() &&
                        voucher.getDateEnd().after(new Date()))
                .toList();
    }

    private List<VoucherSaleResponse> parseVoucherResponse(List<Voucher> vouchers) {
        List<VoucherSaleResponse> voucherResponses = new ArrayList<>();
        if (vouchers != null) {// list không rỗng
            for (Voucher voucher : vouchers) {
                if (voucher.isStatus()) {// status = true
                    if (voucher.getCondition().equals("Đang diễn ra")) {// đang diễn ra
                        if (voucher.getQuantity() > voucher.getQuantityUsed()) {// số lượng > số lượng đã dùng
                            voucherResponses.add(new VoucherSaleResponse(
                                    voucher.getId(),
                                    voucher.getName(),
                                    voucher.getCode(),
                                    voucher.getDiscount(),
                                    voucher.isStyleVoucher(),
                                    voucher.isFormVoucher(),
                                    voucher.getMinimumOrder(),
                                    voucher.getMaximumReduction()
                            ));
                        }
                    }
                }
            }
        } else {
            System.out.println("Không có voucher.");
            voucherResponses = null;
        }
        return voucherResponses;
    }

    public List<Voucher> getVouchersCurrent() {
        return voucherRepository.findAll().stream()
                .filter(voucher -> !voucher.getCondition().equals(Condition.INACTIVE) && voucher.isFormVoucher())
                .toList();
    }

    public Employee currentEmployee(Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        Optional<Employee> user = employeeRepo.findByUsername(userDetails.getUsername());
        return user.orElse(null);
    }

    public List<Voucher> getAllCheckOut() {
        return voucherRepository.getAllCheckOut();
    }

    public Voucher findById(Integer idVoucher) {
        return voucherRepository.findById(idVoucher).orElse(null);
    }
}
