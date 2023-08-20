package java.net;

import java.net.InetAddress;
import java.net.Socket;
import java.io.IOException;

public class MMOInetAddress extends InetAddress
{
	public MMOInetAddress(byte[] addr)
	{
		int address = addr[3] & 0xFF;
		address |= ((addr[2] << 8) & 0xFF00);
		address |= ((addr[1] << 16) & 0xFF0000);
		address |= ((addr[0] << 24) & 0xFF000000);

		holder().hostName = numericToTextFormat(addr);
		holder().family = IPv4;
		holder().address = address;
		//holder().originalHostName = numericToTextFormat(addr);
    }

	public String getHostAddress()
	{
		return holder().hostName;
	}

	public static String numericToTextFormat(byte[] src)
	{
		return (src[0] & 0xff) + "." + (src[1] & 0xff) + "." + (src[2] & 0xff) + "." + (src[3] & 0xff);
	}
}
