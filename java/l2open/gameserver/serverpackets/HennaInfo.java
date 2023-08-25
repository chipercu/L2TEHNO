package l2open.gameserver.serverpackets;

import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.instances.L2HennaInstance;

public class HennaInfo extends L2GameServerPacket
{
	private final L2HennaInstance[] _hennas = new L2HennaInstance[3];
	private final int _str, _con, _dex, _int, _wit, _men;
	private int _count;

	public HennaInfo(L2Player player)
	{
		_count = 0;
		L2HennaInstance h;
		for(int i = 0; i < 3; i++)
			if((h = player.getHenna(i + 1)) != null)
				_hennas[_count++] = h;

		_str = player.getHennaStatSTR();
		_con = player.getHennaStatCON();
		_dex = player.getHennaStatDEX();
		_int = player.getHennaStatINT();
		_wit = player.getHennaStatWIT();
		_men = player.getHennaStatMEN();
	}

	@Override
	protected final void writeImpl()
	{
		writeC(0xe5);
		writeC(_int); //equip INT
		writeC(_str); //equip STR
		writeC(_con); //equip CON
		writeC(_men); //equip MEM
		writeC(_dex); //equip DEX
		writeC(_wit); //equip WIT
		writeD(3); //interlude, slots?
		writeD(_count);
		for(int i = 0; i < _count; i++)
		{
			writeD(_hennas[i].getSymbolId());
			writeD(_hennas[i].getSymbolId());
		}
	}
}