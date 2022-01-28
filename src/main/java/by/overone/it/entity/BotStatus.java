package by.overone.it.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Component;

import javax.persistence.*;

@Entity
@Table
@NoArgsConstructor
@Getter
@Component
public class BotStatus {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", updatable = false, nullable = false, unique = true)
    private Long id;
    @Setter
    @Column(name = "user_id")
    private String userId;
    @Setter
    private String status;
}
