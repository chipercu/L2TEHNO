package l2open.gameserver.model.instances;

import l2open.config.*;
import l2open.extensions.multilang.CustomMessage;
import l2open.gameserver.cache.Msg;
import l2open.gameserver.model.L2Clan;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.L2Skill;
import l2open.gameserver.model.L2Skill.SkillType;
import l2open.gameserver.model.entity.residence.Residence;
import l2open.gameserver.model.entity.residence.ResidenceFunction;
import l2open.gameserver.model.entity.residence.TeleportLocation;
import l2open.gameserver.model.items.L2ItemInstance.ItemClass;
import l2open.gameserver.model.items.Warehouse.WarehouseType;
import l2open.gameserver.serverpackets.NpcHtmlMessage;
import l2open.gameserver.serverpackets.WareHouseDepositList;
import l2open.gameserver.serverpackets.WareHouseWithdrawList;
import l2open.gameserver.tables.SkillTable;
import l2open.gameserver.templates.L2NpcTemplate;

import java.text.SimpleDateFormat;
import java.util.StringTokenizer;

public abstract class L2ResidenceManager extends L2MerchantInstance
{
	public L2ResidenceManager(int objectId, L2NpcTemplate template)
	{
		super(objectId, template);
	}

	protected abstract Residence getResidence();

	protected abstract void broadcastDecoInfo();

	protected abstract int getPrivUseFunctions();

	protected abstract int getPrivSetFunctions();

	protected abstract int getPrivDismiss();

	protected abstract int getPrivDoors();

