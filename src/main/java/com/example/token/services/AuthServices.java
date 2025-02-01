package com.example.token.services;


import com.example.token.dto.LoginRequest;
import com.example.token.dto.LoginResponse;
import com.example.token.dto.RegisterRequest;

public interface AuthServices {

    public LoginResponse login(LoginRequest loginRequest);

    public void register(RegisterRequest request);
}
