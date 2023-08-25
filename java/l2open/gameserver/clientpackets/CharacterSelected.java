package l2open.gameserver.clientpackets;

import l2open.config.ConfigValue;
import l2open.gameserver.cache.Msg;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.network.L2GameClient;
import l2open.gameserver.network.L2GameClient.GameClientState;
import l2open.gameserver.serverpackets.CharSelected;
import l2open.gameserver.tables.player.PlayerData;
import l2open.util.AutoBan;
import l2open.util.Util;

public class CharacterSelected extends L2GameClientPacket
{
	private int _charSlot;

	/**
	 * Format: cdhddd
	 */
	@Override
	public void readImpl()
	{
		_charSlot = readD();
	}

	@Override
	public void runImpl()
	{
		L2GameClient client = getClient();
		if(ConfigValue.SAEnabled && !client.getSecondaryAuth().isAuthed())
		{
			client.getSecondaryAuth().openDialog();
			return;
		}
		else if(client.getActiveChar() != null)
			return;
		L2Player activeChar = client.loadCharFromDisk(_charSlot);
		if(activeChar == null)
			return;
		else if(ConfigValue.ServerOnlyCreate && !activeChar.isGM())
		{
			activeChar.logout(false, false, true, true);
			return;
		}
		else if(ConfigValue.AccHwidLockClear > 0 && (activeChar.getLastAccess() + ConfigValue.AccHwidLockClear * 86400) < (System.currentTimeMillis()/1000))
			activeChar.clearAccLock();
		else if(ConfigValue.AccHwidLockEnable && activeChar.getAccLock() != null && !Util.contains(activeChar.getAccLock(), activeChar.getHWIDs()))
		{
			if(ConfigValue.MultiHwidSystem)
			{
				PlayerData.getInstance().select_answer(activeChar);
				activeChar.is_block = true;
			}
			if(!activeChar.is_block)
			{
				activeChar.logout(false, false, false, true);
				return;
			}
		}
		else if(AutoBan.isBanned(activeChar.getObjectId()))
		{
			activeChar.setAccessLevel(-100);
			activeChar.logout(false, false, true, true);
			return;
		}
		/*else if(!canLogin(client) && (!ConfigValue.MaxInstancesPremium || !activeChar.hasBonus()))
		{
			activeChar.logout(false, false, true, true);
			// GuardMsgPacket.MsgType.INSTANCE_LIMIT.packet
			return;
		}*/
		if(activeChar.getAccessLevel() < 0)
			activeChar.setAccessLevel(0);
		else if(ConfigValue.CCPGuardEnable)
			if(!ccpGuard.Protection.checkPlayerWithHWID(client, activeChar.getObjectId(), activeChar.getName()))
				return;

		//PlayerData.getInstance().select_answer(activeChar);
		//activeChar.is_block = true;

		client.setState(GameClientState.IN_GAME);

		sendPacket(new CharSelected(activeChar, client.getSessionId().playOkID1));
	}

	/*public boolean canLogin(L2GameClient client)
	{
		if(ConfigValue.MaxInstances <= 0)
			return true;

		com.l2scripts.sguard.core.manager.session.GuardSession gs = com.l2scripts.sguard.core.manager.GuardSessionManager.getSession(com.l2scripts.sguard.core.manager.session.HWID.fromString(new com.l2scripts.sguard.api.GuardPlayer(client).getHWID()));

		int count = gs.getCount();

		if(gs.hasAccountSession(client.getLoginName()))
			return true;

		return count < ConfigValue.MaxInstances;
	}*/
}