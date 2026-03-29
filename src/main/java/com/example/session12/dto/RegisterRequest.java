package com.example.session12.dto;

import lombok.Data;

@Data
public class RegisterRequest {
    private String email;
    private String password;
    private String phone;
}