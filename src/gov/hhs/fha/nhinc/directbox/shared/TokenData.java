package gov.hhs.fha.nhinc.directbox.shared;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("serial")
public class TokenData
		implements Serializable {
	
	private String id;
	private Map<String,String> resources;
	
	public static final String TOKEN_LOCATION = "location";
	public static final String TOKEN_PT_ID = "ptId";
	public static final String TOKEN_PT_F_NAME = "ptFName";
	public static final String TOKEN_PT_L_NAME = "ptLName";
	public static final String TOKEN_DOMAIN_ALLERGIES = "allergiesDomain";
	public static final String TOKEN_DOMAIN_MEDS = "medsDomain";
	public static final String TOKEN_DOMAIN_LABS = "labsDomain";
	public static final String TOKEN_DOMAIN_PROCS = "procsDomain";
	public static final String TOKEN_DOMAIN_VITALS = "vitalsDomain";

	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public Map<String, String> getResources() {
		if (resources == null) {
			resources = new HashMap<String,String>();
		}
		return resources;
	}
	public void setResources(Map<String, String> resources) {
		this.resources = resources;
	}
	
}
