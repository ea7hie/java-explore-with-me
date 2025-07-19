package ru.yandex.practicum.compilation.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.yandex.practicum.compilation.dto.CompilationDto;
import ru.yandex.practicum.compilation.service.CompilationService;

import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CompilationPublicController.class)
public class CompilationPublicControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CompilationService compilationService;

    private CompilationDto compilationDto;

    @BeforeEach
    void setUp() {
        compilationDto = new CompilationDto(List.of(), 1L, false, "Test Compilation");
        when(compilationService.getCompilationById(1L)).thenReturn(compilationDto);
    }

    @Test
    void testFindCompilations_defaults() throws Exception {
        List<CompilationDto> mockList = List.of(compilationDto);
        when(compilationService.findCompilations(null, 0, 10)).thenReturn(mockList);

        mockMvc.perform(get("/compilations"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));
        verify(compilationService).findCompilations(null, 0, 10);
    }

    @Test
    void testFindCompilations_pinnedTrue() throws Exception {
        when(compilationService.findCompilations(true, 0, 10))
                .thenReturn(Collections.emptyList());

        mockMvc.perform(get("/compilations").param("pinned", "true"))
                .andExpect(status().isOk());
        verify(compilationService).findCompilations(true, 0, 10);
    }

    @Test
    void testFindCompilations_pagination() throws Exception {
        mockMvc.perform(get("/compilations")
                        .param("from", "5")
                        .param("size", "2"))
                .andExpect(status().isOk());
        verify(compilationService).findCompilations(null, 5, 2);
    }

    @Test
    void testGetCompilationById_success() throws Exception {
        mockMvc.perform(get("/compilations/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.title").value("Test Compilation"));

        verify(compilationService).getCompilationById(1L);
    }
}