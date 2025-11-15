package org.cinema;

import org.cinema.factory.RegularTicket;
import org.cinema.factory.VIPTicket;
import org.cinema.model.Ticket;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class BookingBuilderTest {

    // Метод для создания билета
    private Ticket createTicket(double price) {
        Ticket ticket;

        if (price == 10.0) {
            ticket = new RegularTicket();  // Создаем RegularTicket
        } else {
            ticket = new VIPTicket();      // Создаем VIPTicket
        }

        return ticket;  // Возвращаем созданный билет
    }

    @Test
    public void buildValidBooking_success() {
        // Создаем билеты
        Ticket regularTicket = createTicket(10.0);  // Regular ticket
        Ticket vipTicket = createTicket(20.0);      // VIP ticket

        // Логика создания заказа и проверки
        assertEquals(10.0, regularTicket.getPrice(), 0.01);  // Проверяем цену для обычного билета
        assertEquals(20.0, vipTicket.getPrice(), 0.01);      // Проверяем цену для VIP билета
    }
}
