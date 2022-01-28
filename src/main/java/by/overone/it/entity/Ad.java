package by.overone.it.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "ADS")
@NoArgsConstructor
@Getter
public class Ad {

    @Id
    @GeneratedValue(generator = "uuid-generator")
    @GenericGenerator(name = "uuid-generator", strategy = "uuid")
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
