/**
 * 
 */
package gov.hhs.fha.nhinc.directbox.client;

import gov.hhs.fha.nhinc.directbox.client.richtext.RichTextToolbar;
import gov.hhs.fha.nhinc.directbox.shared.EmailAttachment;
import gov.hhs.fha.nhinc.directbox.shared.TokenData;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.RichTextArea;
import com.google.gwt.user.client.ui.TextBox;

/**
 * @author cmatser
 * 
 */
public class SendMail extends Composite 
		implements AttachmentsDialogBox.CloseListener {

	/**
	 * Create a remote service proxy to talk to the server-side email service.
	 */
	private final TokenServiceAsync tokenService = GWT
			.create(TokenService.class);

	private final EmailServiceAsync emailService = GWT
			.create(EmailService.class);

	private static SendMailUiBinder uiBinder = GWT
			.create(SendMailUiBinder.class);

	@UiTemplate("SendMail.ui.xml")
	interface SendMailUiBinder extends UiBinder<Panel, SendMail> {
	}

	@UiField
	Button sendButton;
	
	@UiField
	Button discardButton;
	
	@UiField
	Button attachButton;
	
	@UiField
	Label attachLabel;
	
	@UiField
	Label statusLabel;

	@UiField
	Label fromLabel;

	@UiField
	Label toLabel;

	@UiField
	TextBox subjTextBox;

	@UiField
	Grid dataTextGrid;

	RichTextArea dataTextArea;
	
	AttachmentsDialogBox attachBox = new AttachmentsDialogBox(this);
	
	public static final String DIRECT_SUBJECT = "DIRECT: consult request for %s ";

	public static final String DIRECT_MESSAGE =
	    "You are being sent the following consult request for: <strong>%s</strong>"
		+ "<p/>"
        + "Please follow the link to view the consult request.<br/>"
		+ "<a href=\"https://direct.stormwoods.info:8443/ConsultLibrary/service/consult/%s\">https://direct.stormwoods.info:8443/ConsultLibrary/service/consult/%s</a>"
        + "<p/>"
        + "%s"
        + "<p/>"
        + "Thank you.";

	public static final String DOMAIN_ADDED = "Additionally, these domain data feeds are available for you to cut & paste into your feed reader:<br/>\n";
	
	public static final String DOMAIN_ANCHOR = "https://direct.stormwoods.info:8443/greencda/%s/%s/";

	/**
	 */
	public SendMail() {
		initWidget(uiBinder.createAndBindUi(this));
	}

	@UiHandler("sendButton")
	void onSendClick(ClickEvent e) {

		showWorking(true);
		
		String from = fromLabel.getText();
		String to = toLabel.getText();
		emailService.sendMail(from,
				new String[] { to },
				subjTextBox.getText(), dataTextArea.getHTML(), attachBox.getSelected(),
				new AsyncCallback<String>() {
					public void onFailure(Throwable caught) {
						caught.printStackTrace();
						Window.alert("Mail error: " + caught.getMessage());
						showWorking(false);
						Window.Location.assign(GWT.getHostPageBaseURL() + "?page=inbox");
					}

					public void onSuccess(String result) {
						Window.alert(result);
						showWorking(false);
						Window.Location.assign(GWT.getHostPageBaseURL() + "?page=inbox");
					}
				});

		subjTextBox.setEnabled(false);
		dataTextArea.setEnabled(false);
	}

	@UiHandler("discardButton")
	void onDiscardClick(ClickEvent e) {
		Window.Location.assign(GWT.getHostPageBaseURL() + "?page=inbox");
	}

	@UiHandler("attachButton")
	void onAttachClick(ClickEvent e) {
		attachBox.center();
		attachBox.show();
	}

	@Override
	public void onClose() {
		StringBuffer buffer = new StringBuffer();
		String [] filenames = attachBox.getSelected();
		for (int i=0; i < filenames.length; i++) {
			buffer.append(filenames[i]);
			buffer.append(" ");
		}
		attachLabel.setText(buffer.toString());
	}

	public void init(String from, String to, String tokenId) {
		sendButton.setEnabled(false);

		dataTextArea = new RichTextArea();
		dataTextArea.setWidth("100%");
		dataTextArea.addStyleName("dataTextArea");
	    RichTextToolbar toolbar = new RichTextToolbar(dataTextArea);

	    // Add the rich text components to a panel
	    dataTextGrid.resize(2,1);
	    dataTextGrid.setWidget(0, 0, toolbar);
	    dataTextGrid.setWidget(1, 0, dataTextArea);
	    	    
	    fromLabel.setText(from);
		toLabel.setText(to);
		attachLabel.setText("");
		
		tokenService.getTokenData(tokenId, to, new AsyncCallback<TokenData>() {
			public void onFailure(Throwable caught) {
				caught.printStackTrace();
				Window.alert("Token retrieve error: " + caught.getMessage());
				sendButton.setEnabled(false);
				showWorking(false);
			}

			public void onSuccess(TokenData result) {
				showWorking(false);
				sendButton.setEnabled(true);
				String ptFName = result.getResources().get(TokenData.TOKEN_PT_F_NAME);
				String ptLName = result.getResources().get(TokenData.TOKEN_PT_L_NAME);
				String domainInfo = createDomainInfo(result);
				subjTextBox.setText(format(DIRECT_SUBJECT, ptFName + " " + ptLName));
				dataTextArea.setHTML(format(DIRECT_MESSAGE, ptLName + ", " + ptFName, result.getId(), result.getId(), domainInfo));
			}

		});

		emailService.getAvailableAttachments(
			new AsyncCallback<EmailAttachment[]>() {
				public void onFailure(Throwable caught) {
					caught.printStackTrace();
					Window.alert("Mail error: " + caught.getMessage());
				}

				public void onSuccess(EmailAttachment result[]) {
					String[] filenames = new String[result.length];
					for (int i = 0; i < filenames.length; i++) {
						filenames[i] = result[i].getName();
					}
					attachBox.setItems(filenames);
				}
			});

	}
	
	/**
	 * Show working status
	 */
	public void showWorking(boolean working) {
		statusLabel.setVisible(working);
	}

	/**
	 * Inspect token to see if additional domain info is warranted.
	 * 
	 * @return
	 */
	private String createDomainInfo(TokenData token) {
		StringBuilder retVal = new StringBuilder();
		
		String allergiesDomain = token.getResources().get(TokenData.TOKEN_DOMAIN_ALLERGIES);
		String medsDomain = token.getResources().get(TokenData.TOKEN_DOMAIN_MEDS);
		String labsDomain = token.getResources().get(TokenData.TOKEN_DOMAIN_LABS);
		String procsDomain = token.getResources().get(TokenData.TOKEN_DOMAIN_PROCS);
		String vitalsDomain = token.getResources().get(TokenData.TOKEN_DOMAIN_VITALS);

		if ((allergiesDomain != null)
				|| (medsDomain != null)
				|| (labsDomain != null)
				|| (procsDomain != null)
				|| (vitalsDomain != null)) {
			retVal.append(DOMAIN_ADDED);
		}
		
		if (allergiesDomain != null) {
			retVal.append("Allergies: ");
			retVal.append(format(DOMAIN_ANCHOR, token.getId(), "allergies"));
			retVal.append("<br/>\n");
		}
		
		if (medsDomain != null) {
			retVal.append("Medications: ");
			retVal.append(format(DOMAIN_ANCHOR, token.getId(), "meds"));
			retVal.append("<br/>\n");
		}
		
		if (labsDomain != null) {
			retVal.append("Labs: ");
			retVal.append(format(DOMAIN_ANCHOR, token.getId(), "labs"));
			retVal.append("<br/>\n");
		}
		
		if (procsDomain != null) {
			retVal.append("Procedures: ");
			retVal.append(format(DOMAIN_ANCHOR, token.getId(), "procedures"));
			retVal.append("<br/>\n");
		}
		
		if (vitalsDomain != null) {
			retVal.append("Vitals: ");
			retVal.append(format(DOMAIN_ANCHOR, token.getId(), "vitals"));
			retVal.append("<br/>\n");
		}
		
		return retVal.toString();
	}
	
	/**
	 * This method isn't perfrect.  If the string to be replaced is the last string, it won't be included.
	 * 
	 * @param format
	 * @param args
	 * @return
	 */
	public static String format(final String format, final String... args) {
	    String[] split = format.split("%s");
	    final StringBuffer msg = new StringBuffer();
	    for (int pos = 0; pos < split.length - 1; pos += 1) {
	        msg.append(split[pos]);
	        msg.append(args[pos]);
	    }
	    msg.append(split[split.length - 1]);
	    return msg.toString();
	 }

}
