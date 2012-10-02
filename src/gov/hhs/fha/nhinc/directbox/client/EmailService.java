package gov.hhs.fha.nhinc.directbox.client;

import gov.hhs.fha.nhinc.directbox.shared.EmailAttachment;
import gov.hhs.fha.nhinc.directbox.shared.EmailData;

import java.util.List;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * The client side stub for the RPC service.
 */
@RemoteServiceRelativePath("emailservice")
public interface EmailService extends RemoteService {
	List<EmailData> getMail(String folderName, int start, int length, String userId) throws Exception;
	String sendMail(String sender, String[] recipients, String subject, String message, String[] filenames) throws Exception;
	void deleteMsg(String folderName, String uid) throws Exception;
	EmailAttachment[] getAvailableAttachments() throws Exception;
}
