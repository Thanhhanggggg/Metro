package Metro;

public class RefundedState implements TicketState{

	@Override
	public void handle(Ticket ticket) {
		// TODO Auto-generated method stub
		System.out.println("Ticket already refunded!");
	}

	@Override
	public boolean isValid() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean canRefund() {
		// TODO Auto-generated method stub
		return false;
	}

}
