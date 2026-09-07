package ch.tbz.ticketservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Wird geworfen, wenn Datumsangaben (Review- oder Done-Datum) nicht zum aktuellen Status des Tickets passen.
 */
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class InvalidTicketStateException extends RuntimeException {
    public InvalidTicketStateException(String message) {
        super(message);
    }
}
