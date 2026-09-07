package ch.tbz.employeeservice.controller;

import ch.tbz.employeeservice.dto.EmployeeCreateRequest;
import ch.tbz.employeeservice.dto.EmployeeResponse;
import ch.tbz.employeeservice.service.EmployeeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

/**
 * REST Controller für Mitarbeiter-Endpunkte.
 * Bietet Schnittstellen für User Story 1 (Mitarbeiter erfassen) und User Story 2 (Mitarbeiter auslesen).
 */
@RestController
@RequestMapping("/api/employees")
@RequiredArgsConstructor
public class EmployeeController {

    private final EmployeeService employeeService;

    /**
     * User Story 1: Neuer Mitarbeiter erfassen.
     *
     * @param request Valider Request-Body mit Mitarbeiterattributen
     * @return 201 Created mit dem erstellten Mitarbeiter und Location-Header
     */
    @PostMapping
    public ResponseEntity<EmployeeResponse> createEmployee(@Valid @RequestBody EmployeeCreateRequest request) {
        EmployeeResponse created = employeeService.createEmployee(request);
        URI location = URI.create("/api/employees/" + created.getId());
        return ResponseEntity.created(location).body(created);
    }

    /**
     * User Story 2: Alle erfassten Mitarbeiter auslesen.
     *
     * @return 200 OK mit Liste aller Mitarbeiter. Falls keine vorhanden, enthält der Header 'X-Info-Message' einen Hinweis.
     */
    @GetMapping
    public ResponseEntity<List<EmployeeResponse>> getAllEmployees() {
        List<EmployeeResponse> employees = employeeService.getAllEmployees();
        HttpHeaders headers = new HttpHeaders();
        if (employees.isEmpty()) {
            headers.add("X-Info-Message", "Es sind derzeit keine Mitarbeiter erfasst.");
        }
        return new ResponseEntity<>(employees, headers, HttpStatus.OK);
    }

    /**
     * Einzelnen Mitarbeiter nach ID abrufen.
     * Wird insbesondere vom Ticket Service zur Validierung der employee_id aufgerufen.
     *
     * @param id Die ID des Mitarbeiters
     * @return 200 OK mit dem gefundenen Mitarbeiter
     */
    @GetMapping("/{id}")
    public ResponseEntity<EmployeeResponse> getEmployeeById(@PathVariable Long id) {
        EmployeeResponse employee = employeeService.getEmployeeById(id);
        return ResponseEntity.ok(employee);
    }
}
