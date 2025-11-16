package org.cinema;

import org.cinema.model.enums.BookingEvent;
import org.cinema.observer.InventoryObserver;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for InventoryObserver
 * Tests seat inventory tracking and booking statistics
 */
public class InventoryObserverTest {

    private InventoryObserver observer;

    @BeforeEach
    public void setUp() {
        observer = new InventoryObserver();
    }

    @Test
    public void update_withCreatedEvent_reservesSeats() {
        // Act
        observer.update("BOOKING-001", BookingEvent.CREATED,
                "test@example.com", "555-0000", "2 seats reserved");

        // Assert
        assertEquals(2, observer.getTotalSeatsReserved(), "Should reserve 2 seats");
        assertEquals(0, observer.getTotalSeatsReleased(), "No seats should be released");
    }

    @Test
    public void update_withSeatsReservedEvent_increasesReservedCount() {
        // Act
        observer.update("BOOKING-001", BookingEvent.SEATS_RESERVED,
                "test@example.com", "555-0000", "3 tickets for showtime");

        // Assert
        assertEquals(3, observer.getTotalSeatsReserved(), "Should reserve 3 seats");
    }

    @Test
    public void update_withMultipleReservations_accumulatesTotal() {
        // Act
        observer.update("BOOKING-001", BookingEvent.CREATED,
                "test1@example.com", "555-0001", "2 seats");
        observer.update("BOOKING-002", BookingEvent.CREATED,
                "test2@example.com", "555-0002", "4 tickets");
        observer.update("BOOKING-003", BookingEvent.SEATS_RESERVED,
                "test3@example.com", "555-0003", "1 seat");

        // Assert
        assertEquals(7, observer.getTotalSeatsReserved(), "Should reserve total of 7 seats");
    }

    @Test
    public void update_withCancelledEvent_releasesSeats() {
        // Arrange
        observer.update("BOOKING-001", BookingEvent.CREATED,
                "test@example.com", "555-0000", "3 seats");

        // Act
        observer.update("BOOKING-001", BookingEvent.CANCELLED,
                "test@example.com", "555-0000", "3 seats cancelled");

        // Assert
        assertEquals(3, observer.getTotalSeatsReserved(), "Reserved count should remain");
        assertEquals(3, observer.getTotalSeatsReleased(), "Should release 3 seats");
    }

    @Test
    public void update_withSeatsReleasedEvent_increasesReleasedCount() {
        // Act
        observer.update("BOOKING-001", BookingEvent.SEATS_RELEASED,
                "test@example.com", "555-0000", "2 seats released");

        // Assert
        assertEquals(2, observer.getTotalSeatsReleased(), "Should release 2 seats");
    }

    @Test
    public void update_withConfirmedEvent_increasesBookingCount() {
        // Act
        observer.update("BOOKING-001", BookingEvent.CONFIRMED,
                "test@example.com", "555-0000", "Booking confirmed");

        // Assert
        assertEquals(1, observer.getBookingCount(), "Should have 1 confirmed booking");
    }

    @Test
    public void update_withMultipleConfirmations_tracksAllBookings() {
        // Act
        observer.update("BOOKING-001", BookingEvent.CONFIRMED,
                "test1@example.com", "555-0001", "Details");
        observer.update("BOOKING-002", BookingEvent.CONFIRMED,
                "test2@example.com", "555-0002", "Details");
        observer.update("BOOKING-003", BookingEvent.CONFIRMED,
                "test3@example.com", "555-0003", "Details");

        // Assert
        assertEquals(3, observer.getBookingCount(), "Should have 3 confirmed bookings");
    }

    @Test
    public void update_withPaymentEvents_logsButDoesNotChangeInventory() {
        // Act
        observer.update("BOOKING-001", BookingEvent.PAYMENT_COMPLETED,
                "test@example.com", "555-0000", "Payment successful");
        observer.update("BOOKING-002", BookingEvent.PAYMENT_FAILED,
                "test@example.com", "555-0000", "Payment failed");

        // Assert
        assertEquals(0, observer.getTotalSeatsReserved(), "Payment events should not reserve seats");
        assertEquals(0, observer.getTotalSeatsReleased(), "Payment events should not release seats");
        assertEquals(0, observer.getBookingCount(), "Payment events should not count as confirmed bookings");
    }

