package main;

import Metro.*;
import view.*;
import controller.*;
import java.time.LocalDate;

public class AdminMain {

    public static void main(String[] args) {

        // ── 1. Tao tuyen & ga ──────────────────────────────
        MetroLine line1 = new MetroLine("L1", "Ben Thanh - Suoi Tien");
        Station s1 = new Station("S01", "Ben Thanh", line1, 500);
        Station s2 = new Station("S02", "Ba Son",    line1, 400);
        Station s3 = new Station("S03", "Van Thanh", line1, 350);
        Station s4 = new Station("S04", "Tan Cang",  line1, 300);
        line1.addStation(s1); line1.addStation(s2);
        line1.addStation(s3); line1.addStation(s4);

        MetroLine line2 = new MetroLine("L2", "Ben Thanh - Tham Luong");
        Station s5 = new Station("S05", "Pham Van Hai", line2, 400);
        Station s6 = new Station("S06", "Tham Luong",   line2, 300);
        line2.addStation(s5); line2.addStation(s6);

        // ── 2. Hanh khach mau ──────────────────────────────
        Passenger p1 = new Passenger("P001", "Nguyen Van A", PassengerType.NORMAL,  "ID001", 500000);
        Passenger p2 = new Passenger("P002", "Tran Thi B",   PassengerType.STUDENT, "ID002", 300000);
        Passenger p3 = new Passenger("P003", "Le Van C",     PassengerType.SENIOR,  "ID003", 300000);

        TicketManager tm = TicketManager.getInstance();
        FareConfig    cfg = FareConfig.getInstance();

        // ── 3. PHASE A: Phat hanh ve voi GIA GOC ──────────
        // [THEM MOI] - Danh dau era "Gia goc" truoc khi tao ve Phase A
        tm.markPriceEra("Gia goc (truoc cap nhat)");

        tm.issueTicket(p1, TicketType.SINGLE,  3);
        tm.issueTicket(p2, TicketType.DAILY,   0);
        tm.issueTicket(p3, TicketType.MONTHLY, 0);

        // ── 4. Simulate cap nhat gia lan 1 ─────────────────
        // [THEM MOI] - Danh dau era moi TRUOC khi doi gia, sau do doi gia
        // Thu tu nay quan trong: markPriceEra phai goi truoc issueTicket tiep theo
        cfg.setBaseFare(10000);
        cfg.setFarePerStop(5000);
        cfg.setFixedPriceDaily(60000);
        cfg.setFixedPriceMonthly(500000);
        tm.markPriceEra("Sau cap nhat lan 1 (base=10k, perStop=5k, daily=60k, monthly=500k)");

        // ── 5. PHASE B: Phat hanh ve voi GIA MOI ──────────
        tm.issueTicket(p1, TicketType.SINGLE,  3);
        tm.issueTicket(p2, TicketType.DAILY,   0);
        tm.issueTicket(p3, TicketType.MONTHLY, 0);

        // ── 6. HeatMap data ────────────────────────────────
        for (int i = 0; i < 290; i++) s2.incrementCheckIn();
        for (int i = 0; i < 340; i++) s3.incrementCheckIn();
        HeatmapService hms = HeatmapService.getInstance();
        hms.analyzeRealtime(s2);
        hms.analyzeRealtime(s3);

        // ── 7. VerifyService ───────────────────────────────
        VerifyService vs = VerifyService.getInstance();
        vs.registerCitizen(new CitizenInfo("ID002", "Tran Thi B", LocalDate.of(2004, 1, 1), true,  false));
        vs.registerCitizen(new CitizenInfo("ID003", "Le Van C",   LocalDate.of(1958, 5, 10), false, false));

        // ── 8. Khoi tao MVC ────────────────────────────────
        Admin admin = new Admin("ADM01", "Nguyen Thi Admin", "admin123");
        if (!admin.login("admin123")) { System.err.println("Login failed!"); return; }

        AdminView view = new AdminView();

        // [THEM MOI] - Truyen TicketManager vao AdminController de controller
        // co the goi markPriceEra() moi khi Admin cap nhat gia tren UI
        AdminController controller = new AdminController(admin, view);
        view.setController(controller);
        controller.registerLine(line1);
        controller.registerLine(line2);

        view.show();
    }
}
