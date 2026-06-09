package Metro;

import java.util.*;

//Singleton + Observer
public class HeatmapService implements Subject {
	private static HeatmapService uniqueInstance;
	private List<StationStaff> observers = new ArrayList<>();
	private double alertThreshold = 0.5;
	private List<HeatmapAlert> alertHistory = new ArrayList<>();

	private HeatmapService() {
		this.observers = new ArrayList<>();
		this.alertHistory = new ArrayList<>();
	}

	public static HeatmapService getUniqueInstance() {
		return uniqueInstance;
	}

	public static void setUniqueInstance(HeatmapService uniqueInstance) {
		HeatmapService.uniqueInstance = uniqueInstance;
	}

	public List<StationStaff> getObservers() {
		return observers;
	}

	public void setObservers(List<StationStaff> observers) {
		this.observers = observers;
	}

	public double getAlertThreshold() {
		return alertThreshold;
	}

	public void setAlertThreshold(double alertThreshold) {
		this.alertThreshold = alertThreshold;
	}

	public List<HeatmapAlert> getAlertHistory() {
		return alertHistory;
	}

	public void setAlertHistory(List<HeatmapAlert> alertHistory) {
		this.alertHistory = alertHistory;
	}

	public static HeatmapService getInstance() {
		if (uniqueInstance == null) {
			uniqueInstance = new HeatmapService();
		}
		return uniqueInstance;

	}

	@Override
	public void attach(StationStaff staff) {
		// TODO Auto-generated method stub
		if (!observers.contains(staff)) {
			observers.add(staff);
			System.out.println("Đăng ký nhận cảnh báo: " + staff.getName());
		}
	}

	@Override
	public void detach(StationStaff staff) {
		// TODO Auto-generated method stub
		observers.remove(staff);
		System.out.println("Hủy đăng ký nhận cảnh báo: " + staff.getName());
	}

	@Override
	public void notifyObservers(HeatmapAlert alert) {
		// TODO Auto-generated method stub
		System.out.println("Gửi cảnh báo đến " + observers.size() + " nhân viên");
		for (StationStaff stationStaff : observers) {
			stationStaff.update(alert);
		}
	}

	public void analyzeRealtime(Station station) {
		double rate = station.getOccupancyRate();
		AlertLevel level = AlertLevel.fromRate(rate);
		System.out.printf("[HeatmapService] Ga %-15s | %d/%d người | %.0f%% | %s%n", station.getStationName(),
				station.getCheckInCount(), station.getCapacity(), rate * 100, level.getMoTa());

		// Chỉ tạo và gửi cảnh báo khi không phải NORMAL
		if (level != AlertLevel.NORMAL) {
			HeatmapAlert alert = new HeatmapAlert(station, rate, level);
			alertHistory.add(alert);
			notifyObservers(alert);
		}
	}

	public List<HeatmapAlert> getHeatmapReport() {
		System.out.println("===== BÁO CÁO HEATMAP =====");
		if (alertHistory.isEmpty()) {
			System.out.println("  Chưa có cảnh báo nào.");
		} else {
			for (HeatmapAlert a : alertHistory) {
				System.out.println("  " + a);
			}
		}
		System.out.println("  Tổng: " + alertHistory.size() + " cảnh báo");
		System.out.println("===========================");
		return alertHistory;
	}
	
	
}
