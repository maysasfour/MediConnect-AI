package com.mediconnect.identity.web;

import com.mediconnect.identity.dto.AuthDtos.LoginRequest;
import com.mediconnect.identity.dto.AuthDtos.RefreshRequest;
import com.mediconnect.identity.dto.AuthDtos.RegisterRequest;
import com.mediconnect.identity.dto.AuthDtos.TokenResponse;
import com.mediconnect.identity.dto.AuthDtos.UserSummary;
import com.mediconnect.identity.service.AuthService;
import com.mediconnect.shared.security.AppUserPrincipal;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@Tag(name = "Authentication", description = "Registration, login and token rotation")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    @Operation(summary = "Register a new patient account")
    public ResponseEntity<UserSummary> register(@Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.register(request));
    }

    @PostMapping("/login")
    @Operation(summary = "Authenticate and receive access + refresh tokens")
    public ResponseEntity<TokenResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @PostMapping("/refresh")
    @Operation(summary = "Rotate a refresh token for a new token pair")
    public ResponseEntity<TokenResponse> refresh(@Valid @RequestBody RefreshRequest request) {
        return ResponseEntity.ok(authService.refresh(request.refreshToken()));
    }

    @PostMapping("/logout")
    @Operation(summary = "Revoke all refresh tokens for the current user")
    public ResponseEntity<Void> logout(@AuthenticationPrincipal AppUserPrincipal principal) {
        authService.logout(principal.getUserId());
        return ResponseEntity.noContent().build();
    }
}
