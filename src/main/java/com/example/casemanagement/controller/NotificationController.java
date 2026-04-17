package com.example.casemanagement.controller;

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
    private final UserRepository userRepository;

    public NotificationController(NotificationService service, UserRepository userRepository) {
        this.service = service;
        this.userRepository = userRepository;
    }

    private User getCurrentUser() {
        String email = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();

        return userRepository.findByEmail(email).orElseThrow();
    }

    @DeleteMapping("/{id}")
    public void deleteNotification(@PathVariable Long id) {
        service.delete(id);
    }

    @GetMapping
    public List<?> getMyNotifications() {
        return service.getMyNotifications(getCurrentUser());
    }

    @PatchMapping("/read-all")
    public void markAllAsRead() {
        service.markAllAsReadForCurrentUser();
    }

    @GetMapping("/unread-count")
    public int getUnreadCount() {
        return service.getUnreadCount();
    }
}
