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

	@Override
	public boolean canPass(GateType gateType) {
		// TODO Auto-generated method stub
		return gateType == GateType.OUT;
	}

	@Override
	public String getDescription() {
		// TODO Auto-generated method stub
		return "Vé đã check-in, chờ check-out";
	}
	
}
