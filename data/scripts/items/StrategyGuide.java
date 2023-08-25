package items;

import l2open.extensions.scripts.ScriptFile;
import l2open.gameserver.cache.Msg;
import l2open.gameserver.handler.IItemHandler;
import l2open.gameserver.handler.ItemHandler;
import l2open.gameserver.model.*;
import l2open.gameserver.model.base.Transaction;
import l2open.gameserver.model.items.L2ItemInstance;
import l2open.gameserver.serverpackets.ExAskJoinMPCC;
import l2open.gameserver.serverpackets.SystemMessage;
import l2open.gameserver.tables.SkillTable;

public class StrategyGuide implements IItemHandler, ScriptFile
{
	private static final int STRATEGY_GUIDE_ID = 8871;

	private static final int[] ITEM_IDS = {STRATEGY_GUIDE_ID};

	@Override
	public int[] getItemIds()
	{
		return ITEM_IDS;
	}

	@Override
	public void useItem(L2Playable playable, L2ItemInstance item, Boolean val)
	{
		if(playable == null || !playable.isPlayer() )
			return;
		L2Player player = (L2Player) playable;
		L2Object getTarget = player.getTarget();

		// Если таргет не игрок, то посылаем нахуй.
		if(getTarget == null || !getTarget.isPlayer())
		{
			player.sendPacket(Msg.YOU_HAVE_INVITED_WRONG_TARGET);
			player.sendActionFailed();
			return;
		}
		L2Player target = (L2Player) getTarget;

		//нету итемов.
		if(item == null || item.getCount() < 1)
		{
			player.sendPacket(Msg.INCORRECT_ITEM_COUNT);
			return;
		}

		// Цель не выбрана
		if(target == null)
			return;

		// Самому себе нельзя кидать СС.
		if(target == player || !player.isInParty())
		{
			player.sendPacket(Msg.YOU_HAVE_INVITED_WRONG_TARGET);
			player.sendActionFailed();
			return;
		}

		//Если кидающий не в пати.
		if(!player.isInParty())
		{
		 	player.sendPacket(Msg.YOU_DO_NOT_HAVE_AUTHORITY_TO_USE_THE_COMMAND_CHANNEL);
			player.sendActionFailed();
			return;
		}

		//Если цель не в пати.
		if(!target.isInParty())
		{
			player.sendPacket(Msg.YOU_HAVE_INVITED_WRONG_TARGET);
			player.sendActionFailed();
			return;
		}

		// Своей пати нельзя кидать СС
		for(L2Player member : player.getParty().getPartyMembers())
			if(member == target)
			{
				player.sendPacket(Msg.YOU_HAVE_INVITED_WRONG_TARGET);
				player.sendActionFailed();
				return;
			}

		// Если приглашен в СС не лидер партии, то посылаем приглашение лидеру его партии
		if(target.isInParty() && !target.getParty().isLeader(target))
			target = target.getParty().getPartyLeader();

		if(target == null)
			return;

		// Цель уже в командном канале.
		if(target.getParty() != null && target.getParty().isInCommandChannel())
		{
			player.sendPacket(new SystemMessage(SystemMessage.S1_PARTY_IS_ALREADY_A_MEMBER_OF_THE_COMMAND_CHANNEL).addString(target.getName()));
			player.sendActionFailed();
			return;
		}

		//цель отвечает на какой-то запрос
		if(target.isInTransaction())
		{
			player.sendPacket(new SystemMessage(SystemMessage.S1_IS_BUSY_PLEASE_TRY_AGAIN_LATER).addString(target.getName()));
			player.sendActionFailed();
			return;
		}

		//кто кидает СС, не являеться лидером
		if(player.getParty().isInCommandChannel())
			if(player.getParty().getCommandChannel().getChannelLeader() != player)
			{
				player.sendPacket(Msg.YOU_DO_NOT_HAVE_AUTHORITY_TO_INVITE_SOMEONE_TO_THE_COMMAND_CHANNEL);
				return;
			}

		player.setCreateCommandChannelWithItem(true);
		sendInvite(player, target);
	}

	public void sendInvite(L2Player requestor, L2Player target)
	{
		new Transaction(Transaction.TransactionType.CHANNEL, requestor, target, 30000);
		target.sendPacket(new ExAskJoinMPCC(requestor.getName()));
		requestor.sendMessage("You invited " + target.getName() + " to your Command Channel.");
	}

	@Override
	public void onLoad()
	{
		ItemHandler.getInstance().registerItemHandler(this);
	}

	@Override
	public void onReload()
	{}

	@Override
	public void onShutdown()
	{}
}