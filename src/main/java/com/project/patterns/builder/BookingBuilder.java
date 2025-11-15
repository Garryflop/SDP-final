package com.project.patterns.builder;

import com.project.model.Booking;
import com.project.model.BookingStatus;
import com.project.model.Customer;
import com.project.model.Seat;
import com.project.patterns.strategy.PricingStrategy;
import org.cinema.model.Ticket;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class BookingBuilder {

    private Customer customer;
    private LocalDateTime showtime;
    private final List<Ticket> tickets = new ArrayList<>();
    private final List<Seat> seats = new ArrayList<>();
    private double totalPrice;
    private BookingStatus status = BookingStatus.PENDING;

    private LocalDateTime createdAt;
    private boolean totalCalculated = false;

    // === Fluent API methods ===

    public BookingBuilder setCustomer(Customer customer) {
        this.customer = customer;
        return this;
    }

    public BookingBuilder setShowtime(LocalDateTime showtime) {
        this.showtime = showtime;
        return this;
    }

    public BookingBuilder addTicket(Ticket ticket) {
        if (ticket != null) {
            this.tickets.add(ticket);
        }
        return this;
    }

    public BookingBuilder addSeat(Seat seat) {
        if (seat != null) {
            this.seats.add(seat);
        }
        return this;
    }

    /**
     * Uses given PricingStrategy to calculate total price.
     * This is where Strategy and Builder work together.
     */
    public BookingBuilder calculateTotal(PricingStrategy strategy) {
        if (strategy == null) {
            throw new IllegalArgumentException("PricingStrategy cannot be null");
        }
        if (tickets.isEmpty()) {
            throw new IllegalStateException("Cannot calculate price without tickets");
        }
        if (showtime == null) {
            throw new IllegalStateException("Showtime must be set before calculating price");
        }

        this.totalPrice = strategy.calculateTotal(tickets, showtime);
        this.totalCalculated = true;
        return this;
    }

    public BookingBuilder setStatus(BookingStatus status) {
        this.status = status;
        return this;
    }

    // === Final build() with validation ===

    public Booking build() {
        // generate ID & timestamp
        String id = UUID.randomUUID().toString();
        this.createdAt = LocalDateTime.now();

        // basic validation
        if (customer == null) {
            throw new IllegalStateException("Customer must be provided");
        }
        if (showtime == null) {
            throw new IllegalStateException("Showtime must be provided");
        }
        if (tickets.isEmpty()) {
            throw new IllegalStateException("At least one ticket must be added");
        }
        if (seats.isEmpty()) {
            throw new IllegalStateException("At least one seat must be selected");
        }
        if (seats.size() < tickets.size()) {
            throw new IllegalStateException("Not enough seats for all tickets");
        }
        if (!totalCalculated) {
            throw new IllegalStateException("Total price must be calculated via PricingStrategy");
        }

        return new Booking(
                id,
                customer,
                new ArrayList<>(tickets),
                showtime,
                new ArrayList<>(seats),
                totalPrice,
                status,
                createdAt
        );
    }
}
