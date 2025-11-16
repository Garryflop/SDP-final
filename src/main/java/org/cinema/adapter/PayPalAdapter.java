package org.cinema.adapter;


import org.cinema.model.Payment;
import org.cinema.model.enums.PaymentStatus;
import java.util.UUID;

/**
 * Adapter for PayPal payment gateway
 * Student 3: ERNAR
 * Pattern: Adapter (Structural)
 */
public class PayPalAdapter implements PaymentGatewayAdapter {
    private static final String GATEWAY_NAME = "PayPal";
    private static final double MAX_AMOUNT = 10000.00;
    private static final double MIN_AMOUNT = 1.00;

    @Override
    public boolean processPayment(Payment payment) {
        System.out.println("\n[PayPalAdapter] Processing payment...");
        System.out.println("├─ Gateway: " + GATEWAY_NAME);
        System.out.println("├─ Amount: $" + String.format("%.2f", payment.getAmount()));
        System.out.println("├─ Booking ID: " + payment.getBookingId());

        // Validate payment amount
        if (!validateAmount(payment.getAmount())) {
            System.out.println("└─ Status: FAILED (Invalid amount)");
            payment.setStatus(PaymentStatus.FAILED);
            return false;
        }

        // Simulate PayPal API call
        try {
            payment.setStatus(PaymentStatus.PROCESSING);

            // Simulate PayPal authorization flow
            System.out.println("├─ Step 1: Creating PayPal order...");
            Thread.sleep(300);

            // Generate mock PayPal order ID
            String orderId = "PAYPAL-" + UUID.randomUUID().toString().toUpperCase().substring(0, 17);

            System.out.println("├─ Step 2: Authorizing payment...");
            Thread.sleep(400);

            // Simulate payment approval
            boolean approved = simulatePayPalApproval();

            if (approved) {
                System.out.println("├─ Step 3: Capturing payment...");
                Thread.sleep(300);

                // Generate capture/transaction ID
                String captureId = "CAPTURE-" + UUID.randomUUID().toString().toUpperCase().substring(0, 13);
                payment.setTransactionId(captureId);
                payment.setStatus(PaymentStatus.COMPLETED);

                System.out.println("├─ PayPal Order ID: " + orderId);
                System.out.println("├─ Capture ID: " + captureId);
                System.out.println("└─ Status: ✓ SUCCESS (Payment Captured)");
                return true;
            } else {
                payment.setStatus(PaymentStatus.FAILED);
                System.out.println("└─ Status: ✗ FAILED (Payment not approved)");
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
        System.out.println("\n[PayPalAdapter] Processing refund...");
        System.out.println("├─ Gateway: " + GATEWAY_NAME);
        System.out.println("├─ Capture ID: " + payment.getTransactionId());
        System.out.println("├─ Amount: $" + String.format("%.2f", payment.getAmount()));

        // Validate payment is refundable
        if (payment.getStatus() != PaymentStatus.COMPLETED) {
            System.out.println("└─ Status: FAILED (Cannot refund incomplete payment)");
            return false;
        }

        try {
            // Simulate PayPal refund API call
            System.out.println("├─ Initiating PayPal refund...");
            Thread.sleep(400);

            // Generate refund ID
            String refundId = "REFUND-" + UUID.randomUUID().toString().toUpperCase().substring(0, 13);
            payment.setStatus(PaymentStatus.REFUNDED);

            System.out.println("├─ Refund ID: " + refundId);
            System.out.println("└─ Status: ✓ REFUNDED (Funds returned to PayPal account)");
            return true;

        } catch (InterruptedException e) {
            System.out.println("└─ Status: ✗ REFUND FAILED");
            Thread.currentThread().interrupt();
            return false;
        }
    }

    @Override
    public String verifyPaymentStatus(String transactionId) {
        System.out.println("\n[PayPalAdapter] Verifying payment status...");
        System.out.println("├─ Gateway: " + GATEWAY_NAME);
        System.out.println("├─ Transaction ID: " + transactionId);

        // Simulate PayPal API verification
        if (transactionId != null && transactionId.startsWith("CAPTURE-")) {
            System.out.println("└─ Status: COMPLETED - Payment Captured");
            return "COMPLETED";
        } else if (transactionId != null && transactionId.startsWith("REFUND-")) {
            System.out.println("└─ Status: REFUNDED");
            return "REFUNDED";
        } else {
            System.out.println("└─ Status: NOT_FOUND - Transaction not found");
            return "NOT_FOUND";
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
     * Simulate PayPal payment approval
     */
    private boolean simulatePayPalApproval() {
        // 92% success rate for demonstration
        return Math.random() < 0.92;
    }

    /**
     * Get PayPal-specific features
     */
    public boolean supportsPayLater() {
        // PayPal Pay Later feature
        return true;
    }

    /**
     * Check if payment is eligible for buyer protection
     */
    public boolean isBuyerProtectionEligible(double amount) {
        // Payments under $10,000 are eligible
        return amount < 10000.00;
    }

    /**
     * Get PayPal currency conversion rate (mock)
     */
    public double getCurrencyConversionRate(String fromCurrency, String toCurrency) {
        // Simplified mock implementation
        return 1.0; // Assume USD for demo
    }
}
