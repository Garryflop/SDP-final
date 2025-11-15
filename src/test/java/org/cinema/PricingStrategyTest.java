package org.cinema;

import org.cinema.decorator.Glasses3DDecorator;
import org.cinema.decorator.SnackComboDecorator;
import org.cinema.factory.RegularTicket;
import org.cinema.factory.VIPTicket;
import org.cinema.model.Ticket;
import org.cinema.strategy.HolidayPricingStrategy;
import org.cinema.strategy.MatineePricingStrategy;
import org.cinema.strategy.PricingStrategy;
import org.cinema.strategy.WeekendPricingStrategy;
import org.junit.jupiter.api.Test;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for Strategy Pattern implementations
 * Tests MatineePricingStrategy, WeekendPricingStrategy, HolidayPricingStrategy
 */
public class PricingStrategyTest {

    // ==================== MatineePricingStrategy Tests ====================

    @Test
    public void matineeStrategy_before5pm_applies20PercentDiscount() {
        // Arrange
        List<Ticket> tickets = Arrays.asList(new RegularTicket()); // $10
        LocalDateTime matineeTime = LocalDateTime.of(2025, 11, 16, 14, 30); // 2:30 PM
        PricingStrategy strategy = new MatineePricingStrategy();

        // Act
        double total = strategy.calculateTotal(tickets, matineeTime);

        // Assert
        assertEquals(8.0, total, 0.01, "Matinee discount should apply 20% off: 10 * 0.8 = 8");
    }

    @Test
    public void matineeStrategy_exactlyAt5pm_noDiscount() {
        // Arrange
        List<Ticket> tickets = Arrays.asList(new RegularTicket()); // $10
        LocalDateTime exactTime = LocalDateTime.of(2025, 11, 16, 17, 0); // Exactly 5 PM
        PricingStrategy strategy = new MatineePricingStrategy();

        // Act
        double total = strategy.calculateTotal(tickets, exactTime);

        // Assert
        assertEquals(10.0, total, 0.01, "No discount at or after 5 PM");
    }

    @Test
    public void matineeStrategy_after5pm_noDiscount() {
        // Arrange
        List<Ticket> tickets = Arrays.asList(new VIPTicket()); // $20
        LocalDateTime eveningTime = LocalDateTime.of(2025, 11, 16, 19, 30); // 7:30 PM
        PricingStrategy strategy = new MatineePricingStrategy();

        // Act
        double total = strategy.calculateTotal(tickets, eveningTime);

        // Assert
        assertEquals(20.0, total, 0.01, "No discount after 5 PM");
    }

    @Test
    public void matineeStrategy_earlyMorning_appliesDiscount() {
        // Arrange
        List<Ticket> tickets = Arrays.asList(new RegularTicket()); // $10
        LocalDateTime earlyMorning = LocalDateTime.of(2025, 11, 16, 10, 0); // 10 AM
        PricingStrategy strategy = new MatineePricingStrategy();

        // Act
        double total = strategy.calculateTotal(tickets, earlyMorning);

        // Assert
        assertEquals(8.0, total, 0.01, "Matinee discount applies to morning shows");
    }

    @Test
    public void matineeStrategy_multipleTickets_appliesDiscountToTotal() {
        // Arrange
        Ticket ticket1 = new RegularTicket(); // $10
        Ticket ticket2 = new VIPTicket();     // $20
        List<Ticket> tickets = Arrays.asList(ticket1, ticket2); // Total: $30
        LocalDateTime matineeTime = LocalDateTime.of(2025, 11, 16, 14, 0);
        PricingStrategy strategy = new MatineePricingStrategy();

        // Act
        double total = strategy.calculateTotal(tickets, matineeTime);

        // Assert
        assertEquals(24.0, total, 0.01, "Discount applies to sum of all tickets: 30 * 0.8 = 24");
    }

    @Test
    public void matineeStrategy_decoratedTickets_appliesDiscountToDecoratedPrice() {
        // Arrange
        Ticket baseTicket = new RegularTicket(); // $10
        Ticket decorated = new SnackComboDecorator(baseTicket); // $10 + $10 = $20
        List<Ticket> tickets = Arrays.asList(decorated);
        LocalDateTime matineeTime = LocalDateTime.of(2025, 11, 16, 13, 0);
        PricingStrategy strategy = new MatineePricingStrategy();

        // Act
        double total = strategy.calculateTotal(tickets, matineeTime);

        // Assert
        assertEquals(16.0, total, 0.01, "Discount applies to decorated price: 20 * 0.8 = 16");
    }

