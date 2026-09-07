package ch.tbz.employeeservice.integration;

import ch.tbz.employeeservice.repository.EmployeeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
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
 * Integrationstests für den {@code employee-service}.
 * <p>
 * Diese Tests fahren den vollständigen Spring-Boot-Anwendungskontext hoch und
 * testen den realen Datenfluss vom HTTP-Endpunkt bis in die PostgreSQL-Datenbank ('employee_db').
 * Dank {@link Transactional} werden alle Testdaten nach dem Test automatisch zurückgerollt.
 */
@SpringBootTest
@Transactional
class EmployeeIntegrationTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private EmployeeRepository employeeRepository;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    /**
     * <b>Integrationstest: End-to-End-Flow für Mitarbeiter-Erfassung und Abruf (US 1 & US 2)</b>
     * <p>
     * <b>Ablauf:</b>
     * 1. Sendet einen realen POST-Request an /api/employees.
     * 2. Prüft, ob der Status 201 Created ist und eine ID generiert wurde.
     * 3. Verifiziert direkt in der PostgreSQL-Datenbank, dass der Datensatz persistiert wurde.
     * 4. Ruft den Mitarbeiter per GET /api/employees wieder ab.
     */
    @Test
    @DisplayName("Integration: Mitarbeiter über HTTP anlegen, in DB speichern und abrufen")
    void createAndFetchEmployee_IntegrationFlow() throws Exception {
        long countBefore = employeeRepository.count();

        String newEmployeeJson = """
                {
                    "firstName": "Integration",
                    "lastName": "Tester",
                    "joinedDate": "2024-01-10",
                    "skillLevel": 5
                }
                """;

        // 1. POST: Mitarbeiter anlegen
        mockMvc.perform(post("/api/employees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(newEmployeeJson))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.firstName", is("Integration")))
                .andExpect(jsonPath("$.lastName", is("Tester")))
                .andExpect(jsonPath("$.skillLevel", is(5)));

        // 2. DB-Zustand prüfen: Es muss genau 1 Mitarbeiter mehr in der Datenbank sein
        assertEquals(countBefore + 1, employeeRepository.count());

        // 3. GET: Alle Mitarbeiter abrufen und prüfen, ob der neue enthalten ist
        mockMvc.perform(get("/api/employees"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[*].firstName", hasItem("Integration")))
                .andExpect(jsonPath("$[*].lastName", hasItem("Tester")));
    }

    /**
     * <b>Integrationstest: Fehlgeschlagene Validierung führt nicht zu DB-Einträgen</b>
     * <p>
     * <b>Ablauf:</b>
     * 1. Sendet einen ungültigen POST-Request (Skilllevel = 9).
     * 2. Erwartet 400 Bad Request.
     * 3. Verifiziert, dass die Anzahl der Einträge in der Datenbank unverändert geblieben ist.
     */
    @Test
    @DisplayName("Integration: Ungültiger Request wird abgewiesen und nicht in der DB gespeichert")
    void createEmployee_InvalidSkillLevel_NotPersistedInDb() throws Exception {
        long countBefore = employeeRepository.count();

        String invalidEmployeeJson = """
                {
                    "firstName": "Fehlerhafter",
                    "lastName": "Eintrag",
                    "joinedDate": "2024-01-10",
                    "skillLevel": 9
                }
                """;

        mockMvc.perform(post("/api/employees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidEmployeeJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", is("Validierungsfehler")));

        // DB-Zustand darf sich nicht verändert haben
        assertEquals(countBefore, employeeRepository.count());
    }
}
