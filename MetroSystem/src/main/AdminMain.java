package main;

import Metro.*;
import view.*;
import controller.*;
import java.time.LocalDate;

//
//- UC15: Quan ly tuyen
//- UC16: Quan ly ga
//- UC17: Cau hinh bieu gia
//- UC18: Bao cao doanh thu
//- UC19: Bao cao HeatMap

public class AdminMain {

	public static void main(String[] args) {

		// ── 1. Tao du lieu mau ──────────────────────────────

		// Tuyen 1: Ben Thanh - Suoi Tien
		MetroLine line1 = new MetroLine("L1", "Ben Thanh - Suoi Tien");
		Station s1 = new Station("S01", "Ben Thanh", line1, 500);
		Station s2 = new Station("S02", "Ba Son", line1, 400);
		Station s3 = new Station("S03", "Van Thanh", line1, 350);
		Station s4 = new Station("S04", "Tan Cang", line1, 300);
		line1.addStation(s1);
		line1.addStation(s2);
		line1.addStation(s3);
		line1.addStation(s4);

		// Tuyen 2: Ben Thanh - Tham Luong
		MetroLine line2 = new MetroLine("L2", "Ben Thanh - Tham Luong");
		Station s5 = new Station("S05", "Pham Van Hai", line2, 400);
		Station s6 = new Station("S06", "Tham Luong", line2, 300);
		line2.addStation(s5);
		line2.addStation(s6);

		// Hanh khach mau
		Passenger p1 = new Passenger("P001", "Nguyen Van A", PassengerType.NORMAL, "ID001", 200000);
		Passenger p2 = new Passenger("P002", "Tran Thi B", PassengerType.STUDENT, "ID002", 100000);
		Passenger p3 = new Passenger("P003", "Le Van C", PassengerType.SENIOR, "ID003", 150000);

		// Phat hanh ve de co du lieu bao cao doanh thu
		TicketManager tm = TicketManager.getInstance();
		tm.issueTicket(p1, TicketType.SINGLE, 3);
		tm.issueTicket(p1, TicketType.SINGLE, 5);
		tm.issueTicket(p2, TicketType.DAILY, 0);
		tm.issueTicket(p2, TicketType.DAILY, 0);
		tm.issueTicket(p3, TicketType.MONTHLY, 0);

		// Gia lap luu luong hanh khach de co HeatMap
		// Tuyen 1 - Ben Thanh sap qua tai
		for (int i = 0; i < 48; i++)
			s1.incrementCheckIn(); // 48/500 = 9.6% NORMAL
		for (int i = 0; i < 290; i++)
			s2.incrementCheckIn(); // 290/400 = 72.5% ATTENTION
		for (int i = 0; i < 340; i++)
			s3.incrementCheckIn(); // 340/350 = 97.1% CRITICAL

		// Phan tich de tao HeatmapAlert
		HeatmapService hms = HeatmapService.getInstance();
		hms.analyzeRealtime(s1);
		hms.analyzeRealtime(s2);
		hms.analyzeRealtime(s3);

		System.out.println("=== DU LIEU MAU ===");
		System.out.println("Tuyen da tao: " + line1.getLineName() + " | " + line2.getLineName());
		System.out.println("Ve da phat hanh: 5 ve (2 SINGLE, 2 DAILY, 1 MONTHLY)");
		System.out.println("HeatMap alerts: " + hms.getAlertHistory().size() + " canh bao");
		System.out.println("===================");

		// ── 2. Dang ky CitizenInfo cho VerifyService ─────────
		VerifyService vs = VerifyService.getInstance();
		vs.registerCitizen(new CitizenInfo("ID002", "Tran Thi B", LocalDate.of(2004, 1, 1), true, false));
		vs.registerCitizen(new CitizenInfo("ID003", "Le Van C", LocalDate.of(1958, 5, 10), false, false));

		// ── 3. Tao doi tuong Admin ───────────────────────────
		Admin admin = new Admin("ADM01", "Nguyen Thi Admin", "admin123");
		boolean loggedIn = admin.login("admin123");
		if (!loggedIn) {
			System.err.println("Admin dang nhap that bai!");
			return;
		}

		// ── 4. Khoi tao MVC ──────────────────────────────────
		AdminView view = new AdminView();
		AdminController controller = new AdminController(admin, view);
		view.setController(controller);

		// Dang ky cac tuyen vao controller de tra cuu
		controller.registerLine(line1);
		controller.registerLine(line2);

		// ── 5. Hien thi UI ───────────────────────────────────
		view.show();

	}
}