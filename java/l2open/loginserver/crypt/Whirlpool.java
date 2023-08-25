package l2open.loginserver.crypt;

import jonelo.jacksum.JacksumAPI;
import jonelo.jacksum.algorithm.AbstractChecksum;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Logger;

public class Whirlpool implements Crypt
{
	protected static Logger _log = Logger.getLogger(Whirlpool.class.getName());
	private static Whirlpool _instance = new Whirlpool();

	public static Whirlpool getInstance()
	{
		return _instance;
	}

	@Override
	public boolean compare(String password, String expected)
	{
		try
		{
			return encrypt(password).equals(expected);
		}
		catch(NoSuchAlgorithmException nsee)
		{
			_log.warning("Could not check password, algorithm Whirlpool not found! Check jacksum library!");
			return false;
		}
		catch(UnsupportedEncodingException uee)
		{
			_log.warning("Could not check password, UTF-8 is not supported!");
			return false;
		}
	}

	@Override
	public String encrypt(String password) throws NoSuchAlgorithmException, UnsupportedEncodingException
	{
		AbstractChecksum whirlpool2 = JacksumAPI.getChecksumInstance("whirlpool2");
		whirlpool2.setEncoding("BASE64");
		whirlpool2.update(password.getBytes("UTF-8"));
		return whirlpool2.format("#CHECKSUM");
	}
}