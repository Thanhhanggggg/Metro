package main;

import Metro.*;
import view.LoginView;

import javax.swing.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

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

    public static Passenger passenger;
    public static List<MetroLine> lines = new ArrayList<>();
    public static List<Station> stations = new ArrayList<>();

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

        // ── 1. Tuyến & Ga ─────────────────────────────────────
        MetroLine line1 = new MetroLine("L1", "Ben Thanh - Suoi Tien");
        Station s1 = new Station("S01", "Ben Thanh",   line1, 500);
        Station s2 = new Station("S02", "Nha Hat TP",  line1, 400);
        Station s3 = new Station("S03", "Ba Son",      line1, 350);
        Station s4 = new Station("S04", "Van Thanh",   line1, 300);
        Station s5 = new Station("S05", "Suoi Tien",   line1, 600);
        line1.addStation(s1);
        line1.addStation(s2);
        line1.addStation(s3);
        line1.addStation(s4);
        line1.addStation(s5);

        MetroLine line2 = new MetroLine("L2", "Ben Thanh - Tham Luong");
        Station s6 = new Station("S11", "Ben Thanh",     line2, 500);
        Station s7 = new Station("S12", "Pham Van Hai",  line2, 300);
        Station s8 = new Station("S13", "Tham Luong",    line2, 400);
        line2.addStation(s6);
        line2.addStation(s7);
        line2.addStation(s8);
        
        stations.add(s1);
        stations.add(s2);
        stations.add(s3);
        stations.add(s4);
        stations.add(s5);
        stations.add(s6);
        stations.add(s7);
        stations.add(s8);
        
        lines.add(line1);
        lines.add(line2);

        // ── 2. Hành khách ──────────────────────────────────────
        Passenger p1 = new Passenger("P001", "Nguyen Van A",  PassengerType.NORMAL,  "ID001", 500000);
        Passenger p2 = new Passenger("P002", "Tran Thi B",    PassengerType.STUDENT, "ID002", 200000);
        Passenger p3 = new Passenger("P003", "Le Van C",      PassengerType.SENIOR,  "ID003", 300000);
        Passenger p4 = new Passenger("P004", "Pham Thi D",    PassengerType.SENIOR,  "ID004", 100000);
        Passenger p5 = new Passenger("P005", "Hoang Van E",   PassengerType.NORMAL,  "ID005", 150000);
        
        passenger = p1;

        // ── 3. Phát hành vé vào TicketManager ─────────────────
        TicketManager tm = TicketManager.getInstance();

        // Vé cho module Passenger / Staff
        tm.issueTicket(p1, TicketType.SINGLE,  3);
        tm.issueTicket(p1, TicketType.SINGLE,  5);
        tm.issueTicket(p2, TicketType.DAILY,   0);
        tm.issueTicket(p2, TicketType.DAILY,   0);
        tm.issueTicket(p3, TicketType.MONTHLY, 0);

        // Vé cho module SmartGate (trạng thái cụ thể)
        Ticket t1 = new SingleTrip("T001", p1, 3);   // ActiveState  – check-in được
        Ticket t2 = new SingleTrip("T002", p2, 2);   // ActiveState  – check-in được
        Ticket t3 = new SingleTrip("T003", p3, 4);   // UsedState    – chờ check-out
        Ticket t4 = new SingleTrip("T004", p4, 1);   // ExpiredState – đã hết hạn
        Ticket t5 = new SingleTrip("T005", p5, 5);   // RefundedState – đã hoàn tiền

        t3.setState(new UsedState());
        t4.setState(new ExpiredState());
        t5.setState(new RefundedState());

        tm.saveTicket(t1);
        tm.saveTicket(t2);
        tm.saveTicket(t3);
        tm.saveTicket(t4);
        tm.saveTicket(t5);

        // ── 4. Giả lập lưu lượng HeatMap ──────────────────────
        //   s1 Ben Thanh :  48/500  =  9.6% → NORMAL
        //   s3 Ba Son    : 290/350  = 82.8% → ATTENTION
        //   s4 Van Thanh : 295/300  = 98.3% → CRITICAL
        for (int i = 0; i <  48; i++) s1.incrementCheckIn();
        for (int i = 0; i < 290; i++) s3.incrementCheckIn();
        for (int i = 0; i < 295; i++) s4.incrementCheckIn();

        HeatmapService hms = HeatmapService.getInstance();
        hms.analyzeRealtime(s1);
        hms.analyzeRealtime(s3);
        hms.analyzeRealtime(s4);

        // ── 5. Đăng ký công dân cho VerifyService ─────────────
        VerifyService vs = VerifyService.getInstance();
        vs.registerCitizen(new CitizenInfo("ID002", "Tran Thi B",
                LocalDate.of(2004,  1,  1), true,  false));
        vs.registerCitizen(new CitizenInfo("ID003", "Le Van C",
                LocalDate.of(1958,  5, 10), false, false));

        // ── 6. Cấu hình giá vé mặc định ───────────────────────
        FareConfig cfg = FareConfig.getInstance();
        cfg.setBaseFare(7000);
        cfg.setFarePerStop(1500);
        cfg.setFixedPriceDaily(30000);
        cfg.setFixedPriceMonthly(300000);

        // ── 7. In tóm tắt ra console ───────────────────────────
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
