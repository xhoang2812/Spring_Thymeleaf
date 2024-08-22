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
public class Orders {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Id")
    private Integer id;

    @Column(name = "TotalPrice")
    private Double totalPrice;

    @ManyToOne
    @JoinColumn(name = "UserID", referencedColumnName = "Id")
    private User user;

}
