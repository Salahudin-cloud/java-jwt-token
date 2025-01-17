package com.example.token.services.impl;

import com.example.token.entity.Users;
import com.example.token.model.RegisterRequest;
import com.example.token.model.UserResponse;
import com.example.token.model.UsersUpdateRequest;
import com.example.token.repository.UsersRepository;
import com.example.token.services.UserServices;
import com.example.token.services.ValidatorServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.Date;

@Service
public class UserServicesImpl implements UserServices {

    @Autowired
    private UsersRepository usersRepository;

    @Autowired
    private ValidatorServices validatorServices;


    @Override
    @Transactional
    public void register(RegisterRequest request) {
        validatorServices.validate(request);

        if (usersRepository.existsByUsername(request.getUsername())){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Users already registered");
        }

        Users user = new Users();
        user.setUsername(request.getUsername());
        user.setPassword(request.getPassword());
        user.setCreatedAt(new Date());
        user.setUpdateAt(null);

        usersRepository.save(user);
    }

    @Override
    @Transactional
    public UserResponse update(long id, UsersUpdateRequest usersUpdateRequest) {
        validatorServices.validate(usersUpdateRequest);

        Users data = usersRepository.getReferenceById(id);

    }
}
