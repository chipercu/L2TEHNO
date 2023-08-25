package l2open.extensions.smtp;

/**
 * SMTPMailExchanger.java
 *
 * @author George Crawford III
 * @version 0.1 12/26/97
 */

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.Socket;

public class SMTPMailExchanger
{
	/**
	 * Constructs a new SMTPMailExchanger
	 * with the specified internet address.
	 *
	 * @param address the address of the SMTP server to connect to.
	 * @throws NullPointerException if address is
	 *                              null.
	 */
	public SMTPMailExchanger(String hostName) throws NullPointerException, IOException
	{
		if(hostName == null)
			throw new NullPointerException("SMTPMailExchanger");
		address = InetAddress.getByName(hostName);
		socket = new Socket(address, 25);
		in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		out = new DataOutputStream(socket.getOutputStream());
	}

	public void send(MailMessage message)
	{
		EmailAddress sender = message.getSender();
		EmailAddress[] recipients = message.getRecipients();
		try
		{
			System.out.println(in.readLine());
			out.writeBytes("mail from: " + sender.getUserID());
			out.writeBytes("@" + sender.getDomainAddress() + "\n");
			System.out.println(in.readLine());
			for(EmailAddress element : recipients)
			{
				out.writeBytes("rcpt to: " + element.getUserID());
				out.writeBytes("@" + element.getDomainAddress() + "\n");
				System.out.println(in.readLine());
			}
			out.writeBytes("data\n");
			out.writeBytes("Subject: " + message.getSubject());
			out.writeBytes(message.getData());
			out.writeBytes("\n.\n");
			System.out.println(in.readLine());
			System.out.println(in.readLine());
			out.writeBytes("quit\n");
		}
		catch(IOException ioe)
		{
			ioe.printStackTrace();
		}
	}

	/**
	 * Closes the connection with the SMTP server.
	 */
	@Override
	public void finalize()
	{
		try
		{
			socket.close();
		}
		catch(IOException ioe)
		{
			ioe.printStackTrace();
		}
	}

	private BufferedReader in;
	private DataOutputStream out;
	private Socket socket;
	private InetAddress address;
}
