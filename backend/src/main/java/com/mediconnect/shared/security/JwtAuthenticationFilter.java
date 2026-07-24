package com.mediconnect.shared.security;

import com.mediconnect.identity.domain.Role;
import com.mediconnect.identity.service.JwtService;
import com.mediconnect.shared.tenant.TenantContext;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * Validates the bearer access token on each request and, on success, binds both
 * the Spring Security context and the {@link TenantContext}. The tenant id comes
 * only from the signed token — never from a header or body — so tenant scoping
 * cannot be spoofed by the client.
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    private final JwtService jwtService;

    public JwtAuthenticationFilter(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String header = request.getHeader("Authorization");
        if (StringUtils.hasText(header) && header.startsWith("Bearer ")) {
            String token = header.substring(7);
            try {
                Claims claims = jwtService.parse(token);
                UUID userId = UUID.fromString(claims.getSubject());
                String clinicClaim = claims.get("clinicId", String.class);
                UUID clinicId = clinicClaim == null ? null : UUID.fromString(clinicClaim);
                @SuppressWarnings("unchecked")
                List<String> roleNames = claims.get("roles", List.class);
                Set<Role> roles = roleNames.stream().map(Role::valueOf).collect(Collectors.toSet());

                AppUserPrincipal principal = new AppUserPrincipal(
                        userId, clinicId, claims.get("email", String.class), roles);
                var authentication = new UsernamePasswordAuthenticationToken(
                        principal, null, principal.getAuthorities());
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authentication);
                if (clinicId != null) {
                    TenantContext.set(clinicId);
                }
            } catch (Exception ex) {
                // Invalid/expired token: leave the context unauthenticated.
                log.debug("Rejected bearer token: {}", ex.getMessage());
                SecurityContextHolder.clearContext();
            }
        }
        try {
            filterChain.doFilter(request, response);
        } finally {
            TenantContext.clear();
        }
    }
}
