package l2open.gameserver.instancemanager;

import l2open.config.*;
import l2open.database.DatabaseUtils;
import l2open.database.FiltredPreparedStatement;
import l2open.database.L2DatabaseFactory;
import l2open.database.ThreadConnection;
import l2open.gameserver.model.L2Clan;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.base.Experience;
import l2open.gameserver.tables.ClanTable;
import l2open.gameserver.tables.player.PlayerData;

import java.util.logging.Logger;

/**
 * Набор статичных метордов для работы с игроками.
 */
public class PlayerManager
{
	protected static Logger _log = Logger.getLogger(PlayerManager.class.getName());

	public static void saveCharToDisk(L2Player cha)
	{
		try
		{
			cha.getInventory().updateDatabase(true);
			PlayerData.getInstance().store(cha, false);
		}
		catch(Exception e)
		{
			_log.warning("Error saving player character: " + e);
			e.printStackTrace();
		}
	}

	public static void deleteFromClan(int charId, int clanId)
	{
		if(clanId == 0)
			return;
		L2Clan clan = ClanTable.getInstance().getClan(clanId);
		if(clan != null)
			clan.removeClanMember(charId);
	}

	public static void deleteCharByObjId(int objid)
	{
		if(objid < 0)
			return;
		ThreadConnection con = null;
		FiltredPreparedStatement statement = null;
		try
		{
			con = L2DatabaseFactory.getInstance().getConnection();
			statement = con.prepareStatement("DELETE item_attributes FROM item_attributes, items WHERE item_attributes.itemId=items.object_id AND items.owner_id=?");
			statement.setInt(1, objid);
			statement.execute();
			DatabaseUtils.closeStatement(statement);

			con = L2DatabaseFactory.getInstance().getConnection();
			statement = con.prepareStatement("DELETE FROM characters WHERE obj_Id=?");
			statement.setInt(1, objid);
			statement.execute();
			DatabaseUtils.closeStatement(statement);

			statement = con.prepareStatement("DELETE FROM character_shortcuts WHERE char_obj_id=?");
			statement.setInt(1, objid);
			statement.execute();
			DatabaseUtils.closeStatement(statement);

			statement = con.prepareStatement("DELETE FROM character_macroses WHERE char_obj_id=?");
			statement.setInt(1, objid);
			statement.execute();
			DatabaseUtils.closeStatement(statement);

			statement = con.prepareStatement("DELETE pets FROM pets, items WHERE pets.item_obj_id=items.object_id AND items.owner_id=?");
			statement.setInt(1, objid);
			statement.execute();
			DatabaseUtils.closeStatement(statement);

			statement = con.prepareStatement("DELETE FROM items WHERE owner_id=?");
			statement.setInt(1, objid);
			statement.execute();
			DatabaseUtils.closeStatement(statement);

			statement = con.prepareStatement("DELETE FROM character_skills WHERE char_obj_Id=?");
			statement.setInt(1, objid);
			statement.execute();
			DatabaseUtils.closeStatement(statement);

			statement = con.prepareStatement("DELETE FROM character_effects_save WHERE char_obj_Id=?");
			statement.setInt(1, objid);
			statement.execute();
			DatabaseUtils.closeStatement(statement);

			statement = con.prepareStatement("DELETE FROM character_skills_save WHERE char_obj_Id=?");
			statement.setInt(1, objid);
			statement.execute();
			DatabaseUtils.closeStatement(statement);

			statement = con.prepareStatement("DELETE FROM character_quests WHERE char_id=?");
			statement.setInt(1, objid);
			statement.execute();
			DatabaseUtils.closeStatement(statement);

			statement = con.prepareStatement("DELETE FROM character_recipebook WHERE char_id=?");
			statement.setInt(1, objid);
			statement.execute();
			DatabaseUtils.closeStatement(statement);

			statement = con.prepareStatement("DELETE FROM character_friends WHERE char_id=? or friend_id=?");
			statement.setInt(1, objid);
			statement.setInt(2, objid);
			statement.execute();
			DatabaseUtils.closeStatement(statement);

			statement = con.prepareStatement("DELETE FROM seven_signs WHERE char_obj_id=?");
			statement.setInt(1, objid);
			statement.execute();
			DatabaseUtils.closeStatement(statement);

			statement = con.prepareStatement("DELETE FROM character_subclasses WHERE char_obj_id=?");
			statement.setInt(1, objid);
			statement.execute();
			DatabaseUtils.closeStatement(statement);
			statement = null;
		}
		catch(Exception e)
		{
			_log.warning("data error on delete char:" + e);
		}
		finally
		{
			DatabaseUtils.closeDatabaseCS(con, statement);
		}
	}

