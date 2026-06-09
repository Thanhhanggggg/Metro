package Metro;

// Strategy Pattern
public class NoRefundPolicy implements RefundPolicy {

	@Override
	public boolean canRefund(Ticket ticket) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public double getRefundAmount(Ticket ticket) {
		// TODO Auto-generated method stub
		return 0.0;
	}

	@Override
	public String getRefundReason(Ticket ticket) {
		// TODO Auto-generated method stub
		return "Chính sách không hoàn vé";
	}

	
}
