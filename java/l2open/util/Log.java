package l2open.util;

import l2open.config.ConfigValue;
import l2open.database.DatabaseUtils;
import l2open.database.FiltredPreparedStatement;
import l2open.database.L2DatabaseFactory;
import l2open.database.ThreadConnection;
import l2open.gameserver.model.L2Character;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.gameserver.model.items.L2ItemInstance;
import l2open.gameserver.tables.GmListTable;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Log
{
	//	Chat
	public static final int General = 0;
	public static final int Shout = 1;
	public static final int Whisper = 2;
	public static final int Party = 3;
	public static final int Clan = 4;
	public static final int Petition = 6;
	public static final int GM = 7;
	public static final int Trade = 8;
	public static final int Alliance = 9;
	//	NPC
	public static final int CreatePet = 103;
	public static final int DeletePet = 104;
	public static final int WithDrawPet = 105;
	public static final int DepositPet = 106;
	public static final int ChangePetName = 107;
	public static final int DismissPet = 108;
	public static final int PetGetItem = 109;
	public static final int PetDropItem = 110;
	public static final int GiveItemToPet = 111;
	public static final int GetItemFromPet = 112;
	public static final int PetUseItem = 113;
	public static final int PetDie = 114;
	//	Clan
	public static final int CreatePledge = 201;
	public static final int JoinPledge = 202;
	public static final int DismissPledge = 203;
	public static final int WithdrawPledge = 204;
	public static final int OustPledge = 205;
	public static final int NicknamePledge = 206;
	public static final int BeginPledgeWar = 207;
	public static final int StopWar = 208;
	public static final int SurrenderWar = 209;
	public static final int WinWar = 210;
	public static final int CrestPledge = 211;
	public static final int FinishWar = 212;
	public static final int UpdateCastleOwner = 213;
	public static final int WriteTax = 214;
	public static final int SetPledgeInfo = 215;
	public static final int DeletePledgeByTimer = 216;
	public static final int SetUserPledgeInfo = 217;
	public static final int ChallengeRejected = 218;
	public static final int InstallBattleCamp = 219;
	public static final int UninstallAllBattleCamp = 220;
	public static final int UninstallBattleCamp = 221;
	public static final int SetNextSiegeTime = 222;
	public static final int RegisterAsAttacter = 223;
	public static final int RegisterAsDefender = 224;
	public static final int UnregisterCastleWar = 225;
	public static final int ConfirmCastleDefence = 226;
	public static final int ResetCastleSiegePledge = 227;
	public static final int TryPossessHolyThing = 228;
	public static final int DoorHpChanged = 229;
	public static final int SetPledgeContribution = 230;
	public static final int SetWinnerPledgeContribution = 231;
	public static final int UpdateAgitOwner = 232;
	public static final int SetDoorOpenClose = 233;
	public static final int SaveCastleIncome = 234;
	public static final int InstallAgitDeco = 235;
	public static final int TryDismissPledge = 236;
	public static final int SetNextCastleSiege = 237;
	public static final int CreateAgitAuction = 238;
	public static final int CreateAgitBid = 239;
	public static final int SetAgitAuction = 240;
	public static final int CancelAgitAuction = 241;
	public static final int CancelAgitBid = 242;
	public static final int AutoAgitAuction = 243;
	public static final int WinSiege = 244;
	public static final int WinSiegeAlliance = 245;
	public static final int AgitCost = 246;
	public static final int UndoDismissPledge = 247;
	public static final int CreateAlliance = 248;
	public static final int JoinAlliance = 249;
	public static final int DismissAlliance = 250;
	public static final int OustAlliance = 251;
	public static final int WithdrawAlliance = 252;
	//	Item
	public static final int BuyItem = 901; //
	public static final int SellItem = 902; //
	public static final int Deposit = 903;
	public static final int Retrieve = 904;
	public static final int GetItem = 906; //
	public static final int DeleteItem = 907; //
	public static final int Drop = 908; //
	public static final int TradeGive = 909; //
	public static final int TradeGet = 910; //
	public static final int Use = 911;
	public static final int NPCDrop = 912;
	public static final int NPCShowSellPage = 913;
	public static final int NPCShowBuyPage = 914;
	public static final int DropItemWhenDied = 915;
	public static final int TradeDone = 916;
	public static final int TradeCanceled = 917;
	public static final int BeginTrade = 918;
	public static final int BeginDeposit = 919;
	public static final int DepositDone = 920;
	public static final int DepositCanceled = 921;
	public static final int BeginRetrieval = 922;
	public static final int RetrievalDone = 923;
	public static final int RetrievalCanceled = 924;
	public static final int EnchantItem = 925; //
	public static final int EnchantItemFail = 926; //
	public static final int DepositToWarehouse = 927; //
	public static final int DespositFee = 928;
	public static final int RetrieveFromWarehouse = 929; //
	public static final int SetPrivateMsg = 930;
	public static final int PrivateStoreSell = 931;
	public static final int PrivateStoreBuy = 932;
	public static final int CrystalizeItem = 933; //
	public static final int RecipeDelete = 934;
	public static final int RecipeCreate = 935;
	public static final int ShipDepart = 936;
	public static final int ShipKicked = 937;
	public static final int UseArrow = 938;
	public static final int UseTelepoter = 939;
	public static final int RelatedItem = 940;
	public static final int SetBuyPrivateMsg = 941;
	public static final int ItemAddFailed = 942;
	public static final int RetrieveFromCastleWarehouse = 943;
	public static final int DepositToCastleWarehouse = 944;
	public static final int DepositFee2 = 945;
	public static final int KeepPackage = 946;
	public static final int KeepPackageFee = 947;
	public static final int BuyItemTax = 948;
	public static final int PrecedenceTax = 949;
	public static final int CastleTax = 950;
	public static final int SearchTax = 951;
	public static final int GetItemByAutoLoot = 952; //
	public static final int GetItemInPaty = 953; //
	public static final int PickupItem = 954; //
	public static final int HarvesterItem = 955; //
	public static final int SweepItem = 956; //
	public static final int RetrieveFromClanWarehouse = 957; //
	public static final int DepositToClanWarehouse = 958; //
	public static final int Sys_GetItem = 959; //
	public static final int Sys_DeleteItem = 960; //

	//	Skill
	public static final int LearnSkill = 401;
	public static final int DeleteSkill = 402;
	public static final int CastSkill = 403;
	public static final int CancelSkill = 405;
	public static final int Dispell = 406;
	public static final int DispellAll = 407;
	// administrator Web
	public static final int Web_CheckCharacter = 601;
	public static final int Web_SetCharLocation = 602;
	public static final int Web_SetBuilderCharacter = 603;
	public static final int Web_ChangeCharName = 604;
	public static final int Web_KickChar = 605;
	public static final int Web_AddSkill = 606;
	public static final int Web_DelSkill = 607;
	public static final int Web_ModSkill = 608;
	public static final int Web_SetOneTimeQuest = 609;
	public static final int Web_SetQuest = 610;
	public static final int Web_DelQuest = 611;
	public static final int Web_AddItem = 612;
	public static final int Web_DelItem = 613;
	public static final int Web_ModItem = 614;
	public static final int Web_ModChar = 615;
	public static final int Web_ModChar2 = 616;
	public static final int Web_ModCharPledge = 617;
	public static final int Web_PunishChar = 618;
	public static final int Web_SetBuilderAccount = 619;
	public static final int Web_DisableChar = 620;
	public static final int Web_EnableChar = 621;
	public static final int Web_GetChars = 622;
	public static final int Web_SetBookmark = 623;
	public static final int Web_DelBookmark = 624;
	public static final int Web_SeizeItem = 625;
	public static final int Web_Modchar3 = 626;
	public static final int Web_MoveItem = 627;
	public static final int Web_MoveChar_DisableChar = 628;
	public static final int Web_MoveChar = 629;
	public static final int Web_WriteComment = 630;
	public static final int Web_DeleteComment = 631;
	public static final int Web_DeleteCharCompletely = 632;
	public static final int Web_RestoreChar = 633;
	public static final int Web_Web_OustPledge = 634;
	public static final int Web_ChangePledgeOwner = 635;
	public static final int Web_DeletePledge = 636;
	public static final int Web_BanChar = 637;
	public static final int Web_AddItem2 = 638;
	public static final int Web_DelItem2 = 639;
	public static final int Web_MoveItem2 = 640;
	public static final int Web_CopyChar = 641;
	public static final int Web_CreatePet = 642;

	//	Administrator
	public static final int Adm_DebugChar = 501;
	public static final int Adm_SummonNpc = 502;
	public static final int Adm_SummonItem = 503;
	public static final int Adm_SetParam = 504;
	public static final int Adm_SetSkill = 505;
	public static final int Adm_TeleportToBookmark = 506;
	public static final int Adm_SetOneTimeQuest = 507;
	public static final int Adm_SetQuest = 508;
	public static final int Adm_KillMe = 509;
	public static final int Adm_Home = 510;
	public static final int Adm_SetAI = 511;
	public static final int Adm_SetKarma = 512;
	public static final int Adm_StopSay = 513;
	public static final int Adm_StopLogin = 514;
	public static final int Adm_GMListOn = 515;
	public static final int Adm_Petition = 517;
	public static final int Adm_Recall = 518;
	public static final int Adm_TeleportTo = 519;
	public static final int Adm_Kick = 520;
	public static final int Adm_Announce = 521;
	public static final int Adm_SetAnnounce = 522;
	public static final int Adm_DelAnnounce = 523;
	public static final int Adm_SetBuilder = 524;
	public static final int Adm_Summon = 525;
	public static final int Adm_DelSkill = 526;
	public static final int Adm_DelQuest = 527;
	public static final int Adm_UnregisterCastlePledge = 528;
	public static final int Adm_SetDoorHp = 529;
	public static final int Adm_SetPledgeLevel = 530;
	public static final int Adm_SetSiege = 531;
	public static final int Adm_SetQuickSiege = 532;
	public static final int Adm_SetCastleStatus = 533;
	public static final int Adm_Defend = 534;
	public static final int Adm_Attack = 535;
	public static final int Adm_SetCastleOwner = 536;
	public static final int Adm_SetBp = 537;
	public static final int Adm_Polymorph = 538;
	public static final int Adm_SendHome = 539;
	public static final int Adm_AddItem = 540; //
	public static final int Adm_DelItem = 541; //

	//	Character
	public static final int Authed = 801;
	public static final int Login = 802;
	public static final int EnterWorld = 803; //
	public static final int Logout = 804; //
	public static final int LeaveWorld = 805;
	public static final int CreateChar = 806;
	public static final int DeleteChar = 807;
	public static final int ChangeName = 808;
	public static final int ChangeCharLevel = 810;
	public static final int Teleport = 811;
	public static final int SaveCharInfo = 812;
	public static final int SaveCharItemInfo = 813;
	public static final int SaveQuest = 814;
	public static final int SaveOnetimeQuest = 815;
	public static final int ChangeCharClass = 816;
	public static final int CreateParty = 817;
	public static final int JoinParty = 818;
	public static final int DismissParty = 819;
	public static final int WithdrawParty = 820;
	public static final int OustParty = 821;
	public static final int Stand = 822;
	public static final int Sit = 823;
	public static final int EquipItem = 824;
	public static final int PCAttackPc = 825;
	public static final int PCAttackNpc = 826;
	public static final int NPCAttackPc = 827;
	public static final int NPCAttackNpc = 828;
	public static final int RestoreDeletedChar = 829;
	public static final int ConfirmDeleteChar = 830;
	public static final int Leaveworld2 = 831;
	public static final int SayCount = 832;
	public static final int EquippedItems = 833;
	public static final int DelCharByDelAccount = 834;
	public static final int CharLimitExceed = 835;
	public static final int PartyCount = 836;
	public static final int CharCount = 837;
	public static final int EndPrivateStore = 838;
	public static final int L2WalkerFound = 839; //
	public static final int BugUse = 840; //
	public static final int IllegalAction = 841; //

	//	Quest
	public static final int GetQuestItem = 301;
	public static final int DeleteQuestItem = 302;
	public static final int BeginQuest = 303;
	public static final int UpdateQuest = 304;
	public static final int DelQuest = 305;
	public static final int SetOneTimeQuest = 306;
	public static final int StopQuest = 307;
	//	Death
	public static final int PCDie = 1101;
	public static final int NPCKilledPlayer = 1105;
	public static final int NPCKilledNPC = 1106;
	public static final int PCKilledPlayer = 1111;
	public static final int PCKilledNPC = 1112;
	public static final int KillByDuel = 1113;
	public static final int KillByPK = 1114;
	public static final int Restart = 1115;
	public static final int SummonedNPCKillPC = 1116;
	public static final int GotHeightDamage = 1117;
	public static final int RessurectBy = 1118;
	public static final int DieDropItemCount = 1119;
	public static final int PcDamagedBy = 1120;
	//	Audit
	public static final int CacheDAuditItemInfo = 1401;
	public static final int NPCSpawn = 1402;
	public static final int NPCDropItem = 1403;
	public static final int AuditItem = 1404;
	public static final int InvalidAdena = 1405;

	// LoginServ
	public static final int Login_CreateAcc = 1501; //
	public static final int Login_Authed = 1502; //
	public static final int Login_IncorrectPass = 1503; //
	public static final int Login_Ban = 1504; //
	public static final int Login_BanIp = 1505; //
	public static final int Login_UnBanIp = 1506; //
	public static final int Login_MissingAcc = 1507; //
	public static final int Login_HackExcept = 1508; //
	public static final int Login_LogoutFromGS = 1509; //
	public static final int Login_GSConnect = 1510; //
	public static final int Login_GSDisConnect = 1511; //

	// GameServ
	public static final int GS_start = 1601; //
	public static final int GS_started = 1602; //

	public static final int GS_SIGTERM = 1603; //
	public static final int GS_shutdown = 1604; //
	public static final int GS_restart = 1605; //
	public static final int GS_aborting = 1606; //

	public static final int CMD_FORTH = 1;
	public static final int CMD_ADMH = 2; //AdminCommandHandler
	/*
	 public static final int ITEM_FROM_MOB = 1;
	 public static final int ITEM_BUY = 2; //from shop
	 public static final int ITEM_MAKE = 3; //dwarfs
	 public static final int ITEM_CREATE = 4; // GM
	 public static final int ITEM_TRADE = 5; //p2p
	 public static final int ITEM_PICKUP = 6;

	 public static final int ITEM_DROP = 10;
	 public static final int ITEM_DESTROY = 11;
	 public static final int ITEM_CRYSTALIZE = 12;
	 public static final int ITEM_SELL = 13; //to shop
	 */

	private static final Logger _log = Logger.getLogger(Log.class.getName());
	private static Logger _logCommand = null;
	private static Logger _logGm = null;
	private static Logger _mainLog = null;

	public static void InitGSLoggers()
	{
		if(_logCommand == null)
			_logCommand = Logger.getLogger("commands");
		if(_logGm == null)
			_logGm = Logger.getLogger("gmactions");
		if(_mainLog == null)
			_mainLog = Logger.getLogger("mainlog");
	}

	public static void addStackTrace(String cat)
	{
		StringWriter sWriter = new StringWriter();
		new Exception("Stack trace").printStackTrace(new PrintWriter(sWriter));
		add(sWriter.getBuffer().toString(), cat, "yy.MM.dd HH:mm:ss:SSS", null);
	}

	public static void addMy(String text, String type, String name)
	{
		new File("log/debug/"+type).mkdirs();
		add(text, "../debug/"+type+"/"+name, "yy.MM.dd HH:mm:ss:SSS", null);
	}

	public static void addBot(String text, String type, String name)
	{
		new File("log/bot/"+type).mkdirs();
		add(text, "../bot/"+type+"/"+name, "yy.MM.dd HH:mm:ss", null);
	}

	public static void addGame(String text, String type, String name)
	{
		new File("log/game/"+type+"/"+new SimpleDateFormat("yyyy.MM.dd").format(new Date())).mkdirs();
		add(text, "../game/"+type+"/"+new SimpleDateFormat("yyyy.MM.dd").format(new Date())+"/"+name, "yy.MM.dd HH:mm:ss", null);
	}

	public static void addFolder(String text, String folder, String type, String name, boolean set_date)
	{
		new File("log/"+folder+"/"+type+(set_date ? "/"+new SimpleDateFormat("yyyy.MM.dd").format(new Date()) : "")).mkdirs();
		add(text, "../"+folder+"/"+type+(set_date ? "/"+new SimpleDateFormat("yyyy.MM.dd").format(new Date()) : "")+"/"+name, "yy.MM.dd HH:mm:ss", null);
	}

	public static void add(String text, String cat)
	{
		if(cat.equals("items") || cat.equals("items-detail"))
		{
			new File("log/game/items/"+new SimpleDateFormat("yyyy.MM.dd").format(new Date())).mkdirs();
			cat = "items/"+new SimpleDateFormat("yyyy.MM.dd").format(new Date())+"/"+cat + new SimpleDateFormat("yyyy.MM.dd.HH").format(new Date());
		}
		/*else if(cat.equals("mail"))
		{
			new File("log/game/mail/"+new SimpleDateFormat("yyyy.MM.dd").format(new Date())).mkdirs();
			cat = "mail/"+new SimpleDateFormat("yyyy.MM.dd").format(new Date())+"/"+cat + new SimpleDateFormat("yyyy.MM.dd.HH").format(new Date());
		}*/
		add(text, cat, "yy.MM.dd HH:mm:ss:SSS", null);
	}

	public static void add(PrintfFormat fmt, Object[] o, String cat)
	{
		add(fmt.sprintf(o), cat);
	}

	public static void add(String fmt, Object[] o, String cat)
	{
		add(new PrintfFormat(fmt).sprintf(o), cat);
	}

	public static void add(String text, String cat, L2Player activeChar)
	{
		add(text, cat, "yy.MM.dd HH:mm:ss", activeChar);
	}

	public static void add(String text, String cat, String DateFormat)
	{
		add(text, cat, DateFormat, null);
	}

	public static synchronized void add(String text, String cat, String DateFormat, L2Player activeChar)
	{
		new File("log/game").mkdirs();
		new File("log/thread_pool").mkdirs();
		File file = new File("log/game/" + (cat != null ? cat : "_all") + ".txt");

		if(!file.exists())
			try
			{
				file.createNewFile();
			}
			catch(IOException e)
			{
				_log.warning("saving " + (cat != null ? cat : "all") + " log failed, can't create file: " + e);
				return;
			}

		FileWriter save = null;
		StringBuffer msgb = new StringBuffer();

		try
		{
			save = new FileWriter(file, true);
			if(!DateFormat.equals(""))
			{
				String date = new SimpleDateFormat(DateFormat).format(new Date());
				msgb.append("[" + date + "]: ");
			}

			if(activeChar != null)
				msgb.append(activeChar.toFullString() + " ");

			msgb.append(text + "\n");
			save.write(msgb.toString());
		}
		catch(IOException e)
		{
			try
			{
				if(save != null)
					save.close();
			}
			catch(Exception e2)
			{}
			_log.warning("saving " + (cat != null ? cat : "all") + " log failed: " + e);
			e.printStackTrace();
		}
		finally
		{
			try
			{
				if(save != null)
					save.close();
			}
			catch(Exception e1)
			{}
		}
	}

	public static void IllegalPlayerAction(L2Player player, String msg, int jailItems)
	{
		if(player == null)
			return;

		msg = "Illegal " + player.toFullString() + " action " + (jailItems > 0 ? "(autojailed for " + jailItems + " items)" : "") + ": " + msg;

		Log.add(msg, "illegal-actions");

		GmListTable.broadcastMessageToGMs(msg);

		//TODO реализовать тюрьму :)
	}

	public static void LogCommand(L2Player activeChar, int command_type, String command, Integer success)
	{
		StringBuffer msgb = new StringBuffer(160);
		if(activeChar.isGM())
			msgb.append("GM ");
		msgb.append(activeChar.toFullString()).append(" command_type:").append(command_type).append(" success:").append(success).append(" command:").append(command);
		msgb.append(" [target: ").append(activeChar.getTarget()).append("]");
		_logCommand.info(msgb.toString());
		if(activeChar.isGM())
			_logGm.info(msgb.toString());
	}

	public static void LogServ(Integer log_id, Integer etc_num1, Integer etc_num2, Integer etc_num3, Integer etc_num4)
	{
		if(log_id > 1699 || log_id < 1600)
		{
			_log.warning("Incorrect log_id " + log_id + " for LogServ");
			return;
		}

		SqlLog(null, null, log_id, null, "", "", "", etc_num1, etc_num2, etc_num3, etc_num4, 0, 0, 0, 0, 0L, 0L);
	}

	public static void LogBug(L2Character activeObject, Integer log_id, String etc_str1, String etc_str2, String etc_str3, Integer etc_num1, Integer etc_num2)
	{
		if(log_id != BugUse && log_id != IllegalAction)
		{
			_log.warning("Incorrect log_id " + log_id + " for LogBug");
			return;
		}

		SqlLog(activeObject, null, log_id, null, etc_str1, etc_str2, etc_str3, etc_num1, etc_num2, 0, 0, 0, 0, 0, 0, 0L, 0L);
	}

	public static void LogItem(L2Character activeObject, Integer log_id, L2ItemInstance Item)
	{
		LogItem(activeObject, null, log_id, Item);
	}

	public static void LogItem(L2Character activeObject, L2Character target, Integer log_id, L2ItemInstance Item)
	{
		LogItem(activeObject, target, log_id, Item, 0L);
	}

	public static void LogItem(L2Character activeObject, Integer log_id, L2ItemInstance Item, Long count)
	{
		LogItem(activeObject, null, log_id, Item, count);
	}

	public static void LogItem(L2Character activeObject, L2Character target, Integer log_id, L2ItemInstance Item, Long count)
	{
		if((log_id > 1000 || log_id < 900) && log_id != 301 && log_id != 302 && log_id < 111 && log_id > 113)
		{
			_log.warning("Incorrect log_id " + log_id + " for LogItem");
			Thread.dumpStack();
			return;
		}

		if(activeObject == null)
		{
			_log.warning("null activeObject (target: " + target + "; log_id: " + log_id + "; Item: " + Item + "; count: " + count + ") for LogItem");
			Thread.dumpStack();
			return;
		}

		if(Item == null)
		{
			_log.warning("null Item (activeObject: " + activeObject + "; target: " + target + "; log_id: " + log_id + "; count: " + count + ") for LogItem");
			Thread.dumpStack();
			return;
		}

		SqlLog(activeObject, target, log_id, Item, "", "", "", 0, 0, 0, 0, 0, 0, Item.getEnchantLevel(), Item.getItemId(), count.longValue(), Item.getCount());
	}

	public static void LogChar(L2Character activeObject, Integer log_id, String etc_str3)
	{
		if(activeObject.getTarget() != null && activeObject.getTarget().isCharacter())
			SqlLogWithCharInfo(activeObject, (L2Character) activeObject.getTarget(), log_id, null, etc_str3, 0, 0, 0, 0, 0L, 0L);
		else
			SqlLogWithCharInfo(activeObject, null, log_id, null, etc_str3, 0, 0, 0, 0, 0L, 0L);
	}

	public static void SqlLogWithCharInfo(L2Character activeObject, L2Character target, Integer log_id, L2ItemInstance Item, String etc_str3, Integer etc_num5, Integer etc_num6, Integer etc_num7, Integer etc_num8, Long etc_num9, Long etc_num10)
	{
		if(!activeObject.isPlayer())
		{
			_log.warning("activeObject isn't L2Player");
			return;
		}

		try
		{
			SqlLog(activeObject, target, log_id, Item, "", //etc_str1, title
			((L2Player) activeObject).getClan() != null ? ((L2Player) activeObject).getClan().getName() : "",//etc_str2,
			etc_str3, ((L2Player) activeObject).getBaseClassId(), //etc_num1,
			1, //etc_num2,
			((L2Player) activeObject).getActiveClassId(), //etc_num3,
			0 + activeObject.getLevel(), //etc_num4,
			etc_num5, etc_num6, etc_num7, etc_num8, etc_num9, etc_num10);
		}
		catch(Exception e)
		{}
	}

	public static void SqlLog(L2Character activeObject, L2Character target, Integer log_id, L2ItemInstance Item, String etc_str1, String etc_str2, String etc_str3, Integer etc_num1, Integer etc_num2, Integer etc_num3, Integer etc_num4, Integer etc_num5, Integer etc_num6, Integer etc_num7, Integer etc_num8, Long etc_num9, Long etc_num10)
	{
		if(ConfigValue.SqlLog)
		{
			ThreadConnection con = null;
			FiltredPreparedStatement statement = null;
			try
			{
				con = L2DatabaseFactory.getInstance().getConnection();
				statement = con.prepareStatement("INSERT INTO game_log (serv_id, act_time, log_id, actor, actor_type, target, target_type, location_x, location_y, location_z, etc_str1, etc_str2, etc_str3, etc_num1, etc_num2, etc_num3, etc_num4, etc_num5, etc_num6, etc_num7, etc_num8, etc_num9, etc_num10, STR_actor, STR_actor_account, STR_target, STR_target_account, item_id) " + "VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?);");

				statement.setInt(1, 0); //serv_id
				statement.setLong(2, System.currentTimeMillis() / 1000); //act_time
				statement.setInt(3, log_id); //log_id

				if(activeObject != null)
				{
					statement.setInt(4, activeObject.isNpc() ? ((L2NpcInstance) activeObject).getNpcId() : activeObject.getObjectId());
					statement.setString(5, activeObject.getClass().getName());
				}
				else
				{
					statement.setInt(4, 0);
					statement.setString(5, "");
				}

				if(target != null && activeObject != null)
				{
					statement.setInt(6, target.isNpc() ? ((L2NpcInstance) target).getNpcId() : target.getObjectId());
					statement.setString(7, target.getClass().getName());
					statement.setInt(8, activeObject.getX()); //location_x,
					statement.setInt(9, activeObject.getY()); //location_y,
					statement.setInt(10, activeObject.getZ()); //location_z,
				}
				else
				{
					statement.setInt(6, 0);
					statement.setString(7, "");
					statement.setInt(8, 0);
					statement.setInt(9, 0);
					statement.setInt(10, 0);
				}

				statement.setString(11, etc_str1); //etc_str1,
				statement.setString(12, etc_str2); //etc_str2
				statement.setString(13, etc_str3); //etc_str3,

				statement.setInt(14, etc_num1); //etc_num1,
				statement.setInt(15, etc_num2); //etc_num2,
				statement.setInt(16, etc_num3); //etc_num3,
				statement.setInt(17, etc_num4); //etc_num4,
				statement.setInt(18, etc_num5); //etc_num5,
				statement.setInt(19, etc_num6); //etc_num6,
				statement.setInt(20, etc_num7); //etc_num7,

				statement.setInt(21, etc_num8);
				statement.setLong(22, etc_num9);
				statement.setLong(23, etc_num10);

				if(activeObject != null)
				{
					statement.setString(24, activeObject.getName()); //STR_actor,
					statement.setString(25, activeObject.isPlayer() ? ((L2Player) activeObject).getAccountName() : ""); //STR_actor_account,
				}
				else
				{
					statement.setString(24, "");
					statement.setString(25, "");
				}

				if(target != null)
				{
					statement.setString(26, target.getName()); //STR_target,
					statement.setString(27, target.isPlayer() ? ((L2Player) target).getAccountName() : ""); //STR_target_account
				}
				else
				{
					statement.setString(26, "");
					statement.setString(27, "");
				}
				statement.setInt(28, Item != null ? Item.getObjectId() : 0); //item_id

				statement.executeUpdate();
			}
			catch(Exception e)
			{
				_log.log(Level.SEVERE, "Could not insert log into DB:", e);
			}
			finally
			{
				DatabaseUtils.closeDatabaseCS(con, statement);
			}
		}
		if(ConfigValue.MainLog)
		{
			StringBuffer msgb = new StringBuffer(160);
			msgb.append("0,"); //serv_id
			msgb.append(System.currentTimeMillis() / 1000 + ",");
			msgb.append(log_id + ",");

			if(activeObject != null)
			{
				msgb.append((activeObject.isNpc() ? activeObject.getNpcId() : activeObject.getObjectId()) + ",");
				msgb.append(activeObject.getClass().getName() + ",");
			}
			else
				msgb.append(",,");

			if(target != null && activeObject != null)
			{
				msgb.append((target.isNpc() ? target.getNpcId() : target.getObjectId()) + ",");
				msgb.append(target.getClass().getName() + ",");

				msgb.append(activeObject.getX() + ","); //location_x,
				msgb.append(activeObject.getY() + ","); //location_y,
				msgb.append(activeObject.getZ() + ","); //location_z,
			}
			else
				msgb.append(",,,,,");

			msgb.append(etc_str1 + ","); //etc_str1,
			msgb.append(etc_str2 + ","); //etc_str2
			msgb.append(etc_str3 + ","); //etc_str3,

			msgb.append(etc_num1 + ","); //etc_num1,
			msgb.append(etc_num2 + ","); //etc_num2,
			msgb.append(etc_num3 + ","); //etc_num3,
			msgb.append(etc_num4 + ","); //etc_num4,
			msgb.append(etc_num5 + ","); //etc_num5,
			msgb.append(etc_num6 + ","); //etc_num6,
			msgb.append(etc_num7 + ","); //etc_num7,

			msgb.append(etc_num8 + ",");
			msgb.append(etc_num9 + ",");
			msgb.append(etc_num10 + ",");

			if(activeObject != null)
			{
				msgb.append(activeObject.getName() + ","); //STR_actor,
				msgb.append((activeObject.isPlayer() ? ((L2Player) activeObject).getAccountName() : "") + ","); //STR_actor_account,
			}
			else
				msgb.append(",,");

			if(target != null)
			{
				msgb.append(target.getName() + ","); //STR_target,
				msgb.append((target.isPlayer() ? ((L2Player) target).getAccountName() : "") + ","); //STR_target_account
			}
			else
				msgb.append(",,");
			msgb.append((Item != null ? Item.getObjectId() : 0) + ";"); //item_id
			_mainLog.info(msgb.toString());
		}
	}

	public static void LogPetition(L2Player fromChar, Integer Petition_type, String Petition_text)
	{
		if(ConfigValue.SqlLog)
		{
			ThreadConnection con = null;
			FiltredPreparedStatement statement = null;
			try
			{
				con = L2DatabaseFactory.getInstance().getConnection();
				statement = con.prepareStatement("INSERT INTO petitions (serv_id, act_time, petition_type, actor, location_x, location_y, location_z, petition_text, STR_actor, STR_actor_account) VALUES (?,?,?,?,?,?,?,?,?,?);");

				statement.setInt(1, 0); //serv_id
				statement.setLong(2, System.currentTimeMillis() / 1000); //act_time
				statement.setInt(3, Petition_type); //log_id
				statement.setInt(4, fromChar.getObjectId());
				statement.setInt(5, fromChar.getX()); //location_x,
				statement.setInt(6, fromChar.getY()); //location_y,
				statement.setInt(7, fromChar.getZ()); //location_z,
				statement.setString(8, Petition_text);
				statement.setString(9, fromChar.getName()); //STR_actor,
				statement.setString(10, fromChar.getAccountName()); //STR_actor_account,
				statement.executeUpdate();
			}
			catch(Exception e)
			{
				_log.log(Level.SEVERE, "Could not insert petition into DB:", e);
			}
			finally
			{
				DatabaseUtils.closeDatabaseCS(con, statement);
			}
		}
		add(fromChar.toFullString() + "|" + Petition_type + "|" + Petition_text, "petitions");
	}

	public static void LoginLog(Integer log_id, String etc_str1, String etc_str2, String etc_str3, Integer etc_num1, Integer etc_num2)
	{
		if(!ConfigValue.SqlLog)
			return;
		ThreadConnection con = null;
		FiltredPreparedStatement statement = null;
		try
		{
			con = L2DatabaseFactory.getInstance().getConnection();
			statement = con.prepareStatement("INSERT INTO loginserv_log (act_time, log_id, etc_str1, etc_str2, etc_str3, etc_num1, etc_num2) " + "VALUES (?,?,?,?,?,?,?);");

			statement.setLong(1, System.currentTimeMillis() / 1000); //act_time
			statement.setInt(2, log_id); //log_id

			statement.setString(3, etc_str1); //etc_str1,
			statement.setString(4, etc_str2); //etc_str2
			statement.setString(5, etc_str3); //etc_str3,

			statement.setInt(6, etc_num1); //etc_num1,
			statement.setInt(7, etc_num2); //etc_num2,

			statement.executeUpdate();
		}
		catch(Exception e)
		{
			_log.log(Level.SEVERE, "Could not insert log into DB:", e);
		}
		finally
		{
			DatabaseUtils.closeDatabaseCS(con, statement);
		}
	}

	public static boolean displayStackTrace(Throwable ex, String etc_str1)
	{
		if(null == ex)
		{
			System.out.println("Null stack trace reference! Bailing...");
			return false;
		}

		if(!etc_str1.equals(""))
			System.out.println(etc_str1 + "\n");

		System.out.println("The stack according to printStackTrace():\n");
		ex.printStackTrace();
		System.out.println("");
		return true;
	}
	
	public static void logItem(String text, String text2, String file)
	{
		try
		{
			throw new java.lang.IllegalAccessError();
		}
		catch(IllegalAccessError e)
		{
			try
			{
				String _log2 = "";
				for(int i = 1;i<e.getStackTrace().length;i++)
				{
					StackTraceElement el = e.getStackTrace()[i];
					if(el != null)
					{
						if(el.getFileName() != null)
							_log2 += el.getFileName().replace(".java", "");
						if(el.getMethodName() != null)
							_log2 += ":"+el.getMethodName();
						_log2 += "("+el.getLineNumber()+")<-";
					}
				}
				add(text+"|"+_log2+"|"+text2, file);
			}
			catch(Exception e1)
			{}
		}
	}

	public static void logTrace(String text_log, String folder_name, String file)
	{
		try
		{
			throw new java.lang.IllegalAccessError();
		}
		catch(IllegalAccessError e)
		{
			try
			{
				String _log2 = "";
				for(int i = 1;i<e.getStackTrace().length;i++)
				{
					StackTraceElement el = e.getStackTrace()[i];
					if(el != null)
					{
						if(el.getFileName() != null)
							_log2 += el.getFileName().replace(".java", "");
						if(el.getMethodName() != null)
							_log2 += ":"+el.getMethodName();
						_log2 += "("+el.getLineNumber()+")<-";
					}
				}
				addMy(text_log+"|"+_log2, folder_name , file);
			}
			catch(Exception e1)
			{}
		}
	}

	public static void logTrace2(Exception e, String text_log, String folder_name, String file)
	{
			try
			{
				String _log2 = "";
				for(int i = 1;i<e.getStackTrace().length;i++)
				{
					StackTraceElement el = e.getStackTrace()[i];
					if(el != null)
					{
						if(el.getFileName() != null)
							_log2 += el.getFileName().replace(".java", "");
						if(el.getMethodName() != null)
							_log2 += ":"+el.getMethodName();
						_log2 += "("+el.getLineNumber()+")<-";
					}
				}
				addMy(text_log+"|"+_log2, folder_name , file);
			}
			catch(Exception e1)
			{}
	}

	/*
	 public static final void LogItem(L2Player fromChar, Integer action_type, L2ItemInstance Item)
	 {
	 LogItem(fromChar, null, action_type, Item, 0);
	 }

	 public static final void LogItem(L2Player fromChar, Integer action_type, L2ItemInstance Item, Integer count)
	 {
	 LogItem(fromChar, null, action_type, Item, count);
	 }

	 public static final void LogItem(L2Player fromChar, L2Player toChar, Integer action_type, L2ItemInstance Item, Integer count)
	 {
	 if(!(fromChar.isGM() || Config.LOG_ITEMS))
	 return;

	 StringBuffer msgb = new StringBuffer(160);

	 if(fromChar.isGM()) msgb.append("GM ");
	 msgb.append(fromChar.toFullString());

	 switch (action_type)
	 {
	 case ITEM_FROM_MOB:
	 msgb.append(" get item from mob");
	 break;
	 case ITEM_BUY:
	 msgb.append(" buy item");
	 break;
	 case ITEM_MAKE:
	 msgb.append(" make item");
	 break;
	 case ITEM_CREATE:
	 msgb.append(" create item");
	 break;
	 case ITEM_TRADE:
	 msgb.append(" trade item with ").append(toChar.getName());
	 break;
	 case ITEM_PICKUP:
	 msgb.append(" pickup item");
	 break;
	 case ITEM_DROP:
	 msgb.append(" drop item");
	 break;
	 case ITEM_DESTROY:
	 msgb.append(" destroy item");
	 break;
	 case ITEM_CRYSTALIZE:
	 msgb.append(" crystalize item");
	 break;
	 case ITEM_SELL:
	 msgb.append(" sell item price:").append(count);
	 break;
	 }
	 msgb.append(" Item: ")
	 .append(Item.getItem().getName())
	 .append(" (")
	 .append(Item.getItemId())
	 .append(") ObjectId:")
	 .append(Item.getObjectId())
	 .append(" count:")
	 .append(Item.getCount());
	 _logItems.info(msgb.toString());
	 if(fromChar.isGM()) _logGm.info(msgb.toString());

	 msgb=null;
	 }
	 */

}