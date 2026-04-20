package com.example.casemanagement.controller;

import com.example.casemanagement.dto.NotificationDTO;
import com.example.casemanagement.model.User;
import com.example.casemanagement.service.NotificationService;
import com.example.casemanagement.repository.UserRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/notifications")
public class NotificationController {

    private final NotificationService service;

    public NotificationController(NotificationService service) {
        this.service = service;
    }

    @GetMapping
    public List<NotificationDTO> getMyNotifications() {
        return service.getMyNotifications();
    }

    @DeleteMapping("/{id}")
    public void deleteNotification(@PathVariable Long id) {
        service.delete(id);
    }

    @PatchMapping("/read-all")
    public void markAllAsRead() {
        service.markAllAsRead();
    }

    @GetMapping("/unread-count")
    public int getUnreadCount() {
        return service.getUnreadCount();
    }
}