	/**
	 * Create a new player in the characters table of the database.
	 */
	public static boolean createDb(L2Player player, int bot, int level)
	{
		ThreadConnection con = null;
		FiltredPreparedStatement statement = null;
		try
		{
			con = L2DatabaseFactory.getInstance().getConnection();
			statement = con.prepareStatement("INSERT INTO `characters` (account_name, obj_Id, char_name, face, hairStyle, hairColor, sex, karma, pvpkills, pkkills, clanid, createtime, deletetime, title, accesslevel, online, leaveclan, deleteclan, nochannel, pledge_type, pledge_rank, lvl_joined_academy, apprentice, bot) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
			statement.setString(1, player.getAccountName());
			statement.setInt(2, player.getObjectId());
			statement.setString(3, player.getName());
			statement.setInt(4, player.getFace());
			statement.setInt(5, player.getHairStyle());
			statement.setInt(6, player.getHairColor());
			statement.setInt(7, player.getSex());
			statement.setInt(8, player.getKarma());
			statement.setInt(9, player.getPvpKills());
			statement.setInt(10, player.getPkKills());
			statement.setInt(11, player.getClanId());
			statement.setLong(12, player.getCreateTime() / 1000);
			statement.setInt(13, 0);
			statement.setString(14, player.getTitle());
			statement.setInt(15, player.getAccessLevel());
			statement.setInt(16, player.isOnline() ? 1 : 0);
			statement.setLong(17, player.getLeaveClanTime() / 1000);
			statement.setLong(18, player.getDeleteClanTime() / 1000);
			statement.setLong(19, player.getNoChannel() > 0 ? player.getNoChannel() / 1000 : player.getNoChannel());
			statement.setInt(20, player.getPledgeType());
			statement.setInt(21, player.getPowerGrade());
			statement.setInt(22, player.getLvlJoinedAcademy());
			statement.setInt(23, player.getApprentice());
			statement.setInt(24, bot);
			statement.executeUpdate();
			DatabaseUtils.closeStatement(statement);

			statement = con.prepareStatement("INSERT INTO character_subclasses (char_obj_id, class_id, exp, sp, curHp, curMp, curCp, maxHp, maxMp, maxCp, level, active, isBase, death_penalty, certification) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
			statement.setInt(1, player.getObjectId());
			statement.setInt(2, player.getTemplate().classId.getId());
			statement.setLong(3, Experience.LEVEL[level]);
			statement.setLong(4, ConfigValue.CharacterCreateSP);
			statement.setDouble(5, player.getTemplate().baseHpMax + player.getTemplate().lvlHpAdd + player.getTemplate().lvlHpMod);
			statement.setDouble(6, player.getTemplate().baseMpMax + player.getTemplate().lvlMpAdd + player.getTemplate().lvlMpMod);
			statement.setDouble(7, player.getTemplate().baseCpMax + player.getTemplate().lvlCpAdd + player.getTemplate().lvlCpMod);
			statement.setDouble(8, player.getTemplate().baseHpMax + player.getTemplate().lvlHpAdd + player.getTemplate().lvlHpMod);
			statement.setDouble(9, player.getTemplate().baseMpMax + player.getTemplate().lvlMpAdd + player.getTemplate().lvlMpMod);
			statement.setDouble(10, player.getTemplate().baseCpMax + player.getTemplate().lvlCpAdd + player.getTemplate().lvlCpMod);
			statement.setInt(11, level); // Level
			statement.setInt(12, 1);
			statement.setInt(13, 1);
			statement.setInt(14, 0);
			statement.setInt(15, 0);
			statement.executeUpdate();
		}
		catch(final Exception e)
		{
			e.printStackTrace();
			return false;
		}
		finally
		{
			DatabaseUtils.closeDatabaseCS(con, statement);
		}
		return true;
	}
}