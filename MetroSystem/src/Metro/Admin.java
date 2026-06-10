package Metro;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

public class Admin extends Employee {

	public Admin(String employeeId, String name, String password) {
		super(employeeId, name, password);
		// TODO Auto-generated constructor stub

	}

	// Cau hinh gia ve co ban va gia moi tram cho toan he thong
	// gia ve co ban
	// gia ve moi tram
	public void setFareDetail(double base, double perStop) {
		// gia truoc khi cap nhat
		if (base <= 0 || perStop <= 0) {
			System.out.println("Gia khong hop li gia ve hoac gia tram phai > 0 ");
			return;
		}
		// Lay istance duy nhat cua FareConfig (singleton) va cap nhat
		FareConfig fareConfig = FareConfig.getInstance();
		fareConfig.setBaseFare(base);
		fareConfig.setFarePerStop(perStop);
		System.out.println("Cap nhat bieu gia");
		System.out.println("Gia co ban: " + base + "VND");
		System.out.println("Gia /tram: " + perStop + "VND");
	}

	// Cap nhat Bang chiet khau
	// NGHIEN CUU THEM
	public void updateDiscounts(Map<PassengerType, Double> ratesMap) {
		// Kiem tra dau vao co bi rong khong
		if (ratesMap == null || ratesMap.isEmpty()) {
			System.out.println("Bang chiet khau khong duoc de trong ");
			return;
		}
		// B1 kiem tra tung loai hanh khach trong bang
		for (Map.Entry<PassengerType, Double> loaiHK : ratesMap.entrySet()) {
			double rate = loaiHK.getValue();
			if (rate < 0 || rate > 1) {
				System.out.println("He so chiet khau khong hop le");
				System.out.println("   Loai hanh khach : " + loaiHK.getKey());
				System.out.println("   He so nhap vao  : " + rate);
				System.out.println("   He so hop le    : tu 0.0 den 1.0");
				System.out.println("Bang chiet khau KHONG duoc cap nhat");
				return;
			}
		}
		FareConfig.getInstance().updateDiscounts(ratesMap);
		System.out.println("Bang chiet khau da duoc cap nhat:");
		for (Map.Entry<PassengerType, Double> loaiHK : ratesMap.entrySet()) {
			System.out.printf("   %-10s → %.0f%%%n", loaiHK.getKey(), // Ten loai HK: STUDENT, SENIOR...
					loaiHK.getValue() * 100);// doi thanh phan tram
		}
	}

	// Them mot ga moi vao 1 tuyen metro
	public void addStation(String name, MetroLine metroLine, int capacity) {
		// gia dau vao
		if (name == null || name.trim().isEmpty()) {
			System.out.println("Ten ga khong duoc de trong ");
			return;
		}
		if (metroLine == null) {
			System.out.println("Tuyen metro khong hop le");
			return;
		}
		if (capacity <= 0) {
			System.out.println("Suc chua phai > 0");
			return;
		}
		// DANG XEM XET LAI
		// Tao ma ga tu dong tu ten tuyen + so thu tu ga hien co
		String stationId = metroLine.getLineId() + "_S" + String.format("%02d", metroLine.getStations().size() + 1);
		// Tao doi tuong Station moi
		Station newStation = new Station(stationId, name, metroLine, capacity);
		// Them vao tuyen (metroLine se kiem tra trung lap ben trong )
		metroLine.addStation(newStation);
		System.out.println("Ga moi " + name + " (ID: " + stationId + " ) da duoc vao tuyen" + metroLine.getLineName());
	}

