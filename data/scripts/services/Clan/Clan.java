package services.Clan;

import l2open.config.ConfigValue;
import l2open.extensions.multilang.CustomMessage;
import l2open.extensions.scripts.Functions;
import l2open.extensions.scripts.ScriptFile;
import l2open.gameserver.common.DifferentMethods;
import l2open.gameserver.model.L2Clan;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.tables.player.PlayerData;
import l2open.util.Files;
import l2open.util.Log;
import l2open.util.Util;

public class Clan extends Functions implements ScriptFile
{
	public void show_level()
	{
		L2Player player = (L2Player) getSelf();

		if(player == null)
			return;

		String html = "";

		if(player.getClan() == null)
			html = Files.read("data/scripts/services/Clan/no_clan.htm", player);
		else if(!player.isClanLeader())
			html = Files.read("data/scripts/services/Clan/not_leader.htm", player);
		else
		{
			L2Clan clan = player.getClan();
			int level = clan.getLevel();

			html = Files.read("data/scripts/services/Clan/level.htm", player);
			html = html.replace("<?level?>", String.valueOf(level));
			html = html.replace("<?item?>", level >= 11 ? "--" : DifferentMethods.getItemName(ConfigValue.ClanLevelItem[level]));
			html = html.replace("<?count?>", level >= 11 ? "--" : Util.formatAdena(ConfigValue.ClanLevelPrice[level]));
		}

		show(html, player);
	}

	public void show_point()
	{
		L2Player player = (L2Player) getSelf();

		if(player == null)
			return;

		String html = "";

		if(player.getClan() == null)
			html = Files.read("data/scripts/services/Clan/no_clan.htm", player);
		else if(!player.isClanLeader())
			html = Files.read("data/scripts/services/Clan/not_leader.htm", player);
		else
		{
			L2Clan clan = player.getClan();

			html = Files.read("data/scripts/services/Clan/point.htm", player);
			html = html.replace("<?point?>", Util.formatAdena(clan.getReputationScore()));
			html = html.replace("<?item?>", DifferentMethods.getItemName(ConfigValue.ClanPointItem));
			html = html.replace("<?info?>", (Util.formatAdena(ConfigValue.ClanPointPrice[0]) + " " + DifferentMethods.declension(player, ConfigValue.ClanPointPrice[0], "Point")));
			html = html.replace("<?count?>", Util.formatAdena(ConfigValue.ClanPointPrice[1]));
		}

		show(html, player);
	}

	public void level()
	{
		if(!ConfigValue.ClanLevelEnable)
			return;

		L2Player player = (L2Player) getSelf();

		if(player == null)
			return;

		String html = "";

		if(player.getClan() == null)
			html = Files.read("data/scripts/services/Clan/no_clan.htm", player);
		else if(!player.isClanLeader())
			html = Files.read("data/scripts/services/Clan/not_leader.htm", player);
		else
		{
			L2Clan clan = player.getClan();
			int level = clan.getLevel();

			if(level >= 11)
				html = Files.read("data/scripts/services/Clan/max_level.htm", player);
			else
			{
				if(DifferentMethods.getPay(player, ConfigValue.ClanLevelItem[level], ConfigValue.ClanLevelPrice[level], true))
				{
					clan.setLevel((byte) (level + 1));
					player.sendMessage("Уровень вашего клана был повышен до " + (level + 1) + ".");
					PlayerData.getInstance().updateClanInDB(player.getClan());
					player.getClan().broadcastClanStatus(true, true, false);
					Log.add("Character " + player + " Clan " + player.getClan().getName() + " changed change level at " + level + " to " + (level + 1), "services");

					level = clan.getLevel();
					html = Files.read("data/scripts/services/Clan/level.htm", player);
					html = html.replace("<?level?>", String.valueOf(level));
					html = html.replace("<?item?>", level >= 11 ? "--" : DifferentMethods.getItemName(ConfigValue.ClanLevelItem[level]));
					html = html.replace("<?count?>", level >= 11 ? "--" : Util.formatAdena(ConfigValue.ClanLevelPrice[level]));
				}
				else
					html = Files.read("data/scripts/services/Clan/no_money.htm", player);
			}
		}
		show(html, player);
	}

