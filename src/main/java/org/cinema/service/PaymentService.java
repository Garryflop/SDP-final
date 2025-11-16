package org.cinema.service;

import org.cinema.adapter.PaymentGatewayAdapter;
import org.cinema.model.enums.BookingEvent;
import org.cinema.model.Payment;
import org.cinema.model.enums.PaymentStatus;
import org.cinema.observer.BookingSubject;

import java.util.HashMap;
import java.util.Map;

/**
 * Service layer for payment processing
 * Uses Adapter pattern to process payments through different gateways
 * Student 3: ERNAR
 */
public class PaymentService {
    private BookingSubject bookingSubject;
    private Map<String, PaymentGatewayAdapter> paymentGateways;
    private Map<String, Payment> payments;

    public PaymentService(BookingSubject bookingSubject) {
        this.bookingSubject = bookingSubject;
        this.paymentGateways = new HashMap<>();
        this.payments = new HashMap<>();
    }

    /**
     * Register a payment gateway adapter
     */
    public void registerGateway(String gatewayName, PaymentGatewayAdapter adapter) {
        paymentGateways.put(gatewayName.toUpperCase(), adapter);
        System.out.println("[PaymentService] Registered gateway: " + gatewayName);
    }

    /**
     * Process payment for a booking
     */
    public boolean processPayment(String bookingId, double amount, String paymentMethod,
                                  String customerEmail, String customerPhone) {
        System.out.println("\n╔════════════════════════════════════════════════════════╗");
        System.out.println("║              PROCESSING PAYMENT                        ║");
        System.out.println("╚════════════════════════════════════════════════════════╝");

        // Get appropriate payment gateway
        PaymentGatewayAdapter gateway = paymentGateways.get(paymentMethod.toUpperCase());
        if (gateway == null) {
            System.out.println("[PaymentService] ERROR: Payment gateway not found: " + paymentMethod);
            notifyPaymentFailed(bookingId, customerEmail, customerPhone,
                    "Payment gateway not available: " + paymentMethod);
            return false;
        }

        // Create payment object
        Payment payment = new Payment(bookingId, amount, paymentMethod.toUpperCase());
        payments.put(payment.getId(), payment);

        System.out.println("[PaymentService] Processing via " + gateway.getGatewayName());
        System.out.println("[PaymentService] Booking: " + bookingId);
        System.out.println("[PaymentService] Amount: $" + String.format("%.2f", amount));

        // Process payment through adapter
        boolean success = gateway.processPayment(payment);

        if (success) {
            // Notify observers about successful payment
            String details = String.format("Payment of $%.2f via %s - Transaction: %s",
                    amount, gateway.getGatewayName(),
                    payment.getTransactionId());
            bookingSubject.notifyObservers(bookingId, BookingEvent.PAYMENT_COMPLETED,
                    customerEmail, customerPhone, details);

            System.out.println("\n[PaymentService] ✓ Payment successful!");
            System.out.println("════════════════════════════════════════════════════════\n");
            return true;
        } else {
            // Notify observers about failed payment
            notifyPaymentFailed(bookingId, customerEmail, customerPhone,
                    "Payment failed via " + gateway.getGatewayName());

            System.out.println("\n[PaymentService] ✗ Payment failed!");
            System.out.println("════════════════════════════════════════════════════════\n");
            return false;
        }
    }

    /**
     * Refund payment for a booking
     */
    public boolean refundPayment(String paymentId, String customerEmail, String customerPhone) {
        Payment payment = payments.get(paymentId);
        if (payment == null) {
            System.out.println("[PaymentService] ERROR: Payment not found: " + paymentId);
            return false;
        }

        PaymentGatewayAdapter gateway = paymentGateways.get(payment.getMethod());
        if (gateway == null) {
            System.out.println("[PaymentService] ERROR: Payment gateway not found: " + payment.getMethod());
            return false;
        }

        System.out.println("\n[PaymentService] Processing refund via " + gateway.getGatewayName());
        boolean success = gateway.refundPayment(payment);

        if (success) {
            String details = String.format("Refund of $%.2f processed via %s",
                    payment.getAmount(), gateway.getGatewayName());
            bookingSubject.notifyObservers(payment.getBookingId(), BookingEvent.CANCELLED,
                    customerEmail, customerPhone, details);
            System.out.println("[PaymentService] ✓ Refund successful!");
            return true;
        } else {
            System.out.println("[PaymentService] ✗ Refund failed!");
            return false;
        }
    }

