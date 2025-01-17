package com.poly.du_an_tot_nghiep_f6.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.Objects;

@Entity
@Table(name = "promotion_detail")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class PromotionDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id; // id_promotion sẽ là khóa chính

    @ManyToOne
    @JoinColumn(name = "id_promotion", referencedColumnName = "id")
    private Promotion promotion; // Liên kết với bảng Promotion

    @ManyToOne
    @JoinColumn(name = "id_product_detail", referencedColumnName = "id")
    private ProductDetail productDetail; // Liên kết với bảng Promotion

    private int giaMoi;
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PromotionDetail that = (PromotionDetail) o;
        return Objects.equals(id, that.id); // So sánh thuộc tính id (hoặc các thuộc tính cần thiết)
    }
}

