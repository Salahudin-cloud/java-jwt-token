package com.example.token.services.impl;

import com.example.token.entity.Users;
import com.example.token.dto.LoginRequest;
import com.example.token.dto.LoginResponse;
import com.example.token.dto.RegisterRequest;
import com.example.token.repository.UsersRepository;
import com.example.token.services.AuthServices;
import com.example.token.services.JWTServices;
import com.example.token.services.ValidatorServices;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Date;

@Service
public class AuthServicesImpl implements AuthServices {

    @Autowired
    private UsersRepository usersRepository;

    @Autowired
    private ValidatorServices validatorServices;

    @Autowired
    private JWTServices jwtServices;

    @Autowired
    private PasswordEncoder passwordEncoder;


    @Override
    public LoginResponse login(LoginRequest loginRequest) {
        validatorServices.validate(loginRequest);

        Users users = usersRepository.findByUsername(loginRequest.getUsername());

        if (users == null) {
            throw  new EntityNotFoundException("Invalid username or password");
        }

        if (!passwordEncoder.matches(loginRequest.getPassword(), users.getPassword())) {
            throw  new EntityNotFoundException("Invalid username or password");
        }

        String token = jwtServices.generateToken(users.getUsername(), users.getRole());

        return LoginResponse.builder().token(token).build();
    }

    @Override
    public void register(RegisterRequest request) {
        validatorServices.validate(request);

        if (usersRepository.existsByUsername(request.getUsername())){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Users already registered");
        }

        Users user = new Users();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole("user");
        user.setCreatedAt(new Date());
        user.setUpdateAt(null);

        usersRepository.save(user);
    }
}
