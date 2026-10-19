package ch.tbz.ticketservice.controller;

import ch.tbz.ticketservice.dto.MetricsResponse;
import ch.tbz.ticketservice.service.MetricsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST Controller für Kennzahlen-Endpunkte (Z1 – Continuous Improvement).
 *
 * <p>Stellt unter GET /api/metrics einen maschinenlesbaren Bericht mit aktuellen
 * KPIs des Ticket-Systems bereit. Die Daten werden direkt aus der Datenbank
 * erhoben und zur Laufzeit berechnet.</p>
 *
 * <p>Zweck: Grundlage für einen Geschäftsreport und für zukünftige
 * Verbesserungsmassnahmen im DevOps-Prozess.</p>
 */
@RestController
@RequestMapping("/api/metrics")
@RequiredArgsConstructor
public class MetricsController {

    private final MetricsService metricsService;

    /**
     * Liefert alle erhobenen Kennzahlen des Ticket-Systems zum aktuellen Zeitpunkt.
     *
     * @return 200 OK mit einem {@link MetricsResponse}-Objekt
     */
    @GetMapping
    public ResponseEntity<MetricsResponse> getMetrics() {
        MetricsResponse metrics = metricsService.collectMetrics();
        return ResponseEntity.ok(metrics);
    }
}
