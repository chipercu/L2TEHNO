package commands.admin;

import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import l2open.config.ConfigValue;
import l2open.common.ThreadPoolManager;
import l2open.database.*;
import l2open.extensions.multilang.CustomMessage;
import l2open.extensions.scripts.ScriptFile;
import l2open.gameserver.Announcements;
import l2open.gameserver.cache.Msg;
import l2open.gameserver.communitybbs.Manager.ClassBBSManager;
import l2open.gameserver.handler.AdminCommandHandler;
import l2open.gameserver.handler.IAdminCommandHandler;
import l2open.gameserver.model.L2Object;
import l2open.gameserver.model.L2ObjectsStorage;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.L2SubClass;
import l2open.gameserver.model.L2World;
import l2open.gameserver.model.base.ClassId;
import l2open.gameserver.model.base.PlayerClass;
import l2open.gameserver.model.base.Race;
import l2open.gameserver.model.entity.Hero;
import l2open.gameserver.model.entity.olympiad.Olympiad;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.gameserver.tables.ClanTable;
import l2open.gameserver.tables.player.PlayerData;
import l2open.gameserver.skills.Stats;
import l2open.gameserver.serverpackets.*;
import l2open.util.*;

import java.util.*;
import java.sql.ResultSet;
import java.sql.SQLException;

@SuppressWarnings("unused")
public class AdminEditChar implements IAdminCommandHandler, ScriptFile
{
	private static enum Commands
	{
		admin_edit_character,
		admin_character_actions,
		admin_current_player,
		admin_nokarma,
		admin_setkarma,
		admin_character_list,
		admin_show_characters,
		admin_find_character,
		admin_save_modifications,
		admin_rec,
		admin_setclass,
		admin_settitle,
		admin_setname,
		admin_setsex,
		admin_setcolor,
		admin_add_exp_sp_to_character,
		admin_add_exp_sp,
		admin_sethero,
		admin_setnoble,
		admin_trans,
		admin_setsubclass,
		admin_setfame,
		admin_setbday,
		admin_resists,
		admin_set_bang,
		admin_add_bang,
		admin_setteam,
		admin_clan_war,
		admin_hinfo,
		admin_flag,
		admin_reset_waits,
		admin_seeradius
	}

