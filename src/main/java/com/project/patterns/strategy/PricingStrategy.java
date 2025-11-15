package com.project.patterns.strategy;// package your.package.patterns.strategy;


import org.cinema.model.Ticket;
import java.time.LocalDateTime;
import java.util.List;

public interface PricingStrategy {

    /**
     * Calculates final total price for all tickets
     * based on showtime (matinee / weekend / holiday).
     */
    double calculateTotal(List<Ticket> tickets, LocalDateTime showtime);
}
