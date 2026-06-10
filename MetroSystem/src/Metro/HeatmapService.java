package Metro;

import java.util.*;

//Singleton + Observer
public class HeatmapService implements Subject {
	private static HeatmapService uniqueInstance;
	private List<Observer> observers;
	private double alertThreshold = 0.5;
	private List<HeatmapAlert> alertHistory;
    private HeatmapAlert latestAlert;

	private HeatmapService() {
		this.observers = new ArrayList<>();
		this.alertHistory = new ArrayList<>();
	}

	public static HeatmapService getUniqueInstance() {
		if (uniqueInstance == null) {
			uniqueInstance = new HeatmapService();
		}
		return uniqueInstance;
	}

	public static void setUniqueInstance(HeatmapService uniqueInstance) {
		HeatmapService.uniqueInstance = uniqueInstance;
	}

	public List<Observer> getObservers() {
		return observers;
	}

	public HeatmapAlert getLatestAlert() {
		return latestAlert;
	}

	public void setObservers(List<Observer> observers) {
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
	public void attach(Observer o) {
		// TODO Auto-generated method stub
		 if (!observers.contains(o)) {
	            observers.add(o);
	            System.out.println("Dang ky nhan canh bao thanh cong.");
	        }
	    }

	@Override
	public void detach(Observer o) {
		// TODO Auto-generated method stub
		observers.remove(o);
        System.out.println("Huy dang ky nhan canh bao thanh cong.");
	}

	@Override
	public void notifyObserver() {
		// TODO Auto-generated method stub
		System.out.println("Gui canh bao den " + observers.size() + " observer");
        for (Observer observer : observers) {
            observer.update();
        }
	}

	public void analyzeRealtime(Station station) {
		 double rate = station.getOccupancyRate();
	        AlertLevel level = AlertLevel.fromRate(rate);
	        System.out.printf("[HeatmapService] Ga %-15s | %d/%d nguoi | %.0f%% | %s%n",
	                station.getStationName(),
	                station.getCheckInCount(),
	                station.getCapacity(),
	                rate * 100,
	                level.getMoTa());

	        if (level != AlertLevel.NORMAL) {
	            latestAlert = new HeatmapAlert(station, rate, level);
	            alertHistory.add(latestAlert);
	            notify();
	        }
	}

	public List<HeatmapAlert> getHeatmapReport() {
		System.out.println("===== BAO CAO HEATMAP =====");
        if (alertHistory.isEmpty()) {
            System.out.println("Chua co bao cao nao.");
        } else {
            for (HeatmapAlert a : alertHistory) {
                System.out.println("  " + a);
            }
        }
        System.out.println("  Tong: " + alertHistory.size() + "canh bao");
        System.out.println("===========================");
        return alertHistory;
    
	}

	
	
	
}
