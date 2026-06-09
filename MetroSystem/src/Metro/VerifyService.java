package Metro;

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
			return PassengerType.DISABLE;
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

	
}
