package Controller;

import java.util.List;
import Metro.*;
import view.*;


public class StaffController implements IController {

    private final StationStaff staff;
    private final StaffView    view;

    public StaffController(StationStaff staff, StaffView view) {
        this.staff = staff;
        this.view  = view;

        // Đăng ký View (Observer) vào HeatmapService (Subject)
        HeatmapService.getInstance().attach(view);
    }

    //  IController
    @Override
    public void handleAction(String action, Object... params) {
        switch (action) {
            case "CHECK_TICKET" -> handleCheckTicket((String) params[0]);
            case "REFUND"       -> handleRefund((String) params[0]);
            case "FAULT"        -> handleFault((String) params[0], (String) params[1]);
            default             -> view.showError("Hành động không hợp lệ: " + action);
        }
    }

    @Override
    public boolean validate(Object input) {
        if (input == null) return false;
        if (input instanceof String s) return !s.isBlank();
        return true;
    }


    // UC11 — Kiểm tra trạng thái vé 
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

    // UC12 — Hoàn vé 
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

        // Thực hiện hoàn vé 
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

    // UC13 — Ghi nhận sự cố thiết bị 
    private void handleFault(String gateId, String description) {
        if (!validate(gateId)) {
            view.showError("Vui lòng nhập mã cổng!");
            return;
        }
        if (!validate(description)) {
            view.showError("Vui lòng nhập mô tả sự cố!");
            return;
        }

        // Tạo SmartGate tạm để gọi reportFault
        SmartGate gate = new SmartGate(gateId, GateType.IN);
        gate.reportFault(description);

        // Tìm vé bị ảnh hưởng
        List<Ticket> affected = TicketManager.getInstance().findAffectedTickets(gateId);
        System.out.println("So ve bi anh huong: " + affected.size());

        view.showFaultLogged(gateId, true);
    }
    
  
    //Hỗ trợ
    private String describeState(Ticket ticket) {
        TicketState state = ticket.getState();
        if (state instanceof ActiveState)   return "ACTIVE — Chưa sử dụng";
        if (state instanceof UsedState)     return "USED — Đang trong hành trình";
        if (state instanceof ExpiredState)  return "EXPIRED — Đã hoàn tất hành trình";
        if (state instanceof RefundedState) return "REFUNDED — Đã hoàn tiền";
        return "Không xác định";
    }
}
