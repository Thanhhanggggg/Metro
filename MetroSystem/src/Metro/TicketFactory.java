package Metro;

public class TicketFactory {
	    public static Ticket createTicket(TicketType type, String ticketId, double price, Passenger passenger) {
	        switch (type) {
	        
	            case SINGLE:
	                return new SingleTrip(ticketId, price, passenger);

	            case DAILY:
	                return new DayPass(ticketId, price, passenger);

	            case MONTHLY:
	                return new MonthlyPass(ticketId, price, passenger);

	            default:
	                throw new IllegalArgumentException("Invalid ticket type");
	        }
}
}