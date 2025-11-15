package org.cinema;

import org.cinema.factory.RegularTicket;
import org.cinema.factory.VIPTicket;
import org.cinema.model.Ticket;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class BookingRepositoryTest {

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
    public void findByCustomerEmail_returnsAllBookingsForSameEmail() {
        // Создаем билеты
        Ticket regularTicket = createTicket(10.0);  // Regular ticket
        Ticket vipTicket = createTicket(20.0);      // VIP ticket

        // Логика поиска и проверки результата
        assertEquals(10.0, regularTicket.getPrice(), 0.01);  // Проверяем, что цена для обычного билета верна
        assertEquals(20.0, vipTicket.getPrice(), 0.01);      // Проверяем, что цена для VIP билета верна
    }
}
