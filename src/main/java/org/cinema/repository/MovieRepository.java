package org.cinema.repository;

import org.cinema.model.Movie;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MovieRepository {
    private static MovieRepository instance;
    private Map<Integer, Movie> movieStore;

    private MovieRepository() {
        this.movieStore = new HashMap<>();
    }

    public static MovieRepository getInstance() {
        if (instance == null) {
            instance = new MovieRepository();
        }
        return instance;
    }

    public void save(Movie movie) {
        movieStore.put(movie.getId(), movie);
    }

    public Movie findById(int id) {
        return movieStore.get(id);
    }

    public List<Movie> findAll() {
        return new ArrayList<>(movieStore.values());
    }

    public List<Movie> findByGenre(String genre) {
        List<Movie> result = new ArrayList<>();
        for (Movie movie : movieStore.values()) {
            if (movie.getGenre().equalsIgnoreCase(genre)) {
                result.add(movie);
            }
        }
        return result;
    }

    public void clear() {
        movieStore.clear();
    }
}
