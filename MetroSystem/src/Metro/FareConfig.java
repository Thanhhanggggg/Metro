package Metro;

import java.time.LocalDate;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

public class FareConfig {
	private String configId;// ma cau hinh
	private double baseFare;
	private double farePerStop;// gia moi tram
	private double maxFare;// gia toi da so voi ve tram
	private double fixedPriceDaily;
	private double fixedPriceMonthly;
	private Map<PassengerType, Double> discountRate;
	private LocalDate effectiveDate;
	private static FareConfig uniqueInstance;

	public FareConfig(String configId) {
		super();
		this.configId = configId;
		this.baseFare = 7000;// qua 1 ga
		this.farePerStop = 10000;// qua 3 ga tro len
		this.maxFare = 20000;
		this.fixedPriceDaily = 40000;
		this.fixedPriceMonthly = 300000;
		this.discountRate = new HashMap<>();// bang chiet khau

		discountRate.put(PassengerType.NORMAL, 1.0); // Người thuong: 100% gia
		discountRate.put(PassengerType.STUDENT, 0.7); // Sinh vien: 70% gia
		discountRate.put(PassengerType.SENIOR, 0.5); // Nguoi cao tuoi: 50% gia
		discountRate.put(PassengerType.DISABLE, 0.0);

		this.effectiveDate = LocalDate.now();// co hieu luc tu ngay hom nay theo lich hien hanh
	}

	public FareConfig() {
		super();
		this.configId = configId;
		this.baseFare = 7000;// qua 1 ga
		this.farePerStop = 10000;// qua 3 ga tro len
		this.maxFare = 25000;
		this.fixedPriceDaily = 40000;
		this.fixedPriceMonthly = 300000;
		this.discountRate = new HashMap<>();// bang chiet khau

		discountRate.put(PassengerType.NORMAL, 1.0); // Người thuong: 100% gia
		discountRate.put(PassengerType.STUDENT, 0.7); // Sinh vien: 70% gia
		discountRate.put(PassengerType.SENIOR, 0.5); // Nguoi cao tuoi: 50% gia
		discountRate.put(PassengerType.DISABLE, 0.0);

		this.effectiveDate = LocalDate.now();// co hieu luc tu ngay hom nay theo lich hien hanh
	}

	public String getConfigId() {
		return configId;
	}

	public double getBaseFare() {
		return baseFare;
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

	// Methods
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
//		double rawFare;
        double rawFare = Math.min(baseFare + (stops * farePerStop), maxFare);
//		if (stops <= 3) {
//			rawFare = 10000; // 1 - 3 tram
//		} else if (stops <= 6) {
//			rawFare = 15000; // 4 - 6 tram
//		} else if (stops <= 9) {
//			rawFare = 20000; // 7 - 9 tram
//		} else {
//			rawFare = 25000; // 10 tram tro len
//		}
		
		double rate = discountRate.getOrDefault(passengerType, 1.0);
		double finalFare = rawFare * rate;

		System.out.println("Tinh gia " + stops + " tram | " + passengerType + " | " + (int) rawFare + " x "
				+ (int) (rate * 100) + "% = " + (int) finalFare + " VND");

		return finalFare;
	}

	// Lap muc chiet khau cua mot loai hanh khach
	public double getDiscount(PassengerType type) {
		return discountRate.getOrDefault(type, 1.0);
	}

	// Dieu chinh chiet khau cho 1 loai hanh khach
	public void setDiscount(PassengerType type, double rate) {
		// gia co muc chiet khau trong khoang [0.0, 1.0]
		if (rate < 0 || rate > 1) {
			throw new IllegalArgumentException("Rate khong hop le, phai nam trong khoang [0.0, 1.0]");
		}
		discountRate.put(type, rate);
		System.out.println("Chiet khau " + type + " cap nhat: " + (int) (rate * 100) + "%");
	}

	// Cap nhat gia ve co ban
//	public double getBaseFare(double baseFare) {
//		 if (baseFare > 0) {
//	            this.baseFare = baseFare;
//	            System.out.printf("Cap nhat gia co ban"+ baseFare +" VND");
//	        } else {
//	            System.out.println("Gia co ban phai > 0.");
//	        }
//		return baseFare;
//	}

	// Cap nhat gia moi tram
	public void updateDiscounts(Map<PassengerType, Double> ratesMap) {
		if (ratesMap == null || ratesMap.isEmpty()) {
			System.out.println("Bang chiet khau khong hop le");
			return;
		}

		// gia tat ca truoc khi cap nhat
		for (Map.Entry<PassengerType, Double> entry : ratesMap.entrySet()) {
			if (entry.getValue() < 0 || entry.getValue() > 1) {
				throw new IllegalArgumentException("Rate khong hop le  " + entry.getKey() + ": " + entry.getValue());
			}
		}
		// Tat ca hop le → ghi de bang cu
		this.discountRate.putAll(ratesMap);
		System.out.println("Bang chiet khau da duoc cap nhat toan bo");
	}

