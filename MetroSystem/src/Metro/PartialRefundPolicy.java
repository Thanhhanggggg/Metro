package Metro;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

// Strategy Pattern 
public class PartialRefundPolicy implements RefundPolicy {
	private double penaltyRate;
	private int windowMinutes;

	public PartialRefundPolicy(double penaltyRate, int windowMinutes) {
		if (penaltyRate < 0 || penaltyRate > 1)
			throw new IllegalArgumentException("penaltyRate phai tu 0.0 den 1.0");
		this.penaltyRate = penaltyRate;
		this.windowMinutes = windowMinutes;
	}

	private long tinhSoPhut(Ticket ticket) {
		return ChronoUnit.MINUTES.between(ticket.getPurchasedAt(), LocalDateTime.now());
	}

	@Override
	public boolean canRefund(Ticket ticket) {
		if (!ticket.getState().canRefund())
			return false;
		return tinhSoPhut(ticket) <= windowMinutes;
	}

	@Override
	public double getRefundAmount(Ticket ticket) {
		if (!canRefund(ticket))
			return 0.0;
		// Hoàn = giá gốc * (1 - tỉ lệ phạt)
		return ticket.getPrice() * (1.0 - penaltyRate);
	}

	@Override
	public String getRefundReason(Ticket ticket) {
		if (!ticket.getState().canRefund())
			return "Ve da su dung, khong the hoan.";
		if (tinhSoPhut(ticket) <= windowMinutes)
			return "Hoan " + (int) ((1 - penaltyRate) * 100) + "% (phat " + (int) (penaltyRate * 100) + "%).";
		return "Qua " + windowMinutes + " phut, khong hoan tien.";
	}

	public static void main(String[] args) {
		// Phạt 30%, trong vòng 120 phút
		PartialRefundPolicy policy = new PartialRefundPolicy(0.3, 120);

		Station s1 = new Station("S01", "Ben Thanh", 500);
		Station s2 = new Station("S02", "Nha Hat", 300);
		MetroLine ml = new MetroLine("L1", "Tuyen 1");
		ml.addStation(s1);
		ml.addStation(s2);
		Passenger p = new Passenger("P001", "Test");

		Ticket ve = TicketManager.getInstance().issueTicket(p, TicketType.SINGLE, 1, s1, s2, ml);
		double giaGoc = ve.getPrice();

		System.out.println("Gia goc: " + giaGoc);
		// Vé mới, Active -> hoàn 70%
		System.out.println("\n-- Ve ACTIVE moi mua --");
		System.out.println("canRefund: " + policy.canRefund(ve) + " | mong doi: true");
		System.out.println("tienHoan: " + policy.getRefundAmount(ve) + " | mong doi: " + (giaGoc * 0.7));
		System.out.println("lyDo: " + policy.getRefundReason(ve));

		// Vé đã check-in -> không hoàn
		System.out.println("\n-- Ve da check-in --");
		TicketManager.getInstance().checkIn(ve.getTicketId(), s1);
		System.out.println("canRefund: " + policy.canRefund(ve) + " | mong doi: false");
		System.out.println("tienHoan: " + policy.getRefundAmount(ve) + " | mong doi: 0.0");

		// Test penaltyRate không hợp lệ
		System.out.println("\n-- penaltyRate = 1.5 (khong hop le) --");
		try {
			new PartialRefundPolicy(1.5, 60);
			System.out.println("FAIL: phai throw exception");
		} catch (IllegalArgumentException e) {
			System.out.println("OK: bat duoc loi - " + e.getMessage());
		}
	}

}
