package az.code.telegrambot;

import az.code.telegrambot.repo.LanguageRepo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
//@EnableCaching
public class TelegramBotApplication {
    public static void main(String[] args) {
        SpringApplication.run(TelegramBotApplication.class, args);
    }

}
