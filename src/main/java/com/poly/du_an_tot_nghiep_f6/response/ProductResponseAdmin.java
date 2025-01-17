package com.poly.du_an_tot_nghiep_f6.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ProductResponseAdmin {
    private Integer id;
    private String name;
    private String gender;
    private String category;
    private String material;
    private String brand;
    private String style;
    private Long sumQuantity;
    private Integer status;

}
