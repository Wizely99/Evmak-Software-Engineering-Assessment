package com.memplas.parking.feature.account.user.controller;

import com.memplas.parking.feature.account.user.dto.KeycloakLoggedInUser;
import com.memplas.parking.feature.account.user.helper.AuthenticatedUserProvider;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/logged-in-user")
@Validated
public class LoggedInUserController {
    private final AuthenticatedUserProvider authenticatedUserProvider;

    public LoggedInUserController(AuthenticatedUserProvider authenticatedUserProvider) {
        this.authenticatedUserProvider = authenticatedUserProvider;
    }

    @GetMapping("/me")
    public ResponseEntity<KeycloakLoggedInUser> getCurrentUser() {
        return ResponseEntity.ok(authenticatedUserProvider.getCurrentKeycloakUser());
    }

    @GetMapping("/developer")
    @PreAuthorize("hasRole('DEVELOPER')")
    public ResponseEntity<String> testDeveloper() {
        return ResponseEntity.ok("I am a developer");
    }
}
