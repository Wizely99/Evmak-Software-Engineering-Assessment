package com.memplas.parking.feature.account.user.service;

import com.memplas.parking.feature.account.user.dto.KeycloakUserDto;
import com.memplas.parking.feature.account.user.mapper.KeycloakUserMapper;
import jakarta.ws.rs.core.Response;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.representations.idm.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public class KeycloakAdminServiceImpl implements KeycloakAdminService {
    private final Keycloak keycloak;

    @Value("${app.keycloak.realm}")
    private String realm;

    private final KeycloakUserMapper keycloakUserMapper;

    public KeycloakAdminServiceImpl(Keycloak keycloak, KeycloakUserMapper keycloakUserMapper) {
        this.keycloak = keycloak;
        this.keycloakUserMapper = keycloakUserMapper;
    }

    // User Management
    @Override
    public String createUser(KeycloakUserDto keycloakUserDto) {

        try (Response response =
                     keycloak
                             .realm(realm)
                             .users()
                             .create(keycloakUserMapper.toEntity(keycloakUserDto, keycloakUserDto.isAdmin()))) {
            if (response.getStatus() == 201) { // HTTP 201 Created
                String locationHeader = response.getHeaderString("Location");
                if (locationHeader != null) {
                    String userId = locationHeader.substring(locationHeader.lastIndexOf("/") + 1);
                    System.out.println("Created User ID: " + userId);
                    return userId;
                }
            }

            // User already exists, find the user by username
            List<UserRepresentation> userRepresentations =
                    keycloak.realm(realm).users().searchByUsername(keycloakUserDto.username(), true);
            return userRepresentations.stream()
                    .findFirst()
                    .map(AbstractUserRepresentation::getId)
                    .orElse(null);
        }
    }

    @Override
    public List<UserRepresentation> getAllUsers() {
        return keycloak.realm(realm).users().list();
    }

    @Override
    public UserRepresentation getUserById(String userId) {
        return keycloak.realm(realm).users().get(userId).toRepresentation();
    }

    @Override
    public void updateUser(String userId, KeycloakUserDto keycloakUserDto) {

        UserRepresentation user = keycloakUserMapper.toEntity(keycloakUserDto, keycloakUserDto.isAdmin());
        keycloak.realm(realm).users().get(userId).update(user);
    }

    @Override
    public void deleteUser(String userId) {
        keycloak.realm(realm).users().get(userId).remove();
    }

    // Role Management
    @Override
    public void createRole(String roleName) {
        RoleRepresentation role = new RoleRepresentation();
        role.setName(roleName);
        keycloak.realm(realm).roles().create(role);
    }

    @Override
    public List<RoleRepresentation> getAllRoles() {
        return keycloak.realm(realm).roles().list();
    }

    @Override
    public void deleteRole(String roleName) {
        keycloak.realm(realm).roles().deleteRole(roleName);
    }

    @Override
    public void assignRoleToUser(String userId, String roleName) {
        UserResource userResource = keycloak.realm(realm).users().get(userId);
        RoleRepresentation role = keycloak.realm(realm).roles().get(roleName).toRepresentation();
        userResource.roles().realmLevel().add(Collections.singletonList(role));
    }

    // Group Management
    @Override
    public void createGroup(String groupName) {
        GroupRepresentation group = new GroupRepresentation();
        group.setName(groupName);
        keycloak.realm(realm).groups().add(group);
    }

    @Override
    public List<GroupRepresentation> getAllGroups() {
        return keycloak.realm(realm).groups().groups();
    }

    @Override
    public void deleteGroup(String groupId) {
        keycloak.realm(realm).groups().group(groupId).remove();
    }

    @Override
    public void assignGroupToUser(String userId, String groupId) {
        keycloak.realm(realm).users().get(userId).joinGroup(groupId);
    }

    @Override
    public void removeGroupFromUser(String userId, String groupId) {
        keycloak.realm(realm).users().get(userId).leaveGroup(groupId);
    }

    @Override
    public List<UserSessionRepresentation> getUserSessions(String userId) {
        UserResource userResource = keycloak.realm(realm).users().get(userId);
        return userResource.getUserSessions();
    }

    //    public void endUserSession(String userId, String sessionId) {
    //        UserResource userResource = keycloak.realm(realm).users().get(userId);
    //        userResource.remove(sessionId);  // Delete the session by sessionId//TODO USE LOGOUT URL
    // IN FRONTEND
    //    }
}
