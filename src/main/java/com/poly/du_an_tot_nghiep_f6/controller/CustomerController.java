package com.poly.du_an_tot_nghiep_f6.controller;

import com.poly.du_an_tot_nghiep_f6.entity.Address;
import com.poly.du_an_tot_nghiep_f6.entity.Cart;
import com.poly.du_an_tot_nghiep_f6.entity.Voucher;
import com.poly.du_an_tot_nghiep_f6.repository.CartRepo;
import com.poly.du_an_tot_nghiep_f6.service.AddressService;
import com.poly.du_an_tot_nghiep_f6.service.CustomerService;
import com.poly.du_an_tot_nghiep_f6.entity.Customer;
import com.poly.du_an_tot_nghiep_f6.service.EmailService;
import com.poly.du_an_tot_nghiep_f6.service.VoucherService;
import com.poly.du_an_tot_nghiep_f6.service.impl.UploadServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.*;
@Controller
@RequestMapping("/customer")
public class CustomerController {

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    private UploadServiceImpl uploadService;

    @Autowired
    private CustomerService customerService;

    @Autowired
    private AddressService addressService;

    @Autowired
    private EmailService emailService;
    @Autowired
    private CartRepo cartRepo;

    @Autowired
    private VoucherService voucherService;


    //Load table
    @GetMapping("/display")
    public String listCustomers(@RequestParam(defaultValue = "0") int page,
                                @RequestParam(defaultValue = "5") int size,
                                @RequestParam(required = false) String keyword,
                                @RequestParam(required = false) String gender,
                                @RequestParam(required = false) Boolean status,
                                Model model) {
        Page<Customer> customersPage = customerService.getCustomers(page, size, keyword, gender, status);

        model.addAttribute("customersPage", customersPage);
        model.addAttribute("keyword", keyword);
        model.addAttribute("gender", gender);
        model.addAttribute("status", status);

        if (model.containsAttribute("error")) {
            model.addAttribute("error", model.asMap().get("error"));
        }
        if (!customersPage.hasContent()) {
            model.addAttribute("noData", true);
        }

        return "admin/customer/customer";
    }

//Thêm khách hàng
    @GetMapping("/add")
    public String showAddCustomerForm(Model model) {
        model.addAttribute("customer", new Customer());
        return "admin/customer/add";
    }

//Thêm khách hàng
    @PostMapping("/save")
    public String saveCustomer(@RequestParam String name,
                               @RequestParam String phone,
                               @RequestParam String email,
                               @RequestParam String gender,
                               @RequestParam String dateOfBirth,
                               @RequestParam boolean status,
                               @RequestParam("photo") MultipartFile photo,
                               @RequestParam("provinceName") String provinceName,
                               @RequestParam("districtName") String districtName,
                               @RequestParam("wardName") String wardName,
                               @RequestParam("addressDetails") String addressDetails,
                               RedirectAttributes redirectAttributes) {
        try {
            String customerCode = "KH" + (customerService.count() + 1);


            String username = customerCode + generateRandomString(4);
            String password = generateRandomString(6);

            Customer customer = new Customer();
            customer.setCustomerCode(customerCode);
            customer.setName(name);
            customer.setPhone(phone);
            customer.setEmail(email);
            customer.setDateOfBirth(LocalDate.parse(dateOfBirth));
            customer.setUsername(username);
            customer.setPassword(passwordEncoder.encode(password));
            customer.setGender(gender);
            customer.setStatus(status);
            customer.setPhoto(uploadService.storeFile(photo));

            Address customerAddress = new Address();
            customerAddress.setCustomer(customer);
            customerAddress.setNameRecipient(name);
            customerAddress.setPhone(phone);
            customerAddress.setProvince(provinceName);
            customerAddress.setDistrict(districtName);
            customerAddress.setWard(wardName);
            customerAddress.setAddreseDetail(addressDetails);
            customerAddress.setDefault(true);

            List<Address> addresses = new ArrayList<>();
            addresses.add(customerAddress);
            customer.setAddresses(addresses);
            customer.setVouchers(voucherService.getVouchersCurrent()); // lay ra voucher va add vao cho khach hang
            customerService.save(customer);
            Cart cart = new Cart();
            cart.setCustomer(customer);
            cart.setStatus(true);
            cartRepo.save(cart);

            emailService.sendEmail(email, "Thông tin đăng nhập tại F6 Store", emailService.buildHtmlEmailContent(username, password));


            redirectAttributes.addFlashAttribute("message", "Thêm khách hàng thành công và thông tin đăng nhập đã được gửi qua email!");

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Có lỗi xảy ra khi thêm khách hàng!");
        }
        return "redirect:/customer/display";
    }

//Tạo tài khoản mật khẩu ngẫu nhiên
    private String generateRandomString(int length) {
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder result = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < length; i++) {
            result.append(characters.charAt(random.nextInt(characters.length())));
        }
        return result.toString();
    }

//Sửa khách hàng
    @GetMapping("/edit/{id}")
    public String showUpdateForm(@PathVariable("id") Integer id, Model model) {
        Customer customer = customerService.findById(id);
        model.addAttribute("customer", customer);

        return "admin/customer/edit";
    }

//Sửa khách hàng
    @PostMapping("/update/{id}")
    public String editCustomer(@PathVariable("id") Integer id,
                               @RequestParam String name,
                               @RequestParam String phone,
                               @RequestParam String email,
                               @RequestParam String gender,
                               @RequestParam String dateOfBirth,
                               @RequestParam("photo") MultipartFile photo,
                               RedirectAttributes redirectAttributes) {
        try {
            Customer customer = customerService.findById(id);

            customer.setName(name);
            customer.setPhone(phone);
            customer.setEmail(email);
            customer.setGender(gender);
            customer.setDateOfBirth(LocalDate.parse(dateOfBirth));

            if (!photo.isEmpty()) {
                customer.setPhoto(uploadService.storeFile(photo));
            }

            customerService.save(customer);
            redirectAttributes.addFlashAttribute("message", "Chỉnh sửa thông tin khách hàng thành công!");

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Có lỗi xảy ra khi chỉnh sửa khách hàng!");
        }
        return "redirect:/customer/display";
    }


