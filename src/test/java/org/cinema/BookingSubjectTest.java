package org.cinema;

import org.cinema.model.enums.BookingEvent;
import org.cinema.observer.BookingObserver;
import org.cinema.observer.BookingSubject;
import org.cinema.observer.EmailNotificationObserver;
import org.cinema.observer.InventoryObserver;
import org.cinema.observer.SMSNotificationObserver;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for BookingSubject (Observer Pattern - Subject)
 * Tests observer attachment, detachment, and notification
 */
public class BookingSubjectTest {

    private BookingSubject subject;

    @BeforeEach
    public void setUp() {
        subject = new BookingSubject();
    }

    @Test
    public void attach_singleObserver_increasesCount() {
        // Arrange
        BookingObserver observer = new EmailNotificationObserver();

        // Act
        subject.attach(observer);

        // Assert
        assertEquals(1, subject.getObserverCount(), "Should have 1 observer attached");
    }

    @Test
    public void attach_multipleObservers_increasesCount() {
        // Arrange
        BookingObserver observer1 = new EmailNotificationObserver();
        BookingObserver observer2 = new SMSNotificationObserver();
        BookingObserver observer3 = new InventoryObserver();

        // Act
        subject.attach(observer1);
        subject.attach(observer2);
        subject.attach(observer3);

        // Assert
        assertEquals(3, subject.getObserverCount(), "Should have 3 observers attached");
    }

    @Test
    public void attach_duplicateObserver_doesNotIncrease() {
        // Arrange
        BookingObserver observer = new EmailNotificationObserver();

        // Act
        subject.attach(observer);
        subject.attach(observer); // Try to attach again

        // Assert
        assertEquals(1, subject.getObserverCount(), "Duplicate observer should not be added");
    }

    @Test
    public void detach_existingObserver_decreasesCount() {
        // Arrange
        BookingObserver observer1 = new EmailNotificationObserver();
        BookingObserver observer2 = new SMSNotificationObserver();
        subject.attach(observer1);
        subject.attach(observer2);

        // Act
        subject.detach(observer1);

        // Assert
        assertEquals(1, subject.getObserverCount(), "Should have 1 observer remaining");
    }

    @Test
    public void detach_nonExistentObserver_doesNothing() {
        // Arrange
        BookingObserver observer1 = new EmailNotificationObserver();
        BookingObserver observer2 = new SMSNotificationObserver();
        subject.attach(observer1);

        // Act
        subject.detach(observer2); // Try to detach observer that wasn't attached

        // Assert
        assertEquals(1, subject.getObserverCount(), "Count should remain unchanged");
    }

    @Test
    public void detach_allObservers_resultInZeroCount() {
        // Arrange
        BookingObserver observer1 = new EmailNotificationObserver();
        BookingObserver observer2 = new SMSNotificationObserver();
        subject.attach(observer1);
        subject.attach(observer2);

        // Act
        subject.detach(observer1);
        subject.detach(observer2);

        // Assert
        assertEquals(0, subject.getObserverCount(), "Should have no observers");
    }

    @Test
    public void notifyObservers_callsUpdateOnAllObservers() {
        // Arrange
        TestObserver observer1 = new TestObserver();
        TestObserver observer2 = new TestObserver();
        TestObserver observer3 = new TestObserver();

        subject.attach(observer1);
        subject.attach(observer2);
        subject.attach(observer3);

        // Act
        subject.notifyObservers("BOOKING-123", BookingEvent.CONFIRMED,
                "test@example.com", "555-0000", "Test booking");

        // Assert
        assertEquals(1, observer1.getUpdateCount(), "Observer1 should receive 1 update");
        assertEquals(1, observer2.getUpdateCount(), "Observer2 should receive 1 update");
        assertEquals(1, observer3.getUpdateCount(), "Observer3 should receive 1 update");
        assertEquals("BOOKING-123", observer1.getLastBookingId(), "Should receive correct booking ID");
        assertEquals(BookingEvent.CONFIRMED, observer1.getLastEvent(), "Should receive correct event");
    }