	@Override
	public boolean useAdminCommand(Enum comm, String[] wordList, String fullString, L2Player activeChar)
	{
		Commands command = (Commands) comm;

		if(fullString.startsWith("admin_seeradius"))
		{
			int radius = -1;
			try
			{
				radius = Integer.parseInt(wordList[1]);
			}
			catch(Exception e)
			{}
			admin_hinfo(activeChar, wordList.length < 3 ? null : wordList[2], radius);
		}
		else if(fullString.startsWith("admin_reset_waits"))
		{
			int radius = 1000;
			try
			{
				radius = Integer.parseInt(fullString.substring(18));
			}
			catch(Exception e)
			{}
			for(L2Player player : L2World.getAroundPlayers(activeChar, radius, 200, false))
				if(player != null)
					player.setLeaveClanTime(0);
			return true;
		}
		else if(activeChar.getPlayerAccess().CanRename)
			if(fullString.startsWith("admin_settitle"))
				try
				{
					String val = fullString.substring(15);
					L2Object target = activeChar.getTarget();
					L2Player player = null;
					if(target == null)
						return false;
					if(target.isPlayer())
					{
						player = (L2Player) target;
						player.setTitle(val);
						player.sendMessage("Your title has been changed by a GM");
						player.sendChanges();
					}
					else if(target.isNpc())
					{
						((L2NpcInstance) target).setTitle(val);
						target.decayMe();
						target.spawnMe();
					}

					return true;
				}
				catch(StringIndexOutOfBoundsException e)
				{ // Case of empty character title
					activeChar.sendMessage("You need to specify the new title.");
					return false;
				}
			else if(fullString.startsWith("admin_setname"))
				try
				{
					String val = fullString.substring(14);
					L2Object target = activeChar.getTarget();
					L2Player player;
					if(target != null && target.isPlayer())
						player = (L2Player) target;
					else
						return false;
					if(mysql.simple_get_int("count(*)", "characters", "`char_name` like '" + val + "'") > 0)
					{
						activeChar.sendMessage("Name already exist.");
						return false;
					}
					Log.add("Character " + player.getName() + " renamed to " + val + " by GM " + activeChar.getName(), "renames");
					player.reName(val);
					player.sendMessage("Your name has been changed by a GM");
					return true;
				}
				catch(StringIndexOutOfBoundsException e)
				{ // Case of empty character name
					activeChar.sendMessage("You need to specify the new name.");
					return false;
				}

		if(!activeChar.getPlayerAccess().CanEditChar && !activeChar.getPlayerAccess().CanViewChar)
			return false;
	
		if(fullString.startsWith("admin_flag"))
		{
			int radius = Integer.parseInt(fullString.substring(11));
			for(L2Player player : L2World.getAroundPlayers(activeChar, radius, 200, false))
				if(player != null && !player.isDead() && !player.isGM())
					player.startPvPFlag(null);
		}
		else if(fullString.startsWith("admin_hinfo"))
			admin_hinfo(activeChar, wordList.length == 1 ? null : wordList[1], -1);
		else if(fullString.equals("admin_current_player"))
			showCharacterList(activeChar, null);
		else if(fullString.startsWith("admin_character_list"))
			try
			{
				String val = fullString.substring(21);
				L2Player target = L2ObjectsStorage.getPlayer(val);
				showCharacterList(activeChar, target);
			}
			catch(StringIndexOutOfBoundsException e)
			{
				// Case of empty character name
			}
		else if(fullString.startsWith("admin_show_characters"))
			try
			{
				String val = fullString.substring(22);
				int page = Integer.parseInt(val);
				listCharacters(activeChar, page);
			}
			catch(StringIndexOutOfBoundsException e)
			{
				// Case of empty page
			}
		else if(fullString.startsWith("admin_find_character"))
			try
			{
				String val = fullString.substring(21);
				findCharacter(activeChar, val);
			}
			catch(StringIndexOutOfBoundsException e)
			{ // Case of empty character name
				activeChar.sendMessage("You didnt enter a character name to find.");

				listCharacters(activeChar, 0);
			}
		else if(!activeChar.getPlayerAccess().CanEditChar)
			return false;
		else if(fullString.equals("admin_edit_character"))
			editCharacter(activeChar);
		else if(fullString.equals("admin_character_actions"))
			showCharacterActions(activeChar);
		else if(fullString.equals("admin_nokarma"))
			setTargetKarma(activeChar, 0);
		else if(fullString.startsWith("admin_setkarma"))
			try
			{
				String val = fullString.substring(15);
				int karma = Integer.parseInt(val);
				setTargetKarma(activeChar, karma);
			}
			catch(StringIndexOutOfBoundsException e)
			{
				activeChar.sendMessage("Please specify new karma value.");
			}
		else if(fullString.startsWith("admin_save_modifications"))
			try
			{
				String val = fullString.substring(24);
				adminModifyCharacter(activeChar, val);
			}
			catch(StringIndexOutOfBoundsException e)
			{ // Case of empty character name
				activeChar.sendMessage("Error while modifying character.");
				listCharacters(activeChar, 0);
			}
		else if(fullString.equals("admin_rec"))
		{
			L2Object target = activeChar.getTarget();
			L2Player player = null;
			if(target != null && target.isPlayer())
				player = (L2Player) target;
			else
				return false;
			player.getRecommendation().addRecomHave(1);
			player.sendMessage("You have been recommended by a GM");
			player.broadcastUserInfo(true);
		}
		else if(fullString.startsWith("admin_rec"))
			try
			{
				String val = fullString.substring(10);
				int recVal = Integer.parseInt(val);
				L2Object target = activeChar.getTarget();
				L2Player player = null;
				if(target != null && target.isPlayer())
					player = (L2Player) target;
				else
					return false;
				player.getRecommendation().addRecomHave(recVal);
				player.sendMessage("You have been recommended by a GM");
				player.broadcastUserInfo(true);
			}
			catch(NumberFormatException e)
			{
				activeChar.sendMessage("Command format is //rec <number>");
			}
		else if(fullString.startsWith("admin_setclass"))
		{
			try
			{
				String val = fullString.substring(15);
				int classidval = 0;
				try
				{
					classidval = Integer.parseInt(val);
				}
				catch(NumberFormatException e)
				{
					classidval = Integer.parseInt(val.substring(1));
				}
				L2Object target = activeChar.getTarget();
				L2Player player = null;
				if(target instanceof L2Player)
					player = (L2Player) target;
				else
					return false;
				boolean valid = false;
				for(ClassId classid : ClassId.values())
					if(classidval == classid.getId())
						valid = true;
				if(valid && (player.getClassId().getId() != classidval))
				{
					player.setClassId(classidval, true);
					if(player.getClassId().getLevel() == 4)
						ClassBBSManager.ClassBBSManagerAddReward(player);
					if(!player.isSubClassActive())
						player.setBaseClass(classidval);
					String newclass = player.getTemplate().className;
					/*player.store();*/
					if (player != activeChar)
						player.sendMessage("A GM changed your class to " + newclass);
					player.broadcastUserInfo(true);
					activeChar.sendMessage(player.getName() + " is a " + newclass);
					player.setBaseTemplate(player.getTemplate());
					player.setTransformation(105);
					ThreadPoolManager.getInstance().schedule(new Untransform(player), 200);
				}
				activeChar.sendMessage("Usage: //setclass <valid_new_classid>");
			}
			catch(StringIndexOutOfBoundsException e)
			{
				AdminHelpPage.showHelpPage(activeChar, "charclasses.htm");
			}
		}
		else if(fullString.startsWith("admin_sethero"))
		{
			// Статус меняется только на текущую логон сессию
			L2Object target = activeChar.getTarget();
			L2Player player;
			if(wordList.length > 1 && wordList[1] != null)
			{
				player = L2ObjectsStorage.getPlayer(wordList[1]);
				if(player == null)
				{
					activeChar.sendMessage("Character " + wordList[1] + " not found in game.");
					return false;
				}
			}
			else if(target != null && target.isPlayer())
				player = (L2Player) target;
			else
			{
				activeChar.sendMessage("You must specify the name or target character.");
				return false;
			}

			if(player.isHero())
			{
				player.setHero(false, -1);
				player.updatePledgeClass();
				Hero.removeSkills(player);
			}
			else
			{
				player.setHero(true, 0);
				player.updatePledgeClass();
				Hero.addSkills(player);
			}

			player.sendPacket(new SkillList(player));

			if(player.isHero())
			{
				player.broadcastPacket(new SocialAction(player.getObjectId(), 16));
				Announcements.getInstance().announceToAll(player.getName() + " has become a hero.");
			}
			player.sendMessage("Admin changed your hero status.");
			player.broadcastUserInfo(true);
		}
		else if(fullString.startsWith("admin_setnoble"))
		{
			// Статус сохраняется в базе
			L2Object target = activeChar.getTarget();
			L2Player player;
			if(wordList.length > 1 && wordList[1] != null)
			{
				player = L2ObjectsStorage.getPlayer(wordList[1]);
				if(player == null)
				{
					activeChar.sendMessage("Character " + wordList[1] + " not found in game.");
					return false;
				}
			}
			else if(target != null && target.isPlayer())
				player = (L2Player) target;
			else
			{
				activeChar.sendMessage("You must specify the name or target character.");
				return false;
			}

			if(player.isNoble())
			{
				Olympiad.removeNoble(player);
				player.setNoble(false);
				player.sendMessage("Admin changed your noble status, now you are not nobless.");
			}
			else
			{
				Olympiad.addNoble(player);
				player.setNoble(true);
				player.broadcastPacket(new SocialAction(player.getObjectId(), SocialAction.VICTORY));
				player.sendMessage("Admin changed your noble status, now you nobless.");
			}

			player.updatePledgeClass();
			player.updateNobleSkills();
			player.sendPacket(new SkillList(player));
			player.broadcastUserInfo(true);
		}
		else if(fullString.startsWith("admin_setsex"))
		{
			L2Object target = activeChar.getTarget();
			L2Player player = null;
			if(target != null && target.isPlayer())
				player = (L2Player) target;
			else
				return false;
			player.changeSex();
			player.sendMessage("Your gender has been changed by a GM");
			player.broadcastUserInfo(true);
		}
		else if(fullString.startsWith("admin_setcolor"))
			try
			{
				String val = fullString.substring(15);
				L2Object target = activeChar.getTarget();
				L2Player player = null;
				if(target != null && target.isPlayer())
					player = (L2Player) target;
				else
					return false;
				player.setNameColor(Integer.decode("0x" + val));
				player.sendMessage("Your name color has been changed by a GM");
				player.broadcastUserInfo(true);
			}
			catch(StringIndexOutOfBoundsException e)
			{ // Case of empty color
				activeChar.sendMessage("You need to specify the new color.");
			}
		else if(fullString.startsWith("admin_add_exp_sp_to_character"))
			addExpSp(activeChar);
		else if(fullString.startsWith("admin_add_exp_sp"))
			try
			{
				final String val = fullString.substring(16);
				adminAddExpSp(activeChar, val);
			}
			catch(final StringIndexOutOfBoundsException e)
			{ // Case of empty character name
				activeChar.sendMessage("Error while adding Exp-Sp.");
			}
		else if(fullString.startsWith("admin_trans"))
		{
			StringTokenizer st = new StringTokenizer(fullString);
			if(st.countTokens() > 1)
			{
				st.nextToken();
				int transformId = 0;
				try
				{
					transformId = Integer.parseInt(st.nextToken());
				}
				catch(Exception e)
				{
					activeChar.sendMessage("Specify a valid integer value.");
					return false;
				}
				if(transformId != 0 && activeChar.getTransformation() != 0)
				{
					activeChar.sendPacket(Msg.YOU_ALREADY_POLYMORPHED_AND_CANNOT_POLYMORPH_AGAIN);
					return false;
				}
				activeChar.setTransformation(transformId);
				activeChar.sendMessage("Transforming...");
			}
			else
				activeChar.sendMessage("Usage: //trans <ID>");
		}
		else if(fullString.startsWith("admin_setsubclass"))
		{
			final L2Object target = activeChar.getTarget();
			if(target == null || !target.isPlayer())
			{
				activeChar.sendPacket(Msg.SELECT_TARGET);
				return false;
			}
			final L2Player player = (L2Player) target;

			StringTokenizer st = new StringTokenizer(fullString);
			if(st.countTokens() > 1)
			{
				st.nextToken();
				short classId = Short.parseShort(st.nextToken());
				if(!PlayerData.getInstance().addSubClass(player, classId, true, 0, false))
				{
					activeChar.sendMessage(new CustomMessage("l2open.gameserver.model.instances.L2VillageMasterInstance.SubclassCouldNotBeAdded", activeChar));
					return false;
				}
				player.sendPacket(Msg.CONGRATULATIONS_YOU_HAVE_TRANSFERRED_TO_A_NEW_CLASS); // Transfer to new class.
			}
			else
				setSubclass(activeChar, player);
		}
		else if(fullString.startsWith("admin_setfame"))
			try
			{
				String val = fullString.substring(14);
				int fame = Integer.parseInt(val);
				setTargetFame(activeChar, fame);
			}
			catch(StringIndexOutOfBoundsException e)
			{
				activeChar.sendMessage("Please specify new fame value.");
			}
		else if(fullString.startsWith("admin_setbday"))
		{
			String msgUsage = "Usage: //setbday YYYY-MM-DD";
			String date = fullString.substring(14);
			if(date.length() != 10 || !Util.isMatchingRegexp(date, "[0-9]{4}-[0-9]{2}-[0-9]{2}"))
			{
				activeChar.sendMessage(msgUsage);
				return false;
			}

			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
			try
			{
				dateFormat.parse(date);
			}
			catch(ParseException e)
			{
				activeChar.sendMessage(msgUsage);
			}

			if(activeChar.getTarget() == null || !activeChar.getTarget().isPlayer())
			{
				activeChar.sendMessage("Please select a character.");
				return false;
			}

			if(!mysql.set("update characters set createtime = UNIX_TIMESTAMP('" + date + "') where obj_Id = " + activeChar.getTarget().getObjectId()))
			{
				activeChar.sendMessage(msgUsage);
				return false;
			}

			activeChar.sendMessage("New Birthday for " + activeChar.getTarget().getName() + ": " + date);
			activeChar.getTarget().getPlayer().sendMessage("Admin changed your birthday to: " + date);
		}
		else if(fullString.startsWith("admin_resists"))
			resists(activeChar.getTarget().getPlayer(), activeChar);
		else if(fullString.startsWith("admin_set_bang"))
		{
			if(!ConfigValue.AltPcBangPointsEnabled)
			{
				activeChar.sendMessage("Error! Pc Bang Points service disabled!");
				return true;
			}
			if(wordList.length < 1)
			{
				activeChar.sendMessage("Usage: //set_bang count <target>");
				return false;
			}
			int count = Integer.parseInt(wordList[1]);
			if(count < 1 || activeChar.getTarget() == null || !activeChar.getTarget().isPlayer())
			{
				activeChar.sendMessage("Usage: //set_bang count <target>");
				return false;
			}
			L2Player target = activeChar.getTarget().getPlayer();
			target.setPcBangPoints(count);
			target.sendMessage("Your Pc Bang Points count is now " + count);
			target.sendPacket(new ExPCCafePointInfo(target.getPcBangPoints(), count, 1, 2, 12));
			activeChar.sendMessage("You have set " + target.getName() + "'s Pc Bang Points to " + count);
		}
		else if(fullString.startsWith("admin_add_bang"))
		{
			if(!ConfigValue.AltPcBangPointsEnabled)
			{
				activeChar.sendMessage("Error! Pc Bang Points service disabled!");
				return true;
			}
			if(wordList.length < 1)
			{
				activeChar.sendMessage("Usage: //add_bang count <target>");
				return false;
			}
			int count = Integer.parseInt(wordList[1]);
			if(count < 1 || activeChar.getTarget() == null || !activeChar.getTarget().isPlayer())
			{
				activeChar.sendMessage("Usage: //add_bang count <target>");
				return false;
			}
			L2Player target = activeChar.getTarget().getPlayer();
			target.addPcBangPoints(count, false, 2);
			activeChar.sendMessage("You have added " + count + " Pc Bang Points to " + target.getName());
		}
		else if(fullString.startsWith("admin_setteam"))
		{
			if(activeChar.getTarget() == null || !activeChar.getTarget().isPlayer())
			{
				activeChar.sendPacket(Msg.INVALID_TARGET);
				activeChar.sendMessage("Usage: //setteam <team ID 2-red, 1-blu>");
				return false;
			}
			if(wordList.length >= 2)
			{
				if(wordList[1].equalsIgnoreCase("red") || wordList[1].equalsIgnoreCase("2"))
					activeChar.getTarget().getPlayer().setTeam(2, true);
				else
					activeChar.getTarget().getPlayer().setTeam(1, true);
			}
			else
				activeChar.getTarget().getPlayer().setTeam(0, false);
		}
		else if(fullString.startsWith("admin_clan_war"))
			ClanTable.getInstance().startClanWar(activeChar.getClan(), activeChar.getTarget().getPlayer().getClan());
		return true;
	}

