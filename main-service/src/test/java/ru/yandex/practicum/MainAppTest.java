package ru.yandex.practicum;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

@SpringBootTest
@ActiveProfiles("test")
@EnableJpaAuditing
@ContextConfiguration(classes = MainApp.class)
class MainAppTest {

    @Test
    void contextLoads() {
    }
}