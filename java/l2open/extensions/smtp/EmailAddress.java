package l2open.extensions.smtp;

/**
 * EmailAddress.java
 *
 * @author George Crawford III
 * @version 0.1 12/23/97
 */

import java.net.InetAddress;
import java.net.UnknownHostException;

public class EmailAddress
{
	/**
	 * Constructs a new EmailAddress with the
	 * specified user I.D. and domain address.
	 *
	 * @param userID		user I.D. portion of the email address.
	 * @param domainAddress domain address portion of the email address.
	 * @throws NullPointerException if the user I.D. or
	 *                              domain name are null.
	 */
	public EmailAddress(String userID, String address) throws NullPointerException, UnknownHostException
	{
		if(userID == null || address == null)
			throw new NullPointerException("EmailAddress");
		this.userID = userID;
		domainAddress = InetAddress.getByName(address);
	}

	/**
	 * Returns the domain address portion of this email address.
	 *
	 * @return the domain address for this email address.
	 */
	public InetAddress getDomainAddress()
	{
		return domainAddress;
	}

	/**
	 * Return the user I.D. portion of this email address.
	 *
	 * @return the user I.D. for this email address.
	 */
	public String getUserID()
	{
		return userID;
	}

	/**
	 * Return the domain address portion of this email address.
	 *
	 * @return the domain address for this email address.
	 */
	public void setDomainAddress(String address) throws UnknownHostException
	{
		if(address != null)
			domainAddress = InetAddress.getByName(address);
	}

	/**
	 * public void setUserID(String userID) {
	 * if(userID != null)
	 * this.userID = userID;
	 * }
	 * /**
	 * Checks that obj is an EmailAddress and
	 * has the same user I.D. and domain address as this
	 * EmailAddress.
	 *
	 * @param obj the object we are testing for equality with this object.
	 * @return true if obj is an instance of
	 *         EmailAddress and has the same user I.D. and domain
	 *         address as this EmailAddress.
	 */
	@Override
	public boolean equals(Object obj)
	{
		if(obj != null)
			if(obj instanceof EmailAddress)
			{
				EmailAddress address = (EmailAddress) obj;
				if(address.userID.equalsIgnoreCase(userID) && address.domainAddress.equals(domainAddress))
					return true;
			}
		return false;
	}

	@Override
	public int hashCode()
	{
		int hash = 3;
		hash = 83 * hash + (domainAddress != null ? domainAddress.hashCode() : 0);
		hash = 83 * hash + (userID != null ? userID.hashCode() : 0);
		return hash;
	}

	private InetAddress domainAddress;
	private String userID;
}