package l2open.gameserver.clientpackets;

import l2open.config.ConfigValue;
import l2open.common.ThreadPoolManager;
import l2open.gameserver.cache.Msg;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.serverpackets.SocialAction;
import l2open.util.Util;

public class RequestSocialAction extends L2GameClientPacket
{
	private int _actionId;

	/**
	 * packet type id 0x34
	 * format:		cd
	 */
	@Override
	public void readImpl()
	{
		_actionId = readD();
	}

	@Override
	public void runImpl()
	{
		L2Player activeChar = getClient().getActiveChar();
		if(activeChar == null)
			return;

		if(activeChar.isOutOfControl() || activeChar.getTransformation() != 0)
		{
			activeChar.sendActionFailed();
			return;
		}

		// You cannot do anything else while fishing
		if(activeChar.isFishing())
		{
			activeChar.sendPacket(Msg.YOU_CANNOT_DO_ANYTHING_ELSE_WHILE_FISHING);
			return;
		}

		// internal Social Action check
		if(_actionId < 2 || _actionId > 14)
		{
			Util.handleIllegalPlayerAction(activeChar, "RequestSocialAction[43]", "Character " + activeChar.getName() + " at account " + activeChar.getAccountName() + "requested an internal Social Action " + _actionId, 1);
			return;
		}

		if(activeChar.getPrivateStoreType() == L2Player.STORE_PRIVATE_NONE && !activeChar.isInTransaction() && !activeChar.isActionsDisabled() && !activeChar.isSitting())
		{
			activeChar.broadcastPacket2(new SocialAction(activeChar.getObjectId(), _actionId));
			if(ConfigValue.AltSocialActionReuse)
			{
				ThreadPoolManager.getInstance().schedule(new SocialTask(activeChar), 2600, true);
				activeChar.block();
			}
		}
	}

	class SocialTask extends l2open.common.RunnableImpl
	{
		L2Player _player;

		SocialTask(L2Player player)
		{
			_player = player;
		}

		public void runImpl()
		{
			_player.unblock();
		}
	}
}