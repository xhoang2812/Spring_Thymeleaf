package com.poly.du_an_tot_nghiep_f6.response;

import com.poly.du_an_tot_nghiep_f6.entity.Address;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Data
@NoArgsConstructor
public class CustomerSaleResponse {
    private Integer id;
    private String name;
    private String phone;
    private String email;
    private String gender;
    private String dateOfBirth;
    private String address;

    public CustomerSaleResponse(Integer id, String name, String phone, String email, String gender, LocalDate dateOfBirth, Address address) {
        this.id = id;
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.gender = gender;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        this.dateOfBirth = dateOfBirth.format(formatter);

        if (address != null) {
            this.address = address.getWard() + " - " + address.getDistrict() + " - " + address.getProvince();
        } else {
            this.address = "Không có địa chỉ"; // Xử lý trường hợp không có địa chỉ
        }
    }


}
