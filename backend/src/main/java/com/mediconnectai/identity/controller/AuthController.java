package com.mediconnectai.identity.controller;
import com.mediconnectai.identity.dto.AuthResponse;
import com.mediconnectai.identity.dto.LoginRequest;
import com.mediconnectai.identity.dto.RegisterRequest;
import com.mediconnectai.identity.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {
    private final AuthService service;
    public AuthController(AuthService service) { this.service = service; }
    @PostMapping("/login") public AuthResponse login(@Valid @RequestBody LoginRequest request) { return service.login(request); }
    @PostMapping("/register") @ResponseStatus(HttpStatus.CREATED) public AuthResponse register(@Valid @RequestBody RegisterRequest request) { return service.register(request); }
}
