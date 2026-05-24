package Metro;

public class Passenger {
	    private String id;
	    private String fullName;
	    private String phone;
	    public Passenger(String id, String fullName, String phone) {
	        this.id = id;
	        this.fullName = fullName;
	        this.phone = phone;
	    }
	    public String getId() {
	        return id;
	    }
	    public String getFullName() {
	        return fullName;
	    }
	}
