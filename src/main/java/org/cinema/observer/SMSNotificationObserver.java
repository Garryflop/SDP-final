package org.cinema.observer;

import org.cinema.model.enums.BookingEvent;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Concrete Observer for SMS notifications
 * Student 3: ERNAR
 * Pattern: Observer (Behavioral)
 */
public class SMSNotificationObserver implements BookingObserver {
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");

    @Override
    public void update(String bookingId, BookingEvent event, String customerEmail,
                       String customerPhone, String details) {
        String smsContent = buildSMSContent(bookingId, event, details);
        sendSMS(customerPhone, smsContent);
    }

    /**
     * Build concise SMS content based on event type
     * SMS messages are kept short due to character limitations
     */
    private String buildSMSContent(String bookingId, BookingEvent event, String details) {
        String time = LocalDateTime.now().format(formatter);
        StringBuilder sms = new StringBuilder();

        switch (event) {
            case CREATED:
                sms.append("[Cinema] Booking created: ").append(bookingId, 0, 8)
                        .append("... Please complete payment.");
                break;

            case CONFIRMED:
                sms.append("[Cinema] CONFIRMED! Booking: ").append(bookingId, 0, 8)
                        .append("... See you at the cinema!");
                break;

            case PAYMENT_COMPLETED:
                sms.append("[Cinema] Payment successful for booking: ")
                        .append(bookingId, 0, 8).append("...");
                break;

            case CANCELLED:
                sms.append("[Cinema] Booking cancelled: ").append(bookingId, 0, 8)
                        .append("... Refund in 5-7 days.");
                break;

            case PAYMENT_FAILED:
                sms.append("[Cinema] Payment failed for: ").append(bookingId, 0, 8)
                        .append("... Please retry.");
                break;

            case SEATS_RESERVED:
                sms.append("[Cinema] Seats reserved: ").append(details);
                break;

            case SEATS_RELEASED:
                sms.append("[Cinema] Seats released for: ").append(bookingId, 0, 8).append("...");
                break;

            default:
                sms.append("[Cinema] Update for: ").append(bookingId, 0, 8)
                        .append("... ").append(event);
                break;
        }

        return sms.toString();
    }

    /**
     * Simulate sending SMS (mock implementation)
     */
    private void sendSMS(String phoneNumber, String content) {
        System.out.println("\n[SMSObserver] Sending SMS to: " + phoneNumber);
        System.out.println("┌─────────────────────────────────────────────┐");
        System.out.println("│ " + String.format("%-43s", content) + " │");
        System.out.println("└─────────────────────────────────────────────┘");
    }
}