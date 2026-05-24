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
	        // Validate: kiểm tra hai ga có thuộc tuyến này không
	        int stops = getStopCount(s1, s2);
	        if (stops == -1) {
	            System.out.println("❌ Không tính được giá: ga không thuộc tuyến này.");
	            return 0;
	        }
	 
	        // Lấy FareConfig Singleton — nguồn dữ liệu giá duy nhất của hệ thống
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

//		//
//	 public static MetroLine findLineById(String lineId) {
//	        if (lineId == null || lineId.trim().isEmpty()) return null;
//	        // registry.get(lineId): tra cứu theo key → O(1), rất nhanh
//	        return registry.get(lineId.trim());
//	    }
}
