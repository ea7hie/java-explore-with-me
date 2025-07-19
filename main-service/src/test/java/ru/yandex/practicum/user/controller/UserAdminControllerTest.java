package ru.yandex.practicum.user.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import ru.yandex.practicum.user.dto.UserDto;
import ru.yandex.practicum.user.dto.UserDtoPost;
import ru.yandex.practicum.user.service.UserService;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserAdminController.class)
public class UserAdminControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    private UserDtoPost validUserDto;

    @BeforeEach
    void beforeEach() {
        validUserDto = new UserDtoPost("John Doe", "john@example.com");
    }


    @Test
    void create_User_Success() throws Exception {
        UserDto postResponse = new UserDto(1L, validUserDto.getName(), validUserDto.getEmail());

        when(userService.add(validUserDto)).thenReturn(postResponse);

        ResultActions res = mockMvc.perform(
                post("/admin/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"John Doe\", \"email\":\"john@example.com\"}")
        );

        res.andExpect(status().isCreated())
                .andExpect(content().json(
                        "{\"id\":1, \"name\":\"John Doe\", \"email\":\"john@example.com\"}"
                ));

        verify(userService, times(1)).add(validUserDto);
    }

    @Test
    void create_User_InvalidValidation() throws Exception {
        ResultActions res = mockMvc.perform(
                post("/admin/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"\", \"email\":\"john@example.com\"}")
        );

        res.andExpect(status().isBadRequest());
    }

    @Test
    void delete_User() throws Exception {
        mockMvc.perform(delete("/admin/users/123"))
                .andExpect(status().isNoContent());

        verify(userService).delete(123L);
    }
}