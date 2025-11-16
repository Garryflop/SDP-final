package org.cinema.observer;


import org.cinema.model.enums.BookingEvent;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Concrete Observer for email notifications
 * Student 3: ERNAR
 * Pattern: Observer (Behavioral)
 */
public class EmailNotificationObserver implements BookingObserver {
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    public void update(String bookingId, BookingEvent event, String customerEmail,
                       String customerPhone, String details) {
        String timestamp = LocalDateTime.now().format(formatter);
        String emailContent = buildEmailContent(bookingId, event, details, timestamp);

        sendEmail(customerEmail, emailContent);
    }

    /**
     * Build email content based on event type
     */
    private String buildEmailContent(String bookingId, BookingEvent event, String details, String timestamp) {
        StringBuilder email = new StringBuilder();
        email.append("\n╔════════════════════════════════════════════════════════╗\n");
        email.append("║              EMAIL NOTIFICATION                        ║\n");
        email.append("╚════════════════════════════════════════════════════════╝\n");

        switch (event) {
            case CREATED:
                email.append("Subject: Booking Created - Confirmation Pending\n");
                email.append("Dear Customer,\n\n");
                email.append("Your booking has been created successfully!\n");
                email.append("Booking ID: ").append(bookingId).append("\n");
                email.append(details).append("\n");
                email.append("\nPlease complete payment to confirm your booking.\n");
                break;

            case CONFIRMED:
                email.append("Subject: Booking Confirmed - Your Tickets Are Ready!\n");
                email.append("Dear Customer,\n\n");
                email.append("Congratulations! Your booking is confirmed.\n");
                email.append("Booking ID: ").append(bookingId).append("\n");
                email.append(details).append("\n");
                email.append("\nPlease show this email at the cinema entrance.\n");
                break;

            case PAYMENT_COMPLETED:
                email.append("Subject: Payment Successful\n");
                email.append("Dear Customer,\n\n");
                email.append("Your payment has been processed successfully.\n");
                email.append("Booking ID: ").append(bookingId).append("\n");
                email.append(details).append("\n");
                break;

            case CANCELLED:
                email.append("Subject: Booking Cancelled\n");
                email.append("Dear Customer,\n\n");
                email.append("Your booking has been cancelled.\n");
                email.append("Booking ID: ").append(bookingId).append("\n");
                email.append(details).append("\n");
                email.append("\nRefund will be processed within 5-7 business days.\n");
                break;

            case PAYMENT_FAILED:
                email.append("Subject: Payment Failed - Action Required\n");
                email.append("Dear Customer,\n\n");
                email.append("Unfortunately, your payment could not be processed.\n");
                email.append("Booking ID: ").append(bookingId).append("\n");
                email.append(details).append("\n");
                email.append("\nPlease try again or use a different payment method.\n");
                break;

            default:
                email.append("Subject: Booking Update\n");
                email.append("Dear Customer,\n\n");
                email.append("Your booking has been updated.\n");
                email.append("Booking ID: ").append(bookingId).append("\n");
                email.append("Event: ").append(event).append("\n");
                email.append(details).append("\n");
                break;
        }

        email.append("\nTimestamp: ").append(timestamp).append("\n");
        email.append("════════════════════════════════════════════════════════\n");

        return email.toString();
    }

    /**
     * Simulate sending email (mock implementation)
     */
    private void sendEmail(String email, String content) {
        System.out.println("[EmailObserver] Sending email to: " + email);
        System.out.println(content);
    }
}