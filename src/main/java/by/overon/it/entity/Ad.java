package by.overon.it.entity;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "CARS")
@NoArgsConstructor
@Data
public class Ad {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String id;
    private String user_id;
    private String brand;
    private String model;
    private String mileage;
    private String year;
    private BigDecimal price;
    private String text;
    private byte[] photo;
    private String telephone;
    private String status;
}
