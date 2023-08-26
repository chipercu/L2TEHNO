package communityboard;

import java.io.File;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.logging.Logger;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;

import l2open.config.ConfigValue;
import l2open.database.DatabaseUtils;
import l2open.database.FiltredPreparedStatement;
import l2open.database.L2DatabaseFactory;
import l2open.database.ThreadConnection;
import l2open.extensions.multilang.CustomMessage;
import l2open.extensions.scripts.ScriptFile;
import l2open.gameserver.cache.Msg;
import l2open.gameserver.common.DifferentMethods;
import l2open.gameserver.communitybbs.Manager.BaseBBSManager;
import l2open.gameserver.handler.CommunityHandler;
import l2open.gameserver.handler.ICommunityHandler;
import l2open.gameserver.model.L2Object;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.L2Skill;
import l2open.gameserver.model.L2Zone;
import l2open.gameserver.model.L2Zone.ZoneType;
import l2open.gameserver.model.barahlo.CBTpSch;
import l2open.gameserver.tables.ReflectionTable;
import l2open.gameserver.tables.SkillTable;
import l2open.gameserver.xml.XmlUtils;
import l2open.util.Files;
import l2open.util.Util;
import l2open.util.Location;

/**
 * @author: L2CCCP
 * @coauthor: Diagod
 */
public class CommunityBoardTeleport extends BaseBBSManager implements ICommunityHandler, ScriptFile //TODO: переписать сверку зон на более адекватную.
{
	static final Logger _log = Logger.getLogger(CommunityBoardTeleport.class.getName());

	private static enum Commands
	{
		_bbsteleport
	}

	private static ZoneType[] FORBIDDEN_ZONES_SAVE;
	private static ZoneType[] FORBIDDEN_ZONES_TP;

	public L2Object self;
	public void go_to(L2Player player, String bypass)
	{
		if(player == null)
			return;

		StringTokenizer token = new StringTokenizer(bypass, " ");
		token.nextToken();
		int x = Integer.parseInt(token.nextToken());
		int y = Integer.parseInt(token.nextToken());
		int z = Integer.parseInt(token.nextToken());
		goToTeleportPoint(player, x, y, z);
		showTeleportIndex(player, 0, -1);
	}

	public void save_to(L2Player player, String bypass)
	{
		if(player == null)
			return;

		String name = null;
		StringTokenizer token = new StringTokenizer(bypass, " ");
		token.nextToken();
		if(token.hasMoreTokens())
		{
			name = token.nextToken();
		}
		else
		{
			showTeleportPoint(player);
			return;
		}
		addTeleportPoint(player, name);
		showTeleportPoint(player);
	}

