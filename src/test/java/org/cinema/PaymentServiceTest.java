package org.cinema;

import org.cinema.adapter.PaymentGatewayAdapter;
import org.cinema.model.Payment;
import org.cinema.model.enums.BookingEvent;
import org.cinema.model.enums.PaymentStatus;
import org.cinema.observer.BookingObserver;
import org.cinema.observer.BookingSubject;
import org.cinema.service.PaymentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for PaymentService
 * Tests payment processing with different adapters and observer notifications
 */
public class PaymentServiceTest {

    private PaymentService paymentService;
    private BookingSubject bookingSubject;
    private TestObserver testObserver;

    @BeforeEach
    public void setUp() {
        bookingSubject = new BookingSubject();
        testObserver = new TestObserver();
        bookingSubject.attach(testObserver);
        paymentService = new PaymentService(bookingSubject);
    }

    @Test
    public void registerGateway_addsGatewaySuccessfully() {
        // Arrange
        TestGatewayAdapter adapter = new TestGatewayAdapter("TestGateway", true);

        // Act
        paymentService.registerGateway("TEST", adapter);

        // Assert - verify by processing a payment with it
        boolean result = paymentService.processPayment("BOOKING-001", 50.0, "TEST",
                "test@example.com", "555-0000");
        assertTrue(result, "Should successfully process payment with registered gateway");
    }

    @Test
    public void processPayment_withSuccessfulGateway_returnsTrue() {
        // Arrange
        TestGatewayAdapter adapter = new TestGatewayAdapter("TestSuccess", true);
        paymentService.registerGateway("TEST", adapter);

        // Act
        boolean result = paymentService.processPayment("BOOKING-001", 100.0, "TEST",
                "customer@example.com", "555-1234");

        // Assert
        assertTrue(result, "Payment should succeed");
        assertTrue(adapter.wasProcessPaymentCalled(), "Adapter processPayment should be called");
    }

    @Test
    public void processPayment_withFailingGateway_returnsFalse() {
        // Arrange
        TestGatewayAdapter adapter = new TestGatewayAdapter("TestFail", false);
        paymentService.registerGateway("TEST", adapter);

        // Act
        boolean result = paymentService.processPayment("BOOKING-001", 100.0, "TEST",
                "customer@example.com", "555-1234");

        // Assert
        assertFalse(result, "Payment should fail");
        assertTrue(adapter.wasProcessPaymentCalled(), "Adapter processPayment should be called");
    }

    @Test
    public void processPayment_withUnregisteredGateway_returnsFalse() {
        // Act
        boolean result = paymentService.processPayment("BOOKING-001", 100.0, "UNREGISTERED",
                "customer@example.com", "555-1234");

        // Assert
        assertFalse(result, "Payment should fail for unregistered gateway");
    }

    @Test
    public void processPayment_onSuccess_notifiesObservers() {
        // Arrange
        TestGatewayAdapter adapter = new TestGatewayAdapter("TestGateway", true);
        paymentService.registerGateway("TEST", adapter);

        // Act
        paymentService.processPayment("BOOKING-001", 75.0, "TEST",
                "customer@example.com", "555-1234");

        // Assert
        assertEquals(1, testObserver.getUpdateCount(), "Observer should be notified once");
        assertEquals(BookingEvent.PAYMENT_COMPLETED, testObserver.getLastEvent(),
                "Event should be PAYMENT_COMPLETED");
        assertEquals("customer@example.com", testObserver.getLastCustomerEmail(),
                "Customer email should match");
    }

    @Test
    public void processPayment_onFailure_notifiesObserversWithFailedEvent() {
        // Arrange
        TestGatewayAdapter adapter = new TestGatewayAdapter("TestGateway", false);
        paymentService.registerGateway("TEST", adapter);

        // Act
        paymentService.processPayment("BOOKING-001", 75.0, "TEST",
                "customer@example.com", "555-1234");

        // Assert
        assertEquals(1, testObserver.getUpdateCount(), "Observer should be notified once");
        assertEquals(BookingEvent.PAYMENT_FAILED, testObserver.getLastEvent(),
                "Event should be PAYMENT_FAILED");
    }

    @Test
    public void processPayment_withNonExistentGateway_notifiesFailure() {
        // Act
        paymentService.processPayment("BOOKING-001", 75.0, "NONEXISTENT",
                "customer@example.com", "555-1234");

        // Assert
        assertEquals(1, testObserver.getUpdateCount(), "Observer should be notified of failure");
        assertEquals(BookingEvent.PAYMENT_FAILED, testObserver.getLastEvent(),
                "Event should be PAYMENT_FAILED");
    }

    @Test
    public void processPayment_storesPaymentInService() {
        // Arrange
        TestGatewayAdapter adapter = new TestGatewayAdapter("TestGateway", true);
        paymentService.registerGateway("TEST", adapter);

        // Act
        paymentService.processPayment("BOOKING-001", 50.0, "TEST",
                "customer@example.com", "555-1234");

        // Assert
        Payment payment = paymentService.getPaymentByBookingId("BOOKING-001");
        assertNotNull(payment, "Payment should be stored");
        assertEquals(50.0, payment.getAmount(), 0.01, "Amount should match");
        assertEquals("TEST", payment.getMethod(), "Method should match");
    }