	@Override
	public Enum[] getAdminCommandEnum()
	{
		return Commands.values();
	}

	private void listCharacters(L2Player activeChar, int page)
	{
		List<L2Player> players = new ArrayList<L2Player>(L2ObjectsStorage.getPlayers());

		int MaxCharactersPerPage = 20;
		int MaxPages = players.size() / MaxCharactersPerPage;

		if(players.size() > MaxCharactersPerPage * MaxPages)
			MaxPages++;

		// Check if number of users changed
		if(page > MaxPages)
			page = MaxPages;

		int CharactersStart = MaxCharactersPerPage * page;
		int CharactersEnd = players.size();
		if(CharactersEnd - CharactersStart > MaxCharactersPerPage)
			CharactersEnd = CharactersStart + MaxCharactersPerPage;

		NpcHtmlMessage adminReply = new NpcHtmlMessage(5);

		StringBuffer replyMSG = new StringBuffer("<html><body>");
		replyMSG.append("<table width=260><tr>");
		replyMSG.append("<td width=40><button value=\"Main\" action=\"bypass -h admin_admin\" width=40 height=15 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\"></td>");
		replyMSG.append("<td width=180><center>Character Selection Menu</center></td>");
		replyMSG.append("<td width=40><button value=\"Back\" action=\"bypass -h admin_admin\" width=40 height=15 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\"></td>");
		replyMSG.append("</tr></table>");
		replyMSG.append("<br><br>");
		replyMSG.append("<table width=270>");
		replyMSG.append("<tr><td width=270>You can find a character by writing his name and</td></tr>");
		replyMSG.append("<tr><td width=270>clicking Find bellow.<br></td></tr>");
		replyMSG.append("<tr><td width=270>Note: Names should be written case sensitive.</td></tr>");
		replyMSG.append("</table><br>");
		replyMSG.append("<center><table><tr><td>");
		replyMSG.append("<edit var=\"character_name\" width=80></td><td><button value=\"Find\" action=\"bypass -h admin_find_character $character_name\" width=40 height=15 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\">");
		replyMSG.append("</td></tr></table></center><br><br>");

		for(int x = 0; x < MaxPages; x++)
		{
			int pagenr = x + 1;
			replyMSG.append("<center><a action=\"bypass -h admin_show_characters " + x + "\">Page " + pagenr + "</a></center>");
		}
		replyMSG.append("<br>");

		// List Players in a Table
		replyMSG.append("<table width=270>");
		replyMSG.append("<tr><td width=80>Name:</td><td width=110>Class:</td><td width=40>Level:</td></tr>");
		for(int i = CharactersStart; i < CharactersEnd; i++)
		{
			L2Player p = players.get(i);
			replyMSG.append("<tr><td width=80>" + "<a action=\"bypass -h admin_character_list " + p.getName() + "\">" + p.getName() + "</a></td><td width=110>" + p.getTemplate().className + "</td><td width=40>" + p.getLevel() + "</td></tr>");
		}
		replyMSG.append("</table>");
		replyMSG.append("</body></html>");

		adminReply.setHtml(replyMSG.toString());
		activeChar.sendPacket(adminReply);
	}

