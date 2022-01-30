package by.overone.it.application;

import by.overone.it.tg_bot.Bot;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.generics.BotSession;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

@SpringBootApplication
@EntityScan(basePackages = "by.overone.it.entity")
@ComponentScan(basePackages = "by.overone.it")
@EnableJpaRepositories(basePackages = "by.overone.it.repository")
public class Application {
    @SneakyThrows
    public static void main(String[] args) {
        SpringApplication.run(Application.class);
    }

    @Bean
    public CommandLineRunner commandLineRunner(@Autowired TelegramBotsApi telegramBotsApi, @Autowired Bot bot) {
        return args -> {
            telegramBotsApi.registerBot(bot);
        };
    }

    @SneakyThrows
    @Bean
    public TelegramBotsApi telegramBotsApi(Bot bot) {
        return new TelegramBotsApi(DefaultBotSession.class);
    }
}
