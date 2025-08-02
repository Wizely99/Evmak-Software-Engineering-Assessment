package com.memplas.parking.feature.account.user.service;

import com.memplas.parking.feature.account.user.dto.KeycloakUserDto;
import org.keycloak.representations.idm.GroupRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.keycloak.representations.idm.UserSessionRepresentation;

import java.util.List;

public interface KeycloakAdminService {
    // User Management
    String createUser(KeycloakUserDto keycloakUserDto);

    List<UserRepresentation> getAllUsers();

    UserRepresentation getUserById(String userId);

    void updateUser(String userId, KeycloakUserDto keycloakUserDto);

    void deleteUser(String userId);

    // Role Management
    void createRole(String roleName);

    List<RoleRepresentation> getAllRoles();

    void deleteRole(String roleName);

    void assignRoleToUser(String userId, String roleName);

    // Group Management
    void createGroup(String groupName);

    List<GroupRepresentation> getAllGroups();

    void deleteGroup(String groupId);

    void assignGroupToUser(String userId, String groupId);

    void removeGroupFromUser(String userId, String groupId);

    List<UserSessionRepresentation> getUserSessions(String userId);
}
