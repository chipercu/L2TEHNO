package services;

import l2open.extensions.scripts.Functions;
import l2open.extensions.scripts.ScriptFile;
import l2open.gameserver.cache.Msg;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.tables.SkillTable;
import l2open.util.Files;

/**
 * Используется NPC Black Judge (id: 30981) для сниятия с игрока Death Penalty
 *
 * @Author: SYS
 * @Date: 13/9/2007
 */
public class RemoveDeathPenalty extends Functions implements ScriptFile
{
	public void showdialog()
	{
		L2Player player = (L2Player) getSelf();
		String htmltext;
		if(player.getDeathPenalty().getLevel() > 0)
		{
			htmltext = Files.read("data/scripts/services/RemoveDeathPenalty-1.htm", player);
			htmltext += "<a action=\"bypass -h scripts_services.RemoveDeathPenalty:remove\">Remove 1 level of Death Penalty (" + getPrice() + " adena).</a>";
		}
		else
			htmltext = Files.read("data/scripts/services/RemoveDeathPenalty-0.htm", player);

		show(htmltext, (L2Player) getSelf());
	}

	public void remove()
	{
		if(getNpc() == null)
			return;
		L2Player player = (L2Player) getSelf();
		if(player.getDeathPenalty().getLevel() > 0)
			if(player.getAdena() >= getPrice())
			{
				player.reduceAdena(getPrice(), true);
				getNpc().doCast(SkillTable.getInstance().getInfo(5077, 1), player, false);
			}
			else
				player.sendPacket(Msg.YOU_DO_NOT_HAVE_ENOUGH_ADENA);
		else
			show(Files.read("data/scripts/services/RemoveDeathPenalty-0.htm", player), player);
	}

	public int getPrice()
	{
		byte playerLvl = ((L2Player) getSelf()).getLevel();
		if(playerLvl <= 19)
			return 3600; // Non-grade (confirmed)
		else if(playerLvl >= 20 && playerLvl <= 39)
			return 16400; // D-grade
		else if(playerLvl >= 40 && playerLvl <= 51)
			return 36200; // C-grade
		else if(playerLvl >= 52 && playerLvl <= 60)
			return 50400; // B-grade (confirmed)
		else if(playerLvl >= 61 && playerLvl <= 75)
			return 78200; // A-grade
		else
			return 102800; // S-grade
	}

	public void onLoad()
	{
		_log.info("Loaded Service: NPC RemoveDeathPenalty");
	}

	public void onReload()
	{}

	public void onShutdown()
	{}
}