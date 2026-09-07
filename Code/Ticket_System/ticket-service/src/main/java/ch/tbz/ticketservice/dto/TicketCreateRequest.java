package ch.tbz.ticketservice.dto;

import ch.tbz.ticketservice.model.TicketStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.OffsetDateTime;

/**
 * DTO für das Erfassen eines neuen Tickets (User Story 3).
 * Enthält Validierungsregeln für Pflichtangaben und Formate.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TicketCreateRequest {

    @NotBlank(message = "Der Titel des Tickets darf nicht leer sein.")
    @Size(max = 100, message = "Der Titel darf maximal 100 Zeichen lang sein.")
    private String title;

    private String description;

    @NotNull(message = "Der Status des Tickets darf nicht fehlen (Erlaubte Werte: OPEN, IN_PROGRESS, REVIEW, DONE).")
    private TicketStatus status;

    private OffsetDateTime reviewDate;

    private OffsetDateTime doneDate;

    @NotNull(message = "Ein Mitarbeiter muss dem Ticket immer zugewiesen werden (employeeId darf nicht fehlen).")
    private Long employeeId;
}
