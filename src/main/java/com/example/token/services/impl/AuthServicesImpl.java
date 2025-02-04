package com.example.token.services.impl;

import com.example.token.entity.User;
import com.example.token.dto.LoginRequest;
import com.example.token.dto.LoginResponse;
import com.example.token.dto.RegisterRequest;
import com.example.token.repository.UsersRepository;
import com.example.token.services.AuthServices;
import com.example.token.services.JWTServices;
import com.example.token.services.ValidatorServices;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Date;

@RequiredArgsConstructor
@Service
public class AuthServicesImpl implements AuthServices {

    @Autowired
    private final UsersRepository usersRepository;

    @Autowired
    private ValidatorServices validatorServices;

    @Autowired
    private JWTServices jwtServices;

    @Autowired
    private PasswordEncoder passwordEncoder;


    @Override
    public LoginResponse login( LoginRequest loginRequest) {
        validatorServices.validate(loginRequest);

        User user = usersRepository.findByUsername(loginRequest.getUsername());

        if (user == null) {
            throw  new EntityNotFoundException("Invalid username or password");
        }

        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            throw  new EntityNotFoundException("Invalid username or password");
        }

        String token = jwtServices.generateToken(user.getUsername(), user.getRole());

        return LoginResponse.builder().token(token).build();
    }

    @Override
    public void register(RegisterRequest request) {
        validatorServices.validate(request);

        if (usersRepository.existsByUsername(request.getUsername())){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Users already registered");
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole("user");

        usersRepository.save(user);
    }
}
