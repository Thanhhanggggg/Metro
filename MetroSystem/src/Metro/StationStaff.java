package Metro;
import java.util.ArrayList;
import java.util.List;

public class StationStaff extends Employee implements Observer {
    private Station station;
    private boolean onDuty;
    private List<HeatmapAlert> observerState;

    public StationStaff(String employeeId, String name, String password, String staffId) {
        super(employeeId, name, password);
        this.onDuty = true;
        this.observerState = new ArrayList<>();
    }

    @Override
    public void update() {
        if (onDuty) {
            HeatmapAlert alert = HeatmapService.getInstance().getLatestAlert();
            if (alert != null) {
                receiveAlert(alert);
            }
        }
    }

    public void receiveAlert(HeatmapAlert alert) {
        // Luu canh bao vao danh sach cua nhan vien
        observerState.add(alert);
        System.out.println("Nhan vien: " + name + " - Nhan canh bao: " + alert.toString());
    }

    // Kiem tra ve thu cong
    public void checkStationTicket(Ticket ticket) {
        if (ticket == null) {
            System.out.println("Khong tim thay ve!");
            return;
        }
        if (ticket.getState().isValid()) {
            System.out.println("Ve hop le!");
        } else {
            System.out.println("Ve khong hop le!");
        }
    }

    // Bao loi SmartGate
    public void reportFault(SmartGate gate) {
        gate.disableGate();
        FaultLog log = new FaultLog("F001", gate.getGateId(), "Loi phan cung cong soat ve");
        log.saveLog();
        System.out.println("Da bao loi cong soat ve!");
    }

    // Xac nhan hoan ve
    public void confirmRefund(Ticket ticket) {
        if (ticket == null) {
            System.out.println("Khong tim thay ve!");
            return;
        }
        if (ticket.getState().canRefund()) {
            ticket.refund();
            System.out.println("Xac nhan hoan ve thanh cong!");
        } else {
            System.out.println("Ve khong du dieu kien hoan!");
        }
    }
}