    @Test
    public void refundPayment_withExistingPayment_succeeds() {
        // Arrange
        TestGatewayAdapter adapter = new TestGatewayAdapter("TestGateway", true);
        paymentService.registerGateway("TEST", adapter);
        paymentService.processPayment("BOOKING-001", 50.0, "TEST",
                "customer@example.com", "555-1234");
        
        Payment payment = paymentService.getPaymentByBookingId("BOOKING-001");
        assertNotNull(payment);

        // Act
        boolean result = paymentService.refundPayment(payment.getId(),
                "customer@example.com", "555-1234");

        // Assert
        assertTrue(result, "Refund should succeed");
        assertTrue(adapter.wasRefundPaymentCalled(), "Adapter refundPayment should be called");
    }

    @Test
    public void refundPayment_notifiesObserversWithCancelledEvent() {
        // Arrange
        TestGatewayAdapter adapter = new TestGatewayAdapter("TestGateway", true);
        paymentService.registerGateway("TEST", adapter);
        paymentService.processPayment("BOOKING-001", 50.0, "TEST",
                "customer@example.com", "555-1234");
        
        Payment payment = paymentService.getPaymentByBookingId("BOOKING-001");
        testObserver.reset(); // Clear previous notifications

        // Act
        paymentService.refundPayment(payment.getId(), "customer@example.com", "555-1234");

        // Assert
        assertEquals(1, testObserver.getUpdateCount(), "Observer should be notified of refund");
        assertEquals(BookingEvent.CANCELLED, testObserver.getLastEvent(),
                "Event should be CANCELLED");
    }

    @Test
    public void refundPayment_withNonExistentPayment_returnsFalse() {
        // Act
        boolean result = paymentService.refundPayment("NON_EXISTENT_ID",
                "customer@example.com", "555-1234");

        // Assert
        assertFalse(result, "Refund should fail for non-existent payment");
    }

    @Test
    public void verifyPayment_withExistingPayment_returnsStatus() {
        // Arrange
        TestGatewayAdapter adapter = new TestGatewayAdapter("TestGateway", true);
        paymentService.registerGateway("TEST", adapter);
        paymentService.processPayment("BOOKING-001", 50.0, "TEST",
                "customer@example.com", "555-1234");
        
        Payment payment = paymentService.getPaymentByBookingId("BOOKING-001");

        // Act
        String status = paymentService.verifyPayment(payment.getId());

        // Assert
        assertEquals("VERIFIED", status, "Should return verified status");
        assertTrue(adapter.wasVerifyPaymentStatusCalled(), "Adapter verifyPaymentStatus should be called");
    }

    @Test
    public void verifyPayment_withNonExistentPayment_returnsPaymentNotFound() {
        // Act
        String status = paymentService.verifyPayment("NON_EXISTENT_ID");

        // Assert
        assertEquals("PAYMENT_NOT_FOUND", status, "Should return PAYMENT_NOT_FOUND");
    }

    @Test
    public void getPayment_returnsStoredPayment() {
        // Arrange
        TestGatewayAdapter adapter = new TestGatewayAdapter("TestGateway", true);
        paymentService.registerGateway("TEST", adapter);
        paymentService.processPayment("BOOKING-001", 50.0, "TEST",
                "customer@example.com", "555-1234");
        
        Payment payment = paymentService.getPaymentByBookingId("BOOKING-001");
        String paymentId = payment.getId();

        // Act
        Payment retrieved = paymentService.getPayment(paymentId);

        // Assert
        assertNotNull(retrieved, "Should retrieve stored payment");
        assertEquals(paymentId, retrieved.getId(), "Payment ID should match");
    }

    @Test
    public void getStatistics_tracksPaymentMetrics() {
        // Arrange
        TestGatewayAdapter successAdapter = new TestGatewayAdapter("Success", true);
        TestGatewayAdapter failAdapter = new TestGatewayAdapter("Fail", false);
        paymentService.registerGateway("SUCCESS", successAdapter);
        paymentService.registerGateway("FAIL", failAdapter);

        // Act - Process multiple payments
        paymentService.processPayment("BOOKING-001", 50.0, "SUCCESS",
                "user1@example.com", "555-0001");
        paymentService.processPayment("BOOKING-002", 75.0, "SUCCESS",
                "user2@example.com", "555-0002");
        paymentService.processPayment("BOOKING-003", 100.0, "FAIL",
                "user3@example.com", "555-0003");

        PaymentService.PaymentStatistics stats = paymentService.getStatistics();

        // Assert
        assertEquals(3, stats.getTotalPayments(), "Should have 3 total payments");
        assertEquals(2, stats.getCompletedPayments(), "Should have 2 completed payments");
        assertEquals(1, stats.getFailedPayments(), "Should have 1 failed payment");
        assertEquals(125.0, stats.getTotalRevenue(), 0.01, "Revenue should be 50 + 75 = 125");
    }

