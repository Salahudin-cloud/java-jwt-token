package com.example.token.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AddUserRequest {
    @NotBlank(message = "usernbam tidak boleh kosong")
    @Size(max = 50)
    private String username;
    @NotBlank
    private String password;
}
