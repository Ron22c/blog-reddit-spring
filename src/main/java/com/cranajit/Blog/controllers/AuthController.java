package com.cranajit.Blog.controllers;

import com.cranajit.Blog.dto.LoginRequest;
import com.cranajit.Blog.dto.LoginResponse;
import com.cranajit.Blog.dto.RegisterRequest;
import com.cranajit.Blog.exception.BloggingException;
import com.cranajit.Blog.services.AuthService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/api/auth/")
@AllArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping(value = "/signup", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> signUp(@RequestBody RegisterRequest registerRequest) {
        authService.signup(registerRequest);
        return new ResponseEntity<String>("Registration successful", HttpStatus.OK);
    }

    @GetMapping(value = "/accountverification/{token}")
    public ResponseEntity<String> activateUser(@PathVariable(value = "token") String token) {
        authService.verifyAccount(token);
        return new ResponseEntity<String>("User activated successfully", HttpStatus.OK);
    }

    @PostMapping(value = "/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        try {
            LoginResponse response = authService.login(loginRequest);
            return ResponseEntity.ok().body(response);
        } catch (BloggingException e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
