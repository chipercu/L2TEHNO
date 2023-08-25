package communityboard;

import java.util.StringTokenizer;

import l2open.config.ConfigValue;
import l2open.extensions.multilang.CustomMessage;
import l2open.extensions.scripts.Functions;
import l2open.extensions.scripts.ScriptFile;
import l2open.gameserver.cache.AugmentName;
import l2open.gameserver.common.DifferentMethods;
import l2open.gameserver.communitybbs.Manager.*;
import l2open.gameserver.handler.ICommunityHandler;
import l2open.gameserver.handler.CommunityHandler;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.L2Skill;
import l2open.gameserver.model.base.L2Augmentation;
import l2open.gameserver.model.items.Inventory;
import l2open.gameserver.model.items.L2ItemInstance;
import l2open.gameserver.serverpackets.InventoryUpdate;
import l2open.gameserver.serverpackets.ShowBoard;
import l2open.gameserver.tables.AugmentationData;
import l2open.gameserver.templates.L2Item;
import l2open.gameserver.templates.L2Item.Grade;
import l2open.gameserver.templates.L2Weapon.WeaponType;
import l2open.gameserver.templates.OptionDataTemplate;
import l2open.gameserver.xml.loader.XmlOptionDataLoader;
import l2open.util.*;

import java.util.logging.Logger;

/**
 * 
 * @author L2CCCP
 * @site http://l2cccp.com
 * 
 */
public class CommunityBoardForge extends BaseBBSManager implements ICommunityHandler, ScriptFile
{
	private static final Logger _log = Logger.getLogger(CommunityBoardForge.class.getName());

	private static int CommunityBoardForge = 1 << 123;
	private static int CommunityBoardForgeAtt = 1 << 183;
	private static int CommunityBoardForgeAug = 1 << 188;

	private static enum Commands
	{
		_bbsforges
	}

