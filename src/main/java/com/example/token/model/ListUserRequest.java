package com.example.token.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ListUserRequest {
    private Integer currentPage = 0;
    private Integer itemPerPage = 10;
}