	@Override
	public void onBypassFeedback(L2Player player, String command)
	{
		if(!canBypassCheck(player, this))
			return;

		StringTokenizer st = new StringTokenizer(command, " ");
		String actualCommand = st.nextToken();
		String val = "";
		if(st.countTokens() >= 1)
			val = st.nextToken();

		if(actualCommand.equalsIgnoreCase("banish"))
		{
			NpcHtmlMessage html = new NpcHtmlMessage(player, this);
			html.setFile("data/html/residence/Banish.htm");
			sendHtmlMessage(player, html);
		}
		else if(actualCommand.equalsIgnoreCase("banish_foreigner"))
		{
			if(!isHaveRigths(player, getPrivDismiss()))
			{
				player.sendMessage(new CustomMessage("l2open.gameserver.model.instances.L2ResidenceManager.NotAuthorizedToDoThis", player));
				return;
			}
			getResidence().banishForeigner(player);
			return;
		}
		else if(actualCommand.equalsIgnoreCase("manage_vault"))
		{
			if(val.equalsIgnoreCase("deposit"))
				showDepositWindowClan(player);
			else if(val.equalsIgnoreCase("withdraw"))
			{
				int value = Integer.valueOf(st.nextToken());
				if(value == 9)
				{
					NpcHtmlMessage html = new NpcHtmlMessage(player, this);
					html.setFile("data/html/residence/clan.htm");
					html.replace("%npcname%", getName());
					player.sendPacket(html);
				}
				else
					showWithdrawWindowClan(player, value);
			}
			else
			{
				NpcHtmlMessage html = new NpcHtmlMessage(player, this);
				html.setFile("data/html/residence/vault.htm");
				sendHtmlMessage(player, html);
			}
			return;
		}
		else if(actualCommand.equalsIgnoreCase("door"))
		{
			if(isHaveRigths(player, getPrivDoors()))
			{
				if(val.equalsIgnoreCase("open"))
					getResidence().openCloseDoors(player, true);
				else if(val.equalsIgnoreCase("close"))
					getResidence().openCloseDoors(player, false);
				NpcHtmlMessage html = new NpcHtmlMessage(player, this);
				html.setFile("data/html/residence/door.htm");
				sendHtmlMessage(player, html);
			}
			else
				player.sendMessage(new CustomMessage("l2open.gameserver.model.instances.L2ResidenceManager.NotAuthorizedToDoThis", player));
		}
		else if(actualCommand.equalsIgnoreCase("functions"))
		{
			if(!isHaveRigths(player, getPrivUseFunctions()))
			{
				player.sendMessage(new CustomMessage("l2open.gameserver.model.instances.L2ResidenceManager.NotAuthorizedToDoThis", player));
				return;
			}
			if(val.equalsIgnoreCase("tele"))
			{
				if(!getResidence().isFunctionActive(ResidenceFunction.TELEPORT))
				{
					NpcHtmlMessage html = new NpcHtmlMessage(player, this);
					html.setFile("data/html/residence/teleportNotActive.htm");
					sendHtmlMessage(player, html);
					return;
				}
				NpcHtmlMessage html = new NpcHtmlMessage(player, this);
				html.setFile("data/html/residence/teleport.htm");
				String template = "<a action=\"bypass -h scripts_Util:Gatekeeper %loc% %price% @811;%name%\">%name% - %price% Adena</a><br1>";
				String teleport_list = "";
				for(TeleportLocation loc : getResidence().getFunction(ResidenceFunction.TELEPORT).getTeleports())
					teleport_list += template.replaceAll("%loc%", loc._target).replaceAll("%price%", String.valueOf(loc._price)).replaceAll("%name%", loc.getName(player));
				html.replace("%teleList%", teleport_list);
				sendHtmlMessage(player, html);
			}
			else if(val.equalsIgnoreCase("item_creation"))
			{
				if(!getResidence().isFunctionActive(ResidenceFunction.ITEM_CREATE))
				{
					NpcHtmlMessage html = new NpcHtmlMessage(player, this);
					html.setFile("data/html/residence/itemNotActive.htm");
					sendHtmlMessage(player, html);
					return;
				}
				NpcHtmlMessage html = new NpcHtmlMessage(player, this);
				html.setFile("data/html/residence/item.htm");
				String template = "<button value=\"Buy Item\" action=\"bypass -h npc_%objectId%_Buy %id%\" width=90 height=25 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_ct1.button_df\">";
				template = template.replaceAll("%id%", String.valueOf(getResidence().getFunction(ResidenceFunction.ITEM_CREATE).getBuylist()[1])).replace("%objectId%", String.valueOf(getObjectId()));
				html.replace("%itemList%", template);
				sendHtmlMessage(player, html);
			}
			else if(val.equalsIgnoreCase("support"))
			{
				if(!getResidence().isFunctionActive(ResidenceFunction.SUPPORT))
				{
					NpcHtmlMessage html = new NpcHtmlMessage(player, this);
					html.setFile("data/html/residence/supportNotActive.htm");
					sendHtmlMessage(player, html);
					return;
				}
				NpcHtmlMessage html = new NpcHtmlMessage(player, this);
				html.setFile("data/html/residence/support.htm");
				String template = "<a action=\"bypass -h npc_%objectId%_support %id% %level%\">%name%</a><br1>";
				String support_list = "";
				int i = 0;
				for(Object[] buff : getResidence().getFunction(ResidenceFunction.SUPPORT).getBuffs())
				{
					L2Skill s = (L2Skill) buff[0];
					support_list += template.replaceAll("%id%", String.valueOf(s.getId())).replaceAll("%level%", String.valueOf(s.getLevel())).replaceAll("%name%", s.getName() + " Lv." + s.getDisplayLevel());
					if(++i % 5 == 0)
						support_list += "<br>";
				}
				html.replace("%magicList%", support_list);
				html.replace("%mp%", String.valueOf(Math.round(getCurrentMp())));
				html.replace("%all%", ConfigValue.AltChAllBuffs ? "<a action=\"bypass -h npc_%objectId%_support all\">Give all</a><br1><a action=\"bypass -h npc_%objectId%_support allW\">Give warrior</a><br1><a action=\"bypass -h npc_%objectId%_support allM\">Give mystic</a><br>" : "");
				sendHtmlMessage(player, html);
			}
			else if(val.equalsIgnoreCase("back"))
				showChatWindow(player, 0);
			else
			{
				NpcHtmlMessage html = new NpcHtmlMessage(player, this);
				html.setFile("data/html/residence/functions.htm");
				if(getResidence().isFunctionActive(ResidenceFunction.RESTORE_EXP))
					html.replace("%xp_regen%", String.valueOf(getResidence().getFunction(ResidenceFunction.RESTORE_EXP).getLevel()) + "%");
				else
					html.replace("%xp_regen%", "0%");
				if(getResidence().isFunctionActive(ResidenceFunction.RESTORE_HP))
					html.replace("%hp_regen%", String.valueOf(getResidence().getFunction(ResidenceFunction.RESTORE_HP).getLevel()) + "%");
				else
					html.replace("%hp_regen%", "0%");
				if(getResidence().isFunctionActive(ResidenceFunction.RESTORE_MP))
					html.replace("%mp_regen%", String.valueOf(getResidence().getFunction(ResidenceFunction.RESTORE_MP).getLevel()) + "%");
				else
					html.replace("%mp_regen%", "0%");
				sendHtmlMessage(player, html);
			}
		}
		else if(actualCommand.equalsIgnoreCase("manage"))
		{
			if(!isHaveRigths(player, getPrivSetFunctions()))
			{
				player.sendMessage(new CustomMessage("l2open.gameserver.model.instances.L2ResidenceManager.NotAuthorizedToDoThis", player));
				return;
			}

			if(val.equalsIgnoreCase("recovery"))
			{
				if(st.countTokens() >= 1)
				{
					val = st.nextToken();
					boolean success = true;
					if(val.equalsIgnoreCase("hp"))
						success = getResidence().updateFunctions(ResidenceFunction.RESTORE_HP, Integer.valueOf(st.nextToken()));
					else if(val.equalsIgnoreCase("mp"))
						success = getResidence().updateFunctions(ResidenceFunction.RESTORE_MP, Integer.valueOf(st.nextToken()));
					else if(val.equalsIgnoreCase("exp"))
						success = getResidence().updateFunctions(ResidenceFunction.RESTORE_EXP, Integer.valueOf(st.nextToken()));
					if(!success)
						player.sendPacket(Msg.THERE_IS_NOT_ENOUGH_ADENA_IN_THE_CLAN_HALL_WAREHOUSE);
					else
						broadcastDecoInfo();
				}
				showManageRecovery(player);
			}
			else if(val.equalsIgnoreCase("other"))
			{
				if(st.countTokens() >= 1)
				{
					val = st.nextToken();
					boolean success = true;
					if(val.equalsIgnoreCase("item"))
						success = getResidence().updateFunctions(ResidenceFunction.ITEM_CREATE, Integer.valueOf(st.nextToken()));
					else if(val.equalsIgnoreCase("tele"))
						success = getResidence().updateFunctions(ResidenceFunction.TELEPORT, Integer.valueOf(st.nextToken()));
					else if(val.equalsIgnoreCase("support"))
						success = getResidence().updateFunctions(ResidenceFunction.SUPPORT, Integer.valueOf(st.nextToken()));
					if(!success)
						player.sendPacket(Msg.THERE_IS_NOT_ENOUGH_ADENA_IN_THE_CLAN_HALL_WAREHOUSE);
					else
						broadcastDecoInfo();
				}
				showManageOther(player);
			}
			else if(val.equalsIgnoreCase("deco"))
			{
				if(st.countTokens() >= 1)
				{
					val = st.nextToken();
					boolean success = true;
					if(val.equalsIgnoreCase("platform"))
						success = getResidence().updateFunctions(ResidenceFunction.PLATFORM, Integer.valueOf(st.nextToken()));
					else if(val.equalsIgnoreCase("curtain"))
						success = getResidence().updateFunctions(ResidenceFunction.CURTAIN, Integer.valueOf(st.nextToken()));
					if(!success)
						player.sendPacket(Msg.THERE_IS_NOT_ENOUGH_ADENA_IN_THE_CLAN_HALL_WAREHOUSE);
					else
						broadcastDecoInfo();
				}
				showManageDeco(player);
			}
			else if(val.equalsIgnoreCase("back"))
				showChatWindow(player, 0);
			else
			{
				NpcHtmlMessage html = new NpcHtmlMessage(player, this);
				html.setFile("data/html/residence/manage.htm");
				sendHtmlMessage(player, html);
			}
			return;
		}
		else if(actualCommand.equalsIgnoreCase("support"))
		{
			if(!isHaveRigths(player, getPrivUseFunctions()))
			{
				player.sendMessage(new CustomMessage("l2open.gameserver.model.instances.L2ResidenceManager.NotAuthorizedToDoThis", player));
				return;
			}

			setTarget(player);
			if(val.equals(""))
				return;

			if(!getResidence().isFunctionActive(ResidenceFunction.SUPPORT))
				return;

			if(val.startsWith("all"))
				for(Object[] buff : getResidence().getFunction(ResidenceFunction.SUPPORT).getBuffs())
				{
					if(val.equals("allM") && buff[1] == ResidenceFunction.W || val.equals("allW") && buff[1] == ResidenceFunction.M)
						continue;
					L2Skill s = (L2Skill) buff[0];
					if(!useSkill(s.getId(), s.getLevel(), player))
						break;
				}
			else
			{
				int skill_id = Integer.parseInt(val);
				int skill_lvl = 0;
				if(st.countTokens() >= 1)
					skill_lvl = Integer.parseInt(st.nextToken());
				useSkill(skill_id, skill_lvl, player);
			}

			onBypassFeedback(player, "functions support");
			return;
		}
		super.onBypassFeedback(player, command);
	}

