/*
 *   TERMS AND CONDITIONS FOR USE, REPRODUCTION, AND DISTRIBUTION
 * Copyright (c) 2008, Nationwide Health Information Network (NHIN) Connect. All rights reserved.
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:
 *
 * - Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
 * - Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
 * - Neither the name of the NHIN Connect Project nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 *   END OF TERMS AND CONDITIONS
 */
package gov.hhs.fha.nhinc.directbox.shared;

import java.io.Serializable;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

/**
 * 
 * @author cmatser
 */
@SuppressWarnings("serial")
public class EmailData implements Serializable, Comparable<EmailData> {

	private String emailId;
	private String sender;
	private List<String> recipients;
	private List<String> cc_recipients;
	private Date date;
	private String subject;
	private String content;
	private String contentType;
	private EmailAttachment[] attachments;

	public static final String MAIL_SERVER_INBOX_FOLDER = "INBOX";
	public static final String MAIL_SERVER_SENT_FOLDER = "Sent";

	public EmailData() {
	}

	public String getEmailId() {
		return emailId;
	}

	public void setEmailId(String emailId) {
		this.emailId = emailId;
	}

	public String getSender() {
		return sender;
	}

	public void setSender(String sender) {
		this.sender = sender;
	}

	public List<String> getRecipients() {
		if (recipients == null) {
			recipients = new LinkedList<String>();
		}
		return recipients;
	}

	public void setRecipients(List<String> recipients) {
		this.recipients = recipients;
	}

	public List<String> getCc_recipients() {
		if (cc_recipients == null) {
			cc_recipients = new LinkedList<String>();
		}
		return cc_recipients;
	}

	public void setCc_recipients(List<String> cc_recipients) {
		this.cc_recipients = cc_recipients;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getContentType() {
		return contentType;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	public void setAttachments(EmailAttachment[] attachments) {
		this.attachments = attachments;
	}

	public EmailAttachment[] getAttachments() {
		return attachments;
	}

	@Override
	public int compareTo(EmailData o) {
		return o.date.compareTo(this.date);
	}

}
