package communityboard.manager;

import l2open.common.ThreadPoolManager;
import l2open.config.ConfigValue;
import l2open.database.*;
import l2open.extensions.scripts.*;
import l2open.gameserver.cache.*;
import l2open.gameserver.common.DifferentMethods;
import l2open.gameserver.communitybbs.Manager.*;
import l2open.gameserver.handler.*;
import l2open.gameserver.model.*;
import l2open.gameserver.model.base.*;
import l2open.gameserver.model.instances.L2HennaInstance;
import l2open.gameserver.model.items.Inventory;
import l2open.gameserver.model.items.L2ItemInstance;
import l2open.gameserver.model.items.L2ItemInstance.ItemClass;
import l2open.gameserver.model.items.Warehouse;
import l2open.gameserver.model.items.Warehouse.WarehouseType;
import l2open.gameserver.serverpackets.*;
import l2open.gameserver.tables.*;
import l2open.gameserver.tables.player.PlayerData;
import l2open.gameserver.templates.L2Item;
import l2open.gameserver.xml.ItemTemplates;
import l2open.util.*;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.*;
import java.util.*;
import java.util.logging.Logger;

/**
 * @author Diagod
 */
/**
bypass -h _bbs_visual_enchant_ok:id_item:count_item:visual_enchant_level
bypass -h _bbs_visual_enchant_ok:4037:100:70

bypass -h _bbs_visual_enchant_test:visual_enchant_level
bypass -h _bbs_visual_enchant_test:70

bypass -h _bbs_visual_enchant_index   - этот байпас открывает 75.htm в корне штмл папки КБ, в ней можно вывести название одетого оружия <?item_name?> и его иконку <?item_icon?>, если оружие не одето, оно штмл не откроет, а пошлёт тебя
**/
/**
bypass -h _bbs_visual_s_test_list:0

VisualSWearList=
VisualSWearTime=10
########################################
bypass -h _bbs_visual_s_list:0

VisualSSetList = 15544,221,220,2507
VisualSNoSetList = 15544,221,220,2507
VisualSPriceId = 57
VisualSPriceCount = 1000
**/

/**
bypass -h _bbs_visual_test_list:0

VisualWearList=
VisualWearTime=10
 **/
 /**
 
bypass -h _bbs_visual_w_test_list:0

VisualWWearList=
VisualWWearTime=10
 **/
/**
bypass -h _bbs_visual_list:0

# список предметов, которые будут вставлятся.
VisualSetList = 
# список предметов, в который нельзя вставлять.
VisualNoSetList = 
# предмет взымаемый за вставку
VisualPriceId = 
# количество VisualPriceId
VisualPriceCount = 

########################################
bypass -h _bbs_visual_w_list:0

VisualWSetList = 15544,221,220,2507
VisualWNoSetList = 15544,221,220,2507
VisualWPriceId = 57
VisualWPriceCount = 1000
**/
public class CommunityBoardServices extends BaseBBSManager implements ICommunityHandler, ScriptFile
{
	static final Logger _log = Logger.getLogger(CommunityBoardServices.class.getName());

	private static enum Commands
	{
		_bbs_referal_get,
		_bbs_referal_html,
		_bbs_visual_enchant_test,
		_bbs_visual_enchant_ok,
		_bbs_visual_enchant,
		_bbs_visual_enchant_index,
		_bbs_visual_enchant_dell,
		_bbs_visual_test_list,
		_bbs_visual_test_ok,
		_bbs_visual_list,
		_bbs_visual,
		_bbs_visual_ok,
		_bbs_visual_dell,
		_bbs_visual_w_list,
		_bbs_visual_w,
		_bbs_visual_w_ok,
		_bbs_visual_w_dell,
		_bbs_deposit_p,
		_bbs_withdraw_p,
		_bbs_deposit_c,
		_bbs_withdraw_c,
		_bbs_add_draw,
		_bbs_remove_list_draw,
		_bbs_remove_draw,
		_bbs_npc_html,
		_bbs_visual_w_test_list,
		_bbs_visual_w_test_ok,
		_bbs_visual_s_list,
		_bbs_visual_s,
		_bbs_visual_s_ok,
		_bbs_visual_s_dell,
		_bbs_visual_s_test_list,
		_bbs_visual_s_test_ok,
		_bbs_change_lang,
		_bbs_skilllearn,
		_bbs_exchange
	}

	private static SimpleDateFormat form = new SimpleDateFormat("HH:mm");

