package com.postech.identityservice.api;

public class ApiPatterns {
    public static final String PASSWORD_PATTERN = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#$%^&+=])(?=\\S+$).{8,}$";
    public static final String USERNAME_PATTERN = "^[a-zA-Z0-9._-]{5,}+$";
}
