package com.example.token.controller;

import com.example.token.model.RegisterRequest;
import com.example.token.model.WebResponse;
import com.example.token.services.UserServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

}
