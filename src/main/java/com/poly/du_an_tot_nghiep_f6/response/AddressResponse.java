package com.poly.du_an_tot_nghiep_f6.response;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class AddressResponse {
    private int id;
    private String address;
    private String nameCustomer;
    private String phone;

    public AddressResponse(int id, String ward, String district, String province, String name, String phone) {
        this.id = id;
        this.address = ward + " - " + district + " - " + province;
        this.nameCustomer = name;
        this.phone = phone;
    }
}
