package com.poly.du_an_tot_nghiep_f6.response;

import com.poly.du_an_tot_nghiep_f6.entity.ProductDetail;
import com.poly.du_an_tot_nghiep_f6.entity.Trade;
import jakarta.persistence.Column;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ErrorProductResponse {
    private int id;
    private ProductDetail productDetail;
    private int quantityReturn;
    private int quantityError;
    private String describe;
}
