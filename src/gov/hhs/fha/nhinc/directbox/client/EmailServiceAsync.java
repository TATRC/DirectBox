package gov.hhs.fha.nhinc.directbox.client;

import gov.hhs.fha.nhinc.directbox.shared.EmailAttachment;
import gov.hhs.fha.nhinc.directbox.shared.EmailData;

import java.util.List;

import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * The async counterpart of <code>EmailService</code>.
 */
public interface EmailServiceAsync {
	void getMail(String folderName, int start, int length, String userId,
			AsyncCallback<List<EmailData>> callback) throws Exception;

	void sendMail(String sender, String[] recipients, String subject,
			String message, String[] filenames, AsyncCallback<String> callback);

	void deleteMsg(String folderName, String uid, AsyncCallback<Void> callback);

	void getAvailableAttachments(AsyncCallback<EmailAttachment[]> callback);
}
