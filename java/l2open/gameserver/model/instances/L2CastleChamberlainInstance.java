package l2open.gameserver.model.instances;

import l2open.config.ConfigValue;
import l2open.extensions.multilang.CustomMessage;
import l2open.gameserver.cache.Msg;
import l2open.gameserver.instancemanager.CastleManager;
import l2open.gameserver.instancemanager.CastleManorManager;
import l2open.gameserver.model.L2Clan;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.entity.SevenSigns;
import l2open.gameserver.model.entity.residence.Castle;
import l2open.gameserver.model.entity.residence.Residence;
import l2open.gameserver.model.entity.siege.territory.TerritorySiege;
import l2open.gameserver.serverpackets.*;
import l2open.gameserver.templates.L2NpcTemplate;
import l2open.gameserver.xml.ItemTemplates;
import l2open.util.Log;

import java.util.StringTokenizer;

public class L2CastleChamberlainInstance extends L2ResidenceManager
{
	private static int Cond_All_False = 0;
	private static int Cond_Busy_Because_Of_Siege = 1;
	private static int Cond_Clan = 2;
	private static int Cond_Clan_wPrivs = 3;
	private static int Cond_Owner = 4;

	public L2CastleChamberlainInstance(int objectId, L2NpcTemplate template)
	{
		super(objectId, template);
	}

