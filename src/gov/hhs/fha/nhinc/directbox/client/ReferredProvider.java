package gov.hhs.fha.nhinc.directbox.client;

public class ReferredProvider {

	private String fName = "";
	private String mName = "";
	private String lName = "";
	private String org = "";
	private String email = "";
	private String phone = "";
	private String specialty = "";

	public ReferredProvider() {	}

	public String getFName() {
		return fName;
	}

	public void setFName(String fName) {
		this.fName = fName;
	}

	public String getMName() {
		return mName;
	}

	public void setMName(String mName) {
		this.mName = mName;
	}

	public String getLName() {
		return lName;
	}

	public void setLName(String lName) {
		this.lName = lName;
	}

	public String getOrg() {
		return org;
	}

	public void setOrg(String org) {
		this.org = org;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getSpecialty() {
		return specialty;
	}

	public void setSpecialty(String specialty) {
		this.specialty = specialty;
	}
	
	public String toString() {
		StringBuilder display = new StringBuilder();
		display.append(lName);
		display.append(", ");
		display.append(fName);
		if ((mName != null) && (mName.length() > 0)) {
			display.append(" ");
			display.append(mName.charAt(0));
		}
		
		display.append(" : ");
		if ((org != null) && (org.length() > 0)) {
			display.append(org);
		}

		display.append(" : ");
		if ((phone != null) && (phone.length() > 0)) {
			display.append(phone);
		}

		display.append(" : ");
		if ((email != null) && (email.length() > 0)) {
			display.append(email);
		}

		display.append(" : ");
		if ((specialty != null) && (specialty.length() > 0)) {
			display.append(specialty);
		}

		return display.toString();
	}
}
