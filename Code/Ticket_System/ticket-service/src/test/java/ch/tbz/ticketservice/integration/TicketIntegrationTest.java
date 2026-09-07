package ch.tbz.ticketservice.integration;

import ch.tbz.ticketservice.client.EmployeeServiceClient;
import ch.tbz.ticketservice.repository.TicketRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integrationstests für den {@code ticket-service}.
 * <p>
 * Testet das Zusammenspiel aus WebMvc-Endpunkten, Validierungslogik und
 * PostgreSQL-Datenbank ('ticket_db'). Der Aufruf zum externen Employee-Service
 * wird über eine TestConfiguration gemockt, um Netzwerkabhängigkeiten zu vermeiden.
 */
@SpringBootTest
@Transactional
@Import(TicketIntegrationTest.IntegrationTestConfig.class)
class TicketIntegrationTest {

    @TestConfiguration
    static class IntegrationTestConfig {
        @Bean
        @Primary
        public EmployeeServiceClient employeeServiceClient() {
            EmployeeServiceClient mock = Mockito.mock(EmployeeServiceClient.class);
            Mockito.when(mock.employeeExists(1L)).thenReturn(true);
            Mockito.when(mock.employeeExists(9999L)).thenReturn(false);
            return mock;
        }
    }

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private TicketRepository ticketRepository;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    /**
     * <b>Integrationstest: End-to-End-Flow für Ticket-Erstellung in echter DB (US 3)</b>
     * <p>
     * <b>Ablauf:</b>
     * 1. Sendet einen realen POST-Request an /api/tickets mit Status OPEN und Mitarbeiter-ID 1.
     * 2. Erwartet 201 Created und verifiziert die generierte ID.
     * 3. Prüft direkt in der PostgreSQL-Tabelle 'ticket', ob das Ticket persistiert wurde.
     * 4. Ruft alle Tickets per GET /api/tickets ab.
     */
    @Test
    @DisplayName("Integration: Ticket über HTTP anlegen, in DB speichern und abrufen")
    void createAndFetchTicket_IntegrationFlow() throws Exception {
        long countBefore = ticketRepository.count();

        String newTicketJson = """
                {
                    "title": "Integration Test Ticket",
                    "description": "Prüft das Zusammenspiel mit der echten PostgreSQL-Datenbank",
                    "status": "OPEN",
                    "employeeId": 1
                }
                """;

        // 1. POST: Ticket anlegen
        mockMvc.perform(post("/api/tickets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(newTicketJson))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.title", is("Integration Test Ticket")))
                .andExpect(jsonPath("$.status", is("OPEN")))
                .andExpect(jsonPath("$.employeeId", is(1)));

        // 2. DB prüfen: 1 Ticket mehr in der Datenbank
        assertEquals(countBefore + 1, ticketRepository.count());

        // 3. GET: Alle Tickets abrufen und verifizieren
        mockMvc.perform(get("/api/tickets"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[*].title", hasItem("Integration Test Ticket")));
    }

    /**
     * <b>Integrationstest: Ungültiger Status / Datumsangaben werden abgewiesen</b>
     * <p>
     * <b>Ablauf:</b>
     * 1. Sendet einen Request mit Status REVIEW, aber verbotenem Done-Datum.
     * 2. Erwartet 400 Bad Request.
     * 3. Stellt sicher, dass kein ungültiger Datensatz in die Datenbank geschrieben wurde.
     */
    @Test
    @DisplayName("Integration: Status-Datums-Konflikt wird abgewiesen und nicht in DB gespeichert")
    void createTicket_InvalidStateConflict_NotSavedInDb() throws Exception {
        long countBefore = ticketRepository.count();

        String invalidStateJson = """
                {
                    "title": "Fehlerhaftes Ticket",
                    "status": "REVIEW",
                    "reviewDate": "2026-09-01T10:00:00Z",
                    "doneDate": "2026-09-02T12:00:00Z",
                    "employeeId": 1
                }
                """;

        mockMvc.perform(post("/api/tickets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidStateJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", is("Ungültiger Ticket-Status oder Datumsangabe")));

        assertEquals(countBefore, ticketRepository.count());
    }

    /**
     * <b>Integrationstest: Nicht gefundener Mitarbeiter bricht Transaktion ab</b>
     * <p>
     * <b>Ablauf:</b>
     * 1. Sendet Request mit employeeId 9999 (nicht vorhanden).
     * 2. Erwartet 400 Bad Request mit Fehlermeldung.
     * 3. Keine Persistierung in der Datenbank.
     */
    @Test
    @DisplayName("Integration: Unbekannte Mitarbeiter-ID bricht Erfassung ab")
    void createTicket_UnknownEmployee_RejectsAndNotSaved() throws Exception {
        long countBefore = ticketRepository.count();

        String nonExistingEmployeeJson = """
                {
                    "title": "Ticket ohne echten Mitarbeiter",
                    "status": "OPEN",
                    "employeeId": 9999
                }
                """;

        mockMvc.perform(post("/api/tickets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(nonExistingEmployeeJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", is("Mitarbeiter nicht gefunden")));

        assertEquals(countBefore, ticketRepository.count());
    }
}
