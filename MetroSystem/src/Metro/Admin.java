package Metro;

import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.HashMap;

public class Admin extends Employee {
	private List<MetroLine> metroLines = new ArrayList<>();

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

	public Map<TicketType, Double> requestRevenueReport(String dateRange) {
	    if (dateRange == null || dateRange.trim().isEmpty()) {
	        System.out.println("Khoang thoi gian khong hop le.");
	        return null;
	    }
	    System.out.println("Tong hop bao cao doanh thu " + dateRange);
	    Map<TicketType, Double> report = TicketManager.getInstance().getRevenueReport(dateRange);
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
	// Method dang ky tuyen vao he thong (goi khi tao tuyen moi):
	public void registerLine(MetroLine metroLine) {
	    if (metroLine == null) return;
	    if (!metroLines.contains(metroLine)) {
	        metroLines.add(metroLine);
	        System.out.println("Da dang ky tuyen: " + metroLine.getLineName());
	    }
	}

	// Xoa ga khoi mot tuyen cu the:
	public boolean removeStation(Station station, MetroLine metroLine) {
	    if (station == null) {
	        System.out.println("Station khong hop le (null).");
	        return false;
	    }
	    if (metroLine == null) {
	        System.out.println("Tuyen metro khong hop le (null).");
	        return false;
	    }
	    boolean result = metroLine.removeStation(station);
	    if (result) {
	        System.out.println("Admin da xoa ga " + station.getStationName()
	            + " khoi tuyen " + metroLine.getLineName() + ".");
	    }
	    return result;
	}

	// Xoa toan bo mot tuyen metro:
	public boolean removeMetroLine(MetroLine metroLine) {
	    if (metroLine == null) {
	        System.out.println("Tuyen metro khong hop le (null).");
	        return false;
	    }
	    if (!metroLines.contains(metroLine)) {
	        System.out.println("Tuyen " + metroLine.getLineName() + " khong ton tai trong he thong.");
	        return false;
	    }
	    // Vo hieu hoa tuyen truoc khi xoa de cac SmartGate/Station lien quan biet tuyen da dong
	    metroLine.setStatus(LineStatus.TNACTIVE);
	    metroLines.remove(metroLine);
	    System.out.println("Da xoa tuyen " + metroLine.getLineName() + " khoi he thong.");
	    return true;
	}
	public static void main(String[] args) {
		 
        System.out.println("========================================");
        System.out.println("    KIEM TRA CLASS ADMIN");
        System.out.println("========================================");
 
        // ------------------------------------------------
        // CHUAN BI DU LIEU DUNG CHUNG
        // ------------------------------------------------
        Admin admin = new Admin("A001", "Nguyen Van Admin", "pass123");
 
        MetroLine line1 = new MetroLine("L1", "Ben Thanh - Suoi Tien");
        Station benThanh = new Station("S01", "Ben Thanh", line1, 500);
        Station suoiTien = new Station("S03", "Suoi Tien", line1, 400);
        line1.addStation(benThanh);
        line1.addStation(suoiTien);
 
        Passenger hk1 = new Passenger("P001", "Nguyen Van A", PassengerType.NORMAL, "ID001", 200000);
        Passenger hk2 = new Passenger("P002", "Tran Thi B", PassengerType.STUDENT, "ID002", 100000);
 
        // Phat hanh truoc mot so ve de co du lieu bao cao
        TicketManager.getInstance().issueTicket(hk1, TicketType.SINGLE, 3);
        TicketManager.getInstance().issueTicket(hk1, TicketType.DAILY, 0);
        TicketManager.getInstance().issueTicket(hk2, TicketType.MONTHLY, 0);
 
        // ------------------------------------------------
        // NHOM 1: KIEM TRA LOGIN / LOGOUT
        // ------------------------------------------------
        System.err.println("\n--- NHOM 1: KIEM TRA LOGIN / LOGOUT ---");
 
        // Dang nhap dung mat khau
        boolean loginDung = admin.login("pass123");
        System.out.println("[Login dung] ket qua = true : " + (loginDung == true));
 
        // Dang nhap sai mat khau
        boolean loginSai = admin.login("sai_mat_khau");
        System.out.println("[Login sai] ket qua = false: " + (loginSai == false));
 
        // Dang nhap mat khau rong
        boolean loginRong = admin.login("");
        System.out.println("[Login rong] ket qua = false: " + (loginRong == false));
 
        // Logout khong crash
        System.out.print("[Logout]khong crash: ");
        admin.logout();
        System.out.println("true");
 
        // ------------------------------------------------
        // NHOM 2: KIEM TRA setFareDetail()
        // ------------------------------------------------
        System.err.println("\n--- NHOM 2: KIEM TRA setFareDetail() ---");
 
        FareConfig config = FareConfig.getInstance();
 
        // Gia hop le -> cap nhat thanh cong
        admin.setFareDetail(8000, 1500);
        System.out.println("[Gia hop le] baseFare = 8000 : " + (config.getBaseFare() == 8000));
        System.out.println("[Gia hop le] perStop  = 1500 : " + (config.getFarePerStop() == 1500));
 
        // baseFare = 0 -> khong cap nhat
        double baseTruoc = config.getBaseFare();
        admin.setFareDetail(0, 1500);
        System.out.println("[baseFare = 0]  khong cap nhat  : " + (config.getBaseFare() == baseTruoc));
 
        // perStop am -> khong cap nhat
        double perStopTruoc = config.getFarePerStop();
        admin.setFareDetail(8000, -100);
        System.out.println("[perStop am] khong cap nhat  : " + (config.getFarePerStop() == perStopTruoc));
 
        // Ca hai am -> khong cap nhat
        admin.setFareDetail(-1, -1);
        System.out.println("[Ca hai am] khong cap nhat  : " + (config.getBaseFare() == baseTruoc));
 
        // ------------------------------------------------
        // NHOM 3: KIEM TRA updateDiscounts()
        // ------------------------------------------------
        System.err.println("\n--- NHOM 3: KIEM TRA updateDiscounts() ---");
 
        // Bang hop le -> cap nhat thanh cong
        Map<PassengerType, Double> bangHopLe = new HashMap<>();
        bangHopLe.put(PassengerType.STUDENT, 0.65);
        bangHopLe.put(PassengerType.SENIOR, 0.45);
        admin.updateDiscounts(bangHopLe);
        System.out.println("[STUDENT = 0.65] cap nhat dung  : " + (config.getDiscount(PassengerType.STUDENT) == 0.65));
        System.out.println("[SENIOR  = 0.45] cap nhat dung  : " + (config.getDiscount(PassengerType.SENIOR)  == 0.45));
 
        // Bang null -> khong crash
        System.out.print("[Bang null] khong crash : ");
        admin.updateDiscounts(null);
        System.out.println("true");
 
        // Bang rong -> khong crash
        System.out.print("[Bang rong] khong crash : ");
        admin.updateDiscounts(new HashMap<>());
        System.out.println("true");
 
        // Rate > 1 -> khong cap nhat hoac bao loi
        System.out.print("[Rate > 1] xu ly an toan   : ");
        try {
            Map<PassengerType, Double> bangSai = new HashMap<>();
            bangSai.put(PassengerType.NORMAL, 1.5);
            admin.updateDiscounts(bangSai);
            System.out.println("true (xu ly khong crash)");
        } catch (IllegalArgumentException e) {
            System.out.println("true (Exception: " + e.getClass().getSimpleName() + ")");
        }
 
        // ------------------------------------------------
        // NHOM 4: KIEM TRA addStation()
        // ------------------------------------------------
        System.err.println("\n--- NHOM 4: KIEM TRA addStation() ---");
 
        int soBanDau = line1.getStations().size();
 
        // Them ga hop le -> so ga tang
        admin.addStation("Ga Moi 1", line1, 300);
        System.out.println("[Them hop le] so ga tang 1 : " + (line1.getStations().size() == soBanDau + 1));
 
        // Them ga trung ten -> khong tang
        int soSauThem = line1.getStations().size();
        admin.addStation("Ga Moi 1", line1, 300);
        System.out.println("[Trung ten] so ga khong tang: " + (line1.getStations().size() == soSauThem));
 
        // Ten null -> khong them
        int soTruocNull = line1.getStations().size();
        admin.addStation(null, line1, 300);
        System.out.println("[Ten null] khong them : " + (line1.getStations().size() == soTruocNull));
 
        // Ten rong -> khong them
        admin.addStation("", line1, 300);
        System.out.println("[Ten rong] khong them : " + (line1.getStations().size() == soTruocNull));
 
        // MetroLine null -> khong crash
        System.out.print("[Line null] khong crash : ");
        admin.addStation("Ga X", null, 300);
        System.out.println("true");
 
        // Capacity <= 0 -> khong them
        int soTruocCap = line1.getStations().size();
        admin.addStation("Ga Cap Am", line1, -1);
        System.out.println("[Capacity <= 0] khong them : " + (line1.getStations().size() == soTruocCap));
 
        // ------------------------------------------------
        // NHOM 5: KIEM TRA updateLine()
        // ------------------------------------------------
        System.err.println("\n--- NHOM 5: KIEM TRA updateLine() ---");
 
        MetroLine lineTest = new MetroLine("LT", "Tuyen Test");
 
        // Cap nhat ten va status hop le
        admin.updateLine(lineTest, "Tuyen Da Cap Nhat", LineStatus.ACTIVE);
        System.out.println("[Ten moi hop le] ten dung : " + lineTest.getLineName().equals("Tuyen Da Cap Nhat"));
        System.out.println("[Status moi] status dung : " + (lineTest.getStatus() == LineStatus.ACTIVE));
 
        // Ten null -> giu ten cu
        String tenTruoc = lineTest.getLineName();
        admin.updateLine(lineTest, null, LineStatus.MAINTENANCE);
        System.out.println("[Ten null] giu ten cu      : " + lineTest.getLineName().equals(tenTruoc));
        System.out.println("[Ten null] status van doi  : " + (lineTest.getStatus() == LineStatus.MAINTENANCE));
 
        // Ten rong (khoang trang) -> giu ten cu
        admin.updateLine(lineTest, "   ", LineStatus.ACTIVE);
        System.out.println("[Ten rong] giu ten cu : " + lineTest.getLineName().equals(tenTruoc));
 
        // Line null -> khong crash
        System.out.print("[Line null] khong crash : ");
        admin.updateLine(null, "Ten Moi", LineStatus.ACTIVE);
        System.out.println("true");
 
        // ------------------------------------------------
        // NHOM 6: KIEM TRA requestRevenueReport()
        // ------------------------------------------------
        System.err.println("\n--- NHOM 6: KIEM TRA requestRevenueReport() ---");
 
        // dateRange hop le -> tra ve map co du lieu
        Map<TicketType, Double> baocao = admin.requestRevenueReport("2025-01");
        System.out.println("[dateRange hop le] != null : " + (baocao != null));
        System.out.println("[dateRange hop le] co it nhat 1  : " + (baocao != null && baocao.size() >= 1));
 
        // dateRange null -> tra ve null
        Map<TicketType, Double> bcNull = admin.requestRevenueReport(null);
        System.out.println("[dateRange null]   tra ve null   : " + (bcNull == null));
 
        // dateRange rong -> tra ve null
        Map<TicketType, Double> bcRong = admin.requestRevenueReport("   ");
        System.out.println("[dateRange rong]   tra ve null   : " + (bcRong == null));
 
        // ------------------------------------------------
        // NHOM 7: KIEM TRA requestHeatmapReport()
        // ------------------------------------------------
        System.err.println("\n--- NHOM 7: KIEM TRA requestHeatmapReport() ---");
 
        // Tao du lieu: nhan vien dang ky + check-in de kich canh bao
        MetroLine lineHM = new MetroLine("LH", "Tuyen Heatmap");
        Station gaHM = new Station("SH1", "Ga Heatmap Test", lineHM, 10);
        StationStaff staff = new StationStaff("ST001", "Nhan Vien 1", "pass", "ST001");
      //  HeatmapService.getInstance().attach(staff);
 
        // Check-in qua nguong 50%
        for (int i = 0; i < 6; i++) gaHM.incrementCheckIn();
        HeatmapService.getInstance().analyzeRealtime(gaHM);
 
        // Lay bao cao -> co du lieu
        List<HeatmapAlert> report = admin.requestHeatmapReport();
        System.out.println("[Sau check-in] report != null: " + (report != null));
        System.out.println("[Sau check-in] co it nhat 1 alt : " + (report != null && report.size() >= 1));
 
        // Canh bao chua dung ga
        boolean dungGa = report != null && report.stream()
                .anyMatch(a -> a.getStation().getStationName().equals("Ga Heatmap Test"));
        System.out.println("[Canh bao] dung station : " + dungGa);
 
        // Goi lien tiep lan 2 -> khong crash
        System.out.print("[Goi lan 2] khong crash : ");
        admin.requestHeatmapReport();
        System.out.println("true");
 
        System.out.println("\n========================================");
        System.out.println("    KIEM TRA HOAN TAT");
        System.out.println("========================================");
    }
}
