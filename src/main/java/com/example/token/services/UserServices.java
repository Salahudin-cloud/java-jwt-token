package com.example.token.services;

import com.example.token.model.ListUserRequest;
import com.example.token.model.RegisterRequest;
import com.example.token.model.UserResponse;
import com.example.token.model.UsersUpdateRequest;

import java.util.List;

public interface UserServices {
    public  void  register(RegisterRequest request);

    public UserResponse update(long id , UsersUpdateRequest usersUpdateRequest);
    public void delete(long id);

    public List<UserResponse> list(ListUserRequest listUserRequest);
}
