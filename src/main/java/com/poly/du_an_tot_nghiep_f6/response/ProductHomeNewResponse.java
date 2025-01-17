package com.poly.du_an_tot_nghiep_f6.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductHomeNewResponse {
    private Integer id;
    private String name;
    private Long sumQuantity;
    private String image;
    private Double maxPrice;
}
