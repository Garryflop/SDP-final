package com.project.repository;

import com.project.model.Seat;

import java.util.*;

public class SeatRepository {

    /**
     * Key: showtimeId (or hallId – договорись с командой).
     * Value: list of seats for that showtime.
     */
    private final Map<Integer, List<Seat>> seatsByShowtime = new HashMap<>();

    public void addSeatsForShowtime(int showtimeId, List<Seat> seats) {
        seatsByShowtime.put(showtimeId, new ArrayList<>(seats));
    }

    public List<Seat> getSeatsForShowtime(int showtimeId) {
        return seatsByShowtime.getOrDefault(showtimeId, Collections.emptyList());
    }

    public List<Seat> getAvailableSeats(int showtimeId) {
        List<Seat> result = new ArrayList<>();
        for (Seat seat : getSeatsForShowtime(showtimeId)) {
            if (seat.isAvailable()) {
                result.add(seat);
            }
        }
        return result;
    }

    public boolean markSeatAsBooked(int showtimeId, int row, int number) {
        List<Seat> seats = seatsByShowtime.get(showtimeId);
        if (seats == null) return false;

        for (Seat seat : seats) {
            if (seat.getRow() == row && seat.getNumber() == number && seat.isAvailable()) {
                seat.setAvailable(false);
                return true;
            }
        }
        return false;
    }
}
