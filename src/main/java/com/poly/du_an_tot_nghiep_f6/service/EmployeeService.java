package com.poly.du_an_tot_nghiep_f6.service;

import com.poly.du_an_tot_nghiep_f6.entity.Employee;
import com.poly.du_an_tot_nghiep_f6.repository.CustomerRepo;
import com.poly.du_an_tot_nghiep_f6.repository.EmployeeRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Period;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class EmployeeService  {
    @Autowired
    @Lazy
    private EmployeeRepo employeeRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    @Lazy
    private CustomerService customerService;

    @Autowired
    @Lazy
    private CustomerRepo customerRepository;


    public void save(Employee employee) {
        employeeRepository.save(employee);
    }

    public Page<Employee> getEmployees(int page, int size, String keyword, String gender, Boolean status) {
        Sort sort = Sort.by(Sort.Direction.DESC, "dateCreate");
        Pageable pageable = PageRequest.of(page, size, sort);

        if (keyword != null && !keyword.isEmpty()) {
            if (gender != null && !gender.isEmpty() && status != null) {
                return employeeRepository.findByEmployeeCodeContainingIgnoreCaseOrNameContainingIgnoreCaseAndGenderAndStatus(
                        keyword, keyword, gender, status, pageable);
            } else if (gender != null && !gender.isEmpty()) {
                return employeeRepository.findByEmployeeCodeContainingIgnoreCaseOrNameContainingIgnoreCaseAndGender(
                        keyword, keyword, gender, pageable);
            } else if (status != null) {
                return employeeRepository.findByEmployeeCodeContainingIgnoreCaseOrNameContainingIgnoreCaseAndStatus(
                        keyword, keyword, status, pageable);
            } else {
                return employeeRepository.findByEmployeeCodeContainingIgnoreCaseOrNameContainingIgnoreCase(
                        keyword, keyword, pageable);
            }
        }
        if (gender != null && !gender.isEmpty() && status != null) {
            return employeeRepository.findByGenderAndStatus(gender, status, pageable);
        } else if (gender != null && !gender.isEmpty()) {
            return employeeRepository.findByGender(gender, pageable);
        } else if (status != null) {
            return employeeRepository.findByStatus(status, pageable);
        }

        return employeeRepository.findAll(pageable);
    }

    public long count() {
        return employeeRepository.count();
    }


    public Employee findById(Integer id) {
        return employeeRepository.findById(id).orElseThrow(() -> new RuntimeException("Nhân Viên không tồn tại"));
    }

    public void delete(Integer id) {
        employeeRepository.deleteById(id);
    }

    public boolean isUsernameExist(String username) {
        return employeeRepository.existsByUsername(username);
    }

    public boolean isUsernameExistInBoth(String username) {
        return employeeRepository.existsByUsername(username) || customerRepository.existsByUsername(username);
    }

    public boolean isUsernameExistForEdit(String username, Integer employeeId) {
        return employeeRepository.existsByUsernameAndIdNot(username, employeeId);
    }

    public boolean isPhoneExist(String phone) {
        return employeeRepository.existsByPhone(phone);
    }

    public boolean isEmailExist(String email) {
        return employeeRepository.existsByEmail(email);
    }

    public boolean isEmailExistInBoth(String email) {
        return isEmailExist(email) || customerService.isEmailExist(email);
    }


    public Optional<Employee> findByEmail(String email) {
        return employeeRepository.findByEmail(email);
    }

    public Optional<Employee> findByUsername(String username) {
        return employeeRepository.findByUsername(username);
    }

    // Kiểm tra phone đã tồn tại trong cả Employee và Customer
    public boolean isPhoneExistInBoth(String phone) {
        return employeeRepository.existsByPhone(phone) || customerRepository.existsByPhone(phone);
    }

}

