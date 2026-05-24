package Metro;

import java.util.ArrayList;
import java.util.List;

public class StationStaff {
	private String staffId;
    private String name;
    private Station station;
    private boolean onDuty;
    private List<HeatmapAlert> observerState;
    public StationStaff(String staffId,String name,Station station) {
        this.staffId = staffId;
        this.name = name;
        this.station = station;
        this.onDuty = true;
        this.observerState = new ArrayList<>();
    }
	@Override
	public void update(HeatmapAlert alert) {
		// TODO Auto-generated method stub
		if (onDuty && alert != null) {
            receiveAlert(alert);
        }
		
	}
 // Kiểm tra vé thủ công
    public void checkStationTicket(Ticket ticket) {
        if(ticket == null) {
            System.out.println("Ticket not found!");
            return;
        }
        if(ticket.getState().isValid()) {
            System.out.println("Ticket valid!");
        } 
        else {System.out.println("Ticket invalid!");
        }
    }
    // Báo lỗi SmartGate
    public void reportFault(SmartGate gate) {
        gate.disableGate();
        FaultLog log =
                new FaultLog("F001",gate.getGateId(),"Gate hardware error");
        log.saveLog();
        System.out.println("Fault reported by station staff!");
    }

    // Xác nhận hoàn vé
    public void confirmRefund(Ticket ticket) {
        if(ticket == null) {
            System.out.println("Ticket not found!");
            return;
        }
        if(ticket.getState().canRefund()) {
            ticket.refund();
            System.out.println("Refund confirmed!");
        } else {
            System.out.println("Ticket cannot refund!");
        }
    }
}
