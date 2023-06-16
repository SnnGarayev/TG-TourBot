package az.code.telegrambot.controllers;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

@Configuration
@Getter
public class SpringConfig {

    @Value("${bot.username}")
    public String botname;

    @Value("${bot.token}")
    public String bottoken;



}