	public void parsecmd(String command, final L2Player player)
	{
		if(player.getEventMaster() != null && player.getEventMaster().blockBbs())
			return;
		if(player.is_block || player.isInEvent() > 0)
			return;
		else if(command.startsWith("_bbs_skilllearn"))
		{
			if(player.getTransformation() != 0)
			{
				player.sendMessage("Нельзя изучать умения, находясь в трансформации.");
				return;
			}

			ClassId classId = player.getClassId();
			if(classId == null)
				return;

			AcquireSkillList asl = new AcquireSkillList(AcquireSkillList.USUAL);
			int counts = 0;

			GArray<L2SkillLearn> skills = player.getAvailableSkills(classId);
			for(L2SkillLearn s : skills)
			{
				if(s.getItemCount() == -1)
					continue;
				L2Skill sk = SkillTable.getInstance().getInfo(s.getId(), s.getLevel());
				if(sk == null || !sk.getCanLearn(player.getClassId()))
					continue;
				int cost = SkillTreeTable.getInstance().getSkillCost(player, sk);
				counts++;
				asl.addSkill(s.getId(), s.getLevel(), s.getLevel(), cost, 0);
			}

			if(counts == 0)
			{
				int minlevel = SkillTreeTable.getInstance().getMinLevelForNewSkill(player, classId);
				if(minlevel > 0)
				{
					SystemMessage sm = new SystemMessage(SystemMessage.YOU_DO_NOT_HAVE_ANY_FURTHER_SKILLS_TO_LEARN__COME_BACK_WHEN_YOU_HAVE_REACHED_LEVEL_S1);
					sm.addNumber(minlevel);
					player.sendPacket(sm);
				}
				else
				{
					player.sendMessage("Вы уже изучили все доступные для вас умения.");
				}
			}
			else
			{
				player.setLastBbsOperaion(command);
				player.sendPacket(asl);
			}
			player.sendActionFailed();	
		}
		else if(command.startsWith("_bbs_change_lang"))
		{
			String lang = command.substring(17);
			if(lang.equalsIgnoreCase("en"))
				player.setVar("lang@", "en");
			else if(lang.equalsIgnoreCase("ru"))
				player.setVar("lang@", "ru");
		}
		else if(command.startsWith("_bbs_npc_html"))
		{
			String content = readHtml(ConfigValue.CommunityBoardHtmlRoot+command.substring(14), player);
			NpcHtmlMessage html = new NpcHtmlMessage(player, null);
			html.setHtml(content);
			player.sendPacket(html);
		}
		else if(command.startsWith("_bbs_remove_draw"))
		{
			int slot = Integer.parseInt(command.substring(17));
			PlayerData.getInstance().removeHenna(player, slot);
		}
		else if(command.equals("_bbs_remove_list_draw"))
		{
			StringBuffer html1 = new StringBuffer("<html><body>");
			html1.append("Select symbol you would like to remove:<br><br>");
			boolean hasHennas = false;
			for(int i = 1; i <= 3; i++)
			{
				L2HennaInstance henna = player.getHenna(i);
				if(henna != null)
				{
					hasHennas = true;
					html1.append("<a action=\"bypass -h _bbs_remove_draw " + i + "\">" + henna.getName() + "</a><br>");
				}
			}
			if(!hasHennas)
				html1.append("You don't have any symbol to remove!");
			html1.append("</body></html>");

			NpcHtmlMessage html = new NpcHtmlMessage(player, null);
			html.setHtml(html1.toString());
			player.sendPacket(html);
		}
		else if(command.equals("_bbs_add_draw"))
		{
			L2HennaInstance[] henna = HennaTreeTable.getInstance().getAvailableHenna(player.getClassId(), player.getSex());
			HennaEquipList hel = new HennaEquipList(player, henna);
			player.sendPacket(hel);
		}
		else if(command.equals("_bbs_withdraw_c"))
		{
			if(!player.getPlayerAccess().UseWarehouse)
				return;

			if(player.getClan() == null)
			{
				player.sendActionFailed();
				return;
			}

			L2Clan _clan = player.getClan();

			if(_clan.getLevel() == 0)
			{
				player.sendPacket(Msg.ONLY_CLANS_OF_CLAN_LEVEL_1_OR_HIGHER_CAN_USE_A_CLAN_WAREHOUSE);
				player.sendActionFailed();
				return;
			}

			if(/*ConfigValue.AltAllowOthersWithdrawFromClanWarehouse&&*/(player.getClanPrivileges() & L2Clan.CP_CL_WAREHOUSE_SEARCH) == L2Clan.CP_CL_WAREHOUSE_SEARCH)
			{
				player.tempInventoryDisable();
				player.sendPacket(new WareHouseWithdrawList(player, WarehouseType.CLAN, ItemClass.values()[0]));
			}
			else
			{
				player.sendPacket(Msg.YOU_DO_NOT_HAVE_THE_RIGHT_TO_USE_THE_CLAN_WAREHOUSE);
				player.sendActionFailed();
			}
		}
		else if(command.equals("_bbs_deposit_c"))
		{
			if(!player.getPlayerAccess().UseWarehouse)
				return;

			if(player.getClan() == null)
			{
				player.sendActionFailed();
				return;
			}

			if(player.getClan().getLevel() == 0)
			{
				player.sendPacket(Msg.ONLY_CLANS_OF_CLAN_LEVEL_1_OR_HIGHER_CAN_USE_A_CLAN_WAREHOUSE);
				player.sendActionFailed();
				return;
			}

			player.tempInventoryDisable();

			if(!(player.isClanLeader() // забирать может лидер
					|| ConfigValue.AltAllowOthersWithdrawFromClanWarehouse && (player.getClanPrivileges() & L2Clan.CP_CL_WAREHOUSE_SEARCH) == L2Clan.CP_CL_WAREHOUSE_SEARCH || player.getVarB("canWhWithdraw"))) // выданы персональные права
				player.sendPacket(Msg.ITEMS_LEFT_AT_THE_CLAN_HALL_WAREHOUSE_CAN_ONLY_BE_RETRIEVED_BY_THE_CLAN_LEADER_DO_YOU_WANT_TO_CONTINUE);

			player.sendPacket(new WareHouseDepositList(player, WarehouseType.CLAN));
		}
		else if(command.equals("_bbs_withdraw_p"))
		{
			if(!player.getPlayerAccess().UseWarehouse)
				return;
			player.sendPacket(new WareHouseWithdrawList(player, WarehouseType.PRIVATE, ItemClass.values()[0]));
		}
		else if(command.equals("_bbs_deposit_p"))
		{
			if(!player.getPlayerAccess().UseWarehouse)
				return;

			player.tempInventoryDisable();
			player.sendPacket(new WareHouseDepositList(player, WarehouseType.PRIVATE), Msg.ActionFail);
		}
		else if(command.startsWith("_bbs_visual_w_list"))
		{
			String[] param = command.split(":");
			int page = Integer.parseInt(param[1]);
			String content = readHtml(ConfigValue.CommunityBoardHtmlRoot+"visual_item_w_list.htm", player);
			
			final L2ItemInstance wpn = player.getActiveWeaponInstance();
			if(wpn != null && !wpn.isShadowItem() && !wpn.isTemporalItem())
			{
				content = content.replace("<?ches_icon?>", wpn.getItem().getIcon());
				content = content.replace("<?ches_name?>", wpn.getItem().getName());
				content = content.replace("<?ches_enchant_level?>", (wpn.getEnchantLevel() <= 0 ? "</font>" : " +" + wpn.getEnchantLevel()));
			}
			else
			{
				player.sendMessage("Не подходящий предмет.");
				return;
			}

			StringBuilder sb = new StringBuilder();
			StringBuilder sb2 = new StringBuilder();
			if(wpn._visual_item_id <= 0 && !Util.contains(ConfigValue.VisualWNoSetList, wpn.getItemId()))
			{
				List<L2ItemInstance> list = new ArrayList<L2ItemInstance>();
				for(L2ItemInstance item : player.getInventory().getItemsList())
					if(Util.contains(ConfigValue.VisualWSetList, item.getItemId()) && wpn._visual_item_id != item.getItemId() && !item.isShadowItem() && !item.isTemporalItem() && item.getItemType() == wpn.getItemType())
						list.add(item);
				int MaxCharactersPerPage = 7;
				int MaxPages = list.size() / MaxCharactersPerPage;

				if(list.size() > MaxCharactersPerPage * MaxPages)
					MaxPages++;

				if(page > MaxPages)
					page = MaxPages;

				int CharactersStart = MaxCharactersPerPage * page;
				int CharactersEnd = list.size();
				if(CharactersEnd - CharactersStart > MaxCharactersPerPage)
					CharactersEnd = CharactersStart + MaxCharactersPerPage;

				int i = CharactersStart;

				sb.append("<table width=320 height=40>");
				for(;i<CharactersEnd;i++)
				{
					L2ItemInstance item = list.get(i);
					String icon = item.getItem().getIcon();
					int visual_id = item.getItemId();
					if(icon == null || icon.isEmpty())
						icon = "icon.etc_question_mark_i00";

					sb.append("<tr>");
					sb.append("	<td WIDTH=345 align=center valign=top>");
					sb.append("		<table border=0 cellspacing=4 cellpadding=3 bgcolor="+(i%2==0 ? "333333" : "1a1a1a>"));
					sb.append("			<tr>");
					sb.append("				<td FIXWIDTH=50 align=right valign=top>");
					sb.append("					<img src=\""+icon+"\" width=32 height=32>");
					sb.append("				</td>");
					sb.append("				<td FIXWIDTH=200 align=left valign=top>");
					sb.append("					<br>");
					sb.append("					<font color=\"0099FF\">"+item.getName()+"</font>&nbsp;");
					sb.append("					<br>");
					sb.append("				</td>");
					sb.append("				<td FIXWIDTH=95 align=center valign=top>");
					sb.append("					<table>");
					sb.append("						<tr>");
					sb.append("							<td>");
					sb.append("								<button value=\""+(player.isLangRus() ? "Вставить" : "Insert")+"\" action=\"bypass -h _bbs_visual_w:"+ConfigValue.VisualWPriceId+":"+ConfigValue.VisualWPriceCount+":"+visual_id+"\" back=\"l2ui_ct1.button.button_df_small_down\" fore=\"l2ui_ct1.button.button_df_small\" width=\"74\" height=\"25\"/>");
					sb.append("							</td>");
					sb.append("						</tr>");
					sb.append("					</table>");
					sb.append("				</td>");
					sb.append("			</tr>");
					sb.append("		</table>");
					sb.append("		<br>");
					sb.append("	</td>");
					sb.append("</tr>");
				}
				// Раскоментировать если нужны будут странички:)
				/*sb.append("<tr>");
				sb.append("	<td WIDTH=345 align=center valign=top>");
				sb.append("		<table>");
				sb.append("			<tr>");
				sb.append("				<td WIDTH=50 height=20 align=right valign=top>");

				for(int x = MaxPages-1;x >=0;x--)
				{
					int pagenr = x + 1;
					sb.append("<a action=\"bypass -h _bbs_visual_w_list:"+x+"\">"+(x == page ? "["+pagenr+"]" : pagenr)+" </a>");
					
				}
				
				

				sb.append("				</td>");
				sb.append("			</tr>");
				sb.append("		</table>");
				sb.append("	</td>");
				sb.append("</tr>");*/
				sb.append("</table>");
			}
			else
			{
				sb.append("<br>");
				sb.append("<table width=320>");
				sb.append("	<tr>");
				sb.append("		<td width=223 align=\"center\" >");
				sb.append("<table width=300 bgcolor=891b0c>");
				sb.append("	<tr>");
				sb.append("		<td height=35 width=223 align=\"center\" >");
				sb.append("			<br>");
				sb.append("			<font color=\"ffffff\">В данном оружие уже изменен внещний вид.</font><br1>");
				sb.append("			<br1>");
				sb.append("		</td>");
				sb.append("		<td align=left valign=top>");
				sb.append("			<table>");
				sb.append("				<tr>");
				sb.append("					<td>");
				sb.append("						<button value=\""+(player.isLangRus() ? "Извлечь" : "Remove")+"\" action=\"bypass -h _bbs_visual_w_dell\" back=\"l2ui_ct1.button.button_df_small_down\" fore=\"l2ui_ct1.button.button_df_small\" width=\"74\" height=\"25\"/>");
				sb.append("					</td>");
				sb.append("				</tr>");
				sb.append("			</table>");
				sb.append("		</td>");
				sb.append("	</tr>");
				sb.append("</table>");
				sb.append("		</td>");
				sb.append("	</tr>");
				sb.append("</table>");
			}

			content = content.replace("<?item_list?>", sb.toString());
			content = content.replace("<?allready?>", sb2.toString());
			separateAndSend(content, player);
		}
		else if(command.startsWith("_bbs_visual_w_ok")) // bypass -h _bbs_visual_w_ok:id_item:count_item:visual_id
		{
			final L2ItemInstance wpn = player.getActiveWeaponInstance();
			if(wpn != null && !wpn.isShadowItem() && !wpn.isTemporalItem())
			{
				String[] param = command.split(":");
				int itemid = Integer.parseInt(param[3]);
				L2ItemInstance item = player.getInventory().getItemByItemId(itemid);

				if(item != null && wpn.getVisualItemId() != item.getItemId() && !item.isShadowItem() && !item.isTemporalItem() && item.getItemType() == wpn.getItemType() && DifferentMethods.getPay(player, Integer.parseInt(param[1]), Long.parseLong(param[2]), true))
				{
					player.getInventory().destroyItem(item, 1, true);
					wpn.setVisualItemId(itemid);
					if(ConfigValue.VisualSetZeroEnchant)
						wpn._visual_enchant_level = 0;
					player.getInventory().refreshListeners(wpn, -1);
					player.sendPacket(new InventoryUpdate().addModifiedItem(wpn));
					player.broadcastUserInfo(true);
					player.broadcastUserInfo(true);
				}
			}
			DifferentMethods.communityNextPage(player, "_bbs_visual_w_list:0");
		}
		else if(command.equals("_bbs_visual_w_dell"))
		{
			final L2ItemInstance wpn = player.getActiveWeaponInstance();
			if(wpn == null)
			{
				player.sendMessage("Тут текст 1.");
				return;
			}
			else if(wpn._visual_item_id <= 0)
			{
				player.sendMessage("В данный сет запрещена вставка.");
				return;
			}
			int itemid = wpn._visual_item_id;
			player.getInventory().addItem(itemid, 1);
			wpn.setVisualItemId(0);
			if(ConfigValue.VisualSetZeroEnchant)
				wpn._visual_enchant_level = -1;
			player.getInventory().refreshListeners(wpn, -1);
			player.broadcastUserInfo(true);
			player.broadcastUserInfo(true);
			DifferentMethods.communityNextPage(player, "_bbs_visual_w_list:0");
		}
		/** **/
		else if(command.startsWith("_bbs_visual_s_list"))
		{
			String[] param = command.split(":");
			int page = Integer.parseInt(param[1]);
			String content = readHtml(ConfigValue.CommunityBoardHtmlRoot+"visual_item_s_list.htm", player);
			
			final L2ItemInstance wpn = player.getSecondaryWeaponInstance();
			if(wpn != null && !wpn.isShadowItem() && !wpn.isTemporalItem())
			{
				content = content.replace("<?ches_icon?>", wpn.getItem().getIcon());
				content = content.replace("<?ches_name?>", wpn.getItem().getName());
				content = content.replace("<?ches_enchant_level?>", (wpn.getEnchantLevel() <= 0 ? "</font>" : " +" + wpn.getEnchantLevel()));
			}
			else
			{
				player.sendMessage("Не подходящий предмет.");
				return;
			}

			StringBuilder sb = new StringBuilder();
			StringBuilder sb2 = new StringBuilder();
			if(wpn._visual_item_id <= 0 && !Util.contains(ConfigValue.VisualSNoSetList, wpn.getItemId()))
			{
				List<L2ItemInstance> list = new ArrayList<L2ItemInstance>();
				for(L2ItemInstance item : player.getInventory().getItemsList())
					if(Util.contains(ConfigValue.VisualSSetList, item.getItemId()) && wpn._visual_item_id != item.getItemId() && !item.isShadowItem() && !item.isTemporalItem() && item.getItemType() == wpn.getItemType())
						list.add(item);
				int MaxCharactersPerPage = 7;
				int MaxPages = list.size() / MaxCharactersPerPage;

				if(list.size() > MaxCharactersPerPage * MaxPages)
					MaxPages++;

				if(page > MaxPages)
					page = MaxPages;

				int CharactersStart = MaxCharactersPerPage * page;
				int CharactersEnd = list.size();
				if(CharactersEnd - CharactersStart > MaxCharactersPerPage)
					CharactersEnd = CharactersStart + MaxCharactersPerPage;

				int i = CharactersStart;

				sb.append("<table width=320 height=40>");
				for(;i<CharactersEnd;i++)
				{
					L2ItemInstance item = list.get(i);
					String icon = item.getItem().getIcon();
					int visual_id = item.getItemId();
					if(icon == null || icon.isEmpty())
						icon = "icon.etc_question_mark_i00";

					sb.append("<tr>");
					sb.append("	<td WIDTH=345 align=center valign=top>");
					sb.append("		<table border=0 cellspacing=4 cellpadding=3 bgcolor="+(i%2==0 ? "333333" : "1a1a1a>"));
					sb.append("			<tr>");
					sb.append("				<td FIXWIDTH=50 align=right valign=top>");
					sb.append("					<img src=\""+icon+"\" width=32 height=32>");
					sb.append("				</td>");
					sb.append("				<td FIXWIDTH=200 align=left valign=top>");
					sb.append("					<br>");
					sb.append("					<font color=\"0099FF\">"+item.getName()+"</font>&nbsp;");
					sb.append("					<br>");
					sb.append("				</td>");
					sb.append("				<td FIXWIDTH=95 align=center valign=top>");
					sb.append("					<table>");
					sb.append("						<tr>");
					sb.append("							<td>");
					sb.append("								<button value=\""+(player.isLangRus() ? "Вставить" : "Insert")+"\" action=\"bypass -h _bbs_visual_s:"+ConfigValue.VisualSPriceId+":"+ConfigValue.VisualSPriceCount+":"+visual_id+"\" back=\"l2ui_ct1.button.button_df_small_down\" fore=\"l2ui_ct1.button.button_df_small\" width=\"74\" height=\"25\"/>");
					sb.append("							</td>");
					sb.append("						</tr>");
					sb.append("					</table>");
					sb.append("				</td>");
					sb.append("			</tr>");
					sb.append("		</table>");
					sb.append("		<br>");
					sb.append("	</td>");
					sb.append("</tr>");
				}
				// Раскоментировать если нужны будут странички:)
				/*sb.append("<tr>");
				sb.append("	<td WIDTH=345 align=center valign=top>");
				sb.append("		<table>");
				sb.append("			<tr>");
				sb.append("				<td WIDTH=50 height=20 align=right valign=top>");

				for(int x = MaxPages-1;x >=0;x--)
				{
					int pagenr = x + 1;
					sb.append("<a action=\"bypass -h _bbs_visual_s_list:"+x+"\">"+(x == page ? "["+pagenr+"]" : pagenr)+" </a>");
					
				}
				
				

				sb.append("				</td>");
				sb.append("			</tr>");
				sb.append("		</table>");
				sb.append("	</td>");
				sb.append("</tr>");*/
				sb.append("</table>");
			}
			else
			{
				sb.append("<br>");
				sb.append("<table width=320>");
				sb.append("	<tr>");
				sb.append("		<td width=223 align=\"center\" >");
				sb.append("<table width=300 bgcolor=891b0c>");
				sb.append("	<tr>");
				sb.append("		<td height=35 width=223 align=\"center\" >");
				sb.append("			<br>");
				sb.append("			<font color=\"ffffff\">В данном оружие уже изменен внещний вид.</font><br1>");
				sb.append("			<br1>");
				sb.append("		</td>");
				sb.append("		<td align=left valign=top>");
				sb.append("			<table>");
				sb.append("				<tr>");
				sb.append("					<td>");
				sb.append("						<button value=\""+(player.isLangRus() ? "Извлечь" : "Remove")+"\" action=\"bypass -h _bbs_visual_s_dell\" back=\"l2ui_ct1.button.button_df_small_down\" fore=\"l2ui_ct1.button.button_df_small\" width=\"74\" height=\"25\"/>");
				sb.append("					</td>");
				sb.append("				</tr>");
				sb.append("			</table>");
				sb.append("		</td>");
				sb.append("	</tr>");
				sb.append("</table>");
				sb.append("		</td>");
				sb.append("	</tr>");
				sb.append("</table>");
			}

			content = content.replace("<?item_list?>", sb.toString());
			content = content.replace("<?allready?>", sb2.toString());
			separateAndSend(content, player);
		}
		else if(command.startsWith("_bbs_visual_s_ok")) // bypass -h _bbs_visual_s_ok:id_item:count_item:visual_id
		{
			final L2ItemInstance wpn = player.getSecondaryWeaponInstance();
			if(wpn != null && !wpn.isShadowItem() && !wpn.isTemporalItem())
			{
				String[] param = command.split(":");
				int itemid = Integer.parseInt(param[3]);
				L2ItemInstance item = player.getInventory().getItemByItemId(itemid);

				if(item != null && wpn.getVisualItemId() != item.getItemId() && !item.isShadowItem() && !item.isTemporalItem() && item.getItemType() == wpn.getItemType() && DifferentMethods.getPay(player, Integer.parseInt(param[1]), Long.parseLong(param[2]), true))
				{
					player.getInventory().destroyItem(item, 1, true);
					wpn.setVisualItemId(itemid);
					if(ConfigValue.VisualSetZeroEnchant)
						wpn._visual_enchant_level = 0;
					player.getInventory().refreshListeners(wpn, -1);
					player.sendPacket(new InventoryUpdate().addModifiedItem(wpn));
					player.broadcastUserInfo(true);
					player.broadcastUserInfo(true);
				}
			}
			DifferentMethods.communityNextPage(player, "_bbs_visual_s_list:0");
		}
		else if(command.equals("_bbs_visual_s_dell"))
		{
			final L2ItemInstance wpn = player.getSecondaryWeaponInstance();
			if(wpn == null)
			{
				player.sendMessage("Тут текст 1.");
				return;
			}
			else if(wpn._visual_item_id <= 0)
			{
				player.sendMessage("В данный сет запрещена вставка.");
				return;
			}
			int itemid = wpn._visual_item_id;
			player.getInventory().addItem(itemid, 1);
			wpn.setVisualItemId(0);
			if(ConfigValue.VisualSetZeroEnchant)
				wpn._visual_enchant_level = -1;
			player.getInventory().refreshListeners(wpn, -1);
			player.broadcastUserInfo(true);
			player.broadcastUserInfo(true);
			DifferentMethods.communityNextPage(player, "_bbs_visual_s_list:0");
		}

		/** **/
		else if(command.startsWith("_bbs_visual_test_ok")) // bypass -h _bbs_visual_test_ok:id_item:count_item:visual_id:body_type
		{
			String[] param = command.split(":");
			int id_item = Integer.parseInt(param[1]);
			int count_item = Integer.parseInt(param[2]);
			int visual_id = Integer.parseInt(param[3]);
			int body_type = Integer.parseInt(param[4]);

			player._paperdoll_test = new int[26];

			switch(body_type)
			{
				case L2Item.SLOT_LR_HAND:
				case L2Item.SLOT_R_HAND:
					player._paperdoll_test[Inventory.PAPERDOLL_RHAND] = visual_id;
					break;
				case L2Item.SLOT_L_HAND:
					player._paperdoll_test[Inventory.PAPERDOLL_LHAND] = visual_id;
					break;
				case L2Item.SLOT_FULL_ARMOR:
				case L2Item.SLOT_CHEST:
				case L2Item.SLOT_FORMAL_WEAR:
				case L2Item.SLOT_SIGIL:
					player._paperdoll_test[Inventory.PAPERDOLL_CHEST] = visual_id;
					break;
				case L2Item.SLOT_LEGS:
					player._paperdoll_test[Inventory.PAPERDOLL_LEGS] = visual_id;
					break;
				case L2Item.SLOT_FEET:
					player._paperdoll_test[Inventory.PAPERDOLL_FEET] = visual_id;
					break;
				case L2Item.SLOT_GLOVES:
					player._paperdoll_test[Inventory.PAPERDOLL_GLOVES] = visual_id;
					break;
				case L2Item.SLOT_HEAD:
					player._paperdoll_test[Inventory.PAPERDOLL_HEAD] = visual_id;
					break;
				case L2Item.SLOT_HAIR:
					player._paperdoll_test[Inventory.PAPERDOLL_HAIR] = visual_id;
					break;
				case L2Item.SLOT_DHAIR:
				case L2Item.SLOT_HAIRALL:
					player._paperdoll_test[Inventory.PAPERDOLL_DHAIR] = visual_id;
					break;
				case L2Item.SLOT_UNDERWEAR:
					player._paperdoll_test[Inventory.PAPERDOLL_UNDER] = visual_id;
					break;
				case L2Item.SLOT_BACK:
					player._paperdoll_test[Inventory.PAPERDOLL_BACK] = visual_id;
					break;
				case L2Item.SLOT_BELT:
					player._paperdoll_test[Inventory.PAPERDOLL_BELT] = visual_id;
					break;
			}
			if(player._test_task != null)
			{
				player._test_task.cancel(false);
				player._test_task = null;
			}
			player._test_task = ThreadPoolManager.getInstance().schedule(new l2open.common.RunnableImpl()
			{
				public void runImpl()
				{
					player._paperdoll_test = null;
					player.sendPacket(new UserInfo(player));
					player.sendPacket(new UserInfo(player));
					//player.sendUserInfo(true);
					//player.sendUserInfo(true);
				}
			}, ConfigValue.VisualWearTime*1000);
			player.sendPacket(new UserInfo(player));
			player.sendPacket(new UserInfo(player));
		}
		else if(command.startsWith("_bbs_visual_test_list"))
		{
			String[] param = command.split(":");
			int page = Integer.parseInt(param[1]);
			String content = readHtml(ConfigValue.CommunityBoardHtmlRoot+"visual_test_item_list.htm", player);

			StringBuilder sb = new StringBuilder();
			StringBuilder sb2 = new StringBuilder();

			List<L2Item> list = new ArrayList<L2Item>();

			for(int item_id : ConfigValue.VisualWearList)
				list.add(ItemTemplates.getInstance().getTemplate(item_id));

			int MaxCharactersPerPage = 7;
			int MaxPages = list.size() / MaxCharactersPerPage;

			if(list.size() > MaxCharactersPerPage * MaxPages)
				MaxPages++;

			if(page > MaxPages)
				page = MaxPages;

			int CharactersStart = MaxCharactersPerPage * page;
			int CharactersEnd = list.size();
			if(CharactersEnd - CharactersStart > MaxCharactersPerPage)
				CharactersEnd = CharactersStart + MaxCharactersPerPage;

			int i = CharactersStart;

			sb.append("<table width=320 height=40>");
			for(;i<CharactersEnd;i++)
			{
				L2Item item = list.get(i);
				String icon = item.getIcon();
				if(icon == null || icon.isEmpty())
					icon = "icon.etc_question_mark_i00";

				sb.append("<tr>");
				sb.append("	<td WIDTH=345 align=center valign=top>");
				sb.append("		<table border=0 cellspacing=4 cellpadding=3 bgcolor="+(i%2==0 ? "333333" : "1a1a1a>"));
				sb.append("			<tr>");
				sb.append("				<td FIXWIDTH=50 align=right valign=top>");
				sb.append("					<img src=\""+icon+"\" width=32 height=32>");
				sb.append("				</td>");
				sb.append("				<td FIXWIDTH=200 align=left valign=top>");
				sb.append("					<br>");
				sb.append("					<font color=\"0099FF\">"+item.getName()+"</font>&nbsp;");
				sb.append("					<br>");
				sb.append("				</td>");
				sb.append("				<td FIXWIDTH=95 align=center valign=top>");
				sb.append("					<table>");
				sb.append("						<tr>");
				sb.append("							<td>");
				sb.append("								<button value=\""+(player.isLangRus() ? "Примерить" : "Sample")+"\" action=\"bypass -h _bbs_visual_test_ok:"+ConfigValue.VisualPriceId+":"+ConfigValue.VisualPriceCount+":"+item.getItemId()+":"+item.getBodyPart()+"\" back=\"l2ui_ct1.button.button_df_small_down\" fore=\"l2ui_ct1.button.button_df_small\" width=\"74\" height=\"25\"/>");
				sb.append("							</td>");
				sb.append("						</tr>");
				sb.append("					</table>");
				sb.append("				</td>");
				sb.append("			</tr>");
				sb.append("		</table>");
				sb.append("		<br>");
				sb.append("	</td>");
				sb.append("</tr>");
			}
			
			if(MaxPages > 1)
			{
				// Раскоментировать если нужны будут странички:)
				sb.append("<tr>");
				sb.append("	<td WIDTH=345 align=center valign=top>");
				sb.append("		<table border=0>");
				sb.append("			<tr>");
				sb.append("				<td WIDTH=180 height=35 align=left valign=top>");

				//for(int x = MaxPages-1;x>=0;x--)
				for(int x = 0;x < MaxPages;x++)
				{
					int pagenr = x + 1;
					sb.append("<a action=\"bypass -h _bbs_visual_test_list:"+x+"\"> "+(x == page ? "["+pagenr+"]" : pagenr)+"&nbsp;</a> ");
				}

				sb.append("				</td>");
				sb.append("			</tr>");
				sb.append("		</table>");
				sb.append("	</td>");
				sb.append("</tr>");
			}
			sb.append("</table>");

			content = content.replace("<?item_list?>", sb.toString());
			content = content.replace("<?allready?>", sb2.toString());
			separateAndSend(content, player);
		}
		else if(command.startsWith("_bbs_visual_w_test_ok")) // bypass -h _bbs_visual_w_test_ok:id_item:count_item:visual_id:body_type
		{
			String[] param = command.split(":");
			int id_item = Integer.parseInt(param[1]);
			int count_item = Integer.parseInt(param[2]);
			int visual_id = Integer.parseInt(param[3]);
			int body_type = Integer.parseInt(param[4]);

			player._paperdoll_test = new int[26];

			switch(body_type)
			{
				case L2Item.SLOT_LR_HAND:
				case L2Item.SLOT_R_HAND:
					player._paperdoll_test[Inventory.PAPERDOLL_RHAND] = visual_id;
					break;
				case L2Item.SLOT_L_HAND:
					player._paperdoll_test[Inventory.PAPERDOLL_LHAND] = visual_id;
					break;
				default:
					player._paperdoll_test[Inventory.PAPERDOLL_RHAND] = visual_id;
					break;
			}
			if(player._test_task != null)
			{
				player._test_task.cancel(false);
				player._test_task = null;
			}
			player._test_task = ThreadPoolManager.getInstance().schedule(new l2open.common.RunnableImpl()
			{
				public void runImpl()
				{
					player._paperdoll_test = null;
					player.sendPacket(new UserInfo(player));
					player.sendPacket(new UserInfo(player));
					//player.sendUserInfo(true);
					//player.sendUserInfo(true);
				}
			}, ConfigValue.VisualWWearTime*1000);
			player.sendPacket(new UserInfo(player));
			player.sendPacket(new UserInfo(player));
		}
		else if(command.startsWith("_bbs_visual_s_test_ok")) // bypass -h _bbs_visual_s_test_ok:id_item:count_item:visual_id:body_type
		{
			String[] param = command.split(":");
			int id_item = Integer.parseInt(param[1]);
			int count_item = Integer.parseInt(param[2]);
			int visual_id = Integer.parseInt(param[3]);
			int body_type = Integer.parseInt(param[4]);

			player._paperdoll_test = new int[26];
			player._paperdoll_test[Inventory.PAPERDOLL_LHAND] = visual_id;

			if(player._test_task != null)
			{
				player._test_task.cancel(false);
				player._test_task = null;
			}
			player._test_task = ThreadPoolManager.getInstance().schedule(new l2open.common.RunnableImpl()
			{
				public void runImpl()
				{
					player._paperdoll_test = null;
					player.sendPacket(new UserInfo(player));
					player.sendPacket(new UserInfo(player));
					//player.sendUserInfo(true);
					//player.sendUserInfo(true);
				}
			}, ConfigValue.VisualSWearTime*1000);
			player.sendPacket(new UserInfo(player));
			player.sendPacket(new UserInfo(player));
		}
		else if(command.startsWith("_bbs_visual_s_test_list"))
		{
			String[] param = command.split(":");
			int page = Integer.parseInt(param[1]);
			String content = readHtml(ConfigValue.CommunityBoardHtmlRoot+"visual_s_test_item_list.htm", player);

			StringBuilder sb = new StringBuilder();
			StringBuilder sb2 = new StringBuilder();

			List<L2Item> list = new ArrayList<L2Item>();

			for(int item_id : ConfigValue.VisualSWearList)
				list.add(ItemTemplates.getInstance().getTemplate(item_id));

			int MaxCharactersPerPage = 7;
			int MaxPages = list.size() / MaxCharactersPerPage;

			if(list.size() > MaxCharactersPerPage * MaxPages)
				MaxPages++;

			if(page > MaxPages)
				page = MaxPages;

			int CharactersStart = MaxCharactersPerPage * page;
			int CharactersEnd = list.size();
			if(CharactersEnd - CharactersStart > MaxCharactersPerPage)
				CharactersEnd = CharactersStart + MaxCharactersPerPage;

			int i = CharactersStart;

			sb.append("<table width=320 height=40>");
			for(;i<CharactersEnd;i++)
			{
				L2Item item = list.get(i);
				String icon = item.getIcon();
				if(icon == null || icon.isEmpty())
					icon = "icon.etc_question_mark_i00";

				sb.append("<tr>");
				sb.append("	<td WIDTH=345 align=center valign=top>");
				sb.append("		<table border=0 cellspacing=4 cellpadding=3 bgcolor="+(i%2==0 ? "333333" : "1a1a1a>"));
				sb.append("			<tr>");
				sb.append("				<td FIXWIDTH=50 align=right valign=top>");
				sb.append("					<img src=\""+icon+"\" width=32 height=32>");
				sb.append("				</td>");
				sb.append("				<td FIXWIDTH=200 align=left valign=top>");
				sb.append("					<br>");
				sb.append("					<font color=\"0099FF\">"+item.getName()+"</font>&nbsp;");
				sb.append("					<br>");
				sb.append("				</td>");
				sb.append("				<td FIXWIDTH=95 align=center valign=top>");
				sb.append("					<table>");
				sb.append("						<tr>");
				sb.append("							<td>");
				sb.append("								<button value=\""+(player.isLangRus() ? "Примерить" : "Sample")+"\" action=\"bypass -h _bbs_visual_s_test_ok:"+ConfigValue.VisualSPriceId+":"+ConfigValue.VisualSPriceCount+":"+item.getItemId()+":"+item.getBodyPart()+"\" back=\"l2ui_ct1.button.button_df_small_down\" fore=\"l2ui_ct1.button.button_df_small\" width=\"74\" height=\"25\"/>");
				sb.append("							</td>");
				sb.append("						</tr>");
				sb.append("					</table>");
				sb.append("				</td>");
				sb.append("			</tr>");
				sb.append("		</table>");
				sb.append("		<br>");
				sb.append("	</td>");
				sb.append("</tr>");
			}
			
			if(MaxPages > 1)
			{
				// Раскоментировать если нужны будут странички:)
				sb.append("<tr>");
				sb.append("	<td WIDTH=345 align=center valign=top>");
				sb.append("		<table border=0>");
				sb.append("			<tr>");
				sb.append("				<td WIDTH=180 height=35 align=left valign=top>");

				//for(int x = MaxPages-1;x>=0;x--)
				for(int x = 0;x < MaxPages;x++)
				{
					int pagenr = x + 1;
					sb.append("<a action=\"bypass -h _bbs_visual_s_test_list:"+x+"\"> "+(x == page ? "["+pagenr+"]" : pagenr)+"&nbsp;</a> ");
				}

				sb.append("				</td>");
				sb.append("			</tr>");
				sb.append("		</table>");
				sb.append("	</td>");
				sb.append("</tr>");
			}
			sb.append("</table>");

			content = content.replace("<?item_list?>", sb.toString());
			content = content.replace("<?allready?>", sb2.toString());
			separateAndSend(content, player);
		}
		else if(command.startsWith("_bbs_visual_w_test_list"))
		{
			String[] param = command.split(":");
			int page = Integer.parseInt(param[1]);
			String content = readHtml(ConfigValue.CommunityBoardHtmlRoot+"visual_w_test_item_list.htm", player);

			StringBuilder sb = new StringBuilder();
			StringBuilder sb2 = new StringBuilder();

			List<L2Item> list = new ArrayList<L2Item>();

			for(int item_id : ConfigValue.VisualWWearList)
				list.add(ItemTemplates.getInstance().getTemplate(item_id));

			int MaxCharactersPerPage = 7;
			int MaxPages = list.size() / MaxCharactersPerPage;

			if(list.size() > MaxCharactersPerPage * MaxPages)
				MaxPages++;

			if(page > MaxPages)
				page = MaxPages;

			int CharactersStart = MaxCharactersPerPage * page;
			int CharactersEnd = list.size();
			if(CharactersEnd - CharactersStart > MaxCharactersPerPage)
				CharactersEnd = CharactersStart + MaxCharactersPerPage;

			int i = CharactersStart;

			sb.append("<table width=320 height=40>");
			for(;i<CharactersEnd;i++)
			{
				L2Item item = list.get(i);
				String icon = item.getIcon();
				if(icon == null || icon.isEmpty())
					icon = "icon.etc_question_mark_i00";

				sb.append("<tr>");
				sb.append("	<td WIDTH=345 align=center valign=top>");
				sb.append("		<table border=0 cellspacing=4 cellpadding=3 bgcolor="+(i%2==0 ? "333333" : "1a1a1a>"));
				sb.append("			<tr>");
				sb.append("				<td FIXWIDTH=50 align=right valign=top>");
				sb.append("					<img src=\""+icon+"\" width=32 height=32>");
				sb.append("				</td>");
				sb.append("				<td FIXWIDTH=200 align=left valign=top>");
				sb.append("					<br>");
				sb.append("					<font color=\"0099FF\">"+item.getName()+"</font>&nbsp;");
				sb.append("					<br>");
				sb.append("				</td>");
				sb.append("				<td FIXWIDTH=95 align=center valign=top>");
				sb.append("					<table>");
				sb.append("						<tr>");
				sb.append("							<td>");
				sb.append("								<button value=\""+(player.isLangRus() ? "Примерить" : "Sample")+"\" action=\"bypass -h _bbs_visual_w_test_ok:"+ConfigValue.VisualWPriceId+":"+ConfigValue.VisualWPriceCount+":"+item.getItemId()+":"+item.getBodyPart()+"\" back=\"l2ui_ct1.button.button_df_small_down\" fore=\"l2ui_ct1.button.button_df_small\" width=\"74\" height=\"25\"/>");
				sb.append("							</td>");
				sb.append("						</tr>");
				sb.append("					</table>");
				sb.append("				</td>");
				sb.append("			</tr>");
				sb.append("		</table>");
				sb.append("		<br>");
				sb.append("	</td>");
				sb.append("</tr>");
			}
			
			if(MaxPages > 1)
			{
				// Раскоментировать если нужны будут странички:)
				sb.append("<tr>");
				sb.append("	<td WIDTH=345 align=center valign=top>");
				sb.append("		<table border=0>");
				sb.append("			<tr>");
				sb.append("				<td WIDTH=180 height=35 align=left valign=top>");

				//for(int x = MaxPages-1;x>=0;x--)
				for(int x = 0;x < MaxPages;x++)
				{
					int pagenr = x + 1;
					sb.append("<a action=\"bypass -h _bbs_visual_w_test_list:"+x+"\"> "+(x == page ? "["+pagenr+"]" : pagenr)+"&nbsp;</a> ");
				}

				sb.append("				</td>");
				sb.append("			</tr>");
				sb.append("		</table>");
				sb.append("	</td>");
				sb.append("</tr>");
			}
			sb.append("</table>");

			content = content.replace("<?item_list?>", sb.toString());
			content = content.replace("<?allready?>", sb2.toString());
			separateAndSend(content, player);
		}
		else if(command.startsWith("_bbs_visual_list"))
		{
			if(ConfigValue.VisualSetList.length == 0)
				return;
			String[] param = command.split(":");
			int page = Integer.parseInt(param[1]);
			String content = readHtml(ConfigValue.CommunityBoardHtmlRoot+"visual_item_list.htm", player);
			
			final L2ItemInstance chest = player.getInventory().getPaperdollItem(Inventory.PAPERDOLL_CHEST);
			if(chest != null && !chest.isShadowItem() && !chest.isTemporalItem())
			{
				content = content.replace("<?ches_icon?>", chest.getItem().getIcon());
				content = content.replace("<?ches_name?>", chest.getItem().getName());
				content = content.replace("<?ches_enchant_level?>", (chest.getEnchantLevel() <= 0 ? "</font>" : " +" + chest.getEnchantLevel()));
			}
			else
			{
				//content = readHtml(ConfigValue.CommunityBoardHtmlRoot+"no_arm.htm", player);
				player.sendMessage("Не подходящий предмет.");
				//separateAndSend(content, player);
				return;
			}

			StringBuilder sb = new StringBuilder();
			StringBuilder sb2 = new StringBuilder();
			if(chest._visual_item_id <= 0 && !Util.contains(ConfigValue.VisualNoSetList, chest.getItemId()))
			{
				List<L2ItemInstance> list = new ArrayList<L2ItemInstance>();
				for(L2ItemInstance item : player.getInventory().getItemsList())
					if(Util.contains(ConfigValue.VisualSetList, item.getItemId()) && chest._visual_item_id != item.getItemId() && !item.isShadowItem() && !item.isTemporalItem())
						list.add(item);
				int MaxCharactersPerPage = 7;
				int MaxPages = list.size() / MaxCharactersPerPage;

				if(list.size() > MaxCharactersPerPage * MaxPages)
					MaxPages++;

				if(page > MaxPages)
					page = MaxPages;

				int CharactersStart = MaxCharactersPerPage * page;
				int CharactersEnd = list.size();
				if(CharactersEnd - CharactersStart > MaxCharactersPerPage)
					CharactersEnd = CharactersStart + MaxCharactersPerPage;

				int i = CharactersStart;

				sb.append("<table width=320 height=40>");
				for(;i<CharactersEnd;i++)
				{
					L2ItemInstance item = list.get(i);
					String icon = item.getItem().getIcon();
					int visual_id = item.getItemId();
					if(icon == null || icon.isEmpty())
						icon = "icon.etc_question_mark_i00";

					sb.append("<tr>");
					sb.append("	<td WIDTH=345 align=center valign=top>");
					sb.append("		<table border=0 cellspacing=4 cellpadding=3 bgcolor="+(i%2==0 ? "333333" : "1a1a1a>"));
					sb.append("			<tr>");
					sb.append("				<td FIXWIDTH=50 align=right valign=top>");
					sb.append("					<img src=\""+icon+"\" width=32 height=32>");
					sb.append("				</td>");
					sb.append("				<td FIXWIDTH=200 align=left valign=top>");
					sb.append("					<br>");
					sb.append("					<font color=\"0099FF\">"+item.getName()+"</font>&nbsp;");
					sb.append("					<br>");
					sb.append("				</td>");
					sb.append("				<td FIXWIDTH=95 align=center valign=top>");
					sb.append("					<table>");
					sb.append("						<tr>");
					sb.append("							<td>");
					sb.append("								<button value=\""+(player.isLangRus() ? "Вставить" : "Insert")+"\" action=\"bypass -h _bbs_visual:"+ConfigValue.VisualPriceId+":"+ConfigValue.VisualPriceCount+":"+visual_id+"\" back=\"l2ui_ct1.button.button_df_small_down\" fore=\"l2ui_ct1.button.button_df_small\" width=\"74\" height=\"25\"/>");
					sb.append("							</td>");
					sb.append("						</tr>");
					sb.append("					</table>");
					sb.append("				</td>");
					sb.append("			</tr>");
					sb.append("		</table>");
					sb.append("		<br>");
					sb.append("	</td>");
					sb.append("</tr>");
				}
				// Раскоментировать если нужны будут странички:)
				/*sb.append("<tr>");
				sb.append("	<td WIDTH=345 align=center valign=top>");
				sb.append("		<table>");
				sb.append("			<tr>");
				sb.append("				<td WIDTH=50 height=20 align=right valign=top>");

				for(int x = MaxPages-1;x >=0;x--)
				{
					int pagenr = x + 1;
					sb.append("<a action=\"bypass -h _bbs_visual_list:"+x+"\">"+(x == page ? "["+pagenr+"]" : pagenr)+" </a>");
					
				}
				
				

				sb.append("				</td>");
				sb.append("			</tr>");
				sb.append("		</table>");
				sb.append("	</td>");
				sb.append("</tr>");*/
				sb.append("</table>");
			}
			else
			{
				sb.append("<br>");
				sb.append("<table width=320>");
				sb.append("	<tr>");
				sb.append("		<td width=223 align=\"center\" >");
				sb.append("<table width=300 bgcolor=891b0c>");
				sb.append("	<tr>");
				sb.append("		<td height=35 width=223 align=\"center\" >");
				sb.append("			<br>");
				sb.append("			<font color=\"ffffff\">В данную броню уже вставлен костюм.</font><br1>");
				sb.append("			<br1>");
				sb.append("		</td>");
				sb.append("		<td align=left valign=top>");
				sb.append("			<table>");
				sb.append("				<tr>");
				sb.append("					<td>");
				sb.append("						<button value=\""+(player.isLangRus() ? "Извлечь" : "Remove")+"\" action=\"bypass -h _bbs_visual_dell\" back=\"l2ui_ct1.button.button_df_small_down\" fore=\"l2ui_ct1.button.button_df_small\" width=\"74\" height=\"25\"/>");
				sb.append("					</td>");
				sb.append("				</tr>");
				sb.append("			</table>");
				sb.append("		</td>");
				sb.append("	</tr>");
				sb.append("</table>");
				sb.append("		</td>");
				sb.append("	</tr>");
				sb.append("</table>");
			}

			content = content.replace("<?item_list?>", sb.toString());
			content = content.replace("<?allready?>", sb2.toString());
			separateAndSend(content, player);
		}
		else if(command.startsWith("_bbs_visual_ok")) // bypass -h _bbs_visual_ok:id_item:count_item:visual_id
		{
			final L2ItemInstance chest = player.getInventory().getPaperdollItem(Inventory.PAPERDOLL_CHEST);
			if(chest != null && !chest.isShadowItem() && !chest.isTemporalItem() && chest.getVisualItemId() <= 0)
			{
				String[] param = command.split(":");
				int itemid = Integer.parseInt(param[3]);
				L2ItemInstance item = player.getInventory().getItemByItemId(itemid);

				if(item != null && chest.getVisualItemId() != item.getItemId() && !item.isShadowItem() && !item.isTemporalItem() && DifferentMethods.getPay(player, Integer.parseInt(param[1]), Long.parseLong(param[2]), true))
				{
					player.getInventory().destroyItem(item, 1, true);
					chest.setVisualItemId(itemid);
					player.getInventory().refreshListeners(chest, -1);
					player.sendPacket(new InventoryUpdate().addModifiedItem(chest));
					player.broadcastUserInfo(true);
					player.broadcastUserInfo(true);
				}
			}
			DifferentMethods.communityNextPage(player, "_bbs_visual_list:0");
		}
		else if(command.equals("_bbs_visual_dell"))
		{
			final L2ItemInstance chest = player.getInventory().getPaperdollItem(Inventory.PAPERDOLL_CHEST);
			if(chest == null)
			{
				player.sendMessage("Тут текст 1.");
				return;
			}
			else if(chest._visual_item_id <= 0)
			{
				player.sendMessage("В данный сет запрещена вставка.");
				return;
			}
			int itemid = chest._visual_item_id;
			player.getInventory().addItem(itemid, 1);
			chest.setVisualItemId(0);
			player.getInventory().refreshListeners(chest, -1);
			player.broadcastUserInfo(true);
			player.broadcastUserInfo(true);
			DifferentMethods.communityNextPage(player, "_bbs_visual_list:0");
		}
		else if(command.startsWith("_bbs_visual_enchant_index")) // bypass -h _bbs_visual_enchant_index
		{
			final L2ItemInstance wpn = player.getActiveWeaponInstance();
			if(wpn == null)
			{
				player.sendMessage("Оденьте оружие.");
				return;
			}
			else if(wpn.isShadowItem() || wpn.isTemporalItem())
			{
				player.sendMessage("Не подходящий предмет.");
				return;
			}
			String content = Files.read(ConfigValue.CommunityBoardHtmlRoot+"75.htm", player);
			content = content.replace("<?item_name?>", wpn.getName());
			content = content.replace("<?item_icon?>", wpn.getItem().getIcon());
			separateAndSend(content, player);
		}
		else if(command.startsWith("_bbs_visual_enchant_test")) // bypass -h _bbs_visual_enchant_test:visual_enchant_level
		{
			final L2ItemInstance wpn = player.getActiveWeaponInstance();
			if(wpn == null)
			{
				player.sendMessage("Оденьте оружие.");
				return;
			}
			else if(wpn.isShadowItem() || wpn.isTemporalItem())
			{
				player.sendMessage("Не подходящий предмет.");
				return;
			}
			String[] param = command.split(":");
			if(player._test_task != null)
			{
				player._test_task.cancel(false);
				player._test_task = null;
			}
			player._visual_enchant_level_test = Integer.parseInt(param[1]);
			player._test_task = ThreadPoolManager.getInstance().schedule(new l2open.common.RunnableImpl()
			{
				public void runImpl()
				{
					player._visual_enchant_level_test = -1;
					player.broadcastUserInfo(true);
				}
			}, 10000);
			player.broadcastUserInfo(true);
		}
		else if(command.startsWith("_bbs_visual_enchant_ok")) // bypass -h _bbs_visual_enchant_ok:id_item:count_item:visual_enchant_level
		{
			final L2ItemInstance wpn = player.getActiveWeaponInstance();
			if(wpn == null)
			{
				player.sendMessage("Оденьте оружие.");
				return;
			}
			else if(wpn.isShadowItem() || wpn.isTemporalItem() || wpn._visual_item_id > 0 || wpn._visual_enchant_level > 0)
			{
				player.sendMessage("Не подходящий предмет.");
				return;
			}
			String[] param = command.split(":");
			if(DifferentMethods.getPay(player, Integer.parseInt(param[1]), Long.parseLong(param[2]), true))
			{
				wpn._visual_enchant_level = Integer.parseInt(param[3]);
				wpn._storedInDb = false;
				PlayerData.getInstance().updateInDb(wpn);
				player.broadcastUserInfo(true);
			}
		}
		else if(command.equals("_bbs_visual_enchant_dell"))
		{
			final L2ItemInstance wpn = player.getActiveWeaponInstance();
			if(wpn == null)
			{
				player.sendMessage("Оденьте оружие.");
				return;
			}
			else if(wpn._visual_enchant_level == -1)
			{
				player.sendMessage("У вас нету изменения цвета свечения оружия.");
				return;
			}
			wpn._visual_enchant_level = -1;
			wpn._storedInDb = false;
			PlayerData.getInstance().updateInDb(wpn);
			player.broadcastUserInfo(true);
		}
		else if(command.startsWith("_bbs_visual_enchant")) // bypass -h _bbs_visual_enchant:id_item:count_item:visual_enchant_level
		{
			final L2ItemInstance wpn = player.getActiveWeaponInstance();
			if(wpn == null)
			{
				player.sendMessage("Оденьте оружие.");
				return;
			}
			else if(wpn.isShadowItem() || wpn.isTemporalItem() || wpn._visual_item_id > 0 || wpn._visual_enchant_level > 0)
			{
				player.sendMessage("Не подходящий предмет.");
				return;
			}
			player.scriptRequest("Вы желаете сменить цвет свечения оружия за 5 Gold Einhasad", "call_bbs", new String[]{"_bbs_visual_enchant_ok"+command.substring(19)});
		}
		else if(command.startsWith("_bbs_visual_s")) // bypass -h _bbs_visual_s:id_item:count_item:visual_id
		{
			final L2ItemInstance wpn = player.getSecondaryWeaponInstance();
			if(wpn == null || wpn.isShadowItem() || wpn.isTemporalItem())
			{
				player.sendMessage("Не подходящий предмет.");
				return;
			}
			String[] param = command.split(":");
			if(wpn != null && player.getInventory().getCountOf(Integer.parseInt(param[3])) > 0)
				player.scriptRequest("Вы желаете сменить вид Символа/Щита за "+param[2]+" "+DifferentMethods.getItemName(Integer.parseInt(param[1])), "call_bbs", new String[]{"_bbs_visual_s_ok"+command.substring(13)});
		}
		else if(command.startsWith("_bbs_visual_w")) // bypass -h _bbs_visual_w:id_item:count_item:visual_id
		{
			final L2ItemInstance wpn = player.getActiveWeaponInstance();
			if(wpn == null || wpn.isShadowItem() || wpn.isTemporalItem())
			{
				player.sendMessage("Не подходящий предмет.");
				return;
			}
			String[] param = command.split(":");
			if(wpn != null && player.getInventory().getCountOf(Integer.parseInt(param[3])) > 0)
				player.scriptRequest("Вы желаете сменить вид оружия за "+param[2]+" "+DifferentMethods.getItemName(Integer.parseInt(param[1])), "call_bbs", new String[]{"_bbs_visual_w_ok"+command.substring(13)});
		}
		else if(command.startsWith("_bbs_visual")) // bypass -h _bbs_visual:id_item:count_item:visual_id
		{
			final L2ItemInstance chest = player.getInventory().getPaperdollItem(Inventory.PAPERDOLL_CHEST);
			if(chest == null || chest.isShadowItem() || chest.isTemporalItem())
			{
				player.sendMessage("Не подходящий предмет.");
				return;
			}
			String[] param = command.split(":");
			if(chest != null && player.getInventory().getCountOf(Integer.parseInt(param[3])) > 0)
				player.scriptRequest("Вы желаете сменить вид брони за "+param[2]+" "+DifferentMethods.getItemName(Integer.parseInt(param[1])), "call_bbs", new String[]{"_bbs_visual_ok"+command.substring(11)});
		}
		else if(command.startsWith("_bbs_referal_get"))
		{
			List<String> res = getRefAccList(player, false);
			if(res.size() > 0)
			{
				try
				{
					int count = player.getVarInt("referal_count", 0);
					for(String name : res)
					{
						mysql.setEx(L2DatabaseFactory.getInstanceLogin(), "UPDATE `stress_referal` SET `success`='1' WHERE `account`=? LIMIT 1", name);
						player.sendMessage("Вы получили награду, за приглашение '***"+name.substring(3)+"'.");
						for(int i=0; i < ConfigValue.VidakReferalRewardToRefer.length; i+=2)
							player.getInventory().addItem((int)ConfigValue.VidakReferalRewardToRefer[i], ConfigValue.VidakReferalRewardToRefer[i+1]);
					}
				}
				catch(SQLException e)
				{
					_log.warning("Unable to process referrals for player " + player);
					e.printStackTrace();
				}
			}

			res = getRefAccList(player, true);
			if(res.size() >= 10)
			{
				try
				{
					for(int i=0;i<(res.size()/10*10);i++)
					{
						String name = res.get(i);
						mysql.setEx(L2DatabaseFactory.getInstanceLogin(), "UPDATE `stress_referal` SET `success`='2' WHERE `account`=? LIMIT 1", name);
						if(i%10==0)
						{
							player.sendMessage("Вы получили награду, за каждого 10-го, приглашеного игрока.");
							for(int i2=0; i2 < ConfigValue.VidakReferalRewardFor10Ref.length; i2+=2)
								player.getInventory().addItem((int)ConfigValue.VidakReferalRewardFor10Ref[i2], ConfigValue.VidakReferalRewardFor10Ref[i2+1]);
						}
					}
				}
				catch(SQLException e)
				{
					_log.warning("Unable to process referrals for player " + player);
					e.printStackTrace();
				}
			}
		}
		else if(command.startsWith("_bbs_referal_html"))
		{
			String content = readHtml(ConfigValue.CommunityBoardHtmlRoot+"referal.htm", player);
			
			int[] referal = getRefStat(player);
			content = content.replace("%referal_all%", String.valueOf(referal[0]));
			content = content.replace("%referal_noble%", String.valueOf(referal[1]));
			content = content.replace("%referal_new_noble%", (referal[2] > 0 ? "<font color=00FF00>" : "<font color=FF0000>")+referal[2]+"</font>");
			separateAndSend(content, player);
		}
		else
			separateAndSend("<html><body><br><br><center>В bbsbuff функция: " + command + " пока не реализована</center><br><br></body></html>", player);
	}

