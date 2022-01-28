package by.overon.it.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.math.BigDecimal;

@Entity
@Table
@NoArgsConstructor
@Getter
public class Ad {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", updatable = false, nullable = false, unique=true)
    private String id;
    @Setter
    private String user_id;
    @Setter
    private String brand;
    @Setter
    private String model;
    @Setter
    private String mileage;
    @Setter
    private String year;
    @Setter
    private BigDecimal price;
    @Setter
    private String text;
    @Setter
    private byte[] photo;
    @Setter
    private String telephone;
    @Setter
    private String status;
}
