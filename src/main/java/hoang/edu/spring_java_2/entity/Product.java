package hoang.edu.spring_java_2.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Id")
    private Integer id;

    @Column(name = "Name")
    private String name;

    @Column(name = "Price")
    private Double price;

    @Column(name = "Image")
    private String image;

    @Column(name = "Detail")
    private String detail;

    @Column(name = "Quantity")
    private Integer quantity;

    @Column(name = "Sold")
    private Integer sold;

    @Column(name = "Factory")
    private String factory;

    @Column(name = "Target")
    private String target;
}
