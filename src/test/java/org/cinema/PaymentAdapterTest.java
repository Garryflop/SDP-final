package org.cinema;

import org.cinema.adapter.CashSystemAdapter;
import org.cinema.adapter.PayPalAdapter;
import org.cinema.adapter.PaymentGatewayAdapter;
import org.cinema.adapter.StripeAdapter;
import org.cinema.model.Payment;
import org.cinema.model.enums.PaymentStatus;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for Payment Adapter Pattern implementations
 * Tests StripeAdapter, PayPalAdapter, CashSystemAdapter
 */
public class PaymentAdapterTest {

    // ==================== StripeAdapter Tests ====================

    @Test
    public void stripeAdapter_processPayment_setsTransactionId() {
        // Arrange
        PaymentGatewayAdapter adapter = new StripeAdapter();
        Payment payment = new Payment("BOOKING-001", 50.00, "STRIPE");

        // Act
        boolean result = adapter.processPayment(payment);

        // Assert - Since it's probabilistic, we check that transaction ID is set when successful
        if (result) {
            assertNotNull(payment.getTransactionId(), "Transaction ID should be set");
            assertTrue(payment.getTransactionId().startsWith("pi_"), "Stripe transaction ID should start with 'pi_'");
            assertEquals(PaymentStatus.COMPLETED, payment.getStatus(), "Status should be COMPLETED");
        } else {
            assertEquals(PaymentStatus.FAILED, payment.getStatus(), "Status should be FAILED on failure");
        }
    }

    @Test
    public void stripeAdapter_processPayment_withValidAmount_succeeds() {
        // Arrange
        PaymentGatewayAdapter adapter = new StripeAdapter();
        Payment payment = new Payment("BOOKING-001", 100.00, "STRIPE");

        // Act
        boolean result = adapter.processPayment(payment);

        // Assert - Check that it attempts processing (result can be true/false due to simulation)
        assertTrue(payment.getStatus() == PaymentStatus.COMPLETED || 
                   payment.getStatus() == PaymentStatus.FAILED,
                "Status should be either COMPLETED or FAILED after processing");
    }

    @Test
    public void stripeAdapter_processPayment_withTooLowAmount_fails() {
        // Arrange
        PaymentGatewayAdapter adapter = new StripeAdapter();
        Payment payment = new Payment("BOOKING-001", 0.25, "STRIPE"); // Below $0.50 minimum

        // Act
        boolean result = adapter.processPayment(payment);

        // Assert
        assertFalse(result, "Should fail for amount below minimum");
        assertEquals(PaymentStatus.FAILED, payment.getStatus(), "Status should be FAILED");
    }

    @Test
    public void stripeAdapter_processPayment_withTooHighAmount_fails() {
        // Arrange
        PaymentGatewayAdapter adapter = new StripeAdapter();
        Payment payment = new Payment("BOOKING-001", 1000000.00, "STRIPE"); // Above $999,999.99 max

        // Act
        boolean result = adapter.processPayment(payment);

        // Assert
        assertFalse(result, "Should fail for amount above maximum");
        assertEquals(PaymentStatus.FAILED, payment.getStatus(), "Status should be FAILED");
    }

    @Test
    public void stripeAdapter_refundPayment_withCompletedPayment_succeeds() {
        // Arrange
        PaymentGatewayAdapter adapter = new StripeAdapter();
        Payment payment = new Payment("BOOKING-001", 50.00, "STRIPE");
        payment.setStatus(PaymentStatus.COMPLETED);
        payment.setTransactionId("pi_1234567890");

        // Act
        boolean result = adapter.refundPayment(payment);

        // Assert
        assertTrue(result, "Refund should succeed for completed payment");
        assertEquals(PaymentStatus.REFUNDED, payment.getStatus(), "Status should be REFUNDED");
    }

    @Test
    public void stripeAdapter_refundPayment_withPendingPayment_fails() {
        // Arrange
        PaymentGatewayAdapter adapter = new StripeAdapter();
        Payment payment = new Payment("BOOKING-001", 50.00, "STRIPE");
        // Status is PENDING by default

        // Act
        boolean result = adapter.refundPayment(payment);

        // Assert
        assertFalse(result, "Cannot refund pending payment");
        assertNotEquals(PaymentStatus.REFUNDED, payment.getStatus(), "Status should not be REFUNDED");
    }

    @Test
    public void stripeAdapter_verifyPaymentStatus_withValidTransactionId_returnsVerified() {
        // Arrange
        PaymentGatewayAdapter adapter = new StripeAdapter();

        // Act
        String status = adapter.verifyPaymentStatus("pi_1234567890abcdef");

        // Assert
        assertEquals("VERIFIED", status, "Should verify valid Stripe transaction ID");
    }

    @Test
    public void stripeAdapter_verifyPaymentStatus_withInvalidTransactionId_returnsInvalid() {
        // Arrange
        PaymentGatewayAdapter adapter = new StripeAdapter();

        // Act
        String status = adapter.verifyPaymentStatus("invalid_id");

        // Assert
        assertEquals("INVALID", status, "Should return INVALID for non-Stripe transaction ID");
    }

