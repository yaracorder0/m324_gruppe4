package ch.tbz.ticketservice.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.Collections;
import java.util.List;

/**
 * REST Client zur Kommunikation mit dem Employee Service.
 * <p>
 * Erfüllt die Vorgabe aus User Story 3:
 * "Der Endpoint von User Story 3 muss die Mitarbeiter aus dem Endpoint von User Story 2 auslesen
 * und nicht aus der Datenbank."
 */
@Component
public class EmployeeServiceClient {

    private final RestClient restClient;

    public EmployeeServiceClient(@Value("${employee.service.url:http://localhost:8081/api/employees}") String employeeBaseUrl) {
        this.restClient = RestClient.builder()
                .baseUrl(employeeBaseUrl)
                .build();
    }

    /**
     * Ruft alle Mitarbeiter vom Employee Service (User Story 2 Endpoint) ab.
     *
     * @return Liste der Mitarbeiter
     */
    public List<EmployeeDto> getAllEmployees() {
        try {
            List<EmployeeDto> employees = restClient.get()
                    .retrieve()
                    .body(new ParameterizedTypeReference<List<EmployeeDto>>() {});
            return employees != null ? employees : Collections.emptyList();
        } catch (Exception e) {
            // Falls der Service nicht erreichbar ist oder Fehler liefert
            return Collections.emptyList();
        }
    }

    /**
     * Prüft, ob ein Mitarbeiter mit der gegebenen ID existiert, indem die Liste
     * aus dem Endpoint von User Story 2 gefiltert wird.
     *
     * @param employeeId Die zu prüfende Mitarbeiter-ID
     * @return true wenn vorhanden, sonst false
     */
    public boolean employeeExists(Long employeeId) {
        if (employeeId == null) {
            return false;
        }
        return getAllEmployees().stream()
                .anyMatch(emp -> employeeId.equals(emp.getId()));
    }
}
