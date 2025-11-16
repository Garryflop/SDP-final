package org.cinema.adapter;


import org.cinema.model.Payment;
import org.cinema.model.enums.PaymentStatus;
import java.util.UUID;

/**
 * Adapter for Stripe payment gateway
 * Student 3: ERNAR
 * Pattern: Adapter (Structural)
 */
public class StripeAdapter implements PaymentGatewayAdapter {
    private static final String GATEWAY_NAME = "Stripe";
    private static final double MAX_AMOUNT = 999999.99;
    private static final double MIN_AMOUNT = 0.50;

    @Override
    public boolean processPayment(Payment payment) {
        System.out.println("\n[StripeAdapter] Processing payment...");
        System.out.println("├─ Gateway: " + GATEWAY_NAME);
        System.out.println("├─ Amount: $" + String.format("%.2f", payment.getAmount()));
        System.out.println("├─ Booking ID: " + payment.getBookingId());

        // Validate payment amount
        if (!validateAmount(payment.getAmount())) {
            System.out.println("└─ Status: FAILED (Invalid amount)");
            payment.setStatus(PaymentStatus.FAILED);
            return false;
        }

        // Simulate Stripe API call
        try {
            payment.setStatus(PaymentStatus.PROCESSING);

            // Simulate API processing delay
            Thread.sleep(500);

            // Generate mock Stripe transaction ID
            String transactionId = "pi_" + UUID.randomUUID().toString().replace("-", "").substring(0, 24);
            payment.setTransactionId(transactionId);

            // Simulate payment success (90% success rate for demo)
            boolean success = simulatePaymentProcessing();

            if (success) {
                payment.setStatus(PaymentStatus.COMPLETED);
                System.out.println("├─ Transaction ID: " + transactionId);
                System.out.println("└─ Status: ✓ SUCCESS");
                return true;
            } else {
                payment.setStatus(PaymentStatus.FAILED);
                System.out.println("└─ Status: ✗ FAILED (Card declined)");
                return false;
            }

        } catch (InterruptedException e) {
            payment.setStatus(PaymentStatus.FAILED);
            System.out.println("└─ Status: ✗ ERROR (Processing interrupted)");
            Thread.currentThread().interrupt();
            return false;
        }
    }

    @Override
    public boolean refundPayment(Payment payment) {
        System.out.println("\n[StripeAdapter] Processing refund...");
        System.out.println("├─ Gateway: " + GATEWAY_NAME);
        System.out.println("├─ Transaction ID: " + payment.getTransactionId());
        System.out.println("├─ Amount: $" + String.format("%.2f", payment.getAmount()));

        // Validate payment is refundable
        if (payment.getStatus() != PaymentStatus.COMPLETED) {
            System.out.println("└─ Status: FAILED (Cannot refund incomplete payment)");
            return false;
        }

        try {
            // Simulate Stripe refund API call
            Thread.sleep(300);

            // Generate refund ID
            String refundId = "re_" + UUID.randomUUID().toString().replace("-", "").substring(0, 24);
            payment.setStatus(PaymentStatus.REFUNDED);

            System.out.println("├─ Refund ID: " + refundId);
            System.out.println("└─ Status: ✓ REFUNDED");
            return true;

        } catch (InterruptedException e) {
            System.out.println("└─ Status: ✗ REFUND FAILED");
            Thread.currentThread().interrupt();
            return false;
        }
    }

    @Override
    public String verifyPaymentStatus(String transactionId) {
        System.out.println("\n[StripeAdapter] Verifying payment status...");
        System.out.println("├─ Gateway: " + GATEWAY_NAME);
        System.out.println("├─ Transaction ID: " + transactionId);

        // Simulate Stripe API verification
        if (transactionId != null && transactionId.startsWith("pi_")) {
            System.out.println("└─ Status: VERIFIED - Payment Completed");
            return "VERIFIED";
        } else {
            System.out.println("└─ Status: INVALID - Transaction not found");
            return "INVALID";
        }
    }

    @Override
    public String getGatewayName() {
        return GATEWAY_NAME;
    }

    /**
     * Validate payment amount
     */
    private boolean validateAmount(double amount) {
        return amount >= MIN_AMOUNT && amount <= MAX_AMOUNT;
    }

    /**
     * Simulate payment processing with realistic success/failure
     */
    private boolean simulatePaymentProcessing() {
        // 95% success rate for demonstration
        return Math.random() < 0.95;
    }

    /**
     * Get Stripe-specific configuration
     */
    public String getApiVersion() {
        return "2024-11-20.acacia";
    }

    /**
     * Check if 3D Secure is required
     */
    public boolean requires3DSecure(double amount) {
        // Simulate: amounts over $500 require 3D Secure
        return amount > 500.00;
    }
}