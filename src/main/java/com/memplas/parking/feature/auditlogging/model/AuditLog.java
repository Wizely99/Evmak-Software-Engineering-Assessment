package com.memplas.parking.feature.auditlogging.model;

import com.fasterxml.jackson.databind.JsonNode;
import com.memplas.parking.feature.book.model.User;
import io.hypersistence.utils.hibernate.type.json.JsonType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.hibernate.annotations.Type;

import java.time.LocalDateTime;

@Entity
@Table(name = "audit_logs")
public class AuditLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(nullable = false, length = 50)
    @NotBlank(message = "Entity type is required")
    @Size(max = 50, message = "Entity type must not exceed 50 characters")
    private String entityType;

    @Column(nullable = false)
    @NotNull(message = "Entity ID is required")
    private Long entityId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @NotNull(message = "Action is required")
    private AuditAction action;

    @Type(JsonType.class)
    @Column(columnDefinition = "json")
    private JsonNode oldValues;

    @Type(JsonType.class)
    @Column(columnDefinition = "json")
    private JsonNode newValues;

    @Column(length = 45)
    @Size(max = 45, message = "IP address must not exceed 45 characters")
    private String ipAddress;

    @Column(length = 255)
    @Size(max = 255, message = "User agent must not exceed 255 characters")
    private String userAgent;

    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    // Constructors
    public AuditLog() {}

    // Getters and Setters
    public Long getId() {return id;}

    public void setId(Long id) {this.id = id;}

    public User getUser() {return user;}

    public void setUser(User user) {this.user = user;}

    public String getEntityType() {return entityType;}

    public void setEntityType(String entityType) {this.entityType = entityType;}

    public Long getEntityId() {return entityId;}

    public void setEntityId(Long entityId) {this.entityId = entityId;}

    public AuditAction getAction() {return action;}

    public void setAction(AuditAction action) {this.action = action;}

    public JsonNode getOldValues() {return oldValues;}

    public void setOldValues(JsonNode oldValues) {this.oldValues = oldValues;}

    public JsonNode getNewValues() {return newValues;}

    public void setNewValues(JsonNode newValues) {this.newValues = newValues;}

    public String getIpAddress() {return ipAddress;}

    public void setIpAddress(String ipAddress) {this.ipAddress = ipAddress;}

    public String getUserAgent() {return userAgent;}

    public void setUserAgent(String userAgent) {this.userAgent = userAgent;}

    public LocalDateTime getCreatedAt() {return createdAt;}

    public void setCreatedAt(LocalDateTime createdAt) {this.createdAt = createdAt;}
}