	public static void showCharacterList(L2Player activeChar, L2Player player)
	{
		if(player == null)
		{
			L2Object target = activeChar.getTarget();
			if(target != null && target.isPlayer())
				player = (L2Player) target;
			else
				return;
		}
		else
			activeChar.setTarget(player);

		String clanName = "No Clan";
		if(player.getClan() != null)
			clanName = player.getClan().getName() + "/" + player.getClan().getLevel();

		StringBuffer replyMSG = new StringBuffer();
		{
			replyMSG.append("<html><body><br>");
			NumberFormat df = NumberFormat.getNumberInstance(Locale.ENGLISH);
			df.setMaximumFractionDigits(4);
			df.setMinimumFractionDigits(1);

			NpcHtmlMessage adminReply = new NpcHtmlMessage(5);

			replyMSG.append("<table width=260><tr>");
			replyMSG.append("<td width=40><button value=\"Main\" action=\"bypass -h admin_admin\" width=40 height=15 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\"></td>");
			replyMSG.append("<td width=180><center>Character Selection Menu</center></td>");
			replyMSG.append("<td width=40><button value=\"Back\" action=\"bypass -h admin_show_characters 0\" width=40 height=15 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\"></td>");
			replyMSG.append("</tr></table><br>");

			replyMSG.append("<table<tr>");
			replyMSG.append("<td><button value=\"Go To\" action=\"bypass -h admin_goto_char_menu " + player.getName() + "\" width=80 height=15 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\"></td>");
			replyMSG.append("<td><button value=\"Recall\" action=\"bypass -h admin_recall_char_menu " + player.getName() + "\" width=80 height=15 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\"></td>");
			replyMSG.append("<td><button value=\"Set Noble\" action=\"bypass -h admin_setnoble\" width=80 height=15 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\"></td>");
			replyMSG.append("</tr><tr>");
			replyMSG.append("<td><button value=\"Skills\" action=\"bypass -h admin_show_skills\" width=80 height=15 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\"></td>");
			replyMSG.append("<td><button value=\"Effects\" action=\"bypass -h admin_show_effects\" width=80 height=15 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\"></td>");
			replyMSG.append("<td><button value=\"Actions\" action=\"bypass -h admin_character_actions\" width=80 height=15 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\"></td>");
			replyMSG.append("</tr><tr>");
			replyMSG.append("<td><button value=\"Stats\" action=\"bypass -h admin_edit_character\" width=80 height=15 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\"></td>");
			replyMSG.append("<td><button value=\"Exp & Sp\" action=\"bypass -h admin_add_exp_sp_to_character\" width=80 height=15 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\"></td>");
			replyMSG.append("<td><button value=\"Class\" action=\"bypass -h admin_setclass\" width=80 height=15 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\"></td>");
			replyMSG.append("</tr><tr>");
			replyMSG.append("<td><button value=\"Show Resists\" action=\"bypass -h admin_resists \" width=80 height=15 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\"></td>");
			replyMSG.append("<td><button value=\"Reset Skill\" action=\"bypass -h admin_reset_skill_cool " + player.getName() + "\" width=80 height=15 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\"></td>");
			replyMSG.append("<td><button value=\"All Windows\" action=\"bypass -h admin_hinfo " + player.getName() + "\" width=80 height=15 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\"></td>");
			replyMSG.append("<td></td>");
			replyMSG.append("</tr><tr>");
			replyMSG.append("<td><button value=\"Select Acc\" action=\"bypass -h admin_pass_sc "+player.getAccountName()+"\" width=80 height=15 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\"></td>");
			replyMSG.append("<td><button value=\"Select Pass\" action=\"bypass -h admin_pass_srca 0 "+player.getAccountName()+"\" width=80 height=15 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\"></td>");
			replyMSG.append("<td></td>");
			replyMSG.append("<td></td>");
			replyMSG.append("</tr></table>");

			replyMSG.append("<table width=270>");
			replyMSG.append("<tr><td width=100>AI:</td><td>" + player.getAI()+"</td></tr>");
			replyMSG.append("<tr><td width=100>Account/IP:</td><td>" + player.getAccountName() + "/" + player.getIP() + "</td></tr>");
			replyMSG.append("<tr><td width=100>HWID:</td><td>" + player.getHWIDs() + "</td></tr>");
			replyMSG.append("<tr><td width=100>Name/Level:</td><td>" + player.getName() + "/" + player.getLevel() + "</td></tr>");
			replyMSG.append("<tr><td width=100>Class/Id:</td><td>" + player.getTemplate().className + "/" + player.getClassId().getId() + "</td></tr>");
			replyMSG.append("<tr><td width=100>Clan/Level:</td><td>" + clanName + "</td></tr>");
			replyMSG.append("<tr><td width=100>Exp/Sp:</td><td>" + player.getExp() + "/" + player.getSp() + "</td></tr>");
			replyMSG.append("<tr><td width=100>Cur/Max Hp:</td><td>" + (int) player.getCurrentHp() + "/" + player.getMaxHp() + "</td></tr>");
			replyMSG.append("<tr><td width=100>Cur/Max Mp:</td><td>" + (int) player.getCurrentMp() + "/" + player.getMaxMp() + "</td></tr>");
			replyMSG.append("<tr><td width=100>Cur/Max Load:</td><td>" + player.getCurrentLoad() + "/" + player.getMaxLoad() + "</td></tr>");
			replyMSG.append("<tr><td width=100>Patk/Matk:</td><td>" + player.getPAtk(null) + "/" + player.getMAtk(null, null) + "</td></tr>");
			replyMSG.append("<tr><td width=100>Pdef/Mdef:</td><td>" + player.getPDef(null) + "/" + player.getMDef(null, null) + "</td></tr>");
			replyMSG.append("<tr><td width=100>PAtkSpd/MAtkSpd:</td><td>" + player.getPAtkSpd() + "/" + player.getMAtkSpd() + "</td></tr>");
			replyMSG.append("<tr><td width=100>Acc/Evas:</td><td>" + player.getAccuracy() + "/" + player.getEvasionRate(null) + "</td></tr>");
			replyMSG.append("<tr><td width=100>Crit/MCrit:</td><td>" + player.getCriticalHit(null, null) + "/" + df.format(player.getMagicCriticalRate(null, null)) + "%</td></tr>");
			replyMSG.append("<tr><td width=100>Walk/Run:</td><td>" + player.getWalkSpeed() + "/" + player.getRunSpeed() + "</td></tr>");
			replyMSG.append("<tr><td width=100>Karma/Fame:</td><td>" + player.getKarma() + "/" + player.getFame() + "</td></tr>");
			replyMSG.append("<tr><td width=100>PvP/PK:</td><td>" + player.getPvpKills() + "/" + player.getPkKills() + "</td></tr>");
			replyMSG.append("<tr><td width=100>Coordinates:</td><td>" + player.getX() + "," + player.getY() + "," + player.getZ() + "</td></tr>");
			replyMSG.append("<tr><td width=100>Distance:</td><td>" + activeChar.getRealDistance(player) + "</td></tr>");
			replyMSG.append("</table><br>");

			replyMSG.append("</body></html>");

			adminReply.setHtml(replyMSG.toString());
			activeChar.sendPacket(adminReply);
		}
	}

