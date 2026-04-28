package com.example.casemanagement.controller;

import com.example.casemanagement.dto.NotificationDTO;
import com.example.casemanagement.model.User;
import com.example.casemanagement.service.NotificationService;
import com.example.casemanagement.repository.UserRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller för hantering av användarspecifika notifikationer.
 *
 * Notifikationer är kopplade till den inloggade användaren och
 * hämtas baserat på autentiseringskontexten (SecurityContext).
 *
 * Controllern innehåller ingen affärslogik utan delegerar all
 * hantering till service-lagret.
 */
@RestController
@RequestMapping("/notifications")
public class NotificationController {

    private final NotificationService service;

    public NotificationController(NotificationService service) {
        this.service = service;
    }

    /**
     * Hämtar alla notifikationer för den aktuella användaren.
     *
     * Filtrering baseras på autentiserad användare i backend,
     * vilket förhindrar att klienten kan manipulera vilken data som hämtas.
     */
    @GetMapping
    public List<NotificationDTO> getMyNotifications() {
        return service.getMyNotifications();
    }

    /**
     * Tar bort en notifikation.
     *
     * Implementationen i service-lagret använder soft delete,
     * vilket innebär att data inte tas bort permanent utan endast markeras som borttagen.
     */
    @DeleteMapping("/{id}")
    public void deleteNotification(@PathVariable Long id) {
        service.delete(id);
    }

    /**
     * Markerar alla användarens notifikationer som lästa.
     *
     * Detta är en batch-operation som påverkar flera entiteter samtidigt.
     */
    @PatchMapping("/read-all")
    public void markAllAsRead() {
        service.markAllAsRead();
    }

    /**
     * Returnerar antal olästa notifikationer för aktuell användare.
     *
     * Används exempelvis för att visa notifieringsindikator i UI.
     */
    @GetMapping("/unread-count")
    public int getUnreadCount() {
        return service.getUnreadCount();
    }
}