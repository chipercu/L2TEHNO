package l2open.gameserver.serverpackets;

import l2open.gameserver.model.L2Player;

public class ExVitalityEffectInfo extends L2GameServerPacket
{
	private int points;
	private int expBonus;
	
	public ExVitalityEffectInfo(L2Player player)
	{
		points = (int) player.getVitality() * 2;
		expBonus = (int) player.getVitalityBonus() * 100;
	}
	
	@Override
	protected void writeImpl()
	{
		writeEx(0x11E);
		
		writeD(points);
		writeD(expBonus);
		writeD(0);// TODO: Remaining items count
		writeD(0);// TODO: Remaining items count
	}
}