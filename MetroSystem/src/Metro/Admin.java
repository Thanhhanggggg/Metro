package Metro;

import java.util.List;
import java.util.Map;

public class Admin extends Employee{

	public Admin(String employeeId, String name, String password) {
		super(employeeId, name, password);
		// TODO Auto-generated constructor stub
		
	}

	//Cau hinh gia ve co ban va gia moi tram cho toan he thong 
	//gia ve co ban 
	//gia ve moi tram
	public void setFareDetail(double base, double perStop) {
		//gia truoc khi cap nhat
		if(base <= 0 || perStop <= 0 ) {
			System.out.println("Gia khong hop li gia ve hoac gia tram phai > 0 ");
			return;
		}
		//Lay istance duy nhat cua FareConfig (singleton) va cap nhat 
		FareConfig fareConfig = FareConfig.getInstance();
		fareConfig.setBaseFare(base);
		fareConfig.setFarePerStop(perStop);
		System.out.println("Cap nhat bieu gia");
		System.out.println("Gia co ban: "+base +"VND");
		System.out.println("Gia /tram: "+perStop +"VND");
	}
	//Cap nhat Bang chiet khau 
	//NGHIEN CUU THEM 
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
	
	//Them mot ga moi vao 1 tuyen metro
	public void addStation (String name, MetroLine metroLine, int capacity) {
		//gia dau vao
		if(name == null || name.trim().isEmpty()) {
			System.out.println("Ten ga khong duoc de trong ");
			return;
		}
		if(metroLine == null) {
			System.out.println("Tuyen metro khong hop le");
			return;
		}
		if(capacity <= 0) {
			System.out.println("Suc chua phai > 0");
			return;
		}
		//DANG XEM XET LAI
		//Tao ma ga tu dong tu ten tuyen + so thu tu ga hien co 
        String stationId = metroLine.getLineId() + "_S"
                + String.format("%02d", metroLine.getStations().size() + 1);
        //Tao doi tuong Station moi
        Station newStation = new Station(stationId, name, metroLine, capacity);
        //Them vao tuyen (metroLine se kiem tra trung lap ben trong )
        metroLine.addStation(newStation);
        System.out.println("Ga moi "+ name+ " (ID: "+stationId+ " ) da duoc vao tuyen"+ metroLine.getLineName());
	}
	
	//Cap nhat thong tin tuyen Metro
	public void updateLine (MetroLine metroLine, String newName, LineStatus status) {
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
        System.out.println("Tuyen can cap nhat [" + metroLine.getLineId() + "] "
                + metroLine.getLineName() + " — " + metroLine.getStatus());
 
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
            return null ;
        }
 
        System.out.println("Tong hop bao cao doanh thu " + dateRange);
        // Goi TicketManager Singleton de lay bao cao
        Map<TicketType, Integer> report = TicketManager.getInstance().getRevenueReport(dateRange);
 
        System.out.println("Bao cao doanh thu: " + report);
        return report;
    }
	
	//Yeu cau xem bao cao mat do heatmap
	public List<HeatmapAlert> requestHeatmapReport() {
        // Goi HeatmapService Singleton de lay du lieu
		List<HeatmapAlert> report = HeatmapService.getInstance().getHeatmapReport();
 
        System.out.println("Bao cao HeatMap:\n" + report);
        return report;
    }
}