    // ==================== WeekendPricingStrategy Tests ====================

    @Test
    public void weekendStrategy_onSaturday_adds15PercentSurcharge() {
        // Arrange
        List<Ticket> tickets = Arrays.asList(new RegularTicket()); // $10
        LocalDateTime saturday = LocalDateTime.of(2025, 11, 15, 14, 0); // Saturday
        PricingStrategy strategy = new WeekendPricingStrategy();

        // Act
        double total = strategy.calculateTotal(tickets, saturday);

        // Assert
        assertEquals(DayOfWeek.SATURDAY, saturday.getDayOfWeek());
        assertEquals(11.5, total, 0.01, "Weekend surcharge: 10 * 1.15 = 11.5");
    }

    @Test
    public void weekendStrategy_onSunday_adds15PercentSurcharge() {
        // Arrange
        List<Ticket> tickets = Arrays.asList(new VIPTicket()); // $20
        LocalDateTime sunday = LocalDateTime.of(2025, 11, 16, 19, 0); // Sunday
        PricingStrategy strategy = new WeekendPricingStrategy();

        // Act
        double total = strategy.calculateTotal(tickets, sunday);

        // Assert
        assertEquals(DayOfWeek.SUNDAY, sunday.getDayOfWeek());
        assertEquals(23.0, total, 0.01, "Weekend surcharge: 20 * 1.15 = 23");
    }

    @Test
    public void weekendStrategy_onWeekday_noSurcharge() {
        // Arrange
        List<Ticket> tickets = Arrays.asList(new RegularTicket()); // $10
        LocalDateTime monday = LocalDateTime.of(2025, 11, 17, 14, 0); // Monday
        PricingStrategy strategy = new WeekendPricingStrategy();

        // Act
        double total = strategy.calculateTotal(tickets, monday);

        // Assert
        assertEquals(DayOfWeek.MONDAY, monday.getDayOfWeek());
        assertEquals(10.0, total, 0.01, "No surcharge on weekdays");
    }

    @Test
    public void weekendStrategy_onFriday_noSurcharge() {
        // Arrange
        List<Ticket> tickets = Arrays.asList(new RegularTicket()); // $10
        LocalDateTime friday = LocalDateTime.of(2025, 11, 14, 20, 0); // Friday
        PricingStrategy strategy = new WeekendPricingStrategy();

        // Act
        double total = strategy.calculateTotal(tickets, friday);

        // Assert
        assertEquals(DayOfWeek.FRIDAY, friday.getDayOfWeek());
        assertEquals(10.0, total, 0.01, "Friday is not considered weekend");
    }

    @Test
    public void weekendStrategy_multipleTickets_addsSurchargeToTotal() {
        // Arrange
        List<Ticket> tickets = Arrays.asList(
                new RegularTicket(), // $10
                new VIPTicket()      // $20
        ); // Total: $30
        LocalDateTime saturday = LocalDateTime.of(2025, 11, 15, 18, 0);
        PricingStrategy strategy = new WeekendPricingStrategy();

        // Act
        double total = strategy.calculateTotal(tickets, saturday);

        // Assert
        assertEquals(34.5, total, 0.01, "Surcharge applies to total: 30 * 1.15 = 34.5");
    }

    // ==================== HolidayPricingStrategy Tests ====================

    @Test
    public void holidayStrategy_onHolidayDate_adds25PercentSurcharge() {
        // Arrange
        Set<LocalDate> holidays = new HashSet<>();
        holidays.add(LocalDate.of(2025, 12, 25)); // Christmas
        List<Ticket> tickets = Arrays.asList(new RegularTicket()); // $10
        LocalDateTime christmas = LocalDateTime.of(2025, 12, 25, 14, 0);
        PricingStrategy strategy = new HolidayPricingStrategy(holidays);

        // Act
        double total = strategy.calculateTotal(tickets, christmas);

        // Assert
        assertEquals(12.5, total, 0.01, "Holiday surcharge: 10 * 1.25 = 12.5");
    }

