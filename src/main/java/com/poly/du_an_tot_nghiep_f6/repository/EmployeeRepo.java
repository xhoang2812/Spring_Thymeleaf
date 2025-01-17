package com.poly.du_an_tot_nghiep_f6.repository;

import com.poly.du_an_tot_nghiep_f6.entity.Customer;
import com.poly.du_an_tot_nghiep_f6.entity.Employee;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
public interface EmployeeRepo extends JpaRepository<Employee, Integer> {

    Page<Employee> findByEmployeeCodeContainingIgnoreCaseOrNameContainingIgnoreCase(
            String employeeCode, String name, Pageable pageable);

    Page<Employee> findByEmployeeCodeContainingIgnoreCaseOrNameContainingIgnoreCaseAndGender(
            String employeeCode, String name, String gender, Pageable pageable);

    Page<Employee> findByEmployeeCodeContainingIgnoreCaseOrNameContainingIgnoreCaseAndStatus(
            String employeeCode, String name, Boolean status, Pageable pageable);

    Page<Employee> findByEmployeeCodeContainingIgnoreCaseOrNameContainingIgnoreCaseAndGenderAndStatus(
            String employeeCode, String name, String gender, Boolean status, Pageable pageable);

    Page<Employee> findByGender(String gender, Pageable pageable);

    Page<Employee> findByStatus(Boolean status, Pageable pageable);

    Page<Employee> findByGenderAndStatus(String gender, Boolean status, Pageable pageable);

    boolean existsByUsername(String username);
    boolean existsByUsernameAndIdNot(String username, Integer id);
    boolean existsByPhone(String phone);
    boolean existsByEmail(String email);

    Optional<Employee> findByEmail(String email);
    Optional<Employee> findByUsername(String username);


}
