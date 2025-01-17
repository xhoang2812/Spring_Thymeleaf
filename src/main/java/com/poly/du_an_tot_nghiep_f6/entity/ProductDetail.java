package com.poly.du_an_tot_nghiep_f6.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;

import lombok.experimental.FieldDefaults;


import java.util.Date;
import java.util.List;

@Entity
@Table(name = "product_detail")
@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    Integer id;

    @Column(name = "code")
    String productCode;

    @Column(name = "qrCode")
    String qrCode;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_size", referencedColumnName = "id")
    Size size;

    @ManyToOne
    @JoinColumn(referencedColumnName = "id", name = "id_product")
    Product product;


    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_color", referencedColumnName = "id")
    Color color;

    @ManyToOne
    @JoinColumn(name = "id_image", referencedColumnName = "id")
    Image image;

    @Column(name = "price")
    Double price;

    @Column(name = "quantity")
    Integer quantity;

    @Column(name = "weight")
    Double weight;

    @Column(name = "date_create")
    Date dateCreate;

    @Column(name = "date_update")
    Date dateUpdate;

    @Column(name = "status")
    Integer status;

    @OneToOne
    @JsonIgnore
    PromotionDetail promotionDetail;

    @Override
    public String toString() {
        return "ProductDetail{" +
               "id=" + id +
               ", code='" + productCode + '\'' +
               ", qrCode='" + qrCode + '\'' +
               ", size=" + size.getName() +
               ", product=" + product.getName() +
               ", color=" + color.getName() +
               ", image=" + image.getUrl1() +
               ", price=" + price +
               ", quantity=" + quantity +
               ", weight=" + weight +
               ", dateCreate=" + dateCreate +
               ", dateUpdate=" + dateUpdate +
               ", status=" + status +
               '}';
    }


    public String getFullName() {
        return "ID " + id + ": "+product.getName()+ " - "+size.getName()+" - "+color.getName();
    }
}
