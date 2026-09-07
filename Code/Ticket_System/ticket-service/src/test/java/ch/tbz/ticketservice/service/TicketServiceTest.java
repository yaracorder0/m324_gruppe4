package ch.tbz.ticketservice.service;

import ch.tbz.ticketservice.client.EmployeeServiceClient;
import ch.tbz.ticketservice.dto.TicketCreateRequest;
import ch.tbz.ticketservice.dto.TicketResponse;
import ch.tbz.ticketservice.exception.EmployeeNotFoundException;
import ch.tbz.ticketservice.exception.InvalidTicketStateException;
import ch.tbz.ticketservice.model.Ticket;
import ch.tbz.ticketservice.model.TicketStatus;
import ch.tbz.ticketservice.repository.TicketRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Isolierte Unit Tests für {@link TicketService}.
 * <p>
 * Mockt das {@link TicketRepository} und den externen {@link EmployeeServiceClient},
 * um die Tests ohne laufende Microservices oder Datenbanken ausführen zu können.
 */
@ExtendWith(MockitoExtension.class)
class TicketServiceTest {

    @Mock
    private TicketRepository ticketRepository;

    @Mock
    private EmployeeServiceClient employeeServiceClient;

    @InjectMocks
    private TicketService ticketService;

    private Ticket savedTicket;

    @BeforeEach
    void setUp() {
        savedTicket = Ticket.builder()
                .id(10L)
                .title("Login Bug fixen")
                .description("Fehler beim Login beheben")
                .status(TicketStatus.OPEN)
                .employeeId(1L)
                .build();
    }

    /**
     * <b>Happy Path: Ticket im Status OPEN erstellen (User Story 3)</b>
     * <p>
     * <b>Voraussetzung:</b> Valide Daten, zugewiesener Mitarbeiter existiert im Employee Service.
     * <b>Erwartetes Ergebnis:</b> Ticket wird mit generierter ID zurückgegeben.
     */
    @Test
    @DisplayName("US3 - Happy Path: OPEN Ticket erfolgreich erstellen")
    void createTicket_HappyPath_OpenTicket() {
        TicketCreateRequest request = TicketCreateRequest.builder()
                .title("Login Bug fixen")
                .description("Fehler beim Login beheben")
                .status(TicketStatus.OPEN)
                .employeeId(1L)
                .build();

        when(employeeServiceClient.employeeExists(1L)).thenReturn(true);
        when(ticketRepository.save(any(Ticket.class))).thenReturn(savedTicket);

        TicketResponse response = ticketService.createTicket(request);

        assertNotNull(response);
        assertEquals(10L, response.getId());
        assertEquals("Login Bug fixen", response.getTitle());
        assertEquals(TicketStatus.OPEN, response.getStatus());
        assertEquals(1L, response.getEmployeeId());

        verify(employeeServiceClient, times(1)).employeeExists(1L);
        verify(ticketRepository, times(1)).save(any(Ticket.class));
    }

    /**
     * <b>Happy Path: Ticket im Status DONE mit Review- und Done-Datum erstellen (User Story 3)</b>
     * <p>
     * <b>Voraussetzung:</b> Status DONE, beide Datumsangaben sind vorhanden und chronologisch korrekt.
     * <b>Erwartetes Ergebnis:</b> Ticket wird erfolgreich angelegt.
     */
    @Test
    @DisplayName("US3 - Happy Path: DONE Ticket mit Review- und Done-Datum")
    void createTicket_HappyPath_DoneTicketWithDates() {
        OffsetDateTime reviewDate = OffsetDateTime.of(2026, 9, 1, 10, 0, 0, 0, ZoneOffset.UTC);
        OffsetDateTime doneDate = OffsetDateTime.of(2026, 9, 2, 16, 0, 0, 0, ZoneOffset.UTC);

        TicketCreateRequest request = TicketCreateRequest.builder()
                .title("Projekt initialisieren")
                .status(TicketStatus.DONE)
                .reviewDate(reviewDate)
                .doneDate(doneDate)
                .employeeId(1L)
                .build();

        Ticket doneTicket = Ticket.builder()
                .id(11L)
                .title("Projekt initialisieren")
                .status(TicketStatus.DONE)
                .reviewDate(reviewDate)
                .doneDate(doneDate)
                .employeeId(1L)
                .build();

        when(employeeServiceClient.employeeExists(1L)).thenReturn(true);
        when(ticketRepository.save(any(Ticket.class))).thenReturn(doneTicket);

        TicketResponse response = ticketService.createTicket(request);

        assertNotNull(response);
        assertEquals(TicketStatus.DONE, response.getStatus());
        assertEquals(reviewDate, response.getReviewDate());
        assertEquals(doneDate, response.getDoneDate());
    }

