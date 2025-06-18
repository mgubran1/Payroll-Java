package com.company.payroll.model;

import java.time.LocalDateTime;

public class AuditLog {
    private int id;
    private LocalDateTime timestamp;
    private String actionType;
    private String user;
    private Integer driverId;
    private String entityType;
    private Integer entityId;
    private String details;

    public AuditLog() {}

    public AuditLog(int id, LocalDateTime timestamp, String actionType, String user, Integer driverId,
                    String entityType, Integer entityId, String details) {
        this.id = id;
        this.timestamp = timestamp;
        this.actionType = actionType;
        this.user = user;
        this.driverId = driverId;
        this.entityType = entityType;
        this.entityId = entityId;
        this.details = details;
    }

    // Getters and setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
    public String getActionType() { return actionType; }
    public void setActionType(String actionType) { this.actionType = actionType; }
    public String getUser() { return user; }
    public void setUser(String user) { this.user = user; }
    public Integer getDriverId() { return driverId; }
    public void setDriverId(Integer driverId) { this.driverId = driverId; }
    public String getEntityType() { return entityType; }
    public void setEntityType(String entityType) { this.entityType = entityType; }
    public Integer getEntityId() { return entityId; }
    public void setEntityId(Integer entityId) { this.entityId = entityId; }
    public String getDetails() { return details; }
    public void setDetails(String details) { this.details = details; }
}