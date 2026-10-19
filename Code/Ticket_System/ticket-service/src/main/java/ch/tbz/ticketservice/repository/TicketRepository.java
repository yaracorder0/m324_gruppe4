package ch.tbz.ticketservice.repository;

import ch.tbz.ticketservice.model.Ticket;
import ch.tbz.ticketservice.model.TicketStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Spring Data JPA Repository für den Datenbankzugriff auf Ticket-Entitäten.
 */
@Repository
public interface TicketRepository extends JpaRepository<Ticket, Long> {

    /** Anzahl Tickets je Status (für Kennzahl: Verteilung der Tickets). */
    long countByStatus(TicketStatus status);

    /**
     * Alle abgeschlossenen Tickets, bei denen sowohl review_date als auch done_date gesetzt sind.
     * Wird für die Berechnung der Review-to-Done-Durchlaufzeit verwendet.
     */
    List<Ticket> findByStatusAndReviewDateIsNotNullAndDoneDateIsNotNull(TicketStatus status);

    /**
     * Anzahl offener bzw. in-progress Tickets pro Mitarbeiter-ID.
     * Gibt eine Liste von Object[]{employeeId, count} zurück.
     */
    @Query("SELECT t.employeeId, COUNT(t) FROM Ticket t WHERE t.status IN ('OPEN', 'IN_PROGRESS') GROUP BY t.employeeId ORDER BY COUNT(t) DESC")
    List<Object[]> countOpenTicketsPerEmployee();
}
