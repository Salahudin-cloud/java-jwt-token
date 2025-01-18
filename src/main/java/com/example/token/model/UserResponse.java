package com.example.token.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserResponse {
    private Long id;
    private  String username;
    private String password;
    private String role;
    private Date created_at;
    private Date update_at;
    private Integer currentPage = 0;
    private Integer itemPerPage = 10;
}
