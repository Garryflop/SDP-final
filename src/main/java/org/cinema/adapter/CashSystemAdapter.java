package org.cinema.adapter;



import org.cinema.model.Payment;
import org.cinema.model.enums.PaymentStatus;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

/**
 * Adapter for cash payment system (at cinema counter)
 * Student 3: ERNAR
 * Pattern: Adapter (Structural)
 */
public class CashSystemAdapter implements PaymentGatewayAdapter {
    private static final String GATEWAY_NAME = "Cash";
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss");

    @Override
    public boolean processPayment(Payment payment) {
        System.out.println("\n[CashSystemAdapter] Processing cash payment...");
        System.out.println("├─ Gateway: " + GATEWAY_NAME);
        System.out.println("├─ Amount: $" + String.format("%.2f", payment.getAmount()));
        System.out.println("├─ Booking ID: " + payment.getBookingId());

        try {
            payment.setStatus(PaymentStatus.PROCESSING);

            // Simulate cashier processing
            System.out.println("├─ Waiting for cashier confirmation...");
            Thread.sleep(200);

            // Generate cash receipt number
            String receiptNumber = "CASH-" + LocalDateTime.now().format(formatter) +
                    "-" + UUID.randomUUID().toString().substring(0, 4).toUpperCase();
            payment.setTransactionId(receiptNumber);

            // Cash payments are always successful once confirmed
            payment.setStatus(PaymentStatus.COMPLETED);

            System.out.println("├─ Receipt Number: " + receiptNumber);
            System.out.println("├─ Payment Method: Cash at Counter");
            System.out.println("└─ Status: ✓ RECEIVED");

            printCashReceipt(payment, receiptNumber);
            return true;

        } catch (InterruptedException e) {
            payment.setStatus(PaymentStatus.FAILED);
            System.out.println("└─ Status: ✗ ERROR (Processing interrupted)");
            Thread.currentThread().interrupt();
            return false;
        }
    }

    @Override
    public boolean refundPayment(Payment payment) {
        System.out.println("\n[CashSystemAdapter] Processing cash refund...");
        System.out.println("├─ Gateway: " + GATEWAY_NAME);
        System.out.println("├─ Receipt Number: " + payment.getTransactionId());
        System.out.println("├─ Amount: $" + String.format("%.2f", payment.getAmount()));

        // Validate payment is refundable
        if (payment.getStatus() != PaymentStatus.COMPLETED) {
            System.out.println("└─ Status: FAILED (Cannot refund incomplete payment)");
            return false;
        }

        try {
            // Simulate cashier refund process
            System.out.println("├─ Cashier processing refund...");
            Thread.sleep(300);

            // Generate refund receipt
            String refundReceipt = "REFUND-" + LocalDateTime.now().format(formatter) +
                    "-" + UUID.randomUUID().toString().substring(0, 4).toUpperCase();
            payment.setStatus(PaymentStatus.REFUNDED);

            System.out.println("├─ Refund Receipt: " + refundReceipt);
            System.out.println("└─ Status: ✓ CASH REFUNDED");

            printRefundReceipt(payment, refundReceipt);
            return true;

        } catch (InterruptedException e) {
            System.out.println("└─ Status: ✗ REFUND FAILED");
            Thread.currentThread().interrupt();
            return false;
        }
    }

    @Override
    public String verifyPaymentStatus(String transactionId) {
        System.out.println("\n[CashSystemAdapter] Verifying cash payment...");
        System.out.println("├─ Gateway: " + GATEWAY_NAME);
        System.out.println("├─ Receipt Number: " + transactionId);

        // Validate receipt number format
        if (transactionId != null && transactionId.startsWith("CASH-")) {
            System.out.println("└─ Status: VERIFIED - Cash payment confirmed");
            return "VERIFIED";
        } else if (transactionId != null && transactionId.startsWith("REFUND-")) {
            System.out.println("└─ Status: REFUNDED - Cash refund confirmed");
            return "REFUNDED";
        } else {
            System.out.println("└─ Status: INVALID - Receipt not found");
            return "INVALID";
        }
    }

    @Override
    public String getGatewayName() {
        return GATEWAY_NAME;
    }

    /**
     * Print cash receipt
     */
    private void printCashReceipt(Payment payment, String receiptNumber) {
        System.out.println("\n╔════════════════════════════════════════════════════════╗");
        System.out.println("║                  CASH RECEIPT                          ║");
        System.out.println("╠════════════════════════════════════════════════════════╣");
        System.out.println("║ Receipt No: " + String.format("%-42s", receiptNumber) + "║");
        System.out.println("║ Booking ID: " + String.format("%-42s", payment.getBookingId()) + "║");
        System.out.println("║ Amount Paid: $" + String.format("%-40.2f", payment.getAmount()) + "║");
        System.out.println("║ Payment Method: Cash                                   ║");
        System.out.println("║ Date: " + String.format("%-47s", LocalDateTime.now().format(
                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))) + "║");
        System.out.println("╠════════════════════════════════════════════════════════╣");
        System.out.println("║          THANK YOU FOR YOUR PURCHASE!                  ║");
        System.out.println("║     Please present this receipt at the entrance        ║");
        System.out.println("╚════════════════════════════════════════════════════════╝\n");
    }

    /**
     * Print refund receipt
     */
    private void printRefundReceipt(Payment payment, String refundReceipt) {
        System.out.println("\n╔════════════════════════════════════════════════════════╗");
        System.out.println("║                  REFUND RECEIPT                        ║");
        System.out.println("╠════════════════════════════════════════════════════════╣");
        System.out.println("║ Refund No: " + String.format("%-43s", refundReceipt) + "║");
        System.out.println("║ Original Receipt: " + String.format("%-36s", payment.getTransactionId()) + "║");
        System.out.println("║ Refund Amount: $" + String.format("%-39.2f", payment.getAmount()) + "║");
        System.out.println("║ Refund Method: Cash                                    ║");
        System.out.println("║ Date: " + String.format("%-47s", LocalDateTime.now().format(
                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))) + "║");
        System.out.println("╠════════════════════════════════════════════════════════╣");
        System.out.println("║              REFUND PROCESSED                          ║");
        System.out.println("╚════════════════════════════════════════════════════════╝\n");
    }

    /**
     * Calculate change to return
     */
    public double calculateChange(double amountPaid, double amountDue) {
        if (amountPaid < amountDue) {
            throw new IllegalArgumentException("Insufficient payment");
        }
        return amountPaid - amountDue;
    }

    /**
     * Validate cash denomination
     */
    public boolean validateCashDenomination(double amount) {
        // Check if amount is valid cash denomination (multiples of 0.01)
        return Math.round(amount * 100) == amount * 100;
    }
}