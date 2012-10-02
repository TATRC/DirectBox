package gov.hhs.fha.nhinc.directbox.client;

import java.util.LinkedList;
import java.util.List;

public class ReferredPatient {

	private String id = "";
	private String fName = "";
	private String mName = "";
	private String lName = "";
	private String fmpSsn = "";
	private String email = "";
	private String phone = "";
	private List<String> resources;

	public ReferredPatient() { }

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

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

	public String getFmpSsn() {
		return fmpSsn;
	}

	public void setFmpSsn(String fmpSsn) {
		this.fmpSsn = fmpSsn;
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
	
	public List<String> getResources() {
		if (resources == null) {
			resources = new LinkedList<String>();
		}
		
		return resources;
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
		
		return display.toString();
	}
}
