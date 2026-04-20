package com.example.casemanagement.mapper;

import com.example.casemanagement.dto.NotificationDTO;
import com.example.casemanagement.model.Notification;
import org.springframework.stereotype.Service;

@Service
public class NotificationMapper {

    public NotificationDTO toDTO(Notification n) {
        return new NotificationDTO(
                n.getId(),
                n.getMessage(),
                n.getCaseId(),
                n.isRead(),
                n.getCreatedAt()
        );
    }
}