	// Cap nhat ve ngay
	public void setFixedPriceDaily(double fixedPriceDaily) {
		if (fixedPriceDaily > 0) {
			this.fixedPriceDaily = fixedPriceDaily;
			System.out.println("Gia ve ngay cap nhat: " + fixedPriceDaily + " VND");
		} else {
			System.out.println("Gia ve ngay phai >0");
		}
	}

	// Cap nhat gia ve co ban
	public void setBaseFare(double baseFare) {
		if (baseFare > 0) {
			this.baseFare = baseFare;
			System.out.printf("Gia ve co ban cap nhat " + baseFare + " VND");
		} else {
			System.out.println("Gia co ban phai > 0.");
		}
	}

	public static void main(String[] args) {
		System.out.println("========================================");
		System.out.println("    KIEM TRA CLASS FARECONFIG");
		System.out.println("========================================");

		// Reset singleton de dam bao moi test deu dung instance sach
		FareConfig config = FareConfig.getInstance();

		// ------------------------------------------------
		// NHOM 1: KIEM TRA SINGLETON
		// ------------------------------------------------
		System.err.println("\n--- NHOM 1: KIEM TRA SINGLETON ---");

		FareConfig config2 = FareConfig.getInstance();
		// Hai lan goi getInstance() phai tra ve cung 1 doi tuong
		System.out.println("getInstance() lan 1 == lan 2: " + (config == config2)); // true

		// ------------------------------------------------
		// NHOM 2: KIEM TRA GIA TRI MAC DINH
		// ------------------------------------------------
		System.err.println("\n--- NHOM 2: KIEM TRA GIA TRI MAC DINH ---");

		System.out.println("baseFare mac dinh        = " + config.getBaseFare()); // 7000.0
		System.out.println("farePerStop mac dinh      = " + config.getFarePerStop()); // 10000.0
		System.out.println("maxFare mac dinh          = " + config.getMaxFare()); // 25000.0
		System.out.println("fixedPriceDaily mac dinh  = " + config.getFixedPriceDaily()); // 40000.0
		System.out.println("fixedPriceMonthly mac dinh= " + config.getFixedPriceMonthly()); // 300000.0

		// ------------------------------------------------
		// NHOM 3: KIEM TRA calculateFare() - TINH GIA VE LUOT
		// ------------------------------------------------
		System.err.println("\n--- NHOM 3: KIEM TRA calculateFare() ---");

		// Khach thuong (NORMAL, rate=1.0)
		System.out.println("[1 tram, NORMAL]  gia = " + config.calculateFare(1, PassengerType.NORMAL)); // 10000
		System.out.println("[3 tram, NORMAL]  gia = " + config.calculateFare(3, PassengerType.NORMAL)); // 10000
		System.out.println("[4 tram, NORMAL]  gia = " + config.calculateFare(4, PassengerType.NORMAL)); // 15000
		System.out.println("[6 tram, NORMAL]  gia = " + config.calculateFare(6, PassengerType.NORMAL)); // 15000
		System.out.println("[7 tram, NORMAL]  gia = " + config.calculateFare(7, PassengerType.NORMAL)); // 20000
		System.out.println("[9 tram, NORMAL]  gia = " + config.calculateFare(9, PassengerType.NORMAL)); // 20000
		System.out.println("[10 tram, NORMAL] gia = " + config.calculateFare(10, PassengerType.NORMAL)); // 25000
		System.out.println("[15 tram, NORMAL] gia = " + config.calculateFare(15, PassengerType.NORMAL)); // 25000

		// Sinh vien (STUDENT, rate=0.7)
		System.out.println("[5 tram, STUDENT] gia = " + config.calculateFare(5, PassengerType.STUDENT)); // 10500

		// Nguoi cao tuoi (SENIOR, rate=0.5)
		System.out.println("[5 tram, SENIOR]  gia = " + config.calculateFare(5, PassengerType.SENIOR)); // 7500

		// Nguoi khuyet tat (DISABLE, rate=0.0)
		System.out.println("[5 tram, DISABLE] gia = " + config.calculateFare(5, PassengerType.DISABLE)); // 0

		// So tram am -> bao loi, tra ve 0
		System.out.println("[-1 tram, NORMAL] gia = " + config.calculateFare(-1, PassengerType.NORMAL)); // 0

		// ------------------------------------------------
		// NHOM 4: KIEM TRA getDiscount() va setDiscount()
		// ------------------------------------------------
		System.err.println("\n--- NHOM 4: KIEM TRA getDiscount() va setDiscount() ---");

		System.out.println("Discount NORMAL ban dau  = " + config.getDiscount(PassengerType.NORMAL)); // 1.0
		System.out.println("Discount STUDENT ban dau = " + config.getDiscount(PassengerType.STUDENT)); // 0.7
		System.out.println("Discount SENIOR ban dau  = " + config.getDiscount(PassengerType.SENIOR)); // 0.5
		System.out.println("Discount DISABLE ban dau = " + config.getDiscount(PassengerType.DISABLE)); // 0.0

		// Cap nhat chiet khau hop le
		config.setDiscount(PassengerType.STUDENT, 0.6);
		System.out.println("Sau setDiscount(STUDENT, 0.6) = " + config.getDiscount(PassengerType.STUDENT)); // 0.6

		// Cap nhat chiet khau khong hop le (> 1.0) -> nem exception
		try {
			config.setDiscount(PassengerType.NORMAL, 1.5);
			System.out.println("setDiscount(NORMAL, 1.5): KHONG nem exception (sai)");
		} catch (IllegalArgumentException e) {
			System.out.println("setDiscount(NORMAL, 1.5): nem exception dung -> " + e.getMessage());
		}

		// Cap nhat chiet khau am -> nem exception
		try {
			config.setDiscount(PassengerType.SENIOR, -0.1);
			System.out.println("setDiscount(SENIOR, -0.1): KHONG nem exception (sai)");
		} catch (IllegalArgumentException e) {
			System.out.println("setDiscount(SENIOR, -0.1): nem exception dung -> " + e.getMessage());
		}

		// ------------------------------------------------
		// NHOM 5: KIEM TRA setBaseFare() va setFarePerStop()
		// ------------------------------------------------
		System.err.println("\n--- NHOM 5: KIEM TRA setBaseFare() va setFarePerStop() ---");

		System.out.println("baseFare truoc khi sua    = " + config.getBaseFare()); // 7000.0
		config.setBaseFare(8000);
		System.out.println("Sau setBaseFare(8000)     = " + config.getBaseFare()); // 8000.0

		// Gia am -> khong cap nhat, giu nguyen
		config.setBaseFare(-500);
		System.out.println("Sau setBaseFare(-500)     = " + config.getBaseFare()); // 8000.0 (giu nguyen)

		System.out.println("farePerStop truoc khi sua = " + config.getFarePerStop()); // 10000.0
		config.setFarePerStop(12000);
		System.out.println("Sau setFarePerStop(12000) = " + config.getFarePerStop()); // 12000.0

		// ------------------------------------------------
		// NHOM 6: KIEM TRA setFixedPriceDaily() va setFixedPriceMonthly()
		// ------------------------------------------------
		System.err.println("\n--- NHOM 6: KIEM TRA setFixedPriceDaily() va setFixedPriceMonthly() ---");

		System.out.println("fixedPriceDaily truoc     = " + config.getFixedPriceDaily()); // 40000.0
		config.setFixedPriceDaily(50000);
		System.out.println("Sau setFixedPriceDaily(50000)   = " + config.getFixedPriceDaily()); // 50000.0

		// Gia am -> khong cap nhat
		config.setFixedPriceDaily(-1000);
		System.out.println("Sau setFixedPriceDaily(-1000)   = " + config.getFixedPriceDaily()); // 50000.0 (giu nguyen)

		System.out.println("fixedPriceMonthly truoc   = " + config.getFixedPriceMonthly()); // 300000.0
		config.setFixedPriceMonthly(350000);
		System.out.println("Sau setFixedPriceMonthly(350000)= " + config.getFixedPriceMonthly()); // 350000.0

		// ------------------------------------------------
		// NHOM 7: KIEM TRA updateDiscounts()
		// ------------------------------------------------
		System.err.println("\n--- NHOM 7: KIEM TRA updateDiscounts() ---");

		java.util.Map<PassengerType, Double> bangMoi = new java.util.HashMap<>();
		bangMoi.put(PassengerType.NORMAL, 1.0);
		bangMoi.put(PassengerType.STUDENT, 0.65);
		bangMoi.put(PassengerType.SENIOR, 0.45);
		bangMoi.put(PassengerType.DISABLE, 0.0);

		config.updateDiscounts(bangMoi);
		System.out.println("Sau updateDiscounts - STUDENT = " + config.getDiscount(PassengerType.STUDENT)); // 0.65
		System.out.println("Sau updateDiscounts - SENIOR  = " + config.getDiscount(PassengerType.SENIOR)); // 0.45

		// Bang rong -> khong cap nhat
		config.updateDiscounts(new java.util.HashMap<>());

		// Bang co rate khong hop le -> nem exception
		java.util.Map<PassengerType, Double> bangSai = new java.util.HashMap<>();
		bangSai.put(PassengerType.NORMAL, 2.0); // > 1.0
		try {
			config.updateDiscounts(bangSai);
			System.out.println("updateDiscounts(rate=2.0): KHONG nem exception (sai)");
		} catch (IllegalArgumentException e) {
			System.out.println("updateDiscounts(rate=2.0): nem exception dung -> " + e.getMessage());
		}

		System.out.println("\n========================================");
		System.out.println("    KIEM TRA HOAN TAT");
		System.out.println("========================================");
	}
}
