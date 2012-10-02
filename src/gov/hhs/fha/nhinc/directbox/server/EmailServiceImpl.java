package gov.hhs.fha.nhinc.directbox.server;

import gov.hhs.fha.nhinc.directbox.client.EmailService;
import gov.hhs.fha.nhinc.directbox.shared.EmailAttachment;
import gov.hhs.fha.nhinc.directbox.shared.EmailData;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.FolderNotFoundException;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Part;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.sun.mail.pop3.POP3Folder;

/**
 * The server side implementation of the RPC service.
 */
@SuppressWarnings("serial")
public class EmailServiceImpl extends RemoteServiceServlet implements
		EmailService {

	/** Where attachable files are located, don't include trailing slash. */
	public static final String ATTACHABLE_FILES_DIR = "/home/tomcat/attachments";
//	public static final String ATTACHABLE_FILES_DIR = "f:\\dev";

	/** Where to store message attachments, include trailing slash. */
	public static final String ATTACHMENT_STORE_DIR = "/home/tomcat/attachments/mbox_store/";
//	public static final String ATTACHMENT_STORE_DIR = "f://dev//attach_store//";

	/** URL where rest service is that retrieves attachments, include trailing slash, omit preceding slash. */
	public static final String ATTACHMENT_URL = "attachments/get/";

	/** Logging. */
	private static Log log = LogFactory.getLog(EmailServiceImpl.class);

	public List<EmailData> getMail(String folderName, int start, int length,
			String patientId) throws Exception {
		LinkedList<EmailData> emailList = new LinkedList<EmailData>();
		Store store = null;
		POP3Folder folder = null;

		try {
			String host = "direct.stormwoods.info";
			String username = "doctora";
			String password = "password";

			// Create empty properties
			Properties props = new Properties();

			// Get session
			Session session = null;
			try {
				// First try to get the default (established) instance
				// This can save time, but sometimes errors.
				session = Session.getDefaultInstance(props, null);
			} catch (Exception e) {
				// Create a new session
				session = Session.getInstance(props, null);
			}

			// Get the store
			store = session.getStore("pop3");
			store.connect(host, username, password);

			// Get folder
			folder = (POP3Folder) store.getFolder(folderName);
			folder.open(Folder.READ_ONLY);

			// Get directory
			Message messages[] = folder.getMessages();

			System.out.println("Found messages: " + messages.length);
			log.debug("Found messages: " + messages.length);

			for (int i = 0, n = messages.length; i < n; i++) {
				System.out.println("Retrieving message: " + i);
				EmailData email = new EmailData();
				email.setEmailId(folder.getUID(messages[i]));
				email.setSender(messages[i].getFrom()[0].toString());
				email.getRecipients().add(
						messages[i].getReplyTo()[0].toString());
				email.setSubject(messages[i].getSubject());
				email.setDate(messages[i].getSentDate());
				email.setContent(getMessageText(messages[i]));
				email.setContentType(messages[i].getContentType());
				email.setAttachments(getMessageAttachments(email.getEmailId(), messages[i]));
				emailList.add(email);
			}

		} catch (FolderNotFoundException e) {
			// Empty folder, return empty list
		} catch (Exception e) {
			e.printStackTrace();
			log.error("Error getting emails.", e);
			throw e;
		} finally {
			// Close connection
			if (folder != null) {
				try {
					folder.close(false);
				} catch (Exception e) {
					//do nothing
				}
			}
			if (store != null) {
				try {
					store.close();
				} catch (Exception e) {
					//do nothing
				}
			}
		}

		System.out.println("Done retrieving: " + folderName);
		Collections.sort(emailList);
		return emailList;
	}

	@Override
	public String sendMail(String sender, String[] recipients, String subject,
		String message, String[] filenames) throws Exception {

		Store store = null;
		Folder folder = null;

		try {
			String host = "direct.stormwoods.info";
			final String username = "doctora";
			final String password = "password";

			// Get system properties
			Properties props = System.getProperties();

			// Setup mail server
			props.put("mail.transport.protocol", "smtp");
			props.put("mail.smtp.host", host);
			props.put("mail.smtp.auth", "true");

			// Get session
			Session session = null;
			try {
				// First try to get the default (established) instance
				// This can save time, but sometimes errors.
				session = Session.getDefaultInstance(props,
						new javax.mail.Authenticator() {
							protected PasswordAuthentication getPasswordAuthentication() {
								return new PasswordAuthentication(username,
										password);
							}
						});
			} catch (Exception e) {
				// Create a new session
				session = Session.getInstance(props,
						new javax.mail.Authenticator() {
							protected PasswordAuthentication getPasswordAuthentication() {
								return new PasswordAuthentication(username,
										password);
							}
						});
			}

			// Define message
	        MimeMultipart multipartMsg = new MimeMultipart();
			MimeMessage mimeMessage = new MimeMessage(session);
			mimeMessage.setFrom(new InternetAddress(sender));
			mimeMessage.addRecipient(Message.RecipientType.TO,
					new InternetAddress(recipients[0]));
			mimeMessage.setSubject(subject);

			// Setup message body part
			MimeBodyPart htmlBodyPart = new MimeBodyPart();
			htmlBodyPart.setContent(message, "text/html");
			multipartMsg.addBodyPart(htmlBodyPart);

			// Setup message attachments
	        for (String fileName: filenames) {
	        	String fullPathToFile = ATTACHABLE_FILES_DIR + File.separator + fileName;
	        	File file = new File(fullPathToFile);
	        	if (!file.canRead()) {
	        		log.error("Attachment file not readable: " + fullPathToFile);
	        		continue;
	        	}
	        	
	            MimeBodyPart bp = new MimeBodyPart();
	            FileDataSource fds = new FileDataSource(fullPathToFile);
	            bp.setDataHandler(new DataHandler(fds));
	            bp.setFileName(fds.getName());
	            multipartMsg.addBodyPart(bp);
	        }

	        // Finalize message
	        mimeMessage.setContent(multipartMsg);
//			mimeMessage.setContent(message, "text/html");

			// Send message (bugfix: set class loader before sending to allow handlers to be found)
			Thread.currentThread().setContextClassLoader(getClass().getClassLoader());
			Transport.send(mimeMessage);
			
/*NOT WORKING YET, SUPPOSE TO SAVE COPY IN SENT FOLDER
			// Get the store
			store = session.getStore("imap");
			store.connect(host, username, password);

			// Get "Sent" folder
			folder = store.getFolder(EmailData.MAIL_SERVER_SENT_FOLDER);

			// Create "Sent" folder if it does not exist
			if (!folder.exists()) {
				folder.create(Folder.HOLDS_MESSAGES);
			}

			// Write sent message to folder
			folder.open(Folder.READ_WRITE);
			mimeMessage.setFlag(Flag.SEEN, true);  
			folder.appendMessages(new Message[] { mimeMessage });
			*/
		} catch (Exception e) {
			e.printStackTrace();
			return "Send mail failure: " + e.getMessage();
		} finally {
			// Close connection
			if (folder != null) {
				try {
					folder.close(false);
				} catch (Exception e) {
					//do nothing
				}
			}
			if (store != null) {
				try {
					store.close();
				} catch (Exception e) {
					//do nothing
				}
			}
		}

		return "Mail sent.";
	}

	@Override
	public void deleteMsg(String folderName, String uid) throws Exception {
		Store store = null;
		POP3Folder folder = null;

		try {
			String host = "direct.stormwoods.info";
			String username = "doctora";
			String password = "password";

			// Create empty properties
			Properties props = new Properties();

			// Get session
			Session session = null;
			try {
				// First try to get the default (established) instance
				// This can save time, but sometimes errors.
				session = Session.getDefaultInstance(props, null);
			} catch (Exception e) {
				// Create a new session
				session = Session.getInstance(props, null);
			}

			// Get the store
			store = session.getStore("pop3");
			store.connect(host, username, password);

			// Get folder
			folder = (POP3Folder) store.getFolder("INBOX");
			folder.open(Folder.READ_WRITE);

			// Get directory
			Message messages[] = folder.getMessages();

			System.out.println("Looking for folder: " + folderName + ", msg: "
					+ uid);
			for (int i = 0, n = messages.length; i < n; i++) {
				if (uid.equals(folder.getUID(messages[i]))) {
					System.out.println("Found, deleting.");
					messages[i].setFlag(Flags.Flag.DELETED, true);
				}
			}

		} catch (Exception e) {
			log.error("Error deleting email.", e);
			throw e;
		} finally {
			// Close connection
			if (folder != null) {
				try {
					folder.close(true);
				} catch (Exception e) {
					//do nothing
				}
			}
			if (store != null) {
				try {
					store.close();
				} catch (Exception e) {
					//do nothing
				}
			}
		}

	}

	@Override
	public EmailAttachment[] getAvailableAttachments() throws Exception {
	    List<EmailAttachment> attachList = new LinkedList<EmailAttachment>();

	    try {
			//Get directory contents
		    File dir = new File(ATTACHABLE_FILES_DIR);
		    if (!dir.isDirectory()) {
		    	return new EmailAttachment[0];
		    }
		    
		    File files[] = dir.listFiles();
		    for (int i = 0; i < files.length; i++) {
		    	if (files[i].isFile()) {
			    	EmailAttachment attachment = new EmailAttachment();
		    		attachment.setName(files[i].getName());
		    		StringBuffer buffer = new StringBuffer();
		    		long size = files[i].length();
		    		if (size < 1024) {
		    			buffer.append(size);
			    		buffer.append("B");
		    		}
		    		else if (size < 1048576) {
		    			buffer.append(size / 1024);
			    		buffer.append("K");
		    		}
		    		else {
		    			buffer.append(size / 1048576);
			    		buffer.append("M");
		    		}
		    		attachment.setSizeInfo(buffer.toString());
		    		attachList.add(attachment);
		    	}
		    }
		    
	    } catch (Exception e) {
	    	log.error("Error getting available attachments.", e);
	    }
	    
		return attachList.toArray(new EmailAttachment[attachList.size()]);
	}

	private EmailAttachment[] getMessageAttachments(String msgId, Message msg) 
			throws MessagingException, IOException {

		EmailAttachment attaches[] = null;
		String filenames[];
		
		filenames = saveMessageAttachments(ATTACHMENT_STORE_DIR + msgId + File.separator, msg);
		Arrays.sort(filenames, new MyFileNameComparator());
		attaches = new EmailAttachment[filenames.length];
		for (int i = 0; i < filenames.length; i++) {
			attaches[i] = new EmailAttachment();
			attaches[i].setName(filenames[i]);
			attaches[i].setLocation(ATTACHMENT_URL + msgId + "/" + i);
		}

		return attaches;
	}
	
	private String[] saveMessageAttachments(String storeDir, Part p) 
			throws MessagingException, IOException {
		List<String> retVal = new LinkedList<String>();
		
	     if (p.isMimeType("multipart/*")) {
            Multipart mp = (Multipart)p.getContent();
            for (int i = 0; i < mp.getCount(); i++) {
                String[] attaches = saveMessageAttachments(storeDir, mp.getBodyPart(i));
                for (int j = 0; j < attaches.length; j++) {
                	retVal.add(attaches[j]);
                }
            }
        } else {
	        String disp = p.getDisposition();
	        // many mailers don't include a Content-Disposition
	        if ((disp != null) && disp.equalsIgnoreCase(Part.ATTACHMENT)) {
	        	String attach = p.getFileName();

                File f = new File(storeDir + p.getFileName());
                if (!f.exists()) {
                	File d = new File(storeDir);
                	if (!d.exists()) {
                		d.mkdir();
                	}
	                ((MimeBodyPart)p).saveFile(f);
                }

	            return new String[] { attach };
	        }
        }
		
		return retVal.toArray(new String[retVal.size()]);
	}
	
    /**
     * Return the primary text content of the message.
     */
    private String getMessageText(Part p) throws
                MessagingException, IOException {
        if (p.isMimeType("text/*")) {
            String s = (String)p.getContent();
            return s;
        }

        if (p.isMimeType("multipart/alternative")) {
            // prefer html text over plain text
            Multipart mp = (Multipart)p.getContent();
            String text = null;
            for (int i = 0; i < mp.getCount(); i++) {
                Part bp = mp.getBodyPart(i);
                if (bp.isMimeType("text/plain")) {
                    if (text == null)
                        text = getMessageText(bp);
                    continue;
                } else if (bp.isMimeType("text/html")) {
                    String s = getMessageText(bp);
                    if (s != null)
                        return s;
                } else {
                    return getMessageText(bp);
                }
            }
            return text;
        } else if (p.isMimeType("multipart/*")) {
            Multipart mp = (Multipart)p.getContent();
            for (int i = 0; i < mp.getCount(); i++) {
                String s = getMessageText(mp.getBodyPart(i));
                if (s != null)
                    return s;
            }
        }

        return null;
    }
    
    class MyFileNameComparator implements Comparator<String> {

		@Override
		public int compare(String s1, String s2) {
			return s1.compareToIgnoreCase(s2);
		}
    	
    }
}
