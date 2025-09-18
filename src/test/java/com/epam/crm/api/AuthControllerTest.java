package com.epam.crm.api;

import com.epam.crm.service.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class AuthControllerTest {

    private MockMvc mvc;
    private AuthService authService;

    @BeforeEach
    void setup() {
        authService = Mockito.mock(AuthService.class);
        mvc = MockMvcBuilders
                .standaloneSetup(new AuthController(authService))
                .setControllerAdvice(new com.epam.crm.api.error.GlobalExceptionHandler())
                .build();
    }


    @Test
    void login_ok() throws Exception {
        doNothing().when(authService).authenticate("u","p");

        mvc.perform(get("/api/login")
                        .param("username","u").param("password","p"))
                .andExpect(status().isOk());
    }

    @Test
    void login_unauthorized() throws Exception {
        doThrow(new SecurityException("bad")).when(authService).authenticate(anyString(), anyString());

        mvc.perform(get("/api/login")
                        .param("username","u")
                        .param("password","bad"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void changePassword_ok() throws Exception {
        doNothing().when(authService).changePassword("u","old","new");

        String body = """
      {"username":"u","oldPassword":"old","newPassword":"new"}
    """;

        mvc.perform(put("/api/login/password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk());
    }

    @Test
    void changePassword_wrongOld_401() throws Exception {
        doThrow(new SecurityException("wrong old")).when(authService)
                .changePassword("u","bad","new");

        String body = """
      {"username":"u","oldPassword":"bad","newPassword":"new"}
    """;

        mvc.perform(put("/api/login/password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isUnauthorized());
    }
}