	@Override
	public void parsecmd(String bypass, L2Player player)
	{
		if(player.getEventMaster() != null && player.getEventMaster().blockBbs())
			return;
		if(player.is_block || player.isInEvent() > 0)
			return;
		if(bypass.equals("_bbsteleport"))
			showTeleportIndex(player, 0, -1);
		else if(bypass.startsWith("_bbsteleport:page"))
		{
			String[] b = bypass.split(" ");
			String page = b[1];
			separateAndSend(Files.read(ConfigValue.CommunityBoardHtmlRoot + "teleport/" + page + ".htm", player), player);
		}
		else if(bypass.startsWith("_bbsteleport:go_name"))
		{
			String name = bypass.split(":")[2];
			Point p = _teleport_name_list.get(name.trim());

			if(p != null)
				goToTeleportID(player, p.p_name(player.isLangRus()), p.item_id, p.count, p.p_min, p.p_max, p.x, p.y, p.z, p.p_pk, p.premium_point, p.premium_item_id, p.premium_count, p.skill_id, p.skill_level);
			//showTeleportIndex(player, 0, -1);
		}
		else if(bypass.equals("_bbsteleport:save_page"))
			showTeleportPoint(player);
		else if(bypass.startsWith("_bbsteleport:delete"))
		{
			StringTokenizer token = new StringTokenizer(bypass, " ");
			token.nextToken();
			int name = Integer.parseInt(token.nextToken());
			deleteTeleportPoint(player, name);
			showTeleportPoint(player);
		}
		else if(bypass.startsWith("_bbsteleport:save"))
		{
			long price = player.hasBonus() && ConfigValue.TeleportPointPremiumFree ? 0 : ConfigValue.TeleportSavePrice;
			Object[] obj = {player, bypass};
			player.scriptRequest("Вы желаете сохранить текущее положение за "+price+" "+DifferentMethods.getItemName(ConfigValue.TeleportSaveItem)+"?", "communityboard.CommunityBoardTeleport:save_to", obj);

			/*String name = null;
			StringTokenizer token = new StringTokenizer(bypass, " ");
			token.nextToken();
			if(token.hasMoreTokens())
			{
				name = token.nextToken();
			}
			else
			{
				showTeleportPoint(player);
				return;
			}
			addTeleportPoint(player, name);
			showTeleportPoint(player);*/
		}
		else if(bypass.startsWith("_bbsteleport:go_buff"))
		{
			StringTokenizer token = new StringTokenizer(bypass, " ");
			token.nextToken();
			int x = Integer.parseInt(token.nextToken());
			int y = Integer.parseInt(token.nextToken());
			int z = Integer.parseInt(token.nextToken());

			int skill_id = Integer.parseInt(token.nextToken());
			int skill_level = Integer.parseInt(token.nextToken());
			if(goToTeleportPoint(player, x, y, z))
			{
				L2Skill skill = SkillTable.getInstance().getInfo(skill_id, skill_level);
				if(skill != null)
					skill.getEffects(player, player, false, false);
			}
			showTeleportIndex(player, 0, -1);
		}
		else if(bypass.startsWith("_bbsteleport:go_t"))
		{
			long price = 0;
			if(player.hasBonus() && ConfigValue.TeleportPremiumFree || player.getLevel() <= ConfigValue.TeleportFreeLevel)
				price = 0;
			else
				price = ConfigValue.TeleportPrice;
			Object[] obj = {player, bypass};
			player.scriptRequest("Вы желаете переместится за "+price+" Адена?", "communityboard.CommunityBoardTeleport:go_to", obj);
		}
		else if(bypass.startsWith("_bbsteleport:go"))
		{
			StringTokenizer token = new StringTokenizer(bypass, " ");
			token.nextToken();
			int x = Integer.parseInt(token.nextToken());
			int y = Integer.parseInt(token.nextToken());
			int z = Integer.parseInt(token.nextToken());
			goToTeleportPoint(player, x, y, z);
			showTeleportIndex(player, 0, -1);
		}
		else if(bypass.startsWith("_bbsteleport:id:"))
		{
			StringTokenizer token = new StringTokenizer(bypass, ":");
			token.nextToken();
			token.nextToken();
			int id1 = Integer.parseInt(token.nextToken());
			int id2 = Integer.parseInt(token.nextToken());
			int id3 = Integer.parseInt(token.nextToken());

			Point p = _teleport_list.get(id1).get(id2).point_list[id3];

			goToTeleportID(player, p.p_name(player.isLangRus()), p.item_id, p.count, p.p_min, p.p_max, p.x, p.y, p.z, p.p_pk, p.premium_point, p.premium_item_id, p.premium_count, p.skill_id, p.skill_level);
			showTeleportIndex(player, id1, -1);
		}
		else if(bypass.startsWith("_bbsteleport:list"))
		{
			StringTokenizer token = new StringTokenizer(bypass, ":");
			token.nextToken();
			token.nextToken();
			int index = Integer.parseInt(token.nextToken());
			String next_token = token.nextToken();
			if(next_token.endsWith(" Player"))
			{
				_log.info("WARNING! Player "+player.getName()+" use bot!");
				return;
			}
			int index2 = Integer.parseInt(next_token);

			String content;
			StringBuilder result = new StringBuilder();
			if(index2 == -1)
				showTeleportIndex(player, index, index2);
			else
			{
				content = Files.read(ConfigValue.CommunityBoardHtmlRoot + "teleport/teleport.htm", player);
				result.append("<table width=450>");
				PointGroup pg = _teleport_list.get(index).get(index2);
				int sz = pg.point_list.length;
				Point p;
				for(int i = 0; i < sz; i += 2)
				{
					if(i < sz)
					{
						p = pg.point_list[i];
						pg = _teleport_list.get(index).get(index2);
						result.append("<tr>");
						if(pg != null)
							result.append("	<td valign=\"top\" align=\"center\"><button value=\"" + p.p_name(player.isLangRus()) + "\" action=\"bypass -h _bbsteleport:id:" + index + ":" + index2 + ":" + i + "\" width=200 height=29 back=\"L2UI_CT1.OlympiadWnd_DF_Watch_Down\" fore=\"L2UI_CT1.OlympiadWnd_DF_Watch\"></td>");
					}
					if(i + 1 < sz)
					{
						p = pg.point_list[i + 1];
						pg = _teleport_list.get(index).get(index2);
						if(pg != null)
							result.append("	<td valign=\"top\" align=\"center\"><button value=\"" + p.p_name(player.isLangRus()) + "\" action=\"bypass -h _bbsteleport:id:" + index + ":" + index2 + ":" + (i + 1) + "\" width=200 height=29 back=\"L2UI_CT1.OlympiadWnd_DF_Watch_Down\" fore=\"L2UI_CT1.OlympiadWnd_DF_Watch\"></td>");
					}
					result.append("</tr>");
				}
				result.append("</table>");
				result.append("<table width=200>");
				result.append("	<tr>");
				result.append("		<td valign=\"top\" align=\"center\"><br><br><button value=\"Назад\" action=\"bypass -h _bbsteleport:list:" + index + ":-1" + ":" + pg.group_name + "\" width=200 height=29 back=\"L2UI_CT1.OlympiadWnd_DF_Back_Down\" fore=\"L2UI_CT1.OlympiadWnd_DF_Back\"></td>");
				result.append("	</tr>");
				result.append("</table>");

				content = content.replace("<?name?>", pg.name);
				content = content.replace("<?list?>", result.toString());
				separateAndSend(content, player);
			}
		}
		else
			separateAndSend(DifferentMethods.getErrorHtml(player, bypass), player);
	}

