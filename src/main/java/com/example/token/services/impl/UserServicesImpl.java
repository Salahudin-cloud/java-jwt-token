package com.example.token.services.impl;

import com.example.token.entity.Users;
import com.example.token.model.*;
import com.example.token.repository.UsersRepository;
import com.example.token.services.UserServices;
import com.example.token.services.ValidatorServices;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
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

        Users user = new Users();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole("user");
        user.setCreatedAt(new Date());
        user.setUpdateAt(null);

        usersRepository.save(user);
    }

    @Override
    @Transactional
    public UserResponse update(long id, UsersUpdateRequest usersUpdateRequest) {
        validatorServices.validate(usersUpdateRequest);

        Users users = findUsers(id);

        users.setUsername(usersUpdateRequest.getUsername());
        users.setPassword(passwordEncoder.encode(usersUpdateRequest.getPassword()));
        users.setRole(usersUpdateRequest.getRole());
        users.setUpdateAt(new Date());

        usersRepository.save(users);

        return UserResponse.builder()
                .username(usersUpdateRequest.getUsername())
                .password(passwordEncoder.encode(usersUpdateRequest.getPassword()))
                .build();
    }

    @Override
    public void delete(long id) {
        validatorServices.validate(id);
        Users users = findUsers(id);

        usersRepository.delete(users);
    }



    @Override
    public List<UserResponse> list(ListUserRequest listUserRequest) {
        int currentPage = (listUserRequest.getCurrentPage() != null && listUserRequest.getCurrentPage() >= 0)
                ? listUserRequest.getCurrentPage()
                : 0;

        int itemPerPage = (listUserRequest.getItemPerPage() != null && listUserRequest.getItemPerPage() > 0)
                ? listUserRequest.getItemPerPage()
                : 10;


        Page<Users> page = usersRepository.findAll(PageRequest.of(currentPage, itemPerPage));

        return page.getContent().stream()
                .map(user -> new UserResponse(
                        user.getId(),
                        user.getUsername(),
                        user.getPassword(),
                        user.getRole(),
                        user.getCreatedAt(),
                        user.getUpdateAt(),
                        currentPage,
                        itemPerPage
                ))
                .collect(Collectors.toList());
    }

    @Override
    public UsersGetReponse getUser(long id) {
        Users user = findUsers(id);
        return UsersGetReponse.builder()
                .username(user.getUsername())
                .password(user.getPassword())
                .role(user.getRole())
                .created_at(user.getCreatedAt())
                .update_at(user.getUpdateAt())
                .build();
    }


    private Users findUsers(long id) {
        Users users = usersRepository.getUsersById(id).orElseThrow(() -> new EntityNotFoundException("User not found with id " + id));
        return users;
    }
}
