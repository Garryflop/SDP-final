package org.cinema.observer;

import org.cinema.model.enums.BookingEvent;

/**
 * Observer interface for booking notifications
 * Student 3: ERNAR
 * Pattern: Observer (Behavioral)
 */
public interface BookingObserver {
    /**
     * Update observer when booking event occurs
     * @param bookingId The ID of the booking
     * @param event The type of event that occurred
     * @param customerEmail Customer's email address
     * @param customerPhone Customer's phone number
     * @param details Additional details about the event
     */
    void update(String bookingId, BookingEvent event, String customerEmail,
                String customerPhone, String details);
}