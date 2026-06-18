package Metro;

import java.time.LocalDateTime;

public class SingleTrip extends Ticket {

    private Station origin;
    private Station destination;
    private MetroLine line;
    private int stops;
    private LocalDateTime expiredAt;

    public SingleTrip(String ticketId,Passenger passenger, int stops) {
//            Station origin,
//            Station destination,
//            MetroLine line,
        super(ticketId, TicketType.SINGLE, 0, passenger, new FullRefundPolicy());
//        this.origin = origin;
//        this.destination = destination;
//        this.line = line;
        this.stops = stops;
        this.price = calcPrice(TicketType.SINGLE);
        this.expiredAt = LocalDateTime.now().plusHours(2);
    }
    public SingleTrip(String ticketId, Passenger passenger, int stops, Station destination) {
        this(ticketId, passenger, stops);
        this.destination = destination;
    }
    public Station getDestination() {
        return destination;
    }
    @Override
    public boolean isValid() {
        return !isExpired() && getState().isValid();
    }
    @Override
    public double calcPrice(TicketType type) {
        return FareConfig.getInstance().calculateFare(stops, passenger.getPassengerType());
    }
    public boolean isExpired() {
        return LocalDateTime.now().isAfter(expiredAt);
    }
	@Override
	public void onCheckOut() {
		setState(new ExpiredState());
        setStatus(TicketStatus.EXPIRED);
        System.out.println("Ticket expired after check-out.");
	}
}
