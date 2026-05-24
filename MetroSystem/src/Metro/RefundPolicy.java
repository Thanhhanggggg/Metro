package metro;
// Strategy Pattern 
public interface RefundPolicy {
    boolean canRefund(Ticket ticket);
    double getRefundAmount(Ticket ticket);
    String getRefundReason(Ticket ticket);
}
