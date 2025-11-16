package org.cinema.observer;

import org.cinema.model.enums.BookingEvent;
import java.util.ArrayList;
import java.util.List;

/**
 * Subject class that maintains list of observers and notifies them
 * Student 3: ERNAR
 * Pattern: Observer (Behavioral)
 */
public class BookingSubject {
    private List<BookingObserver> observers;

    public BookingSubject() {
        this.observers = new ArrayList<>();
    }

    /**
     * Attach an observer to the subject
     * @param observer The observer to attach
     */
    public void attach(BookingObserver observer) {
        if (!observers.contains(observer)) {
            observers.add(observer);
            System.out.println("[Observer] Attached: " + observer.getClass().getSimpleName());
        }
    }

    /**
     * Detach an observer from the subject
     * @param observer The observer to detach
     */
    public void detach(BookingObserver observer) {
        if (observers.remove(observer)) {
            System.out.println("[Observer] Detached: " + observer.getClass().getSimpleName());
        }
    }

    /**
     * Notify all observers about a booking event
     * @param bookingId The ID of the booking
     * @param event The type of event
     * @param customerEmail Customer's email
     * @param customerPhone Customer's phone
     * @param details Additional details
     */
    public void notifyObservers(String bookingId, BookingEvent event,
                                String customerEmail, String customerPhone, String details) {
        System.out.println("\n[Subject] Notifying " + observers.size() + " observers about " + event);
        for (BookingObserver observer : observers) {
            observer.update(bookingId, event, customerEmail, customerPhone, details);
        }
    }

    /**
     * Get count of attached observers
     * @return Number of observers
     */
    public int getObserverCount() {
        return observers.size();
    }
}
