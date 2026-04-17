package com.example.casemanagement.service;

import com.example.casemanagement.model.Notification;
import com.example.casemanagement.model.User;
import com.example.casemanagement.repository.NotificationRepository;
import org.springframework.stereotype.Service;
import com.example.casemanagement.repository.UserRepository;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class NotificationService {

    private final NotificationRepository repo;
    private final UserRepository userRepository;

    public NotificationService(NotificationRepository repo, UserRepository userRepository) {
        this.repo = repo;
        this.userRepository = userRepository;
    }

    public void createNotification(User user, String message, Long caseId) {
        Notification n = new Notification();
        n.setUser(user);
        n.setMessage(message);
        n.setCaseId(caseId);
        n.setCreatedAt(LocalDateTime.now());

        repo.save(n);
    }

    public List<Notification> getMyNotifications(User user) {
        return repo.findByUserAndDeletedFalseOrderByCreatedAtDesc(user);
    }

    public void delete(Long id) {
        Notification n = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Notification not found"));

        n.setDeleted(true);
        repo.save(n);
    }

    public void markAllAsReadForCurrentUser() {
        User user = getCurrentUser();

        List<Notification> notifications =
                repo.findByUserAndDeletedFalse(user);

        for (Notification n : notifications) {
            n.setRead(true);
        }

        repo.saveAll(notifications);
    }

    public int getUnreadCount() {
        User user = getCurrentUser();
        return repo.countByUserAndIsReadFalseAndDeletedFalse(user);
    }

    private User getCurrentUser() {
        String email = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();

        return userRepository.findByEmail(email).orElseThrow();
    }
}
