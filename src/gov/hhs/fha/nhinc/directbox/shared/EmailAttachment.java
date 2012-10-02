package gov.hhs.fha.nhinc.directbox.shared;

import java.io.Serializable;

@SuppressWarnings("serial")
public class EmailAttachment implements Serializable {

	private String name;
	private String location;
	private String sizeInfo;
	
	public EmailAttachment() { }
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public String getLocation() {
		return location;
	}
	
	public void setLocation(String location) {
		this.location = location;
	}

	public String getSizeInfo() {
		return sizeInfo;
	}

	public void setSizeInfo(String sizeInfo) {
		this.sizeInfo = sizeInfo;
	}

}