	@Override
	public void parsecmd(String command, L2Player player)
	{
		if(player.getEventMaster() != null && player.getEventMaster().blockBbs())
			return;
		if(player.is_block || player.isInEvent() > 0)
			return;
		String content = "";
		if(command.equals("_bbsforges"))
			content = Files.read(ConfigValue.CommunityBoardHtmlRoot + "forge/index.htm", player);
		else if(command.equals("_bbsforges:enchant:list"))
		{
			//if((Functions.script & CommunityBoardForge) != CommunityBoardForge)
			//	return;
			content = Files.read(ConfigValue.CommunityBoardHtmlRoot + "forge/itemlist.htm", player);

			String noicon = "icon.NOIMAGE";
			String slotclose = "L2UI_CT1.ItemWindow_DF_SlotBox_Disable";
			String dot = "<font color=\"FF0000\">...</font>";
			String maxenchant = new CustomMessage("communityboard.forge.enchant.max", player).toString();
			String picenchant = "l2ui_ch3.multisell_plusicon";
			String pvp = "icon.pvp_tab";

			String HeadButton = dot;
			String HeadIcon = noicon;
			String HeadPic = slotclose;
			String HeadName = new CustomMessage("common.item.not.clothed.head", player).toString();

			String ChestButton = dot;
			String ChestIcon = noicon;
			String ChestPic = slotclose;
			String ChestName = new CustomMessage("common.item.not.clothed.chest", player).toString();

			String LegsButton = dot;
			String LegsIcon = noicon;
			String LegsPic = slotclose;
			String LegsName = new CustomMessage("common.item.not.clothed.legs", player).toString();

			String FeetButton = dot;
			String FeetIcon = noicon;
			String FeetPic = slotclose;
			String FeetName = new CustomMessage("common.item.not.clothed.feet", player).toString();

			String GlovesButton = dot;
			String GlovesIcon = noicon;
			String GlovesPic = slotclose;
			String GlovesName = new CustomMessage("common.item.not.clothed.gloves", player).toString();

			String LEarButton = dot;
			String LEarIcon = noicon;
			String LEarPic = slotclose;
			String LEarName = new CustomMessage("common.item.not.clothed.lear", player).toString();

			String REarButton = dot;
			String REarIcon = noicon;
			String REarPic = slotclose;
			String REarName = new CustomMessage("common.item.not.clothed.rear", player).toString();

			String NeckButton = dot;
			String NeckIcon = noicon;
			String NeckPic = slotclose;
			String NeckName = new CustomMessage("common.item.not.clothed.neck", player).toString();

			String LRingButton = dot;
			String LRingIcon = noicon;
			String LRingPic = slotclose;
			String LRingName = new CustomMessage("common.item.not.clothed.lring", player).toString();

			String RRingButton = dot;
			String RRingIcon = noicon;
			String RRingPic = slotclose;
			String RRingName = new CustomMessage("common.item.not.clothed.rring", player).toString();

			String WeaponButton = dot;
			String WeaponIcon = noicon;
			String WeaponPic = slotclose;
			String WeaponName = new CustomMessage("common.item.not.clothed.weapon", player).toString();

			String ShieldButton = dot;
			String ShieldIcon = noicon;
			String ShieldPic = slotclose;
			String ShieldName = new CustomMessage("common.item.not.clothed.shield", player).toString();

			L2ItemInstance head = player.getInventory().getPaperdollItem(Inventory.PAPERDOLL_HEAD);
			L2ItemInstance chest = player.getInventory().getPaperdollItem(Inventory.PAPERDOLL_CHEST);
			L2ItemInstance legs = player.getInventory().getPaperdollItem(Inventory.PAPERDOLL_LEGS);
			L2ItemInstance gloves = player.getInventory().getPaperdollItem(Inventory.PAPERDOLL_GLOVES);
			L2ItemInstance feet = player.getInventory().getPaperdollItem(Inventory.PAPERDOLL_FEET);

			L2ItemInstance lhand = player.getInventory().getPaperdollItem(Inventory.PAPERDOLL_LHAND);
			L2ItemInstance rhand = player.getInventory().getPaperdollItem(Inventory.PAPERDOLL_RHAND);

			L2ItemInstance lfinger = player.getInventory().getPaperdollItem(Inventory.PAPERDOLL_LFINGER);
			L2ItemInstance rfinger = player.getInventory().getPaperdollItem(Inventory.PAPERDOLL_RFINGER);
			L2ItemInstance neck = player.getInventory().getPaperdollItem(Inventory.PAPERDOLL_NECK);
			L2ItemInstance lear = player.getInventory().getPaperdollItem(Inventory.PAPERDOLL_LEAR);
			L2ItemInstance rear = player.getInventory().getPaperdollItem(Inventory.PAPERDOLL_REAR);

			if(head != null)
			{
				HeadIcon = head.getItem().getIcon();
				HeadName = head.getName() + " " + (head.getEnchantLevel() > 0 ? "+" + head.getEnchantLevel() : "");

				if(!head.canBeEnchanted())
				{
					HeadButton = dot;
					HeadPic = slotclose;
				}
				else if(head.getEnchantLevel() >= ConfigValue.BBS_ENCHANT_MAX[1])
				{
					HeadButton = maxenchant;
					HeadPic = slotclose;
				}
				else
				{
					HeadButton = "<button action=\"bypass -h _bbsforges:enchant:item:" + Inventory.PAPERDOLL_HEAD + "\" value=\"" + new CustomMessage("common.enchant", player).toString() + "\"width=120 height=25 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\">";
					HeadPic = picenchant;
				}
			}

			if(chest != null)
			{
				ChestIcon = chest.getItem().getIcon();
				ChestName = chest.getName() + " " + (chest.getEnchantLevel() > 0 ? "+" + chest.getEnchantLevel() : "");

				if(!chest.canBeEnchanted())
				{
					ChestButton = dot;
					ChestPic = slotclose;
				}
				else if(chest.getEnchantLevel() >= ConfigValue.BBS_ENCHANT_MAX[1])
				{
					ChestButton = maxenchant;
					ChestPic = slotclose;
				}
				else
				{
					ChestButton = "<button action=\"bypass -h _bbsforges:enchant:item:" + Inventory.PAPERDOLL_CHEST + "\" value=\"" + new CustomMessage("common.enchant", player).toString() + "\" width=120 height=25 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\">";
					if(chest.getItem().isPvP())
						ChestPic = pvp;
					else
						ChestPic = picenchant;
				}
			}

			if(legs != null)
			{
				LegsIcon = legs.getItem().getIcon();
				LegsName = legs.getName() + " " + (legs.getEnchantLevel() > 0 ? "+" + legs.getEnchantLevel() : "");

				if(!legs.canBeEnchanted())
				{
					LegsButton = dot;
					LegsPic = slotclose;
				}
				else if(legs.getEnchantLevel() >= ConfigValue.BBS_ENCHANT_MAX[1])
				{
					LegsButton = maxenchant;
					LegsPic = slotclose;
				}
				else
				{
					LegsButton = "<button action=\"bypass -h _bbsforges:enchant:item:" + Inventory.PAPERDOLL_LEGS + "\" value=\"" + new CustomMessage("common.enchant", player).toString() + "\" width=120 height=25 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\">";
					LegsPic = picenchant;
				}
			}

			if(gloves != null)
			{
				GlovesIcon = gloves.getItem().getIcon();
				GlovesName = gloves.getName() + " " + (gloves.getEnchantLevel() > 0 ? "+" + gloves.getEnchantLevel() : "");

				if(!gloves.canBeEnchanted())
				{
					GlovesButton = dot;
					GlovesPic = slotclose;
				}
				else if(gloves.getEnchantLevel() >= ConfigValue.BBS_ENCHANT_MAX[1])
				{
					GlovesButton = maxenchant;
					GlovesPic = slotclose;
				}
				else
				{
					GlovesButton = "<button action=\"bypass -h _bbsforges:enchant:item:" + Inventory.PAPERDOLL_GLOVES + "\" value=\"" + new CustomMessage("common.enchant", player).toString() + "\" width=120 height=25 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\">";
					GlovesPic = picenchant;
				}
			}

			if(feet != null)
			{
				FeetIcon = feet.getItem().getIcon();
				FeetName = feet.getName() + " " + (feet.getEnchantLevel() > 0 ? "+" + feet.getEnchantLevel() : "");

				if(!feet.canBeEnchanted())
				{
					FeetButton = dot;
					FeetPic = slotclose;
				}
				else if(feet.getEnchantLevel() >= ConfigValue.BBS_ENCHANT_MAX[1])
				{
					FeetButton = maxenchant;
					FeetPic = slotclose;
				}
				else
				{
					FeetButton = "<button action=\"bypass -h _bbsforges:enchant:item:" + Inventory.PAPERDOLL_FEET + "\" value=\"" + new CustomMessage("common.enchant", player).toString() + "\" width=120 height=25 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\">";
					FeetPic = picenchant;
				}
			}

			if(rhand != null)
			{
				WeaponIcon = rhand.getItem().getIcon();
				WeaponName = rhand.getName() + " " + (rhand.getEnchantLevel() > 0 ? "+" + rhand.getEnchantLevel() : "");

				if(!rhand.canBeEnchanted())
				{
					WeaponButton = dot;
					WeaponPic = slotclose;
				}
				else if(rhand.getEnchantLevel() >= ConfigValue.BBS_ENCHANT_MAX[0])
				{
					WeaponButton = maxenchant;
					WeaponPic = slotclose;
				}
				else
				{
					WeaponButton = "<button action=\"bypass -h _bbsforges:enchant:item:" + Inventory.PAPERDOLL_RHAND + "\" value=\"" + new CustomMessage("common.enchant", player).toString() + "\" width=120 height=25 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\">";
					if(rhand.getItem().isPvP())
						WeaponPic = pvp;
					else
						WeaponPic = picenchant;
				}

				if(rhand.getItem().getItemType() == WeaponType.BIGBLUNT || rhand.getItem().getItemType() == WeaponType.BOW || rhand.getItem().getItemType() == WeaponType.DUALDAGGER || rhand.getItem().getItemType() == WeaponType.ANCIENTSWORD || rhand.getItem().getItemType() == WeaponType.CROSSBOW || rhand.getItem().getItemType() == WeaponType.BIGBLUNT || rhand.getItem().getItemType() == WeaponType.BIGSWORD || rhand.getItem().getItemType() == WeaponType.DUALFIST || rhand.getItem().getItemType() == WeaponType.DUAL || rhand.getItem().getItemType() == WeaponType.POLE || rhand.getItem().getItemType() == WeaponType.FIST)
				{
					ShieldButton = dot;
					ShieldIcon = rhand.getItem().getIcon();
					ShieldName = rhand.getName() + " " + (rhand.getEnchantLevel() > 0 ? "+" + rhand.getEnchantLevel() : "");
					ShieldPic = slotclose;
				}
			}

			if(lhand != null)
			{
				ShieldIcon = lhand.getItem().getIcon();
				ShieldName = lhand.getName() + " " + (lhand.getEnchantLevel() > 0 ? "+" + lhand.getEnchantLevel() : "");

				if(!lhand.canBeEnchanted())
				{
					ShieldButton = dot;
					ShieldPic = slotclose;
				}
				else if(!lhand.getItem().isArrow())
				{
					if(lhand.getEnchantLevel() >= ConfigValue.BBS_ENCHANT_MAX[1])
					{
						ShieldButton = maxenchant;
						ShieldPic = slotclose;
					}
					else
					{
						ShieldButton = "<button action=\"bypass -h _bbsforges:enchant:item:" + Inventory.PAPERDOLL_LHAND + "\" value=\"" + new CustomMessage("common.enchant", player).toString() + "\"  width=120 height=25 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\">";
						ShieldPic = picenchant;
					}
				}
				else
				{
					ShieldButton = dot;
					ShieldPic = slotclose;
				}
			}

			if(lfinger != null)
			{
				LRingIcon = lfinger.getItem().getIcon();
				LRingName = lfinger.getName() + " " + (lfinger.getEnchantLevel() > 0 ? "+" + lfinger.getEnchantLevel() : "");

				if(!lfinger.canBeEnchanted())
				{
					LRingButton = dot;
					LRingPic = slotclose;
				}
				else if(lfinger.getEnchantLevel() >= ConfigValue.BBS_ENCHANT_MAX[2])
				{
					LRingButton = maxenchant;
					LRingPic = slotclose;
				}
				else
				{
					LRingButton = "<button action=\"bypass -h _bbsforges:enchant:item:" + Inventory.PAPERDOLL_LFINGER + "\" value=\"" + new CustomMessage("common.enchant", player).toString() + "\" width=120 height=25 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\">";
					LRingPic = picenchant;
				}
			}

			if(rfinger != null)
			{
				RRingIcon = rfinger.getItem().getIcon();
				RRingName = rfinger.getName() + " " + (rfinger.getEnchantLevel() > 0 ? "+" + rfinger.getEnchantLevel() : "");

				if(!rfinger.canBeEnchanted())
				{
					RRingButton = dot;
					RRingPic = slotclose;
				}
				else if(rfinger.getEnchantLevel() >= ConfigValue.BBS_ENCHANT_MAX[2])
				{
					RRingButton = maxenchant;
					RRingPic = slotclose;
				}
				else
				{
					RRingButton = "<button action=\"bypass -h _bbsforges:enchant:item:" + Inventory.PAPERDOLL_RFINGER + "\" value=\"" + new CustomMessage("common.enchant", player).toString() + "\" width=120 height=25 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\">";
					RRingPic = picenchant;
				}
			}

			if(neck != null)
			{
				NeckIcon = neck.getItem().getIcon();
				NeckName = neck.getName() + " " + (neck.getEnchantLevel() > 0 ? "+" + neck.getEnchantLevel() : "");

				if(!neck.canBeEnchanted())
				{
					NeckButton = dot;
					NeckPic = slotclose;
				}
				else if(neck.getEnchantLevel() >= ConfigValue.BBS_ENCHANT_MAX[2])
				{
					NeckButton = maxenchant;
					NeckPic = slotclose;
				}
				else
				{
					NeckButton = "<button action=\"bypass -h _bbsforges:enchant:item:" + Inventory.PAPERDOLL_NECK + "\" value=\"" + new CustomMessage("common.enchant", player).toString() + "\" width=120 height=25 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\">";
					NeckPic = picenchant;
				}
			}

			if(lear != null)
			{
				LEarIcon = lear.getItem().getIcon();
				LEarName = lear.getName() + " " + (lear.getEnchantLevel() > 0 ? "+" + lear.getEnchantLevel() : "");

				if(!lear.canBeEnchanted())
				{
					LEarButton = dot;
					LEarPic = slotclose;
				}
				else if(lear.getEnchantLevel() >= ConfigValue.BBS_ENCHANT_MAX[2])
				{
					LEarButton = maxenchant;
					LEarPic = slotclose;
				}
				else
				{
					LEarButton = "<button action=\"bypass -h _bbsforges:enchant:item:" + Inventory.PAPERDOLL_LEAR + "\" value=\"" + new CustomMessage("common.enchant", player).toString() + "\" width=120 height=25 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\">";
					LEarPic = picenchant;
				}
			}

			if(rear != null)
			{
				REarIcon = rear.getItem().getIcon();
				REarName = rear.getName() + " " + (rear.getEnchantLevel() > 0 ? "+" + rear.getEnchantLevel() : "");

				if(!rear.canBeEnchanted())
				{
					REarButton = dot;
					REarPic = slotclose;
				}
				else if(rear.getEnchantLevel() >= ConfigValue.BBS_ENCHANT_MAX[2])
				{
					REarButton = maxenchant;
					REarPic = slotclose;
				}
				else
				{
					REarButton = "<button action=\"bypass -h _bbsforges:enchant:item:" + Inventory.PAPERDOLL_REAR + "\" value=\"" + new CustomMessage("common.enchant", player).toString() + "\" width=120 height=25 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\">";
					REarPic = picenchant;
				}
			}

			content = content.replace("<?content?>", page(0));

			content = content.replace("<?head_name?>", HeadName);
			content = content.replace("<?head_icon?>", HeadIcon);
			content = content.replace("<?head_pic?>", HeadPic);
			content = content.replace("<?head_button?>", HeadButton);

			content = content.replace("<?chest_name?>", ChestName);
			content = content.replace("<?chest_icon?>", ChestIcon);
			content = content.replace("<?chest_pic?>", ChestPic);
			content = content.replace("<?chest_button?>", ChestButton);

			content = content.replace("<?legs_name?>", LegsName);
			content = content.replace("<?legs_icon?>", LegsIcon);
			content = content.replace("<?legs_pic?>", LegsPic);
			content = content.replace("<?legs_button?>", LegsButton);

			content = content.replace("<?gloves_name?>", GlovesName);
			content = content.replace("<?gloves_icon?>", GlovesIcon);
			content = content.replace("<?gloves_pic?>", GlovesPic);
			content = content.replace("<?gloves_button?>", GlovesButton);

			content = content.replace("<?feet_name?>", FeetName);
			content = content.replace("<?feet_icon?>", FeetIcon);
			content = content.replace("<?feet_pic?>", FeetPic);
			content = content.replace("<?feet_button?>", FeetButton);

			content = content.replace("<?lear_name?>", LEarName);
			content = content.replace("<?lear_icon?>", LEarIcon);
			content = content.replace("<?lear_pic?>", LEarPic);
			content = content.replace("<?lear_button?>", LEarButton);

			content = content.replace("<?rear_name?>", REarName);
			content = content.replace("<?rear_icon?>", REarIcon);
			content = content.replace("<?rear_pic?>", REarPic);
			content = content.replace("<?rear_button?>", REarButton);

			content = content.replace("<?neck_name?>", NeckName);
			content = content.replace("<?neck_icon?>", NeckIcon);
			content = content.replace("<?neck_pic?>", NeckPic);
			content = content.replace("<?neck_button?>", NeckButton);

			content = content.replace("<?lring_name?>", LRingName);
			content = content.replace("<?lring_icon?>", LRingIcon);
			content = content.replace("<?lring_pic?>", LRingPic);
			content = content.replace("<?lring_button?>", LRingButton);

			content = content.replace("<?rring_name?>", RRingName);
			content = content.replace("<?rring_icon?>", RRingIcon);
			content = content.replace("<?rring_pic?>", RRingPic);
			content = content.replace("<?rring_button?>", RRingButton);

			content = content.replace("<?weapon_name?>", WeaponName);
			content = content.replace("<?weapon_icon?>", WeaponIcon);
			content = content.replace("<?weapon_pic?>", WeaponPic);
			content = content.replace("<?weapon_button?>", WeaponButton);

			content = content.replace("<?shield_name?>", ShieldName);
			content = content.replace("<?shield_icon?>", ShieldIcon);
			content = content.replace("<?shield_pic?>", ShieldPic);
			content = content.replace("<?shield_button?>", ShieldButton);
		}
		else if(command.startsWith("_bbsforges:enchant:item:"))
		{
			//if((Functions.script & CommunityBoardForge) != CommunityBoardForge)
			//	return;
			StringTokenizer st = new StringTokenizer(command, ":");
			st.nextToken();
			st.nextToken();
			st.nextToken();
			int item = Integer.parseInt(st.nextToken());
			String name = DifferentMethods.getItemName(ConfigValue.BBS_ENCHANT_ITEM);

			name.replace(" {PvP}", "");

			if(name.isEmpty())
				name = new CustomMessage("common.item.no.name", player).toString();

			if(item < 1 || item > 12)
				return;

			L2ItemInstance _item = player.getInventory().getPaperdollItem(item);
			if(_item == null)
			{
				player.sendMessage(new CustomMessage("communityboard.forge.item.null", player).toString());
				DifferentMethods.communityNextPage(player, "_bbsforges:enchant:list");
				return;
			}

			if(_item.getItem().isArrow())
			{
				player.sendMessage(new CustomMessage("communityboard.forge.item.arrow", player));
				DifferentMethods.communityNextPage(player, "_bbsforges:enchant:list");
				return;
			}

			content = Files.read(ConfigValue.CommunityBoardHtmlRoot + "forge/enchant.htm", player);
			StringBuilder html = new StringBuilder("");

			html.append("<br><table border=0 cellspacing=0 cellpadding=0 width=240 height=330 background=\"l2ui_ct1.Windows_DF_TooltipBG\">");
			html.append("<tr>");
			html.append("<td width=230 align=center valign=top>");
			html.append("<table border=0 cellspacing=0 cellpadding=0 width=127 height=100 background=\"l2ui_ch3.refinegrade2_00\">");
			html.append("<tr>");
			html.append("<td width=92 height=22></td>");
			html.append("<td width=30 height=31></td>");
			html.append("<td width=100 height=22></td>");
			html.append("</tr>");
			html.append("<tr>");
			html.append("<td width=92 height=32 align=right valign=top></td>");
			html.append("<td width=32 height=32 align=center valign=top>");
			html.append("<img src=\"" + _item.getItem().getIcon() + "\" width=\"32\" height=\"32\">");
			html.append("</td>");
			html.append("<td width=110 height=32 align=left valign=top></td>");
			html.append("</tr>");
			html.append("</table>");
			html.append("<table border=0 cellspacing=0 cellpadding=0 width=230 height=20>");
			html.append("<tr>");
			html.append("<td width=230 align=center valign=top>");
			html.append("<font name=\"hs9\" color=\"LEVEL\">" + _item.getName() + "</font><font name=\"hs9\" color=\"LEVEL\">" + (_item.getEnchantLevel() <= 0 ? "</font>" : " +" + _item.getEnchantLevel()) + "</font>");
			html.append("</td>");
			html.append("</tr>");
			html.append("<tr>");
			html.append("<td width=230 align=center valign=top height=20>");
			html.append("<font name=\"hs9\">" + new CustomMessage("communityboard.forge.enchant.select", player) + "</font>");
			html.append("</td>");
			html.append("</tr>");

			int[] level = _item.getItem().isWeapon() ? ConfigValue.BBS_WEAPON_ENCHANT_LVL : _item.getItem().isArmor() ? ConfigValue.BBS_ARMOR_ENCHANT_LVL : ConfigValue.BBS_JEWELS_ENCHANT_LVL;
			for(int i = 0; i < level.length; i++)
			{
				if(_item.getEnchantLevel() < level[i])
				{
					html.append("<tr>");
					html.append("<td width=230 align=center valign=top height=35>");
					html.append("<button action=\"bypass -h _bbsforges:enchant:" + i * item + ":" + item + "\" value=\"+" + level[i] + " (" + (_item.getItem().isWeapon() ? ConfigValue.BBS_ENCHANT_PRICE_WEAPON[i] : _item.getItem().isArmor() ? ConfigValue.BBS_ENCHANT_PRICE_ARMOR[i] : ConfigValue.BBS_ENCHANT_PRICE_JEWELS[i]) + " " + name + ")\" width=200 height=31 back=\"L2UI_CT1.OlympiadWnd_DF_Fight3None_Down\" fore=\"L2UI_CT1.OlympiadWnd_DF_Fight3None\">");
					html.append("</td>");
					html.append("</tr>");
				}
			}

			html.append("</table>");
			html.append("</td>");
			html.append("</tr>");
			html.append("</table>");

			content = content.replace("<?content?>", html.toString());
		}
		else if(command.startsWith("_bbsforges:enchant:"))
		{
			//if((Functions.script & CommunityBoardForge) != CommunityBoardForge)
			//	return;
			StringTokenizer st = new StringTokenizer(command, ":");
			st.nextToken();
			st.nextToken();
			int val = Integer.parseInt(st.nextToken());
			int item = Integer.parseInt(st.nextToken());

			int conversion = val / item;

			L2ItemInstance _item = player.getInventory().getPaperdollItem(item);
			if(_item != null)
			{
				int[] level = _item.getItem().isWeapon() ? ConfigValue.BBS_WEAPON_ENCHANT_LVL : _item.getItem().isArmor() ? ConfigValue.BBS_ARMOR_ENCHANT_LVL : ConfigValue.BBS_JEWELS_ENCHANT_LVL;
				int Value = level[conversion];

				int max = _item.getItem().isWeapon() ? ConfigValue.BBS_ENCHANT_MAX[0] : _item.getItem().isArmor() ? ConfigValue.BBS_ENCHANT_MAX[1] : ConfigValue.BBS_ENCHANT_MAX[2];
				if(Value > max)
				{
					DifferentMethods.clear(player);
					return;
				}
				
				if(!_item.canBeEnchanted())
				{
					DifferentMethods.communityNextPage(player, "_bbsforges:enchant:list");
					return;
				}
				else if(_item.getItem().isArrow())
				{
					player.sendMessage(new CustomMessage("communityboard.forge.item.arrow", player));
					DifferentMethods.communityNextPage(player, "_bbsforges:enchant:list");
					return;
				}

				int price = _item.isWeapon() ? ConfigValue.BBS_ENCHANT_PRICE_WEAPON[conversion] : _item.getItem().isArmor() ? ConfigValue.BBS_ENCHANT_PRICE_ARMOR[conversion] : ConfigValue.BBS_ENCHANT_PRICE_JEWELS[conversion];

			
				if(DifferentMethods.getPay(player, ConfigValue.BBS_ENCHANT_ITEM, price, true))
				{
					player.getInventory().unEquipItem(_item);
					_item.setEnchantLevel(Value);
					player.getInventory().equipItem(_item, false);

					player.sendPacket(new InventoryUpdate().addModifiedItem(_item));
					player.broadcastUserInfo(true);

					player.sendMessage(new CustomMessage("communityboard.forge.enchant.success", player).addString(_item.getName()).addNumber(Value));
					Log.add("enchant item " + _item.getName() + " at +" + Value + "", "CommunityBoardForge", player);
				}
			}

			DifferentMethods.communityNextPage(player, "_bbsforges:enchant:list");
			return;
		}
		else if(command.equals("_bbsforges:attribute:list"))
		{
			//if((Functions.script & CommunityBoardForgeAtt) != CommunityBoardForgeAtt)
			//	return;
			content = Files.read(ConfigValue.CommunityBoardHtmlRoot + "forge/attributelist.htm", player);

			String noicon = "icon.NOIMAGE";
			String slotclose = "L2UI_CT1.ItemWindow_DF_SlotBox_Disable";
			String dot = "<font color=\"FF0000\">...</font>";
			String immposible = new CustomMessage("communityboard.forge.attribute.immposible", player).toString();
			String maxenchant = new CustomMessage("communityboard.forge.attribute.maxenchant", player).toString();
			String heronot = new CustomMessage("communityboard.forge.attribute.heronot", player).toString();
			String picenchant = "l2ui_ch3.multisell_plusicon";
			String pvp = "icon.pvp_tab";

			String HeadButton = dot;
			String HeadIcon = noicon;
			String HeadPic = slotclose;
			String HeadName = new CustomMessage("common.item.not.clothed.head", player).toString();

			String ChestButton = dot;
			String ChestIcon = noicon;
			String ChestPic = slotclose;
			String ChestName = new CustomMessage("common.item.not.clothed.chest", player).toString();

			String LegsButton = dot;
			String LegsIcon = noicon;
			String LegsPic = slotclose;
			String LegsName = new CustomMessage("common.item.not.clothed.legs", player).toString();

			String FeetButton = dot;
			String FeetIcon = noicon;
			String FeetPic = slotclose;
			String FeetName = new CustomMessage("common.item.not.clothed.feet", player).toString();

			String GlovesButton = dot;
			String GlovesIcon = noicon;
			String GlovesPic = slotclose;
			String GlovesName = new CustomMessage("common.item.not.clothed.gloves", player).toString();

			String LEarButton = dot;
			String LEarIcon = noicon;
			String LEarPic = slotclose;
			String LEarName = new CustomMessage("common.item.not.clothed.lear", player).toString();

			String REarButton = dot;
			String REarIcon = noicon;
			String REarPic = slotclose;
			String REarName = new CustomMessage("common.item.not.clothed.rear", player).toString();

			String NeckButton = dot;
			String NeckIcon = noicon;
			String NeckPic = slotclose;
			String NeckName = new CustomMessage("common.item.not.clothed.neck", player).toString();

			String LRingButton = dot;
			String LRingIcon = noicon;
			String LRingPic = slotclose;
			String LRingName = new CustomMessage("common.item.not.clothed.lring", player).toString();

			String RRingButton = dot;
			String RRingIcon = noicon;
			String RRingPic = slotclose;
			String RRingName = new CustomMessage("common.item.not.clothed.rring", player).toString();

			String WeaponButton = dot;
			String WeaponIcon = noicon;
			String WeaponPic = slotclose;
			String WeaponName = new CustomMessage("common.item.not.clothed.weapon", player).toString();

			String ShieldButton = dot;
			String ShieldIcon = noicon;
			String ShieldPic = slotclose;
			String ShieldName = new CustomMessage("common.item.not.clothed.shield", player).toString();

			L2ItemInstance head = player.getInventory().getPaperdollItem(Inventory.PAPERDOLL_HEAD);
			L2ItemInstance chest = player.getInventory().getPaperdollItem(Inventory.PAPERDOLL_CHEST);
			L2ItemInstance legs = player.getInventory().getPaperdollItem(Inventory.PAPERDOLL_LEGS);
			L2ItemInstance gloves = player.getInventory().getPaperdollItem(Inventory.PAPERDOLL_GLOVES);
			L2ItemInstance feet = player.getInventory().getPaperdollItem(Inventory.PAPERDOLL_FEET);

			L2ItemInstance lhand = player.getInventory().getPaperdollItem(Inventory.PAPERDOLL_LHAND);
			L2ItemInstance rhand = player.getInventory().getPaperdollItem(Inventory.PAPERDOLL_RHAND);

			L2ItemInstance lfinger = player.getInventory().getPaperdollItem(Inventory.PAPERDOLL_LFINGER);
			L2ItemInstance rfinger = player.getInventory().getPaperdollItem(Inventory.PAPERDOLL_RFINGER);
			L2ItemInstance neck = player.getInventory().getPaperdollItem(Inventory.PAPERDOLL_NECK);
			L2ItemInstance lear = player.getInventory().getPaperdollItem(Inventory.PAPERDOLL_LEAR);
			L2ItemInstance rear = player.getInventory().getPaperdollItem(Inventory.PAPERDOLL_REAR);

			if(head != null)
			{
				HeadIcon = head.getItem().getIcon();
				HeadName = head.getName() + " " + (head.getEnchantLevel() > 0 ? "+" + head.getEnchantLevel() : "");

				if(itemCheckGrade(player, head))
				{
					if(((head.getDefenceFire() | head.getDefenceWater()) & (head.getDefenceWind() | head.getDefenceEarth()) & (head.getDefenceHoly() | head.getDefenceUnholy())) >= ConfigValue.BBS_ENCHANT_ARMOR_ATTRIBUTE_MAX)
					{
						HeadButton = maxenchant;
						HeadPic = slotclose;
					}
					else
					{
						if(ConfigValue.BBS_ENCHANT_HEAD_ATTRIBUTE)
						{
							HeadButton = "<button action=\"bypass -h _bbsforges:attribute:item:" + Inventory.PAPERDOLL_HEAD + "\" value=\"" + new CustomMessage("common.enchant.attribute", player).toString() + "\" width=120 height=25 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\">";
							HeadPic = picenchant;
						}
						else
						{
							HeadButton = immposible;
							HeadPic = slotclose;
						}
					}
				}
				else
				{
					HeadButton = immposible;
					HeadPic = slotclose;
				}
			}

			if(chest != null)
			{
				ChestIcon = chest.getItem().getIcon();
				ChestName = chest.getName() + " " + (chest.getEnchantLevel() > 0 ? "+" + chest.getEnchantLevel() : "");

				if(itemCheckGrade(player, chest))
				{
					if(((chest.getDefenceFire() | chest.getDefenceWater()) & (chest.getDefenceWind() | chest.getDefenceEarth()) & (chest.getDefenceHoly() | chest.getDefenceUnholy())) >= ConfigValue.BBS_ENCHANT_ARMOR_ATTRIBUTE_MAX)
					{
						ChestButton = maxenchant;
						ChestPic = slotclose;
					}
					else
					{
						ChestButton = "<button action=\"bypass -h _bbsforges:attribute:item:" + Inventory.PAPERDOLL_CHEST + "\" value=\"" + new CustomMessage("common.enchant.attribute", player).toString() + "\" width=120 height=25 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\">";
						if(chest.getItem().isPvP())
							ChestPic = pvp;
						else
							ChestPic = picenchant;
					}
				}
				else
				{
					ChestButton = immposible;
					ChestPic = slotclose;
				}
			}

			if(legs != null)
			{
				LegsIcon = legs.getItem().getIcon();
				LegsName = legs.getName() + " " + (legs.getEnchantLevel() > 0 ? "+" + legs.getEnchantLevel() : "");

				if(itemCheckGrade(player, legs))
				{
					if(((legs.getDefenceFire() | legs.getDefenceWater()) & (legs.getDefenceWind() | legs.getDefenceEarth()) & (legs.getDefenceHoly() | legs.getDefenceUnholy())) >= ConfigValue.BBS_ENCHANT_ARMOR_ATTRIBUTE_MAX)
					{
						LegsButton = maxenchant;
						LegsPic = slotclose;
					}
					else
					{
						LegsButton = "<button action=\"bypass -h _bbsforges:attribute:item:" + Inventory.PAPERDOLL_LEGS + "\" value=\"" + new CustomMessage("common.enchant.attribute", player).toString() + "\" width=120 height=25 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\">";
						LegsPic = picenchant;
					}
				}
				else
				{
					LegsButton = immposible;
					LegsPic = slotclose;
				}
			}

			if(gloves != null)
			{
				GlovesIcon = gloves.getItem().getIcon();
				GlovesName = gloves.getName() + " " + (gloves.getEnchantLevel() > 0 ? "+" + gloves.getEnchantLevel() : "");

				if(itemCheckGrade(player, gloves))
				{
					if(((gloves.getDefenceFire() | gloves.getDefenceWater()) & (gloves.getDefenceWind() | gloves.getDefenceEarth()) & (gloves.getDefenceHoly() | gloves.getDefenceUnholy())) >= ConfigValue.BBS_ENCHANT_ARMOR_ATTRIBUTE_MAX)
					{
						GlovesButton = maxenchant;
						GlovesPic = slotclose;
					}
					else
					{
						GlovesButton = "<button action=\"bypass -h _bbsforges:attribute:item:" + Inventory.PAPERDOLL_GLOVES + "\" value=\"" + new CustomMessage("common.enchant.attribute", player).toString() + "\" width=120 height=25 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\">";
						GlovesPic = picenchant;
					}
				}
				else
				{
					GlovesButton = immposible;
					GlovesPic = slotclose;
				}
			}

			if(feet != null)
			{
				FeetIcon = feet.getItem().getIcon();
				FeetName = feet.getName() + " " + (feet.getEnchantLevel() > 0 ? "+" + feet.getEnchantLevel() : "");

				if(itemCheckGrade(player, feet))
				{
					if(((feet.getDefenceFire() | feet.getDefenceWater()) & (feet.getDefenceWind() | feet.getDefenceEarth()) & (feet.getDefenceHoly() | feet.getDefenceUnholy())) >= ConfigValue.BBS_ENCHANT_ARMOR_ATTRIBUTE_MAX)
					{
						FeetButton = maxenchant;
						FeetPic = slotclose;
					}
					else
					{
						FeetButton = "<button action=\"bypass -h _bbsforges:attribute:item:" + Inventory.PAPERDOLL_FEET + "\" value=\"" + new CustomMessage("common.enchant.attribute", player).toString() + "\" width=120 height=25 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\">";
						FeetPic = picenchant;
					}
				}
				else
				{
					FeetButton = immposible;
					FeetPic = slotclose;
				}
			}

			if(rhand != null)
			{
				WeaponIcon = rhand.getItem().getIcon();
				WeaponName = rhand.getName() + " " + (rhand.getEnchantLevel() > 0 ? "+" + rhand.getEnchantLevel() : "");

				if(rhand.getAttackElementValue() >= ConfigValue.BBS_ENCHANT_WEAPON_ATTRIBUTE_MAX)
				{
					WeaponButton = maxenchant;
					WeaponPic = slotclose;
				}
				else
				{
					if(itemCheckGrade(player, rhand) && !rhand.isHeroWeapon())
					{
						if(rhand.getItem().isHeroWeapon())
						{
							WeaponButton = heronot;
							WeaponPic = slotclose;
						}
						else
						{
							WeaponButton = "<button action=\"bypass -h _bbsforges:attribute:item:" + Inventory.PAPERDOLL_RHAND + "\" value=\"" + new CustomMessage("common.enchant.attribute", player).toString() + "\" width=120 height=25 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\">";
							if(rhand.getItem().isPvP())
								WeaponPic = pvp;
							else
								WeaponPic = picenchant;
						}
					}
					else
					{
						WeaponButton = immposible;
						WeaponPic = slotclose;
					}
				}

				if(rhand.getItem().getItemType() == WeaponType.BIGBLUNT || rhand.getItem().getItemType() == WeaponType.BOW || rhand.getItem().getItemType() == WeaponType.DUALDAGGER || rhand.getItem().getItemType() == WeaponType.ANCIENTSWORD || rhand.getItem().getItemType() == WeaponType.CROSSBOW || rhand.getItem().getItemType() == WeaponType.BIGBLUNT || rhand.getItem().getItemType() == WeaponType.BIGSWORD || rhand.getItem().getItemType() == WeaponType.DUALFIST || rhand.getItem().getItemType() == WeaponType.DUAL || rhand.getItem().getItemType() == WeaponType.POLE || rhand.getItem().getItemType() == WeaponType.FIST)
				{
					ShieldButton = dot;
					ShieldIcon = rhand.getItem().getIcon();
					ShieldName = rhand.getName() + " " + (rhand.getEnchantLevel() > 0 ? "+" + rhand.getEnchantLevel() : "");
					ShieldPic = slotclose;
				}
			}

			if(lhand != null)
			{
				ShieldIcon = lhand.getItem().getIcon();
				ShieldName = lhand.getName() + " " + (lhand.getEnchantLevel() > 0 ? "+" + lhand.getEnchantLevel() : "");

				ShieldButton = immposible;
				ShieldPic = slotclose;
			}

			if(lfinger != null)
			{
				LRingIcon = lfinger.getItem().getIcon();
				LRingName = lfinger.getName() + " " + (lfinger.getEnchantLevel() > 0 ? "+" + lfinger.getEnchantLevel() : "");

				LRingButton = immposible;
				LRingPic = slotclose;
			}

			if(rfinger != null)
			{
				RRingIcon = rfinger.getItem().getIcon();
				RRingName = rfinger.getName() + " " + (rfinger.getEnchantLevel() > 0 ? "+" + rfinger.getEnchantLevel() : "");

				RRingButton = immposible;
				RRingPic = slotclose;
			}

			if(neck != null)
			{
				NeckIcon = neck.getItem().getIcon();
				NeckName = neck.getName() + " " + (neck.getEnchantLevel() > 0 ? "+" + neck.getEnchantLevel() : "");

				NeckButton = immposible;
				NeckPic = slotclose;
			}

			if(lear != null)
			{
				LEarIcon = lear.getItem().getIcon();
				LEarName = lear.getName() + " " + (lear.getEnchantLevel() > 0 ? "+" + lear.getEnchantLevel() : "");
				LEarButton = immposible;
				LEarPic = slotclose;
			}

			if(rear != null)
			{
				REarIcon = rear.getItem().getIcon();
				REarName = rear.getName() + " " + (rear.getEnchantLevel() > 0 ? "+" + rear.getEnchantLevel() : "");
				REarButton = immposible;
				REarPic = slotclose;
			}

			content = content.replace("<?content?>", page(1));

			content = content.replace("<?head_name?>", HeadName);
			content = content.replace("<?head_icon?>", HeadIcon);
			content = content.replace("<?head_pic?>", HeadPic);
			content = content.replace("<?head_button?>", HeadButton);

			content = content.replace("<?chest_name?>", ChestName);
			content = content.replace("<?chest_icon?>", ChestIcon);
			content = content.replace("<?chest_pic?>", ChestPic);
			content = content.replace("<?chest_button?>", ChestButton);

			content = content.replace("<?legs_name?>", LegsName);
			content = content.replace("<?legs_icon?>", LegsIcon);
			content = content.replace("<?legs_pic?>", LegsPic);
			content = content.replace("<?legs_button?>", LegsButton);

			content = content.replace("<?gloves_name?>", GlovesName);
			content = content.replace("<?gloves_icon?>", GlovesIcon);
			content = content.replace("<?gloves_pic?>", GlovesPic);
			content = content.replace("<?gloves_button?>", GlovesButton);

			content = content.replace("<?feet_name?>", FeetName);
			content = content.replace("<?feet_icon?>", FeetIcon);
			content = content.replace("<?feet_pic?>", FeetPic);
			content = content.replace("<?feet_button?>", FeetButton);

			content = content.replace("<?lear_name?>", LEarName);
			content = content.replace("<?lear_icon?>", LEarIcon);
			content = content.replace("<?lear_pic?>", LEarPic);
			content = content.replace("<?lear_button?>", LEarButton);

			content = content.replace("<?rear_name?>", REarName);
			content = content.replace("<?rear_icon?>", REarIcon);
			content = content.replace("<?rear_pic?>", REarPic);
			content = content.replace("<?rear_button?>", REarButton);

			content = content.replace("<?neck_name?>", NeckName);
			content = content.replace("<?neck_icon?>", NeckIcon);
			content = content.replace("<?neck_pic?>", NeckPic);
			content = content.replace("<?neck_button?>", NeckButton);

			content = content.replace("<?lring_name?>", LRingName);
			content = content.replace("<?lring_icon?>", LRingIcon);
			content = content.replace("<?lring_pic?>", LRingPic);
			content = content.replace("<?lring_button?>", LRingButton);

			content = content.replace("<?rring_name?>", RRingName);
			content = content.replace("<?rring_icon?>", RRingIcon);
			content = content.replace("<?rring_pic?>", RRingPic);
			content = content.replace("<?rring_button?>", RRingButton);

			content = content.replace("<?weapon_name?>", WeaponName);
			content = content.replace("<?weapon_icon?>", WeaponIcon);
			content = content.replace("<?weapon_pic?>", WeaponPic);
			content = content.replace("<?weapon_button?>", WeaponButton);

			content = content.replace("<?shield_name?>", ShieldName);
			content = content.replace("<?shield_icon?>", ShieldIcon);
			content = content.replace("<?shield_pic?>", ShieldPic);
			content = content.replace("<?shield_button?>", ShieldButton);
		}
		else if(command.startsWith("_bbsforges:attribute:item:"))
		{
			//if((Functions.script & CommunityBoardForgeAtt) != CommunityBoardForgeAtt)
			//	return;
			StringTokenizer st = new StringTokenizer(command, ":");
			st.nextToken();
			st.nextToken();
			st.nextToken();
			int item = Integer.parseInt(st.nextToken());

			if(item < 1 || item > 12)
				return;

			L2ItemInstance _item = player.getInventory().getPaperdollItem(item);
			if(_item == null)
			{
				player.sendMessage(new CustomMessage("communityboard.forge.item.null", player).toString());
				DifferentMethods.communityNextPage(player, "_bbsforges:attribute:list");
				return;
			}

			if(!itemCheckGrade(player, _item))
			{
				player.sendMessage(new CustomMessage("communityboard.forge.grade.incorrect", player).toString());
				DifferentMethods.communityNextPage(player, "_bbsforges:attribute:list");
				return;
			}

			if(_item.isHeroWeapon())
			{
				player.sendMessage(new CustomMessage("communityboard.forge.item.hero", player).toString());
				DifferentMethods.communityNextPage(player, "_bbsforges:attribute:list");
				return;
			}

			content = Files.read(ConfigValue.CommunityBoardHtmlRoot + "forge/attribute.htm", player);
			StringBuilder html = new StringBuilder("");

			html.append("<br><table border=0 cellspacing=0 cellpadding=0 width=240 height=330 background=\"l2ui_ct1.Windows_DF_TooltipBG\">");
			html.append("<tr>");
			html.append("<td width=230 align=center valign=top>");
			html.append("<table border=0 cellspacing=0 cellpadding=0 width=127 height=100 background=\"l2ui_ch3.refinegrade2_00\">");
			html.append("<tr>");
			html.append("<td width=92 height=22></td>");
			html.append("<td width=30 height=31></td>");
			html.append("<td width=100 height=22></td>");
			html.append("</tr>");
			html.append("<tr>");
			html.append("<td width=92 height=32 align=right valign=top></td>");
			html.append("<td width=32 height=32 align=center valign=top>");
			html.append("<img src=\"" + _item.getItem().getIcon() + "\" width=\"32\" height=\"32\">");
			html.append("</td>");
			html.append("<td width=110 height=32 align=left valign=top></td>");
			html.append("</tr>");
			html.append("</table>");
			html.append("<table border=0 cellspacing=0 cellpadding=0 width=230 height=20>");
			html.append("<tr>");
			html.append("<td width=230 align=center valign=top>");
			html.append("<font name=\"hs9\" color=\"LEVEL\">" + _item.getName() + "</font><font name=\"hs9\" color=\"LEVEL\">" + (_item.getEnchantLevel() <= 0 ? "</font>" : " +" + _item.getEnchantLevel()) + "</font>");
			html.append("</td>");
			html.append("</tr>");
			html.append("<tr>");
			html.append("<td width=230 align=center valign=top height=30>");
			html.append("<font name=\"hs9\">" + new CustomMessage("communityboard.forge.attribute.select", player) + "</font>");
			html.append("</td>");
			html.append("</tr>");

			String slotclose = "<img src=\"L2UI_CT1.ItemWindow_DF_SlotBox_Disable\" width=\"32\" height=\"32\">";
			String buttonFire = "<button action=\"bypass -h _bbsforges:attribute:element:0:" + item + "\" width=34 height=34 back=\"L2UI_CT1.ItemWindow_DF_Frame_Down\" fore=\"L2UI_CT1.ItemWindow_DF_Frame\"/>";
			String buttonWater = "<button action=\"bypass -h _bbsforges:attribute:element:1:" + item + "\" width=34 height=34 back=\"L2UI_CT1.ItemWindow_DF_Frame_Down\" fore=\"L2UI_CT1.ItemWindow_DF_Frame\"/>";
			String buttonWind = "<button action=\"bypass -h _bbsforges:attribute:element:2:" + item + "\" width=34 height=34 back=\"L2UI_CT1.ItemWindow_DF_Frame_Down\" fore=\"L2UI_CT1.ItemWindow_DF_Frame\"/>";
			String buttonEarth = "<button action=\"bypass -h _bbsforges:attribute:element:3:" + item + "\" width=34 height=34 back=\"L2UI_CT1.ItemWindow_DF_Frame_Down\" fore=\"L2UI_CT1.ItemWindow_DF_Frame\"/>";
			String buttonHoly = "<button action=\"bypass -h _bbsforges:attribute:element:4:" + item + "\" width=34 height=34 back=\"L2UI_CT1.ItemWindow_DF_Frame_Down\" fore=\"L2UI_CT1.ItemWindow_DF_Frame\"/>";
			String buttonUnholy = "<button action=\"bypass -h _bbsforges:attribute:element:5:" + item + "\" width=34 height=34 back=\"L2UI_CT1.ItemWindow_DF_Frame_Down\" fore=\"L2UI_CT1.ItemWindow_DF_Frame\"/>";

			if(_item.isWeapon())
			{
				switch(_item.getAttackElement())
				{
					case 0:
						buttonWater = slotclose;
						buttonWind = slotclose;
						buttonEarth = slotclose;
						buttonHoly = slotclose;
						buttonUnholy = slotclose;
						break;
					case 1:
						buttonFire = slotclose;
						buttonWind = slotclose;
						buttonEarth = slotclose;
						buttonHoly = slotclose;
						buttonUnholy = slotclose;
						break;
					case 2:
						buttonWater = slotclose;
						buttonFire = slotclose;
						buttonEarth = slotclose;
						buttonHoly = slotclose;
						buttonUnholy = slotclose;
						break;
					case 3:
						buttonWater = slotclose;
						buttonWind = slotclose;
						buttonFire = slotclose;
						buttonHoly = slotclose;
						buttonUnholy = slotclose;
						break;
					case 4:
						buttonWater = slotclose;
						buttonWind = slotclose;
						buttonEarth = slotclose;
						buttonFire = slotclose;
						buttonUnholy = slotclose;
						break;
					case 5:
						buttonWater = slotclose;
						buttonWind = slotclose;
						buttonEarth = slotclose;
						buttonHoly = slotclose;
						buttonFire = slotclose;
						break;
				}
			}

			if(_item.isArmor())
			{
				if(_item.getDefenceFire() > 0)
				{
					if(_item.getDefenceFire() >= ConfigValue.BBS_ENCHANT_ARMOR_ATTRIBUTE_MAX)
						buttonFire = slotclose;
					buttonWater = slotclose;
				}
				if(_item.getDefenceWater() > 0)
				{
					if(_item.getDefenceWater() >= ConfigValue.BBS_ENCHANT_ARMOR_ATTRIBUTE_MAX)
						buttonWater = slotclose;
					buttonFire = slotclose;
				}
				if(_item.getDefenceWind() > 0)
				{
					if(_item.getDefenceWind() >= ConfigValue.BBS_ENCHANT_ARMOR_ATTRIBUTE_MAX)
						buttonWind = slotclose;
					buttonEarth = slotclose;
				}
				if(_item.getDefenceEarth() > 0)
				{
					if(_item.getDefenceEarth() >= ConfigValue.BBS_ENCHANT_ARMOR_ATTRIBUTE_MAX)
						buttonEarth = slotclose;
					buttonWind = slotclose;
				}
				if(_item.getDefenceHoly() > 0)
				{
					if(_item.getDefenceHoly() >= ConfigValue.BBS_ENCHANT_ARMOR_ATTRIBUTE_MAX)
						buttonHoly = slotclose;
					buttonUnholy = slotclose;
				}
				if(_item.getDefenceUnholy() > 0)
				{
					if(_item.getDefenceUnholy() >= ConfigValue.BBS_ENCHANT_ARMOR_ATTRIBUTE_MAX)
						buttonUnholy = slotclose;
					buttonHoly = slotclose;
				}
			}

			html.append("<tr>");
			html.append("	<td width=250 align=center valign=top height=20>");
			html.append("<table border=0 cellspacing=0 cellpadding=0 width=30 height=20>");
			html.append("<tr>");
			html.append("<td width=32 height=45 align=center valign=top>");
			html.append("<table border=0 cellspacing=0 cellpadding=0 width=32 height=32 background=\"icon.etc_fire_crystal_i00\">");
			html.append("<tr>");
			html.append("<td width=32 align=center valign=top>");
			html.append(buttonFire);
			html.append("</td>");
			html.append("</tr>");
			html.append("</table>");
			html.append("</td>");
			html.append("<td width=32 height=10></td>");
			html.append("<td width=32 height=45 align=center valign=top>");
			html.append("<table border=0 cellspacing=0 cellpadding=0 width=32 height=32 background=\"icon.etc_water_crystal_i00\">");
			html.append("<tr>");
			html.append("<td width=32 align=center valign=top>");
			html.append(buttonWater);
			html.append("</td>");
			html.append("</tr>");
			html.append("</table>");
			html.append("</td>");
			html.append("</tr>");
			html.append("<tr>");
			html.append("<td width=32 height=45 align=center valign=top>");
			html.append("<table border=0 cellspacing=0 cellpadding=0 width=32 height=32 background=\"icon.etc_earth_crystal_i00\">");
			html.append("<tr>");
			html.append("<td width=32 align=center valign=top>");
			html.append(buttonEarth);
			html.append("</td>");
			html.append("</tr>");
			html.append("</table>");
			html.append("</td>");
			html.append("<td width=32 height=10></td>");
			html.append("<td width=32 height=45 align=center valign=top>");
			html.append("<table border=0 cellspacing=0 cellpadding=0 width=32 height=32 background=\"icon.etc_wind_crystal_i00\">");
			html.append("<tr>");
			html.append("<td width=32 align=center valign=top>");
			html.append(buttonWind);
			html.append("</td>");
			html.append("</tr>");
			html.append("</table>");
			html.append("</td>");
			html.append("</tr>");
			html.append("<tr>");
			html.append("<td width=32 height=45 align=center valign=top>");
			html.append("<table border=0 cellspacing=0 cellpadding=0 width=32 height=32 background=\"icon.etc_holy_crystal_i00\">");
			html.append("<tr>");
			html.append("<td width=32 align=center valign=top>");
			html.append(buttonHoly);
			html.append("</td>");
			html.append("</tr>");
			html.append("</table>");
			html.append("</td>");
			html.append("<td width=32 height=10></td>");
			html.append("<td width=32 height=45 align=center valign=top>");
			html.append("<table border=0 cellspacing=0 cellpadding=0 width=32 height=32 background=\"icon.etc_unholy_crystal_i00\">");
			html.append("<tr>");
			html.append("<td width=32 align=center valign=top>");
			html.append(buttonUnholy);
			html.append("</td>");
			html.append("</tr>");
			html.append("</table>");
			html.append("</td>");
			html.append("</tr>");
			html.append("</table>");
			html.append("</td>");
			html.append("</tr>");

			html.append("</table>");
			html.append("</td>");
			html.append("</tr>");
			html.append("</table>");

			content = content.replace("<?content?>", html.toString());
		}
		else if(command.startsWith("_bbsforges:attribute:element:"))
		{
			//if((Functions.script & CommunityBoardForgeAtt) != CommunityBoardForgeAtt)
			//	return;
			StringTokenizer st = new StringTokenizer(command, ":");
			st.nextToken();
			st.nextToken();
			st.nextToken();
			int element = Integer.parseInt(st.nextToken());
			String elementName = "";
			if(element == 0)
				elementName = new CustomMessage("common.element.0", player).toString();
			else if(element == 1)
				elementName = new CustomMessage("common.element.1", player).toString();
			else if(element == 2)
				elementName = new CustomMessage("common.element.2", player).toString();
			else if(element == 3)
				elementName = new CustomMessage("common.element.3", player).toString();
			else if(element == 4)
				elementName = new CustomMessage("common.element.4", player).toString();
			else if(element == 5)
				elementName = new CustomMessage("common.element.5", player).toString();

			int item = Integer.parseInt(st.nextToken());

			String name = DifferentMethods.getItemName(ConfigValue.BBS_ENCHANT_ITEM);

			if(name.isEmpty())
				name = new CustomMessage("common.item.no.name", player).toString();

			name.replace(" {PvP}", "");

			L2ItemInstance _item = player.getInventory().getPaperdollItem(item);

			if(_item == null)
			{
				player.sendMessage(new CustomMessage("communityboard.forge.item.null", player).toString());
				DifferentMethods.communityNextPage(player, "_bbsforges:attribute:list");
				return;
			}

			if(!itemCheckGrade(player, _item))
			{
				player.sendMessage(new CustomMessage("communityboard.forge.grade.incorrect", player).toString());
				DifferentMethods.communityNextPage(player, "_bbsforges:attribute:list");
				return;
			}

			if(_item.isHeroWeapon())
			{
				player.sendMessage(new CustomMessage("communityboard.forge.item.hero", player).toString());
				DifferentMethods.communityNextPage(player, "_bbsforges:attribute:list");
				return;
			}

			content = Files.read(ConfigValue.CommunityBoardHtmlRoot + "forge/attribute.htm", player);
			StringBuilder html = new StringBuilder("");

			html.append("<br><table border=0 cellspacing=0 cellpadding=0 width=240 height=330 background=\"l2ui_ct1.Windows_DF_TooltipBG\">");
			html.append("<tr>");
			html.append("<td width=230 align=center valign=top>");
			html.append("<table border=0 cellspacing=0 cellpadding=0 width=127 height=100 background=\"l2ui_ch3.refinegrade2_00\">");
			html.append("<tr>");
			html.append("<td width=92 height=22></td>");
			html.append("<td width=30 height=31></td>");
			html.append("<td width=100 height=22></td>");
			html.append("</tr>");
			html.append("<tr>");
			html.append("<td width=92 height=32 align=right valign=top></td>");
			html.append("<td width=32 height=32 align=center valign=top>");
			html.append("<img src=\"" + _item.getItem().getIcon() + "\" width=\"32\" height=\"32\">");
			html.append("</td>");
			html.append("<td width=110 height=32 align=left valign=top></td>");
			html.append("</tr>");
			html.append("</table>");
			html.append("<table border=0 cellspacing=0 cellpadding=0 width=230 height=20>");
			html.append("<tr>");
			html.append("<td width=230 align=center valign=top>");
			html.append("<font name=\"hs9\" color=\"LEVEL\">" + _item.getName() + "</font><font name=\"hs9\" color=\"LEVEL\">" + (_item.getEnchantLevel() <= 0 ? "</font>" : " +" + _item.getEnchantLevel()) + "</font>");
			html.append("</td>");
			html.append("</tr>");
			html.append("<tr>");
			html.append("<td width=230 align=center valign=top height=30>");
			html.append("<font name=\"hs9\">" + new CustomMessage("communityboard.forge.attribute.selected", player).addString(elementName) + "</font>");
			html.append("</td>");
			html.append("</tr>");

			for(int i = 0; i < (_item.isWeapon() ? ConfigValue.BBS_ENCHANT_ATRIBUTE_LVL_WEAPON.length : ConfigValue.BBS_ENCHANT_ATRIBUTE_LVL_ARMOR.length); i++)
			{
				//if(_item.getAttributeElementValue(Element.getElementById(element), false) < (_item.isWeapon() ? ConfigValue.BBS_ENCHANT_ATRIBUTE_LVL_WEAPON[i] : ConfigValue.BBS_ENCHANT_ATRIBUTE_LVL_ARMOR[i]))
				if(_item.isWeapon() ? (_item.getAttackElementValue() < ConfigValue.BBS_ENCHANT_ATRIBUTE_LVL_WEAPON[i]) : (_item.getElementDefAttr((byte)element) < ConfigValue.BBS_ENCHANT_ATRIBUTE_LVL_ARMOR[i]))
				{
					html.append("<tr>");
					html.append("<td width=230 align=center valign=top height=35>");
					html.append("<button action=\"bypass -h _bbsforges:attribute:" + i * item + ":" + item + ":" + element + "\" value=\"+" + (_item.isWeapon() ? ConfigValue.BBS_ENCHANT_ATRIBUTE_LVL_WEAPON[i] : ConfigValue.BBS_ENCHANT_ATRIBUTE_LVL_ARMOR[i]) + " (" + (_item.isWeapon() ? ConfigValue.BBS_ENCHANT_ATRIBUTE_PRICE_WEAPON[i] : ConfigValue.BBS_ENCHANT_ATRIBUTE_PRICE_ARMOR[i]) + " " + name + ")\" width=200 height=31 back=\"L2UI_CT1.OlympiadWnd_DF_Fight3None_Down\" fore=\"L2UI_CT1.OlympiadWnd_DF_Fight3None\">");
					html.append("</td>");
					html.append("</tr>");
				}
			}

			html.append("</table>");
			html.append("</td>");
			html.append("</tr>");
			html.append("</table>");

			content = content.replace("<?content?>", html.toString());
		}
		else if(command.startsWith("_bbsforges:attribute:"))
		{
			//if((Functions.script & CommunityBoardForgeAtt) != CommunityBoardForgeAtt)
			//	return;
			StringTokenizer st = new StringTokenizer(command, ":");
			st.nextToken();
			st.nextToken();
			int val = Integer.parseInt(st.nextToken());
			int item = Integer.parseInt(st.nextToken());
			int att = Integer.parseInt(st.nextToken());

			L2ItemInstance _item = player.getInventory().getPaperdollItem(item);

			if(_item == null)
			{
				player.sendMessage(new CustomMessage("communityboard.forge.item.null", player).toString());
				DifferentMethods.communityNextPage(player, "_bbsforges:attribute:list");
				return;
			}

			if(!itemCheckGrade(player, _item))
			{
				player.sendMessage(new CustomMessage("communityboard.forge.grade.incorrect", player).toString());
				DifferentMethods.communityNextPage(player, "_bbsforges:attribute:list");
				return;
			}

			if(_item.isHeroWeapon())
			{
				player.sendMessage(new CustomMessage("communityboard.forge.item.hero", player).toString());
				DifferentMethods.communityNextPage(player, "_bbsforges:attribute:list");
				return;
			}

			if(_item.isArmor() && !canEnchantArmorAttribute(att, _item))
			{
				player.sendMessage(new CustomMessage("communityboard.forge.attribute.terms.incorrect", player).toString());
				DifferentMethods.communityNextPage(player, "_bbsforges:attribute:list");
				return;
			}

			int conversion = val / item;

			int Value = _item.isWeapon() ? ConfigValue.BBS_ENCHANT_ATRIBUTE_LVL_WEAPON[conversion] : ConfigValue.BBS_ENCHANT_ATRIBUTE_LVL_ARMOR[conversion];

			if(Value > (_item.isWeapon() ? ConfigValue.BBS_ENCHANT_WEAPON_ATTRIBUTE_MAX : ConfigValue.BBS_ENCHANT_ARMOR_ATTRIBUTE_MAX))
			{
				DifferentMethods.clear(player);
				return;
			}

			int price = _item.isWeapon() ? ConfigValue.BBS_ENCHANT_ATRIBUTE_PRICE_WEAPON[conversion] : ConfigValue.BBS_ENCHANT_ATRIBUTE_PRICE_ARMOR[conversion];

			if(DifferentMethods.getPay(player, ConfigValue.BBS_ENCHANT_ITEM, price, true))
			{
				player.getInventory().unEquipItem(_item);

				if(_item.isWeapon())
					_item.setAttributeElement((byte) att, Value, new int[] { 0, 0, 0, 0, 0, 0 }, true);
				else
				{
					_item.getDeffAttr()[att] = Value;
					_item.setAttributeElement((byte) -2, 0, _item.getDeffAttr(), true);
				}
				player.getInventory().equipItem(_item, false);

				player.sendPacket(new InventoryUpdate().addModifiedItem(_item));
				player.broadcastUserInfo(true);

				String elementName = "";
				if(att == 0)
					elementName = new CustomMessage("common.element.0", player).toString();
				else if(att == 1)
					elementName = new CustomMessage("common.element.1", player).toString();
				else if(att == 2)
					elementName = new CustomMessage("common.element.2", player).toString();
				else if(att == 3)
					elementName = new CustomMessage("common.element.3", player).toString();
				else if(att == 4)
					elementName = new CustomMessage("common.element.4", player).toString();
				else if(att == 5)
					elementName = new CustomMessage("common.element.5", player).toString();

				player.sendMessage(new CustomMessage("communityboard.forge.enchant.attribute.success", player).addString(_item.getName()).addString(elementName).addNumber(Value));
				Log.add("enchant item:" + _item.getName() + " val: " + Value + " AtributType:" + att, "CommunityBoardForge", player);
			}

			DifferentMethods.communityNextPage(player, "_bbsforges:attribute:list");
			return;
		}
		else if(command.equals("_bbsforges:augment:list"))
		{
			//if((Functions.script & CommunityBoardForgeAug) != CommunityBoardForgeAug)
			//	return;
			content = Files.read(ConfigValue.CommunityBoardHtmlRoot + "forge/augmentlist.htm", player);

			String noicon = "icon.NOIMAGE";
			String slotclose = "L2UI_CT1.ItemWindow_DF_SlotBox_Disable";
			String dot = "<font color=\"FF0000\">...</font>";
			String immposible = new CustomMessage("communityboard.forge.augment.immposible", player).toString();
			String heronot = new CustomMessage("communityboard.forge.augment.heronot", player).toString();
			String picenchant = "l2ui_ch3.multisell_plusicon";
			String pvp = "icon.pvp_tab";

			String HeadButton = dot;
			String HeadIcon = noicon;
			String HeadPic = slotclose;
			String HeadName = new CustomMessage("common.item.not.clothed.head", player).toString();

			String ChestButton = dot;
			String ChestIcon = noicon;
			String ChestPic = slotclose;
			String ChestName = new CustomMessage("common.item.not.clothed.chest", player).toString();

			String LegsButton = dot;
			String LegsIcon = noicon;
			String LegsPic = slotclose;
			String LegsName = new CustomMessage("common.item.not.clothed.legs", player).toString();

			String FeetButton = dot;
			String FeetIcon = noicon;
			String FeetPic = slotclose;
			String FeetName = new CustomMessage("common.item.not.clothed.feet", player).toString();

			String GlovesButton = dot;
			String GlovesIcon = noicon;
			String GlovesPic = slotclose;
			String GlovesName = new CustomMessage("common.item.not.clothed.gloves", player).toString();

			String LEarButton = dot;
			String LEarIcon = noicon;
			String LEarPic = slotclose;
			String LEarName = new CustomMessage("common.item.not.clothed.lear", player).toString();

			String REarButton = dot;
			String REarIcon = noicon;
			String REarPic = slotclose;
			String REarName = new CustomMessage("common.item.not.clothed.rear", player).toString();

			String NeckButton = dot;
			String NeckIcon = noicon;
			String NeckPic = slotclose;
			String NeckName = new CustomMessage("common.item.not.clothed.neck", player).toString();

			String LRingButton = dot;
			String LRingIcon = noicon;
			String LRingPic = slotclose;
			String LRingName = new CustomMessage("common.item.not.clothed.lring", player).toString();

			String RRingButton = dot;
			String RRingIcon = noicon;
			String RRingPic = slotclose;
			String RRingName = new CustomMessage("common.item.not.clothed.rring", player).toString();

			String WeaponButton = dot;
			String WeaponIcon = noicon;
			String WeaponPic = slotclose;
			String WeaponName = new CustomMessage("common.item.not.clothed.weapon", player).toString();

			String ShieldButton = dot;
			String ShieldIcon = noicon;
			String ShieldPic = slotclose;
			String ShieldName = new CustomMessage("common.item.not.clothed.shield", player).toString();

			L2ItemInstance head = player.getInventory().getPaperdollItem(Inventory.PAPERDOLL_HEAD);
			L2ItemInstance chest = player.getInventory().getPaperdollItem(Inventory.PAPERDOLL_CHEST);
			L2ItemInstance legs = player.getInventory().getPaperdollItem(Inventory.PAPERDOLL_LEGS);
			L2ItemInstance gloves = player.getInventory().getPaperdollItem(Inventory.PAPERDOLL_GLOVES);
			L2ItemInstance feet = player.getInventory().getPaperdollItem(Inventory.PAPERDOLL_FEET);

			L2ItemInstance lhand = player.getInventory().getPaperdollItem(Inventory.PAPERDOLL_LHAND);
			L2ItemInstance rhand = player.getInventory().getPaperdollItem(Inventory.PAPERDOLL_RHAND);

			L2ItemInstance lfinger = player.getInventory().getPaperdollItem(Inventory.PAPERDOLL_LFINGER);
			L2ItemInstance rfinger = player.getInventory().getPaperdollItem(Inventory.PAPERDOLL_RFINGER);
			L2ItemInstance neck = player.getInventory().getPaperdollItem(Inventory.PAPERDOLL_NECK);
			L2ItemInstance lear = player.getInventory().getPaperdollItem(Inventory.PAPERDOLL_LEAR);
			L2ItemInstance rear = player.getInventory().getPaperdollItem(Inventory.PAPERDOLL_REAR);

			if(head != null)
			{
				HeadIcon = head.getItem().getIcon();
				HeadName = head.getName() + " " + (head.getEnchantLevel() > 0 ? "+" + head.getEnchantLevel() : "");

				HeadButton = immposible;
				HeadPic = slotclose;
			}

			if(chest != null)
			{
				ChestIcon = chest.getItem().getIcon();
				ChestName = chest.getName() + " " + (chest.getEnchantLevel() > 0 ? "+" + chest.getEnchantLevel() : "");

				ChestButton = immposible;
				ChestPic = slotclose;
			}

			if(legs != null)
			{
				LegsIcon = legs.getItem().getIcon();
				LegsName = legs.getName() + " " + (legs.getEnchantLevel() > 0 ? "+" + legs.getEnchantLevel() : "");

				LegsButton = immposible;
				LegsPic = slotclose;
			}

			if(gloves != null)
			{
				GlovesIcon = gloves.getItem().getIcon();
				GlovesName = gloves.getName() + " " + (gloves.getEnchantLevel() > 0 ? "+" + gloves.getEnchantLevel() : "");

				GlovesButton = immposible;
				GlovesPic = slotclose;
			}

			if(feet != null)
			{
				FeetIcon = feet.getItem().getIcon();
				FeetName = feet.getName() + " " + (feet.getEnchantLevel() > 0 ? "+" + feet.getEnchantLevel() : "");

				FeetButton = immposible;
				FeetPic = slotclose;
			}

			if(lhand != null)
			{
				ShieldIcon = lhand.getItem().getIcon();
				ShieldName = lhand.getName() + " " + (lhand.getEnchantLevel() > 0 ? "+" + lhand.getEnchantLevel() : "");

				ShieldButton = immposible;
				ShieldPic = slotclose;
			}

			if(rhand != null)
			{
				WeaponIcon = rhand.getItem().getIcon();
				WeaponName = rhand.getName() + " " + (rhand.getEnchantLevel() > 0 ? "+" + rhand.getEnchantLevel() : "");

				if(rhand.isAugmented() || !canBeAugmented(rhand, player, false))
				{
					WeaponButton = immposible;
					WeaponPic = slotclose;
				}
				else
				{
					if(itemCheckGrade(player, rhand))
					{
						if(rhand.getItem().isHeroWeapon())
						{
							WeaponButton = heronot;
							WeaponPic = slotclose;
						}
						else
						{
							WeaponButton = "<button action=\"bypass -h _bbsforges:augment:item:0:" + Inventory.PAPERDOLL_RHAND + "\" value=\"" + new CustomMessage("common.enchant.augment", player).toString() + "\" width=140 height=25 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\">";
							if(rhand.getItem().isPvP())
								WeaponPic = pvp;
							else
								WeaponPic = picenchant;
						}
					}
					else
					{
						WeaponButton = immposible;
						WeaponPic = slotclose;
					}
				}

				if(rhand.getItem().getItemType() == WeaponType.BIGBLUNT || rhand.getItem().getItemType() == WeaponType.BOW || rhand.getItem().getItemType() == WeaponType.DUALDAGGER || rhand.getItem().getItemType() == WeaponType.ANCIENTSWORD || rhand.getItem().getItemType() == WeaponType.CROSSBOW || rhand.getItem().getItemType() == WeaponType.BIGBLUNT || rhand.getItem().getItemType() == WeaponType.BIGSWORD || rhand.getItem().getItemType() == WeaponType.DUALFIST || rhand.getItem().getItemType() == WeaponType.DUAL || rhand.getItem().getItemType() == WeaponType.POLE || rhand.getItem().getItemType() == WeaponType.FIST)
				{
					ShieldButton = dot;
					ShieldIcon = rhand.getItem().getIcon();
					ShieldName = rhand.getName() + " " + (rhand.getEnchantLevel() > 0 ? "+" + rhand.getEnchantLevel() : "");
					ShieldPic = slotclose;
				}
			}

			if(lfinger != null)
			{
				LRingIcon = lfinger.getItem().getIcon();
				LRingName = lfinger.getName() + " " + (lfinger.getEnchantLevel() > 0 ? "+" + lfinger.getEnchantLevel() : "");

				if(lfinger.isAugmented() || !canBeAugmented(lfinger, player, true))
				{
					LRingButton = immposible;
					LRingPic = slotclose;
				}
				else
				{
					LRingButton = "<button action=\"bypass -h _bbsforges:augment:item:0:" + Inventory.PAPERDOLL_LFINGER + "\" value=\"" + new CustomMessage("common.enchant.augment", player).toString() + "\" width=140 height=25 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\">";
					LRingPic = picenchant;
				}
			}

			if(rfinger != null)
			{
				RRingIcon = rfinger.getItem().getIcon();
				RRingName = rfinger.getName() + " " + (rfinger.getEnchantLevel() > 0 ? "+" + rfinger.getEnchantLevel() : "");

				if(rfinger.isAugmented() || !canBeAugmented(rfinger, player, true))
				{
					RRingButton = immposible;
					RRingPic = slotclose;
				}
				else
				{
					RRingButton = "<button action=\"bypass -h _bbsforges:augment:item:0:" + Inventory.PAPERDOLL_RFINGER + "\" value=\"" + new CustomMessage("common.enchant.augment", player).toString() + "\" width=140 height=25 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\">";
					RRingPic = picenchant;
				}
			}

			if(neck != null)
			{
				NeckIcon = neck.getItem().getIcon();
				NeckName = neck.getName() + " " + (neck.getEnchantLevel() > 0 ? "+" + neck.getEnchantLevel() : "");

				if(neck.isAugmented() || !canBeAugmented(neck, player, true))
				{
					NeckButton = immposible;
					NeckPic = slotclose;
				}
				else
				{
					NeckButton = "<button action=\"bypass -h _bbsforges:augment:item:0:" + Inventory.PAPERDOLL_NECK + "\" value=\"" + new CustomMessage("common.enchant.augment", player).toString() + "\" width=140 height=25 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\">";
					NeckPic = picenchant;
				}
			}

			if(lear != null)
			{
				LEarIcon = lear.getItem().getIcon();
				LEarName = lear.getName() + " " + (lear.getEnchantLevel() > 0 ? "+" + lear.getEnchantLevel() : "");

				if(lear.isAugmented() || !canBeAugmented(lear, player, true))
				{
					LEarButton = immposible;
					LEarPic = slotclose;
				}
				else
				{
					LEarButton = "<button action=\"bypass -h _bbsforges:augment:item:0:" + Inventory.PAPERDOLL_LEAR + "\" value=\"" + new CustomMessage("common.enchant.augment", player).toString() + "\" width=140 height=25 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\">";
					LEarPic = picenchant;
				}
			}

			if(rear != null)
			{
				REarIcon = rear.getItem().getIcon();
				REarName = rear.getName() + " " + (rear.getEnchantLevel() > 0 ? "+" + rear.getEnchantLevel() : "");

				if(rear.isAugmented() || !canBeAugmented(rear, player, true))
				{
					REarButton = immposible;
					REarPic = slotclose;
				}
				else
				{
					REarButton = "<button action=\"bypass -h _bbsforges:augment:item:0:" + Inventory.PAPERDOLL_REAR + "\" value=\"" + new CustomMessage("common.enchant.augment", player).toString() + "\" width=140 height=25 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\">";
					REarPic = picenchant;
				}
			}

			content = content.replace("<?content?>", page(2));

			content = content.replace("<?head_name?>", HeadName);
			content = content.replace("<?head_icon?>", HeadIcon);
			content = content.replace("<?head_pic?>", HeadPic);
			content = content.replace("<?head_button?>", HeadButton);

			content = content.replace("<?chest_name?>", ChestName);
			content = content.replace("<?chest_icon?>", ChestIcon);
			content = content.replace("<?chest_pic?>", ChestPic);
			content = content.replace("<?chest_button?>", ChestButton);

			content = content.replace("<?legs_name?>", LegsName);
			content = content.replace("<?legs_icon?>", LegsIcon);
			content = content.replace("<?legs_pic?>", LegsPic);
			content = content.replace("<?legs_button?>", LegsButton);

			content = content.replace("<?gloves_name?>", GlovesName);
			content = content.replace("<?gloves_icon?>", GlovesIcon);
			content = content.replace("<?gloves_pic?>", GlovesPic);
			content = content.replace("<?gloves_button?>", GlovesButton);

			content = content.replace("<?feet_name?>", FeetName);
			content = content.replace("<?feet_icon?>", FeetIcon);
			content = content.replace("<?feet_pic?>", FeetPic);
			content = content.replace("<?feet_button?>", FeetButton);

			content = content.replace("<?lear_name?>", LEarName);
			content = content.replace("<?lear_icon?>", LEarIcon);
			content = content.replace("<?lear_pic?>", LEarPic);
			content = content.replace("<?lear_button?>", LEarButton);

			content = content.replace("<?rear_name?>", REarName);
			content = content.replace("<?rear_icon?>", REarIcon);
			content = content.replace("<?rear_pic?>", REarPic);
			content = content.replace("<?rear_button?>", REarButton);

			content = content.replace("<?neck_name?>", NeckName);
			content = content.replace("<?neck_icon?>", NeckIcon);
			content = content.replace("<?neck_pic?>", NeckPic);
			content = content.replace("<?neck_button?>", NeckButton);

			content = content.replace("<?lring_name?>", LRingName);
			content = content.replace("<?lring_icon?>", LRingIcon);
			content = content.replace("<?lring_pic?>", LRingPic);
			content = content.replace("<?lring_button?>", LRingButton);

			content = content.replace("<?rring_name?>", RRingName);
			content = content.replace("<?rring_icon?>", RRingIcon);
			content = content.replace("<?rring_pic?>", RRingPic);
			content = content.replace("<?rring_button?>", RRingButton);

			content = content.replace("<?weapon_name?>", WeaponName);
			content = content.replace("<?weapon_icon?>", WeaponIcon);
			content = content.replace("<?weapon_pic?>", WeaponPic);
			content = content.replace("<?weapon_button?>", WeaponButton);

			content = content.replace("<?shield_name?>", ShieldName);
			content = content.replace("<?shield_icon?>", ShieldIcon);
			content = content.replace("<?shield_pic?>", ShieldPic);
			content = content.replace("<?shield_button?>", ShieldButton);
		}
		else if(command.startsWith("_bbsforges:augment:item:"))
		{
			//if((Functions.script & CommunityBoardForgeAug) != CommunityBoardForgeAug)
			//	return;
			StringTokenizer st = new StringTokenizer(command, ":");
			st.nextToken();
			st.nextToken();
			st.nextToken();
			int pageId = Integer.parseInt(st.nextToken());
			int item = Integer.parseInt(st.nextToken());
			int type = 0;
			if(st.hasMoreTokens())
				type = Integer.parseInt(st.nextToken());

			if(item < 1 || item > 15)
				return;

			L2ItemInstance _item = player.getInventory().getPaperdollItem(item);
			if(_item == null || _item.isHeroWeapon() || _item.isRaidAccessory())
			{
				player.sendMessage(new CustomMessage("communityboard.forge.item.null", player).toString());
				DifferentMethods.communityNextPage(player, "_bbsforges:augment:list");
				return;
			}

			if(_item.getItem().isArrow())
			{
				player.sendMessage(new CustomMessage("communityboard.forge.item.arrow", player));
				DifferentMethods.communityNextPage(player, "_bbsforges:augment:list");
				return;
			}

			content = Files.read(ConfigValue.CommunityBoardHtmlRoot + "forge/augment.htm", player);
			StringBuilder html = new StringBuilder("");

			html.append("<br><table border=0 cellspacing=0 cellpadding=0 width=730 height=338 background=\"l2ui_ct1.Windows_DF_TooltipBG\">");
			html.append("<tr>");
			html.append("<td width=230 align=center valign=top>");

			html.append("<table border=0 cellspacing=0 cellpadding=0 width=127 height=100 background=\"l2ui_ch3.refinegrade2_00\">");
			html.append("<tr>");
			html.append("<td width=92 height=1></td>");
			html.append("<td width=30 height=31></td>");
			html.append("<td width=100 height=22></td>");
			html.append("</tr>");
			html.append("<tr>");
			html.append("<td width=92 height=10 align=right valign=top></td>");
			html.append("<td width=32 height=32 align=center valign=top>");
			html.append("<img src=\"" + _item.getItem().getIcon() + "\" width=\"32\" height=\"32\">");
			html.append("</td>");
			html.append("<td width=110 height=32 align=left valign=top></td>");
			html.append("</tr>");
			html.append("</table>");

			html.append("<table border=0 cellspacing=0 cellpadding=0 width=229 height=20>");
			html.append("<tr>");
			html.append("<td width=330 align=center valign=top>");
			html.append("<font name=\"hs9\" color=\"LEVEL\">" + _item.getName() + "</font><font name=\"hs9\" color=\"LEVEL\">" + (_item.getEnchantLevel() <= 0 ? "</font>" : " +" + _item.getEnchantLevel()) + "</font>");
			html.append("</td>");
			html.append("</tr>");
			html.append("<tr>");
			html.append("<td width=330 align=center valign=top height=20>");
			html.append("<font name=\"hs9\">" + new CustomMessage("communityboard.forge.augment.select", player) + "</font>");
			html.append("</td>");
			html.append("</tr>");
			html.append("</table>");

			html.append("<table border=0 cellspacing=2 cellpadding=4 width=600 height=48>");
			int[] option_id = _item.getItem().isWeapon() ? ConfigValue.WeaponAugmentOptionList : ConfigValue.JewelAugmentOptionList;

			int a = 0, b = 1;
			String[] color = new String[] { "666666", "333333" };

			int size = option_id.length;
			// String option_name = "Предметное умение: Увеличивает шанс выпадения пред\\nметов на 10%."; TODO

			int MaxCharactersPerPage = 12;
			int MaxPages = size / MaxCharactersPerPage;
			if(size > MaxCharactersPerPage * MaxPages)
				MaxPages++;
			if(pageId > MaxPages)
				pageId = MaxPages;
			int CharactersStart = MaxCharactersPerPage * pageId;
			int CharactersEnd = size;
			if(CharactersEnd - CharactersStart > MaxCharactersPerPage)
				CharactersEnd = CharactersStart + MaxCharactersPerPage;

			int[] WeaponAugmentOptionItemId;
			int[] JewelAugmentOptionItemId;

			long[] WeaponAugmentOptionCount;
			long[] JewelAugmentOptionCount;

			switch(type)
			{
				case 1:
					WeaponAugmentOptionItemId = ConfigValue.WeaponAugmentOptionItemId1;
					JewelAugmentOptionItemId = ConfigValue.JewelAugmentOptionItemId1;

					WeaponAugmentOptionCount = ConfigValue.WeaponAugmentOptionCount1;
					JewelAugmentOptionCount = ConfigValue.JewelAugmentOptionCount1;
					break;
				case 2:
					WeaponAugmentOptionItemId = ConfigValue.WeaponAugmentOptionItemId2;
					JewelAugmentOptionItemId = ConfigValue.JewelAugmentOptionItemId2;

					WeaponAugmentOptionCount = ConfigValue.WeaponAugmentOptionCount2;
					JewelAugmentOptionCount = ConfigValue.JewelAugmentOptionCount2;
					break;
				default:
					WeaponAugmentOptionItemId = ConfigValue.WeaponAugmentOptionItemId;
					JewelAugmentOptionItemId = ConfigValue.JewelAugmentOptionItemId;

					WeaponAugmentOptionCount = ConfigValue.WeaponAugmentOptionCount;
					JewelAugmentOptionCount = ConfigValue.JewelAugmentOptionCount;
					break;
			}
			
			
			for(int i = CharactersStart; i < CharactersEnd; i += 3)
			{
				String name = DifferentMethods.getItemName(_item.getItem().isWeapon() ? WeaponAugmentOptionItemId[i] : JewelAugmentOptionItemId[i]);
				if(name.isEmpty())
					name = new CustomMessage("common.item.no.name", player).toString();

				L2Skill skill = null;
				OptionDataTemplate template = XmlOptionDataLoader.getInstance().getTemplate(option_id[i]);
				if(template != null)
				{
					if(template.getSkills().size() > 0)
						skill = template.getSkills().get(0);
					if(skill == null && template.getTriggerList().size() > 0)
						skill = template.getTriggerList().get(0).getSkill();
				}
				/*if(skill == null)
				{
					DifferentMethods.communityNextPage(player, "_bbsforges:augment:list");
					return;
				}*/
				html.append("<tr>");
				//clan_DF_clanwaricon_redshield
				//ICON_DF_Exclamation

				// ------------------------------------------------------------
				html.append("<td FIXWIDTH=210 align=center valign=top>");
				html.append("<table border=0 cellspacing=2 cellpadding=4 width=218 height=40 bgcolor=" + color[a] + ">");
				html.append("<tr>");
				html.append("<td FIXWIDTH=40 align=right valign=top>");
				html.append("<img src=\"" + (skill == null ? "icon.NOIMAGE" : skill.getIcon()) + "\" width=32 height=32>");
				html.append("</td>");
				html.append("<td FIXWIDTH=200 align=left valign=top>");
				html.append("<font color=\"LEVEL\"><a action=\"bypass -h _bbsforges:augment:" + option_id[i] + ":" + item + ":" + (_item.getItem().isWeapon() ? WeaponAugmentOptionItemId[i] : JewelAugmentOptionItemId[i]) + ":" + (_item.getItem().isWeapon() ? WeaponAugmentOptionCount[i] : JewelAugmentOptionCount[i]) + "\">" + (skill == null ? AugmentName.getString(option_id[i]) : skill.getName()) + "</a></font><br1>› <font color=8DB600>Цена:</font> " + (_item.getItem().isWeapon() ? WeaponAugmentOptionCount[i] : JewelAugmentOptionCount[i]) + " " + name + "");
				html.append("</td>");
				html.append("</tr>");
				html.append("</table>");
				html.append("</td>");

				if(size > (i + 1))
				{
					skill = null;
					name = DifferentMethods.getItemName(_item.getItem().isWeapon() ? WeaponAugmentOptionItemId[i + 1] : JewelAugmentOptionItemId[i + 1]);
					if(name.isEmpty())
						name = new CustomMessage("common.item.no.name", player).toString();

					template = XmlOptionDataLoader.getInstance().getTemplate(option_id[i + 1]);
					if(template != null)
					{
						if(template.getSkills().size() > 0)
							skill = template.getSkills().get(0);
						if(skill == null && template.getTriggerList().size() > 0)
							skill = template.getTriggerList().get(0).getSkill();
					}
					html.append("<td FIXWIDTH=210 align=center valign=top>");
					html.append("<table border=0 cellspacing=2 cellpadding=4 width=218 height=40 bgcolor=" + color[b] + ">");
					html.append("<tr>");
					html.append("<td FIXWIDTH=40 align=right valign=top>");
					html.append("<img src=\"" + (skill == null ? "icon.NOIMAGE" : skill.getIcon()) + "\" width=32 height=32>");
					html.append("</td>");
					html.append("<td FIXWIDTH=200 align=left valign=top>");
					html.append("<font color=\"LEVEL\"><a action=\"bypass -h _bbsforges:augment:" + option_id[i + 1] + ":" + item + ":" + (_item.getItem().isWeapon() ? WeaponAugmentOptionItemId[i + 1] : JewelAugmentOptionItemId[i + 1]) + ":" + (_item.getItem().isWeapon() ? WeaponAugmentOptionCount[i + 1] : JewelAugmentOptionCount[i + 1]) + "\">" + (skill == null ? AugmentName.getString(option_id[i + 1]) : skill.getName()) + "</a></font><br1>› <font color=8DB600>Цена:</font> " + (_item.getItem().isWeapon() ? WeaponAugmentOptionCount[i + 1] : JewelAugmentOptionCount[i + 1]) + " " + name + "");
					html.append("</td>");
					html.append("</tr>");
					html.append("</table>");
					html.append("</td>");
				}

				if(size > (i + 2))
				{
					skill = null;
					name = DifferentMethods.getItemName(_item.getItem().isWeapon() ? WeaponAugmentOptionItemId[i + 2] : JewelAugmentOptionItemId[i + 2]);
					if(name.isEmpty())
						name = new CustomMessage("common.item.no.name", player).toString();

					template = XmlOptionDataLoader.getInstance().getTemplate(option_id[i + 2]);
					if(template != null)
					{
						if(template.getSkills().size() > 0)
							skill = template.getSkills().get(0);
						if(skill == null && template.getTriggerList().size() > 0)
							skill = template.getTriggerList().get(0).getSkill();
					}

					html.append("<td FIXWIDTH=210 align=center valign=top>");
					html.append("<table border=0 cellspacing=2 cellpadding=4 width=218 height=40 bgcolor=" + color[a] + ">");
					html.append("<tr>");
					html.append("<td FIXWIDTH=40 align=right valign=top>");
					html.append("<img src=\"" + (skill == null ? "icon.NOIMAGE" : skill.getIcon()) + "\" width=32 height=32>");
					html.append("</td>");
					html.append("<td FIXWIDTH=200 align=left valign=top>");
					html.append("<font color=\"LEVEL\"><a action=\"bypass -h _bbsforges:augment:" + option_id[i + 2] + ":" + item + ":" + (_item.getItem().isWeapon() ? WeaponAugmentOptionItemId[i + 2] : JewelAugmentOptionItemId[i + 2]) + ":" + (_item.getItem().isWeapon() ? WeaponAugmentOptionCount[i + 2] : JewelAugmentOptionCount[i + 2]) + "\">" + (skill == null ? AugmentName.getString(option_id[i + 2]) : skill.getName()) + "</a></font><br1>› <font color=8DB600>Цена:</font> " + (_item.getItem().isWeapon() ? WeaponAugmentOptionCount[i + 2] : JewelAugmentOptionCount[i + 2]) + " " + name + "");
					html.append("</td>");
					html.append("</tr>");
					html.append("</table>");
					html.append("</td>");
				}
				// ------------------------------------------------------------
				html.append("</tr>");
				if(a == 0)
				{
					a++;
					b = 0;
				}
				else
				{
					a = 0;
					b++;
				}
			}
			html.append("</table><br><br><br>");

			html.append("</td>");
			html.append("</tr>");
			html.append("</table>");

			html.append("<center><table border=0 cellspacing=0 cellpadding=0>");
			html.append("<tr>");

			// Список страниц...
			for(int x = 0; x < MaxPages; x++)
				if(MaxPages > 1)
				{
					html.append("<td width=35 height=35 align=center valign=top>");
					html.append("<button action=\"bypass -h _bbsforges:augment:item:" + x + ":" + item + ":" + type + "\" value=\"" + (x + 1) + "\" width=30 height=25 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\">");
					html.append("</td>");
				}
			html.append("</tr>");
			html.append("</table></center>");

			content = content.replace("<?content2?>", pageId + ":" + item);
			content = content.replace("<?content?>", html.toString());
		}
		else if(command.startsWith("_bbsforges:augment:"))
		{
			StringTokenizer st = new StringTokenizer(command, ":");
			st.nextToken();
			st.nextToken();
			int stat34 = Integer.parseInt(st.nextToken());
			int item = Integer.parseInt(st.nextToken());
			int consume_id = Integer.parseInt(st.nextToken());
			long consume_count = Long.parseLong(st.nextToken());

			
				
			L2ItemInstance _item = player.getInventory().getPaperdollItem(item);
			if(_item == null)
			{
				player.sendMessage(new CustomMessage("communityboard.forge.item.null", player).toString());
				DifferentMethods.communityNextPage(player, "_bbsforges:augment:list");
				return;
			}
			else if(_item.getItem().isHeroWeapon() || _item.isRaidAccessory())
			{
				DifferentMethods.communityNextPage(player, "_bbsforges:augment:list");
				return;
			}
			else if(DifferentMethods.getPay(player, consume_id, consume_count, true))
			{
				L2Skill skill = null;

				OptionDataTemplate template = XmlOptionDataLoader.getInstance().getTemplate(stat34);
				if(template != null)
				{
					if(template.getSkills().size() > 0)
						skill = template.getSkills().get(0);
					if(skill == null && template.getTriggerList().size() > 0)
						skill = template.getTriggerList().get(0).getSkill();
				}

				int stat16 = (_item.isWeapon() ? ConfigValue.WeaponAugmentOptionStat : ConfigValue.JewelAugmentOptionStat);
				_item.setAugmentation(new L2Augmentation(((stat34 << 16) + stat16), skill));
				if(_item.isEquipped())
					_item.getAugmentation().applyBoni(player, true);

				player.updateStats();

				player.sendPacket(new InventoryUpdate().addModifiedItem(_item));
				player.sendUserInfo(false);
			}
			DifferentMethods.communityNextPage(player, "_bbsforges:augment:list");
			return;
		}
		else
			separateAndSend(DifferentMethods.getErrorHtml(player, command), player);

		ShowBoard.separateAndSend(content, player);
	}

