package org.cinema.factory;

import org.cinema.model.Ticket;

public class RegularTicket implements Ticket {
    private static final double BASE_PRICE = 10.0;

    @Override
    public double getPrice() {
        return BASE_PRICE;
    }

    @Override
    public String getDescription() {
        return "Regular Ticket";
    }
}
