package ru.yandex.practicum;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@SpringBootApplication
@Component
@Data
@AllArgsConstructor
public class StatsServerApp {
    public static void main(String[] args) {
        SpringApplication.run(StatsServerApp.class, args);
        System.out.println("Начало работы StatsServerApp в " + LocalDateTime.now());
    }
}