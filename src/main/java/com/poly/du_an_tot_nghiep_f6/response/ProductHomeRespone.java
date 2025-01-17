package com.poly.du_an_tot_nghiep_f6.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductHomeRespone {
    private Integer id;
    private String name;
    private Long sumQuantity;
    private Long count;
    private String image;
    private Double maxPrice;

}
