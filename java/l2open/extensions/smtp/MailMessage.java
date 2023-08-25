package l2open.extensions.smtp;

/**
 * MailMessage.java
 *
 * @author George Crawford III
 * @version 0.1 12/23/97
 */

import java.util.Vector;

public class MailMessage
{
	/**
	 * Creates a new email message with the specified sender and recipient.
	 *
	 * @param sender	source of this email message.
	 * @param recipient receiver of this email message.
	 * @throws NullPointerException if either the sender
	 *                              or receiver is null.
	 */
	MailMessage(EmailAddress sender, EmailAddress recipient) throws NullPointerException
	{
		if(sender == null || recipient == null)
			throw new NullPointerException("MailMessage");
		this.sender = sender;
		recipients.addElement(recipient);
	}

	/**
	 * Adds another recipient to the list of recipients for this message.
	 *
	 * @param recipient receiver of this message.
	 */
	public void addRecipient(EmailAddress recipient)
	{
		if(recipient != null)
			if(!recipients.contains(recipient))
				recipients.addElement(recipient);
	}

	/**
	 * Returns the text data contained in this MailMessage.
	 *
	 * @return the text data contained in this MailMessage.
	 */
	public String getData()
	{
		return data;
	}

	/**
	 * Returns an array of recipients.
	 *
	 * @return an array of EmailAddresses of the recipients of
	 *         this message.
	 */
	public EmailAddress[] getRecipients()
	{
		EmailAddress[] emailAddresses = new EmailAddress[recipients.size()];
		int index = 0;
		for(Object element : recipients)
			emailAddresses[index++] = (EmailAddress) element;
		return emailAddresses;
	}

	/**
	 * Returns the EmailAddress of the sender.
	 *
	 * @return the EmailAddress of the sender of this message.
	 */
	public EmailAddress getSender()
	{
		return sender;
	}

	/**
	 * Returns the subject of this MailMessage.
	 *
	 * @return the subject of this MailMessage
	 */
	public String getSubject()
	{
		return subject;
	}

	/**
	 * Sets the text data contained in this MailMessage to the
	 * specified string.
	 *
	 * @param data the text data that is to be included in this
	 *             MailMessage.
	 */
	public void setData(String data)
	{
		this.data = data;
	}

	/**
	 * Sets the address of the sender of this MailMessage.
	 *
	 * @param sender the address of the sender of this
	 *               Email Message.
	 */
	public void setSender(EmailAddress sender)
	{
		if(sender != null)
			this.sender = sender;
	}

	/**
	 * Sets the subject of this MailMessage.
	 *
	 * @param the subject of this MailMessage.
	 */
	public void setSubject(String subject)
	{
		if(subject != null)
			this.subject = subject;
	}

	private EmailAddress sender;
	private String subject = "";
	private String data = "";
	private Vector<EmailAddress> recipients = new Vector<EmailAddress>(5);
}
