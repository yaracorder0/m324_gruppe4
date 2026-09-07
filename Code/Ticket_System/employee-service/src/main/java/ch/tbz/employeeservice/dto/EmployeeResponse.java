package ch.tbz.employeeservice.dto;

import lombok.*;

import java.time.LocalDate;

/**
 * DTO für die Rückgabe von Mitarbeiterdaten (User Story 1 & 2).
 * Liefert alle Attribute inklusive der generierten eindeutigen ID zurück.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmployeeResponse {
    private Long id;
    private String firstName;
    private String lastName;
    private LocalDate joinedDate;
    private Integer skillLevel;
}
