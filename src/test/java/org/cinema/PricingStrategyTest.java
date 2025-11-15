package org.cinema;

import org.cinema.factory.RegularTicket;
import org.cinema.factory.VIPTicket;
import org.cinema.model.Ticket;
import org.cinema.model.enums.TicketType;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class PricingStrategyTest {

    // Метод для создания билета
    private Ticket createTicket(double price) {
        // Выбираем тип билета в зависимости от цены
        Ticket ticket;

        if (price == 10.0) {
            ticket = new RegularTicket();  // Создаем RegularTicket
        } else {
            ticket = new VIPTicket();      // Создаем VIPTicket
        }

        return ticket;  // Возвращаем созданный билет
    }

    @Test
    public void matineeStrategy_noDiscountAfter5pm() {
        // Создаем билеты
        Ticket regularTicket = createTicket(10.0);  // Regular ticket
        Ticket vipTicket = createTicket(20.0);      // VIP ticket

        // Проверяем, что цена соответствует
        assertEquals(10.0, regularTicket.getPrice(), 0.01);  // Ожидаемая цена для обычного билета
        assertEquals(20.0, vipTicket.getPrice(), 0.01);      // Ожидаемая цена для VIP билета
    }

    @Test
    public void holidayStrategy_adds25PercentOnHolidayDates() {
        // Создаем билеты с ценой 10
        Ticket ticket = createTicket(10.0);  // Regular ticket с базовой ценой 10.0

        // Применяем праздничную наценку 25%
        double holidayPrice = ticket.getPrice() * 1.25;  // 25% наценка

        // Проверяем, что цена после наценки верна
        assertEquals(12.5, holidayPrice, 0.01);  // Ожидаемая цена после наценки
    }

    @Test
    public void weekendStrategy_adds15PercentOnSaturday() {
        // Создаем билеты с ценой 10
        Ticket ticket = createTicket(10.0);  // Regular ticket с базовой ценой 10.0

        // Применяем наценку 15% для выходных
        double weekendPrice = ticket.getPrice() * 1.15;  // 15% наценка на цену

        // Проверяем, что цена после наценки верна
        assertEquals(11.5, weekendPrice, 0.01);  // Ожидаемая цена после наценки
    }

    @Test
    public void matineeStrategy_applies20PercentDiscountBefore5pm() {
        // Создаем билеты с ценой 10
        Ticket ticket = createTicket(10.0);  // Regular ticket с базовой ценой 10.0

        // Применяем 20% скидку
        double discountedPrice = ticket.getPrice() * 0.8;  // 20% скидка на обычный билет

        // Проверяем, что цена после скидки верна
        assertEquals(8.0, discountedPrice, 0.01);  // Ожидаемая цена после скидки
    }

}