	public static void resists(L2Player player, L2Player activeChar)
	{
		if(player == null)
			return;

		NpcHtmlMessage adminReply = new NpcHtmlMessage(5);
		StringBuilder dialog = new StringBuilder("<html><body><center><font color=\"LEVEL\">");
		dialog.append(player.getName()).append("<br></font></center><table width=\"80%\">");

		if(player.getTraitStat().trait_sword != 1)
			dialog.append("<tr><td>trait_sword: </td><td>").append(player.getTraitStat().trait_sword*-1).append("</td></tr>");
		if(player.getTraitStat().trait_blunt != 1)
			dialog.append("<tr><td>trait_blunt: </td><td>").append(player.getTraitStat().trait_blunt*-1).append("</td></tr>");
		if(player.getTraitStat().trait_dagger != 1)
			dialog.append("<tr><td>trait_dagger: </td><td>").append(player.getTraitStat().trait_dagger*-1).append("</td></tr>");
		if(player.getTraitStat().trait_bow != 1)
			dialog.append("<tr><td>trait_bow: </td><td>").append(player.getTraitStat().trait_bow*-1).append("</td></tr>");
		if(player.getTraitStat().trait_pole != 1)
			dialog.append("<tr><td>trait_pole: </td><td>").append(player.getTraitStat().trait_pole*-1).append("</td></tr>");
		if(player.getTraitStat().trait_fist != 1)
			dialog.append("<tr><td>trait_fist: </td><td>").append(player.getTraitStat().trait_fist*-1).append("</td></tr>");
		if(player.getTraitStat().trait_dual != 1)
			dialog.append("<tr><td>trait_dual: </td><td>").append(player.getTraitStat().trait_dual*-1).append("</td></tr>");
		if(player.getTraitStat().trait_dualfist != 1)
			dialog.append("<tr><td>trait_dualfist: </td><td>").append(player.getTraitStat().trait_dualfist*-1).append("</td></tr>");
		if(player.getTraitStat().trait_rapier != 1)
			dialog.append("<tr><td>trait_rapier: </td><td>").append(player.getTraitStat().trait_rapier*-1).append("</td></tr>");
		if(player.getTraitStat().trait_crossbow != 1)
			dialog.append("<tr><td>trait_crossbow: </td><td>").append(player.getTraitStat().trait_crossbow*-1).append("</td></tr>");
		if(player.getTraitStat().trait_ancientsword != 1)
			dialog.append("<tr><td>trait_ancientsword: </td><td>").append(player.getTraitStat().trait_ancientsword*-1).append("</td></tr>");
		if(player.getTraitStat().trait_dualdagger != 1)
			dialog.append("<tr><td>trait_dualdagger: </td><td>").append(player.getTraitStat().trait_dualdagger*-1).append("</td></tr>");

		if(player.getTraitStat().trait_bleed != 0)
			dialog.append("<tr><td>trait_bleed: </td><td>").append(player.getTraitStat().trait_bleed).append("</td></tr>");
		if(player.getTraitStat().trait_poison != 0)
			dialog.append("<tr><td>trait_poison: </td><td>").append(player.getTraitStat().trait_poison).append("</td></tr>");
		if(player.getTraitStat().trait_shock != 0)
			dialog.append("<tr><td>trait_shock: </td><td>").append(player.getTraitStat().trait_shock).append("</td></tr>");
		if(player.getTraitStat().trait_hold != 0)
			dialog.append("<tr><td>trait_hold: </td><td>").append(player.getTraitStat().trait_hold).append("</td></tr>");
		if(player.getTraitStat().trait_sleep != 0)
			dialog.append("<tr><td>trait_sleep: </td><td>").append(player.getTraitStat().trait_sleep).append("</td></tr>");
		if(player.getTraitStat().trait_paralyze != 0)
			dialog.append("<tr><td>trait_paralyze: </td><td>").append(player.getTraitStat().trait_paralyze).append("</td></tr>");
		if(player.getTraitStat().trait_derangement != 0)
			dialog.append("<tr><td>trait_derangement: </td><td>").append(player.getTraitStat().trait_derangement).append("</td></tr>");
			
		if(player.getTraitStat().trait_bleed_power != 0)
			dialog.append("<tr><td>trait_bleed_power: </td><td>").append(player.getTraitStat().trait_bleed_power).append("</td></tr>");
		if(player.getTraitStat().trait_poison_power != 0)
			dialog.append("<tr><td>trait_poison_power: </td><td>").append(player.getTraitStat().trait_poison_power).append("</td></tr>");
		if(player.getTraitStat().trait_shock_power != 0)
			dialog.append("<tr><td>trait_shock_power: </td><td>").append(player.getTraitStat().trait_shock_power).append("</td></tr>");
		if(player.getTraitStat().trait_hold_power != 0)
			dialog.append("<tr><td>trait_hold_power: </td><td>").append(player.getTraitStat().trait_hold_power).append("</td></tr>");
		if(player.getTraitStat().trait_sleep_power != 0)
			dialog.append("<tr><td>trait_sleep_power: </td><td>").append(player.getTraitStat().trait_sleep_power).append("</td></tr>");
		if(player.getTraitStat().trait_paralyze_power != 0)
			dialog.append("<tr><td>trait_paralyze_power: </td><td>").append(player.getTraitStat().trait_paralyze_power).append("</td></tr>");
		if(player.getTraitStat().trait_derangement_power != 0)
			dialog.append("<tr><td>trait_derangement_power: </td><td>").append(player.getTraitStat().trait_derangement_power).append("</td></tr>");



		int FIRE_RECEPTIVE = (int) player.calcStat(Stats.FIRE_RECEPTIVE, 0, null, null);
		if(FIRE_RECEPTIVE != 0)
			dialog.append("<tr><td>Fire</td><td>").append(-FIRE_RECEPTIVE).append("</td></tr>");

		int WIND_RECEPTIVE = (int) player.calcStat(Stats.WIND_RECEPTIVE, 0, null, null);
		if(WIND_RECEPTIVE != 0)
			dialog.append("<tr><td>Wind</td><td>").append(-WIND_RECEPTIVE).append("</td></tr>");

		int WATER_RECEPTIVE = (int) player.calcStat(Stats.WATER_RECEPTIVE, 0, null, null);
		if(WATER_RECEPTIVE != 0)
			dialog.append("<tr><td>Water</td><td>").append(-WATER_RECEPTIVE).append("</td></tr>");

		int EARTH_RECEPTIVE = (int) player.calcStat(Stats.EARTH_RECEPTIVE, 0, null, null);
		if(EARTH_RECEPTIVE != 0)
			dialog.append("<tr><td>Earth</td><td>").append(-EARTH_RECEPTIVE).append("</td></tr>");

		int SACRED_RECEPTIVE = (int) player.calcStat(Stats.SACRED_RECEPTIVE, 0, null, null);
		if(SACRED_RECEPTIVE != 0)
			dialog.append("<tr><td>Light</td><td>").append(-SACRED_RECEPTIVE).append("</td></tr>");

		int UNHOLY_RECEPTIVE = (int) player.calcStat(Stats.UNHOLY_RECEPTIVE, 0, null, null);
		if(UNHOLY_RECEPTIVE != 0)
			dialog.append("<tr><td>Darkness</td><td>").append(-UNHOLY_RECEPTIVE).append("</td></tr>");

		int DEBUFF_RECEPTIVE = (int) player.calcStat(Stats.DEBUFF_RECEPTIVE, null, null);
		if(DEBUFF_RECEPTIVE != 0)
			dialog.append("<tr><td>Debuff</td><td>").append(DEBUFF_RECEPTIVE).append("</td></tr>");

		int CANCEL_RECEPTIVE = (int) player.calcStat(Stats.CANCEL_RECEPTIVE, null, null);
		if(CANCEL_RECEPTIVE != 0)
			dialog.append("<tr><td>Cancel</td><td>").append(CANCEL_RECEPTIVE).append("</td></tr>");

		int CRIT_CHANCE_RECEPTIVE = 100 - (int) player.calcStat(Stats.CRIT_CHANCE_RECEPTIVE, null, null);
		if(CRIT_CHANCE_RECEPTIVE != 0)
			dialog.append("<tr><td>Crit get chance</td><td>").append(CRIT_CHANCE_RECEPTIVE).append("%</td></tr>");

		int CRIT_DAMAGE_RECEPTIVE = 100 - (int) player.calcStat(Stats.CRIT_DAMAGE_RECEPTIVE, null, null);
		if(CRIT_DAMAGE_RECEPTIVE != 0)
			dialog.append("<tr><td>Crit get damage</td><td>").append(CRIT_DAMAGE_RECEPTIVE).append("%</td></tr>");

		/*int ElemAtkPower = 0;
		switch(player.getTemplate().atkElement)
		{
			case 0:
				ElemAtkPower = (int) player.calcStat(Stats.ATTACK_ELEMENT_FIRE, 0, null, null);
				break;
			case 1:
				ElemAtkPower = (int) player.calcStat(Stats.ATTACK_ELEMENT_WATER, 0, null, null);
				break;
			case 2:
				ElemAtkPower = (int) player.calcStat(Stats.ATTACK_ELEMENT_WIND, 0, null, null);
				break;
			case 3:
				ElemAtkPower = (int) player.calcStat(Stats.ATTACK_ELEMENT_EARTH, 0, null, null);
				break;
			case 4:
				ElemAtkPower = (int) player.calcStat(Stats.ATTACK_ELEMENT_SACRED, 0, null, null);
				break;
			case 5:
				ElemAtkPower = (int) player.calcStat(Stats.ATTACK_ELEMENT_UNHOLY, 0, null, null);
				break;
			default:
				ElemAtkPower = 0;
				break;
		}

		if(ElemAtkPower != 0)
			dialog.append("<tr><td>Attack Element: "+getElementNameById(player.getTemplate().atkElement)+"</td><td>").append(ElemAtkPower).append("</td></tr>");*/

		/*if(FIRE_RECEPTIVE == 0 && WIND_RECEPTIVE == 0 && WATER_RECEPTIVE == 0 && EARTH_RECEPTIVE == 0 && UNHOLY_RECEPTIVE == 0 && SACRED_RECEPTIVE // primary elements
		== 0 && trait_bleed == 0 && trait_shock // phys debuff
		== 0 && trait_poison == 0 && trait_hold == 0 && trait_sleep == 0 && trait_paralyze == 0 && trait_derangement == 0 && DEBUFF_RECEPTIVE == 0 && CANCEL_RECEPTIVE // mag debuff
		== 0 && SWORD_WPN_RECEPTIVE == 0 && DUAL_WPN_RECEPTIVE == 0 && BLUNT_WPN_RECEPTIVE == 0 && DAGGER_WPN_RECEPTIVE == 0 && BOW_WPN_RECEPTIVE == 0 && POLE_WPN_RECEPTIVE == 0 && FIST_WPN_RECEPTIVE == 0// weapons
		)
			dialog.append("</table>No resists</body></html>");
		else*/
			dialog.append("</table></body></html>");
		adminReply.setHtml(dialog.toString());
		activeChar.sendPacket(adminReply);
	}

