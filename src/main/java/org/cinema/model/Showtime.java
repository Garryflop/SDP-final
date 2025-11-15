package org.cinema.model;

import java.time.LocalDateTime;

public class Showtime {
    private int id;
    private int movieId;
    private LocalDateTime dateTime;
    private int availableSeats;

    public Showtime(int id, int movieId, LocalDateTime dateTime, int availableSeats) {
        this.id = id;
        this.movieId = movieId;
        this.dateTime = dateTime;
        this.availableSeats = availableSeats;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getMovieId() {
        return movieId;
    }

    public void setMovieId(int movieId) {
        this.movieId = movieId;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public void setDateTime(LocalDateTime dateTime) {
        this.dateTime = dateTime;
    }

    public int getAvailableSeats() {
        return availableSeats;
    }

    public void setAvailableSeats(int availableSeats) {
        this.availableSeats = availableSeats;
    }

    @Override
    public String toString() {
        return "Showtime{" +
                "id=" + id +
                ", movieId=" + movieId +
                ", dateTime=" + dateTime +
                ", availableSeats=" + availableSeats +
                '}';
    }
}
