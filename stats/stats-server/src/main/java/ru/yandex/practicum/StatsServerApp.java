package ru.yandex.practicum;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.time.LocalDateTime;

@SpringBootApplication
public class StatsServerApp {
    public static void main(String[] args) {
        SpringApplication.run(StatsServerApp.class, args);
        System.out.println("Начало работы StatsServerApp в " + LocalDateTime.now());
    }
}