    @Test
    public void holidayStrategy_onNonHolidayDate_noSurcharge() {
        // Arrange
        Set<LocalDate> holidays = new HashSet<>();
        holidays.add(LocalDate.of(2025, 12, 25)); // Christmas
        List<Ticket> tickets = Arrays.asList(new RegularTicket()); // $10
        LocalDateTime regularDay = LocalDateTime.of(2025, 11, 16, 14, 0);
        PricingStrategy strategy = new HolidayPricingStrategy(holidays);

        // Act
        double total = strategy.calculateTotal(tickets, regularDay);

        // Assert
        assertEquals(10.0, total, 0.01, "No surcharge on non-holiday dates");
    }

    @Test
    public void holidayStrategy_withMultipleHolidays_appliesSurchargeCorrectly() {
        // Arrange
        Set<LocalDate> holidays = new HashSet<>();
        holidays.add(LocalDate.of(2025, 12, 25)); // Christmas
        holidays.add(LocalDate.of(2025, 1, 1));   // New Year
        holidays.add(LocalDate.of(2025, 7, 4));   // Independence Day
        
        List<Ticket> tickets = Arrays.asList(new VIPTicket()); // $20
        LocalDateTime newYear = LocalDateTime.of(2025, 1, 1, 20, 0);
        PricingStrategy strategy = new HolidayPricingStrategy(holidays);

        // Act
        double total = strategy.calculateTotal(tickets, newYear);

        // Assert
        assertEquals(25.0, total, 0.01, "Holiday surcharge: 20 * 1.25 = 25");
    }

    @Test
    public void holidayStrategy_withEmptyHolidaySet_noSurcharge() {
        // Arrange
        Set<LocalDate> holidays = new HashSet<>(); // Empty
        List<Ticket> tickets = Arrays.asList(new RegularTicket()); // $10
        LocalDateTime anyDate = LocalDateTime.of(2025, 12, 25, 14, 0);
        PricingStrategy strategy = new HolidayPricingStrategy(holidays);

        // Act
        double total = strategy.calculateTotal(tickets, anyDate);

        // Assert
        assertEquals(10.0, total, 0.01, "No holidays defined, no surcharge");
    }

    @Test
    public void holidayStrategy_withNullHolidaySet_noSurcharge() {
        // Arrange
        List<Ticket> tickets = Arrays.asList(new RegularTicket()); // $10
        LocalDateTime anyDate = LocalDateTime.of(2025, 12, 25, 14, 0);
        PricingStrategy strategy = new HolidayPricingStrategy(null);

        // Act
        double total = strategy.calculateTotal(tickets, anyDate);

        // Assert
        assertEquals(10.0, total, 0.01, "Null holiday set should not cause errors");
    }

    // ==================== Integration Tests ====================

    @Test
    public void strategies_withDecoratedTickets_calculateCorrectly() {
        // Arrange
        Ticket baseTicket = new RegularTicket(); // $10
        Ticket decoratedTicket = new Glasses3DDecorator(
                new SnackComboDecorator(baseTicket)
        ); // $10 + $10 (snacks) + $5 (3D) = $25
        
        List<Ticket> tickets = Arrays.asList(decoratedTicket);
        LocalDateTime matinee = LocalDateTime.of(2025, 11, 16, 14, 0);
        
        PricingStrategy matineeStrategy = new MatineePricingStrategy();

        // Act
        double total = matineeStrategy.calculateTotal(tickets, matinee);

        // Assert
        assertEquals(20.0, total, 0.01, "Matinee discount on decorated ticket: 25 * 0.8 = 20");
    }

    @Test
    public void strategies_withEmptyTicketList_returnsZero() {
        // Arrange
        List<Ticket> emptyList = Arrays.asList();
        LocalDateTime anyTime = LocalDateTime.now();
        
        PricingStrategy matineeStrategy = new MatineePricingStrategy();
        PricingStrategy weekendStrategy = new WeekendPricingStrategy();

        // Act
        double matineeTotal = matineeStrategy.calculateTotal(emptyList, anyTime);
        double weekendTotal = weekendStrategy.calculateTotal(emptyList, anyTime);

        // Assert
        assertEquals(0.0, matineeTotal, 0.01, "Empty ticket list should return 0");
        assertEquals(0.0, weekendTotal, 0.01, "Empty ticket list should return 0");
    }
}
