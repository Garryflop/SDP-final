package org.cinema;

import org.cinema.decorator.Glasses3DDecorator;
import org.cinema.decorator.SnackComboDecorator;
import org.cinema.facade.CinemaBookingFacade;
import org.cinema.factory.TicketFactory;
import org.cinema.model.Movie;
import org.cinema.model.Ticket;
import org.cinema.model.enums.TicketType;
import org.cinema.repository.MovieRepository;
import org.cinema.util.DataInitializer;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {

        // Initialize data
        DataInitializer.init();
        System.out.println();

        System.out.println("╔════════════════════════════════════════════════════════╗");
        System.out.println("║     CINEMA TICKET BOOKING SYSTEM - DEMO                ║");
        System.out.println("║     Demonstrating All 7 Design Patterns                ║");
        System.out.println("╚════════════════════════════════════════════════════════╝\n");

        // Demo Individual Patterns (Student 1)
        System.out.println("═══════════════════════════════════════════════════════════");
        System.out.println("STUDENT 1 PATTERNS: Factory, Decorator, Repository");
        System.out.println("═══════════════════════════════════════════════════════════\n");

        demoFactoryPattern();
        System.out.println();

        demoDecoratorPattern();
        System.out.println();

        demoRepositoryPattern();
        System.out.println();

        demoIntegration();
        System.out.println("\n");

        // Demo Student 2 Patterns (Builder + Strategy)
        System.out.println("═══════════════════════════════════════════════════════════");
        System.out.println("STUDENT 2 PATTERNS: Builder, Strategy");
        System.out.println("═══════════════════════════════════════════════════════════\n");
        // Note: Builder and Strategy are tested in unit tests and integrated in Facade

        // Demo Student 3 Patterns (Observer, Adapter, Facade)
        System.out.println("═══════════════════════════════════════════════════════════");
        System.out.println("STUDENT 3 PATTERNS: Observer, Adapter, Facade");
        System.out.println("═══════════════════════════════════════════════════════════\n");

        demoFacadePattern();
        System.out.println();

        // Interactive demo
        System.out.println("\n═══════════════════════════════════════════════════════════");
        System.out.println("Would you like to try an interactive booking? (y/n)");
        Scanner scanner = new Scanner(System.in);
        String response = scanner.nextLine().trim().toLowerCase();
        
        if (response.equals("y") || response.equals("yes")) {
            interactiveBooking(scanner);
        }
        
        scanner.close();
        
        System.out.println("\n╔════════════════════════════════════════════════════════╗");
        System.out.println("║              DEMO COMPLETED                            ║");
        System.out.println("║     Thank you for using Cinema Booking System!         ║");
        System.out.println("╚════════════════════════════════════════════════════════╝");
    }

    private static void demoFactoryPattern() {
        System.out.println("Creating tickets using Factory Pattern...\n");

        // Create Regular Ticket
        Ticket regularTicket = TicketFactory.createTicket(TicketType.REGULAR);
        System.out.println(" Created: " + regularTicket.getDescription());
        System.out.println("  Price: $" + regularTicket.getPrice());

        // Create VIP Ticket
        Ticket vipTicket = TicketFactory.createTicket(TicketType.VIP);
        System.out.println("\n Created: " + vipTicket.getDescription());
        System.out.println("  Price: $" + vipTicket.getPrice());
    }

    private static void demoDecoratorPattern() {
        System.out.println("Enhancing tickets with Decorator Pattern...\n");

        // Base ticket
        Ticket baseTicket = TicketFactory.createTicket(TicketType.REGULAR);
        System.out.println("Base Ticket:");
        System.out.println("  " + baseTicket.getDescription());
        System.out.println("  Price: $" + baseTicket.getPrice());

        // Add Snack Combo
        Ticket ticketWithSnacks = new SnackComboDecorator(baseTicket);
        System.out.println("\n After adding Snack Combo:");
        System.out.println("  " + ticketWithSnacks.getDescription());
        System.out.println("  Price: $" + ticketWithSnacks.getPrice());

        // Add 3D Glasses
        Ticket fullyDecoratedTicket = new Glasses3DDecorator(ticketWithSnacks);
        System.out.println("\n After adding 3D Glasses:");
        System.out.println("  " + fullyDecoratedTicket.getDescription());
        System.out.println("  Price: $" + fullyDecoratedTicket.getPrice());
    }

    private static void demoRepositoryPattern() {
        System.out.println("Accessing movies from Repository...\n");

        MovieRepository movieRepo = MovieRepository.getInstance();

        // Get all movies
        List<Movie> allMovies = movieRepo.findAll();
        System.out.println(" All Movies in Database:");
        for (Movie movie : allMovies) {
            System.out.println("  • " + movie.getTitle() + " (" + movie.getGenre() + ", " + movie.getFormat() + ")");
        }

        // Find specific movie
        Movie movie = movieRepo.findById(1);
        System.out.println("\n Movie Details (ID: 1):");
        System.out.println("  Title: " + movie.getTitle());
        System.out.println("  Genre: " + movie.getGenre());
        System.out.println("  Format: " + movie.getFormat());
        System.out.println("  Duration: " + movie.getDuration() + " minutes");

        // Find by genre
        List<Movie> actionMovies = movieRepo.findByGenre("Action");
        System.out.println("\n Action Movies:");
        for (Movie actionMovie : actionMovies) {
            System.out.println("  • " + actionMovie.getTitle());
        }
    }

    private static void demoIntegration() {
        System.out.println("Demonstrating Factory + Decorator working together...\n");

        MovieRepository movieRepo = MovieRepository.getInstance();
        Movie movie = movieRepo.findById(2); // Avatar 3D

        System.out.println("Customer booking: " + movie.getTitle() + " (" + movie.getFormat() + ")");
        System.out.println();

        // Step 1: Factory creates VIP ticket
        System.out.println("Step 1: Factory creates VIP ticket");
        Ticket ticket = TicketFactory.createTicket(TicketType.VIP);
        System.out.println(ticket.getDescription() + " - $" + ticket.getPrice());

        // Step 2: Add 3D glasses (required for 3D movie)
        System.out.println("\nStep 2: Decorator adds 3D glasses");
        ticket = new Glasses3DDecorator(ticket);
        System.out.println( ticket.getDescription());
        System.out.println("  Running total: $" + ticket.getPrice());

        // Step 3: Add snack combo
        System.out.println("\nStep 3: Decorator adds snack combo");
        ticket = new SnackComboDecorator(ticket);
        System.out.println(ticket.getDescription());
        System.out.println("  Running total: $" + ticket.getPrice());

        System.out.println("\n" + "─".repeat(50));
        System.out.println("FINAL TICKET:");
        System.out.println("  " + ticket.getDescription());
        System.out.println("  TOTAL PRICE: $" + ticket.getPrice());
        System.out.println("─".repeat(50));
    }

    /**
     * Demo Facade Pattern - Orchestrating all 7 patterns
     * Student 3: ERNAR
     */
    private static void demoFacadePattern() {
        System.out.println("FACADE PATTERN - Complete Booking Workflow");
        System.out.println("(Orchestrates: Factory, Decorator, Builder, Strategy, Observer, Adapter, Repository)\n");

        // Initialize Facade
        CinemaBookingFacade facade = new CinemaBookingFacade();
        
        // Demo Observer Pattern - attach/detach
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("OBSERVER PATTERN - Dynamic Observer Management");
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n");
        
        System.out.println("Initially, 3 observers are attached:");
        System.out.println("  • EmailNotificationObserver");
        System.out.println("  • SMSNotificationObserver");
        System.out.println("  • InventoryObserver");
        System.out.println("\nAll observers will receive notifications...\n");

        System.out.println("\n━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("SCENARIO 1: Simple Matinee Booking with Cash Payment");
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n");

        LocalDateTime matineeTime = LocalDateTime.of(2025, 11, 25, 14, 30);
        CinemaBookingFacade.BookingResult result1 = facade.completeBookingWorkflow(
                "Alice Johnson", "alice.j@email.com", "555-1001",
                1, // Movie: The Matrix Reloaded
                TicketType.REGULAR,
                2, // 2 tickets
                matineeTime,
                false, false, // No 3D, No snacks
                "CASH"
        );

        System.out.println("\n✓ Result: " + result1.getMessage());
        System.out.println("✓ Booking ID: " + result1.getBookingId());
        
        System.out.println("\n━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("OBSERVER PATTERN - Detaching SMS Observer");
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n");
        
        System.out.println("Customer opts out of SMS notifications...");
        System.out.println("Detaching SMSNotificationObserver...\n");
        
        // Detach SMS observer
        facade.getBookingService().getBookingSubject().detach(facade.getSmsObserver());
        
        System.out.println("✓ SMS notifications disabled");
        System.out.println("✓ Only Email and Inventory observers will be notified\n");

        System.out.println("\n━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("SCENARIO 2: Premium Weekend Booking (SMS Disabled)");
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n");

        LocalDateTime weekendTime = LocalDateTime.of(2025, 11, 29, 19, 0); // Saturday
        CinemaBookingFacade.BookingResult result2 = facade.completeBookingWorkflow(
                "Bob Smith", "bob.smith@email.com", "555-2002",
                2, // Movie: Avatar 3D
                TicketType.VIP,
                3, // 3 VIP tickets
                weekendTime,
                true, true, // With 3D glasses and snacks
                "STRIPE"
        );

        System.out.println("\n✓ Result: " + result2.getMessage());
        if (result2.isSuccess()) {
            System.out.println("✓ Booking ID: " + result2.getBookingId());
        }
        
        System.out.println("\n━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("OBSERVER PATTERN - Re-attaching SMS Observer");
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n");
        
        System.out.println("Customer re-enables SMS notifications...");
        System.out.println("Re-attaching SMSNotificationObserver...\n");
        
        // Re-attach SMS observer
        facade.getBookingService().getBookingSubject().attach(facade.getSmsObserver());
        
        System.out.println("✓ SMS notifications re-enabled");
        System.out.println("✓ All 3 observers active again\n");

        System.out.println("\n━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("SCENARIO 3: Booking Cancellation with Refund (All Observers Active)");
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n");

        // Create a booking to cancel
        LocalDateTime futureTime = LocalDateTime.of(2025, 12, 5, 20, 0);
        CinemaBookingFacade.BookingResult result3 = facade.completeBookingWorkflow(
                "Charlie Brown", "charlie@email.com", "555-3003",
                3, // Movie: Inception
                TicketType.REGULAR,
                1,
                futureTime,
                false, false,
                "CASH"
        );

        if (result3.isSuccess()) {
            System.out.println("✓ Booking created: " + result3.getBookingId());
            System.out.println("\nNow cancelling the booking...");
            
            boolean cancelled = facade.cancelBooking(
                    result3.getBookingId(),
                    "charlie@email.com",
                    "555-3003"
            );

            if (cancelled) {
                System.out.println("✓ Booking cancelled and refund processed");
            }
        }

        System.out.println("\n━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("SYSTEM REPORTS");
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n");

        facade.printInventoryReport();
        facade.printPaymentStatistics();
    }

    /**
     * Interactive booking demo
     */
    private static void interactiveBooking(Scanner scanner) {
        System.out.println("\n╔════════════════════════════════════════════════════════╗");
        System.out.println("║         INTERACTIVE BOOKING                            ║");
        System.out.println("╚════════════════════════════════════════════════════════╝\n");

        CinemaBookingFacade facade = new CinemaBookingFacade();

        // Show available movies
        System.out.println("Available Movies:");
        List<Movie> movies = facade.searchMovies();
        for (int i = 0; i < movies.size(); i++) {
            Movie m = movies.get(i);
            System.out.println((i + 1) + ". " + m.getTitle() + " (" + m.getGenre() + ", " + m.getFormat() + ")");
        }

        System.out.print("\nSelect movie (1-" + movies.size() + "): ");
        int movieChoice = scanner.nextInt();
        scanner.nextLine(); // consume newline

        if (movieChoice < 1 || movieChoice > movies.size()) {
            System.out.println("Invalid movie selection!");
            return;
        }

        System.out.print("Your name: ");
        String name = scanner.nextLine();

        System.out.print("Your email: ");
        String email = scanner.nextLine();

        System.out.print("Your phone: ");
        String phone = scanner.nextLine();

        System.out.print("Ticket type (REGULAR/VIP): ");
        String ticketTypeStr = scanner.nextLine().toUpperCase();
        TicketType ticketType = ticketTypeStr.equals("VIP") ? TicketType.VIP : TicketType.REGULAR;

        System.out.print("Number of tickets: ");
        int ticketCount = scanner.nextInt();
        scanner.nextLine();

        System.out.print("Add 3D glasses? (y/n): ");
        boolean add3D = scanner.nextLine().trim().toLowerCase().startsWith("y");

        System.out.print("Add snack combo? (y/n): ");
        boolean addSnacks = scanner.nextLine().trim().toLowerCase().startsWith("y");

        System.out.print("Payment method (STRIPE/PAYPAL/CASH): ");
        String paymentMethod = scanner.nextLine().toUpperCase();

        // Create booking with current time + 1 day
        LocalDateTime showtime = LocalDateTime.now().plusDays(1);

        System.out.println("\nProcessing your booking...\n");

        CinemaBookingFacade.BookingResult result = facade.completeBookingWorkflow(
                name, email, phone,
                movies.get(movieChoice - 1).getId(),
                ticketType,
                ticketCount,
                showtime,
                add3D, addSnacks,
                paymentMethod
        );

        if (result.isSuccess()) {
            System.out.println("\n╔════════════════════════════════════════════════════════╗");
            System.out.println("║              BOOKING SUCCESSFUL!                       ║");
            System.out.println("╠════════════════════════════════════════════════════════╣");
            System.out.println("║ Booking ID: " + String.format("%-41s", result.getBookingId()) + "║");
            System.out.println("║ Check your email for confirmation!                     ║");
            System.out.println("╚════════════════════════════════════════════════════════╝");
        } else {
            System.out.println("\n✗ Booking failed: " + result.getMessage());
        }
    }
}