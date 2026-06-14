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
    private List<MetroLine> metroLines = new ArrayList<>();
    /** Kết quả của lần gọi handleAction() gần nhất – dùng để UI lấy về. */
    private String lastResult = "";

    // ─────────────────────────────────────────────────────────────────────────
    public SmartGateController() {
    	 MetroEventBus bus = MetroEventBus.getInstance();
    	 bus.subscribe(Event.GATE_FAULT,   payload -> onGateFault((String) payload));
    	 bus.subscribe(Event.GATE_ENABLED, payload -> onGateEnabled((String) payload));
    }
    public SmartGateController(List<MetroLine> sharedLines) {
        this();
        this.metroLines = sharedLines;
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

        // FIX: truoc day cac cong duoc tao ma khong gan Station nao ca
        // (getStation() luon tra ve null) -> checkIn()/checkOut() khong
        // bao gio tang/giam duoc checkInCount cua ga, va Admin khong
        // nhan duoc CHECKIN_UPDATED de refresh cot "Hien tai".
        // Gan tat ca cong vao 1 ga mac dinh (ga dau tuyen dau tien)
        // de mo phong 1 cum cong ra/vao cua cung 1 nha ga.
        Station defaultStation = resolveDefaultStation();
        if (defaultStation != null) {
            assignStation("G001", defaultStation);
            assignStation("G002", defaultStation);
            assignStation("G003", defaultStation);
            assignStation("G004", defaultStation);
        }
    }

    /**
     * Lay ga mac dinh de gan cho cum cong SmartGate (G001-G004).
     * Uu tien danh sach tuyen duoc truyen vao controller (metroLines),
     * neu rong thi fallback sang danh sach tuyen toan cuc cua he thong
     * (main.Main.METRO_LINES) - day la danh sach AdminView dang hien thi.
     */
    private Station resolveDefaultStation() {
        List<MetroLine> lines = (metroLines != null && !metroLines.isEmpty())
                ? metroLines
                : main.Main.METRO_LINES;
        if (lines == null || lines.isEmpty()) return null;
        List<Station> stations = lines.get(0).getStations();
        if (stations.isEmpty()) return null;
        return stations.get(0);
    }
    public String addGate(String gateId, GateType type) {
        if (GateRegistry.getInstance().exists(gateId))
            return "FAIL:Cổng " + gateId + " đã tồn tại!";
        GateRegistry.getInstance().register(new SmartGate(gateId, type));
        return "OK:Đã thêm cổng " + gateId;
    }
    public String addGate(String gateId, GateType type, Station station) {
        if (GateRegistry.getInstance().exists(gateId))
            return "FAIL:Cổng " + gateId + " đã tồn tại!";
        GateRegistry.getInstance().register(new SmartGate(gateId, type, station));
        return "OK:Đã thêm cổng " + gateId + " tại ga " + station.getStationName();
    }
    /** Gán station cho cổng đã tồn tại */
    public String assignStation(String gateId, Station station) {
        SmartGate gate = findGateById(gateId);
        if (gate == null) return "FAIL:Không tìm thấy cổng: " + gateId;
        gate.setStation(station);
        return "OK:Đã gán ga " + station.getStationName() + " cho cổng " + gateId;
    }
 
    /** Tìm Station theo tên trong danh sách tuyến đang quản lý */
    public Station findStationByName(String name) {
        for (MetroLine line : metroLines)
            for (Station s : line.getStations())
                if (s.getStationName().equalsIgnoreCase(name)) return s;
        return null;
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

        // Cập nhật số hành khách tại ga → Admin thấy cột "Hiện tại" tăng
        SmartGate inGate = findActiveGateByType(GateType.IN);
        if (inGate != null && inGate.getStation() != null) {
            Station station = inGate.getStation();
            station.incrementCheckIn();
            HeatmapService.getInstance().analyzeRealtime(station);
            MetroEventBus.getInstance().publish(Event.CHECKIN_UPDATED, station);
        }

        String msg = "OK:Check-in thành công! Vé " + ticketId
                   + " → " + ticket.getState().getClass().getSimpleName();
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

     // Giảm ga xuất phát
        SmartGate inGate = findActiveGateByType(GateType.IN);
        if (inGate != null && inGate.getStation() != null) {
            inGate.getStation().decrementCheckIn();
            HeatmapService.getInstance().analyzeRealtime(inGate.getStation());
            MetroEventBus.getInstance().publish(Event.CHECKIN_UPDATED, inGate.getStation());
        }
        // Tăng ga đích
        if (ticket instanceof SingleTrip st && st.getDestination() != null) {
            st.getDestination().incrementCheckIn();
            HeatmapService.getInstance().analyzeRealtime(st.getDestination());
            MetroEventBus.getInstance().publish(Event.CHECKIN_UPDATED, st.getDestination());
        }
        String msg = "OK:Check-out thành công! Vé " + ticketId
                   + " → " + ticket.getState().getClass().getSimpleName();
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
    /** Tìm cổng đầu tiên còn active theo loại IN/OUT (fallback khi không biết gateId cụ thể) */
    private SmartGate findActiveGateByType(GateType type) {
        return GateRegistry.getInstance().getAll().stream()
            .filter(g -> g.getType() == type && g.isActive() && g.getStation() != null)
            .findFirst().orElse(null);
    }
}