	@Override
	public void onBypassFeedback(L2Player player, String command)
	{
		if(!canBypassCheck(player, this))
			return;

		int condition = validateCondition(player);
		if(condition <= Cond_All_False)
			return;

		if(condition == Cond_Busy_Because_Of_Siege)
			return;

		if(condition < Cond_Clan_wPrivs)
			return;

		StringTokenizer st = new StringTokenizer(command, " ");
		String actualCommand = st.nextToken();
		String val = "";
		if(st.countTokens() >= 1)
			val = st.nextToken();

		Castle castle = getCastle();
		if(actualCommand.equalsIgnoreCase("list_siege_clans"))
		{
			if(!isHaveRigths(player, L2Clan.CP_CS_MANAGE_SIEGE))
			{
				player.sendMessage(new CustomMessage("l2open.gameserver.model.instances.L2ResidenceManager.NotAuthorizedToDoThis", player));
				return;
			}
			castle.getSiege().listRegisterClan(player);
		}
		else if(actualCommand.equalsIgnoreCase("CastleFunctions"))
		{
			if(!isHaveRigths(player, L2Clan.CP_CS_SET_FUNCTIONS))
			{
				player.sendMessage(new CustomMessage("l2open.gameserver.model.instances.L2ResidenceManager.NotAuthorizedToDoThis", player));
				return;
			}
			NpcHtmlMessage html = new NpcHtmlMessage(player, this);
			html.setFile("./data/html/castle/chamberlain/chamberlain-castlefunc.htm");
			player.sendPacket(html);
		}
		else if(actualCommand.equalsIgnoreCase("ManageTreasure"))
		{
			if(!player.isClanLeader())
			{
				player.sendMessage(new CustomMessage("l2open.gameserver.model.instances.L2ResidenceManager.NotAuthorizedToDoThis", player));
				return;
			}
			NpcHtmlMessage html = new NpcHtmlMessage(player, this);
			html.setFile("./data/html/castle/chamberlain/chamberlain-castlevault.htm");
			html.replace("%Treasure%", String.valueOf(castle.getTreasury()));
			html.replace("%CollectedShops%", String.valueOf(castle.getCollectedShops()));
			html.replace("%CollectedSeed%", String.valueOf(castle.getCollectedSeed()));
			player.sendPacket(html);
		}
		else if(actualCommand.equalsIgnoreCase("TakeTreasure"))
		{
			if(!player.isClanLeader())
			{
				player.sendMessage(new CustomMessage("l2open.gameserver.model.instances.L2ResidenceManager.NotAuthorizedToDoThis", player));
				return;
			}
			if(!val.equals(""))
			{
				long treasure = Long.parseLong(val);
				if(castle.getTreasury() < treasure)
				{
					NpcHtmlMessage html = new NpcHtmlMessage(player, this);
					html.setFile("./data/html/castle/chamberlain/chamberlain-havenottreasure.htm");
					html.replace("%Treasure%", String.valueOf(castle.getTreasury()));
					html.replace("%Requested%", String.valueOf(treasure));
					player.sendPacket(html);
					return;
				}
				if(treasure > 0)
				{
					castle.addToTreasuryNoTax(-treasure, false, false);
					Log.add(castle.getName() + "|" + -treasure + "|CastleChamberlain", "treasury");
					player.addAdena(treasure);
				}
			}

			NpcHtmlMessage html = new NpcHtmlMessage(player, this);
			html.setFile("./data/html/castle/chamberlain/chamberlain-castlevault.htm");
			html.replace("%Treasure%", String.valueOf(castle.getTreasury()));
			html.replace("%CollectedShops%", String.valueOf(castle.getCollectedShops()));
			html.replace("%CollectedSeed%", String.valueOf(castle.getCollectedSeed()));
			player.sendPacket(html);
		}
		else if(actualCommand.equalsIgnoreCase("PutTreasure"))
		{
			if(!val.equals(""))
			{
				long treasure = Long.parseLong(val);
				if(treasure > player.getAdena())
				{
					player.sendPacket(Msg.YOU_DO_NOT_HAVE_ENOUGH_ADENA);
					return;
				}
				if(treasure > 0)
				{
					castle.addToTreasuryNoTax(treasure, false, false);
					Log.add(castle.getName() + "|" + treasure + "|CastleChamberlain", "treasury");
					player.reduceAdena(treasure, true);
				}
			}

			NpcHtmlMessage html = new NpcHtmlMessage(player, this);
			html.setFile("./data/html/castle/chamberlain/chamberlain-castlevault.htm");
			html.replace("%Treasure%", String.valueOf(castle.getTreasury()));
			html.replace("%CollectedShops%", String.valueOf(castle.getCollectedShops()));
			html.replace("%CollectedSeed%", String.valueOf(castle.getCollectedSeed()));
			player.sendPacket(html);
		}
		else if(actualCommand.equalsIgnoreCase("manor"))
		{
			if(!isHaveRigths(player, L2Clan.CP_CS_MANOR_ADMIN))
			{
				player.sendMessage(new CustomMessage("l2open.gameserver.model.instances.L2ResidenceManager.NotAuthorizedToDoThis", player));
				return;
			}
			String filename = "";
			if(CastleManorManager.getInstance().isDisabled())
				filename = ConfigValue.DatapackRoot +  "/data/html/npcdefault.htm";
			else
			{
				int cmd = Integer.parseInt(val);
				switch(cmd)
				{
					case 0:
						filename = ConfigValue.DatapackRoot + "/data/html/castle/chamberlain/manor/manor.htm";
						break;
					// TODO: correct in html's to 1
					case 4:
						filename = ConfigValue.DatapackRoot + "/data/html/castle/chamberlain/manor/manor_help00" + st.nextToken() + ".htm";
						break;
					default:
						filename = ConfigValue.DatapackRoot + "/data/html/castle/chamberlain/chamberlain-no.htm";
						break;
				}
			}

			if(filename.length() > 0)
			{
				NpcHtmlMessage html = new NpcHtmlMessage(player, this);
				html.setFile(filename);
				html.replace("%objectId%", String.valueOf(getObjectId()));
				html.replace("%npcname%", getName());
				player.sendPacket(html);
			}
		}
		else if(actualCommand.startsWith("manor_menu_select"))
		{
			if(!isHaveRigths(player, L2Clan.CP_CS_MANOR_ADMIN))
			{
				player.sendMessage(new CustomMessage("l2open.gameserver.model.instances.L2ResidenceManager.NotAuthorizedToDoThis", player));
				return;
			}
			// input string format:
			// manor_menu_select?ask=X&state=Y&time=X
			if(CastleManorManager.getInstance().isUnderMaintenance())
			{
				player.sendPacket(Msg.ActionFail, Msg.THE_MANOR_SYSTEM_IS_CURRENTLY_UNDER_MAINTENANCE);
				return;
			}

			String params = actualCommand.substring(actualCommand.indexOf("?") + 1);
			StringTokenizer str = new StringTokenizer(params, "&");
			int ask = Integer.parseInt(str.nextToken().split("=")[1]);
			int state = Integer.parseInt(str.nextToken().split("=")[1]);
			int time = Integer.parseInt(str.nextToken().split("=")[1]);

			int castleId;
			if(state == -1) // info for current manor
				castleId = castle.getId();
			else
				// info for requested manor
				castleId = state;

			switch(ask)
			{ // Main action
				case 3: // Current seeds (Manor info)
					if(time == 1 && !CastleManager.getInstance().getCastleByIndex(castleId).isNextPeriodApproved())
						player.sendPacket(new ExShowSeedInfo(castleId, null));
					else
						player.sendPacket(new ExShowSeedInfo(castleId, CastleManager.getInstance().getCastleByIndex(castleId).getSeedProduction(time)));
					break;
				case 4: // Current crops (Manor info)
					if(time == 1 && !CastleManager.getInstance().getCastleByIndex(castleId).isNextPeriodApproved())
						player.sendPacket(new ExShowCropInfo(castleId, null));
					else
						player.sendPacket(new ExShowCropInfo(castleId, CastleManager.getInstance().getCastleByIndex(castleId).getCropProcure(time)));
					break;
				case 5: // Basic info (Manor info)
					player.sendPacket(new ExShowManorDefaultInfo());
					break;
				case 7: // Edit seed setup
					if(castle.isNextPeriodApproved())
						player.sendPacket(Msg.A_MANOR_CANNOT_BE_SET_UP_BETWEEN_6_AM_AND_8_PM);
					else
						player.sendPacket(new ExShowSeedSetting(castle.getId()));
					break;
				case 8: // Edit crop setup
					if(castle.isNextPeriodApproved())
						player.sendPacket(Msg.A_MANOR_CANNOT_BE_SET_UP_BETWEEN_6_AM_AND_8_PM);
					else
						player.sendPacket(new ExShowCropSetting(castle.getId()));
					break;
			}
		}
		else if(actualCommand.equalsIgnoreCase("operate_door")) // door control
		{
			if(!isHaveRigths(player, L2Clan.CP_CS_ENTRY_EXIT))
			{
				player.sendMessage(new CustomMessage("l2open.gameserver.model.instances.L2ResidenceManager.NotAuthorizedToDoThis", player));
				return;
			}
			if(!val.equals(""))
			{
				boolean open = Integer.parseInt(val) == 1;
				while(st.hasMoreTokens())
					castle.openCloseDoor(player, Integer.parseInt(st.nextToken()), open);
			}

			NpcHtmlMessage html = new NpcHtmlMessage(player, this);
			html.setFile("./data/html/castle/chamberlain/" + getTemplate().npcId + "-d.htm");
			html.replace("%npcname%", getName());
			player.sendPacket(html);
		}
		else if(actualCommand.equalsIgnoreCase("tax_set")) // tax rates control
		{
			if(!isHaveRigths(player, L2Clan.CP_CS_TAXES))
			{
				player.sendMessage(new CustomMessage("l2open.gameserver.model.instances.L2ResidenceManager.NotAuthorizedToDoThis", player));
				return;
			}
			if(!val.equals(""))
			{
				// По умолчанию налог не более 15%
				Integer maxTax = ConfigValue.SetMaxTaxSealNone;
				// Если печатью SEAL_STRIFE владеют DUSK то налог можно выставлять не более 5%
				if(SevenSigns.getInstance().getSealOwner(SevenSigns.SEAL_STRIFE) == SevenSigns.CABAL_DUSK)
					maxTax = ConfigValue.SetMaxTaxSealDusk;
				// Если печатью SEAL_STRIFE владеют DAWN то налог можно выставлять не более 25%
				else if(SevenSigns.getInstance().getSealOwner(SevenSigns.SEAL_STRIFE) == SevenSigns.CABAL_DAWN)
					maxTax = ConfigValue.SetMaxTaxSealDawn;

				if(Integer.parseInt(val) < 0 || Integer.parseInt(val) > maxTax)
				{
					NpcHtmlMessage html = new NpcHtmlMessage(player, this);
					html.setFile("./data/html/castle/chamberlain/chamberlain-hightax.htm");
					html.replace("%CurrentTax%", String.valueOf(castle.getTaxPercent()));
					player.sendPacket(html);
					return;
				}
				castle.setTaxPercent(player, Integer.parseInt(val));
			}

			NpcHtmlMessage html = new NpcHtmlMessage(player, this);
			html.setFile("./data/html/castle/chamberlain/chamberlain-settax.htm");
			html.replace("%CurrentTax%", String.valueOf(castle.getTaxPercent()));
			player.sendPacket(html);
		}
		else if(actualCommand.equalsIgnoreCase("upgrade_castle"))
		{
			if(!isHaveRigths(player, L2Clan.CP_CS_MANAGE_SIEGE))
			{
				player.sendMessage(new CustomMessage("l2open.gameserver.model.instances.L2ResidenceManager.NotAuthorizedToDoThis", player));
				return;
			}
			NpcHtmlMessage html = new NpcHtmlMessage(player, this);
			html.setFile("./data/html/castle/chamberlain/chamberlain-upgrades.htm");
			player.sendPacket(html);
		}
		else if(actualCommand.equalsIgnoreCase("reinforce"))
		{
			if(!isHaveRigths(player, L2Clan.CP_CS_MANAGE_SIEGE))
			{
				player.sendMessage(new CustomMessage("l2open.gameserver.model.instances.L2ResidenceManager.NotAuthorizedToDoThis", player));
				return;
			}
			NpcHtmlMessage html = new NpcHtmlMessage(player, this);
			html.setFile("./data/html/castle/chamberlain/doorStrengthen-" + castle.getName() + ".htm");
			player.sendPacket(html);
		}
		else if(actualCommand.equalsIgnoreCase("door_manage"))
		{
			if(!isHaveRigths(player, L2Clan.CP_CS_ENTRY_EXIT))
			{
				player.sendMessage(new CustomMessage("l2open.gameserver.model.instances.L2ResidenceManager.NotAuthorizedToDoThis", player));
				return;
			}
			NpcHtmlMessage html = new NpcHtmlMessage(player, this);
			html.setFile("./data/html/castle/chamberlain/doorManage.htm");
			html.replace("%id%", val);
			html.replace("%type%", st.nextToken());
			player.sendPacket(html);
		}
		else if(actualCommand.equalsIgnoreCase("upgrade_door_confirm"))
		{
			if(!isHaveRigths(player, L2Clan.CP_CS_MANAGE_SIEGE))
			{
				player.sendMessage(new CustomMessage("l2open.gameserver.model.instances.L2ResidenceManager.NotAuthorizedToDoThis", player));
				return;
			}
			int id = Integer.parseInt(val);
			int type = Integer.parseInt(st.nextToken());
			int level = Integer.parseInt(st.nextToken());
			long price = getDoorCost(type, level);

			NpcHtmlMessage html = new NpcHtmlMessage(player, this);
			html.setFile("./data/html/castle/chamberlain/doorConfirm.htm");
			html.replace("%id%", String.valueOf(id));
			html.replace("%level%", String.valueOf(level));
			html.replace("%type%", String.valueOf(type));
			html.replace("%price%", String.valueOf(price));
			player.sendPacket(html);
		}
		else if(actualCommand.equalsIgnoreCase("upgrade_door"))
		{
			if(!isHaveRigths(player, L2Clan.CP_CS_MANAGE_SIEGE))
			{
				player.sendMessage(new CustomMessage("l2open.gameserver.model.instances.L2ResidenceManager.NotAuthorizedToDoThis", player));
				return;
			}
			int id = Integer.parseInt(val);
			int type = Integer.parseInt(st.nextToken());
			int level = Integer.parseInt(st.nextToken());
			long price = getDoorCost(type, level);

			L2DoorInstance door = castle.getDoor(id);
			if(door == null)
			{
				player.sendMessage(new CustomMessage("common.Error", player));
				return;
			}
			int upgradeHp = (door.getMaxHp() - door.getUpgradeHp()) * level - door.getMaxHp();

			if(price == 0 || upgradeHp < 0)
			{
				player.sendMessage(new CustomMessage("common.Error", player));
				return;
			}

			if(door.getUpgradeHp() >= upgradeHp)
			{
				int oldLevel = door.getUpgradeHp() / (door.getMaxHp() - door.getUpgradeHp()) + 1;
				NpcHtmlMessage html = new NpcHtmlMessage(player, this);
				html.setFile("./data/html/castle/chamberlain/doorAlready.htm");
				html.replace("%level%", String.valueOf(oldLevel));
				player.sendPacket(html);
				return;
			}

			if(player.getClan().getAdenaCount() < price)
			{
				player.sendPacket(Msg.YOU_DO_NOT_HAVE_ENOUGH_ADENA);
				return;
			}

			player.getClan().getWarehouse().destroyItem(57, price);
			castle.upgradeDoor(id, upgradeHp, true);
		}
		else if(actualCommand.equalsIgnoreCase("report")) // Report page
		{
			if(!isHaveRigths(player, L2Clan.CP_CS_USE_FUNCTIONS))
			{
				player.sendMessage(new CustomMessage("l2open.gameserver.model.instances.L2ResidenceManager.NotAuthorizedToDoThis", player));
				return;
			}
			String ssq_period;
			if(SevenSigns.getInstance().getCurrentPeriod() == 1)
				ssq_period = "Competition";
			else if(SevenSigns.getInstance().getCurrentPeriod() == 3)
				ssq_period = "Effective sealing";
			else
				ssq_period = "Ready";

			NpcHtmlMessage html = new NpcHtmlMessage(player, this);
			html.setFile("./data/html/castle/chamberlain/chamberlain-report.htm");
			html.replace("%FeudName%", castle.getName());
			html.replace("%CharClan%", player.getClan().getName());
			html.replace("%CharName%", player.getName());
			html.replace("%SSPeriod%", ssq_period);
			html.replace("%Avarice%", getSealOwner(1));
			html.replace("%Revelation%", getSealOwner(2));
			html.replace("%Strife%", getSealOwner(3));
			player.sendPacket(html);
		}
		else if(actualCommand.equalsIgnoreCase("Crown")) // Give Crown to Castle Owner
		{
			if(!player.isClanLeader())
			{
				player.sendMessage(new CustomMessage("l2open.gameserver.model.instances.L2ResidenceManager.NotAuthorizedToDoThis", player));
				return;
			}
			if(player.getInventory().getItemByItemId(6841) == null)
			{
				player.getInventory().addItem(ItemTemplates.getInstance().createItem(6841));

				NpcHtmlMessage html = new NpcHtmlMessage(player, this);
				html.setFile("./data/html/castle/chamberlain/gavecrown.htm");
				html.replace("%CharName%", String.valueOf(player.getName()));
				html.replace("%FeudName%", castle.getName());
				player.sendPacket(html);
			}
			else
			{
				NpcHtmlMessage html = new NpcHtmlMessage(player, this);
				html.setFile("./data/html/castle/chamberlain/alreadyhavecrown.htm");
				player.sendPacket(html);
			}
		}
		else if(actualCommand.equalsIgnoreCase("default"))
		{
			NpcHtmlMessage html = new NpcHtmlMessage(player, this);
			html.setFile("./data/html/castle/chamberlain/chamberlain.htm");
			player.sendPacket(html);
		}
		else
			super.onBypassFeedback(player, command);
	}

