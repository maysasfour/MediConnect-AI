package com.mediconnectai.identity.service;
import com.mediconnectai.identity.dto.AuthResponse;
import com.mediconnectai.identity.dto.LoginRequest;
import com.mediconnectai.identity.dto.RegisterRequest;
import com.mediconnectai.identity.entity.User;
import com.mediconnectai.security.JwtTokenProvider;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Service;
@Service
public class AuthService {
    private final Map<String, User> users = new ConcurrentHashMap<>();
    private final JwtTokenProvider tokens;
    public AuthService(JwtTokenProvider tokens) {
        this.tokens = tokens;
        seed("Dr. Lina Haddad", "doctor@mediconnect.ai", "doctor123", "DOCTOR");
        seed("Clinic Admin", "admin@mediconnect.ai", "admin123", "ADMIN");
        seed("Omar Nasser", "patient@mediconnect.ai", "patient123", "PATIENT");
    }
    public AuthResponse login(LoginRequest request) {
        User user = users.get(request.email().toLowerCase());
        if (user == null || !MessageDigest.isEqual(user.passwordHash().getBytes(StandardCharsets.UTF_8), hash(request.password()).getBytes(StandardCharsets.UTF_8))) {
            throw new IllegalArgumentException("Invalid email or password");
        }
        return response(user);
    }
    public AuthResponse register(RegisterRequest request) {
        String email = request.email().toLowerCase();
        if (users.containsKey(email)) throw new IllegalArgumentException("Email is already registered");
        User user = new User("u-" + UUID.randomUUID().toString().substring(0, 8), request.fullName(), email,
            hash(request.password()), request.role() == null ? "PATIENT" : request.role().toUpperCase(), true, Instant.now());
        users.put(email, user); return response(user);
    }
    public java.util.List<User> users() { return users.values().stream().sorted(java.util.Comparator.comparing(User::fullName)).toList(); }
    private void seed(String name, String email, String password, String role) {
        users.put(email, new User("u-" + role.toLowerCase(), name, email, hash(password), role, true, Instant.now()));
    }
    private AuthResponse response(User user) { return new AuthResponse(tokens.create(user.id(), user.role()), tokens.createRefreshToken(user.id()), user); }
    private String hash(String value) {
        try {
            byte[] bytes = MessageDigest.getInstance("SHA-256").digest(value.getBytes(StandardCharsets.UTF_8));
            return java.util.HexFormat.of().formatHex(bytes);
        } catch (NoSuchAlgorithmException exception) { throw new IllegalStateException(exception); }
    }
}
