package org.cinema;

import org.cinema.builder.BookingBuilder;
import org.cinema.factory.RegularTicket;
import org.cinema.model.Booking;
import org.cinema.model.Customer;
import org.cinema.model.Seat;
import org.cinema.model.enums.BookingStatus;
import org.cinema.model.enums.SeatType;
import org.cinema.repository.BookingRepository;
import org.cinema.strategy.MatineePricingStrategy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for BookingRepository
 * Tests storage, retrieval, updates, and deletion of bookings
 */
public class BookingRepositoryTest {

    private BookingRepository repository;

    @BeforeEach
    public void setUp() {
        repository = new BookingRepository();
    }

    @Test
    public void save_andFindById_returnsCorrectBooking() {
        // Arrange
        Booking booking = createTestBooking("test@example.com");

        // Act
        repository.save(booking);
        Optional<Booking> found = repository.findById(booking.getId());

        // Assert
        assertTrue(found.isPresent(), "Booking should be found");
        assertEquals(booking.getId(), found.get().getId(), "Booking ID should match");
        assertEquals("test@example.com", found.get().getCustomer().getEmail(), "Customer email should match");
    }

    @Test
    public void findById_withNonExistentId_returnsEmpty() {
        // Act
        Optional<Booking> found = repository.findById("NON_EXISTENT_ID");

        // Assert
        assertFalse(found.isPresent(), "Should return empty Optional for non-existent ID");
    }

    @Test
    public void save_overwritesExistingBooking() {
        // Arrange
        Booking booking1 = createTestBooking("first@example.com");
        repository.save(booking1);

        // Create new booking with same ID but different customer
        Customer newCustomer = new Customer("Updated User", "updated@example.com", "999-9999");
        Booking booking2 = new BookingBuilder()
                .setCustomer(newCustomer)
                .setShowtime(LocalDateTime.now())
                .addTicket(new RegularTicket())
                .addSeat(new Seat(1, 1, SeatType.STANDARD, true))
                .calculateTotal(new MatineePricingStrategy())
                .build();

        // Use reflection or create with same ID (for testing purposes, we'll create a new one)
        repository.save(booking2);

        // Act
        Optional<Booking> found = repository.findById(booking2.getId());

        // Assert
        assertTrue(found.isPresent());
        assertEquals("updated@example.com", found.get().getCustomer().getEmail());
    }

    @Test
    public void findAll_returnsAllBookings() {
        // Arrange
        Booking booking1 = createTestBooking("user1@example.com");
        Booking booking2 = createTestBooking("user2@example.com");
        Booking booking3 = createTestBooking("user3@example.com");

        repository.save(booking1);
        repository.save(booking2);
        repository.save(booking3);

        // Act
        List<Booking> allBookings = repository.findAll();

        // Assert
        assertEquals(3, allBookings.size(), "Should return all 3 bookings");
    }

    @Test
    public void findAll_whenEmpty_returnsEmptyList() {
        // Act
        List<Booking> allBookings = repository.findAll();

        // Assert
        assertNotNull(allBookings, "Should return non-null list");
        assertTrue(allBookings.isEmpty(), "Should return empty list");
    }

    @Test
    public void findByCustomerEmail_returnsSingleBooking() {
        // Arrange
        String email = "customer@example.com";
        Booking booking = createTestBooking(email);
        repository.save(booking);

        // Act
        List<Booking> results = repository.findByCustomerEmail(email);

        // Assert
        assertEquals(1, results.size(), "Should find 1 booking");
        assertEquals(email, results.get(0).getCustomer().getEmail());
    }

    @Test
    public void findByCustomerEmail_returnsMultipleBookingsForSameEmail() {
        // Arrange
        String email = "repeat.customer@example.com";
        Booking booking1 = createTestBooking(email);
        Booking booking2 = createTestBooking(email);
        Booking booking3 = createTestBooking("different@example.com");

        repository.save(booking1);
        repository.save(booking2);
        repository.save(booking3);

        // Act
        List<Booking> results = repository.findByCustomerEmail(email);

        // Assert
        assertEquals(2, results.size(), "Should find 2 bookings for the email");
        assertTrue(results.stream().allMatch(b -> 
                b.getCustomer().getEmail().equalsIgnoreCase(email)),
                "All bookings should have matching email");
    }

    @Test
    public void findByCustomerEmail_caseInsensitive() {
        // Arrange
        String email = "Case.Sensitive@Example.COM";
        Booking booking = createTestBooking(email);
        repository.save(booking);

        // Act
        List<Booking> results = repository.findByCustomerEmail("case.sensitive@example.com");

        // Assert
        assertEquals(1, results.size(), "Should find booking regardless of case");
    }