	@Override
	public void showChatWindow(L2Player player, int val)
	{
		String filename = ConfigValue.DatapackRoot + "/data/html/castle/chamberlain/chamberlain-notlord.htm";
		int condition = validateCondition(player);
		if(condition > Cond_All_False)
			if(condition == Cond_Busy_Because_Of_Siege)
				filename = ConfigValue.DatapackRoot + "/data/html/castle/chamberlain/chamberlain-busy.htm";
			else if(condition == Cond_Owner || condition == Cond_Clan_wPrivs) // Clan owns castle
				filename = ConfigValue.DatapackRoot + "/data/html/castle/chamberlain/chamberlain.htm";
		player.sendPacket(new NpcHtmlMessage(player, this, filename, val));
	}

	protected int validateCondition(L2Player player)
	{
		if(player.isGM())
			return Cond_Owner;
		Residence castle = getCastle();
		if(castle != null && castle.getId() > 0)
			if(player.getClan() != null)
				if(castle.getSiege().isInProgress() || TerritorySiege.isInProgress())
					return Cond_Busy_Because_Of_Siege; // Busy because of siege
				else if(castle.getOwnerId() == player.getClanId())
				{
					if(player.isClanLeader()) // Leader of clan
						return Cond_Owner;
					if(isHaveRigths(player, L2Clan.CP_CS_ENTRY_EXIT) || // doors
					isHaveRigths(player, L2Clan.CP_CS_MANOR_ADMIN) || // manor
					isHaveRigths(player, L2Clan.CP_CS_MANAGE_SIEGE) || // siege
					isHaveRigths(player, L2Clan.CP_CS_USE_FUNCTIONS) || // funcs
					isHaveRigths(player, L2Clan.CP_CS_DISMISS) || // banish
					isHaveRigths(player, L2Clan.CP_CS_TAXES) || // tax
					isHaveRigths(player, L2Clan.CP_CS_MERCENARIES) || // merc
					isHaveRigths(player, L2Clan.CP_CS_SET_FUNCTIONS) //funcs
					)
						return Cond_Clan_wPrivs; // Есть какие либо замковые привилегии
					return Cond_Clan;
				}

		return Cond_All_False;
	}

