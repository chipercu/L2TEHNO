package services;

import l2open.config.ConfigValue;
import l2open.database.L2DatabaseFactory;
import l2open.database.mysql;
import l2open.extensions.multilang.CustomMessage;
import l2open.extensions.scripts.Functions;
import l2open.extensions.scripts.ScriptFile;
import l2open.gameserver.cache.Msg;
import l2open.gameserver.common.DifferentMethods;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.items.L2ItemInstance;
import l2open.gameserver.serverpackets.ExBrPremiumState;
import l2open.gameserver.templates.L2Item;
import l2open.gameserver.xml.ItemTemplates;
import l2open.util.Files;
import l2open.util.Log;

import java.sql.SQLException;
import java.util.Date;

public class RateBonus extends Functions implements ScriptFile
{
	public void list()
	{
		L2Player player = (L2Player) getSelf();
		String html;
		if(player.getNetConnection().getBonus() == 1)
		{
			html = Files.read("data/scripts/services/RateBonus.htm", player);

			String add = new String();
			for(int i = 0; i < ConfigValue.RateBonusTime.length; i++)
				add += "<button value=\"" + new CustomMessage("communityboard.cabinet.premium.button", player).addString(String.valueOf(ConfigValue.RateBonusValue[i])).addString(ConfigValue.RateBonusTime[i] + " " + DifferentMethods.declension(player, ConfigValue.RateBonusTime[i], "Days")) + "\" action=\"bypass -h scripts_services.RateBonus:get " + i + "\" width=200 height=29 back=\"L2UI_CT1.OlympiadWnd_DF_HeroConfirm_Down\" fore=\"L2UI_CT1.OlympiadWnd_DF_HeroConfirm\"><br1>" + new CustomMessage("scripts.services.cost", player).addNumber(ConfigValue.RateBonusPrice[i]).addString(DifferentMethods.getItemName(ConfigValue.RateBonusItem[i])) + "";

			html = html.replaceFirst("%toreplace%", add);
		}
		else if(player.getNetConnection().getBonus() > 1)
		{
			long endtime = player.getNetConnection().getBonusExpire();
			if(endtime >= 0)
				html = Files.read("data/scripts/services/RateBonusAlready.htm", player).replaceFirst("endtime", new Date(endtime * 1000L).toString());
			else
				html = Files.read("data/scripts/services/RateBonusInfinite.htm", player);
		}
		else
			html = Files.read("data/scripts/services/RateBonusNo.htm", player);
		show(html, player);
	}

	public void get(String[] param)
	{
		L2Player player = (L2Player) getSelf();

		if(player.getNetConnection().getBonus() > 1)
		{
			String html;
			long endtime = player.getNetConnection().getBonusExpire();
			if(endtime >= 0)
				html = Files.read("data/scripts/services/RateBonusAlready.htm", player).replaceFirst("endtime", new Date(endtime * 1000L).toString());
			else
				html = Files.read("data/scripts/services/RateBonusInfinite.htm", player);
			show(html, player);
		}
		else
		{
			int i = Integer.parseInt(param[0]);

			L2Item item = ItemTemplates.getInstance().getTemplate(ConfigValue.RateBonusItem[i]);
			L2ItemInstance pay = player.getInventory().getItemByItemId(item.getItemId());
			if(pay != null && pay.getCount() >= ConfigValue.RateBonusPrice[i])
			{
				player.getInventory().destroyItem(pay, ConfigValue.RateBonusPrice[i], true);
				Log.add(player.getName() + "|" + player.getObjectId() + "|rate bonus|" + ConfigValue.RateBonusValue[i] + "|" + ConfigValue.RateBonusTime[i] + "|", "services");
				try
				{
					mysql.setEx(L2DatabaseFactory.getInstanceLogin(), "UPDATE `accounts` SET `bonus`=?,`bonus_expire`=UNIX_TIMESTAMP()+" + ConfigValue.RateBonusTime[i] + "*24*60*60 WHERE `login`=?", ConfigValue.RateBonusValue[i], player.getAccountName());
				}
				catch(SQLException e)
				{
					e.printStackTrace();
				}
				player.getNetConnection().setBonus(ConfigValue.RateBonusValue[i]);
				player.getNetConnection().setBonusExpire(System.currentTimeMillis() / 1000 + ConfigValue.RateBonusTime[i] * 24 * 60 * 60);
				player.restoreBonus();
				player.sendPacket(new ExBrPremiumState(player, 1));
				if(player.getParty() != null)
					player.getParty().recalculatePartyData();
				show(Files.read("data/scripts/services/RateBonusGet.htm", player), player);
			}
			else if(ConfigValue.RateBonusItem[i] == 57)
				player.sendPacket(Msg.YOU_DO_NOT_HAVE_ENOUGH_ADENA);
			else
				player.sendPacket(Msg.INCORRECT_ITEM_COUNT);
		}
	}

	public void update(String[] param)
	{
		L2Player player = (L2Player) getSelf();

		int i = Integer.parseInt(param[0]);
		if(DifferentMethods.getPay(player, ConfigValue.RateBonusItem[i], ConfigValue.RateBonusPrice[i], true))
		{

			int Total = (int) ((player.getNetConnection().getBonusExpire() - System.currentTimeMillis() / 1000L));
			int Day = Math.round(Total / 60 / 60 / 24);
			long time = (Day + ConfigValue.RateBonusTime[i]) * 24 * 60 * 60;
			try
			{
				mysql.setEx(L2DatabaseFactory.getInstanceLogin(), "UPDATE `accounts` SET `bonus`=?,`bonus_expire`=UNIX_TIMESTAMP()+" + time + " WHERE `login`=?", ConfigValue.RateBonusValue[i], player.getAccountName());
			}
			catch(SQLException e)
			{
				e.printStackTrace();
			}
			player.getNetConnection().setBonus(ConfigValue.RateBonusValue[i]);
			player.getNetConnection().setBonusExpire(System.currentTimeMillis() / 1000 + time);
			player.restoreBonus();
			player.sendPacket(new ExBrPremiumState(player, 1));
			if(player.getParty() != null)
				player.getParty().recalculatePartyData();
			show(Files.read("data/scripts/services/RateBonusGet.htm", player), player);
			Log.add(player.getName() + "|" + player.getObjectId() + "|rate bonus|" + ConfigValue.RateBonusValue[i] + "|" + ConfigValue.RateBonusTime[i] + "|", "services");
		}
	}

	public void howtogetcol()
	{
		show("data/scripts/services/howtogetcol.htm", (L2Player) getSelf());
	}

	public void onLoad()
	{
		_log.info("Loaded Service: Rate bonus");
	}

	public void onReload()
	{}

	public void onShutdown()
	{}
}