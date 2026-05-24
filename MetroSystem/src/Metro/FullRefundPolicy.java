package metro;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

//Strategy Pattern
public class FullRefundPolicy implements RefundPolicy {
	private final int PHUT_DUOC_HOAN = 30;

	private long tinhSoPhut(Ticket ticket) {
		return ChronoUnit.MINUTES.between(ticket.getPurchasedAt(), LocalDateTime.now());
	}

	@Override
	public boolean canRefund(Ticket ticket) {
		if (!ticket.getState().canRefund())
			return false;
		return tinhSoPhut(ticket) <= PHUT_DUOC_HOAN;
	}

	@Override
	public double getRefundAmount(Ticket ticket) {
		if (!canRefund(ticket))
			return 0.0;
		return ticket.getPrice();
	}

	@Override
	public String getRefundReason(Ticket ticket) {
		if (!ticket.getState().canRefund())
			return "Vé đã được sử dụng. Không thể hoàn";
		if (tinhSoPhut(ticket) <= PHUT_DUOC_HOAN)
			return "Hoàn 100%: còn trong " + PHUT_DUOC_HOAN + " phút.";
		return "Quá " + PHUT_DUOC_HOAN + " phút, không đủ điều kiện hoàn";
	}

	// Test
	public static void main(String[] args) {
		FullRefundPolicy policy = new FullRefundPolicy();

		Station s1 = new Station("S01", "Bến Thành", 500);
		Station s2 = new Station("S02", "Nhà Hát Thành Phố", 300);
		MetroLine ml = new MetroLine("L1", "Tuyến 1");
		ml.addStation(s1);
		ml.addStation(s2);
		Passenger p = new Passenger("P001", "Test User");

		Ticket ve = TicketManager.getInstance().issueTicket(p, TicketType.SINGLE, 1, s1, s2, ml);

		System.out.println("Vé mới mua: " + ve);
		// Vé mới, Active -> được hoàn
		System.out.println("--- Vé ACTIVE  --");
		System.out.println("canRefund: " + policy.canRefund(ve) + " | mong doi: true");
		System.out.println("tienHoan: " + policy.getRefundAmount(ve) + " | mong doi: " + ve.getPrice());
		System.out.println("lyDo: " + policy.getRefundReason(ve));

		// Vé đã check-in (UsedState) -> không được hoàn
		System.out.println("--- Vé  check-in (UsedState) ---");
		TicketManager.getInstance().checkIn(ve.getTicketId(), s1);
		System.out.println("canRefund: " + policy.canRefund(ve) + " | mong doi: false");
		System.out.println("tienHoan: " + policy.getRefundAmount(ve) + " | mong doi: 0.0");
		System.out.println("lyDo: " + policy.getRefundReason(ve));
	}

}
