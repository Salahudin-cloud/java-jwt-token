package com.example.token.controller;

import com.example.token.entity.Users;
import com.example.token.model.RegisterRequest;
import com.example.token.model.UsersUpdateRequest;
import com.example.token.model.WebResponse;
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
import org.springframework.test.web.servlet.MockMvc;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class UsersControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;


    @Autowired
    private UsersRepository usersRepository;

    @InjectMocks
    private UserServicesImpl userServices;


    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        usersRepository.deleteAll();
    }

    @Test
    void testRegisterSuccess() throws  Exception{
        RegisterRequest request = new RegisterRequest();
        request.setUsername("user_test1");
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


    @Test
    void testUpdateSuccess() throws  Exception{
        Users user = new Users();
        user.setUsername("user_test");
        user.setPassword("asdf");
        user.setCreatedAt(new Date());
        user.setUpdateAt(null);

        Users saved = usersRepository.save(user);
        long getIdSavedUsers = saved.getId();

        UsersUpdateRequest updateRequest = new UsersUpdateRequest();
        updateRequest.setUsername("user_test");
        updateRequest.setPassword("123");

        mockMvc.perform(
                patch("/api/v1/users/{id}", getIdSavedUsers)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest))
        ).andExpectAll(
                status().isOk()
        ).andDo(result -> {
            WebResponse<UsersUpdateRequest> response = objectMapper.readValue(
                    result.getResponse().getContentAsString(),
                    new TypeReference<WebResponse<UsersUpdateRequest>>() {}
            );

            assertEquals("OK", response.getMessage());
            assertNotNull(response.getData());
            assertEquals("user_test", response.getData().getUsername());
            assertEquals("123", response.getData().getPassword());
        });
    }

    @Test
    void testUpdateBadRequest() throws  Exception{
        Users user = new Users();
        user.setUsername("user_test");
        user.setPassword("asdf");
        user.setCreatedAt(new Date());
        user.setUpdateAt(null);

        Users saved = usersRepository.save(user);
        long getIdSavedUsers = saved.getId();

        UsersUpdateRequest updateRequest = new UsersUpdateRequest();
        updateRequest.setUsername("");
        updateRequest.setPassword("");

        mockMvc.perform(
                patch("/api/v1/users/{id}", getIdSavedUsers)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest))
        ).andExpectAll(
                status().isBadRequest()
        ).andDo(result -> {
            WebResponse<UsersUpdateRequest> response = objectMapper.readValue(
                    result.getResponse().getContentAsString(),
                    new TypeReference<WebResponse<UsersUpdateRequest>>() {}
            );


            assertNotNull(response.getMessage());

        });
    }


//    @Test
//    void testUpdateNonExistUser() throws Exception {
//        // Create a new user
//        Users user = new Users();
//        user.setUsername("user_test");
//        user.setPassword("asdf");
//        user.setCreatedAt(new Date());
//        user.setUpdateAt(null);
//
//        // Mock the save operation to return the user with a generated ID
//        Users savedUser = new Users();
//        savedUser.setId(1L);  // Assign an ID here
//        savedUser.setUsername(user.getUsername());
//        savedUser.setPassword(user.getPassword());
//        savedUser.setCreatedAt(user.getCreatedAt());
//        savedUser.setUpdateAt(user.getUpdateAt());
//
//        // Mock the save method to return savedUser
//        Mockito.when(usersRepository.save(user)).thenReturn(savedUser);
//
//        // Save the user and check if the ID is assigned
//        Users result = usersRepository.save(user);
//        assertNotNull(result.getId(), "User ID should not be null after saving.");
//
//        // Create the update request
//        UsersUpdateRequest updateRequest = new UsersUpdateRequest();
//        updateRequest.setUsername("user_test");
//        updateRequest.setPassword("123");
//
//        // Set up a non-existent user scenario
//        long id = result.getId() + 1;  // Non-existent user ID
//
//        // Mock the getUsersById method to return empty
//        Mockito.when(usersRepository.getUsersById(id)).thenReturn(Optional.empty());
//
//        // Perform the patch request and verify the result
//        mockMvc.perform(
//                patch("/api/v1/users/{id}", id)
//                        .accept(MediaType.APPLICATION_JSON)
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(updateRequest))
//        ).andExpectAll(
//                status().isNotFound()
//        ).andDo(test -> {
//            WebResponse<UsersUpdateRequest> response = objectMapper.readValue(
//                    test.getResponse().getContentAsString(),
//                    new TypeReference<WebResponse<UsersUpdateRequest>>() {}
//            );
//
//            assertNotNull(response.getMessage());
//        });
//    }

    @Test
    void deleteUserSuccess() throws  Exception{
        Users user = new Users();
        user.setUsername("user_test");
        user.setPassword("asdf");
        user.setCreatedAt(new Date());
        user.setUpdateAt(null);

        Users saved = usersRepository.save(user);
        long getIdSavedUsers = saved.getId();


        mockMvc.perform(
                delete("/api/v1/users/{id}", getIdSavedUsers)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpectAll(
                status().isOk()
        ).andDo(result -> {
            WebResponse<UsersUpdateRequest> response = objectMapper.readValue(
                    result.getResponse().getContentAsString(),
                    new TypeReference<WebResponse<UsersUpdateRequest>>() {}
            );

            assertEquals("OK", response.getMessage());
        });
    }


}