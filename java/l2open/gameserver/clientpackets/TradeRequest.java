package l2open.gameserver.clientpackets;

import l2open.config.ConfigValue;
import l2open.gameserver.cache.Msg;
import l2open.gameserver.model.L2Object;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.L2World;
import l2open.gameserver.model.base.Transaction;
import l2open.gameserver.model.base.Transaction.TransactionType;
import l2open.gameserver.serverpackets.SendTradeRequest;
import l2open.gameserver.serverpackets.SystemMessage;
import l2open.util.Util;

public class TradeRequest extends L2GameClientPacket
{
	//Format: cd
	private int _objectId;

	@Override
	public void readImpl()
	{
		_objectId = readD();
	}

	@Override
	public void runImpl()
	{
		L2Player activeChar = getClient().getActiveChar();
		if(activeChar == null)
			return;
		else if(!activeChar.getPlayerAccess().UseTrade || activeChar.is_block || !activeChar.canItemAction())
		{
			activeChar.sendPacket(Msg.THIS_ACCOUNT_CANOT_TRADE_ITEMS);
			activeChar.sendActionFailed();
			return;
		}

		String tradeBan = activeChar.getVar("tradeBan");
		if(tradeBan != null && (tradeBan.equals("-1") || Long.parseLong(tradeBan) >= System.currentTimeMillis()))
		{
			activeChar.sendMessage("Your trade is banned! Expires: " + (tradeBan.equals("-1") ? "never" : Util.formatTime((Long.parseLong(tradeBan) - System.currentTimeMillis()) / 1000)) + ".");
			return;
		}
		else if(activeChar.isDead())
		{
			activeChar.sendActionFailed();
			return;
		}

		L2Object target = L2World.getAroundObjectById(activeChar, _objectId);

		if(target == null || !target.isPlayer() || target.getObjectId() == activeChar.getObjectId() || target.getPlayer().is_block)
		{
			activeChar.sendPacket(Msg.THAT_IS_THE_INCORRECT_TARGET());
			return;
		}

		L2Player pcTarget = (L2Player) target;

		if(activeChar.getPrivateStoreType() != L2Player.STORE_PRIVATE_NONE || pcTarget.getPrivateStoreType() != L2Player.STORE_PRIVATE_NONE)
		{
			activeChar.sendPacket(Msg.WHILE_OPERATING_A_PRIVATE_STORE_OR_WORKSHOP_YOU_CANNOT_DISCARD_DESTROY_OR_TRADE_AN_ITEM);
			return;
		}
		else if(!pcTarget.getPlayerAccess().UseTrade)
		{
			activeChar.sendPacket(Msg.THIS_ACCOUNT_CANOT_TRADE_ITEMS);
			activeChar.sendActionFailed();
			return;
		}
		else if(ConfigValue.NoInviteTradeForPvp && pcTarget.getPvpFlag() != 0)
		{
			activeChar.sendPacket(Msg.INVALID_TARGET(), Msg.ActionFail);
			return;
		}

		tradeBan = pcTarget.getVar("tradeBan");
		if(tradeBan != null && (tradeBan.equals("-1") || Long.parseLong(tradeBan) >= System.currentTimeMillis()))
		{
			activeChar.sendMessage("Target trade is banned! Expires: " + (tradeBan.equals("-1") ? "never" : Util.formatTime((Long.parseLong(tradeBan) - System.currentTimeMillis()) / 1000)) + ".");
			return;
		}
		else if(activeChar.getTeam() != 0 || pcTarget.getTeam() != 0 || activeChar.isInEvent() > 0 || pcTarget.isInEvent() > 0)
		{
			activeChar.sendActionFailed();
			return;
		}
		else if(pcTarget.isInOlympiadMode() || activeChar.isInOlympiadMode() || activeChar.getOlympiadGame() != null || pcTarget.getOlympiadGame() != null)
		{
			activeChar.sendPacket(Msg.THAT_IS_THE_INCORRECT_TARGET());
			return;
		}
		else if(pcTarget.getTradeRefusal() || pcTarget.isInBlockList(activeChar) || pcTarget.isBlockAll())
		{
			activeChar.sendPacket(Msg.YOU_HAVE_BEEN_BLOCKED_FROM_THE_CONTACT_YOU_SELECTED);
			return;
		}
		else if(activeChar.isInTransaction())
		{
			activeChar.sendPacket(Msg.YOU_ARE_ALREADY_TRADING_WITH_SOMEONE);
			return;
		}
		else if(pcTarget.isInTransaction())
		{
			activeChar.sendPacket(new SystemMessage(SystemMessage.S1_IS_BUSY_PLEASE_TRY_AGAIN_LATER).addString(pcTarget.getName()));
			return;
		}
		else if(activeChar.isFishing())
		{
			activeChar.sendPacket(Msg.YOU_CANNOT_DO_THAT_WHILE_FISHING);
			return;
		}

		new Transaction(TransactionType.TRADE_REQUEST, activeChar, pcTarget, 10000);
		pcTarget.sendPacket(new SendTradeRequest(activeChar.getObjectId()));
		activeChar.sendPacket(new SystemMessage(SystemMessage.YOU_HAVE_REQUESTED_A_TRADE_WITH_C1).addString(pcTarget.getTransformationName()!= null ? pcTarget.getTransformationName() : pcTarget.getName()));
	}
}