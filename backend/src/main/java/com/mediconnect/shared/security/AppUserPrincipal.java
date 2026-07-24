package com.mediconnect.shared.security;

import com.mediconnect.identity.domain.Role;
import java.util.Collection;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * The authenticated principal placed in the security context. Carries the tenant
 * (clinic) id so authorization and tenant filtering can both key off a single,
 * server-trusted source rather than any client-supplied value.
 */
public class AppUserPrincipal implements UserDetails {

    private final UUID userId;
    private final UUID clinicId;
    private final String email;
    private final java.util.Set<Role> roles;

    public AppUserPrincipal(UUID userId, UUID clinicId, String email, java.util.Set<Role> roles) {
        this.userId = userId;
        this.clinicId = clinicId;
        this.email = email;
        this.roles = roles;
    }

    public UUID getUserId() {
        return userId;
    }

    public UUID getClinicId() {
        return clinicId;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roles.stream().map(r -> new SimpleGrantedAuthority(r.authority())).collect(Collectors.toSet());
    }

    @Override
    public String getPassword() {
        return null;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
