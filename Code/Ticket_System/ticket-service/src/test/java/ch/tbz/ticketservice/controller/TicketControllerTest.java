package ch.tbz.ticketservice.controller;

import ch.tbz.ticketservice.dto.TicketCreateRequest;
import ch.tbz.ticketservice.dto.TicketResponse;
import ch.tbz.ticketservice.exception.EmployeeNotFoundException;
import ch.tbz.ticketservice.exception.GlobalExceptionHandler;
import ch.tbz.ticketservice.exception.InvalidTicketStateException;
import ch.tbz.ticketservice.model.TicketStatus;
import ch.tbz.ticketservice.service.TicketService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Isolierte Controller-Unit-Tests für {@link TicketController}.
 * <p>
 * Verwendet MockMvc im Standalone-Modus mit gemockter Service-Schicht und registriertem
 * {@link GlobalExceptionHandler}.
 */
@ExtendWith(MockitoExtension.class)
class TicketControllerTest {

    private MockMvc mockMvc;

    @Mock
    private TicketService ticketService;

    @InjectMocks
    private TicketController ticketController;

    private TicketResponse sampleTicketResponse;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(ticketController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();

        sampleTicketResponse = TicketResponse.builder()
                .id(10L)
                .title("Login Bug fixen")
                .description("Fehler beim Login beheben")
                .status(TicketStatus.OPEN)
                .employeeId(1L)
                .build();
    }

    /**
     * <b>Happy Path: POST /api/tickets (User Story 3)</b>
     * <p>
     * <b>Voraussetzung:</b> Valider JSON-Request mit allen Pflichtfeldern.
     * <b>Erwartetes Ergebnis:</b> Status 201 Created, Location Header und Ticket-Daten mit generierter ID.
     */
    @Test
    @DisplayName("POST /api/tickets - Happy Path: Ticket erfolgreich erstellen")
    void createTicket_ValidRequest_Returns201Created() throws Exception {
        String jsonPayload = """
                {
                    "title": "Login Bug fixen",
                    "description": "Fehler beim Login beheben",
                    "status": "OPEN",
                    "employeeId": 1
                }
                """;

        when(ticketService.createTicket(any(TicketCreateRequest.class))).thenReturn(sampleTicketResponse);

        mockMvc.perform(post("/api/tickets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonPayload))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/api/tickets/10"))
                .andExpect(jsonPath("$.id", is(10)))
                .andExpect(jsonPath("$.title", is("Login Bug fixen")))
                .andExpect(jsonPath("$.status", is("OPEN")))
                .andExpect(jsonPath("$.employeeId", is(1)));
    }

    /**
     * <b>Sad Path 1: POST /api/tickets - Fehlender Titel (User Story 3)</b>
     * <p>
     * <b>Voraussetzung:</b> Request-Body ohne Titel.
     * <b>Erwartetes Ergebnis:</b> Status 400 Bad Request mit Fehlermeldung zum Feld 'title'.
     */
    @Test
    @DisplayName("POST /api/tickets - Sad Path: Fehlender Titel liefert 400 Bad Request")
    void createTicket_MissingTitle_Returns400BadRequest() throws Exception {
        String jsonPayload = """
                {
                    "title": "",
                    "status": "OPEN",
                    "employeeId": 1
                }
                """;

        mockMvc.perform(post("/api/tickets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonPayload))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", is("Validierungsfehler")))
                .andExpect(jsonPath("$.validationErrors.title", containsString("darf nicht leer sein")));
    }

    /**
     * <b>Sad Path 2: POST /api/tickets - Fehlender Mitarbeiter (User Story 3)</b>
     * <p>
     * <b>Voraussetzung:</b> Request-Body ohne employeeId.
     * <b>Erwartetes Ergebnis:</b> Status 400 Bad Request mit Fehlermeldung zur Zuweisungspflicht.
     */
    @Test
    @DisplayName("POST /api/tickets - Sad Path: Fehlende employeeId liefert 400 Bad Request")
    void createTicket_MissingEmployeeId_Returns400BadRequest() throws Exception {
        String jsonPayload = """
                {
                    "title": "Neues Feature",
                    "status": "OPEN"
                }
                """;

        mockMvc.perform(post("/api/tickets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonPayload))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.validationErrors.employeeId", containsString("immer zugewiesen werden")));
    }

    /**
     * <b>Sad Path 3: POST /api/tickets - Ungültiger Status / Datumsangaben (User Story 3)</b>
     * <p>
     * <b>Voraussetzung:</b> Service wirft InvalidTicketStateException wegen unzulässigem Review-Datum.
     * <b>Erwartetes Ergebnis:</b> Status 400 Bad Request mit klarer Erklärung.
     */
    @Test
    @DisplayName("POST /api/tickets - Sad Path: Ungültige Status-Datum-Kombination liefert 400")
    void createTicket_InvalidTicketState_Returns400BadRequest() throws Exception {
        String jsonPayload = """
                {
                    "title": "Neues Feature",
                    "status": "OPEN",
                    "reviewDate": "2026-09-01T10:00:00Z",
                    "employeeId": 1
                }
                """;

        when(ticketService.createTicket(any(TicketCreateRequest.class)))
                .thenThrow(new InvalidTicketStateException("Ein Ticket im Status OPEN darf weder ein Review-Datum noch ein Done-Datum haben."));

        mockMvc.perform(post("/api/tickets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonPayload))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", is("Ungültiger Ticket-Status oder Datumsangabe")))
                .andExpect(jsonPath("$.message", containsString("OPEN darf weder ein Review-Datum")));
    }

    /**
     * <b>Sad Path 4: POST /api/tickets - Mitarbeiter existiert nicht im Employee Service (User Story 3)</b>
     * <p>
     * <b>Voraussetzung:</b> Service wirft EmployeeNotFoundException.
     * <b>Erwartetes Ergebnis:</b> Status 400 Bad Request mit Handlungsanweisung für den Administrator.
     */
    @Test
    @DisplayName("POST /api/tickets - Sad Path: Nicht gefundener Mitarbeiter liefert 400")
    void createTicket_EmployeeNotFound_Returns400BadRequest() throws Exception {
        String jsonPayload = """
                {
                    "title": "Neues Feature",
                    "status": "OPEN",
                    "employeeId": 999
                }
                """;

        when(ticketService.createTicket(any(TicketCreateRequest.class)))
                .thenThrow(new EmployeeNotFoundException("Mitarbeiter mit ID 999 existiert nicht im Employee-Service."));

        mockMvc.perform(post("/api/tickets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonPayload))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", is("Mitarbeiter nicht gefunden")))
                .andExpect(jsonPath("$.message", containsString("999")));
    }
}
