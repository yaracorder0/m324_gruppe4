package ch.tbz.ticketservice.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.OffsetDateTime;

/**
 * JPA Entity zur Abbildung eines Tickets in der Datenbanktabelle 'ticket'.
 */
@Entity
@Table(name = "ticket")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Ticket {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "title", nullable = false, length = 100)
    private String title;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private TicketStatus status;

    @Column(name = "review_date")
    private OffsetDateTime reviewDate;

    @Column(name = "done_date")
    private OffsetDateTime doneDate;

    @Column(name = "employee_id", nullable = false)
    private Long employeeId;
}
