package Metro;

public interface Subject {
	public void attach(StationStaff staff);
	public void detach(StationStaff staff);
	public void notifyObservers(HeatmapAlert alert);

}
