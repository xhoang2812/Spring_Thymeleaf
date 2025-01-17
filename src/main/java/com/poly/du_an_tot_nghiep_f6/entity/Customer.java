package com.poly.du_an_tot_nghiep_f6.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "customer")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Customer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    Integer id;

    @NotBlank(message = "Mã khách hàng không được để trống")
    @Column(name = "customer_code")
    String customerCode;

    @Column(name = "photo")
    String photo;

    @NotBlank(message = "Tên không được để trống")
    @Column(name = "name", columnDefinition = "NVARCHAR(50)")
    String name;

    @Column(name = "gender", columnDefinition = "NVARCHAR(50)")
    String gender;

    @Pattern(regexp = "\\d{10}", message = "Số điện thoại phải có 10 chữ số")
    @Column(name = "phone")
    String phone;

    @Email(message = "Email không hợp lệ")
    @NotBlank(message = "Email không được để trống")
    @Column(name = "email")
    String email;

    @NotBlank(message = "Username không được để trống")
    @Column(name = "username")
    String username;

    @NotBlank(message = "Password không được để trống")
    @Column(name = "password")
    String password;

    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<Address> addresses;

    @Column(name = "date_of_birth")
    @DateTimeFormat(pattern = "dd/MM/yyyy")
    @NotNull(message = "Ngày sinh không được để trống")
    LocalDate dateOfBirth;

    @Column(name = "created_date", updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    Date createdDate;

    @Column(name = "updated_date")
    @Temporal(TemporalType.TIMESTAMP)
    Date updatedDate;

    @ManyToMany(fetch = FetchType.EAGER)
    private List<Voucher> vouchers = new ArrayList<>();

    @Column(name = "status")
    boolean status;

    @PrePersist
    protected void onCreate() {
        createdDate = new Date();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedDate = new Date();
    }

    @Override
    public String toString() {
        return "Customer{" +
               "id=" + id +
               ", customerCode='" + customerCode + '\'' +
               ", name='" + name + '\'' +
               ", email='" + email + '\'' +
               ", phone='" + phone + '\'' +
               ", status=" + status +
               // Không gọi addresses.toString() ở đây
               '}';
    }

    public List<Voucher> getVouchers() {
        if(vouchers != null){
            vouchers.removeIf(voucher -> voucher.getDateEnd().before(new Date()));
        }
        return vouchers;
    }
}