	private boolean useSkill(int id, int level, L2Player player)
	{
		L2Skill skill = SkillTable.getInstance().getInfo(id, level);
		if(skill == null)
		{
			player.sendMessage("Invalid skill " + id);
			return true;
		}
		if(skill.getMpConsume() > getCurrentMp())
		{
			NpcHtmlMessage html = new NpcHtmlMessage(player, this);
			html.setFile("data/html/residence/NeedCoolTime.htm");
			html.replace("%mp%", String.valueOf(Math.round(getCurrentMp())));
			sendHtmlMessage(player, html);
			return false;
		}
		altUseSkill(skill, player);
		//if(ConfigValue.BuffSummon && skill.getSkillType() != SkillType.SUMMON && player.getPet() != null && !player.getPet().isDead())
		//	altUseSkill(skill, player.getPet());
		return true;
	}

	private void sendHtmlMessage(L2Player player, NpcHtmlMessage html)
	{
		html.replace("%npcname%", getName());
		player.sendPacket(html);
	}

	private void showDepositWindowClan(L2Player player)
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
			player.sendPacket(Msg.ONLY_CLANS_OF_CLAN_LEVEL_1_OR_HIGHER_CAN_USE_A_CLAN_WAREHOUSE, Msg.ActionFail);
			return;
		}

		player.tempInventoryDisable();

		if(!(player.isClanLeader() || (ConfigValue.AltAllowOthersWithdrawFromClanWarehouse || player.getVarB("canWhWithdraw")) && (player.getClanPrivileges() & L2Clan.CP_CL_WAREHOUSE_SEARCH) == L2Clan.CP_CL_WAREHOUSE_SEARCH))
			player.sendPacket(Msg.ITEMS_LEFT_AT_THE_CLAN_HALL_WAREHOUSE_CAN_ONLY_BE_RETRIEVED_BY_THE_CLAN_LEADER_DO_YOU_WANT_TO_CONTINUE);

		player.sendPacket(new WareHouseDepositList(player, WarehouseType.CLAN));
	}

	private void showWithdrawWindowClan(L2Player player, int val)
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
			player.sendPacket(Msg.ONLY_CLANS_OF_CLAN_LEVEL_1_OR_HIGHER_CAN_USE_A_CLAN_WAREHOUSE, Msg.ActionFail);
			return;
		}

		if(isHaveRigths(player, L2Clan.CP_CL_WAREHOUSE_SEARCH))
		{
			player.tempInventoryDisable();
			player.sendPacket(new WareHouseWithdrawList(player, WarehouseType.CLAN, ItemClass.values()[val]));
		}
		else
			player.sendPacket(Msg.YOU_DO_NOT_HAVE_THE_RIGHT_TO_USE_THE_CLAN_WAREHOUSE, Msg.ActionFail);
	}

	private void replace(NpcHtmlMessage html, int type, String replace1, String replace2)
	{
		SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy HH:mm");
		boolean proc = type == ResidenceFunction.RESTORE_HP || type == ResidenceFunction.RESTORE_MP || type == ResidenceFunction.RESTORE_EXP;
		if(getResidence().isFunctionActive(type))
		{
			html.replace("%" + replace1 + "%", String.valueOf(getResidence().getFunction(type).getLevel()) + (proc ? "%" : ""));
			html.replace("%" + replace1 + "Price%", String.valueOf(getResidence().getFunction(type).getLease()));
			html.replace("%" + replace1 + "Date%", format.format(getResidence().getFunction(type).getEndTimeInMillis()));
		}
		else
		{
			html.replace("%" + replace1 + "%", "0");
			html.replace("%" + replace1 + "Price%", "0");
			html.replace("%" + replace1 + "Date%", "0");
		}
		if(getResidence().getFunction(type) != null && getResidence().getFunction(type).getLevels().size() > 0)
		{
			String out = "[<a action=\"bypass -h npc_%objectId%_manage " + replace2 + " " + replace1 + " 0\">Stop</a>]";
			for(int level : getResidence().getFunction(type).getLevels())
				out += "[<a action=\"bypass -h npc_%objectId%_manage " + replace2 + " " + replace1 + " " + level + "\">" + level + (proc ? "%" : "") + "</a>]";
			html.replace("%" + replace1 + "Manage%", out);
		}
		else
			html.replace("%" + replace1 + "Manage%", "Not Available");
	}

	private void showManageRecovery(L2Player player)
	{
		NpcHtmlMessage html = new NpcHtmlMessage(player, this);
		html.setFile("data/html/residence/edit_recovery.htm");

		replace(html, ResidenceFunction.RESTORE_EXP, "exp", "recovery");
		replace(html, ResidenceFunction.RESTORE_HP, "hp", "recovery");
		replace(html, ResidenceFunction.RESTORE_MP, "mp", "recovery");

		sendHtmlMessage(player, html);
	}

	private void showManageOther(L2Player player)
	{
		NpcHtmlMessage html = new NpcHtmlMessage(player, this);
		html.setFile("data/html/residence/edit_other.htm");

		replace(html, ResidenceFunction.TELEPORT, "tele", "other");
		replace(html, ResidenceFunction.SUPPORT, "support", "other");
		replace(html, ResidenceFunction.ITEM_CREATE, "item", "other");

		sendHtmlMessage(player, html);
	}

	private void showManageDeco(L2Player player)
	{
		NpcHtmlMessage html = new NpcHtmlMessage(player, this);
		html.setFile("data/html/residence/edit_deco.htm");

		replace(html, ResidenceFunction.CURTAIN, "curtain", "deco");
		replace(html, ResidenceFunction.PLATFORM, "platform", "deco");

		sendHtmlMessage(player, html);
	}

	protected boolean isHaveRigths(L2Player player, int rigthsToCheck)
	{
		if(player.isGM())
			return true;
		return player.getClan() != null && (player.getClanPrivileges() & rigthsToCheck) == rigthsToCheck;
	}
}