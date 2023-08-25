package services;

import l2open.extensions.scripts.Functions;
import l2open.extensions.scripts.ScriptFile;
import l2open.gameserver.model.L2Player;

/**
 * Используется NPC Lekon (id: 32557)
 *
 * @Author: SYS
 * @Date: 23/5/2010
 */
public class AirshipLicense extends Functions implements ScriptFile
{
	private static final int ENERGY_STAR_STONE = 13277;
	private static final int AIRSHIP_SUMMON_LICENSE = 13559;

	public void sell()
	{
		L2Player player = (L2Player) getSelf();

		if(player.getClan() == null || !player.isClanLeader() || player.getClan().getLevel() < 5)
		{
			show("data/html/default/32557-2.htm", player);
			return;
		}

		if(player.getClan().isHaveAirshipLicense() || Functions.getItemCount(player, AIRSHIP_SUMMON_LICENSE) > 0)
		{
			show("data/html/default/32557-4.htm", player);
			return;
		}

		if(Functions.removeItem(player, ENERGY_STAR_STONE, 10) != 10)
		{
			show("data/html/default/32557-3.htm", player);
			return;
		}

		Functions.addItem(player, AIRSHIP_SUMMON_LICENSE, 1);
	}

	public void onLoad()
	{}

	public void onReload()
	{}

	public void onShutdown()
	{}
}