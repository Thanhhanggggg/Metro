package metro;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

public class VerifyService {
	private static VerifyService uniqueInstance;
	private Map<String, CitizenInfo> identityDB;

	private VerifyService() {
		identityDB = new HashMap<>();
	}

	public static VerifyService getInstance() {
		if (uniqueInstance == null) {
			uniqueInstance = new VerifyService();
		}
		return uniqueInstance;
	}

	public void registerCitizen(CitizenInfo info) {
		identityDB.put(info.getNationalId(), info);
	}

	public PassengerType verifyPriority(Passenger passenger, String verificationId) {
		CitizenInfo info = identityDB.get(verificationId);
		if (info == null || !info.isValid()) {
			System.out.println("Verify: Không tìm thấy ID -> NORMAL");
			return PassengerType.NORMAL;
		}
		if (info.isDisable()) {
			System.out.println("Verify: DISABLED");
			return PassengerType.DISABLED;
		}
		if (info.isSenior()) {
			System.out.println("Verify: SENIOR");
			return PassengerType.SENIOR;
		}
		if (info.isStudent()) {
			System.out.println("Verify: STUDENT");
			return PassengerType.STUDENT;
		}
		System.out.println("Verify: NORMAL");
		return PassengerType.NORMAL;
	}

	public boolean isValid(String id) {
		return identityDB.containsKey(id);
	}

	// Test
	public static void main(String[] args) {
		VerifyService vs = VerifyService.getInstance();
		// Test Singleton
		System.out.println("Singleton: " + (vs == VerifyService.getInstance()));
		vs.registerCitizen(new CitizenInfo("SV001", "Nguyen Van A", LocalDate.of(2004, 3, 15), true, false));
		vs.registerCitizen(new CitizenInfo("NCT001", "Tran Thi B", LocalDate.of(1958, 1, 1), false, false));
		vs.registerCitizen(new CitizenInfo("NKT001", "Le Van C", LocalDate.of(1990, 6, 20), false, true));
		vs.registerCitizen(new CitizenInfo("NOR001", "Pham Thi D", LocalDate.of(1995, 5, 10), false, false));
		Passenger p = new Passenger("P-TEST", "Test");

		// Test từng loại
		System.out.println("--- STUDENT ---");
		System.out.println(vs.verifyPriority(p, "SV001"));

		System.out.println("--- SENIOR ---");
		System.out.println(vs.verifyPriority(p, "NCT001"));

		System.out.println("--- DISABLED ---");
		System.out.println(vs.verifyPriority(p, "NKT001"));

		System.out.println("--- NORMAL ---");
		System.out.println(vs.verifyPriority(p, "NOR001"));

		System.out.println("--- ID khong ton tai ---");
		System.out.println(vs.verifyPriority(p, "FAKE"));
		// Test isValid
		System.out.println("\nisValid(SV001): " + vs.isValid("SV001"));
		System.out.println("isValid(FAKE): " + vs.isValid("FAKE"));
	}
}
