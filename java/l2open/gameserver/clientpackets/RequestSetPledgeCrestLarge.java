package l2open.gameserver.clientpackets;

import l2open.config.ConfigValue;
import l2open.gameserver.cache.CrestCache;
import l2open.gameserver.cache.Msg;
import l2open.gameserver.model.L2Clan;
import l2open.gameserver.model.L2Player;

import java.util.logging.Logger;

public class RequestSetPledgeCrestLarge extends L2GameClientPacket
{
	static Logger _log = Logger.getLogger(RequestSetPledgeCrestLarge.class.getName());
	private int _length;
	private byte[] _data;

	/**
	 * format: chd(b)
	 */
	@Override
	public void readImpl()
	{
		_length = readD();
		if(_length > _buf.remaining() || _length != 2176)
		{
			_log.warning("Possibly server crushing packet: " + getType() + " with length " + _length);
			_buf.clear();
			return;
		}
		_data = new byte[_length];
		readB(_data);

		// сравниваем нашу шапку, она всегда статическая...
		for(int i=0;i<116;i++)
			if(CrestCache.header_clan_larg_crest[i] != _data[i])
			{
				_log.info("Crest Err["+i+"]["+CrestCache.header_clan_larg_crest[i]+"]["+_data[i]+"]");
				_length=0;
				_data = null;
				break;
			}
	}

	@Override
	public void runImpl()
	{
		L2Player activeChar = getClient().getActiveChar();
		if(activeChar == null)
			return;

		L2Clan clan = activeChar.getClan();
		if(clan == null)
			return;

		if(System.currentTimeMillis() - activeChar.getLastRequestSetPledgeCrestLargePacket() < ConfigValue.RequestSetPledgeCrestLargePacketDelay)
		{
			activeChar.sendActionFailed();
			return;
		}
		activeChar.setLastRequestSetPledgeCrestLargePacket();

		if((activeChar.getClanPrivileges() & L2Clan.CP_CL_EDIT_CREST) == L2Clan.CP_CL_EDIT_CREST)
		{
			if(clan.getHasCastle() == 0 && clan.getHasHideout() == 0)
			{
				activeChar.sendPacket(Msg.THE_CLANS_EMBLEM_WAS_SUCCESSFULLY_REGISTERED__ONLY_A_CLAN_THAT_OWNS_A_CLAN_HALL_OR_A_CASTLE_CAN_GET_THEIR_EMBLEM_DISPLAYED_ON_CLAN_RELATED_ITEMS);
				return;
			}

			if(clan.hasCrestLarge())
				CrestCache.removePledgeCrestLarge(clan);

			//_log.info("PCL size: " + _length);
			if(_data != null && _length != 0)
			{
				CrestCache.savePledgeCrestLarge(clan, _data);
				activeChar.sendPacket(Msg.THE_CLANS_EMBLEM_WAS_SUCCESSFULLY_REGISTERED__ONLY_A_CLAN_THAT_OWNS_A_CLAN_HALL_OR_A_CASTLE_CAN_GET_THEIR_EMBLEM_DISPLAYED_ON_CLAN_RELATED_ITEMS);
			}

			clan.broadcastClanStatus(false, true, false);
		}
	}
}