	private void setTargetKarma(L2Player activeChar, int newKarma)
	{
		L2Object target = activeChar.getTarget();
		if(target == null)
		{
			activeChar.sendPacket(Msg.INVALID_TARGET);
			return;
		}

		L2Player player;
		if(target.isPlayer())
			player = (L2Player) target;
		else
			return;

		if(newKarma >= 0)
		{
			int oldKarma = player.getKarma();
			player.setKarma(newKarma);

			player.sendMessage("Admin has changed your karma from " + oldKarma + " to " + newKarma + ".");
			activeChar.sendMessage("Successfully Changed karma for " + player.getName() + " from (" + oldKarma + ") to (" + newKarma + ").");
		}
		else
			activeChar.sendMessage("You must enter a value for karma greater than or equal to 0.");
	}

	private void setTargetFame(L2Player activeChar, int newFame)
	{
		L2Object target = activeChar.getTarget();
		if(target == null)
		{
			activeChar.sendPacket(Msg.INVALID_TARGET);
			return;
		}

		L2Player player;
		if(target.isPlayer())
			player = (L2Player) target;
		else
			return;

		if(newFame >= 0)
		{
			int oldFame = player.getFame();
			player.setFame(newFame, "Admin manual");

			player.sendMessage("Admin has changed your fame from " + oldFame + " to " + newFame + ".");
			activeChar.sendMessage("Successfully Changed fame for " + player.getName() + " from (" + oldFame + ") to (" + newFame + ").");
		}
		else
			activeChar.sendMessage("You must enter a value for fame greater than or equal to 0.");
	}

	private void adminModifyCharacter(L2Player activeChar, String modifications)
	{
		L2Object target = activeChar.getTarget();
		if(target == null || !target.isPlayer())
		{
			activeChar.sendPacket(Msg.SELECT_TARGET);
			return;
		}

		L2Player player = (L2Player) target;
		String[] strvals = modifications.split("&");
		Integer[] vals = new Integer[strvals.length];
		for(int i = 0; i < strvals.length; i++)
		{
			strvals[i] = strvals[i].trim();
			vals[i] = strvals[i].isEmpty() ? null : Integer.valueOf(strvals[i]);
		}

		if(vals[0] != null)
			player.setCurrentHp(vals[0], false);

		if(vals[1] != null)
			player.setCurrentMp(vals[1]);

		if(vals[2] != null)
			player.setKarma(vals[2]);

		if(vals[3] != null)
			player.setPvpFlag(vals[3]);

		if(vals[4] != null)
			player.setPvpKills(vals[4]);

		if(vals[5] != null)
			player.setClassId(vals[5], true);

		player.sendChanges();
		editCharacter(activeChar); // Back to start
		player.broadcastUserInfo(true);
		player.decayMe();
		player.spawnMe(activeChar.getLoc());
	}

	private void editCharacter(L2Player activeChar)
	{
		L2Object target = activeChar.getTarget();
		if(target == null || !target.isPlayer())
		{
			activeChar.sendPacket(Msg.SELECT_TARGET);
			return;
		}

		L2Player player = (L2Player) target;
		NpcHtmlMessage adminReply = new NpcHtmlMessage(5);

		StringBuffer replyMSG = new StringBuffer("<html><body>");
		replyMSG.append("<table width=260><tr>");
		replyMSG.append("<td width=40><button value=\"Main\" action=\"bypass -h admin_admin\" width=40 height=15 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\"></td>");
		replyMSG.append("<td width=180><center>Character Selection Menu</center></td>");
		replyMSG.append("<td width=40><button value=\"Back\" action=\"bypass -h admin_current_player\" width=40 height=15 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\"></td>");
		replyMSG.append("</tr></table>");
		replyMSG.append("<br><br>");
		replyMSG.append("<center>Editing character: " + player.getName() + "</center><br>");
		replyMSG.append("<table width=250>");
		replyMSG.append("<tr><td width=40></td><td width=70>Curent:</td><td width=70>Max:</td><td width=70></td></tr>");
		replyMSG.append("<tr><td width=40>HP:</td><td width=70>" + player.getCurrentHp() + "</td><td width=70>" + player.getMaxHp() + "</td><td width=70>Karma: " + player.getKarma() + "</td></tr>");
		replyMSG.append("<tr><td width=40>MP:</td><td width=70>" + player.getCurrentMp() + "</td><td width=70>" + player.getMaxMp() + "</td><td width=70>Pvp Kills: " + player.getPvpKills() + "</td></tr>");
		replyMSG.append("<tr><td width=40>Load:</td><td width=70>" + player.getCurrentLoad() + "</td><td width=70>" + player.getMaxLoad() + "</td><td width=70>Pvp Flag: " + player.getPvpFlag() + "</td></tr>");
		replyMSG.append("</table>");
		replyMSG.append("<table width=270><tr><td>Class<?> Template Id: " + player.getClassId() + "/" + player.getClassId().getId() + "</td></tr></table><br>");
		replyMSG.append("<table width=270>");
		replyMSG.append("<tr><td>Note: Fill all values before saving the modifications.</td></tr>");
		replyMSG.append("</table><br>");
		replyMSG.append("<table width=270>");
		replyMSG.append("<tr><td width=50>Hp:</td><td><edit var=\"hp\" width=50></td><td width=50>Mp:</td><td><edit var=\"mp\" width=50></td></tr>");
		replyMSG.append("<tr><td width=50>Pvp Flag:</td><td><edit var=\"pvpflag\" width=50></td><td width=50>Karma:</td><td><edit var=\"karma\" width=50></td></tr>");
		replyMSG.append("<tr><td width=50>Class<?> Id:</td><td><edit var=\"classid\" width=50></td><td width=50>Pvp Kills:</td><td><edit var=\"pvpkills\" width=50></td></tr>");
		replyMSG.append("</table><br>");
		replyMSG.append("<center><button value=\"Save Changes\" action=\"bypass -h admin_save_modifications $hp & $mp & $karma & $pvpflag & $pvpkills & $classid &\" width=80 height=15 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\"></center><br>");
		replyMSG.append("</body></html>");

		adminReply.setHtml(replyMSG.toString());
		activeChar.sendPacket(adminReply);
	}

	private void showCharacterActions(L2Player activeChar)
	{
		L2Object target = activeChar.getTarget();
		L2Player player;
		if(target != null && target.isPlayer())
			player = (L2Player) target;
		else
			return;

		NpcHtmlMessage adminReply = new NpcHtmlMessage(5);

		StringBuffer replyMSG = new StringBuffer("<html><body>");
		replyMSG.append("<table width=260><tr>");
		replyMSG.append("<td width=40><button value=\"Main\" action=\"bypass -h admin_admin\" width=40 height=15 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\"></td>");
		replyMSG.append("<td width=180><center>Character Selection Menu</center></td>");
		replyMSG.append("<td width=40><button value=\"Back\" action=\"bypass -h admin_current_player\" width=40 height=15 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\"></td>");
		replyMSG.append("</tr></table><br><br>");
		replyMSG.append("<center>Admin Actions for: " + player.getName() + "</center><br>");
		replyMSG.append("<center><table width=200><tr>");
		replyMSG.append("<td width=100>Argument(*):</td><td width=100><edit var=\"arg\" width=100></td>");
		replyMSG.append("</tr></table><br></center>");
		replyMSG.append("<table width=270>");

		replyMSG.append("<tr><td width=90><button value=\"Teleport\" action=\"bypass -h admin_teleportto " + player.getName() + "\" width=85 height=20 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\"></td>");
		replyMSG.append("<td width=90><button value=\"Recall\" action=\"bypass -h admin_recall " + player.getName() + "\" width=85 height=20 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\"></td>");
		replyMSG.append("<td width=90><button value=\"Quests\" action=\"bypass -h admin_quests " + player.getName() + "\" width=85 height=20 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\"></td></tr>");

		replyMSG.append("</body></html>");

		adminReply.setHtml(replyMSG.toString());
		activeChar.sendPacket(adminReply);
	}

