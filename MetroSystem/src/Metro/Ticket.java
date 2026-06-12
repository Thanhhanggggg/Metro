package Metro;

import java.time.LocalDateTime;

public abstract class Ticket {
	protected RefundPolicy refundPolicy;
    protected String ticketId;
    protected TicketType type;
    protected TicketStatus status;
    protected double price;
    protected Passenger passenger;
    protected LocalDateTime purchasedAt;
    protected TicketState state;
    public Ticket(String ticketId, TicketType type, double price, Passenger passenger, RefundPolicy refundPolicy) {
        this.ticketId = ticketId;
        this.type = type;
        this.price = price;
        this.passenger = passenger;
        this.status = TicketStatus.ACTIVE;
        this.purchasedAt = LocalDateTime.now();
        this.state = new ActiveState();
        this.refundPolicy = refundPolicy; 
    }
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
    public void setStrategy(RefundPolicy refundPolicy) {
        this.refundPolicy = refundPolicy;
    }
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
    public double refund() {
        if (refundPolicy == null) {
            System.out.println("Chua co chinh sach hoan ve!");
            return 0.0;
        }
        if (!refundPolicy.canRefund(this)) {
            System.out.println("Khong du dieu kien hoan ve: " + refundPolicy.getRefundReason(this));
            return 0.0;
        }
        double amount = refundPolicy.getRefundAmount(this);
        setState(new RefundedState());
        System.out.println("Hoan ve thanh cong! So tien hoan: " + amount + " VND");
        System.out.println("Ly do: " + refundPolicy.getRefundReason(this));
        return amount;
    }
	public boolean isActive() {
		// TODO Auto-generated method stub
		return status == TicketStatus.ACTIVE;
	}
	@Override
	 public String toString() {
        return "Ticket [id=" + ticketId+ ", type="+ type+ ", status="+ status+ ", price="+ price+ "]";
    }
}
