package gov.hhs.fha.nhinc.directbox.server;

import gov.hhs.fha.nhinc.directbox.client.EmailService;
import gov.hhs.fha.nhinc.directbox.shared.EmailAttachment;
import gov.hhs.fha.nhinc.directbox.shared.EmailData;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

/**
 * The server side implementation of the RPC service.
 */
@SuppressWarnings("serial")
public class EmailServiceTestImpl extends RemoteServiceServlet implements
		EmailService {

	public List<EmailData> getMail(String folderName, int start, int length, String patientId) 
	throws Exception {
		System.out.println("!!!Getting all docs.");
		List<EmailData> docList = new LinkedList<EmailData>();
		for (int i = 0; i < 7; i++) {
			EmailData docData = new EmailData();
			docData.setEmailId(String.valueOf(i));
			docData.setSender("sender" + i);
			docData.getRecipients().add("recipient");
			docData.setSubject("subject" + i);
			docData.setDate(new Date());
			docData.setContent("data" + i);
			docData.setContentType("text/plain");
			docList.add(docData);
		}	
		System.out.println("!!!Done getting all docs.");

		return docList;
	}

	@Override
	public String sendMail(String sender, String[] recipients, String subject,
			String message, String[] filenames) throws Exception {

		System.out.println("!!!Pretending to send mail:\n"
		    + "\tFrom: " + sender
		    + "\n\tTo: " + recipients[0]
		    + "\n\tSubject: " + subject
		    + "\n\tMessage: " + message
			+ "\n\tAttachments: " + filenames);
		Thread.sleep(5000);
		return "Mail sent.";
	}

	@Override
	public void deleteMsg(String folderName, String uid) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public EmailAttachment[] getAvailableAttachments() throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

}
