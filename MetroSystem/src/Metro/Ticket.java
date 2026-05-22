package Metro;

import java.time.LocalDateTime;

public class Ticket {
	private String ticketId;
    private TicketState state;
    private LocalDateTime createdTime;

    public Ticket(String ticketId) {
        this.ticketId = ticketId;
        this.state = new ActiveState();
        this.createdTime = LocalDateTime.now();
    }

    public void checkIn() {
        if (state.isValid()) {
            state.handle(this);
        } else {
            System.out.println("Ticket invalid for check-in!");
        }
    }

    public void checkOut() {
        if (state instanceof UsedState) {
            state.handle(this);
        } else {
            System.out.println("Ticket invalid for check-out!");
        }
    }

    public void refund() {
        if (state.canRefund()) {
            setState(new RefundedState());
            System.out.println("Refund successful!");
        } else {
            System.out.println("Ticket cannot be refunded!");
        }
    }

    public String getTicketId() {
        return ticketId;
    }

    public TicketState getState() {
        return state;
    }

    public void setState(TicketState state) {
        this.state = state;
    }

    public LocalDateTime getCreatedTime() {
        return createdTime;
    }
}
