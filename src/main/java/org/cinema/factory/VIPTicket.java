package org.cinema.factory;

import org.cinema.model.Ticket;

public class VIPTicket implements Ticket {
    private static final double BASE_PRICE = 20.0;

    @Override
    public double getPrice() {
        return BASE_PRICE;
    }

    @Override
    public String getDescription() {
        return "VIP Ticket";
    }
}
