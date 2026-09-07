package ch.tbz.ticketservice.service;

import ch.tbz.ticketservice.client.EmployeeServiceClient;
import ch.tbz.ticketservice.dto.TicketCreateRequest;
import ch.tbz.ticketservice.dto.TicketResponse;
import ch.tbz.ticketservice.exception.EmployeeNotFoundException;
import ch.tbz.ticketservice.exception.InvalidTicketStateException;
import ch.tbz.ticketservice.model.Ticket;
import ch.tbz.ticketservice.model.TicketStatus;
import ch.tbz.ticketservice.repository.TicketRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Service-Schicht für die Ticketverwaltung (User Story 3).
 * Übernimmt die Validierung von Status und Datumsangaben sowie den
 * externen REST-Aufruf an den Employee Service.
 */
@Service
@RequiredArgsConstructor
public class TicketService {

    private final TicketRepository ticketRepository;
    private final EmployeeServiceClient employeeServiceClient;

    /**
     * Erfasst ein neues Ticket und weist es einem Mitarbeiter zu (User Story 3).
     *
     * @param request Die Daten des zu erfassenden Tickets
     * @return Das gespeicherte Ticket mit generierter ID
     */
    @Transactional
    public TicketResponse createTicket(TicketCreateRequest request) {
        validateTicketDates(request);
        validateEmployeeExists(request.getEmployeeId());

        Ticket ticket = Ticket.builder()
                .title(request.getTitle().trim())
                .description(request.getDescription() != null ? request.getDescription().trim() : null)
                .status(request.getStatus())
                .reviewDate(request.getReviewDate())
                .doneDate(request.getDoneDate())
                .employeeId(request.getEmployeeId())
                .build();

        Ticket saved = ticketRepository.save(ticket);
        return mapToResponse(saved);
    }

    /**
     * Liest alle erfassten Tickets aus der Datenbank aus.
     */
    @Transactional(readOnly = true)
    public List<TicketResponse> getAllTickets() {
        return ticketRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    /**
     * Liest ein einzelnes Ticket anhand seiner ID aus.
     */
    @Transactional(readOnly = true)
    public TicketResponse getTicketById(Long id) {
        return ticketRepository.findById(id)
                .map(this::mapToResponse)
                .orElseThrow(() -> new IllegalArgumentException("Ticket mit ID " + id + " wurde nicht gefunden."));
    }

    /**
     * Validiert, dass Review- und Done-Datumsangaben nur gemäss aktuellem Ticket-Status gesetzt werden.
     */
    private void validateTicketDates(TicketCreateRequest request) {
        TicketStatus status = request.getStatus();

        if (status == TicketStatus.OPEN || status == TicketStatus.IN_PROGRESS) {
            if (request.getReviewDate() != null || request.getDoneDate() != null) {
                throw new InvalidTicketStateException(
                        "Ein Ticket im Status " + status + " darf weder ein Review-Datum noch ein Done-Datum haben."
                );
            }
        } else if (status == TicketStatus.REVIEW) {
            if (request.getDoneDate() != null) {
                throw new InvalidTicketStateException(
                        "Ein Ticket im Status REVIEW darf noch kein Done-Datum haben."
                );
            }
        } else if (status == TicketStatus.DONE) {
            if (request.getReviewDate() != null && request.getDoneDate() != null) {
                if (request.getDoneDate().isBefore(request.getReviewDate())) {
                    throw new InvalidTicketStateException(
                            "Das Done-Datum darf nicht vor dem Review-Datum liegen."
                    );
                }
            }
        }
    }

    /**
     * Validiert über den REST-Aufruf an den Employee Service, ob der zugewiesene Mitarbeiter existiert.
     */
    private void validateEmployeeExists(Long employeeId) {
        if (employeeId == null) {
            throw new EmployeeNotFoundException("Ein Mitarbeiter muss dem Ticket immer zugewiesen werden.");
        }
        boolean exists = employeeServiceClient.employeeExists(employeeId);
        if (!exists) {
            throw new EmployeeNotFoundException(
                    "Mitarbeiter mit ID " + employeeId + " existiert nicht im Employee-Service. " +
                    "Bitte wählen Sie einen gültigen, zuvor erfassten Mitarbeiter aus."
            );
        }
    }

    /**
     * Mappt die JPA Entity auf das DTO.
     */
    private TicketResponse mapToResponse(Ticket ticket) {
        return TicketResponse.builder()
                .id(ticket.getId())
                .title(ticket.getTitle())
                .description(ticket.getDescription())
                .status(ticket.getStatus())
                .reviewDate(ticket.getReviewDate())
                .doneDate(ticket.getDoneDate())
                .employeeId(ticket.getEmployeeId())
                .build();
    }
}
