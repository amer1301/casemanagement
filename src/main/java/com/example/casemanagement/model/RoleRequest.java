package com.example.casemanagement.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class RoleRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private User user;

    @Enumerated(EnumType.STRING)
    private RoleRequestStatus status;

    @Column(name = "is_deleted")
    private boolean deleted = false;

    private LocalDateTime createdAt;

    public RoleRequest() {}

    public RoleRequest(User user) {
        this.user = user;
        this.status = RoleRequestStatus.PENDING;
        this.createdAt = LocalDateTime.now();
    }

    // getters & setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public RoleRequestStatus getStatus() {
        return status;
    }

    public void setStatus(RoleRequestStatus status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }
}
