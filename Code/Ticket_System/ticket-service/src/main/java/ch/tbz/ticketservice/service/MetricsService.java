package ch.tbz.ticketservice.service;

import ch.tbz.ticketservice.dto.MetricsResponse;
import ch.tbz.ticketservice.model.Ticket;
import ch.tbz.ticketservice.model.TicketStatus;
import ch.tbz.ticketservice.repository.TicketRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Service zur Berechnung und Erhebung von Kennzahlen (KPIs) des Ticket-Systems.
 *
 * Erhobene Kennzahlen:
 *  1. Gesamtanzahl Tickets
 *  2. Tickets je Status (OPEN / IN_PROGRESS / REVIEW / DONE)
 *  3. Durchschnittliche Durchlaufzeit Review → Done (in Stunden)
 *  4. Offene Tickets pro Mitarbeiter (Auslastung)
 *  5. Meistbelasteter Mitarbeiter
 *  6. Tickets in Review (Review-Queue-Grösse)
 *  7. Abgeschlossene Tickets (Throughput)
 *  8. Abschlussrate in Prozent
 *
 * Alle Daten werden direkt aus der Ticket-Datenbank erhoben – keine erfundenen Werte.
 */
@Service
@RequiredArgsConstructor
public class MetricsService {

    private final TicketRepository ticketRepository;

    /**
     * Erhebt alle Kennzahlen zum aktuellen Zeitpunkt und gibt sie als DTO zurück.
     *
     * @return MetricsResponse mit allen KPIs, versehen mit dem Erhebungszeitpunkt.
     */
    @Transactional(readOnly = true)
    public MetricsResponse collectMetrics() {

        // 1. Gesamt
        long total = ticketRepository.count();

        // 2. Tickets je Status
        Map<String, Long> byStatus = new LinkedHashMap<>();
        for (TicketStatus s : TicketStatus.values()) {
            byStatus.put(s.name(), ticketRepository.countByStatus(s));
        }

        // 3. Durchschnittliche Durchlaufzeit Review → Done
        List<Ticket> doneTickets = ticketRepository
                .findByStatusAndReviewDateIsNotNullAndDoneDateIsNotNull(TicketStatus.DONE);

        Double avgReviewToDoneHours = null;
        if (!doneTickets.isEmpty()) {
            double totalHours = doneTickets.stream()
                    .mapToDouble(t -> Duration
                            .between(t.getReviewDate(), t.getDoneDate())
                            .toMinutes() / 60.0)
                    .filter(h -> h >= 0) // negative Werte (Datenfehler) ignorieren
                    .average()
                    .orElse(0.0);
            avgReviewToDoneHours = Math.round(totalHours * 100.0) / 100.0;
        }

        // 4. Offene Tickets pro Mitarbeiter
        List<Object[]> rawOpenPerEmployee = ticketRepository.countOpenTicketsPerEmployee();
        Map<Long, Long> openPerEmployee = new LinkedHashMap<>();
        Long mostLoadedId = null;
        long maxLoad = 0;

        for (Object[] row : rawOpenPerEmployee) {
            Long empId = (Long) row[0];
            Long count = (Long) row[1];
            openPerEmployee.put(empId, count);
            if (count > maxLoad) {
                maxLoad = count;
                mostLoadedId = empId;
            }
        }

        // 5. Review-Queue und Throughput
        long inReview = byStatus.getOrDefault("REVIEW", 0L);
        long done = byStatus.getOrDefault("DONE", 0L);

        // 6. Abschlussrate
        double completionRate = total > 0
                ? Math.round((done * 100.0 / total) * 100.0) / 100.0
                : 0.0;

        // 7. Erhebungszeitpunkt
        String collectedAt = OffsetDateTime.now().format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);

        return MetricsResponse.builder()
                .totalTickets(total)
                .ticketsByStatus(byStatus)
                .avgReviewToDoneHours(avgReviewToDoneHours)
                .openTicketsPerEmployee(openPerEmployee)
                .mostLoadedEmployeeId(mostLoadedId)
                .ticketsInReview(inReview)
                .ticketsDone(done)
                .completionRatePercent(completionRate)
                .collectedAt(collectedAt)
                .build();
    }
}
