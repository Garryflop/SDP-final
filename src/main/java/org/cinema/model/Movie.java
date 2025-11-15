package org.cinema.model;

public class Movie {
    private int id;
    private String title;
    private String genre;
    private String format;
    private int duration; // in minutes

    public Movie(int id, String title, String genre, String format, int duration) {
        this.id = id;
        this.title = title;
        this.genre = genre;
        this.format = format;
        this.duration = duration;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    @Override
    public String toString() {
        return "Movie{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", genre='" + genre + '\'' +
                ", format='" + format + '\'' +
                ", duration=" + duration +
                '}';
    }
}
