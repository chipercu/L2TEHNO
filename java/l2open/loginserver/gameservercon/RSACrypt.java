package l2open.loginserver.gameservercon;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

/**
 * @Author: Death
 * @Date: 12/11/2007
 * @Time: 19:45:57
 */
public class RSACrypt
{
	private final Cipher RSAEncrypt;
	private final Cipher RSADecrypt;

	private final byte[] RSAPublicKey = new byte[128];

	public RSACrypt() throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException
	{
		RSAEncrypt = Cipher.getInstance("RSA");
		RSADecrypt = Cipher.getInstance("RSA");
		KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
		KeyPair kp = kpg.generateKeyPair();
		RSAPrivateKey privateRSA = (RSAPrivateKey) kp.getPrivate();
		RSAPublicKey publicRSA = (RSAPublicKey) kp.getPublic();

		byte[] pub = publicRSA.getModulus().toByteArray();
		System.arraycopy(pub, 1, RSAPublicKey, 0, 128);

		RSAEncrypt.init(Cipher.ENCRYPT_MODE, publicRSA);
		RSADecrypt.init(Cipher.DECRYPT_MODE, privateRSA);

		/*
		try
		{
			//byte[] enc = encryptRSA("asd".getBytes());
			//Utils.printPacket(enc);
			//byte[] dec = decryptRSA(enc);
			//System.out.println(new String(dec));
		}
		catch(BadPaddingException e)
		{
			e.printStackTrace();
		}
		catch(IllegalBlockSizeException e)
		{
			e.printStackTrace();
		}
		*/
	}

	public byte[] encryptRSA(byte[] toEncrypt) throws BadPaddingException, IllegalBlockSizeException
	{
		return RSAEncrypt.doFinal(toEncrypt);
	}

	public byte[] decryptRSA(byte[] toDecrypt) throws IllegalBlockSizeException, BadPaddingException
	{
		return RSADecrypt.doFinal(toDecrypt);
	}

	public byte[] getRSAPublicKey()
	{
		return RSAPublicKey;
	}
}