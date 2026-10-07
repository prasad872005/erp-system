package com.erp.erp_backend.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AuthRequest {

    private String username;
    private String password;
}