//Load ảnh
    @GetMapping("/flim-image/{name}")
    public ResponseEntity<ByteArrayResource> getFlimImage(@PathVariable("name")String name){
        try {
            Path image = Path.of("uploads",name);
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

//Sửa trạng thái khách hàng
    @GetMapping("/toggleStatus/{id}")
    public String toggleCustomerStatus(@PathVariable Integer id) {
        Customer customer = customerService.findById(id);
        if (customer != null) {
            customer.setStatus(!customer.isStatus());
            customerService.save(customer);
        }
        return "redirect:/customer/display";
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
        return "admin/address/address";
    }

//Thêm địa chỉ khách hàng
    @PostMapping("/{customerId}/save-address")
    public String saveAddress(@PathVariable Integer customerId,
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
                return "redirect:/customer/display";
            }

            Address customerAddress = new Address();
            customerAddress.setCustomer(customer);
            customerAddress.setNameRecipient(nameRecipient);
            customerAddress.setPhone(phone);
            customerAddress.setProvince(provinceName);
            customerAddress.setDistrict(districtName);
            customerAddress.setWard(wardName);
            customerAddress.setAddreseDetail(addreseDetail);
            customerAddress.setDefault(false);
            addressService.save(customerAddress);

            redirectAttributes.addFlashAttribute("message", "Thêm địa chỉ thành công!");
            return "redirect:/customer/{customerId}/addresses";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Đã xảy ra lỗi: " + e.getMessage());
            return "redirect:/customer/display";
        }
    }

//Sửa địa chỉ khách hàng
    @GetMapping("/{customerId}/edit-address/{addressId}")
    public String editAddress(@PathVariable Integer customerId,
                              @PathVariable Integer addressId,
                              Model model,
                              RedirectAttributes redirectAttributes) {
        try {
            Customer customer = customerService.findById(customerId);
            if (customer == null) {
                redirectAttributes.addFlashAttribute("error", "Khách hàng không tồn tại!");
                return "redirect:/customer/display";
            }
            Address customerAddress = addressService.findById(addressId);
            if (customerAddress == null || !customerAddress.getCustomer().getId().equals(customerId)) {
                redirectAttributes.addFlashAttribute("error", "Địa chỉ không tồn tại hoặc không thuộc khách hàng này!");
                return "redirect:/customer/display";
            }
            model.addAttribute("customer", customer);
            model.addAttribute("address", customerAddress);

            return "admin/address/edit";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Đã xảy ra lỗi: " + e.getMessage());
            return "redirect:/customer/display";
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
                return "redirect:/customer/display";
            }

            Address customerAddress = addressService.findById(addressId);
            if (customerAddress == null || !customerAddress.getCustomer().getId().equals(customerId)) {
                redirectAttributes.addFlashAttribute("error", "Địa chỉ không tồn tại hoặc không thuộc khách hàng này!");
                return "redirect:/customer/display";
            }

            customerAddress.setNameRecipient(nameRecipient);
            customerAddress.setPhone(phone);
            customerAddress.setProvince(provinceName);
            customerAddress.setDistrict(districtName);
            customerAddress.setWard(wardName);
            customerAddress.setAddreseDetail(addreseDetail);
            addressService.save(customerAddress);

            redirectAttributes.addFlashAttribute("message", "Cập nhật địa chỉ thành công!");
            return "redirect:/customer/{customerId}/addresses";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Đã xảy ra lỗi: " + e.getMessage());
            return "redirect:/customer/display";
        }
    }



//Chỉnh trạng thái địa chỉ khách hàng
    @PostMapping("/set-default-address")
    public String setDefaultAddress(@RequestParam Integer addressId, @RequestParam Integer customerId, RedirectAttributes redirectAttributes) {
        try {
            addressService.setDefaultAddress(addressId, customerId);
            redirectAttributes.addFlashAttribute("message", "Địa chỉ đã được đặt làm mặc định thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Không thể đặt địa chỉ này làm mặc định: " + e.getMessage());
        }
        return "redirect:/customer/" + customerId + "/addresses";
    }

//Xóa địa chỉ khách hàng
    @GetMapping("/delete-address/{id}")
    public String deleteAddress(@PathVariable Integer id, RedirectAttributes redirectAttributes) {
        Address address = addressService.findById(id);
        if (address.isDefault()) {
            redirectAttributes.addFlashAttribute("error", "Không thể xóa địa chỉ mặc định.");
            return "redirect:/customer/display?error=cannotdelete";
        }
        addressService.delete(id);
        redirectAttributes.addFlashAttribute("message", "Xóa địa chỉ thành công!");
        return "redirect:/customer/display";
    }

//API check
    @GetMapping("/api/customer/check-username")
    public ResponseEntity<Boolean> checkUsername(@RequestParam String username) {
        return ResponseEntity.ok(customerService.isUsernameExistInBoth(username));
    }

    @GetMapping("/api/customer/check-email")
    public ResponseEntity<Boolean> checkEmail(@RequestParam String email) {
        return ResponseEntity.ok(customerService.isEmailExistInBoth(email));
    }

    @GetMapping("/api/customer/check-phone")
    public ResponseEntity<Boolean> checkPhone(@RequestParam String phone) {
        return ResponseEntity.ok(customerService.isPhoneExistInBoth(phone));
    }
}
