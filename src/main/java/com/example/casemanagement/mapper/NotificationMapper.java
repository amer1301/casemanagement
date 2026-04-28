package com.example.casemanagement.mapper;

import com.example.casemanagement.dto.NotificationDTO;
import com.example.casemanagement.model.Notification;
import org.springframework.stereotype.Service;

/**
 * Mapper för notifieringar.
 *
 * Ansvarar för att omvandla Notification-entiteter till DTO:er
 * som kan skickas till klienten.
 *
 * Syfte:
 * - Separera intern datamodell från API-respons
 * - Säkerställa att endast relevant information exponeras
 *
 * Design:
 * - Enkel enriktad mapping (entity → DTO)
 * - Används i service-lagret för att förbereda data för frontend
 */
@Service
public class NotificationMapper {

    /**
     * Omvandlar Notification-entitet till NotificationDTO.
     *
     * @param n Notification-entitet från databasen
     * @return DTO anpassad för klienten
     */
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