package npc.model;

import java.util.StringTokenizer;

import l2open.config.ConfigValue;
import l2open.extensions.scripts.ScriptFile;
import l2open.gameserver.cache.Msg;
import l2open.gameserver.communitybbs.Manager.ClassBBSManager;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.base.ClassId;
import l2open.gameserver.model.instances.L2MerchantInstance;
import l2open.gameserver.model.items.L2ItemInstance;
import l2open.gameserver.serverpackets.NpcHtmlMessage;
import l2open.gameserver.tables.NpcTable;
import l2open.gameserver.templates.L2Item;
import l2open.gameserver.templates.L2NpcTemplate;
import l2open.gameserver.xml.ItemTemplates;
import l2open.util.Files;
import l2open.util.Util;

public final class L2ClassMasterInstance extends L2MerchantInstance implements ScriptFile
{
	public L2ClassMasterInstance(int objectId, L2NpcTemplate template)
	{
		super(objectId, template);
	}

	private static boolean jobLevelContains(int lvl)
	{
		switch(lvl)
		{
			case 1:
				if(ConfigValue.AllowClassMasters[0] != 0)
					return true;
				return false;
			case 2:
				if(ConfigValue.AllowClassMasters[1] != 0)
					return true;
				return false;
			case 3:
				if(ConfigValue.AllowClassMasters[2] != 0)
					return true;
				return false;
		}
		return false;
	}

	private String makeMessage(L2Player player)
	{
		ClassId classId = player.getClassId();

		int jobLevel = classId.getLevel();
		int level = player.getLevel();

		StringBuilder html = new StringBuilder();
		if(ConfigValue.AllowClassMasters[0] == 0 && ConfigValue.AllowClassMasters[1] == 0 && ConfigValue.AllowClassMasters[2] == 0)
			jobLevel = 4;
		if((level >= 20 && jobLevel == 1 || level >= 40 && jobLevel == 2 || level >= ConfigValue.JobLevel3 && jobLevel == 3) && jobLevelContains(jobLevel))
		{
			L2Item item = ItemTemplates.getInstance().getTemplate(ConfigValue.ClassMastersPriceItem[jobLevel-1]);
			if(ConfigValue.ClassMastersPrice[jobLevel-1] > 0)
				html.append("Price: ").append(Util.formatAdena(ConfigValue.ClassMastersPrice[jobLevel-1])).append(" ").append(item.getName()).append("<br1>");
			for(ClassId cid : ClassId.values())
			{
				// Инспектор является наследником trooper и warder, но сменить его как профессию нельзя,
				// т.к. это сабкласс. Наследуется с целью получения скилов родителей.
				if(cid == ClassId.inspector)
					continue;
				if(cid.childOf(classId) && cid.getLevel() == classId.getLevel() + 1)
					html.append("<a action=\"bypass -h npc_").append(getObjectId()).append("_change_class ").append(cid.getId()).append(" ").append(ConfigValue.ClassMastersPrice[jobLevel-1]).append("\">").append(cid.name()).append("</a><br>");
			}
			player.sendPacket(new NpcHtmlMessage(player, this).setHtml(html.toString()));
		}
		else
			switch(jobLevel)
			{
				case 1:
                    if (player.getLang().equalsIgnoreCase("ru")) {
                        html.append("Возвращайтесь, когда вы достигнете 20 уровня, чтобы сменить вашу профессию.");
                    } else {
					    html.append("Come back here when you reached level 20 to change your class.");
                    }
					break;
				case 2:
                    if (player.getLang().equalsIgnoreCase("ru")) {
                        html.append("Возвращайтесь, когда вы достигнете 40 уровня, чтобы сменить вашу профессию.");
                    } else {
					    html.append("Come back here when you reached level 40 to change your class.");
                    }
					break;
				case 3:
                    if (player.getLang().equalsIgnoreCase("ru")) {
                        html.append("Возвращайтесь, когда вы достигнете "+ConfigValue.JobLevel3+" уровня, чтобы сменить вашу профессию.");
                    } else {
					    html.append("Come back here when you reached level "+ConfigValue.JobLevel3+" to change your class.");
                    }
					break;
				case 4:
                    if (player.getLang().equalsIgnoreCase("ru")) {
                        html.append("Для вас больше нет доступных профессий.");
                    } else {
                        html.append("There is no class changes for you any more.");
                    }
					break;
			}
		return html.toString();
	}