    @Test
    public void update_reserveAndReleaseFlow_calculatesNetCorrectly() {
        // Act - Reserve seats for 3 bookings
        observer.update("BOOKING-001", BookingEvent.CREATED, "user1@test.com", "555-0001", "2 seats");
        observer.update("BOOKING-002", BookingEvent.CREATED, "user2@test.com", "555-0002", "4 tickets");
        observer.update("BOOKING-003", BookingEvent.CREATED, "user3@test.com", "555-0003", "3 seats");

        // Cancel one booking
        observer.update("BOOKING-002", BookingEvent.CANCELLED, "user2@test.com", "555-0002", "4 seats cancelled");

        // Assert
        assertEquals(9, observer.getTotalSeatsReserved(), "Total reserved: 2 + 4 + 3 = 9");
        assertEquals(4, observer.getTotalSeatsReleased(), "Released: 4 seats from BOOKING-002");
    }

    @Test
    public void update_withDetailsWithoutNumber_defaultsToOneSeat() {
        // Act
        observer.update("BOOKING-001", BookingEvent.CREATED,
                "test@example.com", "555-0000", "Booking created successfully");

        // Assert
        assertEquals(1, observer.getTotalSeatsReserved(), "Should default to 1 seat when no number found");
    }

    @Test
    public void update_withLargeNumber_extractsCorrectly() {
        // Act
        observer.update("BOOKING-001", BookingEvent.CREATED,
                "test@example.com", "555-0000", "Reserved 25 seats for group");

        // Assert
        assertEquals(25, observer.getTotalSeatsReserved(), "Should extract 25 seats");
    }

    @Test
    public void update_withInvalidNumber_ignoresAndDefaultsToOne() {
        // Act
        observer.update("BOOKING-001", BookingEvent.CREATED,
                "test@example.com", "555-0000", "Reserved 999 seats"); // Too large (> 100)

        // Assert
        assertEquals(1, observer.getTotalSeatsReserved(), "Should default to 1 for invalid numbers");
    }

    @Test
    public void getTotalSeatsReserved_initiallyZero() {
        // Assert
        assertEquals(0, observer.getTotalSeatsReserved(), "Initially should have 0 reserved seats");
    }

    @Test
    public void getTotalSeatsReleased_initiallyZero() {
        // Assert
        assertEquals(0, observer.getTotalSeatsReleased(), "Initially should have 0 released seats");
    }

    @Test
    public void getBookingCount_initiallyZero() {
        // Assert
        assertEquals(0, observer.getBookingCount(), "Initially should have 0 bookings");
    }

    @Test
    public void printInventoryReport_doesNotThrowException() {
        // Arrange
        observer.update("BOOKING-001", BookingEvent.CREATED, "test@example.com", "555-0000", "2 seats");
        observer.update("BOOKING-001", BookingEvent.CONFIRMED, "test@example.com", "555-0000", "Confirmed");

        // Act & Assert
        assertDoesNotThrow(() -> observer.printInventoryReport(), "Report printing should not throw exception");
    }

    @Test
    public void update_complexScenario_tracksAllMetricsCorrectly() {
        // Scenario: Multiple bookings with reservations, confirmations, and cancellations
        
        // Day 1: 3 bookings created
        observer.update("BOOKING-001", BookingEvent.CREATED, "user1@test.com", "555-0001", "2 seats");
        observer.update("BOOKING-002", BookingEvent.CREATED, "user2@test.com", "555-0002", "4 tickets");
        observer.update("BOOKING-003", BookingEvent.CREATED, "user3@test.com", "555-0003", "1 seat");

        // Day 1: 2 bookings confirmed
        observer.update("BOOKING-001", BookingEvent.CONFIRMED, "user1@test.com", "555-0001", "Confirmed");
        observer.update("BOOKING-003", BookingEvent.CONFIRMED, "user3@test.com", "555-0003", "Confirmed");

        // Day 2: 1 booking cancelled
        observer.update("BOOKING-002", BookingEvent.CANCELLED, "user2@test.com", "555-0002", "4 seats released");

        // Day 2: 1 new booking
        observer.update("BOOKING-004", BookingEvent.CREATED, "user4@test.com", "555-0004", "3 tickets");
        observer.update("BOOKING-004", BookingEvent.CONFIRMED, "user4@test.com", "555-0004", "Confirmed");

        // Assert
        assertEquals(10, observer.getTotalSeatsReserved(), "Total reserved: 2+4+1+3 = 10");
        assertEquals(4, observer.getTotalSeatsReleased(), "Total released: 4");
        assertEquals(3, observer.getBookingCount(), "Confirmed bookings: BOOKING-001, 003, 004");
    }
}
