package com.example.token.controller;

import com.example.token.entity.Users;
import com.example.token.model.*;
import com.example.token.repository.UsersRepository;
import com.example.token.services.JWTServices;
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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class UsersControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private JWTServices jwtServices;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UsersRepository usersRepository;

    @InjectMocks
    private UserServicesImpl userServices;


    @BeforeEach
    void setUp() {
        usersRepository.deleteAll();
    }

    @Test
    void testAddNewUserSuccess() throws  Exception{
        Users users = new Users();
        users.setUsername("user_1");
        users.setPassword(passwordEncoder.encode("asdf"));
        users.setRole("admin");
        users.setCreatedAt(new Date());
        users.setUpdateAt(null);
        usersRepository.save(users);

        AddUserRequest request = new AddUserRequest();
        request.setUsername("user_test");
        request.setPassword("asdf");

        String token = jwtServices.generateToken(users.getUsername(), users.getRole());

        mockMvc.perform(
                post("/api/v1/users")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token)
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
    void testAddNewUserExist() throws  Exception{
        Users users = new Users();
        users.setUsername("user_1");
        users.setPassword(passwordEncoder.encode("asdf"));
        users.setRole("admin");
        users.setCreatedAt(new Date());
        users.setUpdateAt(null);
        usersRepository.save(users);

        AddUserRequest request = new AddUserRequest();
        request.setUsername("user_1");
        request.setPassword("asdf");

        String token = jwtServices.generateToken(users.getUsername(), users.getRole());

        mockMvc.perform(
                post("/api/v1/users")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token)
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
    void testAddNewUserBadRequest() throws  Exception{
        Users users = new Users();
        users.setUsername("user_1");
        users.setPassword(passwordEncoder.encode("asdf"));
        users.setRole("admin");
        users.setCreatedAt(new Date());
        users.setUpdateAt(null);
        usersRepository.save(users);

        RegisterRequest request = new RegisterRequest();
        request.setUsername("user_test");
        request.setPassword("");

        String token = jwtServices.generateToken(users.getUsername(), users.getRole());

        mockMvc.perform(
                post("/api/v1/users")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token)
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
                put("/api/v1/users/{id}", getIdSavedUsers)
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
                put("/api/v1/users/{id}", getIdSavedUsers)
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
            assertNull(response.getData());
        });
    }


    @Test
    void testUpdateNonExistUser() throws Exception {

        Users user = new Users();
        user.setUsername("user_test");
        user.setPassword("asdf");
        user.setCreatedAt(new Date());
        user.setUpdateAt(null);

        usersRepository.save(user);

        UsersUpdateRequest updateRequest = new UsersUpdateRequest();
        updateRequest.setUsername("user_test");
        updateRequest.setPassword("123");

        mockMvc.perform(
                put("/api/v1/users/{id}", 999)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest))
        ).andExpectAll(
                status().isNotFound()
        ).andDo(result -> {
            WebResponse<UsersUpdateRequest> response = objectMapper.readValue(
                    result.getResponse().getContentAsString(),
                    new TypeReference<WebResponse<UsersUpdateRequest>>() {}
            );

            assertNotNull(response.getMessage());
        });
    }

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


    @Test
    void getListUsersSuccess() throws  Exception{
        Users user_test1 = new Users();
        user_test1.setUsername("user_test1");
        user_test1.setPassword("asdf");
        user_test1.setCreatedAt(new Date());
        user_test1.setUpdateAt(null);
        usersRepository.save(user_test1);

        Users user_test2 = new Users();
        user_test2.setUsername("user_test2");
        user_test2.setPassword("asdf");
        user_test2.setCreatedAt(new Date());
        user_test2.setUpdateAt(null);
        usersRepository.save(user_test2);

        Users user_test3 = new Users();
        user_test3.setUsername("user_test3");
        user_test3.setPassword("asdf");
        user_test3.setCreatedAt(new Date());
        user_test3.setUpdateAt(null);

        usersRepository.save(user_test3);



        mockMvc.perform(
                get("/api/v1/users")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpectAll(
                status().isOk()
        ).andDo(result -> {
            WebResponse<List<UserResponse>> response = objectMapper.readValue(
                    result.getResponse().getContentAsString(),
                    new TypeReference<WebResponse<List<UserResponse>>>() {}
            );

            assertEquals("OK", response.getMessage());
            assertNotNull(response.getData());
        });
    }


    @Test
    void getListUsersEmpty() throws  Exception{
        mockMvc.perform(
                get("/api/v1/users")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpectAll(
                status().isOk()
        ).andDo(result -> {
            WebResponse<List<UserResponse>> response = objectMapper.readValue(
                    result.getResponse().getContentAsString(),
                    new TypeReference<WebResponse<List<UserResponse>>>() {}
            );

            assertEquals("OK", response.getMessage());
            assertNotNull(response.getData());
            assertTrue(response.getData().isEmpty());
        });
    }


    @Test
    void getUsersIdSuccess() throws  Exception{

        Users user = new Users();
        user.setUsername("user_test");
        user.setPassword("asdf");
        user.setCreatedAt(new Date());
        user.setUpdateAt(null);

        Users save =  usersRepository.save(user);
        Long user_id = save.getId();

        mockMvc.perform(
                get("/api/v1/users/{id}", user_id)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpectAll(
                status().isOk()
        ).andDo(result -> {
            WebResponse<UsersGetReponse> response = objectMapper.readValue(
                    result.getResponse().getContentAsString(),
                    new TypeReference<WebResponse<UsersGetReponse>>() {}
            );

            assertEquals("OK", response.getMessage());
            assertNotNull(response.getData());
        });
    }

    @Test
    void getUsersByIdNotFound() throws  Exception{

        Users user = new Users();
        user.setUsername("user_test");
        user.setPassword("asdf");
        user.setCreatedAt(new Date());
        user.setUpdateAt(null);

        usersRepository.save(user);


        mockMvc.perform(
                get("/api/v1/users/{id}", 999)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpectAll(
                status().isNotFound()
        ).andDo(result -> {
            WebResponse<String> response = objectMapper.readValue(
                    result.getResponse().getContentAsString(),
                    new TypeReference<WebResponse<String>>() {}
            );

            assertNotNull(response.getMessage());
        });
    }

}