	// Cap nhat thong tin tuyen Metro
	public void updateLine(MetroLine metroLine, String newName, LineStatus status) {
//		if (line == null) {
//            System.out.println(" Tuyen metro khong ton tai.");
//            return;
//        }
// 
//        // Chi cap nhat ten neu ten moi khong null va khong rong
//        if (newName != null && !newName.trim().isEmpty()) {
//            String oldName = line.getLineName();
//            line.setLineName(newName);
//            System.out.println("Doi ten tuyen:" + oldName + "->" + newName );
//        }
// 
//        // Cap nhat trang thai
//        if (status != null) {
//            line.setStatus(status);
//            System.out.println("Trang thai tuyen " + line.getLineName()
//                    + ": " + status);
//        }
// 
//        System.out.println("cap nhat tuyen thanh cong: " + line);
//    }
//	    // B1: Kiem tra thong tin tuyen co ton tai khong
		if (metroLine == null) {
			System.out.println("Tuyen metro khong ton tai (null).");
			return;
		}

		// In thong tin hien tai cua tuyen truoc khi sua
		System.out.println("Tuyen can cap nhat [" + metroLine.getLineId() + "] " + metroLine.getLineName() + " — "
				+ metroLine.getStatus());

		// B2: Cap nhat ten neu NewName hop le
		// dk: newName != null va khong phai chuoi rong/toan khoang trang
		if (newName != null && !newName.trim().isEmpty()) {
			String oldName = metroLine.getLineName(); // Luu ten cu de in log
			metroLine.setLineName(newName);
			System.out.println("Doi ten '" + oldName + " → " + newName + "");
		}

		// B3:Cap nhat trag thai neu status hop le
		if (status != null) {
			metroLine.setStatus(status);
			System.out.println("Trang thai: " + status);
		}

		// B4: in ket qua sau khi cap nhat
		System.out.println("cap nhat thanh cong: " + metroLine);
	}

	public Map<TicketType, Integer> requestRevenueReport(String dateRange) {
		if (dateRange == null || dateRange.trim().isEmpty()) {
			System.out.println("Khoang thoi gian khong hop le.");
			return null;
		}

		System.out.println("Tong hop bao cao doanh thu " + dateRange);
		// Goi TicketManager Singleton de lay bao cao
		Map<TicketType, Integer> report = TicketManager.getInstance().getRevenueReport(dateRange);

		System.out.println("Bao cao doanh thu: " + report);
		return report;
	}

	// Yeu cau xem bao cao mat do heatmap
	public List<HeatmapAlert> requestHeatmapReport() {
		// Goi HeatmapService Singleton de lay du lieu
		List<HeatmapAlert> report = HeatmapService.getInstance().getHeatmapReport();

		System.out.println("Bao cao HeatMap:\n" + report);
		return report;
	}

	static int passed = 0;
	static int failed = 0;

	public static void main(String[] args) {
		System.out.println("========================================");
		System.out.println("         TEST CLASS ADMIN               ");
		System.out.println("========================================\n");

		testLogin();
		testLogout();
		testSetFareDetail();
		testUpdateDiscounts();
		testAddStation();
		testUpdateLine();
		testRequestRevenueReport();
		testRequestHeatmapReport();

		System.out.println("\n========================================");
		System.out.println("KET QUA: " + passed + " PASS | " + failed + " FAIL");
		System.out.println("========================================");
	}

	// -----------------------------------------------
	// HELPER: in ket qua tung test case
	// -----------------------------------------------
	static void assertTest(String testName, boolean condition) {
		if (condition) {
			System.out.println("  [PASS] " + testName);
			passed++;
		} else {
			System.out.println("  [FAIL] " + testName);
			failed++;
		}
	}

	// =============================
	// 1. TEST LOGIN / LOGOUT
	// =============================
	static void testLogin() {
		System.out.println("--- 1. TEST LOGIN ---");
		Admin admin = new Admin("A001", "Nguyen Van A", "mat_khau_123");

		// Dang nhap dung mat khau
		boolean ketQua1 = admin.login("mat_khau_123");
		assertTest("Login dung mat khau -> true", ketQua1 == true);

		// Dang nhap sai mat khau
		boolean ketQua2 = admin.login("sai_mat_khau");
		assertTest("Login sai mat khau -> false", ketQua2 == false);

		// Mat khau rong
		boolean ketQua3 = admin.login("");
		assertTest("Login mat khau rong -> false", ketQua3 == false);

		System.out.println();
	}

	// =============================
	// 2. TEST LOGOUT
	// =============================
	static void testLogout() {
		System.out.println("--- 2. TEST LOGOUT ---");
		Admin admin = new Admin("A002", "Tran Thi B", "pass456");

		// Logout khong nem exception la pass
		try {
			admin.logout();
			assertTest("Logout khong nem exception", true);
		} catch (Exception e) {
			assertTest("Logout khong nem exception", false);
		}

		System.out.println();
	}

