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
	
}
