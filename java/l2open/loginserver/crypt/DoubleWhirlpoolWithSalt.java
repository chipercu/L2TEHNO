package l2open.loginserver.crypt;

import jonelo.jacksum.JacksumAPI;
import jonelo.jacksum.algorithm.AbstractChecksum;

import l2open.config.ConfigValue;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Logger;

public class DoubleWhirlpoolWithSalt implements Crypt
{
	protected static Logger _log = Logger.getLogger(DoubleWhirlpoolWithSalt.class.getName());
	private static DoubleWhirlpoolWithSalt _instance = new DoubleWhirlpoolWithSalt();

	public static DoubleWhirlpoolWithSalt getInstance()
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
		whirlpool2.update(password.getBytes("UTF-8"));
		byte[] stdHash = whirlpool2.getByteArray();

		whirlpool2 = JacksumAPI.getChecksumInstance("whirlpool2");
		whirlpool2.setEncoding("BASE64");
		whirlpool2.update(ConfigValue.DoubleWhirlpoolSalt.getBytes("UTF-8"));
		whirlpool2.update(stdHash);
		String pt1 = whirlpool2.format("#CHECKSUM");

		whirlpool2 = JacksumAPI.getChecksumInstance("whirlpool2");
		whirlpool2.setEncoding("BASE64");
		whirlpool2.update(stdHash);
		whirlpool2.update((ConfigValue.DoubleWhirlpoolSalt + pt1).getBytes("UTF-8"));
		String pt2 = whirlpool2.format("#CHECKSUM");

		return pt1 + '|' + pt2;
	}
}