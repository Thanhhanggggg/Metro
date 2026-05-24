package Metro;

import java.time.LocalDate;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

public class FareConfig {
	private String configId;
	private double baseFare;
	private double farePerStop;
	private double maxFare;//gia toi da so voi ve tram
	private double fixedPriceDaily;
	private double fixedPriceMonthly;
	private Map<PassengerType, Double> discountRate = new EnumMap<>(PassengerType.class);
	private LocalDate effectiveDate;
	private static FareConfig uniqueInstance  ;
	
	public FareConfig() {
		super();
		this.configId = configId;
		this.baseFare = 7000;// qua 1 ga 
		this.farePerStop = 10000;// qua 3 ga tro len 
		this.maxFare = 20000;
		this.fixedPriceDaily = 40000;
		this.fixedPriceMonthly = 300000;
		this.discountRate = new HashMap<>();// bang chiet khau 
		discountRate.put(PassengerType.NORMAL,   1.0);  // Người thuong: 100% gia
	    discountRate.put(PassengerType.STUDENT,  0.7);  // Sinh vien:    70% gia
	    discountRate.put(PassengerType.SENIOR,   0.5);  // Nguoi cao tuoi: 50% gia
	    discountRate.put(PassengerType.DISABLE, 0.0);
	    
		this.effectiveDate = LocalDate.now();
	}

	public String getConfigId() {
		return configId;
	}

	public void setConfigId(String configId) {
		this.configId = configId;
	}

	public double getFarePerStop() {
		return farePerStop;
	}

	public void setFarePerStop(double farePerStop) {
		this.farePerStop = farePerStop;
	}

	public double getMaxFare() {
		return maxFare;
	}

	public void setMaxFare(double maxFare) {
		this.maxFare = maxFare;
	}

	public double getFixedPriceDaily() {
		return fixedPriceDaily;
	}

	public void setFixedPriceDaily(double fixedPriceDaily) {
		this.fixedPriceDaily = fixedPriceDaily;
	}

	public double getFixedPriceMonthly() {
		return fixedPriceMonthly;
	}

	public void setFixedPriceMonthly(double fixedPriceMonthly) {
		this.fixedPriceMonthly = fixedPriceMonthly;
	}

	public Map<PassengerType, Double> getDiscountRate() {
		return discountRate;
	}

	public void setDiscountRate(Map<PassengerType, Double> discountRate) {
		this.discountRate = discountRate;
	}

	public LocalDate getEffectiveDate() {
		return effectiveDate;
	}

	public void setEffectiveDate(LocalDate effectiveDate) {
		this.effectiveDate = effectiveDate;
	}

	public static FareConfig getUniqueInstance() {
		return uniqueInstance;
	}

	public static void setUniqueInstance(FareConfig uniqueInstance) {
		FareConfig.uniqueInstance = uniqueInstance;
	}

	public void setBaseFare(double baseFare) {
		this.baseFare = baseFare;
	}

	//Methods
	public static FareConfig getInstance() {
		if (uniqueInstance == null) {
			uniqueInstance = new FareConfig();
		}
		return uniqueInstance;
	}
	public double calculateFare(int stops, PassengerType passengerType) {
		if (stops < 0) {
            System.out.println("So tram khong hop le: " + stops);
            return 0;
        }
		//Tinh gia chua chiet khau 
		double rawFare = baseFare + stops * farePerStop;
		//Lay he so chiet khau cua hanh khach 
		//mac dinh 1.0 neu ko co key 
		double rate = discountRate.getOrDefault(passengerType, 1.0);
		//Ap dung chiet khau 
		double discountedFare = rawFare * rate;
		//Ap dung gia tran (khong vuot maxfare)
		double finalFare  = Math.min(discountedFare, maxFare);
		System.out.println("Tinh gia "+ stops+" tram" + ", "+passengerType+", "+finalFare+"VND");
		return finalFare;
	}
	
	//Lap muc chiet khau cua mot loai hanh khach 
	public double getDiscount(PassengerType type) {
		return discountRate.getOrDefault(type, 1.0);
	}
	
	//Dieu chinh chiet khau cho 1 loai hanh khach 
	public void setDiscount(PassengerType type, double rate) {
		// gia co muc chiet khau trong khoang [0.0, 1.0]
		if(rate < 0 || rate >1) {
			throw new IllegalArgumentException("Rate khong hop le, phai nam trong khoang [0.0, 1.0]");
		}
		discountRate.put(type, rate);
		System.out.println("Chiet khau " + type + " cap nhat: " + (int)(rate * 100) + "%");
	}
	
	//Cap nhat gia ve co ban 
//	public double getBaseFare(double baseFare) {
//		 if (baseFare > 0) {
//	            this.baseFare = baseFare;
//	            System.out.printf("Cap nhat gia co ban"+ baseFare +" VND");
//	        } else {
//	            System.out.println("Gia co ban phai > 0.");
//	        }
//		return baseFare;
//	}

	//Cap nhat gia moi tram
	public void updateDiscounts(Map<PassengerType, Double> ratesMap) {
		if (ratesMap == null || ratesMap.isEmpty()) {
            System.out.println("Bang chiet khau khong hop le");
            return;
        }

        // gia tat ca truoc khi cap nhat 
        for (Map.Entry<PassengerType, Double> entry : ratesMap.entrySet()) {
            if (entry.getValue() < 0 || entry.getValue() > 1) {
                throw new IllegalArgumentException(
                        "Rate khong hop le  " + entry.getKey()
                        + ": " + entry.getValue());
            }
        }
        //Tat ca hop le → ghi de bang cu
        this.discountRate.putAll(ratesMap);
        System.out.println("Bang chiet khau da duoc cap nhat toan bo");
    }

	
	
}
