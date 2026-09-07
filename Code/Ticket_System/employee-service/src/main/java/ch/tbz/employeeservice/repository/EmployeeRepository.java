package ch.tbz.employeeservice.repository;

import ch.tbz.employeeservice.model.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA Repository für den Datenbankzugriff auf Employee-Entitäten.
 */
@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {
}
