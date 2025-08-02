package com.memplas.parking.feature.account.user.controller;

import com.memplas.parking.feature.account.user.dto.KeycloakUserDto;
import com.memplas.parking.feature.account.user.helper.AuthenticatedUserProvider;
import com.memplas.parking.feature.account.user.service.KeycloakAdminService;
import jakarta.validation.Valid;
import org.keycloak.representations.idm.GroupRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.keycloak.representations.idm.UserSessionRepresentation;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/keycloak")
@Validated
public class KeycloakAdminController {
    private final KeycloakAdminService keycloakAdminService;

    private final AuthenticatedUserProvider authenticatedUserProvider;

    public KeycloakAdminController(
            KeycloakAdminService keycloakAdminService,
            AuthenticatedUserProvider authenticatedUserProvider) {
        this.keycloakAdminService = keycloakAdminService;
        this.authenticatedUserProvider = authenticatedUserProvider;
    }

    @PostMapping("/users")
    public ResponseEntity<String> createUser(
            @Valid @RequestBody KeycloakUserDto keycloakUserDto) {
        return ResponseEntity.ok(keycloakAdminService.createUser(keycloakUserDto));
    }

    @GetMapping("/users")
    public ResponseEntity<List<UserRepresentation>> getAllUsers() {
        return ResponseEntity.ok(keycloakAdminService.getAllUsers());
    }

    @GetMapping("/users/{userId}")
    public ResponseEntity<UserRepresentation> getUser(@PathVariable String userId) {
        return ResponseEntity.ok(keycloakAdminService.getUserById(userId));
    }

    @GetMapping("/users/{userId}/sessions")
    public ResponseEntity<List<UserSessionRepresentation>> getUserSessions(
            @PathVariable String userId) {
        return ResponseEntity.ok(keycloakAdminService.getUserSessions(userId));
    }

    @PutMapping("/users/{userId}")
    public ResponseEntity<Void> updateUser(
            @PathVariable String userId,
            @Valid @RequestBody KeycloakUserDto keycloakUserDto) {
        keycloakAdminService.updateUser(userId, keycloakUserDto);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/users/{userId}")
    public ResponseEntity<Void> deleteUser(@PathVariable String userId) {
        keycloakAdminService.deleteUser(userId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/roles")
    public ResponseEntity<Void> createRole(@RequestParam String roleName) {
        keycloakAdminService.createRole(roleName);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/roles")
    public ResponseEntity<List<RoleRepresentation>> getAllRoles() {
        return ResponseEntity.ok(keycloakAdminService.getAllRoles());
    }

    @PostMapping("/users/{userId}/roles/{roleName}")
    public ResponseEntity<Void> assignRoleToUser(
            @PathVariable String userId, @PathVariable String roleName) {
        keycloakAdminService.assignRoleToUser(userId, roleName);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/groups")
    public ResponseEntity<Void> createGroup(@RequestParam String groupName) {
        keycloakAdminService.createGroup(groupName);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/groups")
    public ResponseEntity<List<GroupRepresentation>> getAllGroups() {
        return ResponseEntity.ok(keycloakAdminService.getAllGroups());
    }

    @PostMapping("/users/{userId}/groups/{groupId}")
    public ResponseEntity<Void> assignGroupToUser(
            @PathVariable String userId, @PathVariable String groupId) {
        keycloakAdminService.assignGroupToUser(userId, groupId);
        return ResponseEntity.ok().build();
    }
}
