package Metro;

public interface TicketState {
	void handle(Ticket ticket);
	boolean isValid();
	boolean canRefund();
	boolean canPass(GateType gateType);
	String getDescription();

}
