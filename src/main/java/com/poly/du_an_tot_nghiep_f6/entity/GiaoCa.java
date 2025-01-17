package com.poly.du_an_tot_nghiep_f6.entity;

import com.poly.du_an_tot_nghiep_f6.common.GiaoCaEnum;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "giaoca")
public class GiaoCa {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // Khóa chính tự động tăng

    @ManyToOne
    @JoinColumn(name = "id_nhan_vien", nullable = false)
    private Employee employee; // Liên kết với bảng nhân viên


    private String magiaoca;
    private String tenca;

    private int slhoadondathanhtoan =0;
    private int slhoadonchuathanhtoan =0;
    private Double tienmatdauca; // Tiền mặt đầu ca

    private Double tienphatsinh = 0.0; // Tiền phát sinh (mặc định là 0)

    private Double tienmattrongca = 0.0; // Tiền mặt trong ca (mặc định là 0)

    private Double chuyenkhoantrongca = 0.0; // Chuyển khoản trong ca (mặc định là 0)

    private Double xacnhantienmat;

    private Double xacnhantienchuyenkhoan; // Xác nhận tiền chuyển khoản

    private Double sotienthucnhan = 0.0; // Số tiền thực nhận

    private Double sotienconthieu = 0.0; // Số tiền còn thiếu

    @Column(columnDefinition = "nvarchar(max)")
    private String ghichu; // Ghi chú

    @Column(columnDefinition = "nvarchar(max)")
    private String lichSuGiaoCa; // Lịch sử giao ca

    private String nhan_vien_ban_giao;

    @Enumerated(EnumType.STRING) // Lưu trữ giá trị enum dưới dạng chuỗi
    private GiaoCaEnum trang_thai;

    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime thoigianvaoca; // Thời gian bắt đầu ca

    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime thoigianketthucca; // Thời gian hientai
}
