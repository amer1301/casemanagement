package com.example.casemanagement.repository;

import com.example.casemanagement.model.Notification;
import com.example.casemanagement.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Repository för Notification-entiteten.
 *
 * Ansvarar för databasanrop kopplade till användarens notifikationer.
 * Bygger på Spring Data JPA vilket möjliggör automatiskt genererade queries
 * baserat på metodnamn.
 *
 * Design:
 * - Fokuserar på användarspecifik data
 * - Innehåller filtrering för read/unread och soft delete
 * - Stödjer sortering för att visa senaste notifikationer först
 */
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    /**
     * Hämtar alla notifikationer för en användare, sorterade efter senaste först
     */
    List<Notification> findByUserOrderByCreatedAtDesc(User user);

    /**
     * Räknar antal olästa notifikationer för en användare
     */
    int countByUserAndIsReadFalse(User user);

    /**
     * Räknar antal olästa och ej borttagna notifikationer
     *
     * Används för att visa korrekt badge/indikator i UI
     */
    int countByUserAndIsReadFalseAndDeletedFalse(User user);

    /**
     * Hämtar alla aktiva (ej borttagna) notifikationer
     */
    List<Notification> findByUserAndDeletedFalse(User user);

    /**
     * Hämtar aktiva notifikationer sorterade efter senaste först
     *
     * Används exempelvis i notifikationsvy
     */
    List<Notification> findByUserAndDeletedFalseOrderByCreatedAtDesc(User user);

    /**
     * Hämtar alla notifikationer (inklusive borttagna)
     */
    List<Notification> findByUser(User user);
}