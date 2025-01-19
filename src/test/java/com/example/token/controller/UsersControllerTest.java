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

    private static final String TEST_USERNAME = "user_test";
    private static final String TEST_PASSWORD = "asdf";

    private String getToken(String username, String role) {
        return jwtServices.generateToken(username, role);
    }

    private Users createUserTest(String username, String password) {
        return createUserTest(username, password, "user");
    }

    private Users createUserTest(String username, String password, String role) {
        Users users = new Users();
        users.setUsername(username);
        users.setPassword(passwordEncoder.encode(password));
        users.setRole(role);
        users.setCreatedAt(new Date());
        users.setUpdateAt(null);
        return usersRepository.save(users);
    }


    @Test
    void testAddNewUserSuccess() throws  Exception{
        Users users = createUserTest(TEST_USERNAME, TEST_PASSWORD, "admin");

        AddUserRequest request = new AddUserRequest();
        request.setUsername("user_test1");
        request.setPassword("asdf");


        mockMvc.perform(
                post("/api/v1/users")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + getToken(users.getUsername(), users.getRole()))
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
        Users users = createUserTest(TEST_USERNAME, TEST_PASSWORD, "admin");

        AddUserRequest request = new AddUserRequest();
        request.setUsername("user_test");
        request.setPassword("asdf");


        mockMvc.perform(
                post("/api/v1/users")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + getToken(users.getUsername(), users.getRole()))
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
        Users users = createUserTest(TEST_USERNAME, TEST_PASSWORD, "admin");

        RegisterRequest request = new RegisterRequest();
        request.setUsername("user_test");
        request.setPassword("");


        mockMvc.perform(
                post("/api/v1/users")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + getToken(users.getUsername(), users.getRole()))
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
        Users users = createUserTest(TEST_USERNAME, TEST_PASSWORD, "admin");

        Users users1 = createUserTest("user", "123");

        long usersId = users1.getId();

        UsersUpdateRequest updateRequest = new UsersUpdateRequest();
        updateRequest.setUsername("user_test1");
        updateRequest.setPassword("asdf");
        updateRequest.setRole("user");

        mockMvc.perform(
                put("/api/v1/users/{id}", usersId)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + getToken(users.getUsername(), users.getRole()))
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
            assertEquals("user", response.getData().getRole());
            assertTrue(passwordEncoder.matches("asdf", response.getData().getPassword()));

        });

        usersRepository.deleteAll();
    }

    @Test
    void testUpdateBadRequest() throws  Exception{
        Users users = createUserTest(TEST_USERNAME, TEST_PASSWORD, "admin");
        Users users1 = createUserTest("user_1", "asdf");
        long usersId = users1.getId();

        UsersUpdateRequest updateRequest = new UsersUpdateRequest();
        updateRequest.setUsername("asd123");
        updateRequest.setPassword("");
        updateRequest.setRole("user");

        mockMvc.perform(
                put("/api/v1/users/{id}", usersId)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + getToken(users.getUsername(), users.getRole()))
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

        usersRepository.deleteAll();
    }


    @Test
    void testUpdateNonExistUser() throws Exception {

        Users users = createUserTest(TEST_USERNAME, TEST_PASSWORD, "admin");

        createUserTest("user_test1", "123");

        UsersUpdateRequest updateRequest = new UsersUpdateRequest();
        updateRequest.setUsername("user_test");
        updateRequest.setPassword("123");
        updateRequest.setRole("user");

        mockMvc.perform(
                put("/api/v1/users/{id}", 999)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + getToken(users.getUsername(), users.getRole()))
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

        usersRepository.deleteAll();
    }

    @Test
    void deleteUserSuccess() throws  Exception{
        Users users = createUserTest(TEST_USERNAME, TEST_PASSWORD, "admin");

        long getIdSavedUsers = users.getId();


        mockMvc.perform(
                delete("/api/v1/users/{id}", getIdSavedUsers)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + getToken(users.getUsername(), users.getRole()))
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

        usersRepository.deleteAll();
    }


    @Test
    void getListUsersSuccess() throws  Exception{
       Users admin = createUserTest(TEST_USERNAME, TEST_PASSWORD, "admin");

       createUserTest("user_test1", "123");
       createUserTest("user_test2", "123");
       createUserTest("user_test3", "123");


        mockMvc.perform(
                get("/api/v1/users")
                        .accept(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + getToken(admin.getUsername(), admin.getRole()))
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
        usersRepository.deleteAll();
    }



    @Test
    void getUsersIdSuccess() throws  Exception{

        Users users = createUserTest(TEST_USERNAME, TEST_PASSWORD, "admin");

        Long user_id = users.getId();

        mockMvc.perform(
                get("/api/v1/users/{id}", user_id)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + getToken(users.getUsername(), users.getRole()))
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
        usersRepository.deleteAll();
    }

    @Test
    void getUsersByIdNotFound() throws  Exception{

        Users users = createUserTest(TEST_USERNAME, TEST_PASSWORD, "admin");

        mockMvc.perform(
                get("/api/v1/users/{id}", 999)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + getToken(users.getUsername(), users.getRole()))
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
        usersRepository.deleteAll();
    }




}