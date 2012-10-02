/**
 * 
 */
package gov.hhs.fha.nhinc.directbox.client;

import gov.hhs.fha.nhinc.directbox.shared.TokenData;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Panel;

/**
 * @author cmatser
 * 
 */
public class ConsultWelcome extends Composite {

	private final TokenServiceAsync tokenService = GWT
			.create(TokenService.class);

	private static ConsultWelcomeUiBinder uiBinder = GWT
			.create(ConsultWelcomeUiBinder.class);

	@UiTemplate("ConsultWelcome.ui.xml")
	interface ConsultWelcomeUiBinder extends UiBinder<Panel, ConsultWelcome> {
	}

	@UiField
	Button submitButton;

	@UiField
	Button cancelButton;

	@UiField
	Label statusLabel;

	@UiField
	ListBox referToListBox;

	@UiField
	ListBox ptListBox;

	@UiField
	CheckBox allergiesDomainBox;
	
	@UiField
	CheckBox medsDomainBox;

	@UiField
	CheckBox labsDomainBox;

	@UiField
	CheckBox procsDomainBox;

	@UiField
	CheckBox vitalsDomainBox;

	LinkedList<ReferredProvider> referProviders = new LinkedList<ReferredProvider>();
	LinkedList<ReferredPatient> referPatients = new LinkedList<ReferredPatient>();
	
	/**
	 */
	public ConsultWelcome() {
		initWidget(uiBinder.createAndBindUi(this));
	}

	@UiHandler("submitButton")
	void onSubmitClick(ClickEvent e) {

		showWorking(true);

		final ReferredPatient pt = referPatients.get(ptListBox.getSelectedIndex());
		final String to = referProviders.get(referToListBox.getSelectedIndex()).getEmail();
		final String from = "doctora@direct.stormwoods.info";

		final Map<String, String> resources = new HashMap<String, String>();
		resources.put(TokenData.TOKEN_LOCATION, pt.getResources().get(0));
		resources.put(TokenData.TOKEN_PT_ID, pt.getId());
		resources.put(TokenData.TOKEN_PT_F_NAME, pt.getFName());
		resources.put(TokenData.TOKEN_PT_L_NAME, pt.getLName());

		if (allergiesDomainBox.getValue()) {
			resources.put(TokenData.TOKEN_DOMAIN_ALLERGIES, "Y");
		}

		if (medsDomainBox.getValue()) {
			resources.put(TokenData.TOKEN_DOMAIN_MEDS, "Y");
		}

		if (labsDomainBox.getValue()) {
			resources.put(TokenData.TOKEN_DOMAIN_LABS, "Y");
		}

		if (procsDomainBox.getValue()) {
			resources.put(TokenData.TOKEN_DOMAIN_PROCS, "Y");
		}

		if (vitalsDomainBox.getValue()) {
			resources.put(TokenData.TOKEN_DOMAIN_VITALS, "Y");
		}

		tokenService.createToken(to, resources, new AsyncCallback<String>() {
			public void onFailure(Throwable caught) {
				caught.printStackTrace();
				Window.alert("Token error: " + caught.getMessage());
				showWorking(false);
			}

			public void onSuccess(String result) {
				showWorking(false);
				Window.Location.assign(GWT.getHostPageBaseURL() 
						+ "?page=send"
						+ "&from=" + from
						+ "&to=" + to
						+ "&token=" + result);
			}
		});

	}

	@UiHandler("cancelButton")
	void onCancelClick(ClickEvent e) {
		Window.Location.assign(GWT.getHostPageBaseURL() + "?page=inbox");
	}
	
	public void init(String providerId, String patientId) {
		buildReferredProviders();
		buildReferredPatients();
		
		Iterator<ReferredProvider> docIterator = referProviders.iterator();
		while (docIterator.hasNext()) {
			ReferredProvider provider = docIterator.next();
			referToListBox.addItem(provider.toString());
		}
		
		Iterator<ReferredPatient> ptIterator = referPatients.iterator();
		while (ptIterator.hasNext()) {
			ReferredPatient pt = ptIterator.next();
			ptListBox.addItem(pt.toString());
		}
		
		showWorking(false);
	}
	
	/**
	 * Show working status
	 */
	public void showWorking(boolean working) {
		statusLabel.setVisible(working);
	}

