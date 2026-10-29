package com.jobportal.dto;

import com.jobportal.model.Role;

public class AuthResponse {

    private Long id;
    private String email;
    private String fullName;
    private Role role;
    private String companyName;
    private String token;
    private String message;

    public AuthResponse() {
    }

    public AuthResponse(Long id, String email, String fullName, Role role, String companyName, String token, String message) {
        this.id = id;
        this.email = email;
        this.fullName = fullName;
        this.role = role;
        this.companyName = companyName;
        this.token = token;
        this.message = message;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
