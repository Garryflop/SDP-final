package org.cinema.patterns.strategy;


import org.cinema.model.Ticket;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

public class MatineePricingStrategy implements PricingStrategy {

    private static final double DISCOUNT_RATE = 0.20; // 20%

    @Override
    public double calculateTotal(List<Ticket> tickets, LocalDateTime showtime) {
        double baseTotal = tickets.stream()
                .mapToDouble(Ticket::getPrice)
                .sum();

        // discount only if showtime is before 17:00
        if (showtime.toLocalTime().isBefore(LocalTime.of(17, 0))) {
            return baseTotal * (1.0 - DISCOUNT_RATE);
        }
        return baseTotal;
    }
}
