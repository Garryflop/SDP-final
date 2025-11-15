package org.cinema.decorator;

import org.cinema.model.Ticket;

public abstract class TicketDecorator implements Ticket {
    protected Ticket ticket;

    public TicketDecorator(Ticket ticket) {
        this.ticket = ticket;
    }

    @Override
    public double getPrice() {
        return ticket.getPrice();
    }

    @Override
    public String getDescription() {
        return ticket.getDescription();
    }
}
