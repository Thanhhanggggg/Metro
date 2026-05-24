package Metro;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

public class TicketManager {

    private static TicketManager instance;
    private Map<String, Ticket> tickets;
    private RefundPolicy refundPolicy;  // them
    private TicketFactory factory;       // them

    private TicketManager() {
        tickets      = new HashMap<>();
        refundPolicy = new FullRefundPolicy(); 
        factory      = new TicketFactory();    
    }

    public static TicketManager getInstance() {
        if (instance == null) {
            instance = new TicketManager();
        }
        return instance;
    }

    public Ticket findById(String ticketId) {
        return tickets.get(ticketId);
    }

    public void saveTicket(Ticket ticket) {
        tickets.put(ticket.getTicketId(), ticket);
        System.out.println("Saved: " + ticket.getTicketId());
    }

    
    public Ticket issueTicket(Passenger passenger, TicketType type, int stops,
                              Station origin, Station destination, MetroLine line) {
        Ticket t = factory.factoryMethod(passenger, type, stops, origin, destination, line);
        tickets.put(t.getTicketId(), t);
        System.out.println("Issued: " + t.getTicketId()
                         + " | gia=" + t.getPrice()
                         + " | QR=" + t.generateQR());
        return t;
    }


    public boolean checkIn(String ticketId, Station station) {
        Ticket t = findById(ticketId);
        if (!(t.getState() instanceof ActiveState)) {
            System.out.println("Check-in REJECTED: "
                             + t.getState().getStateName());
            return false;
        }
        t.getState().handle(t); // ActiveState -> UsedState
        station.incrementCheckIn();
        HeatmapService.getInstance().analyzeRealtime(station);
        System.out.println("[TicketManager] Check-in OK: " + ticketId);
        return true;
    }
    public void setRefundPolicy(RefundPolicy policy) {
        this.refundPolicy = policy;
    }

    public boolean canRefund(Ticket ticket) {
        return refundPolicy.canRefund(ticket);
    }

   
}