	private String getSealOwner(int seal)
	{
		switch(SevenSigns.getInstance().getSealOwner(seal))
		{
			case SevenSigns.CABAL_DUSK:
				return "Evening";
			case SevenSigns.CABAL_DAWN:
				return "Dawn";
			default:
				return "None belongs";
		}
	}

	private long getDoorCost(int type, int level)
	{
		int price = 0;

		switch(type)
		{
			case 1: // Главные ворота
				switch(level)
				{
					case 2:
						price = 3000000;
						break;
					case 3:
						price = 4000000;
						break;
					case 5:
						price = 5000000;
						break;
				}
				break;
			case 2: // Внутренние ворота
				switch(level)
				{
					case 2:
						price = 750000;
						break;
					case 3:
						price = 900000;
						break;
					case 5:
						price = 1000000;
						break;
				}
				break;
			case 3: // Стены
				switch(level)
				{
					case 2:
						price = 1600000;
						break;
					case 3:
						price = 1800000;
						break;
					case 5:
						price = 2000000;
						break;
				}
				break;
		}

		int SSQ_DawnFactor_door = 80;
		int SSQ_DrawFactor_door = 100;
		int SSQ_DuskFactor_door = 300;

		switch(SevenSigns.getInstance().getSealOwner(SevenSigns.SEAL_STRIFE))
		{
			case SevenSigns.CABAL_DUSK:
				price = price * SSQ_DuskFactor_door / 100;
				break;
			case SevenSigns.CABAL_DAWN:
				price = price * SSQ_DawnFactor_door / 100;
				break;
			default:
				price = price * SSQ_DrawFactor_door / 100;
				break;
		}

		return price;
	}

	@Override
	protected Residence getResidence()
	{
		return getCastle();
	}

	@Override
	public void broadcastDecoInfo()
	{}

	@Override
	protected int getPrivUseFunctions()
	{
		return L2Clan.CP_CS_USE_FUNCTIONS;
	}

	@Override
	protected int getPrivSetFunctions()
	{
		return L2Clan.CP_CS_SET_FUNCTIONS;
	}

	@Override
	protected int getPrivDismiss()
	{
		return L2Clan.CP_CS_DISMISS;
	}

	@Override
	protected int getPrivDoors()
	{
		return L2Clan.CP_CS_ENTRY_EXIT;
	}