	private void findCharacter(L2Player activeChar, String CharacterToFind)
	{
		NpcHtmlMessage adminReply = new NpcHtmlMessage(5);
		int CharactersFound = 0;

		StringBuffer replyMSG = new StringBuffer("<html><body>");
		replyMSG.append("<table width=260><tr>");
		replyMSG.append("<td width=40><button value=\"Main\" action=\"bypass -h admin_admin\" width=40 height=15 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\"></td>");
		replyMSG.append("<td width=180><center>Character Selection Menu</center></td>");
		replyMSG.append("<td width=40><button value=\"Back\" action=\"bypass -h admin_show_characters 0\" width=40 height=15 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\"></td>");
		replyMSG.append("</tr></table>");
		replyMSG.append("<br><br>");

		for(L2Player element : L2ObjectsStorage.getPlayers())
			if(element.getName().startsWith(CharacterToFind))
			{
				CharactersFound = CharactersFound + 1;
				replyMSG.append("<table width=270>");
				replyMSG.append("<tr><td width=80>Name</td><td width=110>Class</td><td width=40>Level</td></tr>");
				replyMSG.append("<tr><td width=80><a action=\"bypass -h admin_character_list " + element.getName() + "\">" + element.getName() + "</a></td><td width=110>" + element.getTemplate().className + "</td><td width=40>" + element.getLevel() + "</td></tr>");
				replyMSG.append("</table>");
			}

		if(CharactersFound == 0)
		{
			replyMSG.append("<table width=270>");
			replyMSG.append("<tr><td width=270>Your search did not find any characters.</td></tr>");
			replyMSG.append("<tr><td width=270>Please try again.<br></td></tr>");
			replyMSG.append("</table><br>");
			replyMSG.append("<center><table><tr><td>");
			replyMSG.append("<edit var=\"character_name\" width=80></td><td><button value=\"Find\" action=\"bypass -h admin_find_character $character_name\" width=40 height=15 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\">");
			replyMSG.append("</td></tr></table></center>");
		}
		else
		{
			replyMSG.append("<center><br>Found " + CharactersFound + " character");

			if(CharactersFound == 1)
				replyMSG.append(".");
			else if(CharactersFound > 1)
				replyMSG.append("s.");
		}

		replyMSG.append("</center></body></html>");

		adminReply.setHtml(replyMSG.toString());
		activeChar.sendPacket(adminReply);
	}

	private void addExpSp(final L2Player activeChar)
	{
		final L2Object target = activeChar.getTarget();
		L2Player player;
		if(target != null && target.isPlayer() && (activeChar == target || activeChar.getPlayerAccess().CanEditCharAll))
			player = (L2Player) target;
		else
		{
			activeChar.sendPacket(Msg.INVALID_TARGET);
			return;
		}

		final NpcHtmlMessage adminReply = new NpcHtmlMessage(5);

		final StringBuffer replyMSG = new StringBuffer("<html><body>");
		replyMSG.append("<table width=260><tr>");
		replyMSG.append("<td width=40><button value=\"Main\" action=\"bypass -h admin_admin\" width=40 height=15 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\"></td>");
		replyMSG.append("<td width=180><center>Character Selection Menu</center></td>");
		replyMSG.append("<td width=40><button value=\"Back\" action=\"bypass -h admin_current_player\" width=40 height=15 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\"></td>");
		replyMSG.append("</tr></table>");
		replyMSG.append("<br><br>");
		replyMSG.append("<table width=270><tr><td>Name: " + player.getName() + "</td></tr>");
		replyMSG.append("<tr><td>Lv: " + player.getLevel() + " " + player.getTemplate().className + "</td></tr>");
		replyMSG.append("<tr><td>Exp: " + player.getExp() + "</td></tr>");
		replyMSG.append("<tr><td>Sp: " + player.getSp() + "</td></tr></table>");
		replyMSG.append("<br><table width=270><tr><td>Note: Dont forget that modifying players skills can</td></tr>");
		replyMSG.append("<tr><td>ruin the game...</td></tr></table><br>");
		replyMSG.append("<table width=270><tr><td>Note: Fill all values before saving the modifications.,</td></tr>");
		replyMSG.append("<tr><td>Note: Use 0 if no changes are needed.</td></tr></table><br>");
		replyMSG.append("<center><table><tr>");
		replyMSG.append("<td>Exp: <edit var=\"exp_to_add\" width=50></td>");
		replyMSG.append("<td>Sp:  <edit var=\"sp_to_add\" width=50></td>");
		replyMSG.append("<td>&nbsp;<button value=\"Save Changes\" action=\"bypass -h admin_add_exp_sp $exp_to_add & $sp_to_add &\" width=80 height=15 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\"></td>");
		replyMSG.append("</tr></table></center>");
		replyMSG.append("<center><table><tr>");
		replyMSG.append("<td>LvL: <edit var=\"lvl\" width=50></td>");
		replyMSG.append("<td>&nbsp;<button value=\"Set Level\" action=\"bypass -h admin_setlevel $lvl\" width=80 height=15 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\"></td>");
		replyMSG.append("</tr></table></center>");
		replyMSG.append("</body></html>");

		adminReply.setHtml(replyMSG.toString());
		activeChar.sendPacket(adminReply);
	}

	private void adminAddExpSp(final L2Player activeChar, final String ExpSp)
	{
		if(!activeChar.getPlayerAccess().CanEditCharAll)
		{
			activeChar.sendMessage("You have not enough privileges, for use this function.");
			return;
		}

		final L2Object target = activeChar.getTarget();
		if(target == null || !target.isPlayer())
		{
			activeChar.sendPacket(Msg.SELECT_TARGET);
			return;
		}

		L2Player player = (L2Player) target;
		String[] strvals = ExpSp.split("&");
		long[] vals = new long[strvals.length];
		for(int i = 0; i < strvals.length; i++)
		{
			strvals[i] = strvals[i].trim();
			vals[i] = strvals[i].isEmpty() ? 0 : Long.parseLong(strvals[i]);
		}

		player.addExpAndSp(vals[0], vals[1], false, false);
		player.sendMessage("Admin is adding you " + vals[0] + " exp and " + vals[1] + " SP.");
		activeChar.sendMessage("Added " + vals[0] + " exp and " + vals[1] + " SP to " + player.getName() + ".");
	}

	private void setSubclass(final L2Player activeChar, final L2Player player)
	{
		StringBuffer content = new StringBuffer("<html><body>");
		NpcHtmlMessage html = new NpcHtmlMessage(5);
		Set<PlayerClass> subsAvailable;
		subsAvailable = getAvailableSubClasses(player);

		if(subsAvailable != null && !subsAvailable.isEmpty())
		{
			content.append("Add Subclass:<br>Which subclass do you wish to add?<br>");

			for(PlayerClass subClass : subsAvailable)
				content.append("<a action=\"bypass -h admin_setsubclass " + subClass.ordinal() + "\">" + formatClassForDisplay(subClass) + "</a><br>");
		}
		else
		{
			activeChar.sendMessage(new CustomMessage("l2open.gameserver.model.instances.L2VillageMasterInstance.NoSubAtThisTime", activeChar));
			return;
		}
		content.append("</body></html>");
		html.setHtml(content.toString());
		activeChar.sendPacket(html);
	}

