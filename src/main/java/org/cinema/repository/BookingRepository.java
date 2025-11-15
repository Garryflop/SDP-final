package org.cinema.repository;

import org.cinema.model.Booking;
import org.cinema.model.enums.BookingStatus;

import java.util.*;

public class BookingRepository {

    // key: bookingId
    private final Map<String, Booking> storage = new HashMap<>();

    public void save(Booking booking) {
        storage.put(booking.getId(), booking);
    }

    public Optional<Booking> findById(String id) {
        return Optional.ofNullable(storage.get(id));
    }

    public List<Booking> findAll() {
        return new ArrayList<>(storage.values());
    }

    public List<Booking> findByCustomerEmail(String email) {
        List<Booking> result = new ArrayList<>();
        for (Booking booking : storage.values()) {
            if (booking.getCustomer().getEmail() != null &&
                    booking.getCustomer().getEmail().equalsIgnoreCase(email)) {
                result.add(booking);
            }
        }
        return result;
    }

    public void deleteById(String id) {
        storage.remove(id);
    }

    public void updateStatus(String id, BookingStatus newStatus) {
        Booking booking = storage.get(id);
        if (booking != null) {
            booking.setStatus(newStatus);
        }
    }
}