	public void ask_point(String[] param)
	{
		L2Player player = (L2Player) getSelf();
		int i = Integer.parseInt(param[0]);

		long point = (i / ConfigValue.ClanPointPrice[0]) * ConfigValue.ClanPointPrice[0];

		if(point < ConfigValue.ClanPointPrice[0])
		{
			String html = Files.read("data/scripts/services/Clan/point_min.htm", player);
			html = html.replace("<?point?>", Util.formatAdena(ConfigValue.ClanPointPrice[0]) + " " + DifferentMethods.declension(player, ConfigValue.ClanPointPrice[0], "Point"));
			show(html, player);
			player.setPointToBuy(0);
		}
		else
		{
			long price = (point / ConfigValue.ClanPointPrice[0] * ConfigValue.ClanPointPrice[1]);
			CustomMessage msg = new CustomMessage("services.Clan.point", player).addString(Util.formatAdena(point)).addString(DifferentMethods.declension(player, point, "Point")).addString(Util.formatAdena(price)).addItemName(ConfigValue.ClanPointItem);

			player.scriptRequest(msg.toString(), "services.Clan.Clan:point", new Object[0]);
			player.setPointToBuy((int)point);
		}
	}

	public void point()
	{
		if(!ConfigValue.ClanPointEnable)
			return;

		L2Player player = (L2Player) getSelf();

		if(player == null)
			return;

		String html = "";

		if(player.getPointToBuy() < ConfigValue.ClanPointPrice[0])
		{
			html = Files.read("data/scripts/services/Clan/point_min.htm", player);
			html = html.replace("<?point?>", Util.formatAdena(ConfigValue.ClanPointPrice[0]) + " " + DifferentMethods.declension(player, ConfigValue.ClanPointPrice[0], "Point"));
		}
		else if(player.getClan() == null)
			html = Files.read("data/scripts/services/Clan/no_clan.htm", player);
		else if(!player.isClanLeader())
			html = Files.read("data/scripts/services/Clan/not_leader.htm", player);
		else
		{
			L2Clan clan = player.getClan();

			if(clan.getLevel() < 5)
			{
				html = Files.read("data/scripts/services/Clan/min_level.htm", player);
				html = html.replace("<?lvl?>", String.valueOf(5));
			}
			else
			{
				long point = (long)player.getPointToBuy();
				long price = (point / ConfigValue.ClanPointPrice[0] * ConfigValue.ClanPointPrice[1]);
				if(DifferentMethods.getPay(player, ConfigValue.ClanPointItem, price, true))
				{
					player.getClan().incReputation((int)point, false, "services.Clan.Clan:point");
					PlayerData.getInstance().updateClanInDB(player.getClan());
					player.getClan().broadcastClanStatus(true, true, false);
					Log.add("Character " + player + " Clan " + player.getClan().getName() + " buy point " + player.getPointToBuy(), "services");

					html = Files.read("data/scripts/services/Clan/point.htm", player);
					html = html.replace("<?point?>", Util.formatAdena(clan.getReputationScore()));
					html = html.replace("<?item?>", DifferentMethods.getItemName(ConfigValue.ClanPointItem));
					html = html.replace("<?info?>", (Util.formatAdena(ConfigValue.ClanPointPrice[0]) + " " + DifferentMethods.declension(player, ConfigValue.ClanPointPrice[0], "Point")));
					html = html.replace("<?count?>", Util.formatAdena(ConfigValue.ClanPointPrice[1]));
				}
				else
					html = Files.read("data/scripts/services/Clan/no_money.htm", player);
			}
		}
		show(html, player);
		player.setPointToBuy(0);
	}

	@Override
	public void onLoad()
	{
		_log.info("Loaded Service: Clan function.");
	}

	@Override
	public void onReload()
	{}

	@Override
	public void onShutdown()
	{}
}
