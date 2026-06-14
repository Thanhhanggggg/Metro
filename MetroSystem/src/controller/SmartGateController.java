package controller;

import Metro.*;
import Metro.MetroEventBus.Event;

import java.util.*;


public class SmartGateController implements IController {

    // ── Hằng action ──────────────────────────────────────────────────────────
    public static final String ACTION_CHECK_IN       = "CHECK_IN";
    public static final String ACTION_CHECK_OUT      = "CHECK_OUT";
    public static final String ACTION_VALIDATE       = "VALIDATE";
    public static final String ACTION_GET_INFO       = "GET_INFO";
    public static final String ACTION_GET_SCAN_LOG   = "GET_SCAN_LOG";

    // ── State ─────────────────────────────────────────────────────────────────
    private final List<String>    scanLog = new ArrayList<>();

    /** Kết quả của lần gọi handleAction() gần nhất – dùng để UI lấy về. */
    private String lastResult = "";

    // ─────────────────────────────────────────────────────────────────────────
    public SmartGateController() {
    	 MetroEventBus bus = MetroEventBus.getInstance();
    	 bus.subscribe(Event.GATE_FAULT,   payload -> onGateFault((String) payload));
    	 bus.subscribe(Event.GATE_ENABLED, payload -> onGateEnabled((String) payload));
    }
    private void onGateFault(String gateId) {
        // GateRegistry already updated by StaffController; just log if needed.
        System.out.println("[SmartGateController] Fault reported on: " + gateId);
    }

    private void onGateEnabled(String gateId) {
        System.out.println("[SmartGateController] Gate re-enabled: " + gateId);
    }
    public void init() {
        addGate("G001", GateType.IN);
        addGate("G002", GateType.IN);
        addGate("G003", GateType.OUT);
        addGate("G004", GateType.OUT);
    }
    public String addGate(String gateId, GateType type) {
        if (GateRegistry.getInstance().exists(gateId))
            return "FAIL:Cổng " + gateId + " đã tồn tại!";
        GateRegistry.getInstance().register(new SmartGate(gateId, type));
        return "OK:Đã thêm cổng " + gateId;
    }

    public String removeGate(String gateId) {
        boolean removed = GateRegistry.getInstance().remove(gateId);
        return removed ? "OK:Đã xóa cổng " + gateId
                       : "FAIL:Không tìm thấy cổng " + gateId;
    }

	// ═══════════════════════════════════════════════════════════════════════
    //  IController – handleAction
    //  Dispatch mọi hành động qua một điểm duy nhất.
    //
    //  Cú pháp params:
    //    CHECK_IN   (ticketId)
    //    CHECK_OUT  (ticketId)
    //    VALIDATE   (gateId, ticketId)
    //    GET_INFO   (ticketId)
    //    GET_SCAN_LOG  (không có param)
    // ═══════════════════════════════════════════════════════════════════════
    @Override
    public void handleAction(String action, Object... params) {
        if (action == null) {
            lastResult = "FAIL:Action không được null.";
            return;
        }

        switch (action.toUpperCase()) {

            case ACTION_CHECK_IN -> {
                if (!hasParams(params, 1)) { lastResult = "FAIL:CHECK_IN cần tham số ticketId."; return; }
                lastResult = checkIn(params[0].toString());
            }

            case ACTION_CHECK_OUT -> {
                if (!hasParams(params, 1)) { lastResult = "FAIL:CHECK_OUT cần tham số ticketId."; return; }
                lastResult = checkOut(params[0].toString());
            }

            case ACTION_VALIDATE -> {
                if (!hasParams(params, 2)) { lastResult = "FAIL:VALIDATE cần tham số gateId, ticketId."; return; }
                lastResult = validateTicket(params[0].toString(), params[1].toString());
            }

            case ACTION_GET_INFO -> {
                if (!hasParams(params, 1)) { lastResult = "FAIL:GET_INFO cần tham số ticketId."; return; }
                lastResult = getTicketInfo(params[0].toString());
            }

            case ACTION_GET_SCAN_LOG -> {
                lastResult = String.join("\n", scanLog);
            }

            default -> lastResult = "FAIL:Action không hỗ trợ: " + action;
        }
    }