    @Test
    public void getStatistics_includesRefundedPayments() {
        // Arrange
        TestGatewayAdapter adapter = new TestGatewayAdapter("TestGateway", true);
        paymentService.registerGateway("TEST", adapter);
        
        paymentService.processPayment("BOOKING-001", 50.0, "TEST",
                "customer@example.com", "555-1234");
        Payment payment = paymentService.getPaymentByBookingId("BOOKING-001");
        paymentService.refundPayment(payment.getId(), "customer@example.com", "555-1234");

        // Act
        PaymentService.PaymentStatistics stats = paymentService.getStatistics();

        // Assert
        assertEquals(1, stats.getTotalPayments(), "Should have 1 total payment");
        assertEquals(1, stats.getRefundedPayments(), "Should have 1 refunded payment");
    }

    @Test
    public void processPayment_multipleGateways_usesCorrectAdapter() {
        // Arrange
        TestGatewayAdapter adapter1 = new TestGatewayAdapter("Gateway1", true);
        TestGatewayAdapter adapter2 = new TestGatewayAdapter("Gateway2", true);
        paymentService.registerGateway("GATEWAY1", adapter1);
        paymentService.registerGateway("GATEWAY2", adapter2);

        // Act
        paymentService.processPayment("BOOKING-001", 50.0, "GATEWAY1",
                "user@example.com", "555-0000");
        paymentService.processPayment("BOOKING-002", 75.0, "GATEWAY2",
                "user@example.com", "555-0000");

        // Assert
        assertTrue(adapter1.wasProcessPaymentCalled(), "Gateway1 should be used");
        assertTrue(adapter2.wasProcessPaymentCalled(), "Gateway2 should be used");
    }

    // ==================== Test Helper Classes ====================

    /**
     * Test implementation of PaymentGatewayAdapter for testing
     */
    private static class TestGatewayAdapter implements PaymentGatewayAdapter {
        private final String gatewayName;
        private final boolean shouldSucceed;
        private boolean processPaymentCalled = false;
        private boolean refundPaymentCalled = false;
        private boolean verifyPaymentStatusCalled = false;

        public TestGatewayAdapter(String gatewayName, boolean shouldSucceed) {
            this.gatewayName = gatewayName;
            this.shouldSucceed = shouldSucceed;
        }

        @Override
        public boolean processPayment(Payment payment) {
            processPaymentCalled = true;
            if (shouldSucceed) {
                payment.setStatus(PaymentStatus.COMPLETED);
                payment.setTransactionId("TEST-TXN-" + System.currentTimeMillis());
                return true;
            } else {
                payment.setStatus(PaymentStatus.FAILED);
                return false;
            }
        }

        @Override
        public boolean refundPayment(Payment payment) {
            refundPaymentCalled = true;
            if (payment.getStatus() == PaymentStatus.COMPLETED) {
                payment.setStatus(PaymentStatus.REFUNDED);
                return true;
            }
            return false;
        }

        @Override
        public String verifyPaymentStatus(String transactionId) {
            verifyPaymentStatusCalled = true;
            return "VERIFIED";
        }

        @Override
        public String getGatewayName() {
            return gatewayName;
        }

        public boolean wasProcessPaymentCalled() {
            return processPaymentCalled;
        }

        public boolean wasRefundPaymentCalled() {
            return refundPaymentCalled;
        }

        public boolean wasVerifyPaymentStatusCalled() {
            return verifyPaymentStatusCalled;
        }
    }

    /**
     * Test observer for verifying notifications
     */
    private static class TestObserver implements BookingObserver {
        private int updateCount = 0;
        private String lastBookingId;
        private BookingEvent lastEvent;
        private String lastCustomerEmail;
        private String lastCustomerPhone;
        private String lastDetails;

        @Override
        public void update(String bookingId, BookingEvent event, String customerEmail,
                           String customerPhone, String details) {
            updateCount++;
            this.lastBookingId = bookingId;
            this.lastEvent = event;
            this.lastCustomerEmail = customerEmail;
            this.lastCustomerPhone = customerPhone;
            this.lastDetails = details;
        }

        public int getUpdateCount() {
            return updateCount;
        }

        public String getLastBookingId() {
            return lastBookingId;
        }

        public BookingEvent getLastEvent() {
            return lastEvent;
        }

        public String getLastCustomerEmail() {
            return lastCustomerEmail;
        }

        public String getLastCustomerPhone() {
            return lastCustomerPhone;
        }

        public String getLastDetails() {
            return lastDetails;
        }

        public void reset() {
            updateCount = 0;
            lastBookingId = null;
            lastEvent = null;
            lastCustomerEmail = null;
            lastCustomerPhone = null;
            lastDetails = null;
        }
    }
}
