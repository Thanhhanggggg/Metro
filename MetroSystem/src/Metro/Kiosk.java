package Metro;

import java.nio.file.spi.FileSystemProvider;
import java.util.Iterator;
import java.util.List;

public class Kiosk {
	private String kioskId;
	private String location;
	private boolean status;

	public Kiosk() {
		super();
		// TODO Auto-generated constructor stub
	}

	public Kiosk(String kioskId, String location) {
		super();
		this.kioskId = kioskId;
		this.location = location;
		this.status = true;// Mac dinh la dang hoat dong khi vua khoi tao
	}

	public String getKioskId() {
		return kioskId;
	}

	public void setKioskId(String kioskId) {
		this.kioskId = kioskId;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public boolean getStatus() {
		return status;
	}

	public void setStatus(boolean status) {
		this.status = status;
	}

	// PHUONG THUC METHOL

	// Tim lo trinh toi uu tu ga 1 den ga 2 tren 1 tuyen cu the
	// kiosk uy thac viec tinh toan cho metroLine ( tuyen metro)
	public List<Station> findRoute(MetroLine metroLine, Station s1, Station s2) {
		if (!isAvailble()) {
			return null;
		}
		// Uy thac cho MetroLine tinh toan ( kiosk khong tu tinh)
		List<Station> route = metroLine.findRoute(s1, s2);
		if (route == null) {
			System.out.println("Khong tim thay lo trinh phu hop");
			return null;

		}
		// In lo trinh tuyen ra man hinh kiosk
		System.out.println("Lo trinh tim duoc: " + (route.size() - 1) + " tram:");
		for (int i = 0; i < route.size(); i++) {
			String arrow = (i < route.size() - 1) ? " -> " : "(diem den)";
			System.out.print(" " + (i + 1) + ". " + route.get(i).getStationName() + arrow);
		}
		return route;
	}

	// Tinh gia ve
	public double calcFare(MetroLine metroLine, Station s1, Station s2, TicketType ticketType) {
		if (!isAvailble()) {
			return 0;
		}
		//
		double fare = metroLine.listFare(s1, s2, ticketType);
		System.out.println(" Gia ve tham khao ( " + ticketType + "): " + fare + " VND");
		return fare;
	}

	// Phat hanh ve sau khi thanh toan
	// b1: xac thuc doi tuong uu tien (verifySerive )
	// b2: Tinh gia ve (FareCongi - ben trong tickeManager)
	// b3: Hanh khach xac nhan -> thanh toan
	// b4: Kiosk goi TicketMananger.issueTicket() de tao ve
	// b5: Hien thi QR code cho khach
	// input: Hanh khach mua ve, Loai ve muon mua, so tram di ( dung de tinh gia ve
	// single )
	public Ticket issueTicket(Passenger passenger, TicketType ticketType, int stops) {
		if (!isAvailble()) {
			return null;
		}
		// b1
		// VerifyService xac dinh hanh khach la NORMAL / STUDENT / SENIOR / DISABLED
		VerifyService verifyService = VerifyService.getInstance();

		// b2 Phat hanh ve thong qua TicketManager
		// TicketMananger se goi TicketFactory de tao dung loai ve
		Ticket ticket = TicketManager.getInstance().issueTicket(passenger, ticketType, stops);

		if (ticket == null) {
			System.out.println("Khong tao duoc ve. Thu lai");
			return null;
		}
		// b3 Hien thi ve cho hanh khach
		displayTicket(ticket);
		return ticket;

	}

	// Hien thi danh sach tat ca tuyen va ga
	public void displayRouteList(List<MetroLine> metroLineList) {
		// Kiem tra day kiosk co hoat dong hay khong
		if (!isAvailble()) {
			return;
		}
		System.out.println("\n========== DANH SACH TUYEN METRO ==========");
		if (metroLineList == null || metroLineList == null) {
			System.out.println("Chua co tuyen nao trong danh sach");
			return;
		}
		// Duyet qua tung tuyen va in thong tin
		for (MetroLine metroLine : metroLineList) {
			System.out.println(
					"\n" + metroLine.getLineName() + " [" + metroLine.getLineId() + "] -" + metroLine.getStatus());

			List<Station> stations = metroLine.getStations();
			for (int i = 0; i < stations.size(); i++) {
				Station st = stations.get(i);
				// In so thu tu cho tu + ten danh dau ga chuyen tuyen
				// ga chuyen tuyen: tuyen Ben Thanh - Suoi Tien , tuyen Ben Thanh - Tham Luong
				String transferTag = st.isTransfer() ? "ga chuyen tuyen" : "";
				System.out.println(" " + (i + 1) + ". " + st.getStationName() + transferTag);
			}
		}
		System.out.println("=========================================\n");
	}

	// Hien thi thong tin ve va ma QR cho khach hang
	// chi hien sau khi ve duoc tao thanh cong
	public void displayTicket(Ticket ticket) {
		if (ticket == null) {
			System.out.println("Khong co ve de hien thi");
			return;
		}
		System.out.println("========== VE METRO - QR CODE ==========");
		System.out.println("Ma ve: " + ticket.getTicketId());
		System.out.println("Loai ve: " + ticket.getType());
		System.out.println("Gia ve: " + ticket.getPrice() + " VND");
		System.out.println("QR Code: " + ticket.generateQR());
		System.out.println("Trang thai: " + ticket.getStatus());
		System.out.println("=========================================\n");
	}

	// PHUONG THUC HO TRO
	// Kiem tra kiosk co dang hoat dong khong
	// Neu khong thi bao loi la false de ngan cac thao tac tiep theo tranh bi loi
	// true thi hoat dong binh thuog
	private boolean isAvailble() {
		if (!status) {
			System.out.println("Kiosk [ " + kioskId + " ] tai" + location + " dang tam ngung hoat dong");
			return false;
		}
		return true;
	}

	public static void main(String[] args) {
		System.out.println("========================================");
		System.out.println("    KIEM TRA CLASS KIOSK");
		System.out.println("========================================");

		// ------------------------------------------------
		// CHUAN BI DU LIEU DUNG CHUNG
		// ------------------------------------------------
		MetroLine line1 = new MetroLine("L1", "Ben Thanh - Suoi Tien");
		Station benThanh = new Station("S01", "Ben Thanh", line1, 500);
		Station binhThai = new Station("S02", "Binh Thai", line1, 300);
		Station suoiTien = new Station("S03", "Suoi Tien", line1, 400);
		line1.addStation(benThanh);
		line1.addStation(binhThai);
		line1.addStation(suoiTien);

		Passenger normalPass = new Passenger("P001", "Nguyen Van A", PassengerType.NORMAL, "ID001", 200000);
		Passenger studentPass = new Passenger("P002", "Tran Thi B", PassengerType.STUDENT, "ID002", 100000);
		Passenger disabledPass = new Passenger("P003", "Le Van C", PassengerType.DISABLE, "ID003", 50000);

		Kiosk kiosk = new Kiosk("K001", "Ben Thanh");
		Kiosk kioskOff = new Kiosk("K002", "Suoi Tien");
		kioskOff.setStatus(false);

		// ------------------------------------------------
		// NHOM 1: KIEM TRA KHOI TAO
		// ------------------------------------------------
		System.err.println("\n--- NHOM 1: KIEM TRA KHOI TAO ---");

		System.out.println("kioskId   = " + kiosk.getKioskId()); // K001
		System.out.println("location  = " + kiosk.getLocation()); // Ben Thanh
		System.out.println("status    = " + kiosk.getStatus()); // true

		System.out.println("kioskOff status = " + kioskOff.getStatus()); // false

		// ------------------------------------------------
		// NHOM 2: KIEM TRA findRoute()
		// ------------------------------------------------
		System.err.println("\n--- NHOM 2: KIEM TRA findRoute() ---");

		// Kiosk hoat dong, lo trinh hop le xuoi chieu
		var route1 = kiosk.findRoute(line1, benThanh, suoiTien);
		System.out.println("[Hop le xuoi] route != null        : " + (route1 != null)); // true
		System.out.println("[Hop le xuoi] so ga = 3            : " + (route1 != null && route1.size() == 3)); // true
		System.out.println("[Hop le xuoi] ga dau la Ben Thanh  : " + (route1 != null && route1.get(0) == benThanh)); // true
		System.out.println("[Hop le xuoi] ga cuoi la Suoi Tien : " + (route1 != null && route1.get(2) == suoiTien)); // true

		// Lo trinh nguoc chieu
		var route2 = kiosk.findRoute(line1, suoiTien, benThanh);
		System.out.println("[Nguoc chieu] route != null: " + (route2 != null)); // true
		System.out.println("[Nguoc chieu] ga dau la Suoi Tien  : " + (route2 != null && route2.get(0) == suoiTien)); // true

		// Cung 1 ga -> 1 phan tu
		var route3 = kiosk.findRoute(line1, benThanh, benThanh);
		System.out.println("[Cung ga]so phan tu = 1: " + (route3 != null && route3.size() == 1)); // true

		// Ga khong thuoc tuyen -> null
		MetroLine line2 = new MetroLine("L2", "Tuyen Khac");
		Station gaLa = new Station("S99", "Ga La", line2, 100);
		line2.addStation(gaLa);
		var route4 = kiosk.findRoute(line1, benThanh, gaLa);
		System.out.println("[Ga ngoai tuyen] route = null: " + (route4 == null)); // true

		// Kiosk dang tat -> null
		var route5 = kioskOff.findRoute(line1, benThanh, suoiTien);
		System.out.println("[Kiosk off]   route = null: " + (route5 == null)); // true

		// ------------------------------------------------
		// NHOM 3: KIEM TRA calcFare()
		// ------------------------------------------------
		System.err.println("\n--- NHOM 3: KIEM TRA calcFare() ---");

		// Ve luot -> gia > 0
		double fareSingle = kiosk.calcFare(line1, benThanh, suoiTien, TicketType.SINGLE);
		System.out.println("[Ve luot]  fare > 0: " + (fareSingle > 0)); // true

		// Ve ngay -> dung fixedPriceDaily
		double fareDaily = kiosk.calcFare(line1, benThanh, suoiTien, TicketType.DAILY);
		double expectDaily = FareConfig.getInstance().getFixedPriceDaily();
		System.out.println("[Ve ngay]  fare = fixedPriceDaily  : " + (fareDaily == expectDaily)); // true

		// Ve thang -> dung fixedPriceMonthly
		double fareMonthly = kiosk.calcFare(line1, benThanh, suoiTien, TicketType.MONTHLY);
		double expectMonthly = FareConfig.getInstance().getFixedPriceMonthly();
		System.out.println("[Ve thang] fare = fixedPriceMonthly: " + (fareMonthly == expectMonthly)); // true

		// Kiosk tat -> 0
		double fareOff = kioskOff.calcFare(line1, benThanh, suoiTien, TicketType.SINGLE);
		System.out.println("[Kiosk off] fare = 0: " + (fareOff == 0)); // true

		// ------------------------------------------------
		// NHOM 4: KIEM TRA issueTicket()
		// ------------------------------------------------
		System.err.println("\n--- NHOM 4: KIEM TRA issueTicket() ---");

		// Ve luot - hanh khach thuong
		Ticket tSingle = kiosk.issueTicket(normalPass, TicketType.SINGLE, 3);
		System.out.println("[Ve luot]  ticket != null: " + (tSingle != null)); // true
		System.out.println("[Ve luot]  loai = SINGLE: " + (tSingle != null && tSingle.getType() == TicketType.SINGLE)); // true
		System.out.println("[Ve luot]  la SingleTrip: " + (tSingle instanceof SingleTrip)); // true
		System.out.println("[Ve luot]  gia > 0: " + (tSingle != null && tSingle.getPrice() > 0)); // true
		System.out.println("[Ve luot]  hop le sau khi tao: " + (tSingle != null && tSingle.isValid())); // true

		// Ve luot - sinh vien (gia phai thap hon hanh khach thuong)
		Ticket tStudent = kiosk.issueTicket(studentPass, TicketType.SINGLE, 3);
		System.out.println("[Ve luot SV] gia < gia thuong: "
				+ (tStudent != null && tSingle != null && tStudent.getPrice() < tSingle.getPrice())); // true

		// Ve luot - nguoi khuyet tat (mien phi)
		Ticket tDisabled = kiosk.issueTicket(disabledPass, TicketType.SINGLE, 3);
		System.out.println("[Ve luot KT] gia = 0: " + (tDisabled != null && tDisabled.getPrice() == 0)); // true

		// Ve ngay
		Ticket tDaily = kiosk.issueTicket(normalPass, TicketType.DAILY, 0);
		System.out.println("[Ve ngay]  ticket != null: " + (tDaily != null)); // true
		System.out.println("[Ve ngay]  loai = DAILY: " + (tDaily != null && tDaily.getType() == TicketType.DAILY)); // true
		System.out.println("[Ve ngay]  la DayPass: " + (tDaily instanceof DayPass)); // true
		System.out.println("[Ve ngay]  gia = fixedPriceDaily: "
				+ (tDaily != null && tDaily.getPrice() == FareConfig.getInstance().getFixedPriceDaily())); // true

		// Ve thang
		Ticket tMonthly = kiosk.issueTicket(normalPass, TicketType.MONTHLY, 0);
		System.out.println("[Ve thang] ticket != null: " + (tMonthly != null)); // true
		System.out.println("[Ve thang] loai = MONTHLY: "
				+ (tMonthly != null && tMonthly.getType() == TicketType.MONTHLY)); // true
		System.out.println("[Ve thang] la MonthlyPass: " + (tMonthly instanceof MonthlyPass)); // true
		System.out.println("[Ve thang] gia = fixedPriceMonthly  : "
				+ (tMonthly != null && tMonthly.getPrice() == FareConfig.getInstance().getFixedPriceMonthly())); // true

		// Kiosk tat -> null
		Ticket tOff = kioskOff.issueTicket(normalPass, TicketType.SINGLE, 3);
		System.out.println("[Kiosk off] ticket = null: " + (tOff == null)); // true

		// Passenger null -> null hoac exception
		System.out.print("[Passenger null] xu ly an toan: ");
		try {
			Ticket tNull = kiosk.issueTicket(null, TicketType.SINGLE, 3);
			System.out.println(tNull == null); // true neu tra null
		} catch (Exception e) {
			System.out.println("true (Exception: " + e.getClass().getSimpleName() + ")");
		}

		// ------------------------------------------------
		// NHOM 5: KIEM TRA displayTicket()
		// ------------------------------------------------
		System.err.println("\n--- NHOM 5: KIEM TRA displayTicket() ---");

		// Ve hop le -> in thong tin, khong crash
		System.out.println("[Ve hop le] ket qua hien thi:");
		kiosk.displayTicket(tSingle);

		// Ve null -> khong crash
		System.out.print("[Ve null] goi displayTicket(null) khong crash: ");
		kiosk.displayTicket(null);
		System.out.println("true");

		// ------------------------------------------------
		// NHOM 6: KIEM TRA displayRouteList()
		// ------------------------------------------------
		System.err.println("\n--- NHOM 6: KIEM TRA displayRouteList() ---");

		// Co tuyen -> in ra man hinh
		System.out.println("[Co tuyen] ket qua hien thi:");
		kiosk.displayRouteList(java.util.List.of(line1));

		// Danh sach rong -> khong crash
		System.out.print("[List rong] goi displayRouteList([]) khong crash: ");
		kiosk.displayRouteList(java.util.List.of());
		System.out.println("true");

		// Null -> khong crash
		System.out.print("[null]goi displayRouteList(null) khong crash: ");
		kiosk.displayRouteList(null);
		System.out.println("true");

		System.out.println("\n========================================");
		System.out.println("    KIEM TRA HOAN TAT");
		System.out.println("========================================");
	}
}
