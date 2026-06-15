package controller;

import Metro.*;
import view.AdminView;
import java.util.*;
import Metro.MetroEventBus;
import Metro.MetroEventBus.Event;

public class AdminController implements IController {

	private Admin admin;
	private AdminView view;

	// Du lieu tuyen / ga luu trong bo nho (demo)
//	private final List<MetroLine> metroLines = new ArrayList<>();
	// FIX
	private List<MetroLine> metroLines = new ArrayList<>();
	public AdminController(Admin admin, AdminView view) {
		this.admin = admin;
		this.view = view;
		seedData();
	}
	public AdminController(Admin admin, AdminView view, List<MetroLine> sharedLines) {
	    this.admin = admin;
	    this.view = view;
	    this.metroLines = sharedLines;
	    // Không gọi seedData() vì Main đã quản lý dữ liệu
	}

	public AdminController() {
		// TODO Auto-generated constructor stub
		this.admin = new Admin("A001", "Trần Văn Lâm", "12345678");
		seedData();
	}

	// Tao du lieu mau ban dau
	private void seedData() {
		MetroLine l1 = new MetroLine("L1", "Ben Thanh - Suoi Tien");
		Station s1 = new Station("S01", "Ben Thanh", l1, 500);
		Station s2 = new Station("S02", "Ba Son", l1, 400);
		Station s3 = new Station("S03", "Van Thanh", l1, 350);
		l1.addStation(s1);
		l1.addStation(s2);
		l1.addStation(s3);

		MetroLine l2 = new MetroLine("L2", "Ben Thanh - Tham Luong");
		Station s4 = new Station("S11", "Ben Thanh", l2, 500);
		Station s5 = new Station("S12", "Pham Van Hai", l2, 300);
		l2.addStation(s4);
		l2.addStation(s5);

		metroLines.add(l1);
		metroLines.add(l2);
		admin.registerLine(l1);
		admin.registerLine(l2);
	}

	public List<MetroLine> getMetroLines() {
		return Collections.unmodifiableList(metroLines);
	}

	// -------------------------------------------------------
	@Override
	public void handleAction(String action, Object... params) {
		switch (action) {
		case "LOAD_LINES" -> view.loadLines(metroLines);
		case "SELECT_LINE" -> handleSelectLine((MetroLine) params[0]);
		case "ADD_LINE" -> handleAddLine((String) params[0]);
		case "UPDATE_LINE" -> handleUpdateLine((MetroLine) params[0], (String) params[1], (LineStatus) params[2]);
		case "REMOVE_LINE" -> handleRemoveLine((MetroLine) params[0]);
		case "ADD_STATION" -> handleAddStation((String) params[0], (MetroLine) params[1], (int) params[2]);
		case "UPDATE_STATION" -> handleUpdateStation((Station) params[0], (String) params[1], (int) params[2]);
		case "REMOVE_STATION" -> handleRemoveStation((Station) params[0], (MetroLine) params[1]);
		case "SET_FARE" ->
			handleSetFare((double) params[0], (double) params[1], (double) params[2], (double) params[3]);
		case "UPDATE_DISCOUNTS" -> handleUpdateDiscounts((Map<PassengerType, Double>) params[0]);
		case "REVENUE_REPORT" -> handleRevenueReport((String) params[0]);
		case "HEATMAP_REPORT" -> handleHeatmapReport();
		default -> view.showError("Hanh dong khong hop le: " + action);
		}
	}

	@Override
	public boolean validate(Object input) {
		if (input == null)
			return false;
		if (input instanceof String s)
			return !s.isBlank();
		return true;
	}

	// Chon tuyen -> hien thi danh sach ga
	private void handleSelectLine(MetroLine line) {
		if (line == null)
			return;
		view.loadStations(line.getStations(), line);
	}

