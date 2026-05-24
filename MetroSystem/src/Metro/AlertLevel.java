package metro;

public enum AlertLevel {
	NORMAL, ATTENTION, WARNING, CRITICAL;

	public static AlertLevel fromRate(double rate) {
		if (rate >= 0.95)
			return CRITICAL;
		if (rate >= 0.80)
			return WARNING;
		if (rate >= 0.50)
			return ATTENTION;
		return NORMAL;
	}

	public String getMoTa() {
		switch (this) {
		case NORMAL:
			return "Bình thường (< 50%)";
		case ATTENTION:
			return "Chú ý (50%-79%)";
		case WARNING:
			return "Cảnh báo (80%-94%)";
		case CRITICAL:
			return "KHẨN CẤP (>= 95%)";
		default:
			return "Không xác định";
		}
	}

	public static void main(String[] args) {
		System.out.println("=== TEST BINH THUONG ===");
		System.out.println("Rate 0.30 -> " + fromRate(0.30));
		System.out.println("Rate 0.55 -> " + fromRate(0.55));
		System.out.println("Rate 0.85 -> " + fromRate(0.85));
		System.out.println("Rate 0.97 -> " + fromRate(0.97));

		System.out.println("=== TEST GIA TRI CHINH XAC TAI NGUONG ===");
		System.out.println("Rate 0.50 -> " + fromRate(0.50));
		System.out.println("Rate 0.80 -> " + fromRate(0.80));
		System.out.println("Rate 0.95 -> " + fromRate(0.95));
		System.out.println("Rate 0.499 -> " + fromRate(0.499));
		System.out.println("Rate 0.799 -> " + fromRate(0.799));
		System.out.println("Rate 0.949 -> " + fromRate(0.949));
		System.out.println("=== TEST NGOAI LE ===");
		// Ga trong bao tri, khong co ai (0%)
		System.out.println("Ga dang bao tri, khong co ai: " + fromRate(0.0));

		// Ga day 100% - qua tai
		System.out.println("Ga qua tai: " + fromRate(1.0));

		// Ga bi bao loi checkInCount > capacity (vuot 100%) - loi phan cung
		System.out.println("Ga bi bao loi checkInCount > capacity (vuot 100%) - loi phan cung: " + fromRate(1.2));

		// Rate am - loi du lieu (checkInCount bi am do bug)
		System.out.println("Rate am: "+fromRate(-0.1));
	}
}
