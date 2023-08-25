package l2open.loginserver.crypt;

import java.io.IOException;

public interface ConnectionCrypt
{
	public byte[] decrypt(byte[] raw) throws IOException;

	public void decrypt(byte[] raw, final int offset, final int size) throws IOException;

	public byte[] crypt(byte[] raw) throws IOException;

	public void crypt(byte[] raw, final int offset, final int size) throws IOException;
}