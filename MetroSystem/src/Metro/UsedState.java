package Metro;

public class UsedState implements TicketState{

	@Override
	public void handle(Ticket ticket) {
		// TODO Auto-generated method stub
		System.out.println("Check-out successful!");
        ticket.setState(new ExpiredState());
	}

	@Override
	public boolean isValid() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean canRefund() {
		// TODO Auto-generated method stub
		return false;
	}
	
}
