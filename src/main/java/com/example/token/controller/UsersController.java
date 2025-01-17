package com.example.token.controller;

import com.example.token.model.*;
import com.example.token.services.UserServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class UsersController {

    @Autowired
    private UserServices userServices;

    @PostMapping(
            path = "/users",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<String> registerUser(@RequestBody RegisterRequest registerRequest) {
        userServices.register(registerRequest);
        return WebResponse.<String>builder().message("OK").build();
    }

    @PutMapping(
            path = "/users/{id}",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<UserResponse> updateUser(
            @PathVariable("id") long id,
            @RequestBody UsersUpdateRequest usersUpdateRequest) {
      UserResponse userResponse =  userServices.update(id, usersUpdateRequest);
        return WebResponse.<UserResponse>builder()
                .message("OK")
                .data(userResponse)
                .build();
    }

    @DeleteMapping(
            path = "/users/{id}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<String> deleteUser(@PathVariable long id) {
        userServices.delete(id);
        return WebResponse.<String>builder()
                .message("OK")
                .build();
    }

    @GetMapping(
            path = "/users",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<List<UserResponse>> listUsers(
            @RequestParam(value = "currentPage", defaultValue = "0") Integer currentPage ,
            @RequestParam(value = "itemPerPage", defaultValue = "10") Integer itemPerPage){
        ListUserRequest listUserRequest = new ListUserRequest(currentPage, itemPerPage);
        List<UserResponse> userResponses = userServices.list(listUserRequest);

        return WebResponse.<List<UserResponse>>builder()
                .message("OK")
                .data(userResponses)
                .build();
    }

    @GetMapping(
            path = "/users/{id}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<UsersGetReponse> get(@PathVariable long id){
       UsersGetReponse user =  userServices.getUser(id);
        return WebResponse.<UsersGetReponse>builder()
                .message("OK")
                .data(user)
                .build();
    }

}