	private int[] getRefStat(L2Player player)
	{
		int all = 0;
		int noble = 0;
		int new_noble = 0;

		ThreadConnection con = null;
		FiltredPreparedStatement statement = null;
		ResultSet rset = null;

		try
		{
			con = L2DatabaseFactory.getInstanceLogin().getConnection();
			statement = con.prepareStatement("SELECT is_noobles, success FROM stress_referal WHERE referal='"+player.getAccountName()+"'");
			rset = statement.executeQuery();

			while(rset.next())
			{
				all++;
				int is_noobles = rset.getInt("is_noobles");
				int success = rset.getInt("success");
				if(is_noobles == 1)
				{
					if(success == 0)
						new_noble++;
					noble++;
				}
			}
		}
		catch(Exception e)
		{
			_log.warning("mSGI: Error in query:" + e);
			e.printStackTrace();
		}
		finally
		{
			DatabaseUtils.closeDatabaseCSR(con, statement, rset);
		}
		return new int[]{all, noble, new_noble};
	}
	/**
	DROP TABLE IF EXISTS `stress_referal`;
	CREATE TABLE `stress_referal` (
	  `id` int(11) NOT NULL AUTO_INCREMENT,
	  `account` varchar(100) NOT NULL,
	  `referal` varchar(100) NOT NULL,
	  `is_third_class` enum('0','1') NOT NULL DEFAULT '0',
	  `is_noobles` enum('0','1') NOT NULL DEFAULT '0',
	  `success` enum('0','1','2') NOT NULL DEFAULT '0',
	  `date` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
	  PRIMARY KEY (`id`),
	  KEY `account_referer` (`account`) USING BTREE,
	  KEY `success` (`success`),
	  KEY `is_third_class` (`is_third_class`),
	  KEY `is_noobles` (`is_noobles`)
	) ENGINE=MyISAM DEFAULT CHARSET=utf8;
	**/
	// Пусть будет так, мне лень делать все в одном списке...
	private List<String> getRefAccList(L2Player player, boolean is_10)
	{
		List<String> res = new ArrayList<String>();
		ThreadConnection con = null;
		FiltredPreparedStatement statement = null;
		ResultSet rset = null;

		try
		{
			con = L2DatabaseFactory.getInstanceLogin().getConnection();
			statement = con.prepareStatement("SELECT account FROM stress_referal WHERE referal='"+player.getAccountName()+"' AND is_noobles='1' AND success='"+(is_10 ? "1" : "0")+"'");
			rset = statement.executeQuery();

			if(rset.next())
				res.add(rset.getString("account"));
		}
		catch(Exception e)
		{
			_log.warning("mSGI: Error in query:" + e);
			e.printStackTrace();
		}
		finally
		{
			DatabaseUtils.closeDatabaseCSR(con, statement, rset);
		}

		return res;
	}

	public void parsewrite(String ar1, String ar2, String ar3, String ar4, String ar5, L2Player player)
	{
	}

	public void onLoad()
	{
		CommunityHandler.getInstance().registerCommunityHandler(this);
	}

	public void onReload()
	{}

	public void onShutdown()
	{}

	public Enum[] getCommunityCommandEnum()
	{
		return Commands.values();
	}
}