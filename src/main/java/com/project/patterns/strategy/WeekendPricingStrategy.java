package com.project.patterns.strategy;

import com.project.patterns.strategy.PricingStrategy;
import org.cinema.model.Ticket;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.util.List;

public class WeekendPricingStrategy implements PricingStrategy {

    private static final double SURCHARGE_RATE = 0.15; // 15%

    @Override
    public double calculateTotal(List<Ticket> tickets, LocalDateTime showtime) {
        double baseTotal = tickets.stream()
                .mapToDouble(Ticket::getPrice)
                .sum();

        DayOfWeek day = showtime.getDayOfWeek();
        if (day == DayOfWeek.SATURDAY || day == DayOfWeek.SUNDAY) {
            return baseTotal * (1.0 + SURCHARGE_RATE);
        }
        return baseTotal;
    }
}