	private void goToTeleportID(L2Player player, String name, int priceId, long count, int min, int max, int x, int y, int z, boolean pk, boolean premium, int premiumPriceId, long premiumCount, int skill_id, int skill_level)
	{
		Location loc = player.getLoc();
		int item;
		long price;
		int level = player.getLevel();

		if(level < min || level > max)
		{
			player.sendMessage(new CustomMessage("communityboard.teleport.point.level.min.max", player).addNumber(min).addNumber(max));
			return;
		}

		if(pk && player.getKarma() > 0)
		{
			player.sendMessage(new CustomMessage("communityboard.teleport.point.pk.denied", player));
			return;
		}

		if(premium && !player.hasBonus())
		{
			player.sendMessage(new CustomMessage("communityboard.teleport.point.only.premium", player));
			return;
		}

		if(!CheckConditions(player) || !checkTeleportLocation(player, loc.x, loc.y, loc.z, false, FORBIDDEN_ZONES_TP))
			return;

		if(player.getLevel() <= ConfigValue.TeleportFreeLevel)
		{
			item = priceId;
			price = 0;
		}
		else if(player.hasBonus())
		{
			item = premiumPriceId;
			price = premiumCount;
		}
		else
		{
			item = priceId;
			price = count;
		}

		if(DifferentMethods.getPay(player, item, price, true))
		{
			player.teleToLocation(x, y, z, 0);
			player.sendMessage(new CustomMessage("communityboard.teleport.point.success.location", player).addString(name));
			if(skill_id > 0)
			{
				L2Skill skill = SkillTable.getInstance().getInfo(skill_id, skill_level);
				if(skill != null)
					skill.getEffects(player, player, false, false);
			}
		}
	}

