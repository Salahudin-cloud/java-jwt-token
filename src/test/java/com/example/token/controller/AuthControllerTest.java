package com.example.token.controller;

import com.example.token.entity.User;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

import static org.hamcrest.Matchers.nullValue;
import static org.junit.jupiter.api.Assertions.*;

import com.example.token.dto.*;
import com.example.token.repository.UsersRepository;
import com.example.token.services.impl.UserServicesImpl;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.Date;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
class AuthControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UsersRepository usersRepository;

    @InjectMocks
    private UserServicesImpl userServices;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    void testRegisterSuccess() throws  Exception{
        RegisterRequest request = new RegisterRequest();
        request.setUsername("user_test");
        request.setPassword("asdf");

        mockMvc.perform(
                post("/api/v1/auth/register")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
        ).andExpectAll(
                status().isOk()
        ).andDo(result -> {
            WebResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new
                    TypeReference<WebResponse<String>>() {
                    });

            assertEquals("OK", response.getMessage());
        });
        usersRepository.deleteAll();
   }

    @Test
    void testRegisterUserExist() throws  Exception{

        usersRepository.save(
                User.builder()
                        .username("user_test")
                        .password(passwordEncoder.encode("asdf"))
                        .role("user")
                .build());


        RegisterRequest request = new RegisterRequest();
        request.setUsername("user_test1");
        request.setPassword("asdf");

        mockMvc.perform(
                post("/api/v1/auth/register")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
        ).andExpectAll(
                status().isOk()
        ).andDo(result -> {
            WebResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new
                    TypeReference<WebResponse<String>>() {
                    });

            assertEquals("OK", response.getMessage());
        });

        usersRepository.deleteAll();
    }

    @Test
    void testRegisterBadRequest() throws  Exception{
        RegisterRequest request = new RegisterRequest();
        request.setUsername("");
        request.setPassword("asdf");

        mockMvc.perform(
                post("/api/v1/auth/register")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
        ).andExpectAll(
                status().isBadRequest()
        ).andDo(result -> {
            WebResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new
                    TypeReference<WebResponse<String>>() {
                    });

            assertNotNull(response.getMessage());
        });

        usersRepository.deleteAll();
    }

    @Test
    void testLoginSuccess() throws  Exception{
        User user = new User();
        user.setUsername("user_test");
        user.setPassword(passwordEncoder.encode("asdf"));
        user.setRole("user");
        user.setCreatedAt(new Date());
        user.setUpdateAt(null);
        usersRepository.save(user);

        LoginRequest request = new LoginRequest();
        request.setUsername("user_test");
        request.setPassword("asdf");

        mockMvc.perform(
                post("/api/v1/auth/login")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
        ).andExpectAll(
                status().isOk(),
                jsonPath("$.token").exists(),
                jsonPath("$.token").isNotEmpty()
        ).andDo(result -> {
            LoginResponse response = objectMapper.readValue(result.getResponse().getContentAsString(), new
                    TypeReference<LoginResponse>() {
                    });

            assertNotNull(response.getToken());
        });

        usersRepository.delete(user);
    }


    @Test
    void testLoginBadRequest() throws  Exception{
        User user = new User();
        user.setUsername("user_test");
        user.setPassword(passwordEncoder.encode("asdf"));
        user.setRole("user");
        user.setCreatedAt(new Date());
        user.setUpdateAt(null);
        usersRepository.save(user);

        LoginRequest request = new LoginRequest();
        request.setUsername("user_test");
        request.setPassword("");

        mockMvc.perform(
                post("/api/v1/auth/login")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
        ).andExpectAll(
                status().isBadRequest()
        ).andDo(result -> {
            LoginResponse response = objectMapper.readValue(result.getResponse().getContentAsString(), new
                    TypeReference<LoginResponse>() {
                    });

            assertNull(response.getToken());
        });

        usersRepository.delete(user);
    }


    @Test
    @WithMockUser(username = "user_test", roles = "USER")
    public void testLogout() throws Exception {
        mockMvc.perform(
                        post("/api/v1/auth/logout")
                )
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.cookie().value("token", nullValue()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("OK"));
    }


}