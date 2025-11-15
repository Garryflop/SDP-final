package org.cinema.util;

import org.cinema.model.Movie;
import org.cinema.model.Showtime;
import org.cinema.repository.MovieRepository;

import java.time.LocalDateTime;

public class DataInitializer {
    
    public static void init() {
        initMovies();
        System.out.println("Data initialized: Movies and Showtimes loaded");
    }
    
    private static void initMovies() {
        MovieRepository movieRepo = MovieRepository.getInstance();
        
        // Create sample movies
        Movie movie1 = new Movie(1, "Avengers: Endgame", "Action", "IMAX", 181);
        Movie movie2 = new Movie(2, "Avatar: The Way of Water", "Sci-Fi", "3D", 192);
        Movie movie3 = new Movie(3, "The Batman", "Action", "Standard", 176);
        
        movieRepo.save(movie1);
        movieRepo.save(movie2);
        movieRepo.save(movie3);
        
        System.out.println("  - Loaded " + movieRepo.findAll().size() + " movies");
    }
    
    public static Showtime getMatineeShowtime() {
        // Matinee showtime (before 5 PM)
        LocalDateTime matineeTime = LocalDateTime.now()
                .withHour(14)
                .withMinute(30)
                .withSecond(0);
        return new Showtime(1, 1, matineeTime, 50);
    }
    
    public static Showtime getWeekendShowtime() {
        // Weekend showtime (Saturday evening)
        LocalDateTime weekendTime = LocalDateTime.now()
                .plusDays(2) // Assuming it's close to weekend
                .withHour(19)
                .withMinute(0)
                .withSecond(0);
        return new Showtime(2, 2, weekendTime, 80);
    }
}
