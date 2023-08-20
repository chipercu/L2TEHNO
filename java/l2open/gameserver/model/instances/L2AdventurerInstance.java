package l2open.gameserver.model.instances;

import l2open.extensions.multilang.CustomMessage;
import l2open.gameserver.cache.Msg;
import l2open.gameserver.instancemanager.RaidBossSpawnManager;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.L2Spawn;
import l2open.gameserver.serverpackets.RadarControl;
import l2open.gameserver.templates.L2NpcTemplate;

import java.io.File;
import java.util.logging.Logger;

public class L2AdventurerInstance extends L2NpcInstance
{
	private static Logger _log = Logger.getLogger(L2AdventurerInstance.class.getName());

	public L2AdventurerInstance(int objectId, L2NpcTemplate template)
	{
		super(objectId, template);
	}

	@Override
	public void onBypassFeedback(L2Player player, String command)
	{
		if(!canBypassCheck(player, this))
			return;

		if(command.startsWith("npcfind_byid"))
			try
			{
				int bossId = Integer.parseInt(command.substring(12).trim());
				switch(RaidBossSpawnManager.getInstance().getRaidBossStatusId(bossId))
				{
					case ALIVE:
					case DEAD:
						L2Spawn spawn = RaidBossSpawnManager.getInstance().getSpawnTable().get(bossId);
						// Убираем и ставим флажок на карте и стрелку на компасе
						player.sendPacket(new RadarControl(2, 2, spawn.getLoc()), new RadarControl(0, 1, spawn.getLoc()));
						break;
					case UNDEFINED:
						player.sendMessage(new CustomMessage("l2open.gameserver.model.instances.L2AdventurerInstance.BossNotInGame", player).addNumber(bossId));
						break;
				}
			}
			catch(NumberFormatException e)
			{
				_log.warning("L2AdventurerInstance: Invalid Bypass to Server command parameter.");
			}
		else if(command.startsWith("raidInfo"))
		{
			int bossLevel = Integer.parseInt(command.substring(9).trim());

			String filename = "/data/html/adventurer_guildsman/raid_info/info.htm";
			if(bossLevel != 0)
				filename = "/data/html/adventurer_guildsman/raid_info/level" + bossLevel + ".htm";

			showChatWindow(player, filename);
		}
		else if(command.equalsIgnoreCase("questlist"))
			player.sendPacket(Msg.ExShowQuestInfo);
		else
			super.onBypassFeedback(player, command);
	}

	@Override
	public String getHtmlPath(int npcId, int val)
	{
		String pom;
		if(val == 0)
			pom = "" + npcId;
		else
			pom = npcId + "-" + val;

		String temp = "/data/html/adventurer_guildsman/" + pom + ".htm";

		File mainText = new File(temp);

		// Return the pathfile of the HTML file
		if(mainText.exists())
			return temp;

		// if the file is not found, the standard message "I have nothing to say to you" is returned
		return "/data/html/npcdefault.htm";
	}
}