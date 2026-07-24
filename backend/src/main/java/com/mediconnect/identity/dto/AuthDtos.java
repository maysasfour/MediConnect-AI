package com.mediconnect.identity.dto;

import com.mediconnect.identity.domain.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.util.Set;
import java.util.UUID;

/** Request/response payloads for the authentication endpoints. */
public final class AuthDtos {

    private AuthDtos() {
    }

    public record RegisterRequest(
            @NotNull UUID clinicId,
            @NotBlank @Email String email,
            @Pattern(regexp = "^\\+962[0-9]{9}$", message = "Must be a Jordanian +962 number")
            String phone,
            @NotBlank @Size(min = 12, max = 128,
                    message = "Password must be at least 12 characters") String password,
            @NotBlank String fullNameEn,
            String fullNameAr) {
    }

    public record LoginRequest(
            @NotBlank @Email String email,
            @NotBlank String password) {
    }

    public record RefreshRequest(
            @NotBlank String refreshToken) {
    }

    public record TokenResponse(
            String accessToken,
            String refreshToken,
            String tokenType,
            long expiresInSeconds) {
    }

    public record UserSummary(
            UUID id,
            UUID clinicId,
            String email,
            String fullNameEn,
            String fullNameAr,
            Set<Role> roles,
            String preferredLanguage) {
    }
}
