package org.cinema.decorator;

import org.cinema.model.Ticket;

//Concrete decorators
public class SnackComboDecorator extends TicketDecorator {
    private static final double SNACK_PRICE = 10.0;

    public SnackComboDecorator(Ticket ticket) {
        super(ticket);
    }

    @Override
    public double getPrice() {
        return ticket.getPrice() + SNACK_PRICE;
    }

    @Override
    public String getDescription() {
        return ticket.getDescription() + " + Snack Combo (Popcorn + Drink)";
    }
}
