package com.mediconnect.identity.service;

import com.mediconnect.identity.domain.Role;
import com.mediconnect.identity.domain.UserAccount;
import com.mediconnect.identity.dto.AuthDtos.LoginRequest;
import com.mediconnect.identity.dto.AuthDtos.RegisterRequest;
import com.mediconnect.identity.dto.AuthDtos.TokenResponse;
import com.mediconnect.identity.dto.AuthDtos.UserSummary;
import com.mediconnect.identity.repo.UserAccountRepository;
import com.mediconnect.identity.service.RefreshTokenService.RotationResult;
import com.mediconnect.shared.error.DomainExceptions.ConflictException;
import com.mediconnect.shared.error.DomainExceptions.ForbiddenOperationException;
import com.mediconnect.shared.error.DomainExceptions.ResourceNotFoundException;
import java.util.EnumSet;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Registration, login and token refresh. Login failures are deliberately
 * indistinguishable (same message for unknown user vs. wrong password) so the
 * API does not disclose which accounts exist.
 */
@Service
public class AuthService {

    private static final Logger log = LoggerFactory.getLogger(AuthService.class);

    private final UserAccountRepository users;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;

    public AuthService(UserAccountRepository users, PasswordEncoder passwordEncoder,
                       JwtService jwtService, RefreshTokenService refreshTokenService) {
        this.users = users;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.refreshTokenService = refreshTokenService;
    }

    @Transactional
    public UserSummary register(RegisterRequest request) {
        if (users.existsByEmailIgnoreCase(request.email())) {
            throw new ConflictException("An account with this email already exists");
        }
        UserAccount user = new UserAccount(
                request.clinicId(),
                request.email().toLowerCase(),
                passwordEncoder.encode(request.password()),
                request.fullNameEn(),
                EnumSet.of(Role.PATIENT));
        user.setFullNameAr(request.fullNameAr());
        user.setPhoneE164(request.phone());
        users.save(user);
        log.info("Registered new patient account for clinic {}", request.clinicId());
        return toSummary(user);
    }

    @Transactional
    public TokenResponse login(LoginRequest request) {
        UserAccount user = users.findByEmailIgnoreCase(request.email())
                .orElse(null);
        if (user == null || !passwordEncoder.matches(request.password(), user.getPasswordHash())) {
            if (user != null) {
                user.recordFailedLogin();
            }
            throw new ForbiddenOperationException("Invalid credentials");
        }
        if (!user.canAuthenticate()) {
            throw new ForbiddenOperationException("Account is inactive or not verified");
        }
        user.resetFailedLogins();
        return issueTokens(user);
    }

    @Transactional
    public TokenResponse refresh(String rawRefreshToken) {
        RotationResult result = refreshTokenService.rotate(rawRefreshToken);
        UserAccount user = users.findById(result.userId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        String access = jwtService.issueAccessToken(
                user.getId(), user.getClinicId(), user.getEmail(), user.getRoles());
        return new TokenResponse(access, result.rawToken(), "Bearer", jwtService.getAccessTtlSeconds());
    }

    @Transactional
    public void logout(UUID userId) {
        refreshTokenService.revokeAllForUser(userId);
    }

    private TokenResponse issueTokens(UserAccount user) {
        String access = jwtService.issueAccessToken(
                user.getId(), user.getClinicId(), user.getEmail(), user.getRoles());
        String refresh = refreshTokenService.issueNewFamily(user.getId());
        return new TokenResponse(access, refresh, "Bearer", jwtService.getAccessTtlSeconds());
    }

    private UserSummary toSummary(UserAccount user) {
        return new UserSummary(user.getId(), user.getClinicId(), user.getEmail(),
                user.getFullNameEn(), user.getFullNameAr(), user.getRoles(), user.getPreferredLanguage());
    }
}
