package org.cinema.model.enums;

/**
 * Booking event types for Observer pattern
 * Student 3: ERNAR
 */
public enum BookingEvent {
    CREATED,           // Booking initially created
    CONFIRMED,         // Booking confirmed after payment
    CANCELLED,         // Booking cancelled by user
    PAYMENT_COMPLETED, // Payment successfully processed
    PAYMENT_FAILED,    // Payment processing failed
    SEATS_RESERVED,    // Seats reserved for booking
    SEATS_RELEASED     // Seats released after cancellation
}