package org.cinema.adapter;

import org.cinema.model.Payment;

/**
 * Adapter interface for payment gateway integration
 * Student 3: ERNAR
 * Pattern: Adapter (Structural)
 */
public interface PaymentGatewayAdapter {
    /**
     * Process payment through the gateway
     */
    boolean processPayment(Payment payment);

    /**
     * Refund a payment
     */
    boolean refundPayment(Payment payment);

    /**
     * Verify payment status
     */
    String verifyPaymentStatus(String transactionId);

    /**
     * Get gateway name
     */
    String getGatewayName();
}