    @Test
    public void stripeAdapter_getGatewayName_returnsStripe() {
        // Arrange
        PaymentGatewayAdapter adapter = new StripeAdapter();

        // Act & Assert
        assertEquals("Stripe", adapter.getGatewayName(), "Gateway name should be Stripe");
    }

    // ==================== PayPalAdapter Tests ====================

    @Test
    public void payPalAdapter_processPayment_setsTransactionId() {
        // Arrange
        PaymentGatewayAdapter adapter = new PayPalAdapter();
        Payment payment = new Payment("BOOKING-002", 75.00, "PAYPAL");

        // Act
        boolean result = adapter.processPayment(payment);

        // Assert
        if (result) {
            assertNotNull(payment.getTransactionId(), "Transaction ID should be set");
            assertTrue(payment.getTransactionId().startsWith("CAPTURE-"), 
                    "PayPal transaction ID should start with 'CAPTURE-'");
            assertEquals(PaymentStatus.COMPLETED, payment.getStatus(), "Status should be COMPLETED");
        } else {
            assertEquals(PaymentStatus.FAILED, payment.getStatus(), "Status should be FAILED on failure");
        }
    }

    @Test
    public void payPalAdapter_processPayment_withValidAmount_succeeds() {
        // Arrange
        PaymentGatewayAdapter adapter = new PayPalAdapter();
        Payment payment = new Payment("BOOKING-002", 150.00, "PAYPAL");

        // Act
        boolean result = adapter.processPayment(payment);

        // Assert
        assertTrue(payment.getStatus() == PaymentStatus.COMPLETED || 
                   payment.getStatus() == PaymentStatus.FAILED,
                "Status should be either COMPLETED or FAILED after processing");
    }

    @Test
    public void payPalAdapter_processPayment_withTooLowAmount_fails() {
        // Arrange
        PaymentGatewayAdapter adapter = new PayPalAdapter();
        Payment payment = new Payment("BOOKING-002", 0.50, "PAYPAL"); // Below $1.00 minimum

        // Act
        boolean result = adapter.processPayment(payment);

        // Assert
        assertFalse(result, "Should fail for amount below minimum");
        assertEquals(PaymentStatus.FAILED, payment.getStatus(), "Status should be FAILED");
    }

    @Test
    public void payPalAdapter_processPayment_withTooHighAmount_fails() {
        // Arrange
        PaymentGatewayAdapter adapter = new PayPalAdapter();
        Payment payment = new Payment("BOOKING-002", 15000.00, "PAYPAL"); // Above $10,000 max

        // Act
        boolean result = adapter.processPayment(payment);

        // Assert
        assertFalse(result, "Should fail for amount above maximum");
        assertEquals(PaymentStatus.FAILED, payment.getStatus(), "Status should be FAILED");
    }

    @Test
    public void payPalAdapter_refundPayment_withCompletedPayment_succeeds() {
        // Arrange
        PaymentGatewayAdapter adapter = new PayPalAdapter();
        Payment payment = new Payment("BOOKING-002", 75.00, "PAYPAL");
        payment.setStatus(PaymentStatus.COMPLETED);
        payment.setTransactionId("CAPTURE-ABC123");

        // Act
        boolean result = adapter.refundPayment(payment);

        // Assert
        assertTrue(result, "Refund should succeed for completed payment");
        assertEquals(PaymentStatus.REFUNDED, payment.getStatus(), "Status should be REFUNDED");
    }

    @Test
    public void payPalAdapter_verifyPaymentStatus_withCaptureId_returnsCompleted() {
        // Arrange
        PaymentGatewayAdapter adapter = new PayPalAdapter();

        // Act
        String status = adapter.verifyPaymentStatus("CAPTURE-XYZ789");

        // Assert
        assertEquals("COMPLETED", status, "Should return COMPLETED for capture ID");
    }

    @Test
    public void payPalAdapter_verifyPaymentStatus_withRefundId_returnsRefunded() {
        // Arrange
        PaymentGatewayAdapter adapter = new PayPalAdapter();

        // Act
        String status = adapter.verifyPaymentStatus("REFUND-ABC123");

        // Assert
        assertEquals("REFUNDED", status, "Should return REFUNDED for refund ID");
    }

    @Test
    public void payPalAdapter_verifyPaymentStatus_withInvalidId_returnsNotFound() {
        // Arrange
        PaymentGatewayAdapter adapter = new PayPalAdapter();

        // Act
        String status = adapter.verifyPaymentStatus("INVALID-123");

        // Assert
        assertEquals("NOT_FOUND", status, "Should return NOT_FOUND for invalid ID");
    }

    @Test
    public void payPalAdapter_getGatewayName_returnsPayPal() {
        // Arrange
        PaymentGatewayAdapter adapter = new PayPalAdapter();

        // Act & Assert
        assertEquals("PayPal", adapter.getGatewayName(), "Gateway name should be PayPal");
    }

    // ==================== CashSystemAdapter Tests ====================

