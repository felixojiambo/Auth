package com.LDLS.Auth.dtos;

import com.LDLS.Auth.models.Users;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ReqRes {

    private int statusCode;
    private String error;
    private String message;
    private String jwt; // JWT token
    private String type; // Token type, e.g., "Bearer"
    private String bearer; // Bearer token
    private String role; // User role
    private Map<String, String> header; // Header information
    private String refreshToken; // Refresh token
    private String expirationTime; // Token expiration time
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private String email;
    private String password;
    private Users users;
}
