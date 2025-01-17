package com.example.token.services;

import com.example.token.model.RegisterRequest;
import com.example.token.model.UserResponse;
import com.example.token.model.UsersUpdateRequest;

public interface UserServices {
    public  void  register(RegisterRequest request);
    public UserResponse update(long id , UsersUpdateRequest usersUpdateRequest);
}
