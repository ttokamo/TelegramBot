package by.overone.it.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Component;

import javax.persistence.*;

@Entity
@Table(name = "BOT_STATUS")
@NoArgsConstructor
@Getter
public class BotStatus {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", updatable = false, nullable = false, unique = true)
    private Long id;
    @Setter
    @Column(name = "chat_id")
    private String chatId;
    @Setter
    private String status;
}
