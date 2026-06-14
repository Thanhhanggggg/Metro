package Metro;

public class SmartGate {
	private String gateId;
	private GateType type;
	private boolean active;
	int faultCount = 1;
	private Station station;

	public SmartGate(String gateId, GateType type) {
		this.gateId = gateId;
		this.type = type;
		this.active = true;
	}

	public SmartGate(String gateId, GateType type, Station station) {
		super();
		this.gateId = gateId;
		this.type = type;
		this.station = station;
		// BUG FIX: thieu dong nay khien cong tao bang constructor nay
		// luon co active = false (mac dinh cua boolean) -> isActive() = false
		// -> findActiveGateByType() khong bao gio tim thay cong nay
		this.active = true;
	}

	public Station getStation() {
		return station;
	}

	public void setStation(Station station) {
		this.station = station;
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
		Ticket ticket = TicketManager.getInstance().findById(ticketId);
		if (!validateTicket(ticket)) {
			closeGate();
			return;
		}
		if (type == GateType.IN) {
			ticket.checkIn();
			if (station != null) {
				station.incrementCheckIn();
				HeatmapService.getInstance().analyzeRealtime(station);
				MetroEventBus.getInstance().publish(MetroEventBus.Event.CHECKIN_UPDATED, station);
			}
		} else {
			ticket.checkOut();
            // Giảm số hành khách khi ra khỏi ga
            if (station != null) {
                station.decrementCheckIn();
                HeatmapService.getInstance().analyzeRealtime(station);
                MetroEventBus.getInstance().publish(MetroEventBus.Event.CHECKIN_UPDATED, station);
            }
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