    /**
     * Verify payment status
     */
    public String verifyPayment(String paymentId) {
        Payment payment = payments.get(paymentId);
        if (payment == null) {
            return "PAYMENT_NOT_FOUND";
        }

        PaymentGatewayAdapter gateway = paymentGateways.get(payment.getMethod());
        if (gateway == null) {
            return "GATEWAY_NOT_FOUND";
        }

        return gateway.verifyPaymentStatus(payment.getTransactionId());
    }

    /**
     * Get payment details
     */
    public Payment getPayment(String paymentId) {
        return payments.get(paymentId);
    }

    /**
     * Get all payments
     */
    public Map<String, Payment> getAllPayments() {
        return new HashMap<>(payments);
    }

    /**
     * Get payments by booking ID
     */
    public Payment getPaymentByBookingId(String bookingId) {
        return payments.values().stream()
                .filter(p -> p.getBookingId().equals(bookingId))
                .findFirst()
                .orElse(null);
    }

    /**
     * Notify observers about payment failure
     */
    private void notifyPaymentFailed(String bookingId, String customerEmail,
                                     String customerPhone, String reason) {
        bookingSubject.notifyObservers(bookingId, BookingEvent.PAYMENT_FAILED,
                customerEmail, customerPhone, reason);
    }

    /**
     * Get payment statistics
     */
    public PaymentStatistics getStatistics() {
        int total = payments.size();
        long completed = payments.values().stream()
                .filter(p -> p.getStatus() == PaymentStatus.COMPLETED)
                .count();
        long failed = payments.values().stream()
                .filter(p -> p.getStatus() == PaymentStatus.FAILED)
                .count();
        long refunded = payments.values().stream()
                .filter(p -> p.getStatus() == PaymentStatus.REFUNDED)
                .count();
        double totalAmount = payments.values().stream()
                .filter(p -> p.getStatus() == PaymentStatus.COMPLETED)
                .mapToDouble(Payment::getAmount)
                .sum();

        return new PaymentStatistics(total, (int)completed, (int)failed, (int)refunded, totalAmount);
    }

    /**
     * Inner class for payment statistics
     */
    public static class PaymentStatistics {
        private int totalPayments;
        private int completedPayments;
        private int failedPayments;
        private int refundedPayments;
        private double totalRevenue;

        public PaymentStatistics(int total, int completed, int failed, int refunded, double revenue) {
            this.totalPayments = total;
            this.completedPayments = completed;
            this.failedPayments = failed;
            this.refundedPayments = refunded;
            this.totalRevenue = revenue;
        }

        public void print() {
            System.out.println("\n╔════════════════════════════════════════════════════════╗");
            System.out.println("║           PAYMENT STATISTICS                           ║");
            System.out.println("╚════════════════════════════════════════════════════════╝");
            System.out.println("Total Payments: " + totalPayments);
            System.out.println("Completed: " + completedPayments);
            System.out.println("Failed: " + failedPayments);
            System.out.println("Refunded: " + refundedPayments);
            System.out.printf("Total Revenue: $%.2f%n", totalRevenue);
            System.out.println("════════════════════════════════════════════════════════\n");
        }

        // Getters
        public int getTotalPayments() { return totalPayments; }
        public int getCompletedPayments() { return completedPayments; }
        public int getFailedPayments() { return failedPayments; }
        public int getRefundedPayments() { return refundedPayments; }
        public double getTotalRevenue() { return totalRevenue; }
    }
}