	@Override
	public void MENU_SELECTED(L2Player talker, int ask, int reply)
	{
		if(!canBypassCheck(talker, this))
			return;

		L2Player c0 = null;
		StringBuilder fhtml0 = new StringBuilder();
		int i0 = 0;

		if(ask == 708)
		{
			if(reply == 1)
			{
				if(HaveMemo(talker,708) == 0 && IsMyLord(talker) == 1 && IsDominionOfLord(81) == 0)
				{
					FHTML_SetFileName(fhtml0,"chamberlain_saius_q0708_02.htm");
					FHTML_SetInt(fhtml0,"quest_id",708);
					ShowQuestFHTML(talker,fhtml0,708);
				}
			}
			if(reply == 2)
			{
				if(HaveMemo(talker,708) == 1 && GetMemoState(talker,708) == 2 && IsDominionOfLord(81) == 0)
				{
					SetMemoState(talker,708,3);
					ShowPage(talker,"chamberlain_saius_q0708_08.htm");
					SetFlagJournal(talker,708,2);
					ShowQuestMark(talker,708);
					SoundEffect(talker,"ItemSound.quest_middle");
				}
			}
			if(reply == 3)
			{
				c0 = Pledge_GetLeader(talker);
				i0 = talker.getObjectId();
				if(IsNullCreature(c0) == 0 && DistFromMe(c0) <= 1500 && HaveMemo(c0,708) == 1 && GetMemoState(c0,708) == 3 && Castle_GetPledgeId() == talker.getClanId() && talker.getClanId() != 0)
				{
					FHTML_SetFileName(fhtml0,"chamberlain_saius_q0708_12.htm");
					FHTML_SetStr(fhtml0,"name",talker.getName());
					ShowFHTML(talker,fhtml0);
					Say(MakeFString(70852,talker.getName(),"","","",""));
					SetMemoState(c0,708,4);
					SetFlagJournal(c0,708,3);
					ShowQuestMark(c0,708);
					SoundEffect(c0,"ItemSound.quest_middle");
					SetMemoStateEx(c0,708,1,i0);
				}
				else
					ShowQuestPage(talker,"chamberlain_saius_q0708_13.htm",708);
			}
			if(reply == 4)
			{
				if(HaveMemo(talker,708) == 1 && GetMemoState(talker,708) == 49)
				{
					if(GetDominionWarState(81) != 5 && IsMyLord(talker) == 1)
					{
						Say(MakeFString(70859,talker.getName(),"","","",""));
						//st.exitCurrentQuest(true);
						DeclareLord(81,talker);
						RemoveMemo(talker,708);
						SoundEffect(talker,"ItemSound.quest_finish");
						ShowPage(talker,"chamberlain_saius_q0708_23.htm");
					}
				}
			}
		}
		else if(ask == 709)
		{
			if(reply == 1)
			{
				if(HaveMemo(talker, 709) == 0 && IsMyLord(talker) == 1 && IsDominionOfLord(82) == 0)
				{
					FHTML_SetFileName(fhtml0, "chamberlain_crosby_q0709_02.htm");
					FHTML_SetInt(fhtml0, "quest_id", 709);
					ShowQuestFHTML(talker, fhtml0, 709);
				}
			}
			if(reply == 2)
			{
				if(HaveMemo(talker, 709) == 1 && GetMemoState(talker, 709) == 2)
				{
					SetMemoState(talker, 709, 3);
					ShowPage(talker, "chamberlain_crosby_q0709_08.htm");
					SetFlagJournal(talker, 709, 2);
					ShowQuestMark(talker, 709);
					SoundEffect(talker, "ItemSound.quest_middle");
				}
			}
			if(reply == 3)
			{
				c0 = Pledge_GetLeader(talker);
				i0 = talker.getObjectId();
				if(IsNullCreature(c0) == 0)
				{
					if(HaveMemo(c0, 709) == 1 && GetMemoState(c0, 709) == 3)
					{
						if(DistFromMe(c0) <= 1500)
						{
							FHTML_SetFileName(fhtml0, "chamberlain_crosby_q0709_12.htm");
							FHTML_SetStr(fhtml0, "name", talker.getName());
							ShowQuestFHTML(talker, fhtml0, 709);
							Say(MakeFString(70952, talker.getName(), "", "", "", ""));
							SetMemoState(c0, 709, 4);
							SetMemoStateEx(c0, 709, 1, i0);
							SetFlagJournal(c0, 709, 3);
							ShowQuestMark(c0, 709);
							SoundEffect(c0, "ItemSound.quest_middle");
						}
						else
							ShowPage(talker, "chamberlain_crosby_q0709_13.htm");
					}
				}
			}
			if(reply == 4)
			{
				if(HaveMemo(talker, 709) == 1 && GetMemoState(talker, 709) == 49)
				{
					if(GetDominionWarState(82) != 5 && IsMyLord(talker) == 1)
					{
						Say(MakeFString(70959, talker.getName(), "", "", "", ""));
						DeclareLord(82, talker);
						RemoveMemo(talker, 709);
						SoundEffect(talker, "ItemSound.quest_finish");
						ShowPage(talker, "chamberlain_crosby_q0709_23.htm");
					}
				}
			}
		}
		else if(ask == 710)
		{
			if(reply == 1)
			{
				if(HaveMemo(talker,710) == 0 && IsMyLord(talker) == 1 && IsDominionOfLord(83) == 0)
				{
					FHTML_SetFileName(fhtml0,"chamberlain_saul_q0710_02.htm");
					FHTML_SetInt(fhtml0,"quest_id",710);
					ShowQuestFHTML(talker,fhtml0,710);
				}
			}
			if(reply == 2)
			{
				if(HaveMemo(talker,710) == 1 && GetMemoState(talker,710) == 10 && IsMyLord(talker) == 1)
				{
					if(GetDominionWarState(83) != 5)
					{
						Say(MakeFString(71059,talker.getName(),"","","",""));
						DeclareLord(83,talker);
						RemoveMemo(talker,710);
						SoundEffect(talker,"ItemSound.quest_finish");
						ShowPage(talker,"chamberlain_saul_q0710_11.htm");
					}
				}
			}
		}
		else if(ask == 711 && getNpcId() == 35316)
		{
			if(reply == 1)
			{
				if(HaveMemo(talker,711) == 0 && IsMyLord(talker) == 1)
				{
					FHTML_SetFileName(fhtml0,"chamberlain_neurath_q0711_02.htm");
					FHTML_SetInt(fhtml0,"quest_id",711);
					ShowQuestFHTML(talker,fhtml0,711);
				}
			}
			if(reply == 2)
			{
				if(HaveMemo(talker,711) == 1 && GetMemoState(talker,711) == 2 && IsMyLord(talker) == 1)
				{
					SetMemoState(talker,711,3);
					ShowPage(talker,"chamberlain_neurath_q0711_08.htm");
					SetFlagJournal(talker,711,2);
					ShowQuestMark(talker,711);
					SoundEffect(talker,"ItemSound.quest_middle");
				}
			}
			if(reply == 3)
			{
				c0 = Pledge_GetLeader(talker);
				i0 = talker.getObjectId();
				if(IsNullCreature(c0) == 0 && DistFromMe(c0) <= 1500 && HaveMemo(c0,711) == 1 && GetMemoState(c0,711) == 3 && Castle_GetPledgeId() == talker.getClanId() && talker.getClanId() != 0)
				{
					FHTML_SetFileName(fhtml0,"chamberlain_neurath_q0711_12.htm");
					FHTML_SetStr(fhtml0,"name",talker.getName());
					ShowFHTML(talker,fhtml0);
					Say(MakeFString(71152,talker.getName(),"","","",""));
					SetMemoState(c0,711,4);
					SetFlagJournal(c0,711,3);
					ShowQuestMark(c0,711);
					SoundEffect(c0,"ItemSound.quest_middle");
					SetMemoStateEx(c0,711,1,i0);
				}
				else
				{
					ShowPage(talker,"chamberlain_neurath_q0711_13.htm");
				}
			}
			if(reply == 4)
			{
				if(HaveMemo(talker,711) == 1 && (GetMemoState(talker,711) / 1000) >= 101 && IsMyLord(talker) == 1 && (GetMemoState(talker,711) % 100) >= 15 && GetDominionWarState(86) != 5)
				{
					FHTML_SetFileName(fhtml0,"chamberlain_neurath_q0711_21.htm");
					FHTML_SetStr(fhtml0,"name",talker.getName());
					ShowFHTML(talker,fhtml0);
					Say(MakeFString(71159,talker.getName(),"","","",""));
					DeclareLord(86,talker);
					RemoveMemo(talker,711);
					SoundEffect(talker,"ItemSound.quest_finish");
				}
			}
		}
		else if(ask == 712)
		{
			if(reply == 1)
			{
				if(HaveMemo(talker,712) == 0 && IsMyLord(talker) == 1 && IsDominionOfLord(84) == 0)
				{
					FHTML_SetFileName(fhtml0,"chamberlain_brasseur_q0712_02.htm");
					FHTML_SetInt(fhtml0,"quest_id",712);
					ShowQuestFHTML(talker,fhtml0,712);
				}
			}
			if(reply == 2)
			{
				if(HaveMemo(talker,712) == 1 && GetMemoState(talker,712) == 9 && IsMyLord(talker) == 1 && GetDominionWarState(84) != 5)
				{
					FHTML_SetFileName(fhtml0,"chamberlain_brasseur_q0712_11.htm");
					FHTML_SetStr(fhtml0,"name",talker.getName());
					ShowFHTML(talker,fhtml0);
					Say(MakeFString(71259,talker.getName(),"","","",""));
					DeclareLord(84,talker);
					RemoveMemo(talker,712);
					SoundEffect(talker,"ItemSound.quest_finish");
				}
			}
		}
		else if(ask == 713)
		{
			if(reply == 1)
			{
				if(HaveMemo(talker,713) == 1 && GetMemoState(talker,713) == 1000 && IsMyLord(talker) == 1 && GetDominionWarState(85) != 5)
				{
					Say(MakeFString(71351,talker.getName(),"","","",""));
					DeclareLord(85,talker);
					RemoveMemo(talker,713);
					SoundEffect(talker,"ItemSound.quest_finish");
					ShowPage(talker,"chamberlain_logan_q0713_06.htm");
					AddLog(2,talker,713);
				}
			}
		}
		else if(ask == 714)
		{
			if(reply == 1)
			{
				if(HaveMemo(talker,714) == 0 && IsMyLord(talker) == 1 && IsDominionOfLord(89) == 0)
				{
					FHTML_SetFileName(fhtml0,"chamberlain_august_q0714_02.htm");
					FHTML_SetInt(fhtml0,"quest_id",714);
					ShowQuestFHTML(talker,fhtml0,714);
				}
			}
			if(reply == 2)
			{
				if(HaveMemo(talker,714) == 1 && GetMemoState(talker,714) == 2 && IsMyLord(talker) == 1)
				{
					SetMemoState(talker,714,3);
					ShowPage(talker,"chamberlain_august_q0714_08.htm");
					SetFlagJournal(talker,714,2);
					ShowQuestMark(talker,714);
					SoundEffect(talker,"ItemSound.quest_middle");
				}
			}
			if(reply == 3)
			{
				if(HaveMemo(talker,714) == 1 && GetMemoState(talker,714) == 8 && IsMyLord(talker) == 1 && GetDominionWarState(89) != 5)
				{
					FHTML_SetFileName(fhtml0,"chamberlain_august_q0714_13.htm");
					FHTML_SetStr(fhtml0,"name",talker.getName());
					ShowFHTML(talker,fhtml0);
					Say(MakeFString(71459,talker.getName(),"","","",""));
					DeclareLord(89,talker);
					RemoveMemo(talker,714);
					SoundEffect(talker,"ItemSound.quest_finish");
					AddLog(2,talker,714);
				}
			}
		}
		else if(ask == 715)
		{
			if(reply == 1)
			{
				if(HaveMemo(talker,715) == 0 && IsMyLord(talker) == 1 && IsDominionOfLord(87) == 0)
				{
					FHTML_SetFileName(fhtml0,"chamberlain_alfred_q0715_02.htm");
					FHTML_SetInt(fhtml0,"quest_id",715);
					ShowQuestFHTML(talker,fhtml0,715);
				}
			}
			if(reply == 2)
			{
				if(HaveMemo(talker,715) == 1 && GetMemoState(talker,715) == 1)
				{
					i0 = GetMemoState(talker,715);
					SetMemoState(talker,715,(i0 + 100));
					ShowPage(talker,"chamberlain_alfred_q0715_05.htm");
					SetFlagJournal(talker,715,2);
					ShowQuestMark(talker,715);
					SoundEffect(talker,"ItemSound.quest_middle");
				}
			}
			if(reply == 3)
			{
				if(HaveMemo(talker,715) == 1 && GetMemoState(talker,715) == 1)
				{
					i0 = GetMemoState(talker,715);
					SetMemoState(talker,715,(i0 + 10));
					ShowPage(talker,"chamberlain_alfred_q0715_06.htm");
					SetFlagJournal(talker,715,3);
					ShowQuestMark(talker,715);
					SoundEffect(talker,"ItemSound.quest_middle");
				}
			}
			if(reply == 4)
			{
				if(HaveMemo(talker,715) == 1 && (GetMemoState(talker,715) / 10) == 22 && GetDominionWarState(87) != 5 && IsMyLord(talker) == 1)
				{
					Say(MakeFString(71559,talker.getName(),"","","",""));
					DeclareLord(87,talker);
					RemoveMemo(talker,715);
					SoundEffect(talker,"ItemSound.quest_finish");
					AddLog(2,talker,715);
					ShowPage(talker,"chamberlain_alfred_q0715_12.htm");
				}
			}
		}
		else if(ask == 716)
		{
			if(reply == 1)
			{
				if(HaveMemo(talker,716) == 0 && IsMyLord(talker) == 1 && IsDominionOfLord(88) == 0)
				{
					FHTML_SetFileName(fhtml0,"chamberlain_frederick_q0716_02.htm");
					FHTML_SetInt(fhtml0,"quest_id",716);
					ShowQuestFHTML(talker,fhtml0,716);
				}
			}
			if(reply == 3)
			{
				i0 = talker.getObjectId();
				if(IsMyLord(talker) == 0)
				{
					if(Castle_GetPledgeId() == talker.getClanId() && talker.getClanId() != 0)
					{
						c0 = Pledge_GetLeader(talker);
						if(IsNullCreature(c0) == 0)
						{
							if(HaveMemo(c0,716) == 1 && GetMemoState(c0,716) == 4)
							{
								if(DistFromMe(c0) <= 1500)
								{
									FHTML_SetFileName(fhtml0,"chamberlain_frederick_q0716_17.htm");
									FHTML_SetStr(fhtml0,"name",talker.getName());
									ShowFHTML(talker,fhtml0);
									Say(MakeFString(71652,talker.getName(),"","","",""));
									SetMemoState(c0,716,5);
									SetFlagJournal(c0,716,5);
									ShowQuestMark(c0,716);
									SoundEffect(c0,"ItemSound.quest_middle");
									SetMemoStateEx(c0,716,1,i0);
								}
								else
								{
									ShowPage(talker,"chamberlain_frederick_q0716_18.htm");
								}
							}
						}
					}
				}
			}
		}
		super.MENU_SELECTED(talker, ask, reply);
	}

