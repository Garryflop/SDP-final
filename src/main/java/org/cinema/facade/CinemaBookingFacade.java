package org.cinema.facade;

import org.cinema.adapter.CashSystemAdapter;
import org.cinema.adapter.PayPalAdapter;
import org.cinema.adapter.StripeAdapter;
import org.cinema.builder.BookingBuilder;
import org.cinema.decorator.Glasses3DDecorator;
import org.cinema.decorator.SnackComboDecorator;
import org.cinema.factory.TicketFactory;
import org.cinema.model.Booking;
import org.cinema.model.Customer;
import org.cinema.model.Movie;
import org.cinema.model.Seat;
import org.cinema.model.Ticket;
import org.cinema.model.enums.SeatType;
import org.cinema.model.enums.TicketType;
import org.cinema.observer.BookingSubject;
import org.cinema.observer.EmailNotificationObserver;
import org.cinema.observer.InventoryObserver;
import org.cinema.observer.SMSNotificationObserver;
import org.cinema.repository.MovieRepository;
import org.cinema.service.BookingService;
import org.cinema.service.PaymentService;
import org.cinema.strategy.HolidayPricingStrategy;
import org.cinema.strategy.MatineePricingStrategy;
import org.cinema.strategy.PricingStrategy;
import org.cinema.strategy.WeekendPricingStrategy;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Facade Pattern - Simplified interface for Cinema Booking System
 * Orchestrates all 7 design patterns:
 * 1. Factory Pattern - Creating tickets
 * 2. Decorator Pattern - Adding extras (3D glasses, snacks)
 * 3. Builder Pattern - Constructing bookings
 * 4. Strategy Pattern - Pricing calculations
 * 5. Observer Pattern - Event notifications
 * 6. Adapter Pattern - Payment gateway integration
 * 7. Facade Pattern - This class simplifies the complex subsystem
 * 
 * Student 3: ERNAR
 */
public class CinemaBookingFacade {
    // Repositories
    private MovieRepository movieRepository;
    
    // Observer pattern components
    private BookingSubject bookingSubject;
    private EmailNotificationObserver emailObserver;
    private SMSNotificationObserver smsObserver;
    private InventoryObserver inventoryObserver;
    
    // Services
    private BookingService bookingService;
    private PaymentService paymentService;
    
    // Factory
    private TicketFactory ticketFactory;
    
    // Strategy for pricing
    private MatineePricingStrategy matineePricing;
    private WeekendPricingStrategy weekendPricing;
    private HolidayPricingStrategy holidayPricing;

    /**
     * Initialize the facade with all subsystems
     */
    public CinemaBookingFacade() {
        // Initialize repository (Singleton)
        this.movieRepository = MovieRepository.getInstance();
        
        // Initialize factory
        this.ticketFactory = new TicketFactory();
        
        // Initialize Observer pattern
        this.bookingSubject = new BookingSubject();
        this.emailObserver = new EmailNotificationObserver();
        this.smsObserver = new SMSNotificationObserver();
        this.inventoryObserver = new InventoryObserver();
        
        // Attach observers
        bookingSubject.attach(emailObserver);
        bookingSubject.attach(smsObserver);
        bookingSubject.attach(inventoryObserver);
        
        // Initialize services
        this.bookingService = new BookingService(bookingSubject);
        this.paymentService = new PaymentService(bookingSubject);
        
        // Register payment adapters
        paymentService.registerGateway("STRIPE", new StripeAdapter());
        paymentService.registerGateway("PAYPAL", new PayPalAdapter());
        paymentService.registerGateway("CASH", new CashSystemAdapter());
        
        // Initialize pricing strategies
        this.matineePricing = new MatineePricingStrategy();
        this.weekendPricing = new WeekendPricingStrategy();
        
        Set<LocalDate> holidays = createHolidaySet();
        this.holidayPricing = new HolidayPricingStrategy(holidays);
        
        System.out.println("\n╔════════════════════════════════════════════════════════╗");
        System.out.println("║     CINEMA BOOKING SYSTEM INITIALIZED                  ║");
        System.out.println("║     Facade Pattern - All Subsystems Ready              ║");
        System.out.println("╚════════════════════════════════════════════════════════╝\n");
    }

    /**
     * Search for available movies
     * Uses: Repository Pattern
     */
    public List<Movie> searchMovies() {
        System.out.println("\n[Facade] Searching available movies...");
        return movieRepository.findAll();
    }

    /**
     * Get movie by ID
     * Uses: Repository Pattern
     */
    public Movie getMovie(int movieId) {
        return movieRepository.findById(movieId);
    }

