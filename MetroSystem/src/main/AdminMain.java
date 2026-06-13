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

        // ── 3. PHASE A: Phat hanh ve voi GIA CU (truoc khi cap nhat) ──
        System.out.println("\n======== PHASE A: Ve mua TRUOC khi cap nhat gia ========");
        System.out.println("FareConfig hien tai: baseFare=" + FareConfig.getInstance().getBaseFare()
                + " | farePerStop=" + FareConfig.getInstance().getFarePerStop()
                + " | daily=" + FareConfig.getInstance().getFixedPriceDaily()
                + " | monthly=" + FareConfig.getInstance().getFixedPriceMonthly());

        Ticket oldSingle  = tm.issueTicket(p1, TicketType.SINGLE,  3);
        Ticket oldDaily   = tm.issueTicket(p2, TicketType.DAILY,   0);
        Ticket oldMonthly = tm.issueTicket(p3, TicketType.MONTHLY, 0);

        System.out.println("[Gia cu] SINGLE  = " + oldSingle.getPrice());
        System.out.println("[Gia cu] DAILY   = " + oldDaily.getPrice());
        System.out.println("[Gia cu] MONTHLY = " + oldMonthly.getPrice());

        // ── 4. Cap nhat gia (simulate Admin thay doi) ──────
        System.out.println("\n======== CAP NHAT GIA ========");
        FareConfig cfg = FareConfig.getInstance();
        cfg.setBaseFare(10000);
        cfg.setFarePerStop(5000);
        cfg.setFixedPriceDaily(100000);
        cfg.setFixedPriceMonthly(200000);
        System.out.println("FareConfig moi: baseFare=" + cfg.getBaseFare()
                + " | farePerStop=" + cfg.getFarePerStop()
                + " | daily=" + cfg.getFixedPriceDaily()
                + " | monthly=" + cfg.getFixedPriceMonthly());

        // ── 5. PHASE B: Phat hanh ve voi GIA MOI (sau khi cap nhat) ──
        System.out.println("\n======== PHASE B: Ve mua SAU khi cap nhat gia ========");
        Ticket newSingle  = tm.issueTicket(p1, TicketType.SINGLE,  3);
        Ticket newDaily   = tm.issueTicket(p2, TicketType.DAILY,   0);
        Ticket newMonthly = tm.issueTicket(p3, TicketType.MONTHLY, 0);

        System.out.println("[Gia moi] SINGLE  = " + newSingle.getPrice());
        System.out.println("[Gia moi] DAILY   = " + newDaily.getPrice());
        System.out.println("[Gia moi] MONTHLY = " + newMonthly.getPrice());

        // ── 6. Kiem tra revenue report ──────────────────────
        System.out.println("\n======== REVENUE REPORT (tinh lai theo gia hien tai) ========");
        tm.getRevenueReport("2026-06").forEach((type, revenue) -> {
            if (revenue > 0)
                System.out.printf("%-10s | %,.0f VND%n", type, revenue);
        });

        // ── 7. HeatMap data ────────────────────────────────
        for (int i = 0; i < 290; i++) s2.incrementCheckIn();
        for (int i = 0; i < 340; i++) s3.incrementCheckIn();
        HeatmapService hms = HeatmapService.getInstance();
        hms.analyzeRealtime(s2);
        hms.analyzeRealtime(s3);

        // ── 8. VerifyService ───────────────────────────────
        VerifyService vs = VerifyService.getInstance();
        vs.registerCitizen(new CitizenInfo("ID002", "Tran Thi B", LocalDate.of(2004, 1, 1), true,  false));
        vs.registerCitizen(new CitizenInfo("ID003", "Le Van C",   LocalDate.of(1958, 5, 10), false, false));

        // ── 9. Khoi tao MVC ────────────────────────────────
        Admin admin = new Admin("ADM01", "Nguyen Thi Admin", "admin123");
        if (!admin.login("admin123")) { System.err.println("Login failed!"); return; }

        AdminView view = new AdminView();
        AdminController controller = new AdminController(admin, view);
        view.setController(controller);
        controller.registerLine(line1);
        controller.registerLine(line2);

        view.show();

//        System.out.println("\n======== HUONG DAN TEST TREN GIAO DIEN ========");
//        System.out.println("1. Vao tab 'Cau hinh Gia ve' -> thay doi gia -> bam 'Cap nhat gia'");
//        System.out.println("2. Vao tab 'Bao cao' -> bam 'Doanh thu theo loai ve'");
//        System.out.println("   -> Dong 'Gia hien tai' se phan anh gia vua cap nhat");
//        System.out.println("   -> Doanh thu duoc tinh lai theo gia moi (calcPrice)");
//        System.out.println("   -> Ve Phase A (gia cu) va Phase B (gia moi) deu co mat");
    }
}
