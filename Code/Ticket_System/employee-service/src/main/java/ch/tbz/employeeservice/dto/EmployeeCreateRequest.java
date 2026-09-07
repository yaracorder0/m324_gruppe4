package ch.tbz.employeeservice.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDate;

/**
 * DTO für das Erfassen eines neuen Mitarbeiters (User Story 1).
 * Enthält Validierungsregeln mit klaren Fehlermeldungen für den Administrator.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmployeeCreateRequest {

    @NotBlank(message = "Der Vorname darf nicht leer sein.")
    @Size(max = 50, message = "Der Vorname darf höchstens 50 Zeichen lang sein.")
    private String firstName;

    @NotBlank(message = "Der Nachname darf nicht leer sein.")
    @Size(max = 50, message = "Der Nachname darf höchstens 50 Zeichen lang sein.")
    private String lastName;

    @NotNull(message = "Das Beitrittsdatum darf nicht fehlen (Format: JJJJ-MM-TT).")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate joinedDate;

    @NotNull(message = "Der Skill-Level darf nicht fehlen.")
    @Min(value = 1, message = "Der Skill-Level muss mindestens 1 sein (Bereich 1 bis 5).")
    @Max(value = 5, message = "Der Skill-Level darf maximal 5 sein (Bereich 1 bis 5).")
    private Integer skillLevel;
}