	private Set<PlayerClass> getAvailableSubClasses(L2Player player)
	{
		final int charClassId = player.getBaseClassId();

		PlayerClass currClass = PlayerClass.values()[charClassId];// .valueOf(charClassName);

		/**
		 * If the race of your main class is Elf or Dark Elf, you may not select
		 * each class as a subclass to the other class, and you may not select
		 * Overlord and Warsmith class as a subclass.
		 *
		 * You may not select a similar class as the subclass. The occupations
		 * classified as similar classes are as follows:
		 *
		 * Treasure Hunter, Plainswalker and Abyss Walker Hawkeye, Silver Ranger
		 * and Phantom Ranger Paladin, Dark Avenger, Temple Knight and Shillien
		 * Knight Warlocks, Elemental Summoner and Phantom Summoner Elder and
		 * Shillien Elder Swordsinger and Bladedancer Sorcerer, Spellsinger and
		 * Spellhowler
		 *
		 * Kamael могут брать только сабы Kamael
		 * Другие классы не могут брать сабы Kamael
		 *
		 */
		Set<PlayerClass> availSubs = currClass.getAvailableSubclasses();
		if(availSubs == null)
			return null;

		// Из списка сабов удаляем мейн класс игрока
		availSubs.remove(currClass);

		for(PlayerClass availSub : availSubs)
		{
			// Удаляем из списка возможных сабов, уже взятые сабы и их предков
			for(L2SubClass subClass : player.getSubClasses().values())
			{
				if(availSub.ordinal() == subClass.getClassId())
				{
					availSubs.remove(availSub);
					continue;
				}

				// Удаляем из возможных сабов их родителей, если таковые есть у чара
				ClassId parent = ClassId.values()[availSub.ordinal()].getParent(player.getSex());
				if(parent != null && parent.getId() == subClass.getClassId())
				{
					availSubs.remove(availSub);
					continue;
				}

				// Удаляем из возможных сабов родителей текущих сабклассов, иначе если взять саб berserker
				// и довести до 3ей профы - doombringer, игроку будет предложен berserker вновь (дежавю)
				ClassId subParent = ClassId.values()[subClass.getClassId()].getParent(player.getSex());
				if(subParent != null && subParent.getId() == availSub.ordinal())
					availSubs.remove(availSub);
			}

			// Особенности саб классов камаэль
			if(availSub.isOfRace(Race.kamael))
			{
				// Для Soulbreaker-а и SoulHound не предлагаем Soulbreaker-а другого пола
				if((currClass == PlayerClass.MaleSoulHound || currClass == PlayerClass.FemaleSoulHound || currClass == PlayerClass.FemaleSoulbreaker || currClass == PlayerClass.MaleSoulbreaker) && (availSub == PlayerClass.FemaleSoulbreaker || availSub == PlayerClass.MaleSoulbreaker))
					availSubs.remove(availSub);

				// Для Berserker(doombringer) и Arbalester(trickster) предлагаем Soulbreaker-а только своего пола
				if(currClass == PlayerClass.Berserker || currClass == PlayerClass.Doombringer || currClass == PlayerClass.Arbalester || currClass == PlayerClass.Trickster)
					if(player.getSex() == 1 && availSub == PlayerClass.MaleSoulbreaker || player.getSex() == 0 && availSub == PlayerClass.FemaleSoulbreaker)
						availSubs.remove(availSub);

				// Inspector доступен, только когда вкачаны 2 возможных первых саба камаэль(+ мейн класс):
				// doombringer(berserker), soulhound(maleSoulbreaker, femaleSoulbreaker), trickster(arbalester)
				if(availSub == PlayerClass.Inspector)
					// doombringer(berserker)
					if(!(player.getSubClasses().containsKey(131) || player.getSubClasses().containsKey(127)))
						availSubs.remove(availSub);
					// soulhound(maleSoulbreaker, femaleSoulbreaker)
					else if(!(player.getSubClasses().containsKey(132) || player.getSubClasses().containsKey(133) || player.getSubClasses().containsKey(128) || player.getSubClasses().containsKey(129)))
						availSubs.remove(availSub);
					// trickster(arbalester)
					else if(!(player.getSubClasses().containsKey(134) || player.getSubClasses().containsKey(130)))
						availSubs.remove(availSub);
			}
		}
		return availSubs;
	}

	private final class Untransform extends l2open.common.RunnableImpl
	{
		private final L2Player _player;
		private Untransform(L2Player player)
		{
			_player = player;
		}
		public void runImpl()
		{
			_player.setTransformation(0);
		}
	}

	private String formatClassForDisplay(PlayerClass className)
	{
		String classNameStr = className.toString();
		char[] charArray = classNameStr.toCharArray();

		for(int i = 1; i < charArray.length; i++)
			if(Character.isUpperCase(charArray[i]))
				classNameStr = classNameStr.substring(0, i) + " " + classNameStr.substring(i);

		return classNameStr;
	}

	private void admin_hinfo(L2Player activeChar, String name, int radius)
	{
		StringBuffer content = new StringBuffer("<html><body>");
		NpcHtmlMessage html = new NpcHtmlMessage(5);
		if(radius == -1)
		{
			if(name != null && !name.isEmpty()) // По нику
			{
				try
				{
					L2Player target = L2ObjectsStorage.getPlayer(name);

					for(L2Player player : L2ObjectsStorage.getPlayers())
						if(player.getHWIDs().startsWith(target.getHWIDs()))
						{
							content.append("<table width=270>");
							content.append("<tr><td width=80>Name</td><td width=110>Class</td><td width=40>Level</td></tr>");
							content.append("<tr><td width=80><a action=\"bypass -h admin_character_list " + player.getName() + "\">" + player.getName() + "</a></td><td width=110>" + player.getTemplate().className + "</td><td width=40>" + player.getLevel() + "</td></tr>");
							content.append("</table>");
						}
				}
				catch(StringIndexOutOfBoundsException e)
				{
					// Case of empty character name
				}
			}
			else if(activeChar != null && activeChar.getTarget() != null && activeChar.getTarget().isPlayer()) // По таргету
			{
				try
				{
					L2Player target = activeChar.getTarget().getPlayer();

					for(L2Player player : L2ObjectsStorage.getPlayers())
						if(player.getHWIDs().startsWith(target.getHWIDs()))
						{
							content.append("<table width=270>");
							content.append("<tr><td width=80>Name</td><td width=110>Class</td><td width=40>Level</td></tr>");
							content.append("<tr><td width=80><a action=\"bypass -h admin_character_list " + player.getName() + "\">" + player.getName() + "</a></td><td width=110>" + player.getTemplate().className + "</td><td width=40>" + player.getLevel() + "</td></tr>");
							content.append("</table>");
						}
				}
				catch(StringIndexOutOfBoundsException e)
				{
					// Case of empty character name
				}
			}
		}
		else
		{
			if(name != null && !name.isEmpty())
			{
				content.append("<table width=270>");
				content.append("<tr><td width=80>Name</td><td width=110>Class</td><td width=40>Level</td></tr>");
				for(L2Player player : L2World.getAroundPlayers(activeChar, radius, 200, false))
					if(player != null && player.getHWIDs().equals(name))
						content.append("<tr><td width=80><a action=\"bypass -h admin_character_list " + player.getName() + "\">" + player.getName() + "</a></td><td width=110>" + player.getTemplate().className + "</td><td width=40>" + player.getLevel() + "</td></tr>");
				content.append("</table>");
				content.append("<br><br><br><center><button value=\"Назад\" action=\"bypass -h admin_seeradius "+radius+"\" width=52 height=18 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\"></center>");
			}
			else
			{
				HashMap<String, List<Integer>> _hwid_list = new HashMap<String, List<Integer>>();

				for(L2Player player : L2World.getAroundPlayers(activeChar, radius, 200, false))
					if(player != null)
					{
						if(_hwid_list.containsKey(player.getHWIDs()))
						{
							List<Integer> _r = _hwid_list.get(player.getHWIDs());
							_r.add(player.getObjectId());
							_hwid_list.put(player.getHWIDs(), _r);
						}
						else
						{
							List<Integer> _r = new ArrayList<Integer>();
							_r.add(player.getObjectId());
							_hwid_list.put(player.getHWIDs(), _r);
						}
					}

				_hwid_list = ValueSortMap.sortMapByValue(_hwid_list, _comparator);
				content.append("<table width=270>");
				content.append("<tr><td width=80>HWID</td><td width=110>Windows count</td></tr>");
				for(String hwid : _hwid_list.keySet())
				{
					List<Integer> _h = _hwid_list.get(hwid);
					if(_h.size() > 1)
						content.append("<tr><td width=80><a action=\"bypass -h admin_seeradius "+radius+" " + hwid + "\">" + hwid.substring(0, Math.min(hwid.length(), 15)) + "</a></td><td width=110>" + _h.size() + "</td></tr>");
				}
				content.append("</table>");
			}
		}

		content.append("</body></html>");
		html.setHtml(content.toString());
		activeChar.sendPacket(html);
	}

	private static CharComparator _comparator = new CharComparator();
	private static class CharComparator implements Comparator<List<Integer>>
	{
		public int compare(List<Integer> o1, List<Integer> o2)
		{
			if(o1 == null || o2 == null)
				return 0;
			else if(o1.size() < o2.size())
				return 1;
			else if(o1.size() > o2.size())
				return -1;
			return o1.get(0) - o2.get(0);
		}
	}

	@Override
	public void onLoad()
	{
		AdminCommandHandler.getInstance().registerAdminCommandHandler(this);
	}

	@Override
	public void onReload()
	{}

	@Override
	public void onShutdown()
	{}
}