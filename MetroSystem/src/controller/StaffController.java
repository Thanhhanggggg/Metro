package controller;

import java.util.List;
import Metro.*;
import Metro.StationStaff;
import view.*;

public class StaffController implements IController {

    private StationStaff staff;
    private StaffView    view;

    public StaffController(StationStaff staff, StaffView view) {
        this.staff = staff;
        this.view  = view;
        HeatmapService.getInstance().attach(view);
    }
 
    public StaffController() {
        this.staff = new StationStaff("ST_DEFAULT", "Staff", "1234", "ST_DEFAULT");
    }
 
    public void setView(StaffView view) {
        this.view = view;
        HeatmapService.getInstance().attach(view);
        replayAlertHistory(); 
        simulateRealtime();   
    }

    private void replayAlertHistory() {
        List<HeatmapAlert> history = HeatmapService.getInstance().getAlertHistory();
        System.out.println("[DEBUG] replay size = " + history.size());
        if (history == null || history.isEmpty()) return;

        // Doi 500ms de dam bao view da duoc add vao JFrame
        new javax.swing.Timer(500, e -> {
            for (HeatmapAlert alert : history) {
                view.showAlert(alert);
            }
            ((javax.swing.Timer) e.getSource()).stop();
        }).start();
    }
    
    private void simulateRealtime() {
        new Thread(() -> {
            try {
                Thread.sleep(5000);
                for (Metro.MetroLine line : main.Main.METRO_LINES) {
                    for (Metro.Station s : line.getStations()) {
                        if (s.getCheckInCount() > 0) {
                            HeatmapService.getInstance().analyzeRealtime(s);
                            Thread.sleep(800);
                        }
                    }
                }
            } catch (InterruptedException ignored) {}
        }).start();
    }
    @Override
    public void handleAction(String action, Object... params) {
        switch (action) {
            case "CHECK_TICKET" -> handleCheckTicket((String) params[0]);
            case "REFUND"       -> handleRefund((String) params[0]);
            case "FAULT"        -> handleFault((String) params[0], (String) params[1]);
            case "ENABLE_GATE"   -> handleEnableGate((String) params[0]); // 
            default             -> view.showError("Hành động không hợp lệ: " + action);
        }
    }

    @Override
    public boolean validate(Object input) {
        if (input == null) return false;
        if (input instanceof String s) return !s.isBlank();
        return true;
    }


    // UC11 — Kiem tra trang thai ve
    private void handleCheckTicket(String ticketId) {
        if (!validate(ticketId)) {
            view.showError("Vui lòng nhập mã vé!");
            return;
        }

        Ticket ticket = TicketManager.getInstance().findById(ticketId);

        if (ticket == null) {
            view.showCheckResult(ticketId, false, "Vé không tồn tại trong hệ thống");
            return;
        }

        boolean valid     = ticket.getState().isValid();
        String  stateDesc = describeState(ticket);
        view.showCheckResult(ticketId, valid, stateDesc);
    }

    // UC12 — Hoan ve
    private void handleRefund(String ticketId) {
        if (!validate(ticketId)) {
            view.showError("Vui lòng nhập mã vé!");
            return;
        }

        Ticket ticket = TicketManager.getInstance().findById(ticketId);

        if (ticket == null) {
            view.showRefundResult(ticketId, false, 0, "Vé không tồn tại trong hệ thống");
            return;
        }

        if (!ticket.canRefund()) {
            view.showRefundResult(ticketId, false, 0,
                "Vé ở trạng thái " + describeState(ticket) + " — không thể hoàn");
            return;
        }

        double amount = ticket.refund();

        if (amount >= 0 && ticket.getState() instanceof RefundedState) {
            TicketManager.getInstance().confirmRefund(ticket);
            view.showRefundResult(ticketId, true, amount,
                "Hoàn vé thành công. Vé đã bị vô hiệu hóa.");
        } else {
            view.showRefundResult(ticketId, false, 0,
                "Không đủ điều kiện hoàn theo chính sách hiện tại");
        }
    }

    //UC13 — Ghi nhan su co thiet bi
    private void handleFault(String gateId, String description) {
        if (!validate(gateId)) {
            view.showError("Vui lòng nhập mã cổng!");
            return;
        }
        if (!validate(description)) {
            view.showError("Vui lòng nhập mô tả sự cố!");
            return;
        }

        // lấy cổng thật từ Registry
        SmartGate gate = GateRegistry.getInstance().findById(gateId);
        if (gate == null) {
            view.showFaultLogged(gateId, false, "Không tìm thấy cổng trong hệ thống");
            return;
        }
        gate.reportFault(description);
        FaultLog log = new FaultLog("F-" + gateId, gateId, description);
        log.saveLog();

        List<Ticket> affected = TicketManager.getInstance().findAffectedTickets(gateId);
        System.out.println("So ve bi anh huong: " + affected.size());

        view.showFaultLogged(gateId, true, description);
    }
 // UC13b — Kích hoạt lại cổng sau khi sửa xong
    private void handleEnableGate(String gateId) {
        if (!validate(gateId)) {
            view.showError("Vui lòng nhập mã cổng!");
            return;
        }
        SmartGate gate = GateRegistry.getInstance().findById(gateId);
        if (gate == null) {
            view.showGateEnabled(gateId, false);
            return;
        }
        if (gate.isActive()) {
            view.showError("Cổng [" + gateId + "] đang hoạt động bình thường!");
            return;
        }
        gate.enableGate();
        view.showGateEnabled(gateId, true);
    }

    //Ham ho tro
    private String describeState(Ticket ticket) {
        TicketState state = ticket.getState();
        if (state instanceof ActiveState)   return "ACTIVE — Chưa sử dụng";
        if (state instanceof UsedState)     return "USED — Đang trong hành trình";
        if (state instanceof ExpiredState)  return "EXPIRED — Đã hoàn tất hành trình";
        if (state instanceof RefundedState) return "REFUNDED — Đã hoàn tiền";
        return "Không xác định";
    }
}