    /**
     * Book tickets with full workflow
     * Uses: Factory, Decorator, Builder, Strategy, Observer patterns
     */
    public String bookTickets(String customerName, String customerEmail, String customerPhone,
                             int movieId, TicketType ticketType, int seatCount,
                             List<Integer> seatRows, List<Integer> seatNumbers,
                             LocalDateTime showtime, boolean add3DGlasses, boolean addSnacks) {
        
        System.out.println("\n╔════════════════════════════════════════════════════════╗");
        System.out.println("║              BOOKING PROCESS STARTED                   ║");
        System.out.println("╚════════════════════════════════════════════════════════╝");
        
        // 1. Validate movie exists (Repository)
        Movie movie = getMovie(movieId);
        if (movie == null) {
            System.out.println("[Facade] ERROR: Movie not found");
            return null;
        }
        System.out.println("[Facade] Movie found: " + movie.getTitle());
        
        // 2. Create customer
        Customer customer = new Customer(customerName, customerEmail, customerPhone);
        System.out.println("[Facade] Customer: " + customerName);
        
        // 3. Create tickets using Factory Pattern
        List<Ticket> tickets = new ArrayList<>();
        for (int i = 0; i < seatCount; i++) {
            Ticket ticket = ticketFactory.createTicket(ticketType);
            
            // 4. Apply Decorator Pattern
            if (add3DGlasses) {
                ticket = new Glasses3DDecorator(ticket);
            }
            if (addSnacks) {
                ticket = new SnackComboDecorator(ticket);
            }
            
            tickets.add(ticket);
        }
        System.out.println("[Facade] Created " + seatCount + " tickets with decorators");
        
        // 5. Create seats
        List<Seat> seats = new ArrayList<>();
        for (int i = 0; i < seatCount; i++) {
            SeatType seatType = (ticketType == TicketType.VIP) ? SeatType.VIP : SeatType.STANDARD;
            Seat seat = new Seat(seatRows.get(i), seatNumbers.get(i), seatType, true);
            seats.add(seat);
        }
        System.out.println("[Facade] Reserved " + seatCount + " seats");
        
        // 6. Select appropriate pricing Strategy Pattern
        PricingStrategy pricingStrategy = selectPricingStrategy(showtime);
        System.out.println("[Facade] Selected pricing strategy: " + pricingStrategy.getClass().getSimpleName());
        
        // 7. Build booking using Builder Pattern
        BookingBuilder builder = new BookingBuilder();
        builder.setCustomer(customer)
               .setShowtime(showtime);
        
        for (Ticket ticket : tickets) {
            builder.addTicket(ticket);
        }
        
        for (Seat seat : seats) {
            builder.addSeat(seat);
        }
        
        builder.calculateTotal(pricingStrategy);
        Booking booking = builder.build();
        
        System.out.println("[Facade] Booking created with ID: " + booking.getId());
        System.out.println("[Facade] Total amount: $" + String.format("%.2f", booking.getTotalPrice()));
        
        // 8. Create booking in service (triggers Observer notifications)
        String bookingId = bookingService.createBooking(
                customerEmail, customerPhone, movie.getTitle(),
                seatCount, booking.getTotalPrice()
        );
        
        // 9. Reserve seats (triggers Observer notifications)
        bookingService.reserveSeats(bookingId);
        
        System.out.println("\n[Facade] ✓ Booking process completed");
        System.out.println("════════════════════════════════════════════════════════\n");
        
        return bookingId;
    }

    /**
     * Process payment for a booking
     * Uses: Adapter Pattern, Observer Pattern
     */
    public boolean processPayment(String bookingId, double amount, String paymentMethod,
                                  String customerEmail, String customerPhone) {
        
        System.out.println("\n[Facade] Processing payment for booking: " + bookingId);
        
        // Process payment using Adapter Pattern
        boolean paymentSuccess = paymentService.processPayment(
                bookingId, amount, paymentMethod,
                customerEmail, customerPhone
        );
        
        if (paymentSuccess) {
            // Confirm booking (triggers Observer notifications)
            bookingService.confirmBooking(bookingId);
            System.out.println("[Facade] ✓ Payment successful and booking confirmed");
        } else {
            System.out.println("[Facade] ✗ Payment failed");
        }
        
        return paymentSuccess;
    }

    /**
     * Cancel a booking and process refund
     * Uses: Observer Pattern, Adapter Pattern
     */
    public boolean cancelBooking(String bookingId, String customerEmail, String customerPhone) {
        
        System.out.println("\n[Facade] Cancelling booking: " + bookingId);
        
        // Get payment for this booking
        var payment = paymentService.getPaymentByBookingId(bookingId);
        
        if (payment != null) {
            // Process refund using Adapter Pattern
            boolean refundSuccess = paymentService.refundPayment(
                    payment.getId(), customerEmail, customerPhone
            );
            
            if (!refundSuccess) {
                System.out.println("[Facade] WARNING: Refund failed but booking will be cancelled");
            }
        }
        
        // Cancel booking (triggers Observer notifications)
        boolean cancelled = bookingService.cancelBooking(bookingId);
        
        if (cancelled) {
            System.out.println("[Facade] ✓ Booking cancelled successfully");
        } else {
            System.out.println("[Facade] ✗ Cancellation failed");
        }
        
        return cancelled;
    }

