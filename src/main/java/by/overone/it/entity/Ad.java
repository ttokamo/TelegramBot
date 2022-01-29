package by.overone.it.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

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
    private String chatId;
    @Setter
    private String brand;
    @Setter
    private String model;
    @Setter
    private String mileage;
    @Setter
    private String year;
    @Setter
    private String price;
    @Setter
    private String description;
    @Setter
    private byte[] photo;
    @Setter
    private String telephone;
    @Setter
    private String status;
}
