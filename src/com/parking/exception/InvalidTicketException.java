package com.parking.exception;

/**
 * Exception thrown when an invalid or non-existent ticket ID is provided.
 */
public class InvalidTicketException extends RuntimeException {
    private final String ticketId;

    public InvalidTicketException(String ticketId) {
        super(String.format("Invalid or non-existent ticket: %s", ticketId));
        this.ticketId = ticketId;
    }

    public String getTicketId() {
        return ticketId;
    }
}

