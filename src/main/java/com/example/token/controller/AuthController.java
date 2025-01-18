package com.example.token.controller;


import com.example.token.model.LoginRequest;
import com.example.token.model.LoginResponse;
import com.example.token.model.RegisterRequest;
import com.example.token.model.WebResponse;
import com.example.token.services.AuthServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth/")
public class AuthController {
    @Autowired
    private AuthServices authServices;

    @PostMapping(
            path = "/login",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public LoginResponse login(@RequestBody LoginRequest request){
        return authServices.login(request);
    }

    @PostMapping(
            path = "/register",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<String> login(@RequestBody RegisterRequest request){
         authServices.register(request);
         return WebResponse.<String>builder()
                 .message("OK")
                 .build();
    }
}