	private void buildReferredProviders() {
		ReferredProvider provider = new ReferredProvider();
		provider.setFName("John");
		provider.setLName("Smith");
		provider.setEmail("jsmith@direct.rhex.us");
		provider.setOrg("Mitre");
		provider.setPhone("888-555-1234");
		provider.setSpecialty("Orthopedics");
		referProviders.add(provider);

		provider = new ReferredProvider();
		provider.setFName("Philip");
		provider.setLName("Goldsmith");
		provider.setEmail("philip.goldsmith@direct.jeffplourde.com");
		provider.setOrg("Partners");
		provider.setPhone("888-555-1234");
		provider.setSpecialty("Internal Medicine");
		referProviders.add(provider);

		provider = new ReferredProvider();
		provider.setFName("Doctor");
		provider.setLName("X");
		provider.setEmail("doctorx@direct.jeffplourde.com");
		provider.setOrg("Partners");
		provider.setPhone("888-555-1234");
		provider.setSpecialty("Neurology");
		referProviders.add(provider);

		provider = new ReferredProvider();
		provider.setFName("Bobby");
		provider.setLName("Tables");
		provider.setEmail("bobby.tables@direct.rhex.us");
		provider.setOrg("Mitre");
		provider.setPhone("888-555-1234");
		provider.setSpecialty("Orthopedics");
		referProviders.add(provider);

		provider = new ReferredProvider();
		provider.setFName("Georgia");
		provider.setLName("Canter");
		provider.setEmail("bobby.tables@direct.rhex.us");
		provider.setOrg("Kaiser");
		provider.setPhone("888-555-1234");
		provider.setSpecialty("Internal Medicine");
		referProviders.add(provider);
		
		provider = new ReferredProvider();
		provider.setFName("Adam");
		provider.setLName("Scofer");
		provider.setEmail("bobby.tables@direct.rhex.us");
		provider.setOrg("Scripps");
		provider.setPhone("888-555-1234");
		provider.setSpecialty("Psychiatrics");
		referProviders.add(provider);
		
		provider = new ReferredProvider();
		provider.setFName("Phillys");
		provider.setLName("Danube");
		provider.setEmail("bobby.tables@direct.rhex.us");
		provider.setOrg("Conamaugh");
		provider.setPhone("888-555-1234");
		provider.setSpecialty("Pediatrics");
		referProviders.add(provider);
		
		provider = new ReferredProvider();
		provider.setFName("Carl");
		provider.setLName("Oosterhuis");
		provider.setEmail("bobby.tables@direct.rhex.us");
		provider.setOrg("Mass General");
		provider.setPhone("888-555-1234");
		provider.setSpecialty("Neurology");
		referProviders.add(provider);
		
		provider = new ReferredProvider();
		provider.setFName("Fredericka");
		provider.setLName("Taft");
		provider.setEmail("bobby.tables@direct.rhex.us");
		provider.setOrg("VA");
		provider.setPhone("888-555-1234");
		provider.setSpecialty("Opthamology");
		referProviders.add(provider);

	}

	private void buildReferredPatients() {
		ReferredPatient pt = new ReferredPatient();
		pt.setId("99990070");
		pt.setFName("Randall");
		pt.setLName("Jones");
		pt.getResources().add("rjones_consult.pdf");
		referPatients.add(pt);

		pt = new ReferredPatient();
		pt.setId("99990070");
		pt.setFName("Danica");
		pt.setLName("Woods");
		pt.getResources().add("rjones_consult.pdf");
		referPatients.add(pt);
		
		pt = new ReferredPatient();
		pt.setId("99990070");
		pt.setFName("Anthony");
		pt.setLName("Sparks");
		pt.getResources().add("rjones_consult.pdf");
		referPatients.add(pt);
		
		pt = new ReferredPatient();
		pt.setId("99990070");
		pt.setFName("Nancy");
		pt.setLName("Drew");
		pt.getResources().add("rjones_consult.pdf");
		referPatients.add(pt);
		
		pt = new ReferredPatient();
		pt.setId("99990070");
		pt.setFName("Robert");
		pt.setLName("Jones");
		pt.getResources().add("rjones_consult.pdf");
		referPatients.add(pt);
		
		pt = new ReferredPatient();
		pt.setId("99990070");
		pt.setFName("Bobbi");
		pt.setLName("Zane");
		pt.getResources().add("rjones_consult.pdf");
		referPatients.add(pt);
		
	}

}
