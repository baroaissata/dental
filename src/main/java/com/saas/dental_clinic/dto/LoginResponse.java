package com.saas.dental_clinic.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.util.Set;  // 
@Data
@AllArgsConstructor
public class LoginResponse {
    private String token;
    private String type = "Bearer";
    private Long id;
    private String email;
    private Set<String> roles;
}