package Metro;

public class Station {
	private String stationId;
	private String stationName;
	private MetroLine metroLine;
	private int capacity;
	private int checkInCount;
	private boolean isTransfer;
	
	public Station() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	
	public Station(String stationId, String stationName, MetroLine metroLine, int capacity) {
		super();
		this.stationId = stationId;
		this.stationName = stationName;
		this.metroLine = metroLine;
		this.capacity = capacity;
		this.checkInCount = 0;// Ban dau chua co khach nao
		this.isTransfer = false;// Mac dinh khong phai ga chuyen tuyen
	}


	public String getStationId() {
		return stationId;
	}


	public void setStationId(String stationId) {
		this.stationId = stationId;
	}


	public String getStationName() {
		return stationName;
	}


	public void setStationName(String stationName) {
		this.stationName = stationName;
	}


	public MetroLine getMetroLine() {
		return metroLine;
	}


	public void setMetroLine(MetroLine metroLine) {
		this.metroLine = metroLine;
	}


	public int getCheckInCount() {
		return checkInCount;
	}


	public void setCheckInCount(int checkInCount) {
		this.checkInCount = checkInCount;
	}


	public int getCapacity() {
		return capacity;
	}


	public boolean isTransfer() {
		return isTransfer;
	}


	//PHUONG THUC METHOL
	public boolean isOpen() {
		boolean lineActive = (metroLine != null) && (metroLine.getStatus() == LineStatus.ACTIVE);
		boolean validCapacity = capacity > 0;
		return lineActive && validCapacity;
	}
	
	//Kiem tra ga co bi qua tai khong 
	public boolean isOverloaded() {
		return getOccupancyRate() > 0.95;
	}
	
	//Tinh ti le lap day cua ga tai thoi diem hien tai
	//Cong thuc: checkInCount/ capacity
	//Ket qua nay duoc HeatmapService dung de phan loai  muc canh bao 
	//	< 0.5   → NORMAL
	//	0.5–0.8 → ATTENTION
	//	> 0.8   → WARNING
	//	> 0.95  → CRITICAL
	public double getOccupancyRate() {
		if (capacity == 0) {
			// tranh chia cho 0
			return 0.0;
		}
		return (double) checkInCount / capacity;
	}
	
	//Tang so luong khach khi co nguoi Check- in vao ga 
	
	public void incrementCheckIn() {
		if(checkInCount < capacity) {
			checkInCount++;
			System.out.println("["+stationName+ " ]"+" check- in: "
								+ checkInCount + "/"+ capacity 
								+ "("+getOccupancyRate()*100+"%)");
		}else {
			checkInCount++;
			System.out.println("["+stationName+ " ] vuot qua suc chua");
		}
		
	}
	
	//Giam so hanh khach khi co nguoi check out
	public void decrementCheckin() {
		if (checkInCount > 0) {
            checkInCount--;
            System.out.println("[" + stationName + "] Check-out: "
                    	+ checkInCount + "/" + capacity
                    	+ " (" + getOccupancyRate() * 100+"%)");
        } else {
            System.out.println("[" + stationName + "]checkInCount da = 0");
        }
	}
	
	//Tinh so tram dung giu 2 ga tren cung 1 tuyen 
	// uy thac tinh toan cho metroLine 
	public int getStopCount(Station s1, Station s2) {
		if(metroLine == null) {
			return -1;
		}
		//uy thac cho class metroLine 
		return metroLine.getStopCount(s1, s2);	
	}
	
	//Cap nhat suc chua cua ga 
	public void setCapacity(int capacity) {
		if(capacity >0) {
			this.capacity = capacity;
			 System.out.println("[" + stationName + "] cap nhat suc chua: " + capacity);
        } else {
            System.out.println("Suc chua khong hop le (phai > 0)");
        }
	}
	