	@Override
	public void showChatWindow(L2Player player, int val)
	{
		NpcHtmlMessage msg = new NpcHtmlMessage(player, this, null, 0);
		String html = Files.read("data/html/custom/31860.htm", player);
        if(player.getLang().equalsIgnoreCase("ru"))
		{
            if(ConfigValue.CM_BasicShop)
                html += "<br><a action=\"bypass -h npc_%objectId%_Buy 318601\">Купить обычные вещи</a>";
            if(ConfigValue.CM_CoLShop)
                html += "<br><a action=\"bypass -h npc_%objectId%_Multisell 1\">Специальный магазин</a>";
            if(ConfigValue.NickChangeEnabled)
                html += "<br><a action=\"bypass -h scripts_services.Rename:rename_page\">Смена ника</a>";
            if(ConfigValue.SexChangeEnabled)
                html += "<br><a action=\"bypass -h scripts_services.Rename:changesex_page\">Смена пола</a>";
            if(ConfigValue.BaseChangeEnabled)
                html += "<br><a action=\"bypass -h scripts_services.Rename:changebase_page\">Смена базового класса</a>";
            if(ConfigValue.SeparateSubEnabled)
                html += "<br><a action=\"bypass -h scripts_services.Rename:separate_page\">Отделение сабкласа</a>";
            if(ConfigValue.NickColorChangeEnabled)
                html += "<br><a action=\"bypass -h scripts_services.NickColor:list 1\">Смена цвета ника</a>";
            if(ConfigValue.RateBonusEnabled)
                html += "<br><a action=\"bypass -h scripts_services.RateBonus:list\">Премиум акаунт</a>";
            if(ConfigValue.NoblessSellEnabled && !player.isNoble())
                html += "<br><a action=\"bypass -h scripts_services.NoblessSell:get\">Покупка нублесса</a>";
            if(ConfigValue.ClanNameChangeEnabled)
                html += "<br><a action=\"bypass -h scripts_services.Rename:rename_clan_page\">Смена названия клана</a>";
            if(ConfigValue.HowToGetCoL)
                html += "<br><a action=\"bypass -h scripts_services.RateBonus:howtogetcol\">Как получить Coin of Luck</a>";
            if(ConfigValue.CharToAcc)
                html += "<br><a action=\"bypass -h scripts_services.Account:CharToAcc\">Перенос персонажей между аккаунтами</a>";
        }
		else
		{
            if(ConfigValue.CM_BasicShop)
                html += "<br><a action=\"bypass -h npc_%objectId%_Buy 318601\">Buy basic items</a>";
            if(ConfigValue.CM_CoLShop)
                html += "<br><a action=\"bypass -h npc_%objectId%_Multisell 1\">Special shop</a>";
            if(ConfigValue.NickChangeEnabled)
                html += "<br><a action=\"bypass -h scripts_services.Rename:rename_page\">Nick change</a>";
            if(ConfigValue.SexChangeEnabled)
                html += "<br><a action=\"bypass -h scripts_services.Rename:changesex_page\">Sex change</a>";
            if(ConfigValue.BaseChangeEnabled)
                html += "<br><a action=\"bypass -h scripts_services.Rename:changebase_page\">Base class change</a>";
            if(ConfigValue.SeparateSubEnabled)
                html += "<br><a action=\"bypass -h scripts_services.Rename:separate_page\">Separate subclass</a>";
            if(ConfigValue.NickColorChangeEnabled)
                html += "<br><a action=\"bypass -h scripts_services.NickColor:list 1\">Nick color change</a>";
            if(ConfigValue.RateBonusEnabled)
                html += "<br><a action=\"bypass -h scripts_services.RateBonus:list\">Luck boost</a>";
            if(ConfigValue.NoblessSellEnabled && !player.isNoble())
                html += "<br><a action=\"bypass -h scripts_services.NoblessSell:get\">Become a Nobless</a>";
            if(ConfigValue.ClanNameChangeEnabled)
                html += "<br><a action=\"bypass -h scripts_services.Rename:rename_clan_page\">Clan name change</a>";
            if(ConfigValue.HowToGetCoL)
                html += "<br><a action=\"bypass -h scripts_services.RateBonus:howtogetcol\">How to get Coin of Luck</a>";
            if(ConfigValue.CharToAcc)
                html += "<br><a action=\"bypass -h scripts_services.Account:CharToAcc\">Transfer characters between accounts</a>";
        }
		msg.setHtml(html);
		msg.replace("%classmaster%", makeMessage(player));
		player.sendPacket(msg);
	}

	@Override
	public void onBypassFeedback(L2Player player, String command)
	{
		if(!canBypassCheck(player, this))
			return;

		StringTokenizer st = new StringTokenizer(command);
		if(st.nextToken().equals("change_class"))
		{
			short val = Short.parseShort(st.nextToken());
			long price = Long.parseLong(st.nextToken());
			int jobLevel = player.getClassId().getLevel();
			L2Item item = ItemTemplates.getInstance().getTemplate(ConfigValue.ClassMastersPriceItem[jobLevel-1]);
			L2ItemInstance pay = player.getInventory().getItemByItemId(item.getItemId());
			if(pay != null && pay.getCount() >= price)
			{
				player.getInventory().destroyItem(pay, price, true);
				changeClass(player, val);
			}
			else if(ConfigValue.ClassMastersPriceItem[jobLevel-1] == 57)
				player.sendPacket(Msg.YOU_DO_NOT_HAVE_ENOUGH_ADENA);
			else
				player.sendPacket(Msg.INCORRECT_ITEM_COUNT);
		}
		else
			super.onBypassFeedback(player, command);
	}

	private void changeClass(L2Player player, short val)
	{
		ClassId class_ = player.getClassId();
		ClassId cid = ClassId.values()[val];

		if(cid.childOf(class_) && cid.level() == class_.level() + 1)
		{
			if(player.getClassId().getLevel() == 3)
				player.sendPacket(Msg.YOU_HAVE_COMPLETED_THE_QUEST_FOR_3RD_OCCUPATION_CHANGE_AND_MOVED_TO_ANOTHER_CLASS_CONGRATULATIONS); // для 3 профы
			else
				player.sendPacket(Msg.CONGRATULATIONS_YOU_HAVE_TRANSFERRED_TO_A_NEW_CLASS); // для 1 и 2 профы

			player.setClassId(val, false);
			if(player.getClassId().getLevel() == 4)
				ClassBBSManager.ClassBBSManagerAddReward(player);
			player.broadcastUserInfo(true);
		}
	}

	@Override
	public void onLoad()
	{
		L2NpcTemplate t = NpcTable.getTemplate(31860);
		t.title = "Service Manager";
	}

	@Override
	public void onReload()
	{
		L2NpcTemplate t = NpcTable.getTemplate(31860);
		t.title = "Service Manager";
	}

	@Override
	public void onShutdown()
	{}

}