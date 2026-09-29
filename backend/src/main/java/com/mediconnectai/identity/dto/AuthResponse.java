package com.mediconnectai.identity.dto;

import com.mediconnectai.identity.entity.User;

public record AuthResponse(String accessToken, String refreshToken, User user) {
}
