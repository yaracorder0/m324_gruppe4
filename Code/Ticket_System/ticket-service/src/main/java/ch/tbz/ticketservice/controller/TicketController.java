package ch.tbz.ticketservice.controller;

import ch.tbz.ticketservice.dto.TicketCreateRequest;
import ch.tbz.ticketservice.dto.TicketResponse;
import ch.tbz.ticketservice.service.TicketService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

/**
 * REST Controller für Ticket-Endpunkte.
 * Bietet Schnittstellen für User Story 3 (Ticket erfassen und Mitarbeiter zuweisen).
 */
@RestController
@RequestMapping("/api/tickets")
@RequiredArgsConstructor
public class TicketController {

    private final TicketService ticketService;

    /**
     * User Story 3: Ticket erfassen.
     *
     * @param request Valider Request-Body mit allen Ticketdaten
     * @return 201 Created mit dem gespeicherten Ticket und Location-Header
     */
    @PostMapping
    public ResponseEntity<TicketResponse> createTicket(@Valid @RequestBody TicketCreateRequest request) {
        TicketResponse created = ticketService.createTicket(request);
        URI location = URI.create("/api/tickets/" + created.getId());
        return ResponseEntity.created(location).body(created);
    }

    /**
     * Alle Tickets auslesen.
     *
     * @return 200 OK mit der Liste aller Tickets
     */
    @GetMapping
    public ResponseEntity<List<TicketResponse>> getAllTickets() {
        List<TicketResponse> tickets = ticketService.getAllTickets();
        return ResponseEntity.ok(tickets);
    }

    /**
     * Einzelnes Ticket nach ID auslesen.
     *
     * @param id ID des Tickets
     * @return 200 OK mit den Ticketdaten
     */
    @GetMapping("/{id}")
    public ResponseEntity<TicketResponse> getTicketById(@PathVariable Long id) {
        TicketResponse ticket = ticketService.getTicketById(id);
        return ResponseEntity.ok(ticket);
    }
}
