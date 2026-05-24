package Metro;

import java.time.LocalDateTime;

public abstract class Ticket {
    protected String ticketId;
    protected TicketType type;
    protected TicketStatus status;
    protected double price;
    protected Passenger passenger;
    protected LocalDateTime purchasedAt;
    protected TicketState state;
    public Ticket(String ticketId, TicketType type, double price, Passenger passenger) {
        this.ticketId = ticketId;
        this.type = type;
        this.price = price;
        this.passenger = passenger;
        this.status = TicketStatus.ACTIVE;
        this.purchasedAt = LocalDateTime.now();
        // State mặc định
        this.state = new ActiveState();
    }
    // =========================
    // Getter
    // =========================
    public String getTicketId() {
        return ticketId;
    }
    public TicketType getType() {
        return type;
    }
    public TicketStatus getStatus() {
        return status;
    }

    public double getPrice() {
        return price;
    }
    public Passenger getPassenger() {
        return passenger;
    }
    public LocalDateTime getPurchasedAt() {
        return purchasedAt;
    }
    public TicketState getState() {
        return state;
    }
    // =========================
    // Setter
    // =========================
    public void setState(TicketState state) {
        this.state = state;
    }
    // =========================
    // Methods
    // =========================
    public boolean isValid() {
        return state.isValid();
    }
    public abstract double calcPrice(TicketType type);
    public String generateQR() {
        return "QR-" + ticketId;
    }
    public void read() {
        System.out.println("Reading ticket: " + ticketId);
    }
    public boolean canRefund() {
        return state.canRefund();
    }
    // check-in
    public void checkIn() {
        if(state.isValid()) {
            state.handle(this);
        } else {
            System.out.println("Ticket invalid for check-in!");
        }
    }
    // check-out
    public void checkOut() {
        if(state instanceof UsedState) {
            state.handle(this);
        } else {
            System.out.println("Ticket invalid for check-out!");
        }
    }
    // refund
    public void refund() {
        if(state.canRefund()) {
            setState(new RefundedState());
            System.out.println("Refund successful!");
        } else {
            System.out.println("Ticket cannot be refunded!");
        }
    }
	public boolean isActive() {
		// TODO Auto-generated method stub
		return false;
	}
}