	private String page(int type)
	{
		if(ConfigValue.AugmentOldVer && type == 2)
			return page2();
		StringBuilder html = new StringBuilder("");

		html.append("<center><table border=0 cellpadding=3 cellspacing=3 width=500 height=425><tr><td><center><table><tr><td height=62><table border=0 cellspacing=1 cellpadding=1 height=55 width=220 background=\"l2ui_ct1.EditBox_DF_bg\"><tr><td width=50 align=left valign=top><table border=0 cellspacing=1 cellpadding=1 width=50 height=50 background=\"l2ui_ct1.Windows_DF_TooltipBG\"><tr><td width=70 height=9></td></tr><tr><td width=70 align=center valign=top><table border=0 cellspacing=0 cellpadding=0 width=30 height=32 background=\"<?head_icon?>\"><tr><td width=32 align=center valign=top><img src=\"<?head_pic?>\" width=\"32\" height=\"32\"></td></tr></table></td></tr></table></td><td width=220 align=center valign=top><table border=0 cellspacing=1 cellpadding=1 width=220 height=50><tr><td width=220 align=center valign=top><font name=\"hs9\" color=\"LEVEL\"><?head_name?></font></td></tr><tr><td width=220 align=center valign=top><?head_button?></td></tr></table></td></tr></table></td><td height=62><table border=0 cellspacing=1 cellpadding=1 height=55 width=220 background=\"l2ui_ct1.EditBox_DF_bg\"><tr><td width=50 align=left valign=top><table border=0 cellspacing=1 cellpadding=1 width=50 height=50 background=\"l2ui_ct1.Windows_DF_TooltipBG\"><tr><td width=70 height=9></td></tr><tr><td width=70 align=center valign=top><table border=0 cellspacing=0 cellpadding=0 width=30 height=32 background=\"<?lear_icon?>\"><tr><td width=32 align=center valign=top><img src=\"<?lear_pic?>\" width=\"32\" height=\"32\"></td></tr></table></td> </tr></table></td><td width=220 align=center valign=top><table border=0 cellspacing=1 cellpadding=1 width=220 height=50><tr><td width=220 align=center valign=top><font name=\"hs9\" color=\"LEVEL\"><?lear_name?></font></td></tr><tr><td width=220 align=center valign=top><?lear_button?></td></tr></table></td></tr></table></td></tr><tr><td height=62><table border=0 cellspacing=1 cellpadding=1 height=55 width=220 background=\"l2ui_ct1.EditBox_DF_bg\"><tr><td width=50 align=left valign=top><table border=0 cellspacing=1 cellpadding=1 width=50 height=50 background=\"l2ui_ct1.Windows_DF_TooltipBG\"><tr><td width=70 height=9></td></tr><tr><td width=70 align=center valign=top><table border=0 cellspacing=0 cellpadding=0 width=30 height=32 background=\"<?chest_icon?>\"><tr><td width=32 align=center valign=top><img src=\"<?chest_pic?>\" width=\"32\" height=\"32\"></td></tr></table></td></tr></table></td><td width=220 align=center valign=top><table border=0 cellspacing=1 cellpadding=1 width=220 height=50><tr><td width=220 align=center valign=top><font name=\"hs9\" color=\"LEVEL\"><?chest_name?></font></td></tr><tr><td width=220 align=center valign=top><?chest_button?></td></tr></table></td></tr></table></td><td height=62><table border=0 cellspacing=1 cellpadding=1 height=55 width=220 background=\"l2ui_ct1.EditBox_DF_bg\"><tr><td width=50 align=left valign=top><table border=0 cellspacing=1 cellpadding=1 width=50 height=50 background=\"l2ui_ct1.Windows_DF_TooltipBG\"><tr><td width=70 height=9></td></tr><tr><td width=70 align=center valign=top><table border=0 cellspacing=0 cellpadding=0 width=30 height=32 background=\"<?rear_icon?>\"><tr><td width=32 align=center valign=top><img src=\"<?rear_pic?>\" width=\"32\" height=\"32\"></td></tr></table></td></tr></table></td><td width=220 align=center valign=top><table border=0 cellspacing=1 cellpadding=1 width=220 height=50><tr><td width=220 align=center valign=top><font name=\"hs9\" color=\"LEVEL\"><?rear_name?></font></td></tr><tr><td width=220 align=center valign=top><?rear_button?></td></tr></table></td></tr></table></td></tr><tr><td height=62><table border=0 cellspacing=1 cellpadding=1 height=55 width=220 background=\"l2ui_ct1.EditBox_DF_bg\"><tr><td width=50 align=left valign=top><table border=0 cellspacing=1 cellpadding=1 width=50 height=50 background=\"l2ui_ct1.Windows_DF_TooltipBG\"><tr><td width=70 height=9></td></tr><tr><td width=70 align=center valign=top><table border=0 cellspacing=0 cellpadding=0 width=30 height=32 background=\"<?legs_icon?>\"><tr><td width=32 align=center valign=top><img src=\"<?legs_pic?>\" width=\"32\" height=\"32\"></td></tr></table></td></tr></table></td><td width=220 align=center valign=top><table border=0 cellspacing=1 cellpadding=1 width=220 height=50><tr><td width=220 align=center valign=top><font name=\"hs9\" color=\"LEVEL\"><?legs_name?></font></td></tr><tr><td width=220 align=center valign=top><?legs_button?></td></tr></table></td></tr></table></td><td height=62><table border=0 cellspacing=1 cellpadding=1 height=55 width=220 background=\"l2ui_ct1.EditBox_DF_bg\"><tr><td width=50 align=left valign=top><table border=0 cellspacing=1 cellpadding=1 width=50 height=50 background=\"l2ui_ct1.Windows_DF_TooltipBG\"><tr><td width=70 height=9></td></tr><tr><td width=70 align=center valign=top><table border=0 cellspacing=0 cellpadding=0 width=30 height=32 background=\"<?neck_icon?>\"><tr><td width=32 align=center valign=top><img src=\"<?neck_pic?>\" width=\"32\" height=\"32\"></td></tr></table></td></tr></table></td><td width=220 align=center valign=top><table border=0 cellspacing=1 cellpadding=1 width=220 height=50><tr><td width=220 align=center valign=top><font name=\"hs9\" color=\"LEVEL\"><?neck_name?></font></td></tr><tr><td width=220 align=center valign=top><?neck_button?></td></tr></table></td></tr></table></td></tr><tr><td height=62><table border=0 cellspacing=1 cellpadding=1 height=55 width=220 background=\"l2ui_ct1.EditBox_DF_bg\"><tr><td width=50 align=left valign=top><table border=0 cellspacing=1 cellpadding=1 width=50 height=50 background=\"l2ui_ct1.Windows_DF_TooltipBG\"><tr><td width=70 height=9></td></tr><tr><td width=70 align=center valign=top><table border=0 cellspacing=0 cellpadding=0 width=30 height=32 background=\"<?gloves_icon?>\"><tr><td width=32 align=center valign=top><img src=\"<?gloves_pic?>\" width=\"32\" height=\"32\"></td></tr></table></td></tr></table></td><td width=220 align=center valign=top><table border=0 cellspacing=1 cellpadding=1 width=220 height=50><tr><td width=220 align=center valign=top><font name=\"hs9\" color=\"LEVEL\"><?gloves_name?></font></td></tr><tr><td width=220 align=center valign=top><?gloves_button?></td></tr></table></td></tr></table></td><td height=62><table border=0 cellspacing=1 cellpadding=1 height=55 width=220 background=\"l2ui_ct1.EditBox_DF_bg\"><tr><td width=50 align=left valign=top><table border=0 cellspacing=1 cellpadding=1 width=50 height=50 background=\"l2ui_ct1.Windows_DF_TooltipBG\"><tr><td width=70 height=9></td></tr><tr><td width=70 align=center valign=top><table border=0 cellspacing=0 cellpadding=0 width=30 height=32 background=\"<?lring_icon?>\"><tr><td width=32 align=center valign=top><img src=\"<?lring_pic?>\" width=\"32\" height=\"32\"></td></tr></table></td></tr></table></td><td width=220 align=center valign=top><table border=0 cellspacing=1 cellpadding=1 width=220 height=50><tr><td width=220 align=center valign=top><font name=\"hs9\" color=\"LEVEL\"><?lring_name?></font></td></tr><tr><td width=220 align=center valign=top><?lring_button?></td></tr></table></td></tr></table></td></tr><tr><td height=62><table border=0 cellspacing=1 cellpadding=1 height=55 width=220 background=\"l2ui_ct1.EditBox_DF_bg\"><tr><td width=50 align=left valign=top><table border=0 cellspacing=1 cellpadding=1 width=50 height=50 background=\"l2ui_ct1.Windows_DF_TooltipBG\"><tr><td width=70 height=9></td></tr><tr><td width=70 align=center valign=top><table border=0 cellspacing=0 cellpadding=0 width=30 height=32 background=\"<?feet_icon?>\"><tr><td width=32 align=center valign=top><img src=\"<?feet_pic?>\" width=\"32\" height=\"32\"></td></tr></table></td></tr></table></td><td width=220 align=center valign=top><table border=0 cellspacing=1 cellpadding=1 width=220 height=50><tr><td width=220 align=center valign=top><font name=\"hs9\" color=\"LEVEL\"><?feet_name?></font></td></tr><tr><td width=220 align=center valign=top><?feet_button?></td></tr></table></td></tr></table></td><td height=62><table border=0 cellspacing=1 cellpadding=1 height=55 width=220 background=\"l2ui_ct1.EditBox_DF_bg\"><tr><td width=50 align=left valign=top><table border=0 cellspacing=1 cellpadding=1 width=50 height=50 background=\"l2ui_ct1.Windows_DF_TooltipBG\"><tr><td width=70 height=9></td></tr><tr><td width=70 align=center valign=top><table border=0 cellspacing=0 cellpadding=0 width=30 height=32 background=\"<?rring_icon?>\"><tr><td width=32 align=center valign=top><img src=\"<?rring_pic?>\" width=\"32\" height=\"32\"></td></tr></table></td></tr></table></td><td width=220 align=center valign=top><table border=0 cellspacing=1 cellpadding=1 width=220 height=50><tr><td width=220 align=center valign=top><font name=\"hs9\" color=\"LEVEL\"><?rring_name?></font></td></tr><tr><td width=220 align=center valign=top><?rring_button?></td></tr></table></td></tr></table></td></tr><tr><td height=62><table border=0 cellspacing=1 cellpadding=1 height=55 width=220 background=\"l2ui_ct1.EditBox_DF_bg\"><tr><td width=50 align=left valign=top><table border=0 cellspacing=1 cellpadding=1 width=50 height=50 background=\"l2ui_ct1.Windows_DF_TooltipBG\"><tr><td width=70 height=9></td></tr><tr><td width=70 align=center valign=top><table border=0 cellspacing=0 cellpadding=0 width=30 height=32 background=\"<?weapon_icon?>\"><tr><td width=32 align=center valign=top><img src=\"<?weapon_pic?>\" width=\"32\" height=\"32\"></td></tr></table></td></tr></table></td><td width=220 align=center valign=top><table border=0 cellspacing=1 cellpadding=1 width=220 height=50><tr><td width=220 align=center valign=top><font name=\"hs9\" color=\"LEVEL\"><?weapon_name?></font></td></tr><tr><td width=220 align=center valign=top><?weapon_button?></td></tr></table></td></tr></table></td><td height=62><table border=0 cellspacing=1 cellpadding=1 height=55 width=220 background=\"l2ui_ct1.EditBox_DF_bg\"><tr><td width=50 align=left valign=top><table border=0 cellspacing=1 cellpadding=1 width=50 height=50 background=\"l2ui_ct1.Windows_DF_TooltipBG\"><tr><td width=70 height=9></td></tr><tr><td width=70 align=center valign=top><table border=0 cellspacing=0 cellpadding=0 width=30 height=32 background=\"<?shield_icon?>\"><tr><td width=32 align=center valign=top><img src=\"<?shield_pic?>\" width=\"32\" height=\"32\"></td></tr></table></td></tr></table></td><td width=220 align=center valign=top><table border=0 cellspacing=1 cellpadding=1 width=220 height=50><tr><td width=220 align=center valign=top><font name=\"hs9\" color=\"LEVEL\"><?shield_name?></font></td></tr><tr><td width=220 align=center valign=top><?shield_button?></td></tr></table></td></tr></table></td></tr></table></center></td></tr></table></center>");

		return html.toString();
	}

