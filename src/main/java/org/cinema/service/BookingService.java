package org.cinema.service;

import org.cinema.model.enums.BookingEvent;
import org.cinema.observer.BookingSubject;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Service layer for managing bookings
 * Coordinates business logic and observer notifications
 * Student 3: ERNAR
 */
public class BookingService {
    private BookingSubject bookingSubject;
    private Map<String, BookingData> bookings;

    public BookingService(BookingSubject bookingSubject) {
        this.bookingSubject = bookingSubject;
        this.bookings = new HashMap<>();
    }

    /**
     * Create a new booking
     */
    public String createBooking(String customerEmail, String customerPhone,
                                String movieTitle, int seatCount, double totalAmount) {
        String bookingId = generateBookingId();

        // Create booking data
        BookingData bookingData = new BookingData(
                bookingId, customerEmail, customerPhone,
                movieTitle, seatCount, totalAmount
        );

        bookings.put(bookingId, bookingData);

        // Notify observers
        String details = String.format("Movie: %s, Seats: %d, Amount: $%.2f",
                movieTitle, seatCount, totalAmount);
        bookingSubject.notifyObservers(bookingId, BookingEvent.CREATED,
                customerEmail, customerPhone, details);

        System.out.println("\n[BookingService] Booking created: " + bookingId);
        return bookingId;
    }

    /**
     * Confirm a booking after successful payment
     */
    public boolean confirmBooking(String bookingId) {
        BookingData booking = bookings.get(bookingId);
        if (booking == null) {
            System.out.println("[BookingService] ERROR: Booking not found: " + bookingId);
            return false;
        }

        if (booking.isConfirmed()) {
            System.out.println("[BookingService] Booking already confirmed: " + bookingId);
            return true;
        }

        booking.setConfirmed(true);

        String details = String.format("Movie: %s, Seats: %d, Total: $%.2f - CONFIRMED",
                booking.getMovieTitle(), booking.getSeatCount(),
                booking.getTotalAmount());
        bookingSubject.notifyObservers(bookingId, BookingEvent.CONFIRMED,
                booking.getCustomerEmail(),
                booking.getCustomerPhone(), details);

        System.out.println("[BookingService] Booking confirmed: " + bookingId);
        return true;
    }

    /**
     * Cancel a booking
     */
    public boolean cancelBooking(String bookingId) {
        BookingData booking = bookings.get(bookingId);
        if (booking == null) {
            System.out.println("[BookingService] ERROR: Booking not found: " + bookingId);
            return false;
        }

        if (booking.isCancelled()) {
            System.out.println("[BookingService] Booking already cancelled: " + bookingId);
            return true;
        }

        booking.setCancelled(true);

        String details = String.format("Booking cancelled. Refund: $%.2f",
                booking.getTotalAmount());
        bookingSubject.notifyObservers(bookingId, BookingEvent.CANCELLED,
                booking.getCustomerEmail(),
                booking.getCustomerPhone(), details);

        // Release seats
        bookingSubject.notifyObservers(bookingId, BookingEvent.SEATS_RELEASED,
                booking.getCustomerEmail(),
                booking.getCustomerPhone(),
                booking.getSeatCount() + " seats released");

        System.out.println("[BookingService] Booking cancelled: " + bookingId);
        return true;
    }

    /**
     * Reserve seats for a booking
     */
    public void reserveSeats(String bookingId) {
        BookingData booking = bookings.get(bookingId);
        if (booking == null) {
            System.out.println("[BookingService] ERROR: Booking not found: " + bookingId);
            return;
        }

        String details = booking.getSeatCount() + " seats reserved for " + booking.getMovieTitle();
        bookingSubject.notifyObservers(bookingId, BookingEvent.SEATS_RESERVED,
                booking.getCustomerEmail(),
                booking.getCustomerPhone(), details);
    }

    /**
     * Get booking details
     */
    public BookingData getBooking(String bookingId) {
        return bookings.get(bookingId);
    }

    /**
     * Get all bookings
     */
    public Map<String, BookingData> getAllBookings() {
        return new HashMap<>(bookings);
    }

    /**
     * Get the booking subject for observer management
     */
    public BookingSubject getBookingSubject() {
        return bookingSubject;
    }

    /**
     * Generate unique booking ID
     */
    private String generateBookingId() {
        return "BK-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    /**
     * Inner class to hold booking data
     */
    public static class BookingData {
        private String bookingId;
        private String customerEmail;
        private String customerPhone;
        private String movieTitle;
        private int seatCount;
        private double totalAmount;
        private boolean confirmed;
        private boolean cancelled;

        public BookingData(String bookingId, String customerEmail, String customerPhone,
                           String movieTitle, int seatCount, double totalAmount) {
            this.bookingId = bookingId;
            this.customerEmail = customerEmail;
            this.customerPhone = customerPhone;
            this.movieTitle = movieTitle;
            this.seatCount = seatCount;
            this.totalAmount = totalAmount;
            this.confirmed = false;
            this.cancelled = false;
        }

        // Getters and setters
        public String getBookingId() { return bookingId; }
        public String getCustomerEmail() { return customerEmail; }
        public String getCustomerPhone() { return customerPhone; }
        public String getMovieTitle() { return movieTitle; }
        public int getSeatCount() { return seatCount; }
        public double getTotalAmount() { return totalAmount; }
        public boolean isConfirmed() { return confirmed; }
        public void setConfirmed(boolean confirmed) { this.confirmed = confirmed; }
        public boolean isCancelled() { return cancelled; }
        public void setCancelled(boolean cancelled) { this.cancelled = cancelled; }
    }
}