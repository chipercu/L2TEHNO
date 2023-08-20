package l2open.gameserver.model.entity;

import javolution.util.FastMap;
import l2open.config.ConfigValue;
import l2open.database.*;
import l2open.extensions.scripts.Functions;
import l2open.gameserver.model.*;
import l2open.gameserver.model.entity.olympiad.Olympiad;
import l2open.gameserver.model.items.L2ItemInstance;
import l2open.gameserver.model.items.L2ItemInstance.ItemClass;
import l2open.gameserver.serverpackets.NpcHtmlMessage;
import l2open.gameserver.serverpackets.SocialAction;
import l2open.gameserver.serverpackets.SystemMessage;
import l2open.gameserver.tables.ClanTable;
import l2open.gameserver.tables.SkillTable;
import l2open.gameserver.templates.L2Item;
import l2open.gameserver.templates.StatsSet;
import l2open.util.*;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

public class Hero
{
	private static Logger _log = Logger.getLogger(Hero.class.getName());

	private static Hero _instance;
	private static final String GET_HEROES = "SELECT * FROM heroes WHERE played = 1";
	private static final String GET_ALL_HEROES = "SELECT * FROM heroes";

	private static Map<Integer, StatsSet> _heroes;
	private static Map<Integer, StatsSet> _completeHeroes;
	private static Map<Integer, List<HeroDiary>> _herodiary;
	private static Map<Integer, String> _heroMessage;

	public static final String COUNT = "count";
	public static final String PLAYED = "played";
	public static final String CLAN_NAME = "clan_name";
	public static final String CLAN_CREST = "clan_crest";
	public static final String ALLY_NAME = "ally_name";
	public static final String ALLY_CREST = "ally_crest";
	public static final String ACTIVE = "active";

	public static Hero getInstance()
	{
		if(_instance == null)
			_instance = new Hero();
		return _instance;
	}

	public Hero()
	{
		init();
	}

	private static void HeroSetClanAndAlly(int charId, StatsSet hero)
	{
		Entry<L2Clan, L2Alliance> e = ClanTable.getInstance().getClanAndAllianceByCharId(charId);
		hero.set(CLAN_CREST, e.getKey() == null ? 0 : e.getKey().getCrestId());
		hero.set(CLAN_NAME, e.getKey() == null ? "" : e.getKey().getName());
		hero.set(ALLY_CREST, e.getValue() == null ? 0 : e.getValue().getAllyCrestId());
		hero.set(ALLY_NAME, e.getValue() == null ? "" : e.getValue().getAllyName());
		e = null;
	}

	private void init()
	{
		_heroes = new FastMap<Integer, StatsSet>().setShared(true);
		_completeHeroes = new FastMap<Integer, StatsSet>().setShared(true);
		_herodiary = new ConcurrentHashMap<Integer, List<HeroDiary>>();
		_heroMessage = new ConcurrentHashMap<Integer, String>();

		ThreadConnection con = null;
		FiltredPreparedStatement statement = null;
		ResultSet rset = null;

		try
		{
			con = L2DatabaseFactory.getInstance().getConnection();

			statement = con.prepareStatement(GET_HEROES);
			rset = statement.executeQuery();
			while(rset.next())
			{
				StatsSet hero = new StatsSet();
				int charId = rset.getInt(Olympiad.CHAR_ID);
				hero.set(Olympiad.CHAR_NAME, Olympiad.getNobleName(charId));
				hero.set(Olympiad.CLASS_ID, Olympiad.getNobleClass(charId));
				hero.set(COUNT, rset.getInt(COUNT));
				hero.set(PLAYED, rset.getInt(PLAYED));
				hero.set(ACTIVE, rset.getInt(ACTIVE));
				HeroSetClanAndAlly(charId, hero);
				loadDiary(charId);
				loadMessage(charId);
				_heroes.put(charId, hero);
			}
			DatabaseUtils.closeDatabaseSR(statement, rset);

			statement = con.prepareStatement(GET_ALL_HEROES);
			rset = statement.executeQuery();
			while(rset.next())
			{
				StatsSet hero = new StatsSet();
				int charId = rset.getInt(Olympiad.CHAR_ID);
				hero.set(Olympiad.CHAR_NAME, Olympiad.getNobleName(charId));
				hero.set(Olympiad.CLASS_ID, Olympiad.getNobleClass(charId));
				hero.set(COUNT, rset.getInt(COUNT));
				hero.set(PLAYED, rset.getInt(PLAYED));
				hero.set(ACTIVE, rset.getInt(ACTIVE));
				HeroSetClanAndAlly(charId, hero);
				_completeHeroes.put(charId, hero);
			}
		}
		catch(SQLException e)
		{
			_log.warning("Hero System: Couldnt load Heroes");
		}
		finally
		{
			DatabaseUtils.closeDatabaseCSR(con, statement, rset);
		}

		_log.info("Hero System: Loaded " + _heroes.size() + " Heroes.");
		_log.info("Hero System: Loaded " + _completeHeroes.size() + " all time Heroes.");
	}