	//Danh dau ga co pha ga chuyen tuyen hay khong 
	public void setTransfer(boolean isTransfer) {
		this.isTransfer = isTransfer;
		String label = isTransfer ? "ga chuyen tuyen" : "ga thuong";
		System.out.println("[" + stationName + "] Da dat la: " + label);
	}
	public static void main(String[] args) {
		 System.out.println("========================================");
	        System.out.println("    KIEM TRA CLASS STATION");
	        System.out.println("========================================");

	        // Chuan bi du lieu dung chung
	        MetroLine tuyen = new MetroLine("L1", "Tuyen Ben Thanh - Suoi Tien");
	        Station ga1 = new Station("S01", "Ben Thanh", tuyen, 500);
	        Station ga2 = new Station("S02", "Ba Son",    tuyen, 400);
	        Station ga3 = new Station("S03", "Van Thanh", tuyen, 300);
	        tuyen.addStation(ga1);
	        tuyen.addStation(ga2);
	        tuyen.addStation(ga3);

	        // ------------------------------------------------
	        // NHOM 1: KIEM TRA KHOI TAO
	        // ------------------------------------------------
	        System.out.println("\n--- NHOM 1: KIEM TRA KHOI TAO ---");

	        System.out.println("stationId    = " + ga1.getStationId());     // S01
	        System.out.println("stationName  = " + ga1.getStationName());   // Ben Thanh
	        System.out.println("capacity     = " + ga1.getCapacity());      // 500
	        System.out.println("checkInCount = " + ga1.getCheckInCount());  // 0
	        System.out.println("isTransfer   = " + ga1.isTransfer());       // false

	        // ------------------------------------------------
	        // NHOM 2: KIEM TRA isOpen()
	        // ------------------------------------------------
	        System.out.println("\n--- NHOM 2: KIEM TRA isOpen() ---");

	        // Tuyen ACTIVE, sucChua > 0 -> true
	        System.out.println("[Tuyen ACTIVE, sucChua=500] isOpen = " + ga1.isOpen()); // true

	        // sucChua = 0 -> false
	        Station gaKhongChua = new Station("S04", "Ga Khong Chua", tuyen, 0);
	        System.out.println("[sucChua=0]                 isOpen = " + gaKhongChua.isOpen()); // false

	        // Tuyen MAINTENANCE -> false
	        MetroLine tuyenBaoTri = new MetroLine("L2", "Tuyen Bao Tri");
	        tuyenBaoTri.setStatus(LineStatus.MAINTENANCE);
	        Station gaBaoTri = new Station("S05", "Ga Bao Tri", tuyenBaoTri, 200);
	        System.out.println("[Tuyen MAINTENANCE]         isOpen = " + gaBaoTri.isOpen()); // false

	        // MetroLine null -> false
	        Station gaKhongTuyen = new Station("S06", "Ga Null Tuyen", null, 200);
	        System.out.println("[MetroLine null]            isOpen = " + gaKhongTuyen.isOpen()); // false

	        // ------------------------------------------------
	        // NHOM 3: KIEM TRA getOccupancyRate()
	        // ------------------------------------------------
	        System.out.println("\n--- NHOM 3: KIEM TRA getOccupancyRate() ---");

	        Station gaRate = new Station("S07", "Ga Rate", tuyen, 10);

	        // Chua ai check-in -> 0.0
	        System.out.println("[0/100]   occupancyRate = " + gaRate.getOccupancyRate()); // 0.0

	        // Check-in 50 nguoi -> 0.5
	        for (int i = 0; i < 5; i++) gaRate.incrementCheckIn();
	        System.out.println("[50/10]  occupancyRate = " + gaRate.getOccupancyRate()); // 0.5

	        // Check-in them 50 (day 100) -> 1.0
	        for (int i = 0; i < 5; i++) gaRate.incrementCheckIn();
	        System.out.println("[100/100] occupancyRate = " + gaRate.getOccupancyRate()); // 1.0

	        // sucChua = 0 -> tranh chia cho 0, tra ve 0.0
	        System.out.println("[sucChua=0] occupancyRate = " + gaKhongChua.getOccupancyRate()); // 0.0

	        // ------------------------------------------------
	        // NHOM 4: KIEM TRA isOverloaded()
	        // ------------------------------------------------
	        System.out.println("\n--- NHOM 4: KIEM TRA isOverloaded() ---");

	        Station gaQua = new Station("S08", "Ga Qua Tai", tuyen, 10);

	        // Chua ai -> false
	        System.out.println("[0/100]   isOverloaded = " + gaQua.isOverloaded()); // false

	        // 94/100 = 0.94 < 0.95 -> false
	        for (int i = 0; i < 9; i++) gaQua.incrementCheckIn();
	        System.out.println("[94/100]  isOverloaded = " + gaQua.isOverloaded()); // false

	        // 95/100 = 0.95 >= 0.95 -> true
	        gaQua.incrementCheckIn();
	        System.out.println("[95/100]  isOverloaded = " + gaQua.isOverloaded()); // true

	        // ------------------------------------------------
	        // NHOM 5: KIEM TRA incrementCheckIn() va decrementCheckin()
	        // ------------------------------------------------
	        System.out.println("\n--- NHOM 5: KIEM TRA incrementCheckIn / decrementCheckin ---");

	        Station gaTangGiam = new Station("S09", "Ga Tang Giam", tuyen, 10);

	        gaTangGiam.incrementCheckIn(); // 1
	        gaTangGiam.incrementCheckIn(); // 2
	        gaTangGiam.incrementCheckIn(); // 3
	        System.out.println("Sau 3 check-in  -> checkInCount = " + gaTangGiam.getCheckInCount()); // 3

	        gaTangGiam.decrementCheckin();
	        System.out.println("Sau 1 check-out -> checkInCount = " + gaTangGiam.getCheckInCount()); // 2

	        gaTangGiam.decrementCheckin();
	        gaTangGiam.decrementCheckin();
	        gaTangGiam.decrementCheckin(); // goi du thua, kiem tra khong xuong am
	        System.out.println("Sau check-out du thua -> checkInCount = " + gaTangGiam.getCheckInCount()); // 0

	        // ------------------------------------------------
	        // NHOM 6: KIEM TRA setCapacity()
	        // ------------------------------------------------
	        System.out.println("\n--- NHOM 6: KIEM TRA setCapacity() ---");

	        Station gaCapacity = new Station("S10", "Ga Capacity", tuyen, 100);
	        System.out.println("Suc chua ban dau      = " + gaCapacity.getCapacity()); // 100

	        gaCapacity.setCapacity(200);
	        System.out.println("Sau setCapacity(200)  = " + gaCapacity.getCapacity()); // 200

	        gaCapacity.setCapacity(0);
	        System.out.println("Sau setCapacity(0)    = " + gaCapacity.getCapacity()); // 200 (giu nguyen)

	        gaCapacity.setCapacity(-50);
	        System.out.println("Sau setCapacity(-50)  = " + gaCapacity.getCapacity()); // 200 (giu nguyen)

	        // ------------------------------------------------
	        // NHOM 7: KIEM TRA setTransfer()
	        // ------------------------------------------------
	        System.out.println("\n--- NHOM 7: KIEM TRA setTransfer() ---");

	        System.out.println("isTransfer ban dau     = " + ga1.isTransfer()); // false

	        ga1.setTransfer(true);
	        System.out.println("Sau setTransfer(true)  = " + ga1.isTransfer()); // true

	        ga1.setTransfer(false);
	        System.out.println("Sau setTransfer(false) = " + ga1.isTransfer()); // false

	        // ------------------------------------------------
	        // NHOM 8: KIEM TRA getStopCount()
	        // ------------------------------------------------
	        System.out.println("\n--- NHOM 8: KIEM TRA getStopCount() ---");

	        // Ben Thanh -> Van Thanh: 2 tram
	        System.out.println("[Ben Thanh -> Van Thanh] stopCount = " + ga1.getStopCount(ga1, ga3)); // 2

	        // Nguoc chieu: Van Thanh -> Ben Thanh: 2 tram
	        System.out.println("[Van Thanh -> Ben Thanh] stopCount = " + ga3.getStopCount(ga3, ga1)); // 2

	        // Cung 1 ga: 0 tram
	        System.out.println("[Ben Thanh -> Ben Thanh] stopCount = " + ga1.getStopCount(ga1, ga1)); // 0

	        // metroLine null -> -1
	        System.out.println("[metroLine null]         stopCount = " + gaKhongTuyen.getStopCount(ga1, ga2)); // -1

	        System.out.println("\n========================================");
	        System.out.println("    KIEM TRA HOAN TAT");
	        System.out.println("========================================");
	 
	}
}
