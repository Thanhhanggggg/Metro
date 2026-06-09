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
	
	//Tim lo trinh toi uu tu ga 1 den ga 2 tren 1 tuyen cu the
	// kiosk uy thac viec tinh toan cho metroLine ( tuyen metro)
	public List<Station> findRoute(MetroLine metroLine,Station s1, Station s2){
		if(!isAvailble()) {
			return null;
		}
		//Uy thac cho MetroLine tinh toan ( kiosk khong tu tinh)
		List<Station> route = metroLine.findRoute(s1, s2);
		if (route == null) {
			System.out.println("Khong tim thay lo trinh phu hop");
			return null;
			
		}
		//In lo trinh tuyen ra man hinh kiosk 
		System.out.println("Lo trinh tim duoc: "+(route.size() -1) + " tram:" );
		for (int i = 0; i < route.size(); i++) {
			String arrow = (i< route.size()-1) ? " -> ": "(diem den)";
			System.out.print(" "+(i+1) + ". "+ route.get(i).getStationName() + arrow);
		}
		return route;
	}
	
	//Tinh gia ve 
	public double calcFare (MetroLine metroLine, Station s1, Station s2,TicketType ticketType) {
		if(!isAvailble()) {
			return 0;
		}
		// 
		double fare = metroLine.listFare(s1, s2, ticketType);
		System.out.println(" Gia ve tham khao ( "+ ticketType +"): "+fare +" VND" );
		return fare;
	}
	
	// Phat hanh ve sau khi thanh toan 
	//b1: xac thuc doi tuong uu tien (verifySerive )
	//b2: Tinh gia ve (FareCongi - ben trong tickeManager)
	//b3: Hanh khach xac nhan -> thanh toan 
	//b4: Kiosk goi TicketMananger.issueTicket() de tao ve 
	//b5: Hien thi QR code cho khach 
	//input: Hanh khach mua ve, Loai ve muon mua, so tram di ( dung de tinh gia ve single )
	public Ticket issueTicket (Passenger passenger, TicketType ticketType, int stops) {
		if(!isAvailble()) {
			return null;
		}
		//b1
        // VerifyService xac dinh hanh khach la NORMAL / STUDENT / SENIOR / DISABLED
		VerifyService verifyService = VerifyService.getInstance();
		
		//b2 Phat hanh ve thong qua TicketManager
		//TicketMananger se goi TicketFactory de tao dung loai ve
		Ticket ticket = TicketManager.getInstance().issueTicket(passenger, ticketType, stops);
		
		if (ticket ==  null ) {
			System.out.println("Khong tao duoc ve. Thu lai");
			return null;
		}
		//b3 Hien thi ve cho hanh khach
		displayTicket(ticket);
		return ticket;
			
	}
	
	//Hien thi danh sach tat ca tuyen va ga 
	public void displayRouteList(List<MetroLine> metroLineList) {
		//Kiem tra day kiosk co hoat dong hay khong 
		if(!isAvailble()) {
			return;
		}
		System.out.println("\n========== DANH SACH TUYEN METRO ==========");
		if (metroLineList == null || metroLineList == null ) {
			System.out.println("Chua co tuyen nao trong danh sach");
			return;
		}
		//Duyet qua tung tuyen va in thong tin 
		for (MetroLine metroLine : metroLineList) {
			System.out.println("\n"+metroLine.getLineName()
			+ " ["+metroLine.getLineId()
			+"] -"+metroLine.getStatus());
			
			List<Station> stations = metroLine.getStations();
			for (int i = 0; i < stations.size(); i++) {
				Station st = stations.get(i);
				//In so thu tu cho tu + ten danh dau ga chuyen tuyen 
				// ga chuyen tuyen: tuyen Ben Thanh - Suoi Tien , tuyen Ben Thanh - Tham Luong 
				String transferTag = st.isTransfer() ? "ga chuyen tuyen" : "";
				System.out.println(" "+(i+1) +". "+st.getStationName()+transferTag);
			}
		}
		System.out.println("=========================================\n");
	}
	
	//Hien thi thong tin ve va ma QR cho khach hang 
	//chi hien sau khi ve duoc tao thanh cong 
	public void displayTicket (Ticket ticket) {
		if(ticket == null) {
			System.out.println("Khong co ve de hien thi");
			return;
		}
		System.out.println("========== VE METRO - QR CODE ==========");
		System.out.println("Ma ve: "+ticket.getTicketId());
		System.out.println("Loai ve: "+ticket.getType());
		System.out.println("Gia ve: "+ticket.getPrice()+" VND");
		System.out.println("QR Code: "+ticket.generateQR());
		System.out.println("Trang thai: "+ticket.getStatus());
		System.out.println("=========================================\n");
	}
	// PHUONG THUC HO TRO 
	//Kiem tra kiosk co dang hoat dong khong 
	// Neu khong thi bao loi la false de ngan cac thao tac tiep theo tranh bi loi
	// true thi hoat dong binh thuog
	private boolean isAvailble() {
		if(!status) {
			System.out.println("Kiosk [ "+kioskId +" ] tai" + location + " dang tam ngung hoat dong");
			return false;
		}
		return true;
	}

}