	private String page2()
	{
		StringBuilder html = new StringBuilder("");

		html.append("<center><table border=0 cellpadding=3 cellspacing=3 width=500 height=425>");
		html.append("<tr><td><center><table>");
		html.append("<tr><td height=62><table border=0 cellspacing=1 cellpadding=1 height=55 width=220 background=\"l2ui_ct1.EditBox_DF_bg\">");
		html.append("<tr><td width=50 align=left valign=top><table border=0 cellspacing=1 cellpadding=1 width=50 height=50 background=\"l2ui_ct1.Windows_DF_TooltipBG\">");
		html.append("<tr><td width=70 height=9></td></tr><tr><td width=70 align=center valign=top>");
		html.append("<table border=0 cellspacing=0 cellpadding=0 width=30 height=32 background=\"<?weapon_icon?>\">");
		html.append("<tr><td width=32 align=center valign=top><img src=\"<?weapon_pic?>\" width=\"32\" height=\"32\"></td></tr></table></td></tr></table></td>");
		html.append("<td width=220 align=center valign=top><table border=0 cellspacing=1 cellpadding=1 width=220 height=50><tr><td width=220 align=center valign=top><font name=\"hs9\" color=\"LEVEL\"><?weapon_name?></font></td></tr>");
		html.append("<tr><td width=220 align=center valign=top><?weapon_button?></td></tr></table></td></tr></table></td>");
		//-
		html.append("<td height=62><table border=0 cellspacing=1 cellpadding=1 height=55 width=220 background=\"l2ui_ct1.EditBox_DF_bg\">");
		html.append("<tr><td width=50 align=left valign=top><table border=0 cellspacing=1 cellpadding=1 width=50 height=50 background=\"l2ui_ct1.Windows_DF_TooltipBG\">");
		html.append("<tr><td width=70 height=9></td></tr>");
		html.append("<tr><td width=70 align=center valign=top><table border=0 cellspacing=0 cellpadding=0 width=30 height=32 background=\"<?neck_icon?>\">");
		html.append("<tr><td width=32 align=center valign=top><img src=\"<?neck_pic?>\" width=\"32\" height=\"32\"></td></tr></table></td> </tr></table></td><td width=220 align=center valign=top><table border=0 cellspacing=1 cellpadding=1 width=220 height=50>");
		html.append("<tr><td width=220 align=center valign=top><font name=\"hs9\" color=\"LEVEL\"><?neck_name?></font></td></tr>");
		html.append("<tr><td width=220 align=center valign=top><?neck_button?></td></tr></table></td></tr></table></td></tr>");


		html.append("<tr><td height=62><table border=0 cellspacing=1 cellpadding=1 height=55 width=220 background=\"l2ui_ct1.EditBox_DF_bg\">");
		html.append("<tr><td width=50 align=left valign=top><table border=0 cellspacing=1 cellpadding=1 width=50 height=50 background=\"l2ui_ct1.Windows_DF_TooltipBG\">");
		html.append("<tr><td width=70 height=9></td></tr>");
		html.append("<tr><td width=70 align=center valign=top><table border=0 cellspacing=0 cellpadding=0 width=30 height=32 background=\"<?lring_icon?>\">");
		html.append("<tr><td width=32 align=center valign=top><img src=\"<?lring_pic?>\" width=\"32\" height=\"32\"></td></tr></table></td></tr></table></td><td width=220 align=center valign=top><table border=0 cellspacing=1 cellpadding=1 width=220 height=50>");
		html.append("<tr><td width=220 align=center valign=top><font name=\"hs9\" color=\"LEVEL\"><?lring_name?></font></td></tr>");
		html.append("<tr><td width=220 align=center valign=top><?lring_button?></td></tr></table></td></tr></table></td>");
		//-
		html.append("<td height=62><table border=0 cellspacing=1 cellpadding=1 height=55 width=220 background=\"l2ui_ct1.EditBox_DF_bg\">");
		html.append("<tr><td width=50 align=left valign=top><table border=0 cellspacing=1 cellpadding=1 width=50 height=50 background=\"l2ui_ct1.Windows_DF_TooltipBG\">");
		html.append("<tr><td width=70 height=9></td></tr>");
		html.append("<tr><td width=70 align=center valign=top><table border=0 cellspacing=0 cellpadding=0 width=30 height=32 background=\"<?rring_icon?>\">");
		html.append("<tr><td width=32 align=center valign=top><img src=\"<?rring_pic?>\" width=\"32\" height=\"32\"></td></tr></table></td></tr></table></td><td width=220 align=center valign=top><table border=0 cellspacing=1 cellpadding=1 width=220 height=50>");
		html.append("<tr><td width=220 align=center valign=top><font name=\"hs9\" color=\"LEVEL\"><?rring_name?></font></td></tr>");
		html.append("<tr><td width=220 align=center valign=top><?rring_button?></td></tr></table></td></tr></table></td></tr>");


		html.append("<tr><td height=62><table border=0 cellspacing=1 cellpadding=1 height=55 width=220 background=\"l2ui_ct1.EditBox_DF_bg\">");
		html.append("<tr><td width=50 align=left valign=top><table border=0 cellspacing=1 cellpadding=1 width=50 height=50 background=\"l2ui_ct1.Windows_DF_TooltipBG\">");
		html.append("<tr><td width=70 height=9></td></tr>");
		html.append("<tr><td width=70 align=center valign=top><table border=0 cellspacing=0 cellpadding=0 width=30 height=32 background=\"<?lear_icon?>\">");
		html.append("<tr><td width=32 align=center valign=top><img src=\"<?lear_pic?>\" width=\"32\" height=\"32\"></td></tr></table></td></tr></table></td><td width=220 align=center valign=top><table border=0 cellspacing=1 cellpadding=1 width=220 height=50>");
		html.append("<tr><td width=220 align=center valign=top><font name=\"hs9\" color=\"LEVEL\"><?lear_name?></font></td></tr>");
		html.append("<tr><td width=220 align=center valign=top><?lear_button?></td></tr></table></td></tr></table></td>");
		//-
		html.append("<td height=62><table border=0 cellspacing=1 cellpadding=1 height=55 width=220 background=\"l2ui_ct1.EditBox_DF_bg\">");
		html.append("<tr><td width=50 align=left valign=top><table border=0 cellspacing=1 cellpadding=1 width=50 height=50 background=\"l2ui_ct1.Windows_DF_TooltipBG\">");
		html.append("<tr><td width=70 height=9></td></tr>");
		html.append("<tr><td width=70 align=center valign=top><table border=0 cellspacing=0 cellpadding=0 width=30 height=32 background=\"<?rear_icon?>\">");
		html.append("<tr><td width=32 align=center valign=top><img src=\"<?rear_pic?>\" width=\"32\" height=\"32\"></td></tr></table></td></tr></table></td><td width=220 align=center valign=top><table border=0 cellspacing=1 cellpadding=1 width=220 height=50>");
		html.append("<tr><td width=220 align=center valign=top><font name=\"hs9\" color=\"LEVEL\"><?rear_name?></font></td></tr>");
		html.append("<tr><td width=220 align=center valign=top><?rear_button?></td></tr></table></td></tr></table></td></tr></table></center></td></tr></table></center>");
		return html.toString();
	}

