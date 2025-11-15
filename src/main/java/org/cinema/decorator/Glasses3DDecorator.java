package org.cinema.decorator;

import org.cinema.model.Ticket;

public class Glasses3DDecorator extends TicketDecorator {
    private static final double GLASSES_PRICE = 5.0;

    public Glasses3DDecorator(Ticket ticket) {
        super(ticket);
    }

    @Override
    public double getPrice() {
        return ticket.getPrice() + GLASSES_PRICE;
    }

    @Override
    public String getDescription() {
        return ticket.getDescription() + " + 3D Glasses";
    }
}
