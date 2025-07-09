package ru.yandex.practicum;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class StatisticDtoGetTest {

    @Test
    public void testAllArgsConstructor() {
        StatisticDtoGet dto = new StatisticDtoGet("TestApp", "/home", 123L);

        assertEquals("TestApp", dto.getApp());
        assertEquals("/home", dto.getUri());
        assertEquals(123L, dto.getHits());
    }

    @Test
    public void testNoArgsConstructorAndSetters() {
        StatisticDtoGet dto = new StatisticDtoGet();

        dto.setApp("MyApp");
        dto.setUri("/api/test");
        dto.setHits(999L);

        assertEquals("MyApp", dto.getApp());
        assertEquals("/api/test", dto.getUri());
        assertEquals(999L, dto.getHits());
    }

    @Test
    public void testEqualsAndHashCode() {
        StatisticDtoGet dto1 = new StatisticDtoGet("App", "/path", 100L);
        StatisticDtoGet dto2 = new StatisticDtoGet("App", "/path", 100L);

        assertEquals(dto1, dto2);
        assertEquals(dto1.hashCode(), dto2.hashCode());
    }

    @Test
    public void testToString() {
        StatisticDtoGet dto = new StatisticDtoGet("App", "/endpoint", 10L);
        String str = dto.toString();

        assertTrue(str.contains("App"));
        assertTrue(str.contains("/endpoint"));
        assertTrue(str.contains("10"));
    }
}