package l2open.gameserver.serverpackets;

import l2open.gameserver.model.L2Player;
import l2open.gameserver.templates.L2Henna;

public class HennaUnequipInfo extends L2GameServerPacket
{
	private int _str, _con, _dex, _int, _wit, _men;
	private long char_adena;
	private L2Henna _henna;

	public HennaUnequipInfo(L2Henna henna, L2Player player)
	{
		_henna = henna;
		char_adena = player.getAdena();
		_str = player.getSTR();
		_dex = player.getDEX();
		_con = player.getCON();
		_int = player.getINT();
		_wit = player.getWIT();
		_men = player.getMEN();
	}

	@Override
	protected final void writeImpl()
	{

		writeC(0xE7);
		writeD(_henna.getSymbolId()); //symbol Id
		writeD(_henna.getDyeId()); //item id of dye

		writeQ(_henna.getAmountDyeRequire());
		writeQ(_henna.getPrice());
		writeD(1); //able to draw or not 0 is false and 1 is true
		writeQ(char_adena);

		writeD(_int); //current INT
		writeC(_int + _henna.getStatINT()); //equip INT
		writeD(_str); //current STR
		writeC(_str + _henna.getStatSTR()); //equip STR
		writeD(_con); //current CON
		writeC(_con + _henna.getStatCON()); //equip CON
		writeD(_men); //current MEM
		writeC(_men + _henna.getStatMEM()); //equip MEM
		writeD(_dex); //current DEX
		writeC(_dex + _henna.getStatDEX()); //equip DEX
		writeD(_wit); //current WIT
		writeC(_wit + _henna.getStatWIT()); //equip WIT
	}
}