	@Override
	public void QUEST_ACCEPTED(int quest_id, L2Player talker)
	{
		if(quest_id == 708)
		{
			if((GetCurrentTick() - talker.quest_last_reward_time) > 1)
			{
				talker.quest_last_reward_time = GetCurrentTick();
				if(HaveMemo(talker,708) == 0 && IsMyLord(talker) == 1 && IsDominionOfLord(81) == 0)
				{
					int i0 = GetCurrentTick();
					SetMemo(talker,quest_id);
					SetMemoState(talker,708,1);
					SetMemoStateEx(talker,708,1,i0);
					ShowQuestPage(talker,"chamberlain_saius_q0708_04.htm",708);
					SetFlagJournal(talker,708,1);
					ShowQuestMark(talker,708);
					SoundEffect(talker,"ItemSound.quest_middle");
					AddTimerEx(70801,1000 * 60);
				}
			}
		}
		else if(quest_id == 709)
		{
			if(GetCurrentTick() - talker.quest_last_reward_time > 1)
			{
				talker.quest_last_reward_time = GetCurrentTick();
				if(HaveMemo(talker, 709) == 0 && IsMyLord(talker) == 1 && IsDominionOfLord(82) == 0)
				{
					int i0 = GetCurrentTick();
					SetMemo(talker, quest_id);
					SetMemoState(talker, 709, 1);
					SetMemoStateEx(talker, 709, 1, i0);
					SoundEffect(talker, "ItemSound.quest_accept");
					ShowQuestPage(talker, "chamberlain_crosby_q0709_04.htm", 709);
					SetFlagJournal(talker, 709, 1);
					ShowQuestMark(talker, 709);
					AddTimerEx(70901, 1000 * 60);
				}
			}
		}
		else if(quest_id == 710)
		{
			if(GetInventoryInfo(talker,0) >= (GetInventoryInfo(talker,1) * 0.800000) || GetInventoryInfo(talker,2) >= (GetInventoryInfo(talker,3) * 0.800000))
			{
				ShowSystemMessage(talker,1118);
				return;
			}
			if((GetCurrentTick() - talker.quest_last_reward_time) > 1)
			{
				talker.quest_last_reward_time = GetCurrentTick();
				if(HaveMemo(talker,710) == 0 && IsMyLord(talker) == 1 && IsDominionOfLord(83) == 0)
				{
					int i0 = GetCurrentTick();
					SetMemo(talker,quest_id);
					SetMemoState(talker,710,1);
					SetMemoStateEx(talker,710,1,i0);
					SoundEffect(talker,"ItemSound.quest_accept");
					ShowQuestPage(talker,"chamberlain_saul_q0710_04.htm",710);
					SetFlagJournal(talker,710,1);
					ShowQuestMark(talker,710);
					AddTimerEx(71001,(1000 * 60));
				}
			}
			return;
		}
		else if(quest_id == 711)
		{
			SetCurrentQuestID(711);
			if(GetInventoryInfo(talker,0) >= (GetInventoryInfo(talker,1) * 0.800000) || GetInventoryInfo(talker,2) >= (GetInventoryInfo(talker,3) * 0.800000))
			{
				ShowSystemMessage(talker,1118);
				return;
			}
			if((GetCurrentTick() - talker.quest_last_reward_time) > 1)
			{
				talker.quest_last_reward_time = GetCurrentTick();
				if(HaveMemo(talker,711) == 0 && IsMyLord(talker) == 1 && IsDominionOfLord(86) == 0)
				{
					int i0 = GetCurrentTick();
					SetMemo(talker,quest_id);
					SetMemoState(talker,711,1);
					SetMemoStateEx(talker,711,1,i0);
					SoundEffect(talker,"ItemSound.quest_accept");
					ShowQuestPage(talker,"chamberlain_neurath_q0711_04.htm",711);
					SetFlagJournal(talker,711,1);
					ShowQuestMark(talker,711);
					AddTimerEx(71101,(1000 * 60));
				}
			}
			return;
		}
		else if(quest_id == 712)
		{
			SetCurrentQuestID(712);
			if(GetInventoryInfo(talker,0) >= (GetInventoryInfo(talker,1) * 0.800000) || GetInventoryInfo(talker,2) >= (GetInventoryInfo(talker,3) * 0.800000))
			{
				ShowSystemMessage(talker,1118);
				return;
			}
			if((GetCurrentTick() - talker.quest_last_reward_time) > 1)
			{
				talker.quest_last_reward_time = GetCurrentTick();
				if(HaveMemo(talker,712) == 0 && IsMyLord(talker) == 1 && IsDominionOfLord(84) == 0)
				{
					int i0 = GetCurrentTick();
					SetMemo(talker,quest_id);
					SetMemoState(talker,712,1);
					SetMemoStateEx(talker,712,1,i0);
					SoundEffect(talker,"ItemSound.quest_accept");
					ShowQuestPage(talker,"chamberlain_brasseur_q0712_04.htm",712);
					SetFlagJournal(talker,712,1);
					ShowQuestMark(talker,712);
					AddTimerEx(71201,(1000 * 60));
				}
			}
			return;
		}
		else if(quest_id == 713)
		{
			SetCurrentQuestID(713);
			if(GetInventoryInfo(talker,0) >= (GetInventoryInfo(talker,1) * 0.800000) || GetInventoryInfo(talker,2) >= (GetInventoryInfo(talker,3) * 0.800000))
			{
				ShowSystemMessage(talker,1118);
				return;
			}
			if((GetCurrentTick() - talker.quest_last_reward_time) > 1)
			{
				talker.quest_last_reward_time = GetCurrentTick();
				if(HaveMemo(talker,713) == 0 && IsMyLord(talker) == 1 && IsDominionOfLord(85) == 0)
				{
					SetMemo(talker,quest_id);
					SetMemoState(talker,713,1);
					SoundEffect(talker,"ItemSound.quest_accept");
					ShowQuestPage(talker,"chamberlain_logan_q0713_03.htm",713);
					SetFlagJournal(talker,713,1);
					ShowQuestMark(talker,713);
				}
				AddLog(1,talker,quest_id);
			}
			return;
		}
		else if(quest_id == 714)
		{
			SetCurrentQuestID(714);
			if(GetInventoryInfo(talker,0) >= (GetInventoryInfo(talker,1) * 0.800000) || GetInventoryInfo(talker,2) >= (GetInventoryInfo(talker,3) * 0.800000))
			{
				ShowSystemMessage(talker,1118);
				return;
			}
			if((GetCurrentTick() - talker.quest_last_reward_time) > 1)
			{
				talker.quest_last_reward_time = GetCurrentTick();
				if(HaveMemo(talker,714) == 0 && IsMyLord(talker) == 1 && IsDominionOfLord(89) == 0)
				{
					int i0 = GetCurrentTick();
					SetMemo(talker,quest_id);
					SetMemoState(talker,714,1);
					SetMemoStateEx(talker,714,1,i0);
					SoundEffect(talker,"ItemSound.quest_accept");
					ShowQuestPage(talker,"chamberlain_august_q0714_04.htm",714);
					SetFlagJournal(talker,714,1);
					ShowQuestMark(talker,714);
					AddTimerEx(71401,(1000 * 60));
				}
				AddLog(1,talker,quest_id);
			}
			return;
		}
		else if(quest_id == 715)
		{
			SetCurrentQuestID(715);
			if(GetInventoryInfo(talker,0) >= (GetInventoryInfo(talker,1) * 0.800000) || GetInventoryInfo(talker,2) >= (GetInventoryInfo(talker,3) * 0.800000))
			{
				ShowSystemMessage(talker,1118);
				return;
			}
			if((GetCurrentTick() - talker.quest_last_reward_time) > 1)
			{
				talker.quest_last_reward_time = GetCurrentTick();
				if(HaveMemo(talker,715) == 0 && IsMyLord(talker) == 1 && IsDominionOfLord(87) == 0)
				{
					SetMemo(talker,quest_id);
					SetMemoState(talker,715,1);
					SoundEffect(talker,"ItemSound.quest_accept");
					ShowQuestPage(talker,"chamberlain_alfred_q0715_04.htm",715);
					SetFlagJournal(talker,715,1);
					ShowQuestMark(talker,715);
				}
				AddLog(1,talker,quest_id);
			}
			return;
		}
		else if(quest_id == 716)
		{
			SetCurrentQuestID(716);
			if(GetInventoryInfo(talker,0) >= (GetInventoryInfo(talker,1) * 0.800000) || GetInventoryInfo(talker,2) >= (GetInventoryInfo(talker,3) * 0.800000))
			{
				ShowSystemMessage(talker,1118);
				return;
			}
			if((GetCurrentTick() - talker.quest_last_reward_time) > 1)
			{
				talker.quest_last_reward_time = GetCurrentTick();
				if(HaveMemo(talker,716) == 0 && IsMyLord(talker) == 1 && IsDominionOfLord(88) == 0)
				{
					if(GetOneTimeQuestFlag(talker,25) == 0)
					{
						SetMemo(talker,quest_id);
						SetMemoState(talker,716,1);
						SoundEffect(talker,"ItemSound.quest_accept");
						ShowQuestPage(talker,"chamberlain_frederick_q0716_04.htm",716);
						SetFlagJournal(talker,716,1);
						ShowQuestMark(talker,716);
					}
					else
					{
						SetMemo(talker,quest_id);
						SetMemoState(talker,716,2);
						ShowQuestPage(talker,"chamberlain_frederick_q0716_05.htm",716);
						SetFlagJournal(talker,716,2);
						ShowQuestMark(talker,716);
						SoundEffect(talker,"ItemSound.quest_middle");
					}
				}
				AddLog(1,talker,quest_id);
			}
			return;
		}
	}

	@Override
	public void TIMER_FIRED_EX(int timer_id, Object[] arg)
	{
		if(timer_id == 70801)
			Say(70851);
		else if(timer_id == 70901)
			Say(70951);
		else if(timer_id == 71001)
			Say(71051);
		else if(timer_id == 71101)
			Say(71151);
		else if(timer_id == 71201)
			Say(71251);
		else if(timer_id == 71401)
			Say(71451);
		super.TIMER_FIRED_EX(timer_id, arg);
	}
}