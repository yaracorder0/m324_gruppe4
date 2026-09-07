package ch.tbz.employeeservice.controller;

import ch.tbz.employeeservice.dto.EmployeeCreateRequest;
import ch.tbz.employeeservice.dto.EmployeeResponse;
import ch.tbz.employeeservice.exception.GlobalExceptionHandler;
import ch.tbz.employeeservice.exception.ResourceNotFoundException;
import ch.tbz.employeeservice.service.EmployeeService;
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

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Isolierte Controller-Unit-Tests für {@link EmployeeController}.
 * <p>
 * Verwendet MockMvc im Standalone-Modus mit gemockter Service-Schicht und registriertem
 * {@link GlobalExceptionHandler}. Läuft blitzschnell und völlig unabhängig von Spring-Context
 * oder Datenbank.
 */
@ExtendWith(MockitoExtension.class)
class EmployeeControllerTest {

    private MockMvc mockMvc;

    @Mock
    private EmployeeService employeeService;

    @InjectMocks
    private EmployeeController employeeController;

    private EmployeeResponse sampleResponse;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(employeeController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();

        sampleResponse = EmployeeResponse.builder()
                .id(1L)
                .firstName("Max")
                .lastName("Muster")
                .joinedDate(LocalDate.of(2021, 5, 15))
                .skillLevel(4)
                .build();
    }

    /**
     * <b>Happy Path: POST /api/employees (User Story 1)</b>
     * <p>
     * <b>Voraussetzung:</b> Vollständige und valide JSON-Eingabedaten.
     * <b>Erwartetes Ergebnis:</b> Status 201 Created, Location Header gesetzt und JSON-Response mit generierter ID.
     */
    @Test
    @DisplayName("POST /api/employees - Happy Path: Mitarbeiter erfolgreich erstellen")
    void createEmployee_ValidRequest_Returns201Created() throws Exception {
        String jsonPayload = """
                {
                    "firstName": "Max",
                    "lastName": "Muster",
                    "joinedDate": "2021-05-15",
                    "skillLevel": 4
                }
                """;

        when(employeeService.createEmployee(any(EmployeeCreateRequest.class))).thenReturn(sampleResponse);

        mockMvc.perform(post("/api/employees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonPayload))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/api/employees/1"))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.firstName", is("Max")))
                .andExpect(jsonPath("$.lastName", is("Muster")))
                .andExpect(jsonPath("$.skillLevel", is(4)));
    }

    /**
     * <b>Sad Path 1: POST /api/employees - Fehlender Vorname (User Story 1)</b>
     * <p>
     * <b>Voraussetzung:</b> Request-Body ohne Vorname (oder leer).
     * <b>Erwartetes Ergebnis:</b> Status 400 Bad Request mit klarer Fehlermeldung für den Administrator.
     */
    @Test
    @DisplayName("POST /api/employees - Sad Path: Leerer Vorname liefert 400 Bad Request")
    void createEmployee_MissingFirstName_Returns400BadRequest() throws Exception {
        String invalidJson = """
                {
                    "firstName": "",
                    "lastName": "Muster",
                    "joinedDate": "2021-05-15",
                    "skillLevel": 4
                }
                """;

        mockMvc.perform(post("/api/employees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", is("Validierungsfehler")))
                .andExpect(jsonPath("$.validationErrors.firstName", containsString("darf nicht leer sein")));
    }

    /**
     * <b>Sad Path 2: POST /api/employees - Ungültiger Skill-Level (User Story 1)</b>
     * <p>
     * <b>Voraussetzung:</b> Skill-Level liegt ausserhalb 1-5 (z.B. 7).
     * <b>Erwartetes Ergebnis:</b> Status 400 Bad Request mit Fehlermeldung zur Korrektur.
     */
    @Test
    @DisplayName("POST /api/employees - Sad Path: Skill-Level > 5 liefert 400 Bad Request")
    void createEmployee_SkillLevelOutOfRange_Returns400BadRequest() throws Exception {
        String invalidJson = """
                {
                    "firstName": "Max",
                    "lastName": "Muster",
                    "joinedDate": "2021-05-15",
                    "skillLevel": 7
                }
                """;

        mockMvc.perform(post("/api/employees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.validationErrors.skillLevel", containsString("maximal 5")));
    }

    /**
     * <b>Happy Path: GET /api/employees (User Story 2)</b>
     * <p>
     * <b>Voraussetzung:</b> Mitarbeiter sind im System vorhanden.
     * <b>Erwartetes Ergebnis:</b> Status 200 OK und eine formatierte JSON-Liste aller Mitarbeiter.
     */
    @Test
    @DisplayName("GET /api/employees - Happy Path: Liste aller Mitarbeiter abrufen")
    void getAllEmployees_Returns200AndList() throws Exception {
        when(employeeService.getAllEmployees()).thenReturn(List.of(sampleResponse));

        mockMvc.perform(get("/api/employees"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].firstName", is("Max")));
    }

    /**
     * <b>Sad Path / Edge Case: GET /api/employees - Keine Mitarbeiter vorhanden (User Story 2)</b>
     * <p>
     * <b>Voraussetzung:</b> Keine Mitarbeiter erfasst.
     * <b>Erwartetes Ergebnis:</b> Status 200 OK mit leerer Liste und Hinweismeldung im X-Info-Message Header.
     */
    @Test
    @DisplayName("GET /api/employees - Edge Case: Leere Liste mit Info-Meldung")
    void getAllEmployees_WhenEmpty_Returns200WithInfoMessage() throws Exception {
        when(employeeService.getAllEmployees()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/employees"))
                .andExpect(status().isOk())
                .andExpect(header().string("X-Info-Message", "Es sind derzeit keine Mitarbeiter erfasst."))
                .andExpect(jsonPath("$", hasSize(0)));
    }

    /**
     * <b>Happy Path: GET /api/employees/{id}</b>
     * <p>
     * <b>Voraussetzung:</b> Mitarbeiter mit ID 1 existiert.
     * <b>Erwartetes Ergebnis:</b> Status 200 OK mit Mitarbeiterdaten.
     */
    @Test
    @DisplayName("GET /api/employees/{id} - Happy Path: Mitarbeiter gefunden")
    void getEmployeeById_ExistingId_Returns200() throws Exception {
        when(employeeService.getEmployeeById(1L)).thenReturn(sampleResponse);

        mockMvc.perform(get("/api/employees/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.firstName", is("Max")));
    }

    /**
     * <b>Sad Path: GET /api/employees/{id}</b>
     * <p>
     * <b>Voraussetzung:</b> Mitarbeiter mit ID 999 existiert nicht.
     * <b>Erwartetes Ergebnis:</b> Status 404 Not Found mit passender Fehlermeldung.
     */
    @Test
    @DisplayName("GET /api/employees/{id} - Sad Path: Mitarbeiter nicht gefunden (404)")
    void getEmployeeById_NotFound_Returns404() throws Exception {
        when(employeeService.getEmployeeById(999L))
                .thenThrow(new ResourceNotFoundException("Mitarbeiter mit ID 999 wurde nicht gefunden."));

        mockMvc.perform(get("/api/employees/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error", is("Nicht gefunden")))
                .andExpect(jsonPath("$.message", containsString("999")));
    }
}
