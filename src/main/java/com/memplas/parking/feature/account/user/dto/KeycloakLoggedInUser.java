package com.memplas.parking.feature.account.user.dto;

import com.memplas.parking.feature.book.model.User;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.*;
import java.util.stream.Collectors;

public record KeycloakLoggedInUser(
        UUID userId,
        String username,
        String email,
        String firstName,
        String lastName,
        String phoneNumber,
        boolean isAdmin,
        List<String> roles) {
    public static final Long userId2 = 1L;//TODO CHANGE THIS GET USER FROM DASHBOARD

    public static KeycloakLoggedInUser fromJwt(Jwt jwt) {
        Map<String, Object> realmAccess = jwt.getClaimAsMap("realm_access");
        Set<String> roles =
                Optional.ofNullable(realmAccess)
                        .map(access -> (List<String>) access.get("roles"))
                        .map(roleList -> roleList.stream().collect(Collectors.toSet()))
                        .orElse(Collections.emptySet());
        //        var attributes = jwt.getClaims()//TODO GET ATTRIBUTES FROM THE JWT

        return new KeycloakLoggedInUser(
                UUID.fromString(jwt.getSubject()),
                jwt.getClaimAsString("preferred_username"),
                jwt.getClaimAsString("email"),
                jwt.getClaimAsString("given_name"),
                jwt.getClaimAsString("family_name"),
                jwt.getClaimAsString("phoneNumber"),

                true, new ArrayList<>(Collections.singleton("ADMIN")));
    }

    public User toEntity() {
        var user = new User();
        user.setKeycloakUserId(userId.toString());//todo change
        user.setEmail(email);
        user.setFirstName(fullName());
        return user;
    }

    public String fullName() {
        return String.format("%s %s", firstName, lastName);
    }
}
