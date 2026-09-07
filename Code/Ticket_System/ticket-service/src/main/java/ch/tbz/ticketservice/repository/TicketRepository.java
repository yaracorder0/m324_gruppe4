package ch.tbz.ticketservice.repository;

import ch.tbz.ticketservice.model.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA Repository für den Datenbankzugriff auf Ticket-Entitäten.
 */
@Repository
public interface TicketRepository extends JpaRepository<Ticket, Long> {
}