    @Test
    public void notifyObservers_withNoObservers_doesNotThrowException() {
        // Act & Assert - should not throw any exception
        assertDoesNotThrow(() -> subject.notifyObservers("BOOKING-123", BookingEvent.CREATED,
                "test@example.com", "555-0000", "Test"));
    }

    @Test
    public void notifyObservers_multipleEvents_callsUpdateMultipleTimes() {
        // Arrange
        TestObserver observer = new TestObserver();
        subject.attach(observer);

        // Act
        subject.notifyObservers("BOOKING-1", BookingEvent.CREATED,
                "test@example.com", "555-0000", "Created");
        subject.notifyObservers("BOOKING-1", BookingEvent.PAYMENT_COMPLETED,
                "test@example.com", "555-0000", "Paid");
        subject.notifyObservers("BOOKING-1", BookingEvent.CONFIRMED,
                "test@example.com", "555-0000", "Confirmed");

        // Assert
        assertEquals(3, observer.getUpdateCount(), "Observer should receive 3 updates");
        assertEquals(BookingEvent.CONFIRMED, observer.getLastEvent(), "Last event should be CONFIRMED");
    }

    @Test
    public void notifyObservers_afterDetach_doesNotCallUpdate() {
        // Arrange
        TestObserver observer1 = new TestObserver();
        TestObserver observer2 = new TestObserver();
        subject.attach(observer1);
        subject.attach(observer2);

        // Act
        subject.notifyObservers("BOOKING-1", BookingEvent.CREATED,
                "test@example.com", "555-0000", "Test");
        subject.detach(observer1); // Detach first observer
        subject.notifyObservers("BOOKING-2", BookingEvent.CONFIRMED,
                "test@example.com", "555-0000", "Test");

        // Assert
        assertEquals(1, observer1.getUpdateCount(), "Detached observer should not receive second notification");
        assertEquals(2, observer2.getUpdateCount(), "Attached observer should receive both notifications");
    }

    @Test
    public void notifyObservers_withDifferentEventTypes_sendsCorrectEvent() {
        // Arrange
        TestObserver observer = new TestObserver();
        subject.attach(observer);

        BookingEvent[] events = {
                BookingEvent.CREATED,
                BookingEvent.CONFIRMED,
                BookingEvent.CANCELLED,
                BookingEvent.PAYMENT_COMPLETED,
                BookingEvent.PAYMENT_FAILED,
                BookingEvent.SEATS_RESERVED,
                BookingEvent.SEATS_RELEASED
        };

        // Act & Assert
        for (BookingEvent event : events) {
            observer.reset();
            subject.notifyObservers("BOOKING-TEST", event,
                    "test@example.com", "555-0000", "Test details");
            assertEquals(event, observer.getLastEvent(), "Should receive correct event: " + event);
        }
    }

    @Test
    public void getObserverCount_initiallyZero() {
        // Assert
        assertEquals(0, subject.getObserverCount(), "Initial observer count should be 0");
    }

    /**
     * Test observer implementation for testing purposes
     */
    private static class TestObserver implements BookingObserver {
        private int updateCount = 0;
        private String lastBookingId;
        private BookingEvent lastEvent;
        private String lastCustomerEmail;
        private String lastCustomerPhone;
        private String lastDetails;

        @Override
        public void update(String bookingId, BookingEvent event, String customerEmail,
                           String customerPhone, String details) {
            updateCount++;
            this.lastBookingId = bookingId;
            this.lastEvent = event;
            this.lastCustomerEmail = customerEmail;
            this.lastCustomerPhone = customerPhone;
            this.lastDetails = details;
        }

        public int getUpdateCount() {
            return updateCount;
        }

        public String getLastBookingId() {
            return lastBookingId;
        }

        public BookingEvent getLastEvent() {
            return lastEvent;
        }

        public String getLastCustomerEmail() {
            return lastCustomerEmail;
        }

        public String getLastCustomerPhone() {
            return lastCustomerPhone;
        }

        public String getLastDetails() {
            return lastDetails;
        }

        public void reset() {
            updateCount = 0;
            lastBookingId = null;
            lastEvent = null;
            lastCustomerEmail = null;
            lastCustomerPhone = null;
            lastDetails = null;
        }
    }
}
