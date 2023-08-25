package l2open.gameserver.clientpackets;

import l2open.config.ConfigValue;
import l2open.extensions.multilang.CustomMessage;
import l2open.gameserver.cache.Msg;
import l2open.gameserver.model.L2Party;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.Reflection;
import l2open.gameserver.model.entity.DimensionalRift;
import l2open.gameserver.model.entity.olympiad.Olympiad;

public class RequestOustPartyMember extends L2GameClientPacket
{
	//Format: cS
	private String _name;

	@Override
	public void readImpl()
	{
		_name = readS(ConfigValue.cNameMaxLen);
	}

	@Override
	public void runImpl()
	{
		L2Player activeChar = getClient().getActiveChar();
		if(activeChar == null || activeChar.is_block)
			return;

		L2Party party = activeChar.getParty();
		if(party != null && party.isLeader(activeChar))
		{
			Reflection r = party.getReflection();
			L2Player oustPlayer = party.getPlayerByName(_name);
			
			if(Olympiad.isRegistered(activeChar) || Olympiad.isRegistered(oustPlayer) || oustPlayer != null && (oustPlayer.getOlympiadGame() != null || oustPlayer.isInOlympiadMode()))
			{
				activeChar.sendPacket(Msg.FAILED_TO_EXPEL_A_PARTY_MEMBER);
				return;
			}
			
			if(r != null && r instanceof DimensionalRift && oustPlayer != null && oustPlayer.getReflection().equals(r))
				activeChar.sendMessage(new CustomMessage("l2open.gameserver.clientpackets.RequestOustPartyMember.CantOustInRift", activeChar));
			else if(r != null && !(r instanceof DimensionalRift))
				activeChar.sendMessage(new CustomMessage("l2open.gameserver.clientpackets.RequestOustPartyMember.CantOustInDungeon", activeChar));
			else
				party.oustPartyMember(_name);
		}
	}
}