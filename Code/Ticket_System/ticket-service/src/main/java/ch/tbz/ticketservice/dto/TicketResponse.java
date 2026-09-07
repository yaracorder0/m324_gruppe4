package ch.tbz.ticketservice.dto;

import ch.tbz.ticketservice.model.TicketStatus;
import lombok.*;

import java.time.OffsetDateTime;

/**
 * DTO für die Rückgabe von Ticketdetails nach erfolgreicher Erfassung oder Abfrage.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TicketResponse {
    private Long id;
    private String title;
    private String description;
    private TicketStatus status;
    private OffsetDateTime reviewDate;
    private OffsetDateTime doneDate;
    private Long employeeId;
}