	private boolean itemCheckGrade(L2Player player, L2ItemInstance item)
	{
		Grade grade = item.getCrystalType();

		switch(grade)
		{
			case NONE:
				return ConfigValue.BBS_ENCHANT_GRADE_ATTRIBUTE[0].equals("NG:PA") ? (player.hasBonus() ? true : false) : (ConfigValue.BBS_ENCHANT_GRADE_ATTRIBUTE[0].equals("NG:ON") ? true : ConfigValue.BBS_ENCHANT_GRADE_ATTRIBUTE[0].equals("NG:NO") ? false : true);
			case D:
				return ConfigValue.BBS_ENCHANT_GRADE_ATTRIBUTE[1].equals("D:PA") ? (player.hasBonus() ? true : false) : (ConfigValue.BBS_ENCHANT_GRADE_ATTRIBUTE[1].equals("D:ON") ? true : ConfigValue.BBS_ENCHANT_GRADE_ATTRIBUTE[1].equals("D:NO") ? false : true);
			case C:
				return ConfigValue.BBS_ENCHANT_GRADE_ATTRIBUTE[2].equals("C:PA") ? (player.hasBonus() ? true : false) : (ConfigValue.BBS_ENCHANT_GRADE_ATTRIBUTE[2].equals("C:ON") ? true : ConfigValue.BBS_ENCHANT_GRADE_ATTRIBUTE[2].equals("C:NO") ? false : true);
			case B:
				return ConfigValue.BBS_ENCHANT_GRADE_ATTRIBUTE[3].equals("B:PA") ? (player.hasBonus() ? true : false) : (ConfigValue.BBS_ENCHANT_GRADE_ATTRIBUTE[3].equals("B:ON") ? true : ConfigValue.BBS_ENCHANT_GRADE_ATTRIBUTE[3].equals("B:NO") ? false : true);
			case A:
				return ConfigValue.BBS_ENCHANT_GRADE_ATTRIBUTE[4].equals("A:PA") ? (player.hasBonus() ? true : false) : (ConfigValue.BBS_ENCHANT_GRADE_ATTRIBUTE[4].equals("A:ON") ? true : ConfigValue.BBS_ENCHANT_GRADE_ATTRIBUTE[4].equals("A:NO") ? false : true);
			case S:
				return ConfigValue.BBS_ENCHANT_GRADE_ATTRIBUTE[5].equals("S:PA") ? (player.hasBonus() ? true : false) : (ConfigValue.BBS_ENCHANT_GRADE_ATTRIBUTE[5].equals("S:ON") ? true : ConfigValue.BBS_ENCHANT_GRADE_ATTRIBUTE[5].equals("S:NO") ? false : true);
			case S80:
				return ConfigValue.BBS_ENCHANT_GRADE_ATTRIBUTE[6].equals("S80:PA") ? (player.hasBonus() ? true : false) : (ConfigValue.BBS_ENCHANT_GRADE_ATTRIBUTE[6].equals("S80:ON") ? true : ConfigValue.BBS_ENCHANT_GRADE_ATTRIBUTE[6].equals("S80:NO") ? false : true);
			case S84:
				return ConfigValue.BBS_ENCHANT_GRADE_ATTRIBUTE[7].equals("S84:PA") ? (player.hasBonus() ? true : false) : (ConfigValue.BBS_ENCHANT_GRADE_ATTRIBUTE[7].equals("S84:ON") ? true : ConfigValue.BBS_ENCHANT_GRADE_ATTRIBUTE[7].equals("S84:NO") ? false : true);
			default:
				return false;
		}
	}

