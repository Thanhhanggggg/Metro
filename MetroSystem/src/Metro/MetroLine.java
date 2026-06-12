package Metro;

import java.util.ArrayList;
import java.util.List;

public class MetroLine {
	private String lineId;
	private String lineName;
	private List<Station> stations;
	private FareConfig fareConfig;
	private LineStatus status;

	public MetroLine() {
		super();
		// TODO Auto-generated constructor stub
	}

	// Tao doi tuong
	// chi hien thi ten va ma cua chuyen tau
	public MetroLine(String lineId, String lineName) {
		super();
		this.lineId = lineId;
		this.lineName = lineName;
		this.stations = new ArrayList<>();// khoi tao 1 danh sach ga rong
		this.fareConfig = FareConfig.getInstance();
		this.status = LineStatus.ACTIVE;// mac dinh dang hoat dong
	}

	public String getLineId() {
		return lineId;
	}

	public void setLineId(String lineId) {
		this.lineId = lineId;
	}

	public String getLineName() {
		return lineName;
	}

	public void setLineName(String lineName) {
		this.lineName = lineName;
	}

	public void setStations(List<Station> stations) {
		this.stations = stations;
	}

	public FareConfig getFareConfig() {
		return fareConfig;
	}

	public void setFareConfig(FareConfig fareConfig) {
		this.fareConfig = fareConfig;
	}

	public LineStatus getStatus() {
		return status;
	}

	public void setStatus(LineStatus status) {
		this.status = status;
	}

	// PHUONG THUC METHOL
	
	// Tra ve danh sach ga theo thu tu
	// Tao ra ban sao de tranh sua truc tiep vao danh sach goc
	public List<Station> getStations() {
		// tra ve 1 ban sao de bao ve du lieu goc
		return new ArrayList<>(stations);
	}

	// Tim lo trinh
	// b1: Tim vi tri cua s1 va s2 trong danh sach ga( statons)
	// b2: Trich xuat doan con (subList) tu vi tri nho den vi tri lon hon
	// b3: Dao nguoc neu di nguoc chieu
	// Note: s1: ga xuat phat, s2: ga den
	// Danh sach theo thu tu s1-> s2
	public List<Station> findRoute(Station s1, Station s2) {
		// indexOf cho biet so thu tu cua ga do trong danh sach dem tu 0
		// neu khong tim thay thi tra ve null
		int index1 = stations.indexOf(s1);
		int index2 = stations.indexOf(s2);

		// Kiem tra 2 ga co ton tai trong tuyen ko
		if (index1 == -1 || index2 == -1) {
			System.out.println("Mot trong 2 ga khong thuoc tuyen " + lineName);
			return null;
		}
		List<Station> route = new ArrayList<>();

		// Neu di xuoi
		if (index1 <= index2) {
			for (int i = index1; i <= index2; i++) {
				route.add(stations.get(i));
			}
		} else {
			// Di nguoc chieu: lay danh sach roi dao nguoc lai
			for (int i = index1; i >= index2; i--) {
				route.add(stations.get(i));
			}
		}
		return route;

	}
	
	//Tinh gia ve tham khao 
	//Phuong thuc nay uy thac cho FareConfig tinh toan 
	public double listFare(Station s1, Station s2, TicketType ticketType) {
//		int stops= getStopCount(s1,s2);
//		if(stops == -1 ) {
//			System.out.println("Ga khong hop le -> Khong tinh duoc gia");
//			
//		}
//		//Dung FareConfig( Singleton) de tinh gia theo so tram va loai ve
//		return fareConfig.calculateFare(stops, PassengerType.NORMAL);
//
//	}
	        // Validate: kiem tra hai ga co thuoc tuyen nay khong
	        int stops = getStopCount(s1, s2);
	        if (stops == -1) {
	            System.out.println("Khong tinh duoc gia: ga khong thuoc tuyen nay.");
	            return 0;
	        }
	 
	        // Lay FareConfig Singleton — nguon du lieu gia duy nhat cua he thong
	        FareConfig config = FareConfig.getInstance();
	 
	        double fare;
	 
	        switch (ticketType) {
	 
	            case SINGLE:
	                // Ve luot :tinh theo so tram thuc te di 
	                // Cong thuc: baseFare + stops × farePerStop, toi da maxFare
	                // Vi du: 8000 + 5 × 1500 = 15.500 VND
	                // Dung calculateFare() vi no da xu ly 
	                fare = config.calculateFare(stops, PassengerType.NORMAL);
	                break;
	 
	            case DAILY:
	                // Ve ngay: gia co dinh, lay thang tu FareConfig
	                // Khong quan tam di bao nhiêu tram — cu mua la xai ca ngay
	                // fixedPriceDaily dc Admin cau hinh san trong FareConfig
	                fare = config.getFixedPriceDaily();
	                break;
	 
	            case MONTHLY:
	                // Ve thang: gia co dinh, lay thang tu FareConfig
	                // Tuong tu ve ngay nhung hieu luc ca thang
	                // fixedPriceMonthly dc Admin cau hinh san trong FareConfig
	                fare = config.getFixedPriceMonthly();
	                break;
	 
	            default:
	                // Truong hop khong xac dinh → mac dinh tinh nhu ve luot
	                fare = config.calculateFare(stops, PassengerType.NORMAL);
	        }
	 
	        System.out.printf("Gia tham khao, Loai: %-8s | So tram: %2d | Gia: %,.0f VNĐ%n",
	                ticketType, stops, fare);
	        return fare;
	    }
		
