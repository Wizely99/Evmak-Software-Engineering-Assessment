package com.memplas.parking.feature.account.user.helper;

import com.memplas.parking.feature.account.user.dto.KeycloakLoggedInUser;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

@Component
public class AuthenticatedUserProvider {
    public KeycloakLoggedInUser getCurrentKeycloakUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !(authentication.getPrincipal() instanceof Jwt jwt)) {
            throw new IllegalStateException("User is not authenticated");
        }

        return KeycloakLoggedInUser.fromJwt(jwt);
    }
}
