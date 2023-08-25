package l2open.loginserver.gameservercon.lspackets;

import l2open.config.ConfigValue;
import l2open.loginserver.GameServerTable;

public class AuthResponse extends ServerBasePacket
{
	public AuthResponse(int serverId, int LastProtocolVersion)
	{
		writeC(0x02);
		writeC(serverId);
		writeS(GameServerTable.getInstance().getServerNameById(serverId));
		writeC(ConfigValue.ShowLicence ? 0 : 1);
		writeH(LastProtocolVersion);
	}
}