	public Map<Integer, StatsSet> getHeroes()
	{
		return _heroes;
	}

	public synchronized void clearHeroes()
	{
		mysql.set("UPDATE heroes SET played = 0, active = 0");

		if(!_heroes.isEmpty())
			for(StatsSet hero : _heroes.values())
			{
				if(hero.getInteger(ACTIVE) == 0)
					continue;

				String name = hero.getString(Olympiad.CHAR_NAME);

				L2Player player = L2World.getPlayer(name);

				if(player != null)
				{
					player.getInventory().unEquipItemInBodySlot(L2Item.SLOT_L_HAND, null);
					player.getInventory().unEquipItemInBodySlot(L2Item.SLOT_R_HAND, null);
					player.getInventory().unEquipItemInBodySlot(L2Item.SLOT_LR_HAND, null);
					player.getInventory().unEquipItemInBodySlot(L2Item.SLOT_HAIR, null);
					player.getInventory().unEquipItemInBodySlot(L2Item.SLOT_HAIRALL, null);
					player.getInventory().unEquipItemInBodySlot(L2Item.SLOT_DHAIR, null);

					for(L2ItemInstance item : player.getInventory().getItems())
					{
						if(item == null)
							continue;
						if(item.isHeroWeapon())
							player.getInventory().destroyItem(item, 1, true);
					}

					for(L2ItemInstance item : player.getWarehouse().listItems(ItemClass.EQUIPMENT))
					{
						if(item == null)
							continue;
						if(item.isHeroWeapon())
							player.getWarehouse().destroyItem(item.getItemId(), 1);
					}

					player.setHero(false, -1);
					player.updatePledgeClass();
					player.broadcastUserInfo(true);
				}
			}

		_heroes.clear();
		_herodiary.clear();
	}

	public synchronized boolean computeNewHeroes(GArray<StatsSet> newHeroes)
	{
		if(newHeroes.size() == 0)
			return true;

		Map<Integer, StatsSet> heroes = new FastMap<Integer, StatsSet>().setShared(true);
		boolean error = false;

		for(StatsSet hero : newHeroes)
		{
			int charId = hero.getInteger(Olympiad.CHAR_ID);

			if(_completeHeroes != null && _completeHeroes.containsKey(charId))
			{
				StatsSet oldHero = _completeHeroes.get(charId);
				int count = oldHero.getInteger(COUNT);
				oldHero.set(COUNT, count + 1);
				oldHero.set(PLAYED, 1);
				oldHero.set(ACTIVE, 0);

				heroes.put(charId, oldHero);
			}
			else
			{
				StatsSet newHero = new StatsSet();
				newHero.set(Olympiad.CHAR_NAME, hero.getString(Olympiad.CHAR_NAME));
				newHero.set(Olympiad.CLASS_ID, hero.getInteger(Olympiad.CLASS_ID));
				newHero.set(COUNT, 1);
				newHero.set(PLAYED, 1);
				newHero.set(ACTIVE, 0);

				heroes.put(charId, newHero);
			}
			addHeroDiary(charId, 2, 0);
			loadDiary(charId);
		}

		_heroes.putAll(heroes);
		heroes.clear();

		updateHeroes(0);

		return error;
	}

