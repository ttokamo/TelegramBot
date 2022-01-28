package by.overone.it.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.stereotype.Component;

import javax.persistence.*;

@Entity
@Table(name = "BOT_STATUS")
@NoArgsConstructor
@Getter
public class BotStatus {

    @Id
    @GeneratedValue(generator = "uuid-generator")
    @GenericGenerator(name = "uuid-generator", strategy = "uuid")
    private String id;
    @Setter
    @Column(name = "chat_id")
    private String chatId;
    @Setter
    private String status;
}
