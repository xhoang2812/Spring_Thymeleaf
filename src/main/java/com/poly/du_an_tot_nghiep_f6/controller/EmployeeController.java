package com.poly.du_an_tot_nghiep_f6.controller;

import com.poly.du_an_tot_nghiep_f6.service.EmployeeService;
import com.poly.du_an_tot_nghiep_f6.entity.Employee;
import com.poly.du_an_tot_nghiep_f6.service.impl.UploadServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;

@Controller
@RequestMapping("/employee")
@RequiredArgsConstructor
public class EmployeeController {
    @Autowired
    private UploadServiceImpl uploadService;

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    PasswordEncoder passwordEncoder;

//Load table
    @GetMapping("/display")
    public String listEmployees(@RequestParam(defaultValue = "0") int page,
                                @RequestParam(defaultValue = "5") int size,
                                @RequestParam(required = false) String keyword,
                                @RequestParam(required = false) String gender,
                                @RequestParam(required = false) Boolean status,
                                Model model) {
        Page<Employee> employeesPage = employeeService.getEmployees(page, size, keyword, gender, status);

        model.addAttribute("employeesPage", employeesPage);
        model.addAttribute("keyword", keyword);
        model.addAttribute("gender", gender);
        model.addAttribute("status", status);

        if (!employeesPage.hasContent()) {
            model.addAttribute("noData", true);
        }

        return "admin/employee/employee";
    }

//Thêm khách hàng
    @GetMapping("/add")
    public String showAddForm(Model model) {
        model.addAttribute("employee", new Employee());
        return "admin/employee/add";
    }

//Thêm khách hàng
    @PostMapping("/save")
    public String saveEmployee(
            @RequestParam String name,
            @RequestParam String phone,
            @RequestParam String email,
            @RequestParam String gender,
            @RequestParam String dateOfBirth,
            @RequestParam("photo") MultipartFile photo,
            @RequestParam boolean status,
            @RequestParam String position,
            @RequestParam String username,
            @RequestParam String password,
            @RequestParam("provinceName") String provinceName,
            @RequestParam("districtName") String districtName,
            @RequestParam("wardName") String wardName,
            RedirectAttributes redirectAttributes) {

        System.out.println("Province: " + provinceName);
        System.out.println("District: " + districtName);
        System.out.println("Ward: " + wardName);

        String employeeCode = "NV" + (employeeService.count() + 1);

        try {
            Employee employee = new Employee();
            employee.setEmployeeCode(employeeCode);
            employee.setName(name);
            employee.setPhone(phone);
            employee.setEmail(email);
            employee.setGender(gender);
            employee.setUsername(username);
            employee.setPassword(passwordEncoder.encode(password));
            employee.setPosition(position);
            employee.setStatus(status);
            employee.setDateOfBirth(LocalDate.parse(dateOfBirth));
            employee.setPhoto(uploadService.storeFile(photo));

            // Lưu tên tỉnh, huyện, xã
            employee.setProvince(provinceName);
            employee.setDistrict(districtName);
            employee.setWard(wardName);

            employeeService.save(employee);

            redirectAttributes.addFlashAttribute("message", "Thêm nhân viên thành công!");
        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("error", "Có lỗi xảy ra khi thêm nhân viên!");
        }
        return "redirect:/employee/display";
    }



//Sửa nhân viên
    @GetMapping("/edit/{id}")
    public String editEmployee(@PathVariable Integer id, Model model) {
        Employee employee = employeeService.findById(id);
        model.addAttribute("employee", employee);
        return "admin/employee/edit";
    }
//Sửa nhân viên
    @PostMapping("/update/{id}")
    public String updateEmployee(
            @PathVariable("id") Integer id,
            @RequestParam String employeeCode,
            @RequestParam String name,
            @RequestParam String phone,
            @RequestParam String email,
            @RequestParam String gender,
            @RequestParam String position,
            @RequestParam String dateOfBirth,
            @RequestParam("photo") MultipartFile photo,
            @RequestParam String provinceName,
            @RequestParam String districtName,
            @RequestParam String wardName,
            RedirectAttributes redirectAttributes) {

        try {

            Employee employee = employeeService.findById(id);
            employee.setEmployeeCode(employeeCode);
            employee.setName(name);
            employee.setPhone(phone);
            employee.setEmail(email);
            employee.setProvince(provinceName);
            employee.setDistrict(districtName);
            employee.setWard(wardName);
            employee.setGender(gender);
            employee.setPosition(position);
            employee.setDateOfBirth(LocalDate.parse(dateOfBirth));
            if (!photo.isEmpty()) {
                employee.setPhoto(uploadService.storeFile(photo));
            }
            employeeService.save(employee);
            redirectAttributes.addFlashAttribute("message", "Cập nhật khách hàng thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Có lỗi xảy ra khi cập nhật khách hàng!");
        }
        return "redirect:/employee/display";
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

//Xem chi tiết nhân viên
    @GetMapping("/detail/{id}")
    public String showEmployeeDetail(@PathVariable("id") Integer id, Model model) {
        Employee employee = employeeService.findById(id);
        if (employee != null) {
            model.addAttribute("employee", employee);
            return "admin/employee/detail";
        } else {
            model.addAttribute("error", "Không tìm thấy nhân viên!");
            return "employee/display";
        }
    }

//Sửa trạng thái nhân viên
    @GetMapping("/toggleStatus/{id}")
    public String toggleCustomerStatus(@PathVariable Integer id, RedirectAttributes redirectAttributes) {
        Employee employee = employeeService.findById(id);
        if (employee != null) {
            // Kiểm tra nếu là "Admin", không cho phép thay đổi trạng thái
            if ("Admin".equals(employee.getPosition())) {
                redirectAttributes.addFlashAttribute("error", "Không thể thay đổi trạng thái của Admin!");
                return "redirect:/employee/display";
            }
            // Nếu là "Nhân viên", thay đổi trạng thái
            employee.setStatus(!employee.isStatus());
            employeeService.save(employee);
        }
        return "redirect:/employee/display";
    }

//API check
    @GetMapping("/api/employee/check-username")
    public ResponseEntity<Boolean> checkUsername(@RequestParam String username) {
        return ResponseEntity.ok(employeeService.isUsernameExistInBoth(username));
    }

    @GetMapping("/api/employee/check-email")
    public ResponseEntity<Boolean> checkEmail(@RequestParam String email) {
        return ResponseEntity.ok(employeeService.isEmailExistInBoth(email));
    }

    @GetMapping("/api/employee/check-phone")
    public ResponseEntity<Boolean> checkPhone(@RequestParam String phone) {
        return ResponseEntity.ok(employeeService.isPhoneExistInBoth(phone));
    }
}