	public void updateHeroes(int id)
	{
		ThreadConnection con = null;
		FiltredPreparedStatement statement = null;
		try
		{
			con = L2DatabaseFactory.getInstance().getConnection();
			statement = con.prepareStatement("REPLACE INTO heroes VALUES (?,?,?,?, ?)");

			for(Integer heroId : _heroes.keySet())
			{
				if(id > 0 && heroId != id)
					continue;
				StatsSet hero = _heroes.get(heroId);
				try
				{
					statement.setInt(1, heroId);
					statement.setInt(2, hero.getInteger(COUNT));
					statement.setInt(3, hero.getInteger(PLAYED));
					statement.setInt(4, hero.getInteger(ACTIVE));
					statement.setString(5, "");
					statement.execute();
					if(_completeHeroes != null && !_completeHeroes.containsKey(heroId))
					{
						HeroSetClanAndAlly(heroId, hero);
						_completeHeroes.put(heroId, hero);
					}
				}
				catch(SQLException e)
				{
					_log.warning("Hero System: Couldnt update Hero: " + heroId);
					e.printStackTrace();
				}
			}

		}
		catch(SQLException e)
		{
			_log.warning("Hero System: Couldnt update Heroes");
			e.printStackTrace();
		}
		finally
		{
			DatabaseUtils.closeDatabaseCS(con, statement);
		}
	}

	public boolean isHero(int id)
	{
		if(_heroes == null || _heroes.isEmpty())
			return false;
		if(_heroes.containsKey(id) && _heroes.get(id).getInteger(ACTIVE) == 1)
			return true;
		return false;
	}

	public boolean isInactiveHero(int id)
	{
		if(_heroes == null || _heroes.isEmpty())
			return false;
		if(_heroes.containsKey(id) && _heroes.get(id).getInteger(ACTIVE) == 0)
			return true;
		return false;
	}

	public void activateHero(L2Player player)
	{
		StatsSet hero = _heroes.get(player.getObjectId());
		hero.set(ACTIVE, 1);
		_heroes.remove(player.getObjectId());
		_heroes.put(player.getObjectId(), hero);

		if(!player.isSubClassActive())
			addSkills(player);

		player.setHero(true, 0);
		player.updatePledgeClass();
		player.broadcastPacket2(new SocialAction(player.getObjectId(), SocialAction.GIVE_HERO));
		if(ConfigValue.ActivateHeroReward.length > 0)
			for(int i = 0; i < ConfigValue.ActivateHeroReward.length; i += 3)
				if(Rnd.chance(ConfigValue.ActivateHeroReward[i+2]))
					Functions.addItem(player, (int)ConfigValue.ActivateHeroReward[i], (long)ConfigValue.ActivateHeroReward[i+1]);

		if(player.getClan() != null && player.getClan().getLevel() >= 5)
		{
			player.getClan().incReputation(1000, true, "Hero:activateHero:" + player);
			player.getClan().broadcastToOtherOnlineMembers(new SystemMessage(SystemMessage.CLAN_MEMBER_S1_WAS_NAMED_A_HERO_2S_POINTS_HAVE_BEEN_ADDED_TO_YOUR_CLAN_REPUTATION_SCORE).addString(player.getName()).addNumber(Math.round(1000 * ConfigValue.RateClanRepScore)), player);
		}
		player.broadcastUserInfo(true);
		updateHeroes(player.getObjectId());
	}

