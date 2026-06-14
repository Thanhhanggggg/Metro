package Metro;

public class SmartGate {
	private String gateId;
    private GateType type;
    private boolean active;
    int faultCount = 1;

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

    public String getGateId() {
        return gateId;
    }
    public void reportFault(String description) {
        disableGate();
        String faultId = "F" + String.format("%03d", faultCount++);
        FaultLog log = new FaultLog(faultId, gateId, description);
        log.saveLog();
    }
    public void disableGate() {
        this.active = false;
        System.out.println("Cổng " + gateId + " đã bị vô hiệu hóa!");
    }

    public void enableGate() {
        this.active = true;
        System.out.println("Cổng " + gateId + " đã được kích hoạt lại!");
    }

    public boolean isActive() {
    	return active; 
    	}
    public GateType getType() {
    	return type; 
    }
}
