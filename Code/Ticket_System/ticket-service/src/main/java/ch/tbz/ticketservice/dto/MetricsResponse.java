package ch.tbz.ticketservice.dto;

import lombok.*;

import java.util.Map;

/**
 * DTO für die Rückgabe aller gesammelten Kennzahlen des Ticket-Systems.
 * Wird vom MetricsController als Antwort auf GET /api/metrics zurückgegeben.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MetricsResponse {

    // ---- Ticket-Volumen ----

    /** Gesamtanzahl aller Tickets in der Datenbank. */
    private long totalTickets;

    /** Anzahl Tickets je Status (OPEN, IN_PROGRESS, REVIEW, DONE). */
    private Map<String, Long> ticketsByStatus;

    // ---- Durchlaufzeiten ----

    /**
     * Durchschnittliche Durchlaufzeit (in Stunden) von Erstellung bis Done
     * für alle abgeschlossenen Tickets.
     * Wird aus review_date und done_date berechnet.
     */
    private Double avgReviewToDoneHours;

    // ---- Workload / Mitarbeiter ----

    /** Anzahl offener + in-progress Tickets pro Mitarbeiter-ID. */
    private Map<Long, Long> openTicketsPerEmployee;

    /**
     * Mitarbeiter-ID mit den meisten offenen Tickets (höchste Auslastung).
     * Null, wenn keine offenen Tickets vorhanden sind.
     */
    private Long mostLoadedEmployeeId;

    // ---- CI/CD Pipeline ----

    /** Anzahl aller Tickets, die sich aktuell im Status REVIEW befinden (= Review-Queue-Grösse). */
    private long ticketsInReview;

    /** Anzahl abgeschlossener (DONE) Tickets – entspricht dem Throughput. */
    private long ticketsDone;

    /**
     * Abschlussrate in Prozent: ticketsDone / totalTickets * 100.
     * Zeigt, wie viel Prozent aller erfassten Tasks erledigt sind.
     */
    private double completionRatePercent;

    // ---- Zeitstempel ----

    /** ISO-8601 Zeitstempel der Kennzahlen-Erhebung (Server-Zeit). */
    private String collectedAt;
}
