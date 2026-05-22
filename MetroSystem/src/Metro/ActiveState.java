package Metro;

public class ActiveState implements TicketState {

	@Override
	public void handle(Ticket ticket) {
		// TODO Auto-generated method stub
		System.out.println("Check-in successful!");
        ticket.setState(new UsedState());
	}

	@Override
	public boolean isValid() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean canRefund() {
		// TODO Auto-generated method stub
		return true;
	}

}
