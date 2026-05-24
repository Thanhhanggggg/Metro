package metro;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class HeatmapAlert {
	private static int countId = 1;
	private String alertId;
	private Station station;
	private double occupancyRate;
	private AlertLevel alertLevel;
	private LocalDateTime timestamp;
	private boolean acknowledged;

	public HeatmapAlert() {
		super();
		// TODO Auto-generated constructor stub
	}

	public HeatmapAlert(Station station, double occupancyRate, AlertLevel alertLevel) {
		super();
		this.alertId = "ALT-" + String.format("%03d", countId++);
		this.station = station;
		this.occupancyRate = occupancyRate;
		this.alertLevel = alertLevel;
		this.timestamp = LocalDateTime.now();
		this.acknowledged = false;
	}

	public static int getCountId() {
		return countId;
	}

	public static void setCountId(int countId) {
		HeatmapAlert.countId = countId;
	}

	public String getAlertId() {
		return alertId;
	}

	public void setAlertId(String alertId) {
		this.alertId = alertId;
	}

	public Station getStation() {
		return station;
	}

	public void setStation(Station station) {
		this.station = station;
	}

	public double getOccupancyRate() {
		return occupancyRate;
	}

	public void setOccupancyRate(double occupancyRate) {
		this.occupancyRate = occupancyRate;
	}

	public AlertLevel getAlertLevel() {
		return alertLevel;
	}

	public void setAlertLevel(AlertLevel alertLevel) {
		this.alertLevel = alertLevel;
	}

	public LocalDateTime getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(LocalDateTime timestamp) {
		this.timestamp = timestamp;
	}

	public boolean isAcknowledged() {
		return acknowledged;
	}

	public void setAcknowledged(boolean acknowledged) {
		this.acknowledged = acknowledged;
	}

	public void acknowledge() {
		this.acknowledged = true;
		System.out.println(alertId + ": Đã xác nhận xử lý!");
	}

	@Override
	public String toString() {
		DateTimeFormatter fmt = DateTimeFormatter.ofPattern("HH:mm:ss dd/MM");
		return String.format("[%s] Ga: %-15s | Mật độ: %3.0f%% | Mức: %-9s | %s | %s", alertId,
				station.getStationName(), occupancyRate * 100, alertLevel, timestamp.format(fmt),
				acknowledged ? "Xác nhận" : "Chờ xử lý");
	}
	
	//TEST
	public static void main(String[] args) {
		Station station1 = new Station("S01", "Bến Thành", 500);
		HeatmapAlert alert1 = new HeatmapAlert(station1, 0.86, AlertLevel.WARNING);
		HeatmapAlert alert2 = new HeatmapAlert(station1, 0.97, AlertLevel.CRITICAL);
		System.out.println(alert1);
		System.out.println(alert2);
		alert1.acknowledge();
		System.out.println("Sau acknowledge(): " + alert1.isAcknowledged());
		System.out.println(alert1);
	}

}