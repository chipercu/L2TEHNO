package l2open.gameserver.clientpackets;

import l2open.config.ConfigValue;
import l2open.gameserver.cache.CrestCache;
import l2open.gameserver.cache.Msg;
import l2open.gameserver.model.L2Clan;
import l2open.gameserver.model.L2Player;

import java.util.logging.Logger;

public class RequestSetPledgeCrest extends L2GameClientPacket
{
	// Format: cdb
	static Logger _log = Logger.getLogger(RequestSetPledgeCrest.class.getName());

	private int _length;
	private byte[] _data;

	@Override
	public void readImpl()
	{
		_length = readD();
		if(_length == 0)
			return; // удаление значка
		if(_length > _buf.remaining() || _length != 256)
		{
			_log.warning("Possibly server crushing packet: " + getType() + " with length " + _length);
			_buf.clear();
			return;
		}
		_data = new byte[_length];
		readB(_data);

		// сравниваем нашу шапку, она всегда статическая...
		for(int i=0;i<116;i++)
			if(CrestCache.header_clan_crest[i] != _data[i])
			{
				_log.info("Crest Err["+i+"]["+CrestCache.header_clan_crest[i]+"]["+_data[i]+"]");
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

		if(System.currentTimeMillis() - activeChar.getLastRequestSetPledgeCrestPacket() < ConfigValue.RequestSetPledgeCrestPacketDelay)
		{
			activeChar.sendActionFailed();
			return;
		}
		activeChar.setLastRequestSetPledgeCrestPacket();

		L2Clan clan = activeChar.getClan();
		if((activeChar.getClanPrivileges() & L2Clan.CP_CL_EDIT_CREST) == L2Clan.CP_CL_EDIT_CREST)
		{
			if(clan.getLevel() < 3)
			{
				activeChar.sendPacket(Msg.CLAN_CREST_REGISTRATION_IS_ONLY_POSSIBLE_WHEN_CLANS_SKILL_LEVELS_ARE_ABOVE_3);
				return;
			}

			if(clan.hasCrest())
				CrestCache.removePledgeCrest(clan);

			if(_data != null && _length != 0)
				CrestCache.savePledgeCrest(clan, _data);

			clan.broadcastClanStatus(false, true, false);
		}
	}
}