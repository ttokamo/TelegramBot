package by.overone.it.application;

import by.overone.it.dao.BotStatusDao;
import by.overone.it.entity.BotStatus;
import by.overone.it.repository.BotStatusRepository;
import by.overone.it.tg_bot.Bot;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnSingleCandidate;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import javax.persistence.EntityManagerFactory;

@SpringBootApplication
@EnableJpaRepositories(basePackages = "by.overone.it.repository")
@EntityScan(basePackages = "by.overone.it.entity")
@ComponentScan(basePackages = "by.overone.it.dao")
@EnableAutoConfiguration(exclude={DataSourceAutoConfiguration.class})
@ConditionalOnSingleCandidate(EntityManagerFactory.class)
public class Application {
    @SneakyThrows
    public static void main(String[] args) {
        SpringApplication.run(Application.class);
        TelegramBotsApi telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
        context.scan("by.overone.it");
        context.refresh();
        for (String name : context.getBeanDefinitionNames()) {
            System.out.println(name);
        }
        Bot bot = context.getBean("bot", Bot.class);
        telegramBotsApi.registerBot(bot);
    }
}
