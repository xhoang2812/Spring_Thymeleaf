package com.poly.du_an_tot_nghiep_f6.controller;

import com.poly.du_an_tot_nghiep_f6.entity.Employee;
import com.poly.du_an_tot_nghiep_f6.repository.EmployeeRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PassEndcodeController {
    @Autowired
    EmployeeRepo employeeRepo;

    @Autowired
    PasswordEncoder passwordEncoder;

    @PostMapping("/register/user")
    public Employee createUser(@RequestBody Employee user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return employeeRepo.save(user);
    }
}
