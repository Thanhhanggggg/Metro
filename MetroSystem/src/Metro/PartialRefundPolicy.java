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

	
}