	private boolean goToTeleportPoint(L2Player player, int x, int y, int z)
	{
		Location loc = player.getLoc();
		int price;

		if(!CheckConditions(player) || !checkTeleportLocation(player, loc.x, loc.y, loc.z, false, FORBIDDEN_ZONES_TP))
			return false;

		if(player.hasBonus() && ConfigValue.TeleportPremiumFree || player.getLevel() <= ConfigValue.TeleportFreeLevel)
			price = 0;
		else
			price = ConfigValue.TeleportPrice;

		if(!DifferentMethods.getPay(player, ConfigValue.TeleportItem, price, true))
			return false;

		player.teleToLocation(x, y, z, 0);
		return true;
	}

	private void showTeleportPoint(L2Player player)
	{
		if(ConfigValue.TeleportPointOnlyPremium && !player.hasBonus())
		{
			player.sendMessage(new CustomMessage("communityboard.teleport.personal.point.only.premium", player));
			DifferentMethods.communityNextPage(player, "_bbsteleport");
			return;
		}

		StringBuilder html = new StringBuilder();

		html.append("<table width=220>");

		if(player._tpSchem.size() > 0)
			for(CBTpSch teleport : player._tpSchem.values())
			{
				html.append("<tr>");
				html.append("<td>");
				html.append("<button value=\"" + teleport.name + "\" action=\"bypass -h _bbsteleport:go_t " + teleport.x + " " + teleport.y + " " + teleport.z + " " + 100000 + "\" width=200 height=25 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\">");
				html.append("</td>");
				html.append("<td>");
				html.append("<button value=\"Удалить\" action=\"bypass -h _bbsteleport:delete " + teleport.id + "\" width=80 height=25 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\">");
				html.append("</td>)");
				html.append("</tr>");
			}

		html.append("</table>");

		String content = Files.read(ConfigValue.CommunityBoardHtmlRoot + "teleport/save.htm", player);
		content = content.replace("<?point?>", html.toString());
		separateAndSend(content, player);
	}

	private void showTeleportIndex(L2Player player, int index, int index2)
	{
		StringBuilder result = new StringBuilder();
		String content = Files.read(ConfigValue.CommunityBoardHtmlRoot + "teleport/index.htm", player);
		if(content == null)
			return;
		result.append("<table width=755>");
		PointGroup pg = null;
		int sz = _teleport_list.get(index).size();
		for(int i = 0; i < sz; i += 3)
		{
			if(i < sz)
			{
				pg = _teleport_list.get(index).get(i);
				result.append("<tr>");
				if(pg != null)
					result.append("<td valign=\"top\" align=\"center\"><button value=\"" + pg.name + "\" action=\"bypass -h _bbsteleport:list:" + index + ":" + i + "\" width=200 height=29 back=\"L2UI_CT1.OlympiadWnd_DF_Watch_Down\" fore=\"L2UI_CT1.OlympiadWnd_DF_Watch\"></td>");
			}
			if(i + 1 < sz)
			{
				pg = _teleport_list.get(index).get(i + 1);
				if(pg != null)
					result.append("<td valign=\"top\" align=\"center\"><button value=\"" + pg.name + "\" action=\"bypass -h _bbsteleport:list:" + index + ":" + (i + 1) + "\" width=200 height=29 back=\"L2UI_CT1.OlympiadWnd_DF_Watch_Down\" fore=\"L2UI_CT1.OlympiadWnd_DF_Watch\"></td>");
			}
			if(i + 2 < sz)
			{
				pg = _teleport_list.get(index).get(i + 2);
				if(pg != null)
					result.append("<td valign=\"top\" align=\"center\"><button value=\"" + pg.name + "\" action=\"bypass -h _bbsteleport:list:" + index + ":" + (i + 2) + "\" width=200 height=29 back=\"L2UI_CT1.OlympiadWnd_DF_Watch_Down\" fore=\"L2UI_CT1.OlympiadWnd_DF_Watch\"></td>");
			}
			result.append("</tr>");
		}
		result.append("</table>");
		content = content.replace("<?name?>", pg.group_name);
		content = content.replace("<?list?>", result.toString());

		content = content.replace("<?tp_price?>", Integer.toString(ConfigValue.TeleportPrice));
		content = content.replace("<?tp_max_count?>", Integer.toString(ConfigValue.TeleportMaxPoint));
		content = content.replace("<?tp_free_min_lvl?>", Integer.toString(ConfigValue.TeleportFreeLevel));
		separateAndSend(content, player);
	}

