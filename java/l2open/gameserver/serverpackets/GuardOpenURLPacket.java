package l2open.gameserver.serverpackets;

import l2open.config.ConfigValue;

public class GuardOpenURLPacket extends L2GameServerPacket
{
	private final String _url;

	public GuardOpenURLPacket(String url)
	{
		_url = url;
	}

	@Override
	protected final void writeImpl()
	{
		if(ConfigValue.ScriptsGuardEnable || ConfigValue.SmartGuard)
		{
			writeC(0xFF);
			writeC(0x03);

			writeS(_url);
		}
		else if(ConfigValue.StrixGuardEnable)
		{
			writeC(0x7D);
			writeC(0x02);

			writeS(_url);
		}
	}
}