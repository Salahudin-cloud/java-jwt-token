package com.example.token.services.impl;

import com.example.token.entity.Users;
import com.example.token.model.ListUserRequest;
import com.example.token.model.RegisterRequest;
import com.example.token.model.UserResponse;
import com.example.token.model.UsersUpdateRequest;
import com.example.token.repository.UsersRepository;
import com.example.token.services.UserServices;
import com.example.token.services.ValidatorServices;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
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

        Users users = getUser(id);

        users.setUsername(usersUpdateRequest.getUsername());
        users.setPassword(usersUpdateRequest.getPassword());
        users.setUpdateAt(new Date());

        usersRepository.save(users);

        return UserResponse.builder()
                .username(usersUpdateRequest.getUsername())
                .password(usersUpdateRequest.getPassword())
                .build();
    }

    @Override
    public void delete(long id) {
        validatorServices.validate(id);
        Users users = getUser(id);

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
                        user.getUsername(),
                        user.getPassword(),
                        user.getCreatedAt(),
                        user.getUpdateAt(),
                        currentPage,
                        itemPerPage
                ))
                .collect(Collectors.toList());
    }


    private Users getUser(long id) {
        Users users = usersRepository.getUsersById(id).orElseThrow(() -> new EntityNotFoundException("User not found with id " + id));
        return users;
    }
}