	public static void addSkills(L2Player player)
	{
		player.addSkill(SkillTable.getInstance().getInfo(L2Skill.SKILL_HEROIC_MIRACLE, 1));
		player.addSkill(SkillTable.getInstance().getInfo(L2Skill.SKILL_HEROIC_BERSERKER, 1));
		player.addSkill(SkillTable.getInstance().getInfo(L2Skill.SKILL_HEROIC_VALOR, 1));
		player.addSkill(SkillTable.getInstance().getInfo(L2Skill.SKILL_HEROIC_GRANDEUR, 1));
		player.addSkill(SkillTable.getInstance().getInfo(L2Skill.SKILL_HEROIC_DREAD, 1));
	}

	public static void removeSkills(L2Player player)
	{
		player.removeSkillById(L2Skill.SKILL_HEROIC_MIRACLE, false);
		player.removeSkillById(L2Skill.SKILL_HEROIC_BERSERKER, false);
		player.removeSkillById(L2Skill.SKILL_HEROIC_VALOR, false);
		player.removeSkillById(L2Skill.SKILL_HEROIC_GRANDEUR, false);
		player.removeSkillById(L2Skill.SKILL_HEROIC_DREAD, false);
		player.updateEffectIcons();
	}

	public void loadDiary(int charId)
	{
		List<HeroDiary> diary = new ArrayList<HeroDiary>();

		ThreadConnection con = null;
		FiltredPreparedStatement statement = null;
		ResultSet rset = null;
		try
		{
			con = L2DatabaseFactory.getInstance().getConnection();
			statement = con.prepareStatement("SELECT * FROM  heroes_diary WHERE charId=? ORDER BY time ASC");
			statement.setInt(1, charId);
			rset = statement.executeQuery();
			while(rset.next())
			{
				long time = rset.getLong("time");
				int action = rset.getInt("action");
				int param = rset.getInt("param");

				HeroDiary d = new HeroDiary(action, time, param);
				diary.add(d);
			}

			_herodiary.put(charId, diary);
		}
		catch (SQLException e)
		{
			_log.warning(new StringBuilder().append("Hero System: Couldnt load Hero Diary for CharId: ").append(charId).toString());
		}
		finally
		{
			DatabaseUtils.closeDatabaseCSR(con, statement, rset);
		}
	}

	public void showHeroDiary(L2Player activeChar, int heroclass, int charid, int page)
	{
		final int perpage = 10;

		List<HeroDiary> mainlist = _herodiary.get(charid);

		if(mainlist != null)
		{
			NpcHtmlMessage html = new NpcHtmlMessage(activeChar, null);
			html.setFile("./data/html/olympiad/monument_hero_info.htm");
			html.replace("%title%", activeChar.getLang().equals("ru") ? "Сообщение Героя %heroname% : %message%": "%heroname%'s Hero Message : %message%");
			html.replace("%heroname%", Olympiad.getNobleName(charid));
			html.replace("%message%", _heroMessage.get(charid) == null ? (activeChar.getLang().equals("ru") ? "Нет сообщения.": "No message.") : _heroMessage.get(charid));

			List<HeroDiary> list = new ArrayList<HeroDiary>(mainlist);

			Collections.reverse(list);

			boolean color = true;
			final StringBuilder fList = new StringBuilder(500);
			int counter = 0;
			int breakat = 0;
			for(int i = (page - 1) * perpage; i < list.size(); i++)
			{
				breakat = i;
				HeroDiary diary = list.get(i);
				Map.Entry<String, String> entry = diary.toString(activeChar);

				fList.append("<tr><td>");
				if(color)
					fList.append("<table width=270 bgcolor=\"131210\">");
				else
					fList.append("<table width=270>");
				fList.append("<tr><td width=270><font color=\"LEVEL\">" + entry.getKey() + "</font></td></tr>");
				fList.append("<tr><td width=270>" + entry.getValue() + "</td></tr>");
				fList.append("<tr><td>&nbsp;</td></tr></table>");
				fList.append("</td></tr>");
				color = !color;
				counter++;
				if(counter >= perpage)
					break;
			}

			if(breakat < list.size() - 1)
			{
				html.replace("%buttprev%", "<button value=\"Prev\" action=\"bypass _diary?class=" + heroclass + "&page=" + (page + 1) + "\" width=60 height=25 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\">");
				html.replace("%prev_bypass%", "_diary?class=" + heroclass + "&page=" + (page + 1));
			}
			else
				html.replace("%buttprev%", "");

			if(page > 1)
			{
				html.replace("%buttnext%", "<button value=\"Next\" action=\"bypass _diary?class=" + heroclass + "&page=" + (page - 1) + "\" width=60 height=25 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\">");
				html.replace("%next_bypass%", "_diary?class=" + heroclass + "&page=" + (page - 1));
			}
			else
				html.replace("%buttnext%", "");

			html.replace("%list%", fList.toString());

			activeChar.sendPacket(html);
		}
	}

