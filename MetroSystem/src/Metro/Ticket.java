package Metro;

import java.time.LocalDateTime;

public abstract class Ticket {

    protected String ticketId;
    protected TicketType type;
    protected TicketStatus status;
    protected double price;
    protected Passenger passenger;
    protected LocalDateTime purchaseDate;
    protected TicketState state;

    public Ticket(
            String ticketId,
            TicketType type,
            double price,
            Passenger passenger
    ) {

        this.ticketId = ticketId;
        this.type = type;
        this.price = price;
        this.passenger = passenger;

        this.status = TicketStatus.ACTIVE;

        this.purchaseDate = LocalDateTime.now();

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

    public LocalDateTime getPurchaseDate() {
        return purchaseDate;
    }

    public TicketState getState() {
        return state;
    }

    // =========================
    // Setter
    // =========================

    public void setState(
            TicketState state
    ) {

        this.state = state;
    }

    // =========================
    // Methods
    // =========================

    public boolean isValid() {

        return state.isValid();
    }

    public abstract double calcPrice(
            TicketType type
    );

    public String generateQR() {

        return "QR-" + ticketId;
    }

    public void read() {

        System.out.println(
                "Reading ticket: "
                + ticketId
        );
    }

    public boolean canRefund() {

        return state.canRefund();
    }
}
