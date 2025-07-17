package ru.yandex.practicum.compilation.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.yandex.practicum.compilation.dto.CompilationDto;
import ru.yandex.practicum.compilation.dto.NewCompilationDto;
import ru.yandex.practicum.compilation.dto.UpdateCompilationRequest;
import ru.yandex.practicum.compilation.service.CompilationService;

import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CompilationAdminController.class)
class CompilationAdminControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CompilationService compilationService;

    private NewCompilationDto newCompilationDto;
    private CompilationDto compilationDto;
    private UpdateCompilationRequest updateRequest;

    @BeforeEach
    void setUp() {
        newCompilationDto = new NewCompilationDto();
        newCompilationDto.setTitle("Test Compilation");
        newCompilationDto.setPinned(false);
        newCompilationDto.setEvents(Collections.singletonList(1L));

        compilationDto = new CompilationDto(List.of(), 1L, false, "Test Compilation");

        updateRequest = new UpdateCompilationRequest();
        updateRequest.setTitle("Updated Compilation");
        updateRequest.setPinned(true);

        when(compilationService.add(newCompilationDto)).thenReturn(compilationDto);
        when(compilationService.update(eq(1L), any(UpdateCompilationRequest.class))).thenReturn(compilationDto);
    }

    @Test
    void createCompilation_success() throws Exception {
        String content = objectMapper.writeValueAsString(newCompilationDto);

        mockMvc.perform(post("/admin/compilations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content))
                .andExpect(status().isCreated())
                .andExpect(content().json(objectMapper.writeValueAsString(compilationDto)));

        verify(compilationService).add(newCompilationDto);
    }

    @Test
    void createCompilation_badRequest() throws Exception {
        NewCompilationDto invalid = new NewCompilationDto();

        mockMvc.perform(post("/admin/compilations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalid)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateCompilation_success() throws Exception {
        String content = objectMapper.writeValueAsString(updateRequest);

        mockMvc.perform(patch("/admin/compilations/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content))
                .andExpect(status().isOk());

        verify(compilationService).update(1L, updateRequest);
    }

    @Test
    void updateCompilation_negativeId() throws Exception {
        mockMvc.perform(patch("/admin/compilations/-5"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deleteCompilation_success() throws Exception {
        mockMvc.perform(delete("/admin/compilations/1"))
                .andExpect(status().isNoContent());

        verify(compilationService).delete(1L);
    }

    @Test
    void deleteCompilation_negativeId() throws Exception {
        mockMvc.perform(delete("/admin/compilations/-3"))
                .andExpect(status().isBadRequest());
    }
}