    /**
     * Complete booking workflow - simplified method
     * This is the main facade method that combines all steps
     */
    public BookingResult completeBookingWorkflow(
            String customerName, String customerEmail, String customerPhone,
            int movieId, TicketType ticketType, int seatCount,
            LocalDateTime showtime, boolean add3DGlasses, boolean addSnacks,
            String paymentMethod) {
        
        System.out.println("\n╔════════════════════════════════════════════════════════╗");
        System.out.println("║        COMPLETE BOOKING WORKFLOW (FACADE)              ║");
        System.out.println("║        Orchestrating All 7 Design Patterns             ║");
        System.out.println("╚════════════════════════════════════════════════════════╝\n");
        
        // Generate seat assignments
        List<Integer> seatRows = new ArrayList<>();
        List<Integer> seatNumbers = new ArrayList<>();
        for (int i = 0; i < seatCount; i++) {
            seatRows.add(1);
            seatNumbers.add(i + 1);
        }
        
        // Step 1: Book tickets
        String bookingId = bookTickets(
                customerName, customerEmail, customerPhone,
                movieId, ticketType, seatCount, seatRows, seatNumbers,
                showtime, add3DGlasses, addSnacks
        );
        
        if (bookingId == null) {
            return new BookingResult(false, null, "Booking creation failed");
        }
        
        // Get booking data to retrieve total amount
        var bookingData = bookingService.getBooking(bookingId);
        if (bookingData == null) {
            return new BookingResult(false, bookingId, "Booking data not found");
        }
        
        // Step 2: Process payment
        boolean paymentSuccess = processPayment(
                bookingId, bookingData.getTotalAmount(), paymentMethod,
                customerEmail, customerPhone
        );
        
        if (!paymentSuccess) {
            // Cancel booking if payment failed
            cancelBooking(bookingId, customerEmail, customerPhone);
            return new BookingResult(false, bookingId, "Payment failed - booking cancelled");
        }
        
        System.out.println("\n╔════════════════════════════════════════════════════════╗");
        System.out.println("║        ✓ WORKFLOW COMPLETED SUCCESSFULLY               ║");
        System.out.println("║        Booking ID: " + String.format("%-35s", bookingId) + " ║");
        System.out.println("╚════════════════════════════════════════════════════════╝\n");
        
        return new BookingResult(true, bookingId, "Booking completed successfully");
    }

    /**
     * Get inventory report
     * Uses: Observer Pattern
     */
    public void printInventoryReport() {
        inventoryObserver.printInventoryReport();
    }

    /**
     * Get payment statistics
     * Uses: Adapter Pattern
     */
    public void printPaymentStatistics() {
        paymentService.getStatistics().print();
    }

    /**
     * Select appropriate pricing strategy based on showtime
     * Uses: Strategy Pattern
     */
    private PricingStrategy selectPricingStrategy(LocalDateTime showtime) {
        LocalDate date = showtime.toLocalDate();
        DayOfWeek dayOfWeek = showtime.getDayOfWeek();
        
        // Check if it's a holiday (highest priority)
        if (holidayPricing != null) {
            Set<LocalDate> holidays = createHolidaySet();
            if (holidays.contains(date)) {
                return holidayPricing;
            }
        }
        
        // Check if it's a weekend
        if (dayOfWeek == DayOfWeek.SATURDAY || dayOfWeek == DayOfWeek.SUNDAY) {
            return weekendPricing;
        }
        
        // Check if it's a matinee (before 5 PM)
        if (showtime.getHour() < 17) {
            return matineePricing;
        }
        
        // Default to matinee pricing (no discount after 5 PM)
        return matineePricing;
    }

    /**
     * Create set of holidays
     */
    private Set<LocalDate> createHolidaySet() {
        Set<LocalDate> holidays = new HashSet<>();
        holidays.add(LocalDate.of(2025, 1, 1));   // New Year
        holidays.add(LocalDate.of(2025, 12, 25)); // Christmas
        holidays.add(LocalDate.of(2025, 7, 4));   // Independence Day
        return holidays;
    }

    // Getters for testing
    public BookingService getBookingService() {
        return bookingService;
    }

    public PaymentService getPaymentService() {
        return paymentService;
    }

    public MovieRepository getMovieRepository() {
        return movieRepository;
    }

    public InventoryObserver getInventoryObserver() {
        return inventoryObserver;
    }

    /**
     * Result class for booking operations
     */
    public static class BookingResult {
        private boolean success;
        private String bookingId;
        private String message;

        public BookingResult(boolean success, String bookingId, String message) {
            this.success = success;
            this.bookingId = bookingId;
            this.message = message;
        }

        public boolean isSuccess() {
            return success;
        }

        public String getBookingId() {
            return bookingId;
        }

        public String getMessage() {
            return message;
        }

        @Override
        public String toString() {
            return "BookingResult{" +
                    "success=" + success +
                    ", bookingId='" + bookingId + '\'' +
                    ", message='" + message + '\'' +
                    '}';
        }
    }
}
