package Metro;

import java.util.ArrayList;
import java.util.List;

public class Passenger {
    // Attributes
    private String passengerId;
    private String name;
    private PassengerType passengerType;
    private String verificationID;
    private double balance;
    private List<Ticket> tickets;

    // Constructor
    public Passenger(String passengerId, String name,
                     PassengerType passengerType,
                     String verificationID,
                     double balance) {
        this.passengerId = passengerId;
        this.name = name;
        this.passengerType = passengerType;
        this.verificationID = verificationID;
        this.balance = balance;
        this.tickets = new ArrayList<>();
    }

    // Buy ticket
    public Ticket buyTicket(int stop, MetroLine line) {
        Ticket ticket = TicketFactory.factoryMethod(this, null, stop);

        if (ticket != null) {
            tickets.add(ticket);
        }

        return ticket;
    }

    // Get active ticket
    public Ticket getActiveTicket() {
        for (Ticket ticket : tickets) {
            if (ticket.isActive()) {
                return ticket;
            }
        }
        return null;
    }

    // Refund ticket
    public boolean refund(Ticket ticket) {
        if (ticket != null && tickets.contains(ticket)) {
            tickets.remove(ticket);
            balance += ticket.getPrice();
            return true;
        }
        return false;
    }

    // Top up balance
    public void topUp(double amount) {
        if (amount > 0) {
            balance += amount;
        }
    }

    // Getters & Setters
    public String getPassengerId() {
        return passengerId;
    }

    public void setPassengerId(String passengerId) {
        this.passengerId = passengerId;
    }

    public String getName() {
        return name;
    }

    public PassengerType getPassengerType() {
        return passengerType;
    }

    public String getVerificationID() {
        return verificationID;
    }

    public double getBalance() {
        return balance;
    }

    public List<Ticket> getTickets() {
        return tickets;
    }
}
