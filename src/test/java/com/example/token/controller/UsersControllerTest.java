package com.example.token.controller;

import com.example.token.entity.Users;
import com.example.token.model.RegisterRequest;
import com.example.token.model.WebResponse;
import com.example.token.repository.UsersRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class UsersControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UsersRepository usersRepository;

    @Autowired
    private ObjectMapper objectMapper;

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
                post("/api/v1/users")
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
    }

    @Test
    void testRegisterExist() throws  Exception{
        Users user = new Users();
        user.setUsername("user_test");
        user.setPassword("asdf");
        user.setCreatedAt(new Date());
        user.setUpdateAt(null);
        usersRepository.save(user);

        RegisterRequest request = new RegisterRequest();
        request.setUsername("user_test");
        request.setPassword("asdf");

        mockMvc.perform(
                post("/api/v1/users")
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
    }

    @Test
    void testRegisterBadRequest() throws  Exception{
        RegisterRequest request = new RegisterRequest();
        request.setUsername("user_test");
        request.setPassword("");

        mockMvc.perform(
                post("/api/v1/users")
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
    }
}