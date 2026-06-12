package controller;

import Metro.*;
import java.util.*;

/**
 * Controller cho module SmartGate (UC07, UC08, UC09, UC13)
 * Xử lý đầy đủ các trường hợp nhập mã vé trùng / sai trạng thái.
 */
public class SmartGateController implements IController {

    private List<SmartGate> gates    = new ArrayList<>();
    private List<String>    scanLog  = new ArrayList<>();

    public SmartGateController() {
        gates.add(new SmartGate("G001", GateType.IN));
        gates.add(new SmartGate("G002", GateType.IN));
        gates.add(new SmartGate("G003", GateType.OUT));
        gates.add(new SmartGate("G004", GateType.OUT));
    }

    // ═══════════════════════════════════════════════════════
    //  UC07 – Check-in
    // ═══════════════════════════════════════════════════════
    public String checkIn(String ticketId) {
        Ticket ticket = TicketManager.getInstance().findById(ticketId);

        // Vé không tồn tại
        if (ticket == null)
            return "FAIL:Không tìm thấy vé: " + ticketId;

        TicketState state = ticket.getState();

        // ── Xử lý nhập 2 lần / sai trạng thái ──────────────
        if (state instanceof UsedState)
            return "WARN:Vé " + ticketId + " đã được check-in rồi!\n"
                 + "→ Vé đang chờ check-out, không thể check-in lại.";

        if (state instanceof ExpiredState)
            return "FAIL:Vé " + ticketId + " đã hết hạn, không thể check-in.";

        if (state instanceof RefundedState)
            return "FAIL:Vé " + ticketId + " đã được hoàn trả, không thể sử dụng.";

        // ── ActiveState → check-in hợp lệ ───────────────────
        if (!ticket.isValid())
            return "FAIL:Vé không hợp lệ (trạng thái: " + state.getClass().getSimpleName() + ")";

        ticket.checkIn();
        String msg = "OK:Check-in thành công! Vé " + ticketId
                   + " → " + ticket.getState().getClass().getSimpleName();
        scanLog.add("[CHECK-IN] " + msg.replace("OK:", ""));
        return msg;
    }

    // ═══════════════════════════════════════════════════════
    //  UC08 – Check-out
    // ═══════════════════════════════════════════════════════
    public String checkOut(String ticketId) {
        Ticket ticket = TicketManager.getInstance().findById(ticketId);

        // Vé không tồn tại
        if (ticket == null)
            return "FAIL:Không tìm thấy vé: " + ticketId;

        TicketState state = ticket.getState();

        // ── Xử lý nhập 2 lần / sai trạng thái ──────────────
        if (state instanceof ActiveState)
            return "WARN:Vé " + ticketId + " chưa check-in!\n"
                 + "→ Hành khách phải check-in trước khi check-out.";

        if (state instanceof ExpiredState)
            return "FAIL:Vé " + ticketId + " đã hết hạn (đã check-out trước đó).\n"
                 + "→ Không thể check-out lại.";

        if (state instanceof RefundedState)
            return "FAIL:Vé " + ticketId + " đã được hoàn trả, không thể sử dụng.";

        // ── UsedState → check-out hợp lệ ────────────────────
        if (!(state instanceof UsedState))
            return "FAIL:Trạng thái không hợp lệ: " + state.getClass().getSimpleName();

        ticket.checkOut();
        String msg = "OK:Check-out thành công! Vé " + ticketId
                   + " → " + ticket.getState().getClass().getSimpleName();
        scanLog.add("[CHECK-OUT] " + msg.replace("OK:", ""));
        return msg;
    }

    // ═══════════════════════════════════════════════════════
    //  UC09 – Xác thực hành trình qua cổng
    // ═══════════════════════════════════════════════════════
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


    // ═══════════════════════════════════════════════════════
    //  Helpers
    // ═══════════════════════════════════════════════════════
    public String getTicketInfo(String ticketId) {
        Ticket ticket = TicketManager.getInstance().findById(ticketId);
        if (ticket == null) return "";
        return "Vé ID      : " + ticket.getTicketId()                          + "\n"
             + "Loại       : " + ticket.getType()                              + "\n"
             + "Giá        : " + ticket.getPrice()                 + " VND"   + "\n"
             + "Hành khách : " + ticket.getPassenger().getName()               + "\n"
             + "Trạng thái : " + ticket.getState().getClass().getSimpleName()  + "\n"
             + "Hợp lệ     : " + (ticket.isValid() ? "Có" : "Không");
    }

    public List<SmartGate> getGates()   { return gates; }
    public List<String>    getScanLog() { return scanLog; }
    public String[] getGateIds() {
        return gates.stream().map(SmartGate::getGateId).toArray(String[]::new);
    }

    private SmartGate findGateById(String id) {
        return gates.stream().filter(g -> g.getGateId().equals(id)).findFirst().orElse(null);
    }

	@Override
	public void handleAction(String action, Object... params) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean validate(Object input) {
		// TODO Auto-generated method stub
		return false;
	}
}