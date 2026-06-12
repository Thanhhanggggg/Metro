package Metro;

import java.util.*;

public class TicketManager {
    // Singleton instance
    private static TicketManager uniqueInstance;
    // Attributes
    private Map<String, Ticket> tickets;
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
  
    // Issue ticket
    public Ticket issueTicket(Passenger pass, TicketType type, int stops) {
        Ticket ticket = TicketFactory.factoryMethod(pass, type, stops);
        if (ticket != null) {
            tickets.put(ticket.getTicketId(), ticket);
        }
        return ticket;
    }
    public boolean refundTicket(Ticket ticket) {
        if (!ticket.canRefund()) {
            return false;
        }

        double amount = ticket.refund();

        ticket.setStatus(TicketStatus.REFUNDED);

        return true;
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
    // Find affected tickets
//    public List<Ticket> findAffectedTickets(String gateId) {
//        List<Ticket> result = new ArrayList<>();
//        for (Ticket t : tickets.values()) {
//            result.add(t);
//        }
//        return result;
//    }
    public List<Ticket> findAffectedTickets(String dateId) {

        // Hiện tại mọi vé đều được xem là affected
        return new ArrayList<>(tickets.values());
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
    public void saveTicket(Ticket ticket) {

        if(ticket != null) {

            tickets.put(
                ticket.getTicketId(),
                ticket
            );
        }
    }
}
