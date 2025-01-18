package com.example.token.services;

import com.example.token.entity.Users;
import com.example.token.model.CustomUserDetails;
import com.example.token.repository.UsersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UsersRepository usersRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Users users = usersRepository.findByUsername(username);

        if (users == null) {
            throw  new UsernameNotFoundException("User not found");
        }

        return new CustomUserDetails(users);
    }
}
