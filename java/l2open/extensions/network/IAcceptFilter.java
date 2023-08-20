package l2open.extensions.network;

import java.net.InetAddress;
import java.nio.channels.SocketChannel;

public interface IAcceptFilter<T extends MMOClient>
{
	public boolean accept(SocketChannel sc);
	public void incReceivablePacket(ReceivablePacket rp, T client);
	public void incSendablePacket(SendablePacket sp, T client);
	public void addConnect(T client);
	public void removeConnect(T client);
	public void BanIp(InetAddress address, int time, String comments);
}
