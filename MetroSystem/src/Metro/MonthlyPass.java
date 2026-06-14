package Metro;

import java.time.LocalDate;
import java.util.List;

public class MonthlyPass extends Ticket {
    private List<MetroLine> validLines;
    private LocalDate validFrom;
    private LocalDate validUntil;
    private int rideCount;
    private int maxRides;
    public MonthlyPass(String ticketId, Passenger passenger, List<MetroLine> validLines) {
        super(ticketId, TicketType.MONTHLY, 0, passenger, new NoRefundPolicy());
        this.validLines = validLines;
        this.validFrom = LocalDate.now();
        this.validUntil = validFrom.plusMonths(1);
        this.rideCount = 0;
        this.maxRides = 60;
        this.price = calcPrice(TicketType.MONTHLY);
    }
    @Override
    public boolean isValid() {
    	return LocalDate.now().isBefore(validUntil) && getState().isValid();
    }
    @Override
    public double calcPrice(TicketType type) {
        return FareConfig.getInstance().getFixedPriceMonthly();
    }
    public double getRemainingRides() {
        return maxRides - rideCount;
    }
    public boolean coverLine(MetroLine line) {
        return validLines.contains(line);
    }
    public void onCheckOut() {
        if (!LocalDate.now().isBefore(validUntil)) {
            setState(new ExpiredState());
            setStatus(TicketStatus.EXPIRED);
            System.out.println("MonthlyPass hết hạn.");
        } else {
            setState(new ActiveState());
            setStatus(TicketStatus.ACTIVE);
            System.out.println("MonthlyPass check-out: còn hạn đến " + validUntil);
        }
    }
    public LocalDate getValidUntil() {
        return validUntil;
    }
}
