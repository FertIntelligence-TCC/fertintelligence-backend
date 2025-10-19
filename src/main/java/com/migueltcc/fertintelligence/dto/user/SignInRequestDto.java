package com.migueltcc.fertintelligence.dto.user;

import lombok.Data;

@Data
public class SignInRequestDto {
    private String username;
    private String password;
}