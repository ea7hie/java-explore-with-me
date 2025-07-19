package ru.yandex.practicum.user.dto;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class UserShortDtoTest {

    @Test
    void testNoArgsConstructor() {
        var dto = new UserShortDto();
        assertThat(dto.getId()).isNull();
        assertThat(dto.getName()).isNull();
    }

    @Test
    void testAllArgsConstructor() {
        var dto = new UserShortDto(123L, "Test Name");
        assertThat(dto).usingRecursiveComparison()
                .isEqualTo(new UserShortDto(123L, "Test Name"));
    }

    @Test
    void testSettersAndGetters() {
        var dto = new UserShortDto();

        dto.setId(456L);
        dto.setName("Another Name");

        assertThat(dto).usingRecursiveComparison()
                .isEqualTo(new UserShortDto(456L, "Another Name"));
    }

    @Test
    void testEqualsAndHashCode() {
        var dto1 = new UserShortDto(789L, "User");
        var dto2 = new UserShortDto(789L, "User");
        var differentId = new UserShortDto(999L, "User");
        var differentName = new UserShortDto(789L, "Another");

        assertThat(dto1).isEqualTo(dto2);
        assertThat(dto1).isNotEqualTo(differentId);
        assertThat(dto1).isNotEqualTo(differentName);
        assertThat(dto1.hashCode()).isEqualTo(dto2.hashCode());
    }
}