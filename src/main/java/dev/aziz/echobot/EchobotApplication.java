package dev.aziz.echobot;

import dev.aziz.echobot.service.VkBotService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class EchobotApplication {

    public static void main(String[] args) {
        SpringApplication.run(EchobotApplication.class, args);
    }

    @Bean
    CommandLineRunner runBot(VkBotService botService) {
        return args -> botService.run();
    }

}
