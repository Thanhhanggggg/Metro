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
    // State Pattern
    protected TicketState state;
    // =========================
    // Constructor
    // =========================
    public Ticket(String ticketId,TicketType type,double price,Passenger passenger,RefundPolicy refundPolicy) {
        this.ticketId = ticketId;
        this.type = type;
        this.price = price;
        this.passenger = passenger;
        this.status = TicketStatus.ACTIVE;
        this.purchasedAt = LocalDateTime.now();
        this.state = new ActiveState();
        this.refundPolicy = refundPolicy;
    }
    // Getters
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
    public RefundPolicy getRefundPolicy() {
        return refundPolicy;
    }
    // Setters
    public void setStatus(TicketStatus status) {
        this.status = status;
    }
    public void setState(TicketState state) {
        this.state = state;
    }
    // Cho phép thay đổi Strategy runtime
    public void setStrategy(RefundPolicy refundPolicy) {
        this.refundPolicy = refundPolicy;
    }
    // Abstract Methods
    public abstract double calcPrice(TicketType type);
    public abstract boolean isValid();
    // Common Methods
    public String generateQR() {
        return "QR-" + ticketId;
    }
    public void read() {
        System.out.println("Reading ticket: " + ticketId);
    }
    // State Pattern
    public void checkIn() {
    	if (!isValid()) {
            setState(new ExpiredState());
            setStatus(TicketStatus.EXPIRED);
            System.out.println("Vé không còn hiệu lực, không thể check-in.");
            return;
        }
        if (state instanceof UsedState) {
            System.out.println("Vé đang trong hành trình, vui lòng check-out trước!");
            return;
        }
        if (state instanceof ActiveState) {
            state.handle(this);
            status = TicketStatus.USED;
        }
    }
    public void checkOut() {
        if(state instanceof UsedState) {
            state.handle(this);
            status = TicketStatus.EXPIRED;
        } else {
            System.out.println("Ticket not in journey!");
        }
    }
    // Strategy Pattern
    public boolean canRefund() {
        if(state == null || refundPolicy == null) {
            return false;
        }
        return state.canRefund() && refundPolicy.canRefund(this);
    }
    public double refund() {
        if(refundPolicy == null) {
            System.out.println("Chưa có RefundPolicy");
            return 0;
        }
        if(!state.canRefund()) {
            System.out.println("Trạng thái vé không cho phép hoàn");
            return 0;
        }
        double amount = refundPolicy.getRefundAmount(this);
        setStatus(TicketStatus.REFUNDED);
        setState(new RefundedState());
        System.out.println( "Refund success: " + amount);
        return amount;
    }

	public boolean isActive() {
		// TODO Auto-generated method stub
		return status == TicketStatus.ACTIVE;
	}
	public boolean canPass(GateType gateType) {
	    return state.canPass(gateType);
	}
    @Override
    public String toString() {
        return "Ticket [id=" + ticketId+ ", type="+ type+ ", status="+ status+ ", price="+ price+ "]";
    }
    public void onCheckOut() {
        setState(new ExpiredState());
        setStatus(TicketStatus.EXPIRED);
        System.out.println("Ticket expired after check-out.");
    }
}