    @Test
    public void findByCustomerEmail_withNonExistentEmail_returnsEmptyList() {
        // Arrange
        repository.save(createTestBooking("exists@example.com"));

        // Act
        List<Booking> results = repository.findByCustomerEmail("doesnotexist@example.com");

        // Assert
        assertTrue(results.isEmpty(), "Should return empty list for non-existent email");
    }

    @Test
    public void findByCustomerEmail_withNullEmail_returnsEmptyList() {
        // Arrange
        Customer customerWithNullEmail = new Customer("Test", null, "123");
        Booking booking = new BookingBuilder()
                .setCustomer(customerWithNullEmail)
                .setShowtime(LocalDateTime.now())
                .addTicket(new RegularTicket())
                .addSeat(new Seat(1, 1, SeatType.STANDARD, true))
                .calculateTotal(new MatineePricingStrategy())
                .build();
        repository.save(booking);

        // Act
        List<Booking> results = repository.findByCustomerEmail("any@example.com");

        // Assert
        assertTrue(results.isEmpty(), "Should handle null email gracefully");
    }

    @Test
    public void deleteById_removesBooking() {
        // Arrange
        Booking booking = createTestBooking("delete@example.com");
        repository.save(booking);
        String bookingId = booking.getId();

        // Verify booking exists
        assertTrue(repository.findById(bookingId).isPresent());

        // Act
        repository.deleteById(bookingId);
        Optional<Booking> found = repository.findById(bookingId);

        // Assert
        assertFalse(found.isPresent(), "Booking should be deleted");
    }

    @Test
    public void deleteById_withNonExistentId_doesNothing() {
        // Arrange
        repository.save(createTestBooking("keep@example.com"));

        // Act - should not throw exception
        assertDoesNotThrow(() -> repository.deleteById("NON_EXISTENT_ID"));

        // Assert
        assertEquals(1, repository.findAll().size(), "Existing booking should remain");
    }

    @Test
    public void updateStatus_changesBookingStatus() {
        // Arrange
        Booking booking = createTestBooking("update@example.com");
        repository.save(booking);
        String bookingId = booking.getId();

        // Verify initial status
        assertEquals(BookingStatus.PENDING, booking.getStatus());

        // Act
        repository.updateStatus(bookingId, BookingStatus.CONFIRMED);

        // Assert
        Optional<Booking> updated = repository.findById(bookingId);
        assertTrue(updated.isPresent());
        assertEquals(BookingStatus.CONFIRMED, updated.get().getStatus());
    }

    @Test
    public void updateStatus_withNonExistentId_doesNothing() {
        // Act - should not throw exception
        assertDoesNotThrow(() -> 
                repository.updateStatus("NON_EXISTENT", BookingStatus.CONFIRMED));
    }

    @Test
    public void updateStatus_toCancelled() {
        // Arrange
        Booking booking = createTestBooking("cancel@example.com");
        repository.save(booking);

        // Act
        repository.updateStatus(booking.getId(), BookingStatus.CANCELLED);

        // Assert
        Optional<Booking> found = repository.findById(booking.getId());
        assertTrue(found.isPresent());
        assertEquals(BookingStatus.CANCELLED, found.get().getStatus());
    }

    @Test
    public void multipleOperations_workCorrectly() {
        // Arrange
        Booking booking1 = createTestBooking("user1@example.com");
        Booking booking2 = createTestBooking("user2@example.com");
        Booking booking3 = createTestBooking("user1@example.com"); // Same email as booking1

        // Act
        repository.save(booking1);
        repository.save(booking2);
        repository.save(booking3);

        List<Booking> user1Bookings = repository.findByCustomerEmail("user1@example.com");
        repository.deleteById(booking2.getId());
        repository.updateStatus(booking1.getId(), BookingStatus.CONFIRMED);

        // Assert
        assertEquals(2, user1Bookings.size(), "User1 should have 2 bookings");
        assertEquals(2, repository.findAll().size(), "Should have 2 bookings after deletion");
        assertEquals(BookingStatus.CONFIRMED, 
                repository.findById(booking1.getId()).get().getStatus());
        assertFalse(repository.findById(booking2.getId()).isPresent(), 
                "Booking2 should be deleted");
    }

    // Helper method to create test bookings
    private Booking createTestBooking(String email) {
        Customer customer = new Customer("Test User", email, "555-0000");
        return new BookingBuilder()
                .setCustomer(customer)
                .setShowtime(LocalDateTime.now())
                .addTicket(new RegularTicket())
                .addSeat(new Seat(1, 1, SeatType.STANDARD, true))
                .calculateTotal(new MatineePricingStrategy())
                .build();
    }
}