	public void addHeroDiary(int playerId, int id, int param)
	{
		insertHeroDiary(playerId, id, param);

		List<HeroDiary> list = _herodiary.get(playerId);
		if (list != null)
			list.add(new HeroDiary(id, System.currentTimeMillis(), param));
	}

	private void insertHeroDiary(int charId, int action, int param)
	{
		ThreadConnection con = null;
		FiltredPreparedStatement statement = null;
		try
		{
			con = L2DatabaseFactory.getInstance().getConnection();
			statement = con.prepareStatement("INSERT INTO heroes_diary (charId, time, action, param) values(?,?,?,?)");
			statement.setInt(1, charId);
			statement.setLong(2, System.currentTimeMillis());
			statement.setInt(3, action);
			statement.setInt(4, param);
			statement.execute();
			statement.close();
		}
		catch (SQLException e)
		{
			_log.warning("SQL exception while saving DiaryData.");
		}
		finally
		{
			DatabaseUtils.closeDatabaseCS(con, statement);
		}
	}

	public void loadMessage(int charId)
	{
		ThreadConnection con = null;
		FiltredPreparedStatement statement = null;
		ResultSet rset = null;
		try
		{
			String message = null;
			con = L2DatabaseFactory.getInstance().getConnection();
			statement = con.prepareStatement("SELECT message FROM heroes WHERE char_id=?");
			statement.setInt(1, charId);
			rset = statement.executeQuery();
			rset.next();
			message = rset.getString("message");
			_heroMessage.put(charId, message);
		}
		catch (SQLException e)
		{
			_log.warning(new StringBuilder().append("Hero System: Couldnt load Hero Message for CharId: ").append(charId).toString());
		}
		finally
		{
			DatabaseUtils.closeDatabaseCSR(con, statement, rset);
		}
	}

	public void setHeroMessage(int charId, String message)
	{
		_heroMessage.put(charId, message);
	}

	public void saveHeroMessage(int charId)
	{
		if(_heroMessage.get(charId) == null)
			return;

		ThreadConnection con = null;
		FiltredPreparedStatement statement = null;
		try
		{
			con = L2DatabaseFactory.getInstance().getConnection();
			statement = con.prepareStatement("UPDATE heroes SET message=? WHERE char_id=?;");
			statement.setString(1, _heroMessage.get(charId));
			statement.setInt(2, charId);
			statement.execute();
			statement.close();
		}
		catch (SQLException e)
		{
			_log.warning("SQL exception while saving HeroMessage.");
		}
		finally
		{
			DatabaseUtils.closeDatabaseCS(con, statement);
		}
	}

	public void shutdown()
	{
		for(int charId: _heroMessage.keySet())
			saveHeroMessage(charId);
	}

	public int getHeroByClass(int classid)
	{
		if (!_heroes.isEmpty())
			for (Integer heroId : _heroes.keySet())
			{
				StatsSet hero = _heroes.get(heroId);
				if (hero.getInteger("class_id") == classid)
					return heroId;
			}
		return 0;
	}

	public Entry<Integer, StatsSet> getHeroStats(int classId)
	{
		if (!_heroes.isEmpty())
		{
			for(Entry<Integer, StatsSet> entry : _heroes.entrySet())
			{
				if(entry.getValue().getInteger("class_id") == classId)
					return entry;
			}
		}
		return null;
	}
}