	private void deleteTeleportPoint(L2Player player, int id)
	{
		player._tpSchem.remove(id);
		ThreadConnection con = null;
		FiltredPreparedStatement statement = null;
		try
		{
			con = L2DatabaseFactory.getInstance().getConnection();
			statement = con.prepareStatement("DELETE FROM comteleport WHERE charId=? AND TpId=?;");
			statement.setInt(1, player.getObjectId());
			statement.setInt(2, id);
			statement.execute();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			DatabaseUtils.closeDatabaseCS(con, statement);
		}
	}

	private void addTeleportPoint(L2Player player, String name)
	{
		if(!player.isGM())
		{
			if(!CheckConditions(player) || !checkTeleportLocation(player, player.getX(), player.getY(), player.getZ(), true, FORBIDDEN_ZONES_SAVE))
				return;
			else if(ConfigValue.TeleportPointOnlyPremium && !player.hasBonus())
			{
				player.sendMessage(new CustomMessage("communityboard.teleport.personal.point.only.premium", player));
				return;
			}
			else if(player.isMovementDisabled() || player.isOutOfControl())
			{
				player.sendMessage(new CustomMessage("communityboard.teleport.persoanl.point.outofcontrol", player));
				return;
			}
			else if(player.isInCombat())
			{
				player.sendMessage(new CustomMessage("communityboard.teleport.persoanl.point.incombat", player));
				return;
			}
			else if(player.isInZone(L2Zone.ZoneType.battle_zone) || player.isInZone(L2Zone.ZoneType.no_escape) || player.isInZone(L2Zone.ZoneType.epic) || player.isInZone(L2Zone.ZoneType.Siege) || player.isInZone(L2Zone.ZoneType.RESIDENCE) || player.getVar("jailed") != null)
			{
				player.sendMessage(new CustomMessage("communityboard.teleport.persoanl.point.forbidden.zone", player));
				return;
			}
			else if(ConfigValue.TeleportPointOnlyStaticZone && !player.isInZone(L2Zone.ZoneType.zone_save_tp))
			{
				player.sendMessage(new CustomMessage("communityboard.teleport.persoanl.point.forbidden.zone", player));
				return;
			}
			else if(!Util.isMatchingRegexp(name, "([0-9A-Za-z]{1,16})|([0-9\u0410-\u044f]{1,16})"))
			{
				player.sendMessage("Символы запрещены.");
				return;
			}
			else if(player._tpSchem.size() >= ConfigValue.TeleportMaxPoint)
			{
				player.sendMessage(new CustomMessage("communityboard.teleport.personal.point.max", player).addNumber(ConfigValue.TeleportMaxPoint));
				return;
			}
		}

		boolean _name = false;
		for(CBTpSch sch1 : player._tpSchem.values())
			if(sch1.name.equalsIgnoreCase(name))
				_name = true;
		if(_name)
		{
			player.sendMessage("Это название уже занято.");
			return;
		}

		long price = player.hasBonus() && ConfigValue.TeleportPointPremiumFree ? 0 : ConfigValue.TeleportSavePrice;
		if(DifferentMethods.getPay(player, ConfigValue.TeleportSaveItem, price, true))
		{
			ThreadConnection con = null;
			FiltredPreparedStatement statement = null;
			ResultSet rs = null;
			try
			{
				con = L2DatabaseFactory.getInstance().getConnection();

				statement = con.prepareStatement("INSERT INTO comteleport (charId, xPos, yPos, zPos, name) VALUES(?,?,?,?,?)");
				statement.setInt(1, player.getObjectId());
				statement.setInt(2, player.getX());
				statement.setInt(3, player.getY());
				statement.setInt(4, player.getZ());
				statement.setString(5, name);
				statement.execute();
				DatabaseUtils.closeStatement(statement);

				statement = con.prepareStatement("SELECT TpId FROM comteleport WHERE charId=? AND name=?;");
				statement.setInt(1, player.getObjectId());
				statement.setString(2, name);
				rs = statement.executeQuery();
				rs.next();
				int id = rs.getInt(1);
				player._tpSchem.put(id, new CBTpSch(id, name, player.getX(), player.getY(), player.getZ()));
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
			finally
			{
				DatabaseUtils.closeDatabaseCSR(con, statement, rs);
			}
		}
	}

	private static boolean CheckConditions(L2Player player)
	{
		if(player == null)
			return false;
		else if(player.isGM())
			return true;
		else if(player.isInOlympiadMode())
		{
			player.sendPacket(Msg.YOU_CANNOT_USE_MY_TELEPORTS_WHILE_PARTICIPATING_IN_AN_OLYMPIAD_MATCH);
			return false;
		}
		else if(player.getReflection().getId() != ReflectionTable.DEFAULT && !ConfigValue.TeleportInInstance)
		{
			player.sendPacket(Msg.YOU_CANNOT_USE_MY_TELEPORTS_IN_AN_INSTANT_ZONE);
			return false;
		}
		else if(player.isInDuel())
		{
			player.sendPacket(Msg.YOU_CANNOT_USE_MY_TELEPORTS_DURING_A_DUEL);
			return false;
		}
		else if(player.isInCombat() && !ConfigValue.TeleportInCombat)
		{
			player.sendPacket(Msg.YOU_CANNOT_USE_MY_TELEPORTS_DURING_A_BATTLE);
			return false;
		}
		else if(player.getPvpFlag() != 0 && !ConfigValue.TeleportInPvpFlag)
		{
			player.sendPacket(Msg.YOU_CANNOT_USE_MY_TELEPORTS_DURING_A_BATTLE);
			return false;
		}
		else if((player.isOnSiegeField() || player.isInZoneBattle()) && !ConfigValue.TeleportOnSiege)
		{
			player.sendPacket(Msg.YOU_CANNOT_USE_MY_TELEPORTS_WHILE_PARTICIPATING_A_LARGE_SCALE_BATTLE_SUCH_AS_A_CASTLE_SIEGE);
			return false;
		}
		else if(player.isFlying())
		{
			player.sendPacket(Msg.YOU_CANNOT_USE_MY_TELEPORTS_WHILE_FLYING);
			return false;
		}
		if(player.isInWater() && !ConfigValue.TeleportInWater)
		{
			player.sendPacket(Msg.YOU_CANNOT_USE_MY_TELEPORTS_UNDERWATER);
			return false;
		}
		if(player.isDead() || player.isMovementDisabled() || player.isAlikeDead() || player.isCastingNow() || player.isAttackingNow() || player.getVar("jailed") != null || player.isCombatFlagEquipped() || player.isTerritoryFlagEquipped())
		{
			player.sendMessage(new CustomMessage("communityboard.teleport.terms.incorrect", player));
			return false;
		}
		return true;
	}

	private static boolean checkTeleportLocation(L2Player player, int x, int y, int z, boolean save, ZoneType[] FORBIDDEN_ZONES)
	{
		if(player == null)
			return false;
		else if(player.isGM())
			return true;
		if(player.getZones() != null)
			for(L2Zone zone : player.getZones())
				if(zone != null && Util.contains(save ? ConfigValue.TeleportNoForbiddenZonesSave : ConfigValue.TeleportNoForbiddenZonesTp, zone.getId()))
					return true;
		for(ZoneType zoneType : FORBIDDEN_ZONES)
		{
			L2Zone zone = player.getZone(zoneType);
			if(zone != null && (save || !Util.contains(ConfigValue.TeleportExcludeForbiddenZones, zone.getId())))
			{
				player.sendPacket(Msg.YOU_CANNOT_USE_MY_TELEPORTS_TO_REACH_THIS_AREA);
				return false;
			}
		}
		return true;
	}

	public static class PointGroup
	{
		public int group_id;
		public String name;
		public String group_name;
		public int id;
		public Point[] point_list;

		public PointGroup(int _group_id, String _name, String _group_name, int _id, Point[] _point_list)
		{
			group_id = _group_id;
			name = _name;
			group_name = _group_name;
			id = _id;
			point_list = _point_list;
		}
	}

	public static class Point
	{
		int p_id;
		String p_name_en;
		String p_name_ru;
		int p_min;
		int p_max;
		boolean p_pk;
		boolean premium_point;
		int item_id;
		long count;
		int premium_item_id;
		long premium_count;
		int x;
		int y;
		int z;
		int skill_id;
		int skill_level;

		public Point(int _p_id, String _p_name_en, String _p_name_ru, int _p_min, int _p_max, boolean _p_pk, boolean _premium_point, int _item_id, long _count, int _premium_item_id, long _premium_count, int _x, int _y, int _z, int i4, int i5)
		{
			p_id = _p_id;
			p_name_en = _p_name_en;
			p_name_ru = _p_name_ru;
			p_min = _p_min;
			p_max = _p_max;
			p_pk = _p_pk;
			premium_point = _premium_point;
			item_id = _item_id;
			count = _count;
			premium_item_id = _premium_item_id;
			premium_count = _premium_count;
			x = _x;
			y = _y;
			z = _z;
			skill_id = i4;
			skill_level = i5;
		}

		public String p_name(boolean ru)
		{
			return ru ? p_name_ru : p_name_en;
		}
	}

	private static HashMap<Integer, List<PointGroup>> _teleport_list;
	private static HashMap<String, Point> _teleport_name_list;

	private static ZoneType[] parseCommaSeparatedIntegerArray(String s)
	{
		if(s.isEmpty())
			return new ZoneType[] {};
		String[] tmp = s.replaceAll(",", ";").split(";");
		ZoneType[] ret = new ZoneType[tmp.length];
		for(int i = 0; i < tmp.length; i++)
			ret[i] = Enum.valueOf(ZoneType.class, tmp[i].trim());
		return ret;
	}

	public static void load()
	{
		FORBIDDEN_ZONES_SAVE = parseCommaSeparatedIntegerArray(ConfigValue.TeleportForbiddenZonesSave);
		FORBIDDEN_ZONES_TP = parseCommaSeparatedIntegerArray(ConfigValue.TeleportForbiddenZonesTp);
		try
		{
			File file;
			boolean develop = Boolean.parseBoolean(System.getenv("DEVELOP"));
			if (develop) {
				file = new File("data/xml/teleport_list.xml");
			} else {
				file = new File(ConfigValue.DatapackRoot + "/data/xml/teleport_list.xml");
			}

			if(file == null)
				return;

			Document doc = XmlUtils.readFile(file);
			Element list = doc.getRootElement();

			String lang = list.attributeValue("lang");
			int group_type_count = XmlUtils.getSafeInt(list, "group_type_count", 1);

			_teleport_list = new HashMap<Integer, List<PointGroup>>();
			_teleport_name_list = new HashMap<String, Point>();

			int list_size = 0;
			List<PointGroup> _list;
			for(Element teleport_group : list.elements("teleport_group"))
			{
				int group_id = XmlUtils.getSafeInt(teleport_group, "val", 0);
				String name = teleport_group.attributeValue("name");
				String group_name = teleport_group.attributeValue("group_name");
				int id = XmlUtils.getSafeInt(teleport_group, "id", 0);

				Point[] p = new Point[teleport_group.elements("point").size()];

				int p_id = 0;
				for(Element point : teleport_group.elements("point"))
				{
					String p_name_en = point.attributeValue("name_en") == null ? point.attributeValue("name") : point.attributeValue("name_en");
					String p_name_ru = point.attributeValue("name_ru") == null ? point.attributeValue("name") : point.attributeValue("name_ru");
					int p_min = XmlUtils.getSafeInt(point, "min", 0);
					int p_max = XmlUtils.getSafeInt(point, "max", 0);
					boolean p_pk = XmlUtils.getSafeBoolean(point, "pk", false);
					boolean premium_point = XmlUtils.getSafeBoolean(point, "premium_point", false);

					Element cost = point.element("cost");
					Element loc = point.element("loc");

					int item_id = XmlUtils.getSafeInt(cost, "item_id", 0);
					long count = XmlUtils.getSafeLong(cost, "count", 0);
					int premium_item_id = XmlUtils.getSafeInt(cost, "premium_item_id", 0);
					long premium_count = XmlUtils.getSafeLong(cost, "premium_count", 0);

					int x = XmlUtils.getSafeInt(loc, "x", 0);
					int y = XmlUtils.getSafeInt(loc, "y", 0);
					int z = XmlUtils.getSafeInt(loc, "z", 0);

					int skill_id = XmlUtils.getSafeInt(cost, "skill_id", 0);
					int skill_level = XmlUtils.getSafeInt(cost, "skill_level", 0);

					p[p_id] = new Point(p_id, p_name_en, p_name_ru, p_min, p_max, p_pk, premium_point, item_id, count, premium_item_id, premium_count, x, y, z, skill_id, skill_level);
					p_id++;
					list_size++;
				}

				if(_teleport_list.containsKey(group_id))
				{
					_list = _teleport_list.get(group_id);
					_list.add(new PointGroup(group_id, name, group_name, id, p));
				}
				else
				{
					_list = new ArrayList<PointGroup>();
					_list.add(new PointGroup(group_id, name, group_name, id, p));
				}
				_teleport_list.put(group_id, _list);
			}
			for(Element teleport_name : list.elements("teleport_name"))
			{
				for(Element point : teleport_name.elements("point"))
				{
					String p_name_en = point.attributeValue("name_en") == null ? point.attributeValue("name") : point.attributeValue("name_en");
					String p_name_ru = point.attributeValue("name_ru") == null ? point.attributeValue("name") : point.attributeValue("name_ru");

					int p_min = XmlUtils.getSafeInt(point, "min", 0);
					int p_max = XmlUtils.getSafeInt(point, "max", 0);
					boolean p_pk = XmlUtils.getSafeBoolean(point, "pk", false);
					boolean premium_point = XmlUtils.getSafeBoolean(point, "premium_point", false);

					Element cost = point.element("cost");
					Element loc = point.element("loc");

					int item_id = XmlUtils.getSafeInt(cost, "item_id", 0);
					long count = XmlUtils.getSafeLong(cost, "count", 0);
					int premium_item_id = XmlUtils.getSafeInt(cost, "premium_item_id", 0);
					long premium_count = XmlUtils.getSafeLong(cost, "premium_count", 0);

					int x = XmlUtils.getSafeInt(loc, "x", 0);
					int y = XmlUtils.getSafeInt(loc, "y", 0);
					int z = XmlUtils.getSafeInt(loc, "z", 0);

					int skill_id = XmlUtils.getSafeInt(cost, "skill_id", 0);
					int skill_level = XmlUtils.getSafeInt(cost, "skill_level", 0);

					_teleport_name_list.put(p_name_en, new Point(0, p_name_en, p_name_ru, p_min, p_max, p_pk, premium_point, item_id, count, premium_item_id, premium_count, x, y, z, skill_id, skill_level));
				}
			}
			
			_log.info("Load " + list_size + " teleport points.");
		}
		catch(DocumentException e)
		{
			e.printStackTrace();
		}
	}

	@Override
	public void onLoad()
	{
		CommunityHandler.getInstance().registerCommunityHandler(this);
		load();
	}

	@Override
	public void onReload()
	{
		load();
	}

	@Override
	public void onShutdown()
	{}

	@SuppressWarnings("rawtypes")
	@Override
	public Enum[] getCommunityCommandEnum()
	{
		return Commands.values();
	}

	@Override
	public void parsewrite(String ar1, String ar2, String ar3, String ar4, String ar5, L2Player activeChar)
	{}
}