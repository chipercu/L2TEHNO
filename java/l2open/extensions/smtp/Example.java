package l2open.extensions.smtp;

/**
 * MailTest.java
 *
 * @author George Crawford III
 * @version 0.1 12/27/97
 */

import java.io.IOException;
import java.net.UnknownHostException;

public class Example
{
	public static void main(String[] args)
	{
		try
		{
			EmailAddress user1 = new EmailAddress("gec2", "ra.msstate.edu");
			EmailAddress recp1 = new EmailAddress("crawford", "cs.msstate.edu");
			EmailAddress recp2 = new EmailAddress("george", "erc.msstate.edu");
			MailMessage message = new MailMessage(user1, recp1);
			try
			{
				SMTPMailExchanger exchanger = new SMTPMailExchanger("ra.msstate.edu");
				message.addRecipient(recp2);
				message.setSubject("Hello");
				message.setData("hey!!!\n");
				exchanger.send(message);
			}
			catch(IOException ioe)
			{
				System.err.println(ioe);
			}
			System.out.println("Message Subject: " + message.getSubject());
			System.out.println("Message Data: " + message.getData());
		}
		catch(UnknownHostException uhe)
		{
			System.err.println(uhe);
		}
	}
}
