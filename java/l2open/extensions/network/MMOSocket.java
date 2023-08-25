package l2open.extensions.network;

import java.net.MMOInetAddress;
import java.net.InetAddress;
import java.net.Socket;
import java.io.IOException;

public class MMOSocket
{
	private final Socket _socket;
	private MMOInetAddress _inet_adres;

	public MMOSocket(Socket socket, byte[] addr)
	{
		_socket = socket;

		if(addr == null)
			_inet_adres = null;
		else
			_inet_adres = new MMOInetAddress(addr);
	}

	public InetAddress getLocalAddress()
	{
		return _socket.getLocalAddress();
	}

	public InetAddress getInetAddress()
	{
		if(_inet_adres == null)
			return _socket.getInetAddress();
		return _inet_adres;
	}

	public void setInetAddress(byte[] addr)
	{
		if(addr != null)
			_inet_adres = new MMOInetAddress(addr);
	}

	public void close() throws IOException
	{
		_socket.close();
	}

	public boolean isHaProxy()
	{
		return _inet_adres != null;
	}
}
