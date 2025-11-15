package com.project.model;


public class Seat {
    private final int row;
    private final int number;
    private final SeatType type;
    private boolean available;

    public Seat(int row, int number, SeatType type, boolean available) {
        if (row <= 0 || number <= 0) {
            throw new IllegalArgumentException("Row and number must be positive");
        }
        this.row = row;
        this.number = number;
        this.type = type;
        this.available = available;
    }

    public int getRow() {
        return row;
    }

    public int getNumber() {
        return number;
    }

    public SeatType getType() {
        return type;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    @Override
    public String toString() {
        return "Seat{" +
                "row=" + row +
                ", number=" + number +
                ", type=" + type +
                ", available=" + available +
                '}';
    }
}

/**
 * Seat type affects base price (you can use this later in PricingStrategy).
 */
enum SeatType {
    STANDARD,
    PREMIUM,
    VIP
}
