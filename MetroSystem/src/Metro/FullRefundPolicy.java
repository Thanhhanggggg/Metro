package Metro;

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

	

}
