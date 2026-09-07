package ch.tbz.employeeservice.service;

import ch.tbz.employeeservice.dto.EmployeeCreateRequest;
import ch.tbz.employeeservice.dto.EmployeeResponse;
import ch.tbz.employeeservice.exception.ResourceNotFoundException;
import ch.tbz.employeeservice.model.Employee;
import ch.tbz.employeeservice.repository.EmployeeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit Tests für die Service-Klasse {@link EmployeeService}.
 * <p>
 * Diese Tests sind vollständig isoliert und nutzen Mockito, um Abhängigkeiten zur
 * Datenbank auszuschliessen. Dadurch können alle Teammitglieder die Tests unabhängig
 * von einer laufenden Datenbankumgebung ausführen.
 */
@ExtendWith(MockitoExtension.class)
class EmployeeServiceTest {

    @Mock
    private EmployeeRepository employeeRepository;

    @InjectMocks
    private EmployeeService employeeService;

    private Employee sampleEmployee;
    private EmployeeCreateRequest validRequest;

    @BeforeEach
    void setUp() {
        sampleEmployee = Employee.builder()
                .id(1L)
                .firstName("Max")
                .lastName("Muster")
                .joinedDate(LocalDate.of(2021, 5, 15))
                .skillLevel(4)
                .build();

        validRequest = EmployeeCreateRequest.builder()
                .firstName("Max")
                .lastName("Muster")
                .joinedDate(LocalDate.of(2021, 5, 15))
                .skillLevel(4)
                .build();
    }

    /**
     * <b>Happy Path: Mitarbeiter erfassen (User Story 1)</b>
     * <p>
     * <b>Voraussetzung:</b> Gültige Mitarbeiterdaten werden übergeben.
     * <b>Erwartetes Ergebnis:</b> Der Mitarbeiter wird gespeichert und mit generierter ID zurückgegeben.
     */
    @Test
    @DisplayName("US1 - Happy Path: Neuer Mitarbeiter wird erfolgreich erstellt")
    void createEmployee_HappyPath_ReturnsCreatedEmployee() {
        when(employeeRepository.save(any(Employee.class))).thenReturn(sampleEmployee);

        EmployeeResponse response = employeeService.createEmployee(validRequest);

        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("Max", response.getFirstName());
        assertEquals("Muster", response.getLastName());
        assertEquals(LocalDate.of(2021, 5, 15), response.getJoinedDate());
        assertEquals(4, response.getSkillLevel());

        verify(employeeRepository, times(1)).save(any(Employee.class));
    }

    /**
     * <b>Sad Path: Skill-Level zu hoch (User Story 1)</b>
     * <p>
     * <b>Voraussetzung:</b> Der angegebene Skill-Level liegt ausserhalb des erlaubten Bereichs (z. B. 6).
     * <b>Erwartetes Ergebnis:</b> Eine {@link IllegalArgumentException} wird geworfen und es wird nichts gespeichert.
     */
    @Test
    @DisplayName("US1 - Sad Path: Skill-Level über 5 wirft IllegalArgumentException")
    void createEmployee_SkillLevelAboveMax_ThrowsException() {
        validRequest.setSkillLevel(6);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                employeeService.createEmployee(validRequest)
        );

        assertTrue(exception.getMessage().contains("zwischen 1 und 5"));
        verify(employeeRepository, never()).save(any(Employee.class));
    }

    /**
     * <b>Sad Path: Skill-Level zu niedrig (User Story 1)</b>
     * <p>
     * <b>Voraussetzung:</b> Der angegebene Skill-Level ist kleiner als 1 (z. B. 0).
     * <b>Erwartetes Ergebnis:</b> Eine {@link IllegalArgumentException} wird geworfen.
     */
    @Test
    @DisplayName("US1 - Sad Path: Skill-Level unter 1 wirft IllegalArgumentException")
    void createEmployee_SkillLevelBelowMin_ThrowsException() {
        validRequest.setSkillLevel(0);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                employeeService.createEmployee(validRequest)
        );

        assertTrue(exception.getMessage().contains("zwischen 1 und 5"));
        verify(employeeRepository, never()).save(any(Employee.class));
    }

    /**
     * <b>Happy Path: Alle Mitarbeiter auslesen (User Story 2)</b>
     * <p>
     * <b>Voraussetzung:</b> Die Datenbank enthält Mitarbeiter.
     * <b>Erwartetes Ergebnis:</b> Eine Liste aller Mitarbeiter mit allen Feldern wird zurückgeliefert.
     */
    @Test
    @DisplayName("US2 - Happy Path: Alle vorhandenen Mitarbeiter werden zurückgegeben")
    void getAllEmployees_HappyPath_ReturnsList() {
        when(employeeRepository.findAll()).thenReturn(List.of(sampleEmployee));

        List<EmployeeResponse> result = employeeService.getAllEmployees();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Max", result.get(0).getFirstName());
        verify(employeeRepository, times(1)).findAll();
    }

    /**
     * <b>Sad Path / Edge Case: Keine Mitarbeiter vorhanden (User Story 2)</b>
     * <p>
     * <b>Voraussetzung:</b> Die Datenbank ist leer.
     * <b>Erwartetes Ergebnis:</b> Eine leere Liste wird zurückgegeben (keine NullPointerException).
     */
    @Test
    @DisplayName("US2 - Edge Case: Leere Liste, falls keine Mitarbeiter existieren")
    void getAllEmployees_EmptyDatabase_ReturnsEmptyList() {
        when(employeeRepository.findAll()).thenReturn(Collections.emptyList());

        List<EmployeeResponse> result = employeeService.getAllEmployees();

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(employeeRepository, times(1)).findAll();
    }

    /**
     * <b>Happy Path: Mitarbeiter nach ID suchen (Hilfsmethode für US3)</b>
     * <p>
     * <b>Voraussetzung:</b> Mitarbeiter mit gegebener ID existiert.
     * <b>Erwartetes Ergebnis:</b> Der passende Mitarbeiter wird zurückgegeben.
     */
    @Test
    @DisplayName("Mitarbeiter nach ID abrufen - Happy Path")
    void getEmployeeById_ExistingId_ReturnsEmployee() {
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(sampleEmployee));

        EmployeeResponse response = employeeService.getEmployeeById(1L);

        assertNotNull(response);
        assertEquals(1L, response.getId());
        verify(employeeRepository, times(1)).findById(1L);
    }

    /**
     * <b>Sad Path: Mitarbeiter nach ID suchen, der nicht existiert</b>
     * <p>
     * <b>Voraussetzung:</b> ID existiert nicht in der Datenbank.
     * <b>Erwartetes Ergebnis:</b> Eine {@link ResourceNotFoundException} wird geworfen.
     */
    @Test
    @DisplayName("Mitarbeiter nach ID abrufen - Sad Path: ID existiert nicht")
    void getEmployeeById_NonExistingId_ThrowsResourceNotFoundException() {
        when(employeeRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () ->
                employeeService.getEmployeeById(999L)
        );

        verify(employeeRepository, times(1)).findById(999L);
    }
}