	// Them tuyen moi
	private void handleAddLine(String name) {
		if (!validate(name)) {
			view.showError("Ten tuyen khong duoc trong!");
			return;
		}
		String trimmedName = name.trim();
		for (MetroLine line : metroLines) {
			if (line.getLineName().equalsIgnoreCase(trimmedName)) {
				view.showError("Tuyen '" + trimmedName + "' da ton tai trong he thong!");
				return;
			}
		}
		String id = "L" + (metroLines.size() + 1);
		MetroLine newLine = new MetroLine(id, name.trim());
		metroLines.add(newLine);
		admin.registerLine(newLine);
		view.loadLines(metroLines);
		view.showInfo("Da them tuyen: " + name);
		MetroEventBus.getInstance().publish(Event.LINE_ADDED, newLine);
	}

	// Cap nhat tuyen
	private void handleUpdateLine(MetroLine line, String newName, LineStatus status) {
		if (line == null) {
			view.showError("Chua chon tuyen!");
			return;
		}
		admin.updateLine(line, validate(newName) ? newName : null, status);
		view.loadLines(metroLines);
		view.showInfo("Cap nhat tuyen thanh cong.");
		MetroEventBus.getInstance().publish(Event.LINE_UPDATED, line);
	}
	// Xoa tuyen
	private void handleRemoveLine(MetroLine line) {
		if (line == null) {
			view.showError("Chua chon tuyen!");
			return;
		}
		boolean ok = admin.removeMetroLine(line);
		if (ok) {
			metroLines.remove(line);
			view.loadLines(metroLines);
			view.clearStations();
			view.showInfo("Da xoa tuyen: " + line.getLineName());
	        MetroEventBus.getInstance().publish(Event.LINE_REMOVED, line);
		} else {
			view.showError("Khong the xoa tuyen nay.");
		}
	}
	// Them ga vao tuyen dang chon
	private void handleAddStation(String name, MetroLine line, int capacity) {
		if (!validate(name)) {
			view.showError("Ten ga khong duoc trong!");
			return;
		}
		if (line == null) {
			view.showError("Chua chon tuyen!");
			return;
		}
		if (capacity <= 0) {
			view.showError("Suc chua phai > 0!");
			return;
		}
		String trimmedName = name.trim();
		for (Station s : line.getStations()) {
			if (s.getStationName().equalsIgnoreCase(trimmedName)) {
				view.showError("Ga '" + trimmedName + "' da ton tai trong tuyen nay!");
				return;
			}
		}
		admin.addStation(name.trim(), line, capacity);
		view.loadStations(line.getStations(), line);
		view.showInfo("Da them ga: " + name);
	    MetroEventBus.getInstance().publish(Event.STATION_ADDED, line);
	}

	// Cap nhat ga (ten + suc chua)
	// Cap nhat ga (ten + suc chua)
	private void handleUpdateStation(Station station, String newName, int newCapacity) {
	    if (station == null) {
	        view.showError("Chua chon ga!");
	        return;
	    }
	    if (!validate(newName)) {
	        view.showError("Ten ga khong duoc trong!");
	        return;
	    }
	    if (newCapacity <= 0) {
	        view.showError("Suc chua phai > 0!");
	        return;
	    }
	    // [THEM] Khong cho phep suc chua moi nho hon so khach dang co trong ga
	    if (newCapacity < station.getCheckInCount()) {
	        view.showError("Suc chua moi (" + newCapacity
	                + ") khong duoc nho hon so khach hien co trong ga ("
	                + station.getCheckInCount() + ")!");
	        return;
	    }

	    station.setStationName(newName.trim());
    station.setCapacity(newCapacity);
    // Re-analyze heatmap vi ty le occupancy thay doi khi suc chua thay doi
    if (station.getCheckInCount() > 0) {
        HeatmapService.getInstance().analyzeRealtime(station);
    }
	    // Refresh hien thi
	    MetroLine ownerLine = findLineOf(station);
	    if (ownerLine != null)
	        view.loadStations(ownerLine.getStations(), ownerLine);
	    view.showInfo("Cap nhat ga thanh cong.");
	    MetroEventBus.getInstance().publish(Event.STATION_UPDATED, ownerLine);
	}