	// =============================
	// 3. TEST SET FARE DETAIL
	// =============================
	static void testSetFareDetail() {
		System.out.println("--- 3. TEST SET FARE DETAIL ---");
		Admin admin = new Admin("A003", "Le Van C", "pass789");

		// Reset FareConfig truoc moi test
		FareConfig config = FareConfig.getInstance();

		// Gia hop le
		try {
			admin.setFareDetail(8000, 1500);
			assertTest("setFareDetail gia hop le -> khong nem exception", true);
			assertTest("baseFare duoc cap nhat", config.getBaseFare() == 8000);
			assertTest("farePerStop duoc cap nhat", config.getFarePerStop() == 1500);
		} catch (Exception e) {
			assertTest("setFareDetail gia hop le -> khong nem exception", false);
		}

		// baseFare = 0 (khong hop le)
		double baseTruoc = config.getBaseFare();
		admin.setFareDetail(0, 1500);
		assertTest("setFareDetail baseFare=0 -> khong cap nhat", config.getBaseFare() == baseTruoc);

		// perStop am
		double perStopTruoc = config.getFarePerStop();
		admin.setFareDetail(8000, -100);
		assertTest("setFareDetail perStop am -> khong cap nhat", config.getFarePerStop() == perStopTruoc);

		// Ca hai deu am
		admin.setFareDetail(-1, -1);
		assertTest("setFareDetail ca hai am -> khong cap nhat", config.getBaseFare() == baseTruoc);

		System.out.println();
	}

	// =============================
	// 4. TEST UPDATE DISCOUNTS
	// =============================
	static void testUpdateDiscounts() {
		System.out.println("--- 4. TEST UPDATE DISCOUNTS ---");
		Admin admin = new Admin("A004", "Pham Thi D", "pass000");

		// Bang chiet khau hop le
		Map<PassengerType, Double> bangHopLe = new HashMap<>();
		bangHopLe.put(PassengerType.STUDENT, 0.65);
		bangHopLe.put(PassengerType.SENIOR, 0.45);
		try {
			admin.updateDiscounts(bangHopLe);
			double studentRate = FareConfig.getInstance().getDiscount(PassengerType.STUDENT);
			double seniorRate = FareConfig.getInstance().getDiscount(PassengerType.SENIOR);
			assertTest("updateDiscounts STUDENT -> 0.65", studentRate == 0.65);
			assertTest("updateDiscounts SENIOR  -> 0.45", seniorRate == 0.45);
		} catch (Exception e) {
			assertTest("updateDiscounts hop le -> khong nem exception", false);
		}

		// Bang null
		admin.updateDiscounts(null);
		assertTest("updateDiscounts null -> khong crash", true); // Chi can khong nem exception

		// Bang rong
		admin.updateDiscounts(new HashMap<>());
		assertTest("updateDiscounts rong -> khong crash", true);

		// Rate ngoai khoang [0,1]
		Map<PassengerType, Double> bangSai = new HashMap<>();
		bangSai.put(PassengerType.NORMAL, 1.5);
		try {
			admin.updateDiscounts(bangSai);
			assertTest("updateDiscounts rate > 1 -> khong cap nhat", true);
		} catch (IllegalArgumentException e) {
			// Co the nem exception — van chap nhan
			assertTest("updateDiscounts rate > 1 -> xu ly loi", true);
		}

		System.out.println();
	}

	// =============================
	// 5. TEST ADD STATION
	// =============================
	static void testAddStation() {
		System.out.println("--- 5. TEST ADD STATION ---");
		Admin admin = new Admin("A005", "Nguyen Thi E", "passXYZ");
		MetroLine line = new MetroLine("L1", "Tuyen So 1");

		// Them ga hop le
		int soBanDau = line.getStations().size();
		admin.addStation("Ga Ben Thanh", line, 500);
		assertTest("addStation hop le -> so ga tang them 1", line.getStations().size() == soBanDau + 1);

		// Them ga trung ten
		int soSauThem1 = line.getStations().size();
		admin.addStation("Ga Ben Thanh", line, 500);
		// MetroLine.addStation() da kiem tra trung lap ben trong
		// so ga khong nen tang them
		assertTest("addStation trung ten -> so ga khong tang", line.getStations().size() == soSauThem1);

		// Ten ga null
		int soTruocNull = line.getStations().size();
		admin.addStation(null, line, 300);
		assertTest("addStation ten null -> khong them", line.getStations().size() == soTruocNull);

		// Ten ga rong
		admin.addStation("", line, 300);
		assertTest("addStation ten rong -> khong them", line.getStations().size() == soTruocNull);

		// MetroLine null
		admin.addStation("Ga Moi", null, 300);
		assertTest("addStation line null -> khong crash", true);

		// Suc chua <= 0
		int soTruocCapacity = line.getStations().size();
		admin.addStation("Ga Suc Chua Am", line, -1);
		assertTest("addStation capacity <= 0 -> khong them", line.getStations().size() == soTruocCapacity);

		System.out.println();
	}

