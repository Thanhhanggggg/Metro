package metro;

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

	public static void main(String[] args) {
		NoRefundPolicy policy = new NoRefundPolicy();

		Station s1 = new Station("S01", "Ben Thanh", 500);
		Station s2 = new Station("S02", "Nha Hat", 300);
		MetroLine ml = new MetroLine("L1", "Tuyen 1");
		ml.addStation(s1);
		ml.addStation(s2);
		Passenger p = new Passenger("P001", "Test");

		Ticket ve = TicketManager.getInstance().issueTicket(p, TicketType.SINGLE, 1, s1, s2, ml);

		// Dù vé mới tinh cũng không được hoàn
		System.out.println("-- Ve ACTIVE moi mua --");
		System.out.println("canRefund: " + policy.canRefund(ve) + " | mong doi: false");
		System.out.println("tienHoan: " + policy.getRefundAmount(ve) + " | mong doi: 0.0");
		System.out.println("lyDo: " + policy.getRefundReason(ve));

		// Test đổi policy trong TicketManager (Strategy Pattern)
		System.out.println("\n-- Demo doi policy --");
		TicketManager tm = TicketManager.getInstance();

		Ticket ve2 = TicketManager.getInstance().issueTicket(new Passenger("P002", "User2"), TicketType.DAILY, 0, s1,
				s1, ml);

		tm.setRefundPolicy(new FullRefundPolicy());
		System.out.println("FullRefund - canRefund: " + tm.canRefund(ve2) + " | mong doi: true");

		tm.setRefundPolicy(new NoRefundPolicy());
		System.out.println("NoRefund   - canRefund: " + tm.canRefund(ve2) + " | mong doi: false");
	}
}