	// Xoa ga
	private void handleRemoveStation(Station station, MetroLine line) {
		if (station == null) {
			view.showError("Chua chon ga!");
			return;
		}
		if (line == null) {
			view.showError("Chua chon tuyen!");
			return;
		}
		boolean ok = admin.removeStation(station, line);
		if (ok) {
			view.loadStations(line.getStations(), line);
			view.showInfo("Da xoa ga: " + station.getStationName());
	 MetroEventBus.getInstance().publish(Event.STATION_REMOVED, line);
		} else {
			view.showError("Khong the xoa ga nay.");
		}
	}


	// Cau hinh gia ve
	private void handleSetFare(double base, double perStop, double daily, double monthly) {
	    if (base <= 0 || perStop <= 0 || daily <= 0 || monthly <= 0) {
	        view.showError("Giá vé phải lớn hơn 0!");
	        return;
	    }

	    // BƯỚC 1: Lấy cấu hình hệ thống hiện tại ra để ghi đè số mới vào
	    FareConfig cfg = FareConfig.getInstance();
	    
	    // BƯỚC 2: Cập nhật CÁC GIÁ TRỊ ĐỘNG nhận từ giao diện (Params truyền sang)
	    admin.setFareDetail(base, perStop); // Hàm cập nhật của đối tượng admin
	    cfg.setFixedPriceDaily(daily);      // Cập nhật giá ngày vào config
	    cfg.setFixedPriceMonthly(monthly);  // Cập nhật giá tháng vào config

	    // BƯỚC 3: Tạo nhãn Era tự động gom các thông tin giá vừa nhập trên giao diện
	    String eraLabel = String.format(
	        "Sau cap nhat (base=%,.0f | perStop=%,.0f | daily=%,.0f | monthly=%,.0f)", 
	        base, perStop, daily, monthly
	    );

	    // BƯỚC 4: Đóng dấu mốc thời gian (Era) mới cho TicketManager
	    // Từ giây phút này, bất cứ vé nào mua mới trên giao diện bán vé sẽ mang khung giá này!
	    TicketManager.getInstance().markPriceEra(eraLabel);

	    // Hiển thị thông báo thành công lên màn hình Admin
	    view.showInfo(String.format(
	            "Cap nhat gia thanh cong!\n" + "  Co ban    : %,.0f VND\n" + "  Moi tram  : %,.0f VND\n"
	                    + "  Ve ngay   : %,.0f VND\n" + "  Ve thang  : %,.0f VND",
	            cfg.getBaseFare(), cfg.getFarePerStop(), cfg.getFixedPriceDaily(), cfg.getFixedPriceMonthly()));
	    MetroEventBus.getInstance().publish(Event.FARE_UPDATED, null);
	}
	
	// Cap nhat bang chiet khau
	private void handleUpdateDiscounts(Map<PassengerType, Double> map) {
		if (map == null || map.isEmpty()) {
			view.showError("Bang chiet khau trong!");
			return;
		}
		try {
			admin.updateDiscounts(map);
			view.showInfo("Cap nhat chiet khau thanh cong.");
			MetroEventBus.getInstance().publish(Event.DISCOUNT_UPDATED, null);
		} catch (IllegalArgumentException e) {
			view.showError("He so chiet khau khong hop le (0.0 - 1.0).");
		}
	}

	// Bao cao doanh thu
	private void handleRevenueReport(String dateRange) {
		if (!validate(dateRange)) {
			view.showError("Vui long nhap khoang thoi gian!");
			return;
		}
		Map<TicketType, Double> report = admin.requestRevenueReport(dateRange);
		view.showRevenueReport(report);
	}
	// Bao cao heatmap
	private void handleHeatmapReport() {
	    var report = admin.requestHeatmapReport();
	    view.showHeatmapReport(report, metroLines); // truyen them danh sach tuyen hien tai
	}

	// Tim tuyen chua ga
	private MetroLine findLineOf(Station station) {
		for (MetroLine ml : metroLines) {
			if (ml.getStations().contains(station))
				return ml;
		}
		return null;
	}

	public void registerLine(MetroLine line1) {
		// TODO Auto-generated method stub

	}

	public void setView(AdminView view) {
		this.view = view;
	}
	
}
