package org.cinema;

import org.cinema.decorator.Glasses3DDecorator;
import org.cinema.decorator.SnackComboDecorator;
import org.cinema.factory.TicketFactory;
import org.cinema.model.Movie;
import org.cinema.model.Ticket;
import org.cinema.model.enums.TicketType;
import org.cinema.repository.MovieRepository;
import org.cinema.util.DataInitializer;

import java.util.List;

public class Main {
    public static void main(String[] args) {

        // Initialize data
        DataInitializer.init();
        System.out.println();

        // Demo Factory Pattern
        demoFactoryPattern();
        System.out.println();

        // Demo Decorator Pattern
        demoDecoratorPattern();
        System.out.println();

        // Demo Repository Pattern
        demoRepositoryPattern();
        System.out.println();

        // Demo Factory + Decorator Integration
        demoIntegration();
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
}