	private boolean canEnchantArmorAttribute(int attr, L2ItemInstance item)
	{
		switch(attr)
		{
			case 0:
				if(item.getDeffAttr()[1] != 0)
					return false;
				break;
			case 1:
				if(item.getDeffAttr()[0] != 0)
					return false;
				break;
			case 2:
				if(item.getDeffAttr()[3] != 0)
					return false;
				break;
			case 3:
				if(item.getDeffAttr()[2] != 0)
					return false;
				break;
			case 4:
				if(item.getDeffAttr()[5] != 0)
					return false;
				break;
			case 5:
				if(item.getDeffAttr()[4] != 0)
					return false;
				break;
		}
		return true;
	}

	public void parsewrite(String ar1, String ar2, String ar3, String ar4, String ar5, L2Player player)
	{}

	public void onLoad()
	{
		CommunityHandler.getInstance().registerCommunityHandler(this);
		
		_log.info("CommunityBoard: Forge Enchant: "+((Functions.script & CommunityBoardForge) != CommunityBoardForge ? false : true));
		_log.info("CommunityBoard: Forge Attribut: "+((Functions.script & CommunityBoardForgeAtt) != CommunityBoardForgeAtt ? false : true));
		_log.info("CommunityBoard: Forge Augment: "+((Functions.script & CommunityBoardForgeAug) != CommunityBoardForgeAug ? false : true));		
	}

