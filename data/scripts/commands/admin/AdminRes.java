package commands.admin;

import l2open.extensions.scripts.ScriptFile;
import l2open.gameserver.cache.Msg;
import l2open.gameserver.handler.AdminCommandHandler;
import l2open.gameserver.handler.IAdminCommandHandler;
import l2open.gameserver.model.L2Character;
import l2open.gameserver.model.L2Object;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.L2World;
import l2open.gameserver.serverpackets.Revive;
import l2open.gameserver.serverpackets.SocialAction;
import l2open.gameserver.taskmanager.DecayTaskManager;
import l2open.util.Log;

@SuppressWarnings("unused")
public class AdminRes implements IAdminCommandHandler, ScriptFile
{
	private static enum Commands
	{
		admin_res
	}

	public boolean useAdminCommand(Enum comm, String[] wordList, String fullString, L2Player activeChar)
	{
		Commands command = (Commands) comm;

		if(!activeChar.getPlayerAccess().Res)
			return false;

		if(fullString.startsWith("admin_res "))
			handleRes(activeChar, wordList[1]);
		if(fullString.equals("admin_res"))
			handleRes(activeChar);

		return true;
	}

	public Enum[] getAdminCommandEnum()
	{
		return Commands.values();
	}

	private void handleRes(L2Player activeChar)
	{
		handleRes(activeChar, null);
	}

	private void handleRes(L2Player activeChar, String player)
	{
		L2Object obj = activeChar.getTarget();
		if(player != null)
		{
			L2Player plyr = L2World.getPlayer(player);
			if(plyr != null)
				obj = plyr;
			else
				try
				{
					int radius = Math.max(Integer.parseInt(player), 100);
					for(L2Character character : activeChar.getAroundCharacters(radius, 200))
					{
						character.broadcastPacket(new SocialAction(character.getObjectId(), SocialAction.LEVEL_UP));
						character.doRevive();
						character.setCurrentHpMp(character.getMaxHp(), character.getMaxMp(), true);
						character.setCurrentCp(character.getMaxCp());
						if(character.isPlayer())
							((L2Player) character).restoreExp();
						// If the target is an NPC, then abort it's auto decay and respawn.
						else
							DecayTaskManager.getInstance().cancelDecayTask(character);
					}
					activeChar.sendMessage("Resurrected within " + radius + " unit radius.");
					return;
				}
				catch(NumberFormatException e)
				{
					activeChar.sendMessage("Enter valid player name or radius");
					return;
				}
		}

		if(obj == null)
			obj = activeChar;
		if(obj.isCharacter())
		{
			L2Character target = (L2Character) obj;
			if(!target.isDead())
				return;

			if(!target.isPlayer())
				DecayTaskManager.getInstance().cancelDecayTask(target);
			// GM Resurrection will restore any lost exp
			target.broadcastPacket(new SocialAction(target.getObjectId(), 15));
			target.doRevive();
			target.setCurrentHpMp(target.getMaxHp(), target.getMaxMp(), true);
			target.setCurrentCp(target.getMaxCp());
			if(target.isPlayer())
			{
				L2Player deadplayer = (L2Player) target;
				deadplayer.restoreExp();
			}
		}
		else
			activeChar.sendPacket(Msg.INVALID_TARGET);
	}

	public void onLoad()
	{
		AdminCommandHandler.getInstance().registerAdminCommandHandler(this);
	}

	public void onReload()
	{}

	public void onShutdown()
	{}
}