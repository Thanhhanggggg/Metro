package main;

import Metro.*;
import view.LoginView;

import javax.swing.*;
import java.time.LocalDate;

/**
 * ============================================================
 *  Main – Entry point duy nhất của toàn hệ thống Metro
 * ============================================================
 *
 *  Luồng chạy:
 *    1. seedMetroData()   – Khởi tạo tuyến, ga, hành khách, vé,
 *                           dữ liệu HeatMap, VerifyService.
 *    2. LoginView.show()  – Mở màn hình chọn vai trò (Passenger /
 *                           Staff / SmartGate / Admin).
 *
 *  Dữ liệu mẫu được nạp vào các Singleton (TicketManager,
 *  HeatmapService, VerifyService, FareConfig) nên tất cả các
 *  module đều dùng chung mà không cần truyền tham số.
 * ============================================================
 */
public class Main {

	public static final java.util.List<MetroLine> METRO_LINES = new java.util.ArrayList<>();
    public static final Admin ADMIN = new Admin("A001", "Tran Van Lam", "12345678");
    public static void main(String[] args) {
        // 1. Khởi tạo dữ liệu mẫu cho toàn hệ thống
        seedMetroData();

        // 2. Khởi chạy giao diện trên EDT
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignored) {}

            new LoginView().show();
        });
    }

    // =========================================================
    //  Seed data – chạy một lần trước khi mở UI
    // =========================================================
    private static void seedMetroData() {

        // ── 1. Tuyen & Ga ──────────────────────────────────────
        MetroLine line1 = new MetroLine("L1", "Ben Thanh - Suoi Tien");
        Station s1 = new Station("S01", "Ben Thanh",  line1, 500);
        Station s2 = new Station("S02", "Nha Hat TP", line1, 400);
        Station s3 = new Station("S03", "Ba Son",     line1, 350);
        Station s4 = new Station("S04", "Van Thanh",  line1, 300);
        Station s5 = new Station("S05", "Suoi Tien",  line1, 600);
        line1.addStation(s1); line1.addStation(s2); line1.addStation(s3);
        line1.addStation(s4); line1.addStation(s5);
 
        MetroLine line2 = new MetroLine("L2", "Ben Thanh - Tham Luong");
        Station s6 = new Station("S11", "Ben Thanh",    line2, 500);
        Station s7 = new Station("S12", "Pham Van Hai", line2, 300);
        Station s8 = new Station("S13", "Tham Luong",   line2, 400);
        line2.addStation(s6); line2.addStation(s7); line2.addStation(s8);
        // Thêm vào list dùng chung
        METRO_LINES.add(line1);
        METRO_LINES.add(line2);

        // Đăng ký vào admin
        ADMIN.registerLine(line1);
        ADMIN.registerLine(line2);
 
        // ── 2. Hanh khach ──────────────────────────────────────
        Passenger p1 = new Passenger("P001", "Nguyen Van A", PassengerType.NORMAL,  "ID001", 500000);
        Passenger p2 = new Passenger("P002", "Tran Thi B",   PassengerType.STUDENT, "ID002", 200000);
        Passenger p3 = new Passenger("P003", "Le Van C",     PassengerType.SENIOR,  "ID003", 300000);
        Passenger p4 = new Passenger("P004", "Pham Thi D",   PassengerType.SENIOR,  "ID004", 100000);
        Passenger p5 = new Passenger("P005", "Hoang Van E",  PassengerType.NORMAL,  "ID005", 150000);
 
        TicketManager tm  = TicketManager.getInstance();
        FareConfig    cfg = FareConfig.getInstance();
 
        // ── 3. Cau hinh gia GOC ────────────────────────────────
        cfg.setBaseFare(7000);
        cfg.setFarePerStop(1500);
        cfg.setFixedPriceDaily(30000);
        cfg.setFixedPriceMonthly(300000);
        // Era mac dinh da la "Gia goc (truoc cap nhat)" tu TicketManager constructor
 
        // ── 4. Phat hanh ve TRUOC cap nhat gia ────────────────
        tm.issueTicket(p1, TicketType.SINGLE,  3);
        tm.issueTicket(p1, TicketType.SINGLE,  5);
        tm.issueTicket(p2, TicketType.DAILY,   0);
        tm.issueTicket(p2, TicketType.DAILY,   0);
        tm.issueTicket(p3, TicketType.MONTHLY, 0);
 
        // Ve SmartGate (trang thai cu the) - van thuoc era gia goc
        Ticket t1 = new SingleTrip("T001", p1, 3);
        Ticket t2 = new SingleTrip("T002", p2, 2);
        Ticket t3 = new SingleTrip("T003", p3, 4);
        Ticket t4 = new SingleTrip("T004", p4, 1);
        Ticket t5 = new SingleTrip("T005", p5, 5);
        t3.setState(new UsedState());
        t4.setState(new ExpiredState());
        t5.setState(new RefundedState());
        tm.saveTicket(t1); tm.saveTicket(t2); tm.saveTicket(t3);
        tm.saveTicket(t4); tm.saveTicket(t5);
 
 //        // ── 5. CAP NHAT GIA LAN 1 ─────────────────────────────
//        // Buoc 1: Set gia moi vao FareConfig
//        cfg.setBaseFare(2000);
//        cfg.setFarePerStop(5000);
//        cfg.setFixedPriceDaily(60000);
//        cfg.setFixedPriceMonthly(500000);
//        // Buoc 2: Danh dau era moi SAU khi da set gia xong
//        tm.markPriceEra(String.format(
//            "Sau cap nhat lan 1 (base=%,.0f | perStop=%,.0f | ngay=%,.0f | thang=%,.0f)",
//            cfg.getBaseFare(), cfg.getFarePerStop(),
//            cfg.getFixedPriceDaily(), cfg.getFixedPriceMonthly()));
 
        // ── 6. Phat hanh ve SAU cap nhat gia ──────────────────
        tm.issueTicket(p1, TicketType.SINGLE,  3);
        tm.issueTicket(p4, TicketType.DAILY,   0);
        tm.issueTicket(p5, TicketType.MONTHLY, 0);
 
        // ── 7. HeatMap ─────────────────────────────────────────
        for (int i = 0; i <  48; i++) s1.incrementCheckIn();
        for (int i = 0; i < 290; i++) s3.incrementCheckIn();
        for (int i = 0; i < 295; i++) s4.incrementCheckIn();
        HeatmapService hms = HeatmapService.getInstance();
        hms.analyzeRealtime(s1);
        hms.analyzeRealtime(s3);
        hms.analyzeRealtime(s4);
 
        // ── 8. VerifyService ───────────────────────────────────
        VerifyService vs = VerifyService.getInstance();
        vs.registerCitizen(new CitizenInfo("ID002", "Tran Thi B",
            LocalDate.of(2004, 1, 1),  true,  false));
        vs.registerCitizen(new CitizenInfo("ID003", "Le Van C",
            LocalDate.of(1958, 5, 10), false, false));
     // ── 9. Khởi tạo cổng SmartGate ────────────────────────
        controller.SmartGateController gateCtrl = new controller.SmartGateController();
        gateCtrl.init();

        // ── 10. In tóm tắt ra console ───────────────────────────
        System.out.println("╔══════════════════════════════════════════════╗");
        System.out.println("║   METRO SYSTEM – Khởi tạo dữ liệu mẫu       ║");
        System.out.println("╠══════════════════════════════════════════════╣");
        System.out.printf ("║  Tuyến metro  : %d tuyến%n", 2);
        System.out.printf ("║  Ga           : %d ga%n",    8);
        System.out.printf ("║  Hành khách   : %d người%n", 5);
        System.out.printf ("║  Vé (TM)      : %d vé%n",    tm.getAllTickets().size());
        System.out.printf ("║  HeatMap alert: %d cảnh báo%n", hms.getAlertHistory().size());
        System.out.println("║  Giá cơ bản   : 7.000 VND / 1.500 VND/trạm  ║");
        System.out.println("╚══════════════════════════════════════════════╝");
        System.out.println();
        System.out.println("  Vé SmartGate sẵn sàng test:");
        System.out.println("  T001/T002 → ActiveState   (check-in được)");
        System.out.println("  T003      → UsedState     (check-out được)");
        System.out.println("  T004      → ExpiredState  (không dùng được)");
        System.out.println("  T005      → RefundedState (không dùng được)");
        System.out.println();
     
    }
}
