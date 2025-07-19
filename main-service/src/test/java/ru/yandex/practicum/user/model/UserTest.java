package ru.yandex.practicum.user.model;


import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class UserTest {
    @Test
    void testNoArgsConstructor() {
        var user = new User();
        assertThat(user.getId()).isNull();
        assertThat(user.getName()).isNull();
        assertThat(user.getEmail()).isNull();
    }

    @Test
    void testAllArgsConstructor() {
        var user = new User(123L, "John Doe", "john@example.com");
        assertThat(user.getId()).isEqualTo(123L);
        assertThat(user.getName()).isEqualTo("John Doe");
        assertThat(user.getEmail()).isEqualTo("john@example.com");
    }

    @Test
    void testSetterGetters() {
        var user = new User();
        user.setId(456L);
        user.setName("Jane");
        user.setEmail("jane@example.org");

        assertThat(user).usingRecursiveComparison()
                .isEqualTo(new User(456L, "Jane", "jane@example.org"));
    }
}