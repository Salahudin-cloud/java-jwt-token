package com.example.token.repository;

import com.example.token.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsersRepository extends JpaRepository<User, Long> {

    boolean existsByUsername(String username);

    Optional<User> getUsersById(long id);

    User findByUsername(String username);
}
