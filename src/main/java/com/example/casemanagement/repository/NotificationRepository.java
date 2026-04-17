package com.example.casemanagement.repository;

import com.example.casemanagement.model.Notification;
import com.example.casemanagement.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

    List<Notification> findByUserOrderByCreatedAtDesc(User user);

    int countByUserAndIsReadFalse(User user);

    int countByUserAndIsReadFalseAndDeletedFalse(User user);

    List<Notification> findByUserAndDeletedFalse(User user);

    List<Notification> findByUserAndDeletedFalseOrderByCreatedAtDesc(User user);

    List<Notification> findByUser(User user);
}
