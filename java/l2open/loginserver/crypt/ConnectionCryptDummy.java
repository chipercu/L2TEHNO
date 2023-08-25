package l2open.loginserver.crypt;

import java.io.IOException;

public class ConnectionCryptDummy implements ConnectionCrypt
{
	public static final ConnectionCryptDummy instance = new ConnectionCryptDummy();

	/**
	 * @throws IOException  
	 */
	public byte[] decrypt(byte[] raw) throws IOException
	{
		/*
		byte[] result = new byte[raw.length];
		System.arraycopy(result, 0, raw, 0, raw.length);
		return result;
		*/
		return raw;
	}

	/**
	 * @throws IOException  
	 */
	public void decrypt(byte[] raw, final int offset, final int size) throws IOException
	{}

	/**
	 * @throws IOException  
	 */
	public byte[] crypt(byte[] raw) throws IOException
	{
		/*
		byte[] result = new byte[raw.length];
		System.arraycopy(result, 0, raw, 0, raw.length);
		return result;
		*/
		return raw;
	}

	/**
	 * @throws IOException  
	 */
	public void crypt(byte[] raw, final int offset, final int size) throws IOException
	{}
}