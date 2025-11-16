package org.cinema.observer;

import org.cinema.model.enums.BookingEvent;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

/**
 * Concrete Observer for inventory management
 * Tracks seat availability and booking statistics
 * Student 3: ERNAR
 * Pattern: Observer (Behavioral)
 */
public class InventoryObserver implements BookingObserver {
    private Map<String, Integer> seatInventory;
    private Map<String, Integer> bookingCounts;
    private int totalSeatsReserved;
    private int totalSeatsReleased;
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public InventoryObserver() {
        this.seatInventory = new HashMap<>();
        this.bookingCounts = new HashMap<>();
        this.totalSeatsReserved = 0;
        this.totalSeatsReleased = 0;

        // Initialize default inventory
        initializeInventory();
    }

    private void initializeInventory() {
        // Sample theater inventory (can be expanded)
        seatInventory.put("HALL_1", 100);
        seatInventory.put("HALL_2", 80);
        seatInventory.put("HALL_3", 120);
        seatInventory.put("VIP_HALL", 50);
    }

    @Override
    public void update(String bookingId, BookingEvent event, String customerEmail,
                       String customerPhone, String details) {
        String timestamp = LocalDateTime.now().format(formatter);

        switch (event) {
            case CREATED:
            case SEATS_RESERVED:
                handleSeatsReserved(bookingId, details, timestamp);
                break;

            case CANCELLED:
            case SEATS_RELEASED:
                handleSeatsReleased(bookingId, details, timestamp);
                break;

            case CONFIRMED:
                handleBookingConfirmed(bookingId, details, timestamp);
                break;

            default:
                logInventoryEvent(event, bookingId, timestamp);
                break;
        }
    }

    /**
     * Handle seat reservation
     */
    private void handleSeatsReserved(String bookingId, String details, String timestamp) {
        // Extract seat count from details (simplified)
        int seatsCount = extractSeatCount(details);
        totalSeatsReserved += seatsCount;

        System.out.println("\n[InventoryObserver] SEATS RESERVED");
        System.out.println("├─ Booking ID: " + bookingId);
        System.out.println("├─ Seats Reserved: " + seatsCount);
        System.out.println("├─ Total Reserved Today: " + totalSeatsReserved);
        System.out.println("└─ Timestamp: " + timestamp);

        updateInventoryStats(bookingId, seatsCount);
    }

    /**
     * Handle seat release (cancellation)
     */
    private void handleSeatsReleased(String bookingId, String details, String timestamp) {
        int seatsCount = extractSeatCount(details);
        totalSeatsReleased += seatsCount;

        System.out.println("\n[InventoryObserver] SEATS RELEASED");
        System.out.println("├─ Booking ID: " + bookingId);
        System.out.println("├─ Seats Released: " + seatsCount);
        System.out.println("├─ Total Released Today: " + totalSeatsReleased);
        System.out.println("├─ Net Reserved: " + (totalSeatsReserved - totalSeatsReleased));
        System.out.println("└─ Timestamp: " + timestamp);
    }

    /**
     * Handle booking confirmation
     */
    private void handleBookingConfirmed(String bookingId, String details, String timestamp) {
        bookingCounts.merge(bookingId, 1, Integer::sum);

        System.out.println("\n[InventoryObserver] BOOKING CONFIRMED");
        System.out.println("├─ Booking ID: " + bookingId);
        System.out.println("├─ Total Confirmed Bookings: " + bookingCounts.size());
        System.out.println("└─ Timestamp: " + timestamp);
    }

    /**
     * Log general inventory events
     */
    private void logInventoryEvent(BookingEvent event, String bookingId, String timestamp) {
        System.out.println("\n[InventoryObserver] Event Logged: " + event);
        System.out.println("├─ Booking ID: " + bookingId);
        System.out.println("└─ Timestamp: " + timestamp);
    }

    /**
     * Update inventory statistics
     */
    private void updateInventoryStats(String bookingId, int seatsCount) {
        bookingCounts.put(bookingId, seatsCount);
    }

    /**
     * Extract seat count from details string
     * Simplified implementation - assumes format includes number
     */
    private int extractSeatCount(String details) {
        // Try to extract number from details (e.g., "2 seats", "3 tickets")
        if (details.contains("seat") || details.contains("ticket")) {
            String[] parts = details.split(" ");
            for (String part : parts) {
                try {
                    int count = Integer.parseInt(part);
                    if (count > 0 && count < 100) {
                        return count;
                    }
                } catch (NumberFormatException e) {
                    // Continue searching
                }
            }
        }
        return 1; // Default to 1 seat
    }

    /**
     * Get inventory statistics
     */
    public void printInventoryReport() {
        System.out.println("\n╔════════════════════════════════════════════════════════╗");
        System.out.println("║           INVENTORY REPORT                             ║");
        System.out.println("╚════════════════════════════════════════════════════════╝");
        System.out.println("Total Bookings Tracked: " + bookingCounts.size());
        System.out.println("Total Seats Reserved: " + totalSeatsReserved);
        System.out.println("Total Seats Released: " + totalSeatsReleased);
        System.out.println("Net Seats Occupied: " + (totalSeatsReserved - totalSeatsReleased));
        System.out.println("════════════════════════════════════════════════════════");
    }

    // Getters for testing
    public int getTotalSeatsReserved() {
        return totalSeatsReserved;
    }

    public int getTotalSeatsReleased() {
        return totalSeatsReleased;
    }

    public int getBookingCount() {
        return bookingCounts.size();
    }
}