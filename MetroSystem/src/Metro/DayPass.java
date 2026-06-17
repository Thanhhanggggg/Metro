package Metro;

import java.time.LocalDate;

public class DayPass extends Ticket {
    private LocalDate validDate;
    private int ridesUsed;
    private boolean unlimitedRides;
    public DayPass(String ticketId, Passenger passenger) {
        super(ticketId, TicketType.DAILY, 0, passenger, new FullRefundPolicy());
        this.validDate = LocalDate.now();
        this.ridesUsed = 0;
        this.unlimitedRides = true;
        this.price =
                calcPrice(TicketType.DAILY);
    }
    
    @Override
    public double calcPrice(TicketType type) {
        return FareConfig.getInstance().getFixedPriceDaily();
    }
    public void incrementRide() {
        ridesUsed++;
    }
    @Override
    public boolean isValid() {
        return LocalDate.now().equals(validDate) 
        		&& getState().isValid();
    }
    public void onCheckOut() {
        incrementRide();
        setState(new ActiveState());
        setStatus(TicketStatus.ACTIVE);
        System.out.println("DayPass check-out: lượt " + ridesUsed + " | Vé vẫn active cả ngày.");
    }
    public LocalDate getValidDate() {
        return validDate;
    }
}