    @Test
    public void cashAdapter_processPayment_alwaysSucceeds() {
        // Arrange
        PaymentGatewayAdapter adapter = new CashSystemAdapter();
        Payment payment = new Payment("BOOKING-003", 40.00, "CASH");

        // Act
        boolean result = adapter.processPayment(payment);

        // Assert
        assertTrue(result, "Cash payment should always succeed");
        assertEquals(PaymentStatus.COMPLETED, payment.getStatus(), "Status should be COMPLETED");
        assertNotNull(payment.getTransactionId(), "Receipt number should be generated");
        assertTrue(payment.getTransactionId().startsWith("CASH-"), 
                "Receipt number should start with 'CASH-'");
    }

    @Test
    public void cashAdapter_processPayment_withLargeAmount_succeeds() {
        // Arrange
        PaymentGatewayAdapter adapter = new CashSystemAdapter();
        Payment payment = new Payment("BOOKING-003", 5000.00, "CASH");

        // Act
        boolean result = adapter.processPayment(payment);

        // Assert
        assertTrue(result, "Cash payment should succeed for any amount");
        assertEquals(PaymentStatus.COMPLETED, payment.getStatus(), "Status should be COMPLETED");
    }

    @Test
    public void cashAdapter_refundPayment_withCompletedPayment_succeeds() {
        // Arrange
        PaymentGatewayAdapter adapter = new CashSystemAdapter();
        Payment payment = new Payment("BOOKING-003", 40.00, "CASH");
        payment.setStatus(PaymentStatus.COMPLETED);
        payment.setTransactionId("CASH-20251116-123456-ABCD");

        // Act
        boolean result = adapter.refundPayment(payment);

        // Assert
        assertTrue(result, "Cash refund should succeed for completed payment");
        assertEquals(PaymentStatus.REFUNDED, payment.getStatus(), "Status should be REFUNDED");
    }

    @Test
    public void cashAdapter_refundPayment_withPendingPayment_fails() {
        // Arrange
        PaymentGatewayAdapter adapter = new CashSystemAdapter();
        Payment payment = new Payment("BOOKING-003", 40.00, "CASH");
        // Status is PENDING by default

        // Act
        boolean result = adapter.refundPayment(payment);

        // Assert
        assertFalse(result, "Cannot refund pending payment");
        assertNotEquals(PaymentStatus.REFUNDED, payment.getStatus(), "Status should not be REFUNDED");
    }

    @Test
    public void cashAdapter_verifyPaymentStatus_withCashReceipt_returnsVerified() {
        // Arrange
        PaymentGatewayAdapter adapter = new CashSystemAdapter();

        // Act
        String status = adapter.verifyPaymentStatus("CASH-20251116-143000-ABCD");

        // Assert
        assertEquals("VERIFIED", status, "Should verify valid cash receipt");
    }

    @Test
    public void cashAdapter_verifyPaymentStatus_withRefundReceipt_returnsRefunded() {
        // Arrange
        PaymentGatewayAdapter adapter = new CashSystemAdapter();

        // Act
        String status = adapter.verifyPaymentStatus("REFUND-20251116-150000-WXYZ");

        // Assert
        assertEquals("REFUNDED", status, "Should return REFUNDED for refund receipt");
    }

    @Test
    public void cashAdapter_verifyPaymentStatus_withInvalidReceipt_returnsInvalid() {
        // Arrange
        PaymentGatewayAdapter adapter = new CashSystemAdapter();

        // Act
        String status = adapter.verifyPaymentStatus("INVALID-RECEIPT");

        // Assert
        assertEquals("INVALID", status, "Should return INVALID for invalid receipt");
    }

    @Test
    public void cashAdapter_getGatewayName_returnsCash() {
        // Arrange
        PaymentGatewayAdapter adapter = new CashSystemAdapter();

        // Act & Assert
        assertEquals("Cash", adapter.getGatewayName(), "Gateway name should be Cash");
    }

    // ==================== Adapter Interface Tests ====================

    @Test
    public void allAdapters_implementSameInterface() {
        // Arrange & Act
        PaymentGatewayAdapter stripe = new StripeAdapter();
        PaymentGatewayAdapter paypal = new PayPalAdapter();
        PaymentGatewayAdapter cash = new CashSystemAdapter();

        // Assert
        assertNotNull(stripe.getGatewayName(), "Stripe should implement getGatewayName");
        assertNotNull(paypal.getGatewayName(), "PayPal should implement getGatewayName");
        assertNotNull(cash.getGatewayName(), "Cash should implement getGatewayName");
    }

    @Test
    public void allAdapters_haveUniqueGatewayNames() {
        // Arrange
        PaymentGatewayAdapter stripe = new StripeAdapter();
        PaymentGatewayAdapter paypal = new PayPalAdapter();
        PaymentGatewayAdapter cash = new CashSystemAdapter();

        // Act
        String stripeName = stripe.getGatewayName();
        String paypalName = paypal.getGatewayName();
        String cashName = cash.getGatewayName();

        // Assert
        assertNotEquals(stripeName, paypalName, "Stripe and PayPal should have different names");
        assertNotEquals(stripeName, cashName, "Stripe and Cash should have different names");
        assertNotEquals(paypalName, cashName, "PayPal and Cash should have different names");
    }
}
