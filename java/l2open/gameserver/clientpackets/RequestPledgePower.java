package l2open.gameserver.clientpackets;

import l2open.gameserver.model.L2Clan;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.serverpackets.ManagePledgePower;
import l2open.gameserver.tables.player.PlayerData;

import java.util.logging.Logger;

public class RequestPledgePower extends L2GameClientPacket
{
	static Logger _log = Logger.getLogger(RequestPledgePower.class.getName());
	// format: cdd(d)
	private int _rank;
	private int _action;
	private int _privs;

	@Override
	public void readImpl()
	{
		_rank = readD();
		_action = readD();
		if(_action == 2)
			_privs = readD();
	}

	@Override
	public void runImpl()
	{
		L2Player activeChar = getClient().getActiveChar();
		if(activeChar == null || activeChar.is_block)
			return;
		if(_action == 2)
		{
			if(_rank < L2Clan.RANK_FIRST || _rank > L2Clan.RANK_LAST)
				return;
			if(activeChar.getClan() != null && (activeChar.getClanPrivileges() & L2Clan.CP_CL_MANAGE_RANKS) == L2Clan.CP_CL_MANAGE_RANKS)
			{
				if(_rank == 9) // Академикам оставляем только перечисленные ниже права
					_privs = (_privs & L2Clan.CP_CL_WAREHOUSE_SEARCH) + (_privs & L2Clan.CP_CH_ENTRY_EXIT) + (_privs & L2Clan.CP_CS_ENTRY_EXIT) + (_privs & L2Clan.CP_CH_USE_FUNCTIONS) + (_privs & L2Clan.CP_CS_USE_FUNCTIONS);
				PlayerData.getInstance().setRankPrivs(activeChar.getClan(), _rank, _privs);
				activeChar.getClan().updatePrivsForRank(_rank);
			}
		}
		else if(activeChar.getClan() != null)
			activeChar.sendPacket(new ManagePledgePower(activeChar, _action, _rank));
		else
			activeChar.sendActionFailed();
	}
}