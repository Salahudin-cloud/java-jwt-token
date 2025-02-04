package com.example.token.services.impl;

import com.example.token.entity.User;
import com.example.token.dto.*;
import com.example.token.repository.UsersRepository;
import com.example.token.services.UserServices;
import com.example.token.services.ValidatorServices;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserServicesImpl implements UserServices {

    @Autowired
    private UsersRepository usersRepository;

    @Autowired
    private ValidatorServices validatorServices;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void add(AddUserRequest request) {
        validatorServices.validate(request);

        if (usersRepository.existsByUsername(request.getUsername())){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Users already registered");
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole("user");
        user.setCreatedAt(new Date());
        user.setUpdatedAt(null);

        usersRepository.save(user);
    }


    @Override
    @Transactional
    public UserResponse update(long id, UsersUpdateRequest usersUpdateRequest) {
        validatorServices.validate(usersUpdateRequest);

        User user = findUsers(id);

        user.setUsername(usersUpdateRequest.getUsername());
        user.setPassword(passwordEncoder.encode(usersUpdateRequest.getPassword()));
        user.setRole(usersUpdateRequest.getRole());

        usersRepository.save(user);

        return UserResponse.builder()
                .id(user.getId())
                .username(usersUpdateRequest.getUsername())
                .password(passwordEncoder.encode(usersUpdateRequest.getPassword()))
                .role(usersUpdateRequest.getRole())
                .created_at(user.getCreatedAt())
                .update_at(user.getUpdatedAt())
                .build();
    }

    @Override
    public void delete(long id) {
        validatorServices.validate(id);
        User user = findUsers(id);

        usersRepository.delete(user);
    }



    @Override
    public List<UserResponse> list(ListUserRequest listUserRequest) {
        int currentPage = (listUserRequest.getCurrentPage() != null && listUserRequest.getCurrentPage() >= 0)
                ? listUserRequest.getCurrentPage()
                : 0;

        int itemPerPage = (listUserRequest.getItemPerPage() != null && listUserRequest.getItemPerPage() > 0)
                ? listUserRequest.getItemPerPage()
                : 10;


        Page<User> page = usersRepository.findAll(PageRequest.of(currentPage, itemPerPage));

        return page.getContent().stream()
                .map(user -> new UserResponse(
                        user.getId(),
                        user.getUsername(),
                        user.getPassword(),
                        user.getRole(),
                        user.getCreatedAt(),
                        user.getUpdatedAt(),
                        currentPage,
                        itemPerPage
                ))
                .collect(Collectors.toList());
    }

    @Override
    public UsersGetReponse getUser(long id) {
        User user = findUsers(id);
        return UsersGetReponse.builder()
                .username(user.getUsername())
                .password(user.getPassword())
                .role(user.getRole())
                .created_at(user.getCreatedAt())
                .update_at(user.getUpdatedAt())
                .build();
    }


    private User findUsers(long id) {
        return usersRepository.getUsersById(id).orElseThrow(() -> new EntityNotFoundException("User not found with id " + id));
    }
}
