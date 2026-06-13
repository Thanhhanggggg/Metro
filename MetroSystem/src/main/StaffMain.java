package main;

import java.time.LocalDate;
import Metro.*;
import view.*;
import controller.*;

//Demo chạy thử StaffView + StaffController
// Tạo sẵn dữ liệu mẫu để test các tính năng

public class StaffMain {

	public static void main(String[] args) {

		//Tao du lieu mau
		// Tuyen & ga
		MetroLine line = new MetroLine("L1", "Ben Thanh - Suoi Tien");
		Station s1 = new Station("S01", "Ben Thanh", line, 100);
		Station s2 = new Station("S02", "Ba Son", line, 80);
		line.addStation(s1);
		line.addStation(s2);

		// Hanh khach
		Passenger p1 = new Passenger("P001", "Nguyen Van A", PassengerType.NORMAL, "ID001", 200000);
		Passenger p2 = new Passenger("P002", "Tran Thi B", PassengerType.STUDENT, "ID002", 100000);

		// Tao ve vao TicketManager
		TicketManager tm = TicketManager.getInstance();

		Ticket tActive = tm.issueTicket(p1, TicketType.SINGLE, 3);
		Ticket tUsed = tm.issueTicket(p2, TicketType.DAILY, 0);
		Ticket tExpired = tm.issueTicket(p1, TicketType.MONTHLY, 0);

		tActive.setStrategy(new FullRefundPolicy());
		tUsed.setStrategy(new FullRefundPolicy());
		tExpired.setStrategy(new FullRefundPolicy());

		// Gia lap trang thai
		tUsed.checkIn(); // ACTIVE → USED
		tExpired.checkIn(); // ACTIVE → USED
		tExpired.checkOut(); // USED → EXPIRED

		System.out.println("=== VÉ MẪU ===");
		System.out.println("ACTIVE  ticket ID: " + tActive.getTicketId());
		System.out.println("USED    ticket ID: " + tUsed.getTicketId());
		System.out.println("EXPIRED ticket ID: " + tExpired.getTicketId());
		System.out.println("==============");

		// 2. Dang ky CitizenInfo cho VerifyService 
		VerifyService vs = VerifyService.getInstance();
		vs.registerCitizen(new CitizenInfo("ID002", "Tran Thi B", LocalDate.of(2004, 1, 1), true, false));

		// 3. Tao nhan vien
		StationStaff staff = new StationStaff("ST001", "Nguyen Thi Staff", "pass123", "ST001");

		// 4. Khoi tao MVC
		StaffView view = new StaffView();
		StaffController controller = new StaffController(staff, view);
		view.setController(controller);
		HeatmapService.getInstance().attach(view);

		// 5.Gia lap Heatmap sau 3 giay
		new Thread(() -> {
			try {
				Thread.sleep(3000);
				//Check-in nhieu de alert
				for (int i = 0; i < 55; i++)
					s1.incrementCheckIn();
				HeatmapService.getInstance().analyzeRealtime(s1); // ATTENTION
				Thread.sleep(2000);
				for (int i = 0; i < 30; i++)
					s1.incrementCheckIn();
				HeatmapService.getInstance().analyzeRealtime(s1); // WARNING/CRITICAL
			} catch (InterruptedException ignored) {
			}
		}).start();

		// Hien thi
		javax.swing.SwingUtilities.invokeLater(() -> {
            javax.swing.JFrame frame = new javax.swing.JFrame("Station Staff Management");
            frame.setDefaultCloseOperation(javax.swing.JFrame.EXIT_ON_CLOSE);
            frame.setSize(700, 560);
            frame.setLocationRelativeTo(null);
            frame.add(view);
            frame.setVisible(true);
        });
	}
}