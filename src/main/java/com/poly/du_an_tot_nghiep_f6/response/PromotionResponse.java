package com.poly.du_an_tot_nghiep_f6.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PromotionResponse {
    private Integer id;  // ID tự động sinh

    private String code;  // Mã khuyến mãi 5 chữ số

    private String name;  // Tên khuyến mãi

    private String price;  // Giá trị khuyến mãi

    private String valueType;  // Loại giá trị (VND hoặc %)

    private Integer quantity;  // Số lượng khuyến mãi có thể áp dụng

    private String dateStart;  // Ngày bắt đầu

    private String dateEnd;  // Ngày kết thúc

    private String dateCreate;  // Ngày thêm

    private String dateUpdate;  // Ngày sửa

    private String status;

    private boolean condition;
}
