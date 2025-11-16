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
     * @param payment Payment object to process
     * @return true if payment successful, false otherwise
     */
    boolean processPayment(Payment payment);

    /**
     * Refund a payment
     * @param payment Payment object to refund
     * @return true if refund successful, false otherwise
     */
    boolean refundPayment(Payment payment);

    /**
     * Verify payment status
     * @param transactionId Transaction ID to verify
     * @return Payment status as string
     */
    String verifyPaymentStatus(String transactionId);

    /**
     * Get gateway name
     * @return Name of the payment gateway
     */
    String getGatewayName();
}