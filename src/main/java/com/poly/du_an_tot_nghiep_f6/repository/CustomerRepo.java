package com.poly.du_an_tot_nghiep_f6.repository;

import com.poly.du_an_tot_nghiep_f6.entity.Customer;
import com.poly.du_an_tot_nghiep_f6.entity.Voucher;
import com.poly.du_an_tot_nghiep_f6.response.CustomerSaleResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

import java.util.List;

@Repository
public interface CustomerRepo extends JpaRepository<Customer, Integer> {

    Page<Customer> findByCustomerCodeContainingIgnoreCaseOrNameContainingIgnoreCaseOrPhoneContainingIgnoreCaseOrEmailContainingIgnoreCase(
            String customerCode, String name, String phone, String email, Pageable pageable);

    Page<Customer> findByCustomerCodeContainingIgnoreCaseOrNameContainingIgnoreCaseOrPhoneContainingIgnoreCaseOrEmailContainingIgnoreCaseAndGender(
            String customerCode, String name, String phone, String email, String gender, Pageable pageable);

    Page<Customer> findByCustomerCodeContainingIgnoreCaseOrNameContainingIgnoreCaseOrPhoneContainingIgnoreCaseOrEmailContainingIgnoreCaseAndStatus(
            String customerCode, String name, String phone, String email, Boolean status, Pageable pageable);

    Page<Customer> findByCustomerCodeContainingIgnoreCaseOrNameContainingIgnoreCaseOrPhoneContainingIgnoreCaseOrEmailContainingIgnoreCaseAndGenderAndStatus(
            String customerCode, String name, String phone, String email, String gender, Boolean status, Pageable pageable);

    Page<Customer> findByGender(String gender, Pageable pageable);

    Page<Customer> findByStatus(Boolean status, Pageable pageable);

    Page<Customer> findByGenderAndStatus(String gender, Boolean status, Pageable pageable);

    boolean existsByUsername(String username);

    boolean existsByPhone(String phone);

    boolean existsByEmail(String email);

    Optional<Customer> findByEmail(String email);

    Optional<Customer> findByUsername(String username);

    @Query("""
                SELECT new com.poly.du_an_tot_nghiep_f6.response.CustomerSaleResponse(
                    c.id,
                    c.name,
                    c.phone,
                    c.email,
                    c.gender,
                    c.dateOfBirth,
                    a
                )
                FROM Customer c
                LEFT JOIN Address a ON a.customer.id = c.id
            """)
    List<CustomerSaleResponse> getCustomerResponse();

    @Query("""
        SELECT new com.poly.du_an_tot_nghiep_f6.response.CustomerSaleResponse(
            c.id,
            c.name,
            c.phone,
            c.email,
            c.gender,
            c.dateOfBirth,
            a
        )
        FROM Customer c
        LEFT JOIN Address a ON a.customer.id = c.id AND a.id = (
            SELECT MIN(a2.id) FROM Address a2 WHERE a2.customer.id = c.id
        )
        WHERE (c.name LIKE %:keyWord%
               OR c.phone LIKE %:keyWord%
               OR c.email LIKE %:keyWord%)
    """)
    List<CustomerSaleResponse> findCustomerResponse(String keyWord);


    @Query(" select c.vouchers from Customer c where c.id=?1")
    List<Voucher> getVouchers(Integer id);

}
