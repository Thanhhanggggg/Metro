package Metro;

public class SmartGate {
	private String gateId;
    private GateType type;
    private boolean active;

    public SmartGate(String gateId, GateType type) {
        this.gateId = gateId;
        this.type = type;
        this.active = true;
    }

    public boolean validateTicket(Ticket ticket) {

        if (!active) {
            System.out.println("Gate disabled!");
            return false;
        }

        if (ticket == null) {
            System.out.println("Ticket not found!");
            return false;
        }
        return ticket.canPass(type);
    }

    public void scanQRCode(String ticketId) {
    	Ticket ticket =TicketManager.getInstance().findById(ticketId);
        if (!validateTicket(ticket)) {
            closeGate();
            return;
        }
        if (type == GateType.IN) {
            ticket.checkIn();
        } else {
            ticket.checkOut();
        }
        openGate();
    }

    public void openGate() {
        System.out.println("Gate opened!");
    }

    public void closeGate() {
        System.out.println("Gate closed!");
    }

    public void disableGate() {
        active = false;
        System.out.println("Gate disabled!");
    }

    public String getGateId() {
        return gateId;
    }
    public void reportFault(String description) {
        disableGate();
        FaultLog log = new FaultLog("F001", gateId, description);
        log.saveLog();
    }
}
