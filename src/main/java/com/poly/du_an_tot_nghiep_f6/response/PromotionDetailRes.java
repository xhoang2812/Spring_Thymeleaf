package com.poly.du_an_tot_nghiep_f6.response;

import com.poly.du_an_tot_nghiep_f6.entity.ProductDetail;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PromotionDetailRes {
    private Integer id;
    private ProductDetail productDetail;
    private String giaCu;
    private String giaMoi;
}
