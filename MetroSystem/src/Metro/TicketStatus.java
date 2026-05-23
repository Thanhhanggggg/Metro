package Metro;

public enum TicketStatus {
	    ACTIVE,
	    USED,
	    EXPIRED,
	    REFUNDED;
	    public boolean isValid() {
	        return this == ACTIVE;
	    }
	}
