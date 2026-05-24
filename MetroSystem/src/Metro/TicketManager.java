package Metro;

import java.util.*;

public class TicketManager {
    // Singleton instance
    private static TicketManager uniqueInstance;
    // Attributes
    private Map<String, Ticket> tickets;
    private RefundPolicy refundPolicy;
    // Private constructor
    private TicketManager() {
        tickets = new HashMap<>();
    }
    // Singleton method
    public static TicketManager getInstance() {
        if (uniqueInstance == null) {
            uniqueInstance = new TicketManager();
        }
        return uniqueInstance;
    }
    // Set refund policy
    public void setRefundPolicy(RefundPolicy policy) {
        this.refundPolicy = policy;
    }
    // Issue ticket
    public Ticket issueTicket(Passenger pass, TicketType type, int stops) {
        Ticket ticket = null;
        switch (type) {
            case SINGLE:
                ticket = new SingleTrip("T" + (tickets.size() + 1), pass, stops);
                break;
            case DAILY:
                ticket = new DayPass("T" + (tickets.size() + 1), pass);
                break;

            case MONTHLY:
                ticket = new MonthlyPass("T" + (tickets.size() + 1), pass);
                break;
        }
        if (ticket != null) {
            tickets.put(ticket.getTicketId(), ticket);
        }
        return ticket;
    }
    // Refund ticket
    public boolean refundTicket(Ticket ticket) {
        if (refundPolicy != null) {
            return refundPolicy.canRefund(ticket);
        }
        return false;
    }
    // Find ticket by ID
    public Ticket findById(String id) {
        return tickets.get(id);
    }
    // Check refund condition
    public boolean canRefund(Ticket ticket) {
        return ticket != null && ticket.canRefund();
    }
    // Find affected tickets
    public List<Ticket> findAffectedTickets(String dateId) {
        List<Ticket> result = new ArrayList<>();
        for (Ticket t : tickets.values()) {
            result.add(t);
        }
        return result;
    }
    // Revenue report
    public Map<TicketType, Integer> getRevenueReport(String dateRange) {
        Map<TicketType, Integer> report = new HashMap<>();
        for (Ticket t : tickets.values()) {
            TicketType type = t.getType();
            report.put(type, report.getOrDefault(type, 0) + 1);
        }
        return report;
    }
    // Confirm refund
    public void confirmRefund(Ticket ticket) {

        if (ticket != null) {
            System.out.println("Refund confirmed for ticket: "+ ticket.getTicketId());
        }
    }
}
