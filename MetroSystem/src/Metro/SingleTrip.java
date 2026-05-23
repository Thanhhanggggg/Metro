package Metro;

public class SingleTrip extends Ticket {
	    private int duration;
	    private boolean used;
	    public SingleTrip(String ticketId, double price, Passenger passenger) {
	        super( ticketId, TicketType.SINGLE, price, passenger);
	        this.duration = 1;
	        this.used = false;
	    }
	    @Override
	    public double calculatePrice() {

	        return price;
	    }
	}