	// Them 1 ga vao cuoi danh sach cua tuyen
	public void addStation(Station station) {
		// Kiem tra xem ga da ton tai trong tuyen chua ( tranh trung lap)
		// dung lenh containt de kiem tra xem ga do da co trong danh sach chua
		if (!stations.contains(station)) {
			// them ga va danh sach ga
			stations.add(station);
			System.out.println("Da them ga" + station.getStationName() + "vao tuyen " + lineName);
		} else {
			System.out.println("Ga " + station.getStationName() + " da ton tai trong tuyen");
		}

	}
	// Dem so tram dung 
	public int getStopCount(Station s1, Station s2) {
		int index1 = stations.indexOf(s1);
		int index2 = stations.indexOf(s2);
		if(index1 == -1 || index2 == -1) {
			//bao loi 
			System.out.println("Loi mot trong hai ga khong ton tai trong danh sach");
			return -1;
		}
		//Gia tri tuyet doi du co doi chieu 
		return Math.abs(index2- index1 );
	}
	public boolean removeStation(Station station) {
	    if (station == null) {
	        System.out.println("Station khong hop le (null).");
	        return false;
	    }
	    if (!stations.contains(station)) {
	        System.out.println("Ga " + station.getStationName() + " khong ton tai trong tuyen " + lineName + ".");
	        return false;
	    }
	    stations.remove(station);
	    System.out.println("Da xoa ga " + station.getStationName() + " khoi tuyen " + lineName + ".");
	    return true;
	}

//		//
//	 public static MetroLine findLineById(String lineId) {
//	        if (lineId == null || lineId.trim().isEmpty()) return null;
//	        // registry.get(lineId): tra cứu theo key → O(1), rất nhanh
//	        return registry.get(lineId.trim());
//	    }
	

	public static void main(String[] args) {
	    System.out.println("========= TEST METROLINE =========");

	    MetroLine line = new MetroLine("L1", "Tuyen Ben Thanh - Suoi Tien");

	    // Tao cac ga
	    Station s1 = new Station("S01", "Ben Thanh",    line, 500);
	    Station s2 = new Station("S02", "Nha hat TP",   line, 300);
	    Station s3 = new Station("S03", "Ba Son",        line, 400);
	    Station s4 = new Station("S04", "Van Thanh",     line, 350);
	    Station s5 = new Station("S05", "Tan Cang",      line, 300);

	    // ---- Test addStation ----
	    System.out.println("\n--- TEST addStation ---");
	    line.addStation(s1);
	    line.addStation(s2);
	    line.addStation(s3);
	    line.addStation(s4);
	    line.addStation(s5);
	    System.out.println("Tong so ga: " + line.getStations().size() + " | mong doi: 5");

	    // Them ga trung
	    line.addStation(s1);
	    System.out.println("Sau khi them trung: " + line.getStations().size() + " | mong doi: 5");

	    // ---- Test getStopCount ----
	    System.out.println("\n--- TEST getStopCount ---");
	    System.out.println("s1 -> s3: " + line.getStopCount(s1, s3) + " | mong doi: 2");
	    System.out.println("s1 -> s5: " + line.getStopCount(s1, s5) + " | mong doi: 4");
	    System.out.println("s3 -> s1: " + line.getStopCount(s3, s1) + " | mong doi: 2 (di nguoc)");

	    // Ga khong thuoc tuyen
	    Station sNgoai = new Station("S99", "Ga Ngoai", line, 100);
	    System.out.println("Ga khong thuoc tuyen: " + line.getStopCount(s1, sNgoai) + " | mong doi: -1");

	    // ---- Test findRoute ----
	    System.out.println("\n--- TEST findRoute ---");
	    List<Station> route = line.findRoute(s1, s4);
	    System.out.println("s1 -> s4: " + route.size() + " ga | mong doi: 4");
	    for (Station s : route) System.out.print(s.getStationName() + "\n ");

	    System.out.println("\nRoute nguoc s4 -> s2:");
	    List<Station> routeNguoc = line.findRoute(s4, s2);
	    System.out.println("So ga: " + routeNguoc.size() + " | mong doi: 3");
	    for (Station s : routeNguoc) System.out.print(s.getStationName() + "\n");

	    System.out.println("\nRoute ga khong thuoc tuyen:");
	    List<Station> routeNull = line.findRoute(s1, sNgoai);
	    System.out.println("Ket qua: " + routeNull + " | mong doi: null");

	    // ---- Test listFare ----
	    System.out.println("\n--- TEST listFare ---");
	    line.listFare(s1, s3, TicketType.SINGLE);   // 2 tram -> 10000
	    line.listFare(s1, s5, TicketType.SINGLE);   // 4 tram -> 15000
	    line.listFare(s1, s5, TicketType.DAILY);    // fixed daily
	    line.listFare(s1, s5, TicketType.MONTHLY);  // fixed monthly
	    line.listFare(s1, sNgoai, TicketType.SINGLE); // ga khong hop le -> 0

	    // ---- Test setStatus ----
	    System.out.println("\n--- TEST setStatus ---");
	    System.out.println("Trang thai ban dau: " + line.getStatus() + " | mong doi: ACTIVE");
	    line.setStatus(LineStatus.MAINTENANCE);
	    System.out.println("Sau setStatus: " + line.getStatus() + " | mong doi: MAINTENANCE");

	    System.out.println("\n========= KET THUC TEST METROLINE =========");
	}
}
