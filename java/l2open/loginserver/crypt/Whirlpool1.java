package l2open.loginserver.crypt;

import jonelo.jacksum.JacksumAPI;
import jonelo.jacksum.algorithm.AbstractChecksum;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Logger;

public class Whirlpool1 implements Crypt
{
	protected static Logger _log = Logger.getLogger(Whirlpool1.class.getName());
	private static Whirlpool1 _instance = new Whirlpool1();

	public static Whirlpool1 getInstance()
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
			_log.warning("Could not check password, algorithm Whirlpool1 not found! Check jacksum library!");
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
		AbstractChecksum whirlpool2 = JacksumAPI.getChecksumInstance("whirlpool1");
		whirlpool2.setEncoding("BASE64");
		whirlpool2.update(password.getBytes());
		return whirlpool2.format("#CHECKSUM");
	}
}