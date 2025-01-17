package com.poly.du_an_tot_nghiep_f6.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.Date;

@Entity
@Table(name = "employee")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Employee {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    Integer id;

    @Column(name = "employee_code", unique = true)
    @NotBlank(message = "Mã nhân viên không được để trống")
    String employeeCode;

    String photo;

    @Column(name = "name", columnDefinition = "NVARCHAR(225)")
    @NotBlank(message = "Tên nhân viên không được để trống")
    String name;

    @Column(name = "phone")
    @NotBlank(message = "Số điện thoại không được để trống")
    @Pattern(regexp = "\\d{10}", message = "Số điện thoại phải có 10 hoặc 11 chữ số")
    String phone;

    @Column(name = "province", columnDefinition = "NVARCHAR(225)")
    @NotBlank(message = "Tỉnh chỉ không được để trống")
    String province;

    @Column(name = "district", columnDefinition = "NVARCHAR(225)")
    @NotBlank(message = "Huyện chỉ không được để trống")
    String district;

    @Column(name = "ward", columnDefinition = "NVARCHAR(225)")
    String ward;

    @Column(name = "email")
    @NotBlank(message = "Email không được để trống")
    @Email(message = "Email không hợp lệ")
    String email;

    @Column(name = "username")
    @NotBlank(message = "Username không được để trống")
    String username;

    @Column(name = "password")
    @NotBlank(message = "Password không được để trống")
    String password;

    @Column(name = "gender", columnDefinition = "NVARCHAR(225)")
    @NotBlank(message = "Giới tính không được để trống")
    String gender;

    @Column(name = "date_of_birth")
    @DateTimeFormat(pattern = "dd/MM/yyyy")
    @NotNull(message = "Ngày sinh không được để trống")
    LocalDate dateOfBirth;

    @Column(name = "position", columnDefinition = "NVARCHAR(225)")
    @NotBlank(message = "Chức vụ không được để trống")
    String position;

    @Column(name = "date_create", updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    Date dateCreate;

    @Column(name = "date_update")
    @Temporal(TemporalType.TIMESTAMP)
    Date dateUpdate;

    @Column(name = "status")
    boolean status;

    @PrePersist
    protected void onCreate() {
        this.dateCreate = new Date();
    }

    @PreUpdate
    protected void onUpdate() {
        this.dateUpdate = new Date();
    }
}

