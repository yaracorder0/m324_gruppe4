package ch.tbz.employeeservice.service;

import ch.tbz.employeeservice.dto.EmployeeCreateRequest;
import ch.tbz.employeeservice.dto.EmployeeResponse;
import ch.tbz.employeeservice.exception.ResourceNotFoundException;
import ch.tbz.employeeservice.model.Employee;
import ch.tbz.employeeservice.repository.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Service-Schicht für die Mitarbeiterverwaltung.
 * Implementiert die Geschäftslogik für User Story 1 (Erfassen) und User Story 2 (Auslesen).
 */
@Service
@RequiredArgsConstructor
public class EmployeeService {

    private final EmployeeRepository employeeRepository;

    /**
     * Erfasst einen neuen Mitarbeiter im System (User Story 1).
     *
     * @param request Die Daten des neuen Mitarbeiters
     * @return Der gespeicherte Mitarbeiter mit generierter ID
     * @throws IllegalArgumentException falls Validierungsregeln verletzt werden
     */
    @Transactional
    public EmployeeResponse createEmployee(EmployeeCreateRequest request) {
        if (request.getSkillLevel() == null || request.getSkillLevel() < 1 || request.getSkillLevel() > 5) {
            throw new IllegalArgumentException("Der Skill-Level muss eine Zahl zwischen 1 und 5 sein.");
        }

        Employee employee = Employee.builder()
                .firstName(request.getFirstName().trim())
                .lastName(request.getLastName().trim())
                .joinedDate(request.getJoinedDate())
                .skillLevel(request.getSkillLevel())
                .build();

        Employee saved = employeeRepository.save(employee);
        return mapToResponse(saved);
    }

    /**
     * Liest alle erfassten Mitarbeiter aus der Datenbank aus (User Story 2).
     *
     * @return Liste aller Mitarbeiter
     */
    @Transactional(readOnly = true)
    public List<EmployeeResponse> getAllEmployees() {
        return employeeRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    /**
     * Sucht einen Mitarbeiter anhand seiner eindeutigen ID.
     * Dient unter anderem zur Validierung durch den Ticket Service.
     *
     * @param id Eindeutige Mitarbeiter-ID
     * @return Der gefundene Mitarbeiter
     * @throws ResourceNotFoundException wenn kein Mitarbeiter mit dieser ID existiert
     */
    @Transactional(readOnly = true)
    public EmployeeResponse getEmployeeById(Long id) {
        return employeeRepository.findById(id)
                .map(this::mapToResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Mitarbeiter mit ID " + id + " wurde nicht gefunden."));
    }

    /**
     * Mappt eine JPA Employee Entity auf das Response DTO.
     */
    private EmployeeResponse mapToResponse(Employee employee) {
        return EmployeeResponse.builder()
                .id(employee.getId())
                .firstName(employee.getFirstName())
                .lastName(employee.getLastName())
                .joinedDate(employee.getJoinedDate())
                .skillLevel(employee.getSkillLevel())
                .build();
    }
}
