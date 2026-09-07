package ch.tbz.employeeservice.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

/**
 * JPA Entity zur Abbildung eines Mitarbeiters in der Datenbanktabelle 'employee'.
 */
@Entity
@Table(name = "employee")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Employee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "first_name", nullable = false, length = 50)
    private String firstName;

    @Column(name = "last_name", nullable = false, length = 50)
    private String lastName;

    @Column(name = "joined_date", nullable = false)
    private LocalDate joinedDate;

    @Column(name = "skill_level", nullable = false)
    private Integer skillLevel;
}
