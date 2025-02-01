package com.example.token.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UsersGetReponse {
    private String username;
    private String password;
    private String role;
    private Date created_at;
    private Date update_at;
}
