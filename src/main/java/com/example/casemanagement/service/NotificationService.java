package com.example.casemanagement.service;

import com.example.casemanagement.dto.NotificationDTO;
import com.example.casemanagement.exception.ForbiddenException;
import com.example.casemanagement.exception.ResourceNotFoundException;
import com.example.casemanagement.mapper.NotificationMapper;
import com.example.casemanagement.model.Notification;
import com.example.casemanagement.model.User;
import com.example.casemanagement.repository.NotificationRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class NotificationService {

    private final NotificationRepository repo;
    private final NotificationMapper mapper;

    public NotificationService(NotificationRepository repo,
                               NotificationMapper mapper) {
        this.repo = repo;
        this.mapper = mapper;
    }

    public void createNotification(User user, String message, Long caseId) {
        Notification n = new Notification();
        n.setUser(user);
        n.setMessage(message);
        n.setCaseId(caseId);
        n.setCreatedAt(LocalDateTime.now());
        n.setRead(false);
        n.setDeleted(false);

        repo.save(n);
    }

    public List<NotificationDTO> getMyNotifications(User user) {
        return repo.findByUserAndDeletedFalseOrderByCreatedAtDesc(user)
                .stream()
                .map(mapper::toDTO)
                .toList();
    }

    public void delete(Long id, User currentUser) {
        Notification n = repo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Notification not found"));

        if (!n.getUser().getId().equals(currentUser.getId())) {
            throw new ForbiddenException("Not allowed");
        }

        n.setDeleted(true);
        repo.save(n);
    }

    public void markAllAsRead(User user) {
        List<Notification> notifications =
                repo.findByUserAndDeletedFalse(user);

        for (Notification n : notifications) {
            n.setRead(true);
        }

        repo.saveAll(notifications);
    }

    public int getUnreadCount(User user) {
        return repo.countByUserAndIsReadFalseAndDeletedFalse(user);
    }
}