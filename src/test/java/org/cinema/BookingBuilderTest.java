package org.cinema;

import org.cinema.builder.BookingBuilder;
import org.cinema.factory.RegularTicket;
import org.cinema.factory.VIPTicket;
import org.cinema.model.Booking;
import org.cinema.model.Customer;
import org.cinema.model.Seat;
import org.cinema.model.Ticket;
import org.cinema.model.enums.BookingStatus;
import org.cinema.model.enums.SeatType;
import org.cinema.strategy.MatineePricingStrategy;
import org.cinema.strategy.PricingStrategy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for BookingBuilder (Builder Pattern)
 * Tests the fluent API, validation, and booking construction
 */
public class BookingBuilderTest {

    private Customer testCustomer;
    private LocalDateTime testShowtime;
    private Ticket testTicket;
    private Seat testSeat;
    private PricingStrategy testStrategy;

    @BeforeEach
    public void setUp() {
        testCustomer = new Customer("John Doe", "john@example.com", "555-1234");
        testShowtime = LocalDateTime.of(2025, 11, 16, 14, 30);
        testTicket = new RegularTicket();
        testSeat = new Seat(1, 5, SeatType.STANDARD, true);
        testStrategy = new MatineePricingStrategy();
    }

    @Test
    public void buildValidBooking_withAllRequiredFields_success() {
        // Act
        Booking booking = new BookingBuilder()
                .setCustomer(testCustomer)
                .setShowtime(testShowtime)
                .addTicket(testTicket)
                .addSeat(testSeat)
                .calculateTotal(testStrategy)
                .build();

        // Assert
        assertNotNull(booking, "Booking should not be null");
        assertNotNull(booking.getId(), "Booking ID should be generated");
        assertEquals(testCustomer, booking.getCustomer(), "Customer should match");
        assertEquals(testShowtime, booking.getShowtime(), "Showtime should match");
        assertEquals(1, booking.getTickets().size(), "Should have 1 ticket");
        assertEquals(1, booking.getSeats().size(), "Should have 1 seat");
        assertEquals(BookingStatus.PENDING, booking.getStatus(), "Status should be PENDING by default");
        assertNotNull(booking.getCreatedAt(), "Creation timestamp should be set");
        assertTrue(booking.getTotalPrice() > 0, "Total price should be calculated");
    }

    @Test
    public void buildBooking_withMultipleTicketsAndSeats_success() {
        // Arrange
        Ticket ticket1 = new RegularTicket();
        Ticket ticket2 = new VIPTicket();
        Seat seat1 = new Seat(1, 1, SeatType.STANDARD, true);
        Seat seat2 = new Seat(1, 2, SeatType.VIP, true);

        // Act
        Booking booking = new BookingBuilder()
                .setCustomer(testCustomer)
                .setShowtime(testShowtime)
                .addTicket(ticket1)
                .addTicket(ticket2)
                .addSeat(seat1)
                .addSeat(seat2)
                .calculateTotal(testStrategy)
                .build();

        // Assert
        assertEquals(2, booking.getTickets().size(), "Should have 2 tickets");
        assertEquals(2, booking.getSeats().size(), "Should have 2 seats");
    }

    @Test
    public void buildBooking_withCustomStatus_success() {
        // Act
        Booking booking = new BookingBuilder()
                .setCustomer(testCustomer)
                .setShowtime(testShowtime)
                .addTicket(testTicket)
                .addSeat(testSeat)
                .setStatus(BookingStatus.CONFIRMED)
                .calculateTotal(testStrategy)
                .build();

        // Assert
        assertEquals(BookingStatus.CONFIRMED, booking.getStatus(), "Status should be CONFIRMED");
    }

    @Test
    public void build_withoutCustomer_throwsIllegalStateException() {
        // Arrange
        BookingBuilder builder = new BookingBuilder()
                .setShowtime(testShowtime)
                .addTicket(testTicket)
                .addSeat(testSeat)
                .calculateTotal(testStrategy);

        // Act & Assert
        IllegalStateException exception = assertThrows(IllegalStateException.class, builder::build);
        assertTrue(exception.getMessage().contains("Customer"), "Error message should mention Customer");
    }

    @Test
    public void build_withoutShowtime_throwsIllegalStateException() {
        // Arrange
        BookingBuilder builder = new BookingBuilder()
                .setCustomer(testCustomer)
                .addTicket(testTicket)
                .addSeat(testSeat);
        // NOT setting showtime and NOT calling calculateTotal()

        // Act & Assert
        IllegalStateException exception = assertThrows(IllegalStateException.class, builder::build);
        assertTrue(exception.getMessage().contains("Showtime") || exception.getMessage().contains("price"), 
                "Error message should mention Showtime or price calculation requirement");
    }

