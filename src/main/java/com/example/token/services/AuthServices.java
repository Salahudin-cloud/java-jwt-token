package com.example.token.services;


import com.example.token.model.LoginRequest;
import com.example.token.model.LoginResponse;
import com.example.token.model.RegisterRequest;

public interface AuthServices {

    public LoginResponse login(LoginRequest loginRequest);

    public void register(RegisterRequest request);
}
