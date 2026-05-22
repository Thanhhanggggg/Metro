package Metro;

import java.time.LocalDateTime;

public class FaultLog {
	private String faultId;
    private String gateId;
    private String description;
    private LocalDateTime reportTime;

    public FaultLog(String faultId, String gateId, String description) {
        this.faultId = faultId;
        this.gateId = gateId;
        this.description = description;
        this.reportTime = LocalDateTime.now();
    }

    public void saveLog() {
        System.out.println("Fault saved:");
        System.out.println("Fault ID: " + faultId);
        System.out.println("Gate ID: " + gateId);
        System.out.println("Description: " + description);
        System.out.println("Report Time: " + reportTime);
    }

    public void updateStatus() {
        System.out.println("Fault status updated!");
    }

    public void findAffectedTickets() {
        System.out.println("Finding affected tickets...");
    }
    
}