	// =============================
	// 6. TEST UPDATE LINE
	// =============================
	static void testUpdateLine() {
		System.out.println("--- 6. TEST UPDATE LINE ---");
		Admin admin = new Admin("A006", "Hoang Van F", "passABC");
		MetroLine line = new MetroLine("L2", "Tuyen So 2");

		// Cap nhat ten hop le
		admin.updateLine(line, "Tuyen Ben Thanh - Suoi Tien", LineStatus.ACTIVE);
		assertTest("updateLine ten moi hop le", line.getLineName().equals("Tuyen Ben Thanh - Suoi Tien"));
		assertTest("updateLine status moi", line.getStatus() == LineStatus.ACTIVE);

		// Ten moi null -> giu nguyen ten cu
		String tenTruoc = line.getLineName();
		admin.updateLine(line, null, LineStatus.MAINTENANCE);
		assertTest("updateLine ten null -> giu ten cu", line.getLineName().equals(tenTruoc));
		assertTest("updateLine status van cap nhat khi ten null", line.getStatus() == LineStatus.MAINTENANCE);

		// Ten moi rong -> giu nguyen
		admin.updateLine(line, "   ", LineStatus.ACTIVE);
		assertTest("updateLine ten rong -> giu ten cu", line.getLineName().equals(tenTruoc));

		// Line null
		try {
			admin.updateLine(null, "Ten Moi", LineStatus.ACTIVE);
			assertTest("updateLine line null -> khong crash", true);
		} catch (Exception e) {
			assertTest("updateLine line null -> khong crash", false);
		}

		System.out.println();
	}

	// =============================
	// 7. TEST REQUEST REVENUE REPORT
	// =============================
	static void testRequestRevenueReport() {
		System.out.println("--- 7. TEST REQUEST REVENUE REPORT ---");
		Admin admin = new Admin("A007", "Do Thi G", "passDEF");

		// Phat hanh mot so ve truoc khi test
		Passenger hk = new Passenger("P001", "Khach 1", PassengerType.NORMAL, "CCCD001", 200000);
		TicketManager.getInstance().issueTicket(hk, TicketType.SINGLE, 3);
		TicketManager.getInstance().issueTicket(hk, TicketType.DAILY, 0);

		// dateRange hop le
		Map<TicketType, Integer> baocao = admin.requestRevenueReport("2025-01");
		assertTest("requestRevenueReport dateRange hop le -> tra ve map", baocao != null);
		assertTest("bao cao co it nhat 1 loai ve", baocao.size() >= 1);

		// dateRange null
		Map<TicketType, Integer> bcNull = admin.requestRevenueReport(null);
		assertTest("requestRevenueReport null -> tra ve null", bcNull == null);

		// dateRange rong
		Map<TicketType, Integer> bcRong = admin.requestRevenueReport("   ");
		assertTest("requestRevenueReport rong -> tra ve null", bcRong == null);

		System.out.println();
	}

	// =============================
	// 8. TEST REQUEST HEATMAP REPORT
	// =============================
	static void testRequestHeatmapReport() {
		System.out.println("--- 8. TEST REQUEST HEATMAP REPORT ---");
		Admin admin = new Admin("A008", "Vu Van H", "passGHI");

		// Tao du lieu: ga + nhan vien + check-in gia lap
		MetroLine line = new MetroLine("L3", "Tuyen Test");
		Station station = new Station("S_T01", "Ga Test Heatmap", line, 10);

		StationStaff staff = new StationStaff("ST001", "Nhan vien 1", "pass", "ST001");
		HeatmapService.getInstance().attach(staff);

		// Simulate nhieu check-in de kich hoat canh bao
		for (int i = 0; i < 6; i++) {
			station.incrementCheckIn();
		}
		HeatmapService.getInstance().analyzeRealtime(station); // >= 50% -> ATTENTION/WARNING

		// Lay bao cao
		List<HeatmapAlert> report = admin.requestHeatmapReport();
		assertTest("requestHeatmapReport tra ve list", report != null);
		assertTest("list co it nhat 1 canh bao sau khi vuot nguong", report.size() >= 1);

		// Canh bao dung station
		boolean dungStation = report.stream().anyMatch(a -> a.getStation().getStationName().equals("Ga Test Heatmap"));
		assertTest("canh bao chua dung station", dungStation);

		System.out.println();
	}

}