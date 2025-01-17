package com.poly.du_an_tot_nghiep_f6.response;

import com.poly.du_an_tot_nghiep_f6.entity.ProductDetail;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PromotionDetailResponse {
    private int id;
    private String code;  // Mã khuyến mãi 5 chữ số
    private String name;  // Tên khuyến mãi
    private Integer price;  // Giá trị khuyến mãi
    private String valueType;  // Loại giá trị (VND hoặc %)
    private Integer quantity;  // Số lượng khuyến mãi có thể áp dụng
    private LocalDateTime dateStart;  // Ngày bắt đầu
    private LocalDateTime dateEnd;  // Ngày kết thúc
    private LocalDateTime dateCreate;  // Ngày thêm
    private LocalDateTime dateUpdate;  // Ngày sửa
    private Integer status;  // Trạng thái (0: Chưa diễn ra, 1: Đang diễn ra, 2: Đã hết hạn)
//    private Integer idProductDetail;
   private List<ProductDetail> productDetailList = new ArrayList<>(); // ID sanr phẩm chi tiết

}
