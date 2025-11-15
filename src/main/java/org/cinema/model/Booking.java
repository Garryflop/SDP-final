package org.cinema.model;


import org.cinema.model.enums.BookingStatus;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

public class Booking {

    private final String id;                 // auto-generated
    private final Customer customer;
    private final List<Ticket> tickets;      // assume Ticket exists in your project
    private final LocalDateTime showtime;
    private final List<Seat> seats;
    private final double totalPrice;
    private BookingStatus status;
    private final LocalDateTime createdAt;   // timestamp

    public Booking(String id,
                   Customer customer,
                   List<Ticket> tickets,
                   LocalDateTime showtime,
                   List<Seat> seats,
                   double totalPrice,
                   BookingStatus status,
                   LocalDateTime createdAt) {

        this.id = id;
        this.customer = customer;
        this.tickets = Collections.unmodifiableList(tickets);
        this.showtime = showtime;
        this.seats = Collections.unmodifiableList(seats);
        this.totalPrice = totalPrice;
        this.status = status;
        this.createdAt = createdAt;
    }

    public String getId() {
        return id;
    }

    public Customer getCustomer() {
        return customer;
    }

    public List<Ticket> getTickets() {
        return tickets;
    }

    public LocalDateTime getShowtime() {
        return showtime;
    }

    public List<Seat> getSeats() {
        return seats;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public BookingStatus getStatus() {
        return status;
    }

    public void setStatus(BookingStatus status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    @Override
    public String toString() {
        return "Booking{" +
                "id='" + id + '\'' +
                ", customer=" + customer +
                ", tickets=" + tickets +
                ", showtime=" + showtime +
                ", seats=" + seats +
                ", totalPrice=" + totalPrice +
                ", status=" + status +
                ", createdAt=" + createdAt +
                '}';
    }
}


