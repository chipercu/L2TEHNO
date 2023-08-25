package l2open.gameserver.loginservercon.gspackets;

import l2open.config.*;
import l2open.gameserver.loginservercon.AdvIP;

public class AuthRequest extends GameServerBasePacket
{
	public AuthRequest()
	{
		writeC(0x01);
		writeC(ConfigValue.RequestServerID);
		writeC(ConfigValue.AcceptAlternateID ? 0x01 : 0x00);
		writeS(ConfigValue.ExternalHostname());
		writeS(ConfigValue.InternalHostname);
		if(ConfigValue.GameserverPort.length == 1) // старый формат, однопортовый
			writeH(ConfigValue.GameserverPort[0]);
		else
		//новый формат, многопортовый
		{
			writeH(0xFFFF);
			writeC(ConfigValue.GameserverPort.length);
			for(int PORT_GAME : ConfigValue.GameserverPort)
				writeH(PORT_GAME);
		}
		writeD(ConfigValue.MaximumOnlineUsers);
		byte[] data = ConfigValue.HexID;
		if(data == null)
			writeD(0);
		else
		{
			writeD(ConfigValue.HexID.length);
			writeB(ConfigValue.HexID);
		}
		writeD(ConfigSystem.GAMEIPS.size());
		for(AdvIP ip : ConfigSystem.GAMEIPS)
		{
			writeS(ip.ipadress);
			writeS(ip.ipmask);
			writeS(ip.bitmask);
		}
		writeH(ConfigValue.LoginServerProtocol);
	}
}