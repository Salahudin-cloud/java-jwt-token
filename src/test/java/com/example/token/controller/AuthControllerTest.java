package com.example.token.controller;

import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;
import com.example.token.entity.Users;
import com.example.token.model.*;
import com.example.token.repository.UsersRepository;
import com.example.token.services.impl.UserServicesImpl;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
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

    @BeforeEach
    void setUp() {
        usersRepository.deleteAll();
    }

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
        Users user = new Users();
        user.setUsername("user_test");
        user.setPassword("asdf");
        user.setRole("user");
        user.setCreatedAt(new Date());
        user.setUpdateAt(null);
        usersRepository.save(user);

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
        Users user = new Users();
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
        Users user = new Users();
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
}