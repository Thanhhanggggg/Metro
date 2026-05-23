package Metro;

import java.time.LocalDate;;

public class MonthlyPass extends Ticket {
	    private LocalDate validUntil;
	    private int remainingRide;
	    public MonthlyPass(String ticketId, double price, Passenger passenger) {
	        super(ticketId, TicketType.MONTHLY, price, passenger);
	        this.validUntil = LocalDate.now().plusMonths(1);
	        this.remainingRide = 100;
	    }

	    @Override
	    public boolean isValid() {
	        return super.isValid()
	                && LocalDate.now().isBefore(validUntil);
	    }
	}