    // ═══════════════════════════════════════════════════════════════════════
    //  IController – validate
    //  Kiểm tra input hợp lệ trước khi thực thi action.
    //
    //  Nhận vào:
    //    - String ticketId          → kiểm tra vé tồn tại & đang active
    //    - String[] { gateId, ticketId } → kiểm tra cả cổng lẫn vé
    //    - SmartGate                → kiểm tra cổng tồn tại trong danh sách
    //    - Ticket                   → kiểm tra vé hợp lệ trực tiếp
    // ═══════════════════════════════════════════════════════════════════════
    @Override
    public boolean validate(Object input) {
        if (input == null) return false;

        // Kiểm tra Ticket object trực tiếp
        if (input instanceof Ticket ticket) {
            return ticket.isValid();
        }

        // Kiểm tra SmartGate object
        if (input instanceof SmartGate gate) {
            return GateRegistry.getInstance().exists(gate.getGateId());
        }

        // Kiểm tra ticketId (String)
        if (input instanceof String ticketId) {
            Ticket ticket = TicketManager.getInstance().findById(ticketId);
            return ticket != null && ticket.isValid();
        }

        // Kiểm tra String[] { gateId, ticketId }
        if (input instanceof String[] arr && arr.length >= 2) {
            String gateId    = arr[0];
            String ticketId  = arr[1];
            SmartGate gate   = findGateById(gateId);
            Ticket    ticket = TicketManager.getInstance().findById(ticketId);
            return gate != null && ticket != null && ticket.isValid();
        }

        return false;
    }

    // ═══════════════════════════════════════════════════════════════════════
    //  UC07 – Check-in
    // ═══════════════════════════════════════════════════════════════════════
    public String checkIn(String ticketId) {
        Ticket ticket = TicketManager.getInstance().findById(ticketId);

        if (ticket == null)
            return "FAIL:Không tìm thấy vé: " + ticketId;

        TicketState state = ticket.getState();

        if (state instanceof UsedState)
            return "WARN:Vé " + ticketId + " đã được check-in rồi!\n"
                 + "→ Vé đang chờ check-out, không thể check-in lại.";

        if (state instanceof ExpiredState)
            return "FAIL:Vé " + ticketId + " đã hết hạn, không thể check-in.";

        if (state instanceof RefundedState)
            return "FAIL:Vé " + ticketId + " đã được hoàn trả, không thể sử dụng.";

        if (!ticket.isValid())
            return "FAIL:Vé không hợp lệ (trạng thái: " + state.getClass().getSimpleName() + ")";

        ticket.checkIn();
        String msg = "OK:Check-in thành công! Vé " + ticketId
                   + " → " + ticket.getState().getClass().getSimpleName()
                   + "\n" + buildTicketInfo(ticket);
        scanLog.add("[CHECK-IN] " + msg.replace("OK:", ""));
        return msg;
    }

    // ═══════════════════════════════════════════════════════════════════════
    //  UC08 – Check-out
    // ═══════════════════════════════════════════════════════════════════════
    public String checkOut(String ticketId) {
        Ticket ticket = TicketManager.getInstance().findById(ticketId);

        if (ticket == null)
            return "FAIL:Không tìm thấy vé: " + ticketId;

        TicketState state = ticket.getState();

        if (state instanceof ActiveState)
            return "WARN:Vé " + ticketId + " chưa check-in!\n"
                 + "→ Hành khách phải check-in trước khi check-out.";

        if (state instanceof ExpiredState)
            return "FAIL:Vé " + ticketId + " đã hết hạn (đã check-out trước đó).\n"
                 + "→ Không thể check-out lại.";

        if (state instanceof RefundedState)
            return "FAIL:Vé " + ticketId + " đã được hoàn trả, không thể sử dụng.";

        if (!(state instanceof UsedState))
            return "FAIL:Trạng thái không hợp lệ: " + state.getClass().getSimpleName();

        ticket.checkOut();
        String msg = "OK:Check-out thành công! Vé " + ticketId
                   + " → " + ticket.getState().getClass().getSimpleName()
                   + "\n" + buildTicketInfo(ticket);
        scanLog.add("[CHECK-OUT] " + msg.replace("OK:", ""));
        return msg;
    }

