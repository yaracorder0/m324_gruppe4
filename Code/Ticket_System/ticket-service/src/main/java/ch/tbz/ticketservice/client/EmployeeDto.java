package ch.tbz.ticketservice.client;

import lombok.*;

import java.time.LocalDate;

/**
 * DTO zur Deserialisierung von Mitarbeiterdaten aus dem Employee Service.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmployeeDto {
    private Long id;
    private String firstName;
    private String lastName;
    private LocalDate joinedDate;
    private Integer skillLevel;
}
