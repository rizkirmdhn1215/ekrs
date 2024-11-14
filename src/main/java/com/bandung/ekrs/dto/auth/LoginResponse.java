package com.bandung.ekrs.dto.auth;

import com.bandung.ekrs.model.enums.AccountRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginResponse {
    private Integer id;
    private String username;
    private String email;
    private AccountRole role;
    private String token;
} 