    @Test
    public void build_withoutTickets_throwsIllegalStateException() {
        // Arrange
        BookingBuilder builder = new BookingBuilder()
                .setCustomer(testCustomer)
                .setShowtime(testShowtime)
                .addSeat(testSeat);
        // NOT adding tickets and NOT calling calculateTotal()

        // Act & Assert
        IllegalStateException exception = assertThrows(IllegalStateException.class, builder::build);
        assertTrue(exception.getMessage().contains("ticket") || exception.getMessage().contains("price"), 
                "Error message should mention ticket or price calculation requirement");
    }

    @Test
    public void build_withoutSeats_throwsIllegalStateException() {
        // Arrange
        BookingBuilder builder = new BookingBuilder()
                .setCustomer(testCustomer)
                .setShowtime(testShowtime)
                .addTicket(testTicket)
                .calculateTotal(testStrategy);

        // Act & Assert
        IllegalStateException exception = assertThrows(IllegalStateException.class, builder::build);
        assertTrue(exception.getMessage().contains("seat"), "Error message should mention seat");
    }

    @Test
    public void build_withoutCalculatingTotal_throwsIllegalStateException() {
        // Arrange
        BookingBuilder builder = new BookingBuilder()
                .setCustomer(testCustomer)
                .setShowtime(testShowtime)
                .addTicket(testTicket)
                .addSeat(testSeat);
        // NOT calling calculateTotal()

        // Act & Assert
        IllegalStateException exception = assertThrows(IllegalStateException.class, builder::build);
        assertTrue(exception.getMessage().contains("price"), "Error message should mention price calculation");
    }

    @Test
    public void build_withFewerSeatsThanTickets_throwsIllegalStateException() {
        // Arrange
        BookingBuilder builder = new BookingBuilder()
                .setCustomer(testCustomer)
                .setShowtime(testShowtime)
                .addTicket(new RegularTicket())
                .addTicket(new VIPTicket())
                .addSeat(testSeat) // Only 1 seat for 2 tickets
                .calculateTotal(testStrategy);

        // Act & Assert
        IllegalStateException exception = assertThrows(IllegalStateException.class, builder::build);
        assertTrue(exception.getMessage().contains("seats") || exception.getMessage().contains("seat"), 
                "Error message should mention seats");
    }

    @Test
    public void calculateTotal_withNullStrategy_throwsIllegalArgumentException() {
        // Arrange
        BookingBuilder builder = new BookingBuilder()
                .setCustomer(testCustomer)
                .setShowtime(testShowtime)
                .addTicket(testTicket)
                .addSeat(testSeat);

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> builder.calculateTotal(null));
    }

    @Test
    public void calculateTotal_withoutTickets_throwsIllegalStateException() {
        // Arrange
        BookingBuilder builder = new BookingBuilder()
                .setCustomer(testCustomer)
                .setShowtime(testShowtime)
                .addSeat(testSeat);

        // Act & Assert
        assertThrows(IllegalStateException.class, () -> builder.calculateTotal(testStrategy));
    }

    @Test
    public void calculateTotal_withoutShowtime_throwsIllegalStateException() {
        // Arrange
        BookingBuilder builder = new BookingBuilder()
                .setCustomer(testCustomer)
                .addTicket(testTicket)
                .addSeat(testSeat);

        // Act & Assert
        assertThrows(IllegalStateException.class, () -> builder.calculateTotal(testStrategy));
    }

    @Test
    public void fluentAPI_returnsBuilderInstance_forChaining() {
        // Act
        BookingBuilder builder = new BookingBuilder();
        BookingBuilder result1 = builder.setCustomer(testCustomer);
        BookingBuilder result2 = builder.setShowtime(testShowtime);
        BookingBuilder result3 = builder.addTicket(testTicket);
        BookingBuilder result4 = builder.addSeat(testSeat);

        // Assert
        assertSame(builder, result1, "setCustomer should return same builder instance");
        assertSame(builder, result2, "setShowtime should return same builder instance");
        assertSame(builder, result3, "addTicket should return same builder instance");
        assertSame(builder, result4, "addSeat should return same builder instance");
    }

    @Test
    public void addTicket_withNull_doesNotAddTicket() {
        // Act
        Booking booking = new BookingBuilder()
                .setCustomer(testCustomer)
                .setShowtime(testShowtime)
                .addTicket(testTicket)
                .addTicket(null) // Try to add null ticket
                .addSeat(testSeat)
                .calculateTotal(testStrategy)
                .build();

        // Assert
        assertEquals(1, booking.getTickets().size(), "Null ticket should not be added");
    }

    @Test
    public void addSeat_withNull_doesNotAddSeat() {
        // Act
        Booking booking = new BookingBuilder()
                .setCustomer(testCustomer)
                .setShowtime(testShowtime)
                .addTicket(testTicket)
                .addSeat(testSeat)
                .addSeat(null) // Try to add null seat
                .calculateTotal(testStrategy)
                .build();

        // Assert
        assertEquals(1, booking.getSeats().size(), "Null seat should not be added");
    }
}
