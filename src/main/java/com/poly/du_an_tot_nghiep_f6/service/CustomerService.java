package com.poly.du_an_tot_nghiep_f6.service;

import com.poly.du_an_tot_nghiep_f6.entity.Address;
import com.poly.du_an_tot_nghiep_f6.entity.Customer;
import com.poly.du_an_tot_nghiep_f6.entity.Voucher;
import com.poly.du_an_tot_nghiep_f6.repository.AddressRepo;
import com.poly.du_an_tot_nghiep_f6.repository.CustomerRepo;
import com.poly.du_an_tot_nghiep_f6.repository.EmployeeRepo;
import com.poly.du_an_tot_nghiep_f6.repository.VoucherRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Period;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Service
public class CustomerService {
    @Autowired
    @Lazy
    private CustomerRepo customerRepository;

    @Autowired
    private AddressRepo addressRepository;

    @Autowired
    @Lazy
    private EmployeeService employeeService;

    @Autowired
    private EmployeeRepo employeeRepository;

    @Autowired
    private EmailService emailService; // Service để gửi email
    @Autowired
    private VoucherService voucherService;
    @Autowired
    private VoucherRepo voucherRepo;

    public Page<Customer> getCustomers(int page, int size, String keyword, String gender, Boolean status) {
        Sort sort = Sort.by(Sort.Direction.DESC, "createdDate");
        Pageable pageable = PageRequest.of(page, size, sort);

        if (keyword != null && !keyword.isEmpty()) {
            if (gender != null && !gender.isEmpty() && status != null) {
                return customerRepository.findByCustomerCodeContainingIgnoreCaseOrNameContainingIgnoreCaseOrPhoneContainingIgnoreCaseOrEmailContainingIgnoreCaseAndGenderAndStatus(
                        keyword, keyword, keyword, keyword, gender, status, pageable);
            } else if (gender != null && !gender.isEmpty()) {
                return customerRepository.findByCustomerCodeContainingIgnoreCaseOrNameContainingIgnoreCaseOrPhoneContainingIgnoreCaseOrEmailContainingIgnoreCaseAndGender(
                        keyword, keyword, keyword, keyword, gender, pageable);
            } else if (status != null) {
                return customerRepository.findByCustomerCodeContainingIgnoreCaseOrNameContainingIgnoreCaseOrPhoneContainingIgnoreCaseOrEmailContainingIgnoreCaseAndStatus(
                        keyword, keyword, keyword, keyword, status, pageable);
            } else {
                return customerRepository.findByCustomerCodeContainingIgnoreCaseOrNameContainingIgnoreCaseOrPhoneContainingIgnoreCaseOrEmailContainingIgnoreCase(
                        keyword, keyword, keyword, keyword, pageable);
            }
        }
        if (gender != null && !gender.isEmpty() && status != null) {
            return customerRepository.findByGenderAndStatus(gender, status, pageable);
        } else if (gender != null && !gender.isEmpty()) {
            return customerRepository.findByGender(gender, pageable);
        } else if (status != null) {
            return customerRepository.findByStatus(status, pageable);
        }

        return customerRepository.findAll(pageable);
    }

    public boolean isUsernameExistInBoth(String username) {
        return customerRepository.existsByUsername(username) || employeeRepository.existsByUsername(username);

    }

    public boolean isPhoneExist(String phone) {
        return customerRepository.existsByPhone(phone);
    }

    public boolean isEmailExist(String email) {
        return customerRepository.existsByEmail(email);
    }

    public boolean isEmailExistInBoth(String email) {
        return isEmailExist(email) || employeeService.isEmailExist(email);
    }

    public boolean isPhoneExistInBoth(String phone) {
        return customerRepository.existsByPhone(phone) || employeeRepository.existsByPhone(phone);
    }

    public long count() {
        return customerRepository.count();
    }

    public void save(Customer customer) {
        customerRepository.save(customer);

    }

    public void addVoucher(Integer idCustomer, Integer idVoucher) {
        if (idCustomer == null && idVoucher == null) {
            return;
        } else {
            if (idVoucher != null && idCustomer == null) {
                Voucher voucher = voucherService.getVoucher(idVoucher);
                voucher.setQuantityUsed(voucher.getQuantityUsed() - 1);
                voucherRepo.save(voucher);
                return;
            }
        }
        try {
            Customer customer = findById(idCustomer);
            Voucher voucher = voucherService.getVoucher(idVoucher);
            customer.getVouchers().removeIf(voucher0 -> Objects.equals(voucher.getId(), voucher0.getId()));
            customer.getVouchers().add(voucher);
            voucher.setQuantityUsed(voucher.getQuantityUsed() - 1);
            voucherRepo.save(voucher);
            customerRepository.save(customer);
        } catch (Exception e) {
            System.out.println("Thêm voucher thất bại: " + e.getMessage());
        }
    }

    public void removeVoucher(Integer idCustomer, Integer idVoucher) {
        if (idCustomer == null && idVoucher == null) {
            return;
        } else {
            if (idVoucher != null && idCustomer == null) {
                Voucher voucher = voucherService.getVoucher(idVoucher);
                voucher.setQuantityUsed(voucher.getQuantityUsed() + 1);
                voucherRepo.save(voucher);
                return;
            }
        }
        try {
            Customer customer = findById(idCustomer);
            Voucher voucher = voucherService.getVoucher(idVoucher);
            customer.getVouchers().removeIf(voucher0 -> Objects.equals(voucher.getId(), voucher0.getId()));
            voucher.setQuantityUsed(voucher.getQuantityUsed() + 1);
            voucherRepo.save(voucher);
            customerRepository.save(customer);
        } catch (Exception e) {
            System.out.println("Xóa voucher thất bại: " + e.getMessage());
        }
    }

    public Customer findById(Integer id) {
        return customerRepository.findById(id).orElseThrow(() -> new RuntimeException("Không tìm thấy khách hàng"));
    }

    public Optional<Customer> findByEmail(String email) {
        return customerRepository.findByEmail(email);
    }

    public Optional<Customer> findByUsername(String username) {
        return customerRepository.findByUsername(username);
    }

    public String getCustomerEmail(String username) {
        return customerRepository.findByUsername(username).map(Customer::getEmail).orElse(null);
    }

    public boolean updateEmail(String username, String newEmail) {
        Optional<Customer> customerOpt = customerRepository.findByUsername(username);

        if (customerOpt.isPresent()) {
            Customer customer = customerOpt.get();
            customer.setEmail(newEmail);
            customerRepository.save(customer);
            return true;
        }

        return false;
    }

}
