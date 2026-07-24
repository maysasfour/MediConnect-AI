package com.mediconnect.identity.domain;

import com.mediconnect.shared.domain.BaseEntity;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;
import java.util.EnumSet;
import java.util.Set;
import java.util.UUID;

/**
 * An authenticatable account. Belongs to a clinic except for platform-level
 * {@code SYSTEM_ADMIN} accounts, whose {@code clinicId} is {@code null}.
 *
 * <p>Only the BCrypt password hash is stored — never the plaintext. Verification
 * state and activation flags gate whether the account may authenticate.
 */
@Entity
@Table(name = "user_account")
public class UserAccount extends BaseEntity {

    @Column(name = "clinic_id")
    private UUID clinicId;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(name = "phone_e164")
    private String phoneE164;

    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    @Column(name = "full_name_en", nullable = false)
    private String fullNameEn;

    @Column(name = "full_name_ar")
    private String fullNameAr;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "user_role", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "role", nullable = false)
    @Enumerated(EnumType.STRING)
    private Set<Role> roles = EnumSet.noneOf(Role.class);

    @Column(nullable = false)
    private boolean active = true;

    @Column(name = "email_verified", nullable = false)
    private boolean emailVerified = false;

    @Column(name = "phone_verified", nullable = false)
    private boolean phoneVerified = false;

    @Column(name = "failed_login_attempts", nullable = false)
    private int failedLoginAttempts = 0;

    @Column(name = "preferred_language", nullable = false)
    private String preferredLanguage = "en";

    protected UserAccount() {
    }

    public UserAccount(UUID clinicId, String email, String passwordHash, String fullNameEn, Set<Role> roles) {
        this.clinicId = clinicId;
        this.email = email;
        this.passwordHash = passwordHash;
        this.fullNameEn = fullNameEn;
        this.roles = EnumSet.copyOf(roles);
    }

    public boolean canAuthenticate() {
        return active && (emailVerified || phoneVerified);
    }

    public UUID getClinicId() {
        return clinicId;
    }

    public String getEmail() {
        return email;
    }

    public String getPhoneE164() {
        return phoneE164;
    }

    public void setPhoneE164(String phoneE164) {
        this.phoneE164 = phoneE164;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public String getFullNameEn() {
        return fullNameEn;
    }

    public String getFullNameAr() {
        return fullNameAr;
    }

    public void setFullNameAr(String fullNameAr) {
        this.fullNameAr = fullNameAr;
    }

    public Set<Role> getRoles() {
        return EnumSet.copyOf(roles);
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public boolean isEmailVerified() {
        return emailVerified;
    }

    public void markEmailVerified() {
        this.emailVerified = true;
    }

    public boolean isPhoneVerified() {
        return phoneVerified;
    }

    public void markPhoneVerified() {
        this.phoneVerified = true;
    }

    public int getFailedLoginAttempts() {
        return failedLoginAttempts;
    }

    public void recordFailedLogin() {
        this.failedLoginAttempts++;
    }

    public void resetFailedLogins() {
        this.failedLoginAttempts = 0;
    }

    public String getPreferredLanguage() {
        return preferredLanguage;
    }

    public void setPreferredLanguage(String preferredLanguage) {
        this.preferredLanguage = preferredLanguage;
    }
}
