package metro;

import java.time.LocalDate;
import java.time.Period;

public class CitizenInfo {
	private String nationalId;
	private String fullName;
	private LocalDate birthDate;
	private boolean isStudent;
	private boolean isDisabled;

	public CitizenInfo(String nationalId, String fullName, LocalDate birthDate, boolean isStudent, boolean isDisabled) {
		this.nationalId = nationalId;
		this.fullName = fullName;
		this.birthDate = birthDate;
		this.isStudent = isStudent;
		this.isDisabled = isDisabled;
	}

	public int getAge() {
		return Period.between(birthDate, LocalDate.now()).getYears();
	}

	public boolean isSenior() {
		return getAge() >= 60;
	}

	public boolean isStudent() {
		return isStudent;
	}

	public boolean isValid() {
		return nationalId != null && !nationalId.isBlank();
	}

	public String getNationalId() {
		return nationalId;
	}

	public String getFullName() {
		return fullName;
	}

	public LocalDate getBirthDate() {
		return birthDate;
	}

	public boolean isDisable() {
		return isDisabled;
	}

	public void setDisable(boolean isDisabled) {
		this.isDisabled = isDisabled;
	}

	@Override
	public String toString() {
		return fullName + " | CCCD: " + nationalId + " | Tuoi: " + getAge() + " | SinhVien: " + isStudent
				+ " | KhuyetTat: " + isDisabled;
	}

	// Test
	public static void main(String[] args) {
		// Sinh viên 21 tuổi
		CitizenInfo sv = new CitizenInfo("SV001", "Nguyen Van A", LocalDate.of(2004, 3, 15), true, false);
		System.out.println(sv);
		System.out.println("isSenior: " + sv.isSenior());
		System.out.println("isStudent: " + sv.isStudent());
		System.out.println("isValid: " + sv.isValid());

		// Người cao tuổi
		CitizenInfo nct = new CitizenInfo("NCT001", "Tran Thi B", LocalDate.of(1958, 1, 1), false, false);
		System.out.println("\n" + nct);
		System.out.println("isSenior: " + nct.isSenior());

		// Người khuyết tật
		CitizenInfo nkt = new CitizenInfo("NKT001", "Le Van C", LocalDate.of(1990, 6, 20), false, true);
		System.out.println("\n" + nkt);
		System.out.println("isDisabled: " + nkt.isDisabled);

		// CCCD rỗng -> không hợp lệ
		CitizenInfo invalid = new CitizenInfo("", "Unknown", LocalDate.of(2000, 1, 1), false, false);
		System.out.println("\nisValid (rong): " + invalid.isValid());

		// CCCD null -> không hợp lệ
		CitizenInfo nullId = new CitizenInfo(null, "Unknown", LocalDate.of(2000, 1, 1), false, false);
		System.out.println("isValid (null): " + nullId.isValid());
	}
}
