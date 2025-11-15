package org.cinema.factory;

import org.cinema.model.Ticket;
import org.cinema.model.enums.TicketType;

public class TicketFactory {
    public static Ticket createTicket(TicketType type) {
        switch (type) {
            case REGULAR:
                return new RegularTicket();
            case VIP:
                return new VIPTicket();
            default:
                throw new IllegalArgumentException("Unknown ticket type: " + type);
        }
    }
}
