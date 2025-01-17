package com.poly.du_an_tot_nghiep_f6.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "promotion")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Promotion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;  // ID tự động sinh

    @Column(nullable = false, unique = true)
    private String code;  // Mã khuyến mãi 5 chữ số

    @Column(nullable = false, length = 255, columnDefinition = "nvarchar(max)")
    private String name;  // Tên khuyến mãi

    @Column(nullable = false)
    private Integer price;  // Giá trị khuyến mãi

    @Column(nullable = false)
    private String valueType;  // Loại giá trị (VND hoặc %)

    @Column(nullable = false)
    private int quantity;  // Số lượng khuyến mãi có thể áp dụng

    private Integer quantityUse = 0;  // Số lượng khuyến mãi đã áp dụng

    @Column(nullable = false)
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private LocalDateTime dateStart;  // Ngày bắt đầu

    @Column(nullable = false)
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private LocalDateTime dateEnd;  // Ngày kết thúc

    @Column(nullable = false)
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private LocalDateTime dateCreate;  // Ngày thêm

    @Column
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private LocalDateTime dateUpdate;  // Ngày sửa

    @Column(nullable = false, columnDefinition = "nvarchar(225)")
    private String status;  // Trạng thái (0: Chưa diễn ra, 1: Đang diễn ra, 2: Đã hết hạn,4: Tạm hết)

    private boolean condition;
}
