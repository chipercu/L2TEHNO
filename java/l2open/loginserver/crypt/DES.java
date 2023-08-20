package l2open.loginserver.crypt;

import java.util.logging.Logger;

public class DES implements Crypt
{
	protected static Logger _log = Logger.getLogger(DES.class.getName());

	private static DES _instance = new DES();

	public static DES getInstance()
	{
		return _instance;
	}

	@Override
	public boolean compare(String password, String hash)
	{
		try
		{
			return encrypt(password).equalsIgnoreCase(hash);
		}
		catch(ArrayIndexOutOfBoundsException e)
		{
			_log.warning("Could not check password["+password+"]["+hash+"], OOBE inside algorytm, fake packets possible!");
			return false;
		}
	}

	/**
	 * Шифрует пароль алгоритмом DES. Используется для импорта с офф сервера.
	 * @param password
	 * @return hash
	 * @throws ArrayIndexOutOfBoundsException если произошла ошибка
	 */
	@Override
	public String encrypt(String password) throws ArrayIndexOutOfBoundsException
	{
		short[] key = new short[16];
		short[] dstbytes = new short[16];

		byte[] tmp = password.getBytes();

		for(int i = 0; i < tmp.length; i++)
		{
			key[i] = tmp[i];
			dstbytes[i] = tmp[i];
		}

		long one = bytesToInt(key, 0) * 213119L + 2529077L;
		one = one - Math.round(one / 4294967296L) * 4294967296L;

		long two = bytesToInt(key, 4) * 213247L + 2529089L;
		two = two - Math.round(two / 4294967296L) * 4294967296L;

		long three = bytesToInt(key, 8) * 213203L + 2529589L;
		three = three - Math.round(three / 4294967296L) * 4294967296L;

		long four = bytesToInt(key, 12) * 213821L + 2529997L;
		four = four - Math.round(four / 4294967296L) * 4294967296L;

		myuwSplit(one, key, 0);
		myuwSplit(two, key, 4);
		myuwSplit(three, key, 8);
		myuwSplit(four, key, 12);

		dstbytes[0] = (short) (dstbytes[0] ^ key[0]);

		for(int i = 1; i < 16; i++)
			dstbytes[i] = (short) (dstbytes[i] ^ dstbytes[i - 1] ^ key[i]);

		for(int i = 0; i < 16; i++)
			if(dstbytes[i] == 0)
				dstbytes[i] = 102;

		String encrypt = "";
		for(int i = 0; i < 16; i++)
			encrypt = encrypt + tohex(dstbytes[i]);
		return "0x" + encrypt;
	}

	private static String tohex(short value)
	{
		String[] bytes = { "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "A", "B", "C", "D", "E", "F" };

		byte b1 = (byte) (value >> 4 & 0xF);
		byte b2 = (byte) (value & 0xF);

		return bytes[b1] + bytes[b2];
	}

	private static void myuwSplit(long w, short[] bytes, int index)
	{
		bytes[index + 3] = (short) Math.round(w / 16777216L);
		bytes[index + 2] = (short) Math.round((w - bytes[index + 3] * 16777216L) / 65536L);
		bytes[index + 1] = (short) Math.round((w - bytes[index + 3] * 16777216L - bytes[index + 2] * 65536L) / 256L);
		bytes[index] = (short) Math.round((w - bytes[index + 3] * 16777216L - bytes[index + 2] * 65536L - bytes[index + 1] * 256L));
	}

	private static long bytesToInt(short[] bytes, int index)
	{
		return bytes[index] + bytes[index + 1] * 256L + bytes[index + 2] * 65536L + bytes[index + 3] * 16777216L;
	}
}