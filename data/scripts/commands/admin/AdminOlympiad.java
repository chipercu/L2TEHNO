package commands.admin;

import l2open.config.ConfigValue;
import l2open.common.ThreadPoolManager;
import l2open.extensions.scripts.ScriptFile;
import l2open.gameserver.Announcements;
import l2open.gameserver.cache.Msg;
import l2open.gameserver.handler.AdminCommandHandler;
import l2open.gameserver.handler.IAdminCommandHandler;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.L2World;
import l2open.gameserver.model.entity.Hero;
import l2open.gameserver.model.entity.olympiad.*;
import l2open.gameserver.templates.StatsSet;
import l2open.util.GArray;

public class AdminOlympiad implements IAdminCommandHandler, ScriptFile
{
	private static enum Commands
	{
		admin_oly_save,
		admin_add_oly_points,
		admin_oly_start,
		admin_add_hero,
		admin_fix_noble_name,
		admin_oly_stop,
		admin_oly_end,
		admin_fix_noble_class
	}

	public boolean useAdminCommand(Enum comm, String[] wordList, String fullString, L2Player activeChar)
	{
		Commands command = (Commands) comm;

		switch(command)
		{
			case admin_fix_noble_class:
				if(activeChar.getTarget() == null || activeChar.getTarget().getPlayer() == null || wordList.length < 2)
				{
					activeChar.sendMessage("USAGE: //fix_noble_class <class_id>");
					activeChar.sendMessage("Please select noble character.");
					return false;
				}
				Olympiad.changeNobleClass(activeChar.getTarget().getPlayer().getObjectId(), Integer.parseInt(wordList[1]));
				activeChar.sendMessage("Successful set new class id: "+wordList[1]+" for "+activeChar.getTarget().getPlayer().getName());
				break;
			case admin_oly_save:
			{
				if(!ConfigValue.EnableOlympiad)
					return false;

				try
				{
					OlympiadDatabase.save();
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}
				activeChar.sendMessage("olympaid data saved.");
				break;
			}
			case admin_add_oly_points:
			{
				if(wordList.length < 3)
				{
					activeChar.sendMessage("Command syntax: //add_oly_points <char_name> <point_to_add>");
					activeChar.sendMessage("This command can be applied only for online players.");
					return false;
				}

				L2Player player = L2World.getPlayer(wordList[1]);
				if(player == null)
				{
					activeChar.sendMessage("Character " + wordList[1] + " not found in game.");
					return false;
				}

				int pointToAdd;

				try
				{
					pointToAdd = Integer.parseInt(wordList[2]);
				}
				catch(NumberFormatException e)
				{
					activeChar.sendMessage("Please specify integer value for olympiad points.");
					return false;
				}

				int curPoints = Olympiad.getNoblePoints(player.getObjectId());
				Olympiad.manualSetNoblePoints(player.getObjectId(), curPoints + pointToAdd);
				int newPoints = Olympiad.getNoblePoints(player.getObjectId());

				activeChar.sendMessage("Added " + pointToAdd + " points to character " + player.getName());
				activeChar.sendMessage("Old points: " + curPoints + ", new points: " + newPoints);
				break;
			}
			case admin_oly_start:
			{
				Olympiad._manager = new OlympiadManager();
				Olympiad._inCompPeriod = true;

				new Thread(Olympiad._manager).start();

				Announcements.getInstance().announceToAll(Msg.THE_OLYMPIAD_GAME_HAS_STARTED);
				Olympiad._log.info("Olympiad System: Olympiad Game Started");
				break;
			}
			case admin_oly_stop:
			{
				Olympiad._inCompPeriod = false;
				Announcements.getInstance().announceToAll(Msg.THE_OLYMPIAD_GAME_HAS_ENDED);
				Olympiad._log.info("Olympiad System: Olympiad Game Ended");

				try
				{
					OlympiadDatabase.save();
					OlympiadStat.set_day_reward();
				}
				catch(Exception e)
				{
					Olympiad._log.warning("Olympiad System: Failed to save Olympiad configuration:");
					e.printStackTrace();
				}

				break;
			}
			case admin_add_hero:
			{
				if(wordList.length < 2)
				{
					activeChar.sendMessage("Command syntax: //add_hero <char_name>");
					activeChar.sendMessage("This command can be applied only for online players.");
					return false;
				}

				L2Player player = L2World.getPlayer(wordList[1]);
				if(player == null)
				{
					activeChar.sendMessage("Character " + wordList[1] + " not found in game.");
					return false;
				}

				StatsSet hero = new StatsSet();
				hero.set(Olympiad.CLASS_ID, player.getBaseClassId());
				hero.set(Olympiad.CHAR_ID, player.getObjectId());
				hero.set(Olympiad.CHAR_NAME, player.getName());

				GArray<StatsSet> heroesToBe = new GArray<StatsSet>();
				heroesToBe.add(hero);

				Hero.getInstance().computeNewHeroes(heroesToBe);

				activeChar.sendMessage("Hero status added to player " + player.getName());
				break;
			}
			case admin_fix_noble_name:
			{
				if(activeChar.getTarget() == null || activeChar.getTarget().getPlayer() == null)
				{
					activeChar.sendMessage("USAGE: //fix_noble_name");
					activeChar.sendMessage("Please select noble character.");
					return false;
				}
				Olympiad.changeNobleName(activeChar.getTarget().getPlayer().getObjectId(), activeChar.getTarget().getPlayer().getName());
				break;
			}
			case admin_oly_end:
				Olympiad._olympiadEnd = System.currentTimeMillis()+30000;
				if(Olympiad._scheduledOlympiadEnd != null)
					Olympiad._scheduledOlympiadEnd.cancel(true);

				Olympiad._scheduledOlympiadEnd = ThreadPoolManager.getInstance().schedule(new OlympiadEndTask(), 2000);
				break;
		}

		return true;
	}

	public Enum[] getAdminCommandEnum()
	{
		return Commands.values();
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