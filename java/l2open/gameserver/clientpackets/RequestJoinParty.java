package l2open.gameserver.clientpackets;

import l2open.config.ConfigValue;
import l2open.common.ThreadPoolManager;
import l2open.extensions.multilang.CustomMessage;
import l2open.gameserver.cache.Msg;
import l2open.gameserver.model.L2Party;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.L2World;
import l2open.gameserver.model.base.Transaction;
import l2open.gameserver.model.base.Transaction.TransactionType;
import l2open.gameserver.network.L2GameClient;
import l2open.gameserver.serverpackets.AskJoinParty;
import l2open.gameserver.serverpackets.SystemMessage;

public class RequestJoinParty extends L2GameClientPacket
{
	private String _name;
	private int _itemDistribution;

	@Override
	public void readImpl()
	{
		_name = readS(ConfigValue.cNameMaxLen);
		_itemDistribution = readD();
	}

	@Override
	public void runImpl()
	{
		L2Player activeChar = getClient().getActiveChar();
		if(activeChar == null)
			return;
		L2Player target = L2World.getPlayer(_name);

		if(target == null || target == activeChar || target.isInvisible() || target.is_block || activeChar.is_block)
		{
			activeChar.sendActionFailed();
			return;
		}

		if(activeChar.isInTransaction())
		{
			activeChar.sendPacket(Msg.WAITING_FOR_ANOTHER_REPLY, Msg.ActionFail);
			return;
		}
		else if(ConfigValue.NoInvitePartyForPvp && target.getPvpFlag() != 0)
		{
			activeChar.sendPacket(Msg.INVALID_TARGET(), Msg.ActionFail);
			return;
		}

		SystemMessage problem = target.canJoinParty(activeChar);
		if(problem != null)
		{
			activeChar.sendPacket(problem);
			return;
		}

		if(!activeChar.isInParty())
			createNewParty(getClient(), _itemDistribution, target, activeChar);
		else
			addTargetToParty(getClient(), _itemDistribution, target, activeChar);
	}

	private void addTargetToParty(L2GameClient client, int itemDistribution, L2Player target, L2Player activeChar)
	{
		if(activeChar.getParty().getMemberCount() >= ConfigValue.MAX_SIZE)
		{
			activeChar.sendPacket(Msg.PARTY_IS_FULL);
			return;
		}

		// Только Party Leader может приглашать новых членов
		if(ConfigValue.PartyLeaderOnlyCanInvite && !activeChar.getParty().isLeader(activeChar))
		{
			activeChar.sendPacket(Msg.ONLY_THE_LEADER_CAN_GIVE_OUT_INVITATIONS);
			return;
		}

		if(activeChar.getParty().isInDimensionalRift())
		{
			activeChar.sendMessage(new CustomMessage("l2open.gameserver.clientpackets.RequestJoinParty.InDimensionalRift", activeChar));
			activeChar.sendActionFailed();
		}

		if(!target.isInTransaction())
		{
			new Transaction(TransactionType.PARTY, activeChar, target, 10000);

			target.sendPacket(new AskJoinParty(activeChar.getName(), itemDistribution));
			activeChar.sendPacket(new SystemMessage(SystemMessage.YOU_HAVE_INVITED_C1_TO_JOIN_YOUR_PARTY).addString(target.getName()));
		}
		else
			activeChar.sendPacket(new SystemMessage(SystemMessage.S1_IS_BUSY_PLEASE_TRY_AGAIN_LATER).addString(target.getName()));
	}

	private void createNewParty(L2GameClient client, int itemDistribution, L2Player target, L2Player requestor)
	{
		if(!target.isInTransaction())
		{
			requestor.setParty(new L2Party(requestor, itemDistribution));
			new Transaction(TransactionType.PARTY, requestor, target, 10000);
			target.sendPacket(new AskJoinParty(requestor.getName(), itemDistribution));
			requestor.sendPacket(new SystemMessage(SystemMessage.YOU_HAVE_INVITED_C1_TO_JOIN_YOUR_PARTY).addString(target.getName()));
		}
		else
			requestor.sendPacket(new SystemMessage(SystemMessage.S1_IS_BUSY_PLEASE_TRY_AGAIN_LATER).addString(target.getName()));
	}
}