package Metro;

import java.time.LocalDate;

public class DayPass extends Ticket {
	    private LocalDate validDate;
	    public DayPass(String ticketId, double price, Passenger passenger) {
	        super(ticketId, TicketType.DAILY, price, passenger);
	        this.validDate = LocalDate.now();
	    }

	    @Override
	    public boolean isValid() {
	        return super.isValid()
	                && LocalDate.now().equals(validDate);
	    }
	}