    // ═══════════════════════════════════════════════════════════════════════
    //  UC09 – Xác thực hành trình qua cổng
    // ═══════════════════════════════════════════════════════════════════════
    public String validateTicket(String gateId, String ticketId) {
        SmartGate gate = findGateById(gateId);
        if (gate == null) return "FAIL:Không tìm thấy cổng: " + gateId;

        Ticket ticket = TicketManager.getInstance().findById(ticketId);
        if (ticket == null) return "FAIL:Không tìm thấy vé: " + ticketId;

        boolean valid = gate.validateTicket(ticket);
        String msg = valid
                ? "OK:Vé hợp lệ! Cổng " + gateId + " mở cửa."
                : "FAIL:Vé không hợp lệ! Cổng " + gateId + " đóng. ("
                  + ticket.getState().getDescription() + ")";

        scanLog.add("[VALIDATE] Cổng=" + gateId + " Vé=" + ticketId
                  + " → " + (valid ? "HỢP LỆ" : "KHÔNG HỢP LỆ"));
        return msg;
    }

    // ═══════════════════════════════════════════════════════════════════════
    //  Helpers
    // ═══════════════════════════════════════════════════════════════════════
    public String getTicketInfo(String ticketId) {
        Ticket ticket = TicketManager.getInstance().findById(ticketId);
        if (ticket == null) return "FAIL:Không tìm thấy vé: " + ticketId;
        return "Vé ID      : " + ticket.getTicketId()                          + "\n"
             + "Loại       : " + ticket.getType()                              + "\n"
             + "Giá        : " + ticket.getPrice()                 + " VND"   + "\n"
             + "Hành khách : " + ticket.getPassenger().getName()               + "\n"
             + "Trạng thái : " + ticket.getState().getClass().getSimpleName()  + "\n"
             + "Hợp lệ     : " + (ticket.isValid() ? "Có" : "Không");
    }

    /** Kết quả của handleAction() gần nhất. */
    public String getLastResult()        { return lastResult; }

    public Collection<SmartGate> getGates() {
        return GateRegistry.getInstance().getAll();
    }
    public String[] getGateIds() {
        return GateRegistry.getInstance().getAll()
            .stream().map(SmartGate::getGateId).toArray(String[]::new);
    }
    private SmartGate findGateById(String id) {
    	return GateRegistry.getInstance().findById(id);
    }

    /** Kiểm tra params đủ số lượng tối thiểu. */
    private boolean hasParams(Object[] params, int min) {
        return params != null && params.length >= min && params[0] != null;
    }
    public void clearLog() {
        scanLog.clear();
        
    }
    private String buildTicketInfo(Ticket ticket) {
        StringBuilder sb = new StringBuilder();
        sb.append("=================================\n");
        sb.append("Loại vé    : ").append(ticket.getType()).append("\n");
        sb.append("Trạng thái : ").append(ticket.getState().getDescription()).append("\n");

        if (ticket instanceof DayPass dp) {
            boolean stillToday = java.time.LocalDate.now().equals(dp.getValidDate());
            sb.append("Hiệu lực   : Hôm nay (").append(java.time.LocalDate.now()).append(")")
              .append(stillToday ? " Còn hiệu lực" : " Hết hạn");

        } else if (ticket instanceof MonthlyPass mp) {
            boolean notExpired = java.time.LocalDate.now().isBefore(mp.getValidUntil());
            sb.append("Hết hạn    : ").append(mp.getValidUntil()).append("\n");
            sb.append("Còn hiệu lực: ").append(notExpired ? " Có" : " Không");

        } else if (ticket instanceof SingleTrip st) {
            String trangThai = (ticket.getState() instanceof ExpiredState) ? " Đã sử dụng" : " Còn hiệu lực";
            sb.append("Còn hiệu lực: ").append(trangThai);
        }
        return sb.toString();
    }
}
