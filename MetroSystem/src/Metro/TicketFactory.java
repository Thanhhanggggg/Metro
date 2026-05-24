package Metro;

public abstract class TicketFactory {
	  // Factory Method
 public abstract Ticket factoryMethod(
	            Passenger pass,
	            int stops
	    );
}
