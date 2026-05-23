package Metro;

import java.util.HashMap;
import java.util.Map;

public class TicketManager {
	    private static TicketManager instance;
	    private Map<String, Ticket> tickets;
	    private TicketManager() {
	        tickets = new HashMap<>();
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
}