	public void onReload()
	{}

	public void onShutdown()
	{}

	@SuppressWarnings("rawtypes")
	public Enum[] getCommunityCommandEnum()
	{
		return Commands.values();
	}

	public boolean canBeAugmented(L2ItemInstance item, L2Player player, boolean isAccessoryLifeStone)
	{
		if(!item.canBeEnchanted())
			return false;
		else if(item.isAugmented())
			return false;
		else if(item.isRaidAccessory() || item.getItemId() == 13752 || item.getItemId() == 13753 || item.getItemId() == 13754)
			return false;
		else if(item.getItem().getItemGrade().ordinal() < Grade.C.ordinal())
			return false;
		else if(item.getItemId() >= 14801 && item.getItemId() <= 14809 || item.getItemId() >= 15282 && item.getItemId() <= 15299)
			return false; // бижутерия с ТВ
		int itemType = item.getItem().getType2();
		if((isAccessoryLifeStone ? itemType != L2Item.TYPE2_ACCESSORY : itemType != L2Item.TYPE2_WEAPON) && !ConfigValue.AugmentAll)
			return false;
		else if(player.getPrivateStoreType() != L2Player.STORE_PRIVATE_NONE || player.isDead() || player.isParalyzed() || player.isFishing() || player.isSitting())
			return false;
		return true;
	}
}