    /**
     * <b>Sad Path 1: Zugewiesener Mitarbeiter existiert nicht (User Story 3)</b>
     * <p>
     * <b>Voraussetzung:</b> Die ID wird vom Employee Service nicht gefunden.
     * <b>Erwartetes Ergebnis:</b> Eine {@link EmployeeNotFoundException} wird geworfen und kein Ticket gespeichert.
     */
    @Test
    @DisplayName("US3 - Sad Path: Nicht existierender Mitarbeiter wirft EmployeeNotFoundException")
    void createTicket_SadPath_EmployeeNotFound() {
        TicketCreateRequest request = TicketCreateRequest.builder()
                .title("Task")
                .status(TicketStatus.OPEN)
                .employeeId(999L)
                .build();

        when(employeeServiceClient.employeeExists(999L)).thenReturn(false);

        EmployeeNotFoundException exception = assertThrows(EmployeeNotFoundException.class, () ->
                ticketService.createTicket(request)
        );

        assertTrue(exception.getMessage().contains("999"));
        verify(ticketRepository, never()).save(any(Ticket.class));
    }

    /**
     * <b>Sad Path 2: Review-Datum bei OPEN-Status verboten (User Story 3)</b>
     * <p>
     * <b>Voraussetzung:</b> Status ist OPEN, aber ein Review-Datum wurde angegeben.
     * <b>Erwartetes Ergebnis:</b> Eine {@link InvalidTicketStateException} wird geworfen.
     */
    @Test
    @DisplayName("US3 - Sad Path: OPEN Ticket mit Review-Datum ist unzulässig")
    void createTicket_SadPath_OpenTicketWithReviewDate() {
        TicketCreateRequest request = TicketCreateRequest.builder()
                .title("Task")
                .status(TicketStatus.OPEN)
                .reviewDate(OffsetDateTime.now(ZoneOffset.UTC))
                .employeeId(1L)
                .build();

        InvalidTicketStateException exception = assertThrows(InvalidTicketStateException.class, () ->
                ticketService.createTicket(request)
        );

        assertTrue(exception.getMessage().contains("weder ein Review-Datum noch ein Done-Datum"));
        verify(ticketRepository, never()).save(any(Ticket.class));
    }

    /**
     * <b>Sad Path 3: Done-Datum bei REVIEW-Status verboten (User Story 3)</b>
     * <p>
     * <b>Voraussetzung:</b> Status ist REVIEW, aber ein Done-Datum wurde angegeben.
     * <b>Erwartetes Ergebnis:</b> Eine {@link InvalidTicketStateException} wird geworfen.
     */
    @Test
    @DisplayName("US3 - Sad Path: REVIEW Ticket mit Done-Datum ist unzulässig")
    void createTicket_SadPath_ReviewTicketWithDoneDate() {
        TicketCreateRequest request = TicketCreateRequest.builder()
                .title("Task")
                .status(TicketStatus.REVIEW)
                .doneDate(OffsetDateTime.now(ZoneOffset.UTC))
                .employeeId(1L)
                .build();

        InvalidTicketStateException exception = assertThrows(InvalidTicketStateException.class, () ->
                ticketService.createTicket(request)
        );

        assertTrue(exception.getMessage().contains("darf noch kein Done-Datum haben"));
        verify(ticketRepository, never()).save(any(Ticket.class));
    }

    /**
     * <b>Sad Path 4: Done-Datum liegt vor dem Review-Datum (User Story 3)</b>
     * <p>
     * <b>Voraussetzung:</b> doneDate liegt zeitlich vor reviewDate.
     * <b>Erwartetes Ergebnis:</b> Eine {@link InvalidTicketStateException} wird geworfen.
     */
    @Test
    @DisplayName("US3 - Sad Path: Done-Datum vor Review-Datum ist unzulässig")
    void createTicket_SadPath_DoneDateBeforeReviewDate() {
        OffsetDateTime reviewDate = OffsetDateTime.of(2026, 9, 5, 10, 0, 0, 0, ZoneOffset.UTC);
        OffsetDateTime doneDate = OffsetDateTime.of(2026, 9, 1, 10, 0, 0, 0, ZoneOffset.UTC);

        TicketCreateRequest request = TicketCreateRequest.builder()
                .title("Task")
                .status(TicketStatus.DONE)
                .reviewDate(reviewDate)
                .doneDate(doneDate)
                .employeeId(1L)
                .build();

        InvalidTicketStateException exception = assertThrows(InvalidTicketStateException.class, () ->
                ticketService.createTicket(request)
        );

        assertTrue(exception.getMessage().contains("darf nicht vor dem Review-Datum liegen"));
        verify(ticketRepository, never()).save(any(Ticket.class));
    }
}
