package l2open.gameserver.loginservercon;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPublicKeySpec;

/**
 * @Author: Death
 * @Date: 12/11/2007
 * @Time: 22:11:23
 */
public class RSACrypt
{
	private final Cipher c;

	public RSACrypt(byte[] key) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeySpecException, InvalidKeyException
	{
		c = Cipher.getInstance("RSA");
		KeyFactory kf = KeyFactory.getInstance("RSA");

		RSAPublicKeySpec pks = new RSAPublicKeySpec(new BigInteger(key), new BigInteger(new byte[] { 1, 0, 1 }));
		RSAPublicKey pk = (RSAPublicKey) kf.generatePublic(pks);
		c.init(Cipher.ENCRYPT_MODE, pk);
	}

	public byte[] encryptRSA(byte[] toEncrypt) throws BadPaddingException, IllegalBlockSizeException
	{
		return c.doFinal(toEncrypt);
	}
}
