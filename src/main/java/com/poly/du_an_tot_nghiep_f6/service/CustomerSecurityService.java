package com.poly.du_an_tot_nghiep_f6.service;

import com.poly.du_an_tot_nghiep_f6.entity.Customer;
import com.poly.du_an_tot_nghiep_f6.repository.CustomerRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CustomerSecurityService implements UserDetailsService {

    @Autowired
    private CustomerRepo customerRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<Customer> customer = customerRepository.findByUsername(username);
        if (customer.isPresent()) {
            var customerObj = customer.get();
            return User.builder()
                    .username(customerObj.getUsername())
                    .password(customerObj.getPassword())
                    .roles("USER")  // Khách hàng chỉ có vai trò USER
                    .build();
        } else {
            throw new UsernameNotFoundException(username);
        }
    }
}