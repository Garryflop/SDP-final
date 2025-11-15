package com.project.patterns.strategy;

import org.cinema.model.Ticket;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class HolidayPricingStrategy implements PricingStrategy {

    private static final double SURCHARGE_RATE = 0.25; // 25%

    private final Set<LocalDate> holidays = new HashSet<>();

    public HolidayPricingStrategy(Set<LocalDate> holidays) {
        if (holidays != null) {
            this.holidays.addAll(holidays);
        }
    }

    @Override
    public double calculateTotal(List<Ticket> tickets, LocalDateTime showtime) {
        double baseTotal = tickets.stream()
                .mapToDouble(Ticket::getPrice)
                .sum();

        LocalDate date = showtime.toLocalDate();
        if (holidays.contains(date)) {
            return baseTotal * (1.0 + SURCHARGE_RATE);
        }
        return baseTotal;
    }
}
