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
public class OderDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Id")
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "ProductID", referencedColumnName = "Id")
    private Product product;

    @ManyToOne
    @JoinColumn(name = "OderID", referencedColumnName = "Id")
    private Orders order;

    @Column(name = "Quantity")
    private Integer quantity;

    @Column(name = "Price")
    private Double price;
}
