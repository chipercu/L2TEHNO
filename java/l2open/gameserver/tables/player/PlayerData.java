package l2open.gameserver.tables.player;

import l2open.common.*;
import l2open.config.ConfigValue;
import l2open.database.*;
import l2open.extensions.Stat;
import l2open.extensions.multilang.CustomMessage;
import l2open.extensions.scripts.Functions;
import l2open.gameserver.RecipeController;
import l2open.gameserver.cache.CrestCache;
import l2open.gameserver.cache.Msg;
import l2open.gameserver.geodata.*;
import l2open.gameserver.idfactory.*;
import l2open.gameserver.instancemanager.*;
import l2open.gameserver.model.*;
import l2open.gameserver.model.L2ObjectTasks.*;
import l2open.gameserver.model.L2Zone.ZoneType;
import l2open.gameserver.model.barahlo.*;
import l2open.gameserver.model.barahlo.attainment.*;
import l2open.gameserver.model.base.*;
import l2open.gameserver.model.entity.Hero;
import l2open.gameserver.model.entity.SevenSignsFestival.SevenSignsFestival;
import l2open.gameserver.model.instances.*;
import l2open.gameserver.model.items.*;
import l2open.gameserver.model.items.L2ItemInstance.ItemClass;
import l2open.gameserver.model.items.L2ItemInstance.ItemLocation;
import l2open.gameserver.model.items.MailParcelController.Letter;
import l2open.gameserver.model.entity.olympiad.*;
import l2open.gameserver.model.entity.siege.Siege;
import l2open.gameserver.model.entity.siege.territory.TerritorySiege;
import l2open.gameserver.model.quest.Quest;
import l2open.gameserver.network.L2GameClient;
import l2open.gameserver.network.L2GameClient.GameClientState;
import l2open.gameserver.serverpackets.*;
import l2open.gameserver.skills.*;
import l2open.gameserver.skills.effects.EffectTemplate;
import l2open.gameserver.skills.funcs.FuncAdd;
import l2open.gameserver.skills.funcs.FuncTemplate;
import l2open.gameserver.tables.*;
import l2open.gameserver.taskmanager.*;
import l2open.gameserver.templates.*;
import l2open.gameserver.xml.ItemTemplates;
import l2open.gameserver.xml.loader.XmlOptionDataLoader;
import l2open.util.*;

import javolution.util.FastMap;

import org.apache.commons.lang3.StringEscapeUtils;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;

@SuppressWarnings( { "nls", "unqualified-field-access", "boxing" })
public class PlayerData
{
	private static final Logger _log = Logger.getLogger(PlayerData.class.getName());

	private static PlayerData _instance;

	public static PlayerData getInstance()
	{
		if(_instance == null)
			_instance = new PlayerData();
		return _instance;
	}

	public void loadHwidLock(L2Player player)
	{
		ThreadConnection con = null;
		FiltredPreparedStatement statement = null;
		ResultSet rset = null;

		try
		{
			con = L2DatabaseFactory.getInstance().getConnection();
			statement = con.prepareStatement("SELECT hwid FROM hwid_lock WHERE login=?");
			statement.setString(1, player.getAccountName());
			rset = statement.executeQuery();
			while(rset.next())
				player.setAccLock(rset.getString(1).split(";")) ;
		}
		catch(SQLException e)
		{
			_log.warning("PlayerData error:" + e.getMessage());
		}
		finally
		{
			DatabaseUtils.closeDatabaseCSR(con, statement, rset);
		}
	}

	public boolean isHwidLock(String acc)
	{
		ThreadConnection con = null;
		FiltredPreparedStatement statement = null;
		ResultSet rset = null;

		try
		{
			con = L2DatabaseFactory.getInstance().getConnection();
			statement = con.prepareStatement("SELECT hwid FROM hwid_lock WHERE login=?");
			statement.setString(1, acc);
			rset = statement.executeQuery();
			if(rset.next())
				return true;
		}
		catch(SQLException e)
		{
			_log.warning("PlayerData error:" + e.getMessage());
		}
		finally
		{
			DatabaseUtils.closeDatabaseCSR(con, statement, rset);
		}
		return false;
	}

	public void clearHwidLock(L2Player player)
	{
		ThreadConnection con = null;
		FiltredPreparedStatement statement = null;
		try
		{
			con = L2DatabaseFactory.getInstance().getConnection();
			statement = con.prepareStatement("DELETE FROM hwid_lock WHERE login=?");
			statement.setString(1, player.getAccountName());
			statement.executeUpdate();
		}
		catch(final Exception e)
		{
			_log.log(Level.WARNING, "PlayerData clearHwidLock:", e);
		}
		finally
		{
			DatabaseUtils.closeDatabaseCS(con, statement);
		}
	}

	public void editHwidLock(L2Player player, String hwid_list)
	{
		ThreadConnection con = null;
		FiltredPreparedStatement statement = null;
		try
		{
			con = L2DatabaseFactory.getInstance().getConnection();

			statement = con.prepareStatement("REPLACE INTO hwid_lock (login,hwid) values(?,?)");
			statement.setString(1, player.getAccountName());
			statement.setString(2, hwid_list);
			statement.execute();
		}
		catch(final Exception e)
		{
			_log.log(Level.WARNING, "PlayerData editHwidLock:", e);
		}
		finally
		{
			DatabaseUtils.closeDatabaseCS(con, statement);
		}
	}

	/**
	 * Загружаем самонов\петов после входа, если это возможно.
	 */
	public void restoreSummon(final L2Player player)
	{
		ThreadConnection con = null;
		FiltredPreparedStatement statement = null, statement1 = null;
		ResultSet rset = null;
		try
		{
			con = L2DatabaseFactory.getInstance().getConnection();
			statement = con.prepareStatement("SELECT item_obj_id, class_id, npc_id, life_time, item_consume_idInTime, item_consume_countInTime, item_consume_delay, exp_penalty FROM summon_save WHERE char_obj_id=?");
			statement.setInt(1, player.getObjectId());
			rset = statement.executeQuery();
			while(rset.next())
			{
				if(player.getPet() != null)
				{
					statement1 = con.prepareStatement("DELETE FROM summon_save WHERE char_obj_id = " + player.getObjectId() + "");
					statement1.executeUpdate();
				}
				int controlItem = rset.getInt("item_obj_id");
				boolean summon1 = controlItem == 0;

				int npcId = rset.getInt("npc_id");
				if(npcId == 0)
					return;

				L2NpcTemplate petTemplate = NpcTable.getTemplate(npcId);
				if(petTemplate == null)
					return;

				if(summon1 && rset.getInt("class_id") == player.getClassId().getId())
				{
					L2SummonInstance summon = new L2SummonInstance(IdFactory.getInstance().getNextId(), petTemplate, player, rset.getInt("life_time"), rset.getInt("item_consume_idInTime"), rset.getInt("item_consume_countInTime"), rset.getInt("item_consume_delay"));
					summon.setTitle(player.getName());
					summon.setExpPenalty(rset.getFloat("exp_penalty"));
					summon.setExp(Experience.LEVEL[Math.min(summon.getLevel(), Experience.getMaxLevel() + 1)]);
					summon.setCurrentHp(summon.getMaxHp(), false);
					summon.setCurrentMp(summon.getMaxMp());
					summon.setHeading(player.getHeading());
					summon.setRunning();
					player.setPet(summon);
					summon.spawnMe(GeoEngine.findPointToStayPet(player, 100, 150, player.getReflection().getGeoIndex()));
					if(summon.getSkillLevel(4140) > 0)
						summon.altUseSkill(SkillTable.getInstance().getInfo(4140, summon.getSkillLevel(4140)), player);
					if(summon.getName().equalsIgnoreCase("Shadow"))
						summon.addStatFunc(new FuncAdd(l2open.gameserver.skills.Stats.ABSORB_DAMAGE_PERCENT, 0x40, player, 15));
				}
				else if(rset.getInt("class_id") == player.getClassId().getId())
				{
					L2PetInstance pet = restore_pet(player.getInventory().getItemByObjectId(controlItem), petTemplate, player);
					if(pet == null)
						return;

					player.setPet(pet);
					pet.setTitle(player.getName());
					if(!pet.isRespawned())
					{
						pet.setCurrentHp(pet.getMaxHp(), false);
						pet.setCurrentMp(pet.getMaxMp());
						pet.setCurrentFed(pet.getMaxFed());
						pet.updateControlItem();
						store_pet(pet);
					}
					pet.setNonAggroTime(System.currentTimeMillis() + 15000);
					pet.setReflection(player.getReflection());
					
					pet.spawnMe(Location.findPointToStay(player, 50, 70));
					pet.setRunning();
					pet.startFeed();
					pet.getInventory().validateItems();
					if(pet instanceof L2PetBabyInstance)
						((L2PetBabyInstance) pet).startBuffTask();
				}
				ThreadPoolManager.getInstance().schedule(new l2open.common.RunnableImpl()
				{
					public void runImpl()
					{
						if(player.getPet() != null)
							player.getPet().setFollowStatus(true, true);
					}
				}, 500);
			}
			statement1 = con.prepareStatement("DELETE FROM summon_save WHERE char_obj_id = " + player.getObjectId() + "");
			statement1.executeUpdate();
		}
		catch(Exception e)
		{
			_log.warning("restore summon error" + e);
			e.printStackTrace();
		}
		finally
		{
			DatabaseUtils.closeStatement(statement1);
			DatabaseUtils.closeDatabaseCSR(con, statement, rset);
		}
	}

	/**
	 * Сохраняем самонов\петов при релоге или выходе
	 * @param summon
	 */
	public void storeSummon(L2Character summon, L2Player player)
	{
		ThreadConnection con = null;
		FiltredPreparedStatement statement = null, statement1 = null;
		try
		{

			con = L2DatabaseFactory.getInstance().getConnection();
			statement1 = con.prepareStatement("DELETE FROM summon_save WHERE char_obj_id = " + player.getObjectId() + "");
			statement1.executeUpdate();
			statement = con.prepareStatement("INSERT INTO `summon_save` (char_obj_id, class_id, item_obj_id, npc_id, life_time, item_consume_idInTime, item_consume_countInTime, item_consume_delay, exp_penalty)  VALUES(?,?,?,?,?,?,?,?,?)");
			if(summon.isSummon())
			{
				statement.setInt(1, player.getObjectId());
				statement.setInt(2, player.getClassId().getId());
				statement.setInt(3, 0);
				statement.setInt(4, summon.getNpcId());
				statement.setInt(5, ((L2SummonInstance) summon).getCurrentFed());
				statement.setInt(6, ((L2SummonInstance) summon).getItemConsumeIdInTime());
				statement.setInt(7, ((L2SummonInstance) summon).getItemConsumeCountInTime());
				statement.setInt(8, ((L2SummonInstance) summon).getItemConsumeDelay());
				statement.setDouble(9, ((L2SummonInstance) summon).getExpPenalty());
				statement.executeUpdate();
				DatabaseUtils.closeStatement(statement);
			}
			else if(summon.isPet())
			{
				statement.setInt(1, player.getObjectId());
				statement.setInt(2, player.getClassId().getId());
				statement.setInt(3, ((L2PetInstance) summon).getControlItemObjId());
				statement.setInt(4, summon.getNpcId());
				statement.setInt(5, ((L2PetInstance) summon).getCurrentFed());
				statement.setInt(6, 0);
				statement.setInt(7, 0);
				statement.setInt(8, 0);
				statement.setDouble(9, ((L2PetInstance) summon).getExpPenalty());
				statement.executeUpdate();
				DatabaseUtils.closeStatement(statement);
			}
		}
		catch(Exception e)
		{
			_log.log(Level.WARNING, "store summon error: ", e);
		}
		finally
		{
			DatabaseUtils.closeStatement(statement1);
			DatabaseUtils.closeDatabaseCS(con, statement);
		}
	}

	public boolean canEnterMailOrQuestion(L2Player player, boolean question)
	{
		ThreadConnection con = null;
		FiltredPreparedStatement statement = null;
		ResultSet rset = null;
		try
		{
			con = L2DatabaseFactory.getInstanceLogin().getConnection();
			statement = con.prepareStatement("SELECT login, l2email, l2question, l2answer FROM accounts WHERE login='"+player.getAccountName()+(question ? "' AND l2question = ''" : "' AND l2email = 'null@null'"));
			rset = statement.executeQuery();
			if(rset.next())
				return true;
		}
		catch(SQLException e)
		{
			e.printStackTrace();
			return true;
		}
		finally
		{
			DatabaseUtils.closeDatabaseCSR(con, statement, rset);
		}
		return false;
	}

	public void select_answer(L2Player player)
	{
		ThreadConnection con = null;
		FiltredPreparedStatement statement = null;
		ResultSet rset = null;
		try
		{
			con = L2DatabaseFactory.getInstanceLogin().getConnection();
			statement = con.prepareStatement("SELECT l2question, l2answer FROM accounts WHERE login='"+player.getAccountName()+"'");
			rset = statement.executeQuery();
			if(rset.next())
			{
				player.l2question = rset.getString(1).trim();
				player.l2answer = rset.getString(2).trim();
			}
		}
		catch(SQLException e)
		{
			e.printStackTrace();
		}
		finally
		{
			DatabaseUtils.closeDatabaseCSR(con, statement, rset);
		}
	}

	public void select_2pass_and_answer(L2Player player)
	{
		ThreadConnection con = null;
		FiltredPreparedStatement statement = null;
		ResultSet rset = null;
		try
		{
			con = L2DatabaseFactory.getInstance().getConnection();
			statement = con.prepareStatement("SELECT question, answer, password FROM character_pass WHERE "+(ConfigValue.Enable2PassAcc ? "login='"+player.getAccountName()+"'" : "obj_id='"+player.getObjectId()+"'"));
			rset = statement.executeQuery();
			if(rset.next())
			{
				player.l2question = rset.getString(1).trim();
				player.l2answer = rset.getString(2).trim();
				player.password = rset.getString(3).trim();
			}
		}
		catch(SQLException e)
		{
			e.printStackTrace();
		}
		finally
		{
			DatabaseUtils.closeDatabaseCSR(con, statement, rset);
		}
	}

	public void replace_2pass_and_answer(final L2Player player, String password, String question, String answer)
	{
		ThreadConnection con = null;
		FiltredPreparedStatement statement = null;
		try
		{
			con = L2DatabaseFactory.getInstance().getConnection();

			statement = con.prepareStatement("REPLACE INTO character_pass values(?,?,?,?,?)");
			statement.setString(1, player.getAccountName());
			statement.setInt(2, ConfigValue.Enable2PassAcc ? -1 : player.getObjectId());
			statement.setString(3, question);
			statement.setString(4, answer);
			statement.setString(5, password);
			statement.execute();
		}
		catch(final Exception e)
		{
			_log.log(Level.WARNING, "Error could not store Skills:", e);
		}
		finally
		{
			DatabaseUtils.closeDatabaseCS(con, statement);
		}
	}

	/**
	Тз реферал системы на php уже написана

SET FOREIGN_KEY_CHECKS=0;
-- ----------------------------
-- Table structure for `stress_referal`
-- ----------------------------
ALTER TABLE `accounts` ADD `refCode` varchar(15) NOT NULL DEFAULT '';

CREATE TABLE `stress_referal` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `account` varchar(100) CHARACTER SET utf8 NOT NULL,
  `referal` varchar(100) CHARACTER SET utf8 NOT NULL,
  `success` enum('0','1') CHARACTER SET utf8 NOT NULL DEFAULT '0',
  `date` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `account_referer` (`account`) USING BTREE
) ENGINE=MyISAM DEFAULT CHARSET=utf8;


1. Сделать выдачу бонуса по достижению игроком ноблеса. Получают бонус обе стороны кто пригласил и приглашен.
2. Сделать счетчик сколько игроков было приглашено.
3. Сделать выдачу бонуса если игрок пригласил допустим 10 игроков и за каждого 10 будет выдаваться еще бонус.

   Как это работает.
         Что бы пригласить игрока достаточно иметь аккаунт, учет идет по аккаунту.
         На аккаунте можно получить бонус на любом чаре(т.е можно тупо приглашать людей, а потом зайти создать чара и получить бонус). 
         Заходим в алт+б жмем кнопку получить бонус. За каждого 10 бонус выдается автоматически. Бонус падает в инвентарь.
         
+если будет время сделать вывод сообщения, что если наш игрок реферал достиг ноблеса. Типа поздравляю ваш реферал #ИМЯ достиг ноблеса ваш, бонус зачислен. (как в твт по центру экрана)

Конфиги
Nagrada1=id;колличество (кто приглосил)
Nagrada2=id;колличество (кого приглосили)
Za10=id;колличество (за каждые 10 рыл которые получили ноблес)
[29.12.2014 12:46:43] ViDaK: во так
	**/
	@SuppressWarnings("unchecked")
	private boolean checkReferralBonus2(L2Player player, int id)
	{
		if(ConfigValue.EnableVidakReferal)
		{
			try
			{
				switch(id)
				{
					case 2: // Получил 3-ю профу
						mysql.setEx(L2DatabaseFactory.getInstanceLogin(), "UPDATE `stress_referal` SET `is_third_class`=1 WHERE `account`=? LIMIT 1", player.getAccountName());
						break;
					case 3: // Получил нублес
						mysql.setEx(L2DatabaseFactory.getInstanceLogin(), "UPDATE `stress_referal` SET `is_noobles`=1 WHERE `account`=? LIMIT 1", player.getAccountName());
						player.sendMessage("Вы получили награду, за участвие в реферальной системе.");
						for(int i=0; i < ConfigValue.VidakReferalRewardToPlayer.length; i+=2)
							player.getInventory().addItem((int)ConfigValue.VidakReferalRewardToPlayer[i], ConfigValue.VidakReferalRewardToPlayer[i+1]);
						break;
				}
			}
			catch(SQLException e)
			{
				_log.warning("Unable to process referrals for player " + player);
				e.printStackTrace();
			}
			return true;
		}
		return false;
	}

	@SuppressWarnings("unchecked")
	public void checkReferralBonus(L2Player player, int id)
	{
		if(id == 1) // 2 профа
		{
			if(ConfigValue.SecondClassReward.length > 0 && ConfigValue.SecondClassReward[0] > 0)
				for(int i=0;i<ConfigValue.SecondClassReward.length;i=i+2)
					Functions.addItem(player, (int)ConfigValue.SecondClassReward[i], ConfigValue.SecondClassReward[i+1]);
			if(ConfigValue.EnableHtmlReward52)
			{
				if(player.getLevel() >= 52 && !player.getVarB("reward_52", false))
				{
					String content = Files.read("data/html/html_reward_52.htm", player);
					content = content.replace("<?player_name?>", player.getName());
					player.sendPacket(new NpcHtmlMessage(player, null).setHtml(content));
					player.setVar("reward_52", String.valueOf(true));
					player.setVar("reward_40", String.valueOf(true));
				}
				else if(player.getLevel() < 52 && !player.getVarB("reward_40", false))
				{
					String content = Files.read("data/html/html_reward_40.htm", player);
					content = content.replace("<?player_name?>", player.getName());
					player.sendPacket(new NpcHtmlMessage(player, null).setHtml(content));
					player.setVar("reward_40", String.valueOf(true));
				}
			}
		}
		else if(id == 2 && ConfigValue.ThirdClassReward.length > 0 && ConfigValue.ThirdClassReward[0] > 0) // 3 профа
			for(int i=0;i<ConfigValue.ThirdClassReward.length;i=i+2)
				Functions.addItem(player, (int)ConfigValue.ThirdClassReward[i], ConfigValue.ThirdClassReward[i+1]);
		if(checkReferralBonus2(player, id) || !ConfigValue.AllowReferrals)
			return;

		try
		{
			GArray<Object> referral = mysql.get_array(L2DatabaseFactory.getInstanceLogin(), "SELECT * FROM `referrals` WHERE `login`='" + player.getAccountName() + "'");
			if(referral.isEmpty())
				return; // нету
			Map<String, Object> row = (Map<String, Object>) referral.get(0);

			if(!row.get("bonus" + id).toString().isEmpty())
				return; // бонус уже получен

			int server = Integer.parseInt(row.get("server").toString());
			if(server != ConfigValue.RequestServerID)
				return; // другой сервер

			int char_id = Integer.parseInt(row.get("char").toString());
			if(char_id == 0)
				return; // WTF?

			Letter letter = new Letter();
			letter.receiverId = player.getObjectId();
			letter.receiverName = "";
			letter.senderId = 1;
			letter.senderName = "";
			letter.topic = "Referral reward";
			letter.body = "Congratulations!";
			letter.price = 0;
			letter.unread = 1;
			letter.system = 1;
			letter.hideSender = 2;
			letter.validtime = 1296000 + (int) (System.currentTimeMillis() / 1000); // 14 days
			L2ItemInstance reward1 = ItemTemplates.getInstance().createItem(id == 1 ? ConfigValue.ReferralsBonusId1 : id == 2 ? ConfigValue.ReferralsBonusId2 : ConfigValue.ReferralsBonusId3);
			reward1.setCount(id == 1 ? ConfigValue.ReferralsBonusCount1 : id == 2 ? ConfigValue.ReferralsBonusCount2 : ConfigValue.ReferralsBonusCount3);
			GArray<L2ItemInstance> attachments = new GArray<L2ItemInstance>();
			attachments.add(reward1);
			MailParcelController.getInstance().sendLetter(letter, attachments);
			player.sendPacket(new ExNoticePostArrived(1));

			Letter letter2 = new Letter();
			letter2.receiverId = char_id;
			letter2.receiverName = "";
			letter2.senderId = 1;
			letter2.senderName = "";
			letter2.topic = "Referral reward";
			letter2.body = "Congratulations! Your friend " + player.getName() + " acquired profession " + player.getClassId().toString() + "!";
			letter2.price = 0;
			letter2.unread = 1;
			letter2.system = 1;
			letter2.hideSender = 2;
			letter2.validtime = 1296000 + (int) (System.currentTimeMillis() / 1000); // 14 days
			L2ItemInstance reward2 = ItemTemplates.getInstance().createItem(id == 1 ? ConfigValue.ReferralsBonusId1 : id == 2 ? ConfigValue.ReferralsBonusId2 : ConfigValue.ReferralsBonusId3);
			reward2.setCount(id == 1 ? ConfigValue.ReferralsBonusCount1 : id == 2 ? ConfigValue.ReferralsBonusCount2 : ConfigValue.ReferralsBonusCount3);
			attachments = new GArray<L2ItemInstance>();
			attachments.add(reward2);
			MailParcelController.getInstance().sendLetter(letter2, attachments);
			L2Player other = L2ObjectsStorage.getPlayer(char_id);
			if(other != null)
				other.sendPacket(new ExNoticePostArrived(1));

			Log.add("Bonus #" + id + " for char " + player.getObjectId() + " referred by " + char_id, "referral");
			mysql.setEx(L2DatabaseFactory.getInstanceLogin(), "UPDATE `referrals` SET `bonus" + id + "`=? WHERE `login`=? LIMIT 1", String.valueOf(player.getHWIDs()), player.getAccountName());
		}
		catch(SQLException e)
		{
			_log.warning("Unable to process referrals for player " + player);
			e.printStackTrace();
		}
	}

	public void saveNameToDB(L2Player player)
	{
		ThreadConnection con = null;
		FiltredPreparedStatement st = null;

		try
		{
			con = L2DatabaseFactory.getInstance().getConnection();
			st = con.prepareStatement("UPDATE characters SET char_name = ? WHERE obj_Id = ?");
			st.setString(1, player._name);
			st.setInt(2, player.getObjectId());
			st.executeUpdate();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			DatabaseUtils.closeDatabaseCS(con, st);
		}
	}

	public void restoreRecipeBook(L2Player player)
	{
		ThreadConnection con = null;
		FiltredPreparedStatement statement = null;
		ResultSet rset = null;
		try
		{
			con = L2DatabaseFactory.getInstance().getConnection();
			statement = con.prepareStatement("SELECT id FROM character_recipebook WHERE char_id=?");
			statement.setInt(1, player.getObjectId());
			rset = statement.executeQuery();

			while(rset.next())
			{
				int id = rset.getInt("id");
				L2Recipe recipe = RecipeController.getInstance().getRecipeByRecipeId(id);

				if(recipe == null)
				{
					_log.info("PlayerData: Load recipe err, not find: "+id);
					continue;
				}
				//_log.info("PlayerData: Load recipe: "+id);
				player.registerRecipe(recipe, false);
			}
		}
		catch(Exception e)
		{
			_log.warning("count not recipe skills:" + e);
			e.printStackTrace();
		}
		finally
		{
			DatabaseUtils.closeDatabaseCSR(con, statement, rset);
		}
	}

	/**
	 * Удаляет всю информацию о классе и добавляет новую, только для сабклассов
	 */
	public boolean modifySubClass(final L2Player player, final int oldClassId, final int newClassId)
	{
		final L2SubClass originalClass = player.getSubClasses().get(oldClassId);
		if(originalClass == null || originalClass.isBase())
			return false;
		final L2SubClass active_sub = player.getActiveClass();
		final int certification = originalClass.getCertification();

		if(ConfigValue.MultiProfa)
			try
			{
				for(L2SkillLearn temp : SkillTreeTable.getInstance().getSkillTrees().get(oldClassId))
					removeSkill(player, temp.getId(), true, true);
			}
			catch(Exception e)
			{}

		ThreadConnection con = null;
		FiltredPreparedStatement statement = null;
		try
		{
			con = L2DatabaseFactory.getInstance().getConnection();

			// Remove all basic info stored about this sub-class.
			statement = con.prepareStatement("DELETE FROM character_subclasses WHERE char_obj_id=? AND class_id=? AND (isBase = 0 OR isBase = 2)");
			statement.setInt(1, player.getObjectId());
			statement.setInt(2, oldClassId);
			statement.execute();
			DatabaseUtils.closeStatement(statement);

			// Remove all skill info stored for this sub-class.
			statement = con.prepareStatement("DELETE FROM character_skills WHERE char_obj_id=? AND class_index=? ");
			statement.setInt(1, player.getObjectId());
			statement.setInt(2, oldClassId);
			statement.execute();
			DatabaseUtils.closeStatement(statement);

			// Remove all saved skills info stored for this sub-class.
			statement = con.prepareStatement("DELETE FROM character_skills_save WHERE char_obj_id=? AND class_index=? ");
			statement.setInt(1, player.getObjectId());
			statement.setInt(2, oldClassId);
			statement.execute();
			DatabaseUtils.closeStatement(statement);

			// Remove all saved effects stored for this sub-class.
			statement = con.prepareStatement("DELETE FROM character_effects_save WHERE char_obj_id=? AND class_index=? ");
			statement.setInt(1, player.getObjectId());
			statement.setInt(2, oldClassId);
			statement.execute();
			DatabaseUtils.closeStatement(statement);

			// Remove all henna info stored for this sub-class.
			statement = con.prepareStatement("DELETE FROM character_hennas WHERE char_obj_id=? AND class_index=? ");
			statement.setInt(1, player.getObjectId());
			statement.setInt(2, oldClassId);
			statement.execute();
			DatabaseUtils.closeStatement(statement);

			// Remove all shortcuts info stored for this sub-class.
			statement = con.prepareStatement("DELETE FROM character_shortcuts WHERE char_obj_id=? AND class_index=? ");
			statement.setInt(1, player.getObjectId());
			statement.setInt(2, oldClassId);
			statement.execute();
		}
		catch(final Exception e)
		{
			_log.warning("Could not delete char sub-class: " + e);
			e.printStackTrace();
		}
		finally
		{
			DatabaseUtils.closeDatabaseCS(con, statement);
		}
		player.getSubClasses().remove(oldClassId);

		return newClassId <= 0 || addSubClass(player, newClassId, active_sub.getClassId() != oldClassId, certification, false);
	}

	/**
	 * Добавить класс, используется только для сабклассов
	 *
	 * @param storeOld
	 * @param certification
	 */
	public boolean addSubClass(final L2Player player, final int classId, boolean storeOld, int certification, boolean multa)
	{
		if(player.getSubClasses().size() >= (4 + ConfigValue.AltSubAdd + player.getVarInt("DoubleBaseClass", 0)) && !multa)
			return false;

		final ClassId newId = ClassId.values()[classId];

		final L2SubClass newClass = new L2SubClass(multa ? ConfigValue.Multi_StartSubLevel : ConfigValue.GetStartSubLevel, Experience.LEVEL[multa ? ConfigValue.Multi_StartSubLevel : ConfigValue.GetStartSubLevel]);
		if(newId.getRace() == null)
			return false;

		newClass.setClassId(classId);
		newClass.setPlayer(player);
		newClass.setBase(false);
		newClass.setCertification(certification);

		player.getSubClasses().put(classId, newClass);

		ThreadConnection con = null;
		FiltredPreparedStatement statement = null;
		try
		{
			// Store the basic info about this new sub-class.
			con = L2DatabaseFactory.getInstance().getConnection();
			statement = con.prepareStatement("INSERT INTO character_subclasses (char_obj_id, class_id, exp, sp, curHp, curMp, curCp, maxHp, maxMp, maxCp, level, active, isBase, death_penalty, certification) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
			statement.setInt(1, player.getObjectId());
			statement.setInt(2, newClass.getClassId());
			statement.setLong(3, Experience.LEVEL[multa ? ConfigValue.Multi_StartSubLevel : ConfigValue.GetStartSubLevel]);
			statement.setInt(4, 0);
			statement.setDouble(5, player.getCurrentHp());
			statement.setDouble(6, player.getCurrentMp());
			statement.setDouble(7, player.getCurrentCp());
			statement.setDouble(8, player.getCurrentHp());
			statement.setDouble(9, player.getCurrentMp());
			statement.setDouble(10, player.getCurrentCp());
			statement.setInt(11, multa ? ConfigValue.Multi_StartSubLevel : ConfigValue.GetStartSubLevel);
			statement.setInt(12, 0);
			statement.setInt(13, 0);
			statement.setInt(14, 0);
			statement.setInt(15, 0);
			statement.execute();
		}
		catch(final Exception e)
		{
			_log.warning("Could not add character sub-class: " + e);
			return false;
		}
		finally
		{
			DatabaseUtils.closeDatabaseCS(con, statement);
		}

		player.setActiveSubClass(classId, storeOld);

		// Add all the necessary skills up to level 40 for this new class.
		boolean countUnlearnable = true;
		int unLearnable = 0;
		int numSkillsAdded = 0;
		GArray<L2SkillLearn> skills = player.getAvailableSkills(newId);
		while(skills.size() > unLearnable)
		{
			for(final L2SkillLearn s : skills)
			{
				final L2Skill sk = SkillTable.getInstance().getInfo(s.getId(), s.getLevel());
				if(sk == null || !sk.getCanLearn(newId))
				{
					if(countUnlearnable)
						unLearnable++;
					continue;
				}
				player.addSkill(sk, true);
				s.deleteSkills(player);
				numSkillsAdded++;
			}
			countUnlearnable = false;
			skills = player.getAvailableSkills(newId);
		}

		restoreSkills(player);
		player.rewardSkills();
		player.sendPacket(new SkillList(player));
		player.setCurrentHpMp(player.getMaxHp(), player.getMaxMp(), true);
		player.setCurrentCp(player.getMaxCp());
		return true;
	}

	/**
	 * Restore list of character professions and set up active proof
	 * Used when character is loading
	 */
	public void restoreCharSubClasses(final L2Player player)
	{
		ThreadConnection con = null;
		FiltredPreparedStatement statement = null;
		ResultSet rset = null;
		try
		{
			con = L2DatabaseFactory.getInstance().getConnection();
			statement = con.prepareStatement("SELECT class_id,exp,sp,level,curHp,curCp,curMp,active,isBase,death_penalty,certification FROM character_subclasses WHERE char_obj_id=?");
			statement.setInt(1, player.getObjectId());
			rset = statement.executeQuery();
			while(rset.next())
			{
				final L2SubClass subClass = new L2SubClass(ConfigValue.GetStartSubLevel, Experience.LEVEL[ConfigValue.GetStartSubLevel]);
				subClass.setBase(rset.getInt("isBase") == 1);
				subClass.setBase2(rset.getInt("isBase") == 2);
				subClass.setClassId(rset.getShort("class_id"));
				subClass.setLevel(rset.getByte("level"));
				subClass.setExp(rset.getLong("exp"));
				subClass.setSp(rset.getInt("sp"));
				subClass.setHp(rset.getDouble("curHp"));
				subClass.setMp(rset.getDouble("curMp"));
				subClass.setCp(rset.getDouble("curCp"));
				subClass.setActive(rset.getInt("active") != 0);
				subClass.setDeathPenalty(new DeathPenalty(player, rset.getByte("death_penalty")));
				subClass.setCertification(rset.getInt("certification"));
				subClass.setPlayer(player);

				player.getSubClasses().put(subClass.getClassId(), subClass);
			}

			if(player.getSubClasses().size() == 0)
				throw new Exception("There are no one subclass for player: " + player);

			int BaseClassId = player.getBaseClassId();
			if(BaseClassId == -1)
				throw new Exception("There are no base subclass for player: " + player);

			for(L2SubClass subClass : player.getSubClasses().values())
				if(subClass.isActive())
				{
					player.setActiveSubClass(subClass.getClassId(), false);
					break;
				}

			if(player.getActiveClass() == null)
			{
				//если из-за какого-либо сбоя ни один из сабкласов не отмечен как активный помечаем базовый как активный
				final L2SubClass subClass = player.getSubClasses().get(BaseClassId);
				subClass.setActive(true);
				player.setActiveSubClass(subClass.getClassId(), false);
			}
		}
		catch(final Exception e)
		{
			_log.warning("Could not restore char sub-classes: " + e);
			e.printStackTrace();
		}
		finally
		{
			DatabaseUtils.closeDatabaseCSR(con, statement, rset);
		}
	}

	/**
	 * Сохраняет информацию о классах в БД
	 */
	public void storeCharSubClasses(final L2Player player)
	{
		L2SubClass main = player.getActiveClass();
		if(main != null)
		{
			main.setCp(player.getCurrentCp());
			//main.setExp(player.getExp());
			//main.setLevel(player.getLevel());
			//main.setSp(player.getSp());
			main.setHp(player.getCurrentHp());
			main.setMp(player.getCurrentMp());
			main.setActive(true);
			player.getSubClasses().put(player.getActiveClassId(), main);
		}
		else
			_log.warning("Could not store char sub data, main class " + player._activeClassId + " not found for " + player);

		ThreadConnection con = null;
		FiltredStatement statement = null;
		StringBuilder sb = null;
		try
		{
			con = L2DatabaseFactory.getInstance().getConnection();
			statement = con.createStatement();

			for(L2SubClass subClass : player.getSubClasses().values())
			{
				sb = new StringBuilder("UPDATE character_subclasses SET ");
				sb.append("exp=").append(subClass.getExp()).append(",");
				sb.append("sp=").append(subClass.getSp()).append(",");
				sb.append("curHp=").append(subClass.getHp()).append(",");
				sb.append("curMp=").append(subClass.getMp()).append(",");
				sb.append("curCp=").append(subClass.getCp()).append(",");
				sb.append("level=").append(subClass.getLevel()).append(",");
				sb.append("active=").append(subClass.isActive() ? 1 : 0).append(",");
				sb.append("isBase=").append(subClass.isBase2() ? 2 : subClass.isBase() ? 1 : 0).append(",");
				sb.append("death_penalty=").append(subClass.getDeathPenalty().getLevelOnSaveDB()).append(",");
				sb.append("certification='").append(subClass.getCertification()).append("'");
				sb.append(" WHERE char_obj_id=").append(player.getObjectId()).append(" AND class_id=").append(subClass.getClassId()).append(" LIMIT 1");
				statement.executeUpdate(sb.toString());
			}

			sb = new StringBuilder("UPDATE LOW_PRIORITY character_subclasses SET ");
			sb.append("maxHp=").append(player.getMaxHp()).append(",");
			sb.append("maxMp=").append(player.getMaxMp()).append(",");
			sb.append("maxCp=").append(player.getMaxCp());
			sb.append(" WHERE char_obj_id=").append(player.getObjectId()).append(" AND active=1 LIMIT 1");
			statement.executeUpdate(sb.toString());
		}
		catch(final Exception e)
		{
			_log.warning("Could not store char(" + player.getName() + ") sub data: " + sb.toString());
			e.printStackTrace();
		}
		finally
		{
			DatabaseUtils.closeDatabaseCS(con, statement);
		}
	}

	/**
	 * Changing index of class in DB, used for changing class when finished professional quests
	 * @param oldclass
	 * @param newclass
	 */
	public synchronized void changeClassInDb(final L2Player player, final int oldclass, final int newclass)
	{
		ThreadConnection con = null;
		FiltredPreparedStatement statement = null;
		try
		{
			con = L2DatabaseFactory.getInstance().getConnection();

			statement = con.prepareStatement("UPDATE character_subclasses SET class_id=? WHERE char_obj_id=? AND class_id=?");
			statement.setInt(1, newclass);
			statement.setInt(2, player.getObjectId());
			statement.setInt(3, oldclass);
			statement.executeUpdate();
			DatabaseUtils.closeStatement(statement);

			statement = con.prepareStatement("DELETE FROM character_hennas WHERE char_obj_id=? AND class_index=?");
			statement.setInt(1, player.getObjectId());
			statement.setInt(2, newclass);
			statement.executeUpdate();
			DatabaseUtils.closeStatement(statement);

			statement = con.prepareStatement("UPDATE character_hennas SET class_index=? WHERE char_obj_id=? AND class_index=?");
			statement.setInt(1, newclass);
			statement.setInt(2, player.getObjectId());
			statement.setInt(3, oldclass);
			statement.executeUpdate();
			DatabaseUtils.closeStatement(statement);

			statement = con.prepareStatement("DELETE FROM character_shortcuts WHERE char_obj_id=? AND class_index=?");
			statement.setInt(1, player.getObjectId());
			statement.setInt(2, newclass);
			statement.executeUpdate();
			DatabaseUtils.closeStatement(statement);

			statement = con.prepareStatement("UPDATE character_shortcuts SET class_index=? WHERE char_obj_id=? AND class_index=?");
			statement.setInt(1, ConfigValue.Multi_Enable ? 0 : newclass);
			statement.setInt(2, player.getObjectId());
			statement.setInt(3, ConfigValue.Multi_Enable ? 0 : oldclass);
			statement.executeUpdate();
			DatabaseUtils.closeStatement(statement);

			statement = con.prepareStatement("DELETE FROM character_skills WHERE char_obj_id=? AND class_index=?");
			statement.setInt(1, player.getObjectId());
			statement.setInt(2, newclass);
			statement.executeUpdate();
			DatabaseUtils.closeStatement(statement);

			statement = con.prepareStatement("UPDATE character_skills SET class_index=? WHERE char_obj_id=? AND class_index=?");
			statement.setInt(1, newclass);
			statement.setInt(2, player.getObjectId());
			statement.setInt(3, oldclass);
			statement.executeUpdate();
			DatabaseUtils.closeStatement(statement);

			statement = con.prepareStatement("DELETE FROM character_effects_save WHERE char_obj_id=? AND class_index=?");
			statement.setInt(1, player.getObjectId());
			statement.setInt(2, newclass);
			statement.executeUpdate();
			DatabaseUtils.closeStatement(statement);

			statement = con.prepareStatement("UPDATE character_effects_save SET class_index=? WHERE char_obj_id=? AND class_index=?");
			statement.setInt(1, newclass);
			statement.setInt(2, player.getObjectId());
			statement.setInt(3, oldclass);
			statement.executeUpdate();
			DatabaseUtils.closeStatement(statement);

			statement = con.prepareStatement("DELETE FROM character_skills_save WHERE char_obj_id=? AND class_index=?");
			statement.setInt(1, player.getObjectId());
			statement.setInt(2, newclass);
			statement.executeUpdate();
			DatabaseUtils.closeStatement(statement);

			statement = con.prepareStatement("UPDATE character_skills_save SET class_index=? WHERE char_obj_id=? AND class_index=?");
			statement.setInt(1, newclass);
			statement.setInt(2, player.getObjectId());
			statement.setInt(3, oldclass);
			statement.executeUpdate();
			DatabaseUtils.closeStatement(statement);

			if(ConfigValue.PLRM_Enable)
			{
				statement = con.prepareStatement("UPDATE level_rewards SET classId=? WHERE objectId=? AND classId=?");
				statement.setInt(1, newclass);
				statement.setInt(2, player.getObjectId());
				statement.setInt(3, oldclass);
				statement.executeUpdate();
				DatabaseUtils.closeStatement(statement);
			}

			for(int i=1;i<=6;i++)
			{
				String var1 = player.getVar("skill_count_"+oldclass+"_"+i);
				String var2 = player.getVar("skill_list_"+oldclass+"_"+i);
				if(var1 != null)
				{
					player.setVar("skill_count_"+newclass+"_"+i, var1);
					player.unsetVar("skill_count_"+oldclass+"_"+i);
				}
				if(var2 != null)
				{
					player.setVar("skill_list_"+newclass+"_"+i, var2);
					player.unsetVar("skill_list_"+oldclass+"_"+i);
				}
			}
		}
		catch(final SQLException e)
		{
			e.printStackTrace();
		}
		finally
		{
			DatabaseUtils.closeDatabaseCS(con, statement);
		}
	}

	public void updateNoChannel(final L2Player player, final long time)
	{
		ThreadConnection con = null;
		FiltredPreparedStatement statement = null;

		player.setNoChannel(time);

		try
		{
			con = L2DatabaseFactory.getInstance().getConnection();

			final String stmt = "UPDATE characters SET nochannel = ? WHERE obj_Id=?";
			statement = con.prepareStatement(stmt);
			statement.setLong(1, player.getNoChannel() > 0 ? player.getNoChannel() / 1000 : player.getNoChannel());
			statement.setInt(2, player.getObjectId());
			statement.executeUpdate();
		}
		catch(final Exception e)
		{
			_log.warning("Could not activate nochannel:" + e);
		}
		finally
		{
			DatabaseUtils.closeDatabaseCS(con, statement);
		}
	}

	public String getVarValue(final Integer player_id, final String name)
	{
		String value = "0";
		ThreadConnection con = null;
		FiltredPreparedStatement offline = null;
		ResultSet rs = null;
		try
		{
			con = L2DatabaseFactory.getInstance().getConnection();
			offline = con.prepareStatement("SELECT value FROM character_variables WHERE obj_id = ? AND name = ? AND (expire_time <= '0' OR expire_time > ?) LIMIT 1");
			offline.setInt(1, player_id);
			offline.setString(2, name);
			offline.setLong(3, System.currentTimeMillis());
			rs = offline.executeQuery();
			while(rs.next())
				value = rs.getString("value");
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			DatabaseUtils.closeDatabaseCSR(con, offline, rs);
		}
		return value;
	}

	public void loadVariables(final L2Player player)
	{
		ThreadConnection con = null;
		FiltredPreparedStatement offline = null;
		ResultSet rs = null;
		try
		{
			con = L2DatabaseFactory.getInstance().getConnection();
			offline = con.prepareStatement("SELECT * FROM character_variables WHERE obj_id = ? AND (expire_time <= '0' OR expire_time > ?)");
			offline.setInt(1, player.getObjectId());
			offline.setLong(2, System.currentTimeMillis());
			rs = offline.executeQuery();
			while(rs.next())
			{
				String name = rs.getString("name");
				String value = Strings.stripSlashes(rs.getString("value"));
				player.getVars().put(name, value);
			}

			// TODO Здесь обязятельно выставлять все стандартные параметры, иначе будут NPE
			if(player.getVar("lang@") == null)
				player.setVar("lang@", ConfigValue.DefaultLang);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			DatabaseUtils.closeDatabaseCSR(con, offline, rs);
		}
	}

	public void restoreBlockList(final L2Player player)
	{
		player._blockList.clear();

		ThreadConnection con = null;
		FiltredPreparedStatement statement = null;
		ResultSet rs = null;
		try
		{
			con = L2DatabaseFactory.getInstance().getConnection();
			statement = con.prepareStatement("SELECT target_Id, char_name FROM character_blocklist LEFT JOIN characters ON ( character_blocklist.target_Id = characters.obj_Id ) WHERE character_blocklist.obj_Id = ?");
			statement.setInt(1, player.getObjectId());
			rs = statement.executeQuery();
			while(rs.next())
				player._blockList.put(rs.getInt("target_Id"), rs.getString("char_name"));
		}
		catch(SQLException e)
		{
			_log.warning("Can't restore player blocklist " + e);
		}
		finally
		{
			DatabaseUtils.closeDatabaseCSR(con, statement, rs);
		}
	}

	public void storeBlockList(final L2Player player)
	{
		ThreadConnection con = null;
		FiltredStatement statement = null;
		try
		{
			con = L2DatabaseFactory.getInstance().getConnection();
			statement = con.createStatement();
			statement.executeUpdate("DELETE FROM character_blocklist WHERE obj_Id=" + player.getObjectId());

			if(player._blockList.isEmpty())
				return;

			SqlBatch b = new SqlBatch("INSERT IGNORE INTO `character_blocklist` (`obj_Id`,`target_Id`) VALUES");

			synchronized (player._blockList)
			{
				StringBuilder sb;
				for(Entry<Integer, String> e : player._blockList.entrySet())
				{
					sb = new StringBuilder("(");
					sb.append(player.getObjectId()).append(",");
					sb.append(e.getKey()).append(")");
					b.write(sb.toString());
				}
			}
			if(!b.isEmpty())
				statement.executeUpdate(b.close());
		}
		catch(Exception e)
		{
			_log.warning("Can't store player blocklist " + e);
		}
		finally
		{
			DatabaseUtils.closeDatabaseCS(con, statement);
		}
	}

	/**
	 * Remove a skill from the L2Character and its Func objects from calculator set of the L2Character and save update in the character_skills table of the database.
	 * @return The L2Skill removed
	 */
	public L2Skill removeSkill(final L2Player player, int id, boolean fromDB, boolean update_icon)
	{
		// Remove a skill from the L2Character and its Func objects from calculator set of the L2Character
		L2Skill oldSkill = player.removeSkillById(id, update_icon);

		if(!fromDB)
			return oldSkill;

		ThreadConnection con = null;
		FiltredPreparedStatement statement = null;
		try
		{
			// Remove or update a L2Player skill from the character_skills table of the database
			con = L2DatabaseFactory.getInstance().getConnection();
			if(oldSkill != null)
			{
				if(ConfigValue.MultiProfa)
				{
					statement = con.prepareStatement("DELETE FROM character_skills WHERE skill_id=? AND char_obj_id=?");
					statement.setInt(1, oldSkill.getId());
					statement.setInt(2, player.getObjectId());
				}
				else
				{
					statement = con.prepareStatement("DELETE FROM character_skills WHERE skill_id=? AND char_obj_id=? AND class_index=?");
					statement.setInt(1, oldSkill.getId());
					statement.setInt(2, player.getObjectId());
					statement.setInt(3, ConfigValue.Multi_Enable ? 0 : player.getActiveClassId());
				}
				statement.execute();
			}
		}
		catch(final Exception e)
		{
			_log.log(Level.WARNING, "Error could not delete Skill:", e);
		}
		finally
		{
			DatabaseUtils.closeDatabaseCS(con, statement);
		}

		return oldSkill;
	}

	/**
	 * Add or update a L2Player skill in the character_skills table of the database.
	 */
	public void storeSkill(final L2Player player, final L2Skill newSkill, final L2Skill oldSkill)
	{
		if(newSkill == null) // вообще-то невозможно
		{
			_log.warning("could not store new skill. its NULL");
			return;
		}

		if(ConfigValue.EnableSkillLog)
			Log.logTrace(player.getName() + "|ADD_STORE|" + newSkill + "|" + oldSkill, "skills/store", player.getName());

		ThreadConnection con = null;
		FiltredPreparedStatement statement = null;
		try
		{
			con = L2DatabaseFactory.getInstance().getConnection();

			statement = con.prepareStatement("REPLACE INTO character_skills (char_obj_id,skill_id,skill_level,skill_name,class_index) values(?,?,?,?,?)");
			statement.setInt(1, player.getObjectId());
			statement.setInt(2, newSkill.getId());
			statement.setInt(3, newSkill.getLevel());
			statement.setString(4, newSkill.getName());
			statement.setInt(5, ConfigValue.Multi_Enable ? 0 : player.getActiveClassId());
			statement.execute();
		}
		catch(final Exception e)
		{
			_log.log(Level.WARNING, "Error could not store Skills:", e);
		}
		finally
		{
			DatabaseUtils.closeDatabaseCS(con, statement);
		}
	}

	/**
	 * Retrieve from the database all skills of this L2Player and add them to _skills.
	 */
	public void restoreSkills(final L2Player player)
	{
		ThreadConnection con = null;
		FiltredPreparedStatement statement = null;
		ResultSet rset = null;
		try
		{
			// Retrieve all skills of this L2Player from the database
			// Send the SQL query : SELECT skill_id,skill_level FROM character_skills WHERE char_obj_id=? to the database
			con = L2DatabaseFactory.getInstance().getConnection();
			if(ConfigValue.MultiProfa/* && player.getActiveClass().isBase()*/)
			{
				statement = con.prepareStatement("SELECT skill_id,skill_level FROM character_skills WHERE char_obj_id=? ");
				statement.setInt(1, player.getObjectId());
				rset = statement.executeQuery();
			}
			else
			{
				statement = con.prepareStatement("SELECT skill_id,skill_level FROM character_skills WHERE char_obj_id=? AND class_index=?");
				statement.setInt(1, player.getObjectId());
				statement.setInt(2, ConfigValue.Multi_Enable ? 0 : player.getActiveClassId());
				rset = statement.executeQuery();
			}

			// Go though the recordset of this SQL query
			while(rset.next())
			{
				final int id = rset.getInt("skill_id");
				final int level = rset.getInt("skill_level");

				//if(id > 9000 && id != 20006 && id < 26078)
				if(id > 9000 && id < 9180)
					continue; // fake skills for base stats

				// Create a L2Skill object for each record
				final L2Skill skill = SkillTable.getInstance().getInfo(id, level);

				if(skill == null)
					continue;

				// Remove skill if not possible
				if(!ConfigValue.OldSkillDelete && !ConfigValue.Multi_Enable3)
				{
					if(!player.getPlayerAccess().IsGM && !skill.isCommon() && !SkillTreeTable.getInstance().isSkillPossible(player, skill.getId(), skill.getLevel()))
					{
						int ReturnSP = SkillTreeTable.getInstance().getSkillCost(player, skill);
						if(ReturnSP == Integer.MAX_VALUE || ReturnSP < 0)
							ReturnSP = 0;
						player.removeSkill(skill, true, true);
						player.removeSkillFromShortCut(skill.getId());
						if(ReturnSP > 0)
							player.setSp(player.getSp() + ReturnSP);
						Log.IllegalPlayerAction(player, "has skill " + skill.getName() + "(" + skill.getId() + ") / ReturnSP: " + ReturnSP, 0);
						if(ConfigValue.EnableSkillLog)
							Log.addMy("RESTORE|"+skill, "skill_restore_FDD", player.getName());
						continue;
					}
				}

				// Add the L2Skill object to the L2Character _skills and its Func objects to the calculator set of the L2Character
				player.addSkill(skill);
			}

			// Restore noble skills
			if(player.isNoble())
				player.updateNobleSkills();
			if(ConfigValue.CharacterSetSkillNoble)
				player.addSkill(SkillTable.getInstance().getInfo(L2Skill.SKILL_NOBLESSE_BLESSING, 1));

			// Restore Hero skills at main class only
			if(player.isHero() && (!player.isSubClassActive() || ConfigValue.Multi_Enable) && (ConfigValue.PremiumHeroSetSkill || player.isHeroType() != 2))
				Hero.addSkills(player);

			if(player.getClan() != null)
			{
				// Restore clan leader siege skills
				if(player.getClan().getLeaderId() == player.getObjectId() && player.getClan().getLevel() >= CastleSiegeManager.getSiegeClanMinLevel())
					SiegeManager.addSiegeSkills(player);

				// Restore clan skills
				player.getClan().addAndShowSkillsToPlayer(player);
			}

			// Give dwarven craft skill
			if(player.getActiveClassId() >= 53 && player.getActiveClassId() <= 57 || player.getActiveClassId() == 117 || player.getActiveClassId() == 118)
				player.addSkill(SkillTable.getInstance().getInfo(1321, 1));
			player.addSkill(SkillTable.getInstance().getInfo(1322, 1));

			if(ConfigValue.UnstuckSkill && player.getSkillLevel(1050) < 0)
				player.addSkill(SkillTable.getInstance().getInfo(2099, 1));
		}
		catch(final Exception e)
		{
			_log.warning("Could not restore skills for player objId: " + player.getObjectId());
			e.printStackTrace();
		}
		finally
		{
			DatabaseUtils.closeDatabaseCSR(con, statement, rset);
		}
	}

	public void storeDisableSkills(final L2Player player)
	{
		ThreadConnection con = null;
		FiltredStatement statement = null;
		try
		{
			con = L2DatabaseFactory.getInstance().getConnection();
			statement = con.createStatement();
			statement.executeUpdate("DELETE FROM character_skills_save WHERE char_obj_id = " + player.getObjectId() + " AND class_index=" + (ConfigValue.Multi_Enable ? 0 : player.getActiveClassId()) + " AND `end_time` < " + System.currentTimeMillis());

			if(player.skillReuseTimeStamps.isEmpty())
				return;

			SqlBatch b = new SqlBatch("REPLACE INTO `character_skills_save` (`char_obj_id`,`skill_id`,`class_index`,`end_time`,`reuse_delay_org`) VALUES");
			synchronized (player.skillReuseTimeStamps)
			{
				StringBuilder sb;
				for(Entry<Long, SkillTimeStamp> tmp : player.getSkillReuseTimeStamps().entrySet())
					if(tmp.getValue().hasNotPassed())
					{
						sb = new StringBuilder("(");
						sb.append(player.getObjectId()).append(",");
						sb.append(tmp.getKey()).append(",");
						sb.append(ConfigValue.Multi_Enable ? 0 : player.getActiveClassId()).append(",");
						sb.append(tmp.getValue().getEndTime()).append(",");
						sb.append(tmp.getValue().getReuseBasic()).append(")");
						b.write(sb.toString());
					}
			}
			if(!b.isEmpty())
				statement.executeUpdate(b.close());
		}
		catch(final Exception e)
		{
			_log.warning("Could not store disable skills data: " + e);
		}
		finally
		{
			DatabaseUtils.closeDatabaseCS(con, statement);
		}
	}

	public void storeEffects(final L2Player player)
	{
		ThreadConnection con = null;
		FiltredStatement statement = null;
		try
		{
			con = L2DatabaseFactory.getInstance().getConnection();

			statement = con.createStatement();
			statement.executeUpdate("DELETE FROM character_effects_save WHERE char_obj_id = " + player.getObjectId() + " AND class_index=" + player.getActiveClassId());

			if(player.getEffectList() == null || player.getEffectList().isEmpty())
				return;

			long curTime = System.currentTimeMillis();

			int order = 0;
			SqlBatch b = new SqlBatch("INSERT IGNORE INTO `character_effects_save` (`char_obj_id`,`skill_id`,`skill_level`,`effect_count`,`effect_cur_time`,`duration`,`order`,`class_index`) VALUES");

			synchronized (player.getEffectList())
			{
				StringBuilder sb;
				for(L2Effect effect : player.getEffectList().getAllEffects())
					if(effect != null && effect.isInUse() && !effect.getSkill().isToggle() && effect.getAbnormalType() != SkillAbnormalType.hp_recover)
					{
						if(effect.isSaveable())
						{
							sb = new StringBuilder("(");
							sb.append(player.getObjectId()).append(",");
							sb.append(effect.getSkill().getId()).append(",");
							sb.append(effect.getSkill().getLevel()).append(",");
							sb.append(effect.getCount()).append(",");
							if(effect.getSkill().isOfflineTime())
								sb.append(curTime+effect.getPeriod()-effect.getTime()).append(",");
							else
								sb.append(effect.getTime()).append(",");
							sb.append(effect.getPeriod()).append(",");
							sb.append(order).append(",");
							sb.append(player.getActiveClassId()).append(")");
							b.write(sb.toString());
						}
						order++;
					}
			}
			if(!b.isEmpty())
				statement.executeUpdate(b.close());
		}
		catch(final Exception e)
		{
			_log.warning("Could not store active effects data: " + e);
			e.printStackTrace();
		}
		finally
		{
			DatabaseUtils.closeDatabaseCS(con, statement);
		}
	}

	public void restoreEffects(final L2Player player)
	{
		ThreadConnection con = null;
		FiltredPreparedStatement statement = null;
		ResultSet rset = null;
		try
		{
			con = L2DatabaseFactory.getInstance().getConnection();

			statement = con.prepareStatement("SELECT `skill_id`,`skill_level`,`effect_count`,`effect_cur_time`,`duration` FROM `character_effects_save` WHERE `char_obj_id`=? AND `class_index`=? ORDER BY `order` ASC");
			statement.setInt(1, player.getObjectId());
			statement.setInt(2, player.getActiveClassId());

			long curTime = System.currentTimeMillis();

			rset = statement.executeQuery();
			while(rset.next())
			{
				int skillId = rset.getInt("skill_id");
				int skillLvl = rset.getInt("skill_level");
				int effectCount = rset.getInt("effect_count");
				long effectCurTime = rset.getLong("effect_cur_time");
				long duration = rset.getLong("duration");

				L2Skill skill = SkillTable.getInstance().getInfo(skillId, skillLvl);

				if(skill == null)
				{
					_log.warning("Can't restore Effect\tskill: " + skillId + ":" + skillLvl + " " + player.toFullString());
					//Thread.dumpStack();
					continue;
				}

				if(skill.getEffectTemplates() == null)
				{
					_log.warning("Can't restore Effect, EffectTemplates is NULL\tskill: " + skillId + ":" + skillLvl + " " + player.toFullString());
					//Thread.dumpStack();
					continue;
				}

				long set_time = skill.isOfflineTime() ? effectCurTime-curTime : duration - effectCurTime;

				if(set_time > 3000)
				{
					if(!skill.checkSkillAbnormal(player) && !skill.isBlockedByChar(player, skill))
					{
						for(EffectTemplate et : skill.getEffectTemplates())
						{
							if(et == null)
								continue;
							Env env = new Env(player, player, skill);
							L2Effect effect = et.getEffect(env);
							if(effect == null || !effect.isSaveable() || effect._instantly)
								continue;
							else if(effectCount == 1)
							{
								effect.setCount(effectCount);
								effect.setPeriod(set_time);
							}
							else
							{
								effect.setPeriod(duration);
								effect.setCount(effectCount);
							}
							player.getEffectList().addEffect(effect);
						}
					}
				}
			}
		}
		catch(final Exception e)
		{
			_log.warning("Could not restore active effects data [charId: " + player.getObjectId() + "; ActiveClassId: " + player._activeClassId + "]: " + e);
			e.printStackTrace();
		}
		finally
		{
			DatabaseUtils.closeDatabaseCSR(con, statement, rset);
		}
		player.broadcastUserInfo(true);
	}

	public void restoreDisableSkills(final L2Player player)
	{
		ThreadConnection con = null;
		FiltredStatement statement = null;
		ResultSet rset = null;
		try
		{
			con = L2DatabaseFactory.getInstance().getConnection();

			statement = con.createStatement();
			rset = statement.executeQuery("SELECT skill_id,end_time,reuse_delay_org FROM character_skills_save WHERE char_obj_id=" + player.getObjectId() + " AND class_index=" + (ConfigValue.Multi_Enable ? 0 : player.getActiveClassId()));

			while(rset.next())
			{
				long sk_id = rset.getLong("skill_id");
				int skillId = (int)(ConfigValue.SkillReuseType == 0 ? sk_id/65536 : sk_id);
				int skillLevel = ConfigValue.SkillReuseType == 0 ? (int)sk_id%65536 : Math.max(player.getSkillLevel(skillId), 1);
				long endTime = rset.getLong("end_time");
				long rDelayOrg = rset.getLong("reuse_delay_org");
				long curTime = System.currentTimeMillis();

				if(skillId < 0 && endTime - curTime > 500)
				{
					int item_id = skillId;
					int grp_id = skillId;
					if(item_id < -65536)
					{
						item_id = (item_id % 65536) * -1;
						grp_id /= -65536;
					}
					else
					{
						item_id *= -1;
						grp_id *= -1;
					}
					player.disableItem(skillId, skillLevel, item_id, grp_id, rDelayOrg, endTime - curTime);
				}

				L2Skill skill = SkillTable.getInstance().getInfo(skillId, skillLevel);

				if(skill != null && endTime - curTime > 500)
					player.getSkillReuseTimeStamps().put(ConfigValue.SkillReuseType == 0 ? skillId*65536L+skillLevel : skillId, new SkillTimeStamp(skillId, skillLevel, endTime, rDelayOrg, 0));
			}
			DatabaseUtils.closeStatement(statement);

			statement = con.createStatement();
			statement.executeUpdate("DELETE FROM character_skills_save WHERE char_obj_id = " + player.getObjectId() + " AND class_index=" + (ConfigValue.Multi_Enable ? 0 : player.getActiveClassId()) + " AND `end_time` < " + System.currentTimeMillis());
		}
		catch(Exception e)
		{
			_log.warning("Could not restore active skills data for " + player.getObjectId() + "/" + player._activeClassId);
			e.printStackTrace();
		}
		finally
		{
			DatabaseUtils.closeDatabaseCSR(con, statement, rset);
		}
	}

	/**
	 * Retrieve from the database all Henna of this L2Player, add them to _henna and calculate stats of the L2Player.<BR><BR>
	 */
	public void restoreHenna(final L2Player player)
	{
		ThreadConnection con = null;
		FiltredPreparedStatement statement = null;
		ResultSet rset = null;
		try
		{
			con = L2DatabaseFactory.getInstance().getConnection();
			statement = con.prepareStatement("select slot, symbol_id from character_hennas where char_obj_id=? AND class_index=?");
			statement.setInt(1, player.getObjectId());
			statement.setInt(2, ConfigValue.Multi_Enable ? 0 : player.getActiveClassId());
			rset = statement.executeQuery();

			for(int i = 0; i < 3; i++)
				player._henna[i] = null;

			while(rset.next())
			{
				final int slot = rset.getInt("slot");
				if(slot < 1 || slot > 3)
					continue;

				final int symbol_id = rset.getInt("symbol_id");

				L2HennaInstance sym;

				if(symbol_id != 0)
				{
					final L2Henna tpl = HennaTable.getInstance().getTemplate(symbol_id);
					if(tpl != null)
					{
						sym = new L2HennaInstance(tpl);
						player._henna[slot - 1] = sym;
					}
				}
			}
		}
		catch(final Exception e)
		{
			_log.warning("could not restore henna: " + e);
		}
		finally
		{
			DatabaseUtils.closeDatabaseCSR(con, statement, rset);
		}

		// Calculate Henna modifiers of this L2Player
		player.recalcHennaStats();
	}

	/**
	 * Remove a Henna of the L2Player, save update in the character_hennas table of the database and send Server->Client HennaInfo/UserInfo packet to this L2Player.<BR><BR>
	 */
	public boolean removeHenna(final L2Player player, int slot)
	{
		if(slot < 1 || slot > 3)
			return false;

		slot--;

		if(player._henna[slot] == null)
			return false;

		final L2HennaInstance henna = player._henna[slot];
		final short dyeID = henna.getItemIdDye();

		// Added by Tempy - 10 Aug 05
		// Gives amount equal to half of the dyes needed for the henna back.
		final L2ItemInstance hennaDyes = ItemTemplates.getInstance().createItem(dyeID);
		hennaDyes.setCount(henna.getAmountDyeRequire() / 2);

		player._henna[slot] = null;

		ThreadConnection con = null;
		FiltredPreparedStatement statement = null;
		try
		{
			con = L2DatabaseFactory.getInstance().getConnection();
			statement = con.prepareStatement("DELETE FROM character_hennas where char_obj_id=? and slot=? and class_index=?");
			statement.setInt(1, player.getObjectId());
			statement.setInt(2, slot + 1);
			statement.setInt(3, ConfigValue.Multi_Enable ? 0 : player.getActiveClassId());
			statement.execute();
		}
		catch(final Exception e)
		{
			_log.warning("could not remove char henna: " + e);
		}
		finally
		{
			DatabaseUtils.closeDatabaseCS(con, statement);
		}

		// Calculate Henna modifiers of this L2Player
		player.recalcHennaStats();

		// Send Server->Client HennaInfo packet to this L2Player
		player.sendPacket(new HennaInfo(player));

		// Send Server->Client UserInfo packet to this L2Player
		player.sendUserInfo(false);

		// Add the recovered dyes to the player's inventory and notify them.
		player.getInventory().addItem(hennaDyes);
		player.sendPacket(SystemMessage.obtainItems(henna.getItemIdDye(), henna.getAmountDyeRequire() / 2, 0));

		return true;
	}

	/**
	 * Add a Henna to the L2Player, save update in the character_hennas table of the database and send Server->Client HennaInfo/UserInfo packet to this L2Player.<BR><BR>
	 * @param henna L2HennaInstance для добавления
	 */
	public boolean addHenna(final L2Player player, L2HennaInstance henna)
	{
		if(player.getHennaEmptySlots() == 0)
		{
			player.sendPacket(Msg.NO_SLOT_EXISTS_TO_DRAW_THE_SYMBOL);
			return false;
		}

		// int slot = 0;
		for(int i = 0; i < 3; i++)
			if(player._henna[i] == null)
			{
				player._henna[i] = henna;

				// Calculate Henna modifiers of this L2Player
				player.recalcHennaStats();

				ThreadConnection con = null;
				FiltredPreparedStatement statement = null;
				try
				{
					con = L2DatabaseFactory.getInstance().getConnection();
					statement = con.prepareStatement("INSERT INTO `character_hennas` (char_obj_id, symbol_id, slot, class_index) VALUES (?,?,?,?)");
					statement.setInt(1, player.getObjectId());
					statement.setInt(2, henna.getSymbolId());
					statement.setInt(3, i + 1);
					statement.setInt(4, ConfigValue.Multi_Enable ? 0 : player.getActiveClassId());
					statement.execute();
				}
				catch(Exception e)
				{
					_log.warning("could not save char henna: " + e);
				}
				finally
				{
					DatabaseUtils.closeDatabaseCS(con, statement);
				}

				player.sendPacket(new HennaInfo(player));
				player.sendUserInfo(true);

				return true;
			}

		return false;
	}

	private final Object _storeLock = new Object();

	/**
	 * Update L2Player stats in the characters table of the database.
	 */
	public void store(final L2Player player, boolean fast)
	{
		if(player.isFantome())
			return;
		synchronized (_storeLock)
		{
			long check = player.getVarLong("startBotCheck", -1);
			if(check != -1)
			{
				int time = player.getVarInt("startBotCheckTime", -1);
				player.unsetVar("startBotCheck");
				player.setVar("startBotCheckTime", String.valueOf(time-(System.currentTimeMillis()-check)/1000+Rnd.get(30, 180)));
			}
			ThreadConnection con = null;
			FiltredPreparedStatement statement = null;
			FiltredStatement fs = null;
			try
			{
				con = L2DatabaseFactory.getInstance().getConnection();
				statement = con.prepareStatement(//
				"UPDATE characters SET face=?,hairStyle=?,hairColor=?,heading=?,x=?,y=?,z=?" + //
				",karma=?,pvpkills=?,pkkills=?,rec_have=?,rec_left=?,rec_timeleft=?,clanid=?," + //
				"title=?,accesslevel=?,online=?,leaveclan=?,deleteclan=?,nochannel=?," + //
				"onlinetime=?,pledge_type=?,pledge_rank=?,lvl_joined_academy=?,apprentice=?,key_bindings=?,pcBangPoints=?,char_name=?,vitality=?,fame=?,bookmarks=?,hunt_bonus=?,hunt_timeleft=? WHERE obj_Id=? LIMIT 1");
				statement.setInt(1, player.getFace());
				statement.setInt(2, player.getHairStyle());
				statement.setInt(3, player.getHairColor());
				statement.setInt(4, player.getHeading() & 0xFFFF);
				if(player._stablePoint == null) // если игрок находится в точке в которой его сохранять не стоит (например на виверне) то сохраняются последние координаты
				{
					statement.setInt(5, player.getX());
					statement.setInt(6, player.getY());
					statement.setInt(7, player.getZ());
				}
				else
				{
					statement.setInt(5, player._stablePoint.x);
					statement.setInt(6, player._stablePoint.y);
					statement.setInt(7, player._stablePoint.z);
				}
				statement.setInt(8, player.getKarma());
				statement.setInt(9, player.getPvpKills());
				statement.setInt(10, player.getPkKills());
				statement.setInt(11, player.getRecommendation().getRecomHave());
				statement.setInt(12, player.getRecommendation().getRecomLeft());
				statement.setInt(13, player.getRecommendation().getRecomTimeLeft());
				statement.setInt(14, player.getClanId());
				//statement.setInt(15, player.getDeleteTimer());
				statement.setString(15, player._title);
				statement.setInt(16, player.getAccessLevel());
				statement.setInt(17,  player.isOnline() ? 1 : 0);
				statement.setLong(18,  player.getLeaveClanTime() / 1000);
				statement.setLong(19,  player.getDeleteClanTime() / 1000);
				statement.setLong(20,  player.getNoChannel() > 0 ?  player.getNoChannelRemained() / 1000 :  player.getNoChannel());
				statement.setLong(21,  player._onlineBeginTime > 0 ? ( player._onlineTime + System.currentTimeMillis() -  player._onlineBeginTime) / 1000 :  player._onlineTime / 1000);
				statement.setInt(22,  player.getPledgeType());
				statement.setInt(23,  player.getPowerGrade());
				statement.setInt(24,  player.getLvlJoinedAcademy());
				statement.setInt(25,  player.getApprentice());
				statement.setBytes(26,  player.getKeyBindings());
				statement.setInt(27,  player.getPcBangPoints());
				statement.setString(28,  player._name);
				statement.setInt(29, (int)  player.getVitality());
				statement.setInt(30,  player.getFame());
				statement.setInt(31,  player.bookmarks.getCapacity());
				statement.setInt(32,  player.getNevitBlessing().getPoints());
				statement.setInt(33,  player.getNevitBlessing().getBonusTime());
				statement.setInt(34,  player.getObjectId());

				statement.executeUpdate();
				Stat.increaseUpdatePlayerBase();

				try
				{
					if(!fast && ConfigValue.KillCounter &&  player._StatKills != null)
					{
						fs = con.createStatement();
						for(Entry<Integer, Long> tmp : player._StatKills.entrySet())
						{
							StringBuilder sb = new StringBuilder();
							fs.addBatch(sb.append("REPLACE DELAYED INTO `killcount` SET `npc_id`=").append(tmp.getKey()).append(", `count`=").append(tmp.getValue()).append(", `char_id`=").append(player.getObjectId()).toString());
						}
						fs.executeBatch();
						DatabaseUtils.closeStatement(fs);
					}

					if(!fast && ConfigValue.CraftCounter && player._StatCraft != null)
					{
						fs = con.createStatement();
						for(Entry<Integer, Long> tmp : player._StatCraft.entrySet())
						{
							StringBuilder sb = new StringBuilder();
							fs.addBatch(sb.append("REPLACE DELAYED INTO `craftcount` SET `item_id`=").append(tmp.getKey()).append(", `count`=").append(tmp.getValue()).append(", `char_id`=").append( player.getObjectId()).toString());
						}
						fs.executeBatch();
						DatabaseUtils.closeStatement(fs);
					}

					if(!fast && ConfigValue.DropCounter && player._StatDrop != null)
					{
						fs = con.createStatement();
						for(Entry<Integer, Long> tmp : player._StatDrop.entrySet())
						{
							StringBuilder sb = new StringBuilder();
							fs.addBatch(sb.append("REPLACE DELAYED INTO `craftcount` SET `item_id`=").append(tmp.getKey()).append(", `count`=").append(tmp.getValue()).append(", `char_id`=").append( player.getObjectId()).toString());
						}
						fs.executeBatch();
						DatabaseUtils.closeStatement(fs);
					}
				}
				catch(ConcurrentModificationException e)
				{}

				if(!fast)
				{
					storeEffects(player);
					storeDisableSkills(player);
					storeBlockList(player);
				}

				storeCharSubClasses(player);
				player.bookmarks.store();
			}
			catch(Exception e)
			{
				_log.warning("store: could not store char data: " + e);
				e.printStackTrace();
			}
			finally
			{
				DatabaseUtils.closeDatabaseCS(con, statement);
			}
		}
	}

	/**
	 * Retrieve a L2Player from the characters table of the database and add it in _allObjects of the L2World
	 * @return The L2Player loaded from the database
	 */
	public L2Player restore(final int objectId, int bot)
	{
		L2Player player = null;
		ThreadConnection con = null;
		FiltredStatement statement = null;
		FiltredStatement statement2 = null;
		ResultSet pl_rset = null;
		ResultSet ps_rset = null;
		try
		{
			// Retrieve the L2Player from the characters table of the database
			con = L2DatabaseFactory.getInstance().getConnection();

			statement = con.createStatement();
			statement2 = con.createStatement();
			pl_rset = statement.executeQuery("SELECT * FROM `characters` WHERE `obj_Id`=" + objectId + " LIMIT 1");
			ps_rset = statement2.executeQuery("SELECT `class_id` FROM `character_subclasses` WHERE `char_obj_id`=" + objectId + " AND `isBase`=1 LIMIT 1"); // TODO: Если нету бейса, назначить любой класс...

			if(pl_rset.next() && ps_rset.next())
			{
				final int classId = ps_rset.getInt("class_id");
				int class_race = 0;
				try
				{
					class_race = pl_rset.getInt("class_race");
				}
				catch(SQLException e1e)
				{}
				//_log.info("class_race: "+class_race);
				final boolean female = pl_rset.getInt("sex") == 1;
				final L2PlayerTemplate template = CharTemplateTable.getInstance().getTemplate(class_race == 0 ? classId : class_race, female);

				player = bot > 0 ? new L2BotPlayer(objectId, template, bot) : new L2Player(objectId, template, bot);
				player.class_race=class_race;
				loadVariables(player);
				loadPremiumItemList(player);
				player.bookmarks.setCapacity(pl_rset.getInt("bookmarks"));
				player._postFriends = CharacterPostFriend.getInstance().select(objectId);
				//CharacterGroupReuseDAO.getInstance().select(player);

				player.setBaseClass(classId);
				player._accountName = pl_rset.getString("account_name");
				player.setName(pl_rset.getString("char_name"));

				player.setFace(pl_rset.getByte("face"));
				player.setHairStyle(pl_rset.getByte("hairStyle"));
				player.setHairColor(pl_rset.getByte("hairColor"));
				player.setHeading(pl_rset.getInt("heading"));

				player.setKarma(pl_rset.getInt("karma"));
				player.setPvpKills(pl_rset.getInt("pvpkills"));
				player.setPkKills(pl_rset.getInt("pkkills"));
				player.setLeaveClanTime(pl_rset.getLong("leaveclan") * 1000L);
				if(player.getLeaveClanTime() > 0 && player.canJoinClan())
					player.setLeaveClanTime(0);
				player.setDeleteClanTime(pl_rset.getLong("deleteclan") * 1000L);
				if(player.getDeleteClanTime() > 0 && player.canCreateClan())
					player.setDeleteClanTime(0);

				player.setNoChannel(pl_rset.getLong("nochannel") * 1000L);
				if(player.getNoChannel() > 0 && player.getNoChannelRemained() < 0)
					updateNoChannel(player, 0);

				player.setOnlineTime(pl_rset.getLong("onlinetime") * 1000L);

				final int clanId = pl_rset.getInt("clanid");
				if(clanId > 0)
				{
					player.setClan(ClanTable.getInstance().getClan(clanId), true);
					player.setPledgeType(pl_rset.getInt("pledge_type"));
					player.setPowerGrade(pl_rset.getInt("pledge_rank"));
					player.setLvlJoinedAcademy(pl_rset.getInt("lvl_joined_academy"));
					player.setApprentice(pl_rset.getInt("apprentice"));
				}

				player.setCreateTime(pl_rset.getLong("createtime") * 1000L);
				//player.setDeleteTimer(pl_rset.getInt("deletetime"));
				player.setTitle(pl_rset.getString("title"));
				player.setLastAccess(pl_rset.getLong("lastAccess"));

				player.getRecommendation().setRecomHave(pl_rset.getInt("rec_have"));
				player.getRecommendation().setRecomLeft(pl_rset.getInt("rec_left"));
				player.getRecommendation().setRecomTimeLeft(pl_rset.getInt("rec_timeleft"));
				player.getRecommendation().checkRecom();

				if(player.getVar("TitleColor") != null)
					player.setTitleColor(Integer.decode(player.getVar("TitleColor")).intValue());

				if(player.getVar("buf_title") != null)
					player._buf_title = player.getVar("buf_title");
				player.can_private_log = player.getVarB("can_private_log", false);
				player.send_visual_id = player.getVarB("send_visual_id", true);
				player.send_visual_enchant = player.getVarB("send_visual_enchant", true);
				player.disable_cloak = player.getVarB("disable_cloak", false);

				if(player.getVarLong("no_kill_time", -1) != -1)
					player.no_kill_time = player.getVarLong("no_kill_time");
				if(player.getVarLong("set_fame", -1) != -1)
				{
					player._set_fame = player.getVarLong("set_fame");
					/*long update_time = player._set_fame-System.currentTimeMillis();
					if(update_time > 1000)
						ThreadPoolManager.getInstance().schedule(new player.SetMyFame(), update_time);*/
				}

				if(player.getVar("namecolor") == null)
					if(player.isGM())
						player.setNameColor(Integer.decode("0x" + ConfigValue.GMNameColour));
					else if(player.getClan() != null && player.getClan().getLeaderId() == player.getObjectId())
						player.setNameColor(Integer.decode("0x" + ConfigValue.ClanleaderNameColour));
					else
						player.setNameColor(Integer.decode("0x" + ConfigValue.NormalNameColour));
				else
					player.setNameColor(Integer.decode("0x" + player.getVar("namecolor")));

				if(ConfigValue.AutoLootIndividual)
				{
					player.AutoLoot = player.getVarB("AutoLoot", ConfigValue.AutoLoot);
					player.AutoLootHerbs = player.getVarB("AutoLootHerbs", ConfigValue.AutoLootHerbs);
					player.AutoLootSpecial = player.getVarB("AutoLootSpecial", false);
				}
				player._active_item_protect = player.getVarB("ActiveItemProtect", false);

				player.setFistsWeaponItem(player.findFistsWeaponItem(classId));
				player.setUptime(System.currentTimeMillis());

				player.setKeyBindings(pl_rset.getBytes("key_bindings"));
				player.setPcBangPoints(pl_rset.getInt("pcBangPoints"));

				player.setFame(pl_rset.getInt("fame"), null);
				DelayedItemsManager.getInstance().loadDelayed(player, false);

				if(ConfigValue.EnableAttainment)
				{
					switch(ConfigValue.AttainmentType)
					{
						case 0x100:
							player._attainment = new AttainmentVidak(player);
							break;
						case 0x200:
							player._attainment = new AttainmentFraction(player);
							break;
						case 0x300: // 768
							player._attainment = new AttainmentVladimir(player);
							break;
						case 0x400:
							player._attainment = new AttainmentHunter(player);
							break;
						case 0x600:
							player._attainment = new AttainmentNoname(player);
							break;
						case 0x550: // 1360
							player._attainment = new Attainment1(player);
							break;
						case 0x560: // 1376
							player._attainment = new AttainmentIncubus(player);
							break;
					}
				}

				restoreRecipeBook(player);

				if(ConfigValue.EnableOlympiad)
					player.setHero(Hero.getInstance().isHero(player.getObjectId()), Hero.getInstance().isHero(player.getObjectId()) ? 0 : -1);
				if(player.getVar("HeroPremium") != null && System.currentTimeMillis() < Long.parseLong(player.getVar("HeroPremium")))
				{
					player.setHero(true, 2);
					player._heroTask = ThreadPoolManager.getInstance().schedule(new UnsetHero(player, 2), Long.parseLong(player.getVar("HeroPremium"))-System.currentTimeMillis());
				}
				else if(player.getVar("HeroEvent") != null && System.currentTimeMillis() < Long.parseLong(player.getVar("HeroEvent")))
				{
					player.setHero(true, 1);
					player._heroTask = ThreadPoolManager.getInstance().schedule(new UnsetHero(player, 1), Long.parseLong(player.getVar("HeroEvent"))-System.currentTimeMillis());
				}
				player.setNoble(Olympiad.isNoble(player.getObjectId()));

				player.updatePledgeClass();

				player.updateKetraVarka();
				player.updateRam();

				player._set_sub = player.getVarLong("set_sub_time", 0);
				// для сервиса виверн - возврат денег если сервер упал во время полета
				String wm = player.getVar("wyvern_moneyback");
				if(wm != null && Integer.parseInt(wm) > 0)
					player.addAdena(Integer.parseInt(wm));
				player.unsetVar("wyvern_moneyback");

				int reflection = 0;

				// Set the x,y,z position of the L2Player and make it invisible
				long curTime = System.currentTimeMillis() / 1000;

				if(player.getVar("jailed") != null && curTime + 5 < curTime + Integer.parseInt(player.getVar("jailed").split(";")[0]))
				{
					player.setXYZInvisible(-114648, -249384, -2984);
					String[] re = player.getVar("jailedFrom", "146984;25752;-2016").split(";");
					Location loc = new Location(Integer.parseInt(re[0]), Integer.parseInt(re[1]), Integer.parseInt(re[2]));
					reflection = -3;

					long jailTimes = Long.parseLong(player.getVar("jailed").split(";")[0]);
					player.setVar("jailed", jailTimes + ";" + curTime);

					player._unjailTask = ThreadPoolManager.getInstance().schedule(new TeleportTask(player, loc, 0), jailTimes * 1000L);
				}
				else
				{
					if(player.getVar("jailed") != null)
					{
						player.unsetVar("jailed");
						player.unsetVar("jailedFrom");
						player.unsetVar("reflection");
					}

					player.setXYZInvisible(pl_rset.getInt("x"), pl_rset.getInt("y"), pl_rset.getInt("z"));
					wm = player.getVar("reflection");
					if(wm != null)
					{
						reflection = Integer.parseInt(wm);
						if(reflection > 0)
						{
							String back = player.getVar("backCoords");
							if(back != null)
							{
								player.setLoc(new Location(back));
								player.unsetVar("backCoords");
								player.unsetVar("reflection");
							}
							else
							{
								String backOly = player.getVar("backCoordsOly");
								if(backOly != null)
								{
									player.setLoc(new Location(backOly));
									player.unsetVar("reflection");
									player.unsetVar("backCoordsOly");
								}
							}
							reflection = 0;
						}
					}
					else
					{
						String backOly = player.getVar("backCoordsOly");
						if(backOly != null)
						{
							player.setLoc(new Location(backOly));
							player.unsetVar("backCoordsOly");
							reflection = 0;
						}
					}
				}

				//player.unsetVar("backCoords");

				player.setReflection(reflection);

				player.restoreTradeList();
				if(player.getVar("storemode") != null)
					if(player.getVar("offline") != null) // оффтрейдеры выбивают других, онтрейдеры нет
					{
						if(ConfigValue.TradeOnlyFar)
						{
							L2WorldRegion currentRegion = L2World.getRegion(player.getLoc());
							if(currentRegion != null)
							{
								GArray<L2WorldRegion> neighbors = currentRegion.getNeighbors();
								int size = 0;
								for(L2WorldRegion region : neighbors)
									size += region.getPlayersSize();
								GArray<L2Player> result = new GArray<L2Player>(size);
								for(L2WorldRegion region : neighbors)
									region.getPlayersList(result, 0, player.getReflection(), player.getX(), player.getY(), player.getZ(), ConfigValue.TradeRadius * ConfigValue.TradeRadius, 200, false);

								for(L2Player p : result)
									if(p.isInStoreMode())
										if(p.isInOfflineMode())
											L2TradeList.cancelStore(p);
										else
										{
											p.setPrivateStoreType(L2Player.STORE_PRIVATE_NONE);
											p.broadcastUserInfo(true);
										}
							}
						}

						player.setPrivateStoreType(Short.parseShort(player.getVar("storemode")));
						player.setSitting(true);
					}
					else
					{
						short type = Short.parseShort(player.getVar("storemode"));
						if(player.checksForShop(type == L2Player.STORE_PRIVATE_MANUFACTURE))
						{
							player.setPrivateStoreType(type);
							player.setSitting(true);
						}
						else
							player.unsetVar("storemode");
					}

				if(TerritorySiege.isInProgress())
				{
					player.setTerritorySiege(TerritorySiege.getTerritoryForPlayer(objectId));
					//TerritorySiege.clearReward(player.getObjectId());
				}

				Quest.playerEnter(player);

				player._hidden = true;
				restoreCharSubClasses(player);
				player._hidden = false;

				// 2 очка в минуту оффлайна, на оффе 4, но там очки вдвое легче
				player.setVitality(pl_rset.getInt("vitality") + (int) ((System.currentTimeMillis() / 1000 - pl_rset.getLong("lastAccess")) / 30.));

				try
				{
					String var = player.getVar("ExpandInventory");
					if(var != null)
						player.setExpandInventory(Integer.parseInt(var));
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}

				try
				{
					String var = player.getVar("ExpandWarehouse");
					if(var != null)
						player.setExpandWarehouse(Integer.parseInt(var));
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}

				try
				{
					String var = player.getVar("show_old_dam_message");
					if(var != null)
						player.old_dam_message = var.equals("1") ? true : false;
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}
				try
				{
					String var = player.getVar("show_damage");
					if(var != null)
						player.show_damage = Boolean.parseBoolean(var);
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}
				try
				{
					String var = player.getVar("notShowBuffAnim");
					if(var != null)
						player.setNotShowBuffAnim(var.equals("1") ? true : false);
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}

				try
				{
					String var = player.getVar("show_buff_anim_dist");
					if(var != null)
						player.set_show_buff_anim_dist(Integer.parseInt(var));
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}

				try
				{
					String var = player.getVar("show_attack_dist");
					if(var != null)
						player.set_show_attack_dist(Integer.parseInt(var));
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}

				try
				{
					String var = player.getVar("show_attack_flag_dist");
					if(var != null)
						player.set_show_attack_flag_dist(Integer.parseInt(var));
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}

				FiltredPreparedStatement stmt = null;
				ResultSet chars = null;
				try
				{
					stmt = con.prepareStatement("SELECT obj_Id, char_name FROM characters WHERE account_name=? AND obj_Id!=?");
					stmt.setString(1, player._accountName);
					stmt.setInt(2, objectId);
					chars = stmt.executeQuery();
					while(chars.next())
					{
						final Integer charId = chars.getInt("obj_Id");
						final String charName = chars.getString("char_name");
						player.getAccountChars().put(charId, charName);
					}
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}
				finally
				{
					DatabaseUtils.closeDatabaseSR(stmt, chars);
				}

				if(ConfigValue.KillCounter)
				{
					// Restore kills stat
					FiltredStatement stt = null;
					ResultSet rstkills = null;
					try
					{
						stt = con.createStatement();
						rstkills = stt.executeQuery("SELECT `npc_id`, `count` FROM `killcount` WHERE `char_id`=" + objectId);
						player._StatKills = new HashMap<Integer, Long>(128);
						while(rstkills.next())
							player._StatKills.put(rstkills.getInt("npc_id"), rstkills.getLong("count"));
					}
					catch(Exception e)
					{
						e.printStackTrace();
					}
					finally
					{
						DatabaseUtils.closeDatabaseSR(stt, rstkills);
					}
				}

				//Restore craft stat
				if(ConfigValue.CraftCounter)
				{
					FiltredStatement stcraft = null;
					ResultSet rstcraft = null;
					try
					{
						stcraft = con.createStatement();
						rstcraft = stcraft.executeQuery("SELECT `item_id`, `count` FROM `craftcount` WHERE `char_id`=" + objectId);
						player._StatCraft = new HashMap<Integer, Long>(32);
						while(rstcraft.next())
							player._StatCraft.put(rstcraft.getInt("item_id"), rstcraft.getLong("count"));
					}
					catch(Exception e)
					{
						e.printStackTrace();
					}
					finally
					{
						DatabaseUtils.closeDatabaseSR(stcraft, rstcraft);
					}
				}

				try
				{
					String var = player.getVar("canEnterBeleth");
					if(var != null && Long.parseLong(var) > System.currentTimeMillis())
						player._roomDone = true;
					else if(var != null)
						player.unsetVar("canEnterBeleth");
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}

				if(ConfigValue.DropCounter)
				{
					//Restore drop stat
					FiltredStatement stdrop = null;
					ResultSet rstdrop = null;
					try
					{
						stdrop = con.createStatement();
						rstdrop = stdrop.executeQuery("SELECT `item_id`, `count` FROM `dropcount` WHERE `char_id`=" + objectId);
						player._StatDrop = new HashMap<Integer, Long>(128);
						while(rstdrop.next())
							player._StatDrop.put(rstdrop.getInt("item_id"), rstdrop.getLong("count"));
					}
					catch(Exception e)
					{
						e.printStackTrace();
					}
					finally
					{
						DatabaseUtils.closeDatabaseSR(stdrop, rstdrop);
					}
				}

				if(!L2World.validCoords(player.getX(), player.getY()) || player.getX() == 0 && player.getY() == 0)
					player.setXYZInvisible(MapRegion.getTeleToClosestTown(player));

				// Перед началом работы с территориями, выполним их обновление
				player.updateTerritories();

				player.getNevitBlessing().addPoints(pl_rset.getInt("hunt_bonus"));
				player.getNevitBlessing().setBonusTime(pl_rset.getInt("hunt_timeleft"));

				if(!player.isGM())
				{
					if(ConfigValue.EnableOlympiad && player.isInZone(ZoneType.OlympiadStadia))
					{
						player.sendMessage(new CustomMessage("l2open.gameserver.clientpackets.EnterWorld.TeleportedReasonOlympiad", player));
						player.setXYZInvisible(MapRegion.getTeleToClosestTown(player));
					}

					L2Zone noRestartZone = ZoneManager.getInstance().getZoneByTypeAndObject(ZoneType.no_restart, player);
					if(noRestartZone != null && System.currentTimeMillis() / 1000 - player.getLastAccess() > noRestartZone.getRestartTime())
					{
						player.sendMessage(new CustomMessage("l2open.gameserver.clientpackets.EnterWorld.TeleportedReasonNoRestart", player));

						if(noRestartZone.getRestartPoints() != null)
							player.setXYZInvisible(noRestartZone.getSpawn());
						else
							player.setXYZInvisible(MapRegion.getTeleToClosestTown(player));
					}

					if(player.isInZone(ZoneType.Siege))
					{
						Siege siege = SiegeManager.getSiege(player, true);
						if(siege != null && !siege.checkIsDefender(player.getClan()))
							if(siege.getHeadquarter(player.getClan()) == null)
								player.setXYZInvisible(MapRegion.getTeleToClosestTown(player));
							else
								player.setXYZInvisible(MapRegion.getTeleToHeadquarter(player));
						if(TerritorySiege.checkIfInZone(player))
							if(TerritorySiege.getHeadquarter(player.getClan()) == null)
								player.setXYZInvisible(MapRegion.getTeleToClosestTown(player));
							else
								player.setXYZInvisible(MapRegion.getTeleToHeadquarter(player));
					}

					if(DimensionalRiftManager.getInstance().checkIfInRiftZone(player.getLoc(), false))
						player.setXYZInvisible(DimensionalRiftManager.getInstance().getRoom(0, 0).getTeleportCoords());
				}
				//player.getInventory().restoreCursedWeapon();
				player.getInventory().validateItems();
				player.revalidatePenalties();
				restoreBlockList(player);
				BreakWarnManager.getInstance().addWarnTask(player);
				AutoSaveManager.getInstance().addPlayerTask(player);

				player._buffSchem = new HashMap<Integer, CBBuffSch>(ConfigValue.maxBuffSchem);
				player._buffSchemePerform = new HashMap<Integer, CBBuffSchemePerform>(ConfigValue.maxBuffSchem);
				player._tpSchem = new HashMap<Integer, CBTpSch>(ConfigValue.TeleportMaxPoint);
				loadBuffSch(player);
				loadTpSch(player);
				player._enchantCount = player.getVarInt("EnchantCount", 0);

				int time = player.getVarInt("startBotCheckTime", -1);
				if(time == -1)
					time = Rnd.get(ConfigValue.BotProtectTimeMin, ConfigValue.BotProtectTimeMax);
				player.startBotCheck(time);
				if(ConfigValue.RecruitmentAllow)
					LoadClanDescription(player);

				player._enable_auto_cp_cp = player.getVarInt("cp_percent", 0);
				player._enable_auto_cp_hp = player.getVarInt("hp_percent", 0);
				player._enable_auto_cp_mp = player.getVarInt("mp_percent", 0);

				player._time_auto_cp_cp = player.getVarInt("cp_time", 333);
				player._time_auto_cp_hp = player.getVarInt("hp_time", 333);
				player._time_auto_cp_mp = player.getVarInt("mp_time", 333);

				player._item_id_auto_cp_hp = player.getVarInt("hp_item", ConfigValue.AutoCpPointsHp[0]);
				player._item_id_auto_cp_mp = player.getVarInt("mp_item", ConfigValue.AutoCpPointsMp[0]);
				player._item_id_auto_cp_cp = player.getVarInt("cp_item", ConfigValue.AutoCpPointsCp[0]);
				if(player.getVarB("TheHungerGamesTWinner", false))
					player.startAbnormalEffect(AbnormalVisualEffect.ave_vp_up);

				player._enable_auto = player.getVarB("enable_auto", true);

				player.raid_points = player.getVarInt("raid_points", 0);

				if(ConfigValue.Odyssey_zone_sp_enable)
				{
					long time_exit = player.getVarLong("SP_ZONE", -1);
					if(time_exit > -1)
					{
						int count = (int)((System.currentTimeMillis()-time_exit)/ConfigValue.Odyssey_zone_sp_delay);
						long sp=0;
						long exp=0;
						for(int[] datas : ConfigValue.Odyssey_zone_sp_reward)
							if(player.getLevel() >= datas[0] && player.getLevel() <= datas[1])
								for(int i=0;i<count;i++)
								{
									try
									{
										sp+=datas[2];
										exp+=datas[3];
									}
									catch(Exception e)
									{
										e.printStackTrace();
										break;
									}
								}
						if(sp > 0 || exp > 0)
							player.addExpAndSp(sp, exp, false, false);
						player.unsetVar("SP_ZONE");
					}
				}

				if(ConfigValue.Odyssey_zone_exp_enable)
				{
					long time_exit = player.getVarLong("EXP_ZONE", -1);
					if(time_exit > -1)
					{
						int count = (int)((System.currentTimeMillis()-time_exit)/ConfigValue.Odyssey_zone_exp_delay);
						long sp=0;
						long exp=0;
						for(int[] datas : ConfigValue.Odyssey_zone_exp_reward)
							if(player.getLevel() >= datas[0] && player.getLevel() <= datas[1])
								for(int i=0;i<count;i++)
								{
									try
									{
										sp+=datas[2];
										exp+=datas[3];
									}
									catch(Exception e)
									{
										break;
									}
								}
						if(sp > 0 || exp > 0)
							player.addExpAndSp(sp, exp, false, false);
						player.unsetVar("EXP_ZONE");
					}
				}
			}
		}
		catch(final Exception e)
		{
			_log.info("restore: could not restore char data:");
			e.printStackTrace();
		}
		finally
		{
			DatabaseUtils.closeDatabaseSR(statement2, ps_rset);
			DatabaseUtils.closeDatabaseCSR(con, statement, pl_rset);
		}
		return player;
	}

	public void loadBuffSch(final L2Player player)
	{
		ThreadConnection con = null;
		FiltredStatement statement = null;
		ResultSet rs = null;
		try
		{
			con = L2DatabaseFactory.getInstance().getConnection();
			statement = con.createStatement();
			rs = statement.executeQuery("SELECT `charId`, `schameid`, `name`, `skills` FROM `community_skillsave` WHERE `charId`='" + player.getObjectId() + "'");
			while(rs.next())
			{
				int id = rs.getInt(2);
				String name = rs.getString(3);
				String allskills = rs.getString(4);
				long[] sch = new long[0];
				String[] scheme = new String[0];
				StringTokenizer stBuff = new StringTokenizer(allskills, ";");
				while(stBuff.hasMoreTokens()){
					//sch = ArrayUtils.add(sch, Long.parseLong(stBuff.nextToken()));
					ArrayUtils.add(scheme, stBuff.nextToken());
				}
				//player._buffSchem.put(id, new CBBuffSch(id, name, sch));
				player._buffSchemePerform.put(id, new CBBuffSchemePerform(id, name, scheme));
			}
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

	public void loadTpSch(final L2Player player)
	{
		ThreadConnection con = null;
		FiltredStatement statement = null;
		ResultSet rs = null;
		try
		{
			con = L2DatabaseFactory.getInstance().getConnection();
			statement = con.createStatement();
			rs = statement.executeQuery("SELECT TpId, name, xPos, yPos, zPos FROM comteleport WHERE charId='" + player.getObjectId() + "'");
			while(rs.next())
			{
				int id = rs.getInt("TpId");
				player._tpSchem.put(id, new CBTpSch(id, rs.getString("name"), rs.getInt("xPos"), rs.getInt("yPos"), rs.getInt("zPos")));
			}
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

	public void loadPremiumItemList(final L2Player player)
	{
		ThreadConnection con = null;
		FiltredPreparedStatement statement = null;
		ResultSet rs = null;
		try
		{
			con = L2DatabaseFactory.getInstance().getConnection();
			statement = con.prepareStatement("SELECT itemNum, itemId, itemCount, itemSender FROM character_premium_items WHERE charId=?");
			statement.setInt(1, player.getObjectId());
			rs = statement.executeQuery();
			while(rs.next())
			{
				int itemNum = rs.getInt("itemNum");
				int itemId = rs.getInt("itemId");
				long itemCount = rs.getLong("itemCount");
				String itemSender = rs.getString("itemSender");
				PremiumItem item = new PremiumItem(itemId, itemCount, itemSender);
				player.getPremiumItemList().put(itemNum, item);
			}
		}
		catch(final Exception e)
		{
			_log.log(Level.WARNING, "loadPremiumItemList:", e);
		}
		finally
		{
			DatabaseUtils.closeDatabaseCSR(con, statement, rs);
		}
	}

	public void updatePremiumItem(final L2Player player, int itemNum, long newcount)
	{
		ThreadConnection con = null;
		FiltredPreparedStatement statement = null;
		try
		{
			con = L2DatabaseFactory.getInstance().getConnection();
			statement = con.prepareStatement("UPDATE character_premium_items SET itemCount=? WHERE charId=? AND itemNum=?");
			statement.setLong(1, newcount);
			statement.setInt(2, player.getObjectId());
			statement.setInt(3, itemNum);
			statement.executeUpdate();
		}
		catch(final Exception e)
		{
			_log.log(Level.WARNING, "updatePremiumItem:", e);
		}
		finally
		{
			DatabaseUtils.closeDatabaseCS(con, statement);
		}
	}

	public void deletePremiumItem(final L2Player player, int itemNum)
	{
		ThreadConnection con = null;
		FiltredPreparedStatement statement = null;
		try
		{
			con = L2DatabaseFactory.getInstance().getConnection();
			statement = con.prepareStatement("DELETE FROM character_premium_items WHERE charId=? AND itemNum=?");
			statement.setInt(1, player.getObjectId());
			statement.setInt(2, itemNum);
			statement.executeUpdate();
		}
		catch(final Exception e)
		{
			_log.log(Level.WARNING, "deletePremiumItem:", e);
		}
		finally
		{
			DatabaseUtils.closeDatabaseCS(con, statement);
		}
	}

	public void storeHWID(final L2Player player, String HWID)
	{
		ThreadConnection con = null;
		FiltredPreparedStatement statement = null;
		try
		{
			con = L2DatabaseFactory.getInstance().getConnection();
			statement = con.prepareStatement("UPDATE characters SET last_hwid=? WHERE obj_id=?");
			statement.setString(1, HWID);
			statement.setInt(2, player.getObjectId());
			statement.execute();
		}
		catch(final Exception e)
		{
			_log.warning("could not store characters HWID:" + e);
		}
		finally
		{
			DatabaseUtils.closeDatabaseCS(con, statement);
		}

		try
		{
			con = L2DatabaseFactory.getInstanceLogin().getConnection();
			statement = con.prepareStatement("UPDATE accounts SET last_hwid=? WHERE login=?");
			statement.setString(1, HWID);
			statement.setString(2, player.getAccountName());
			statement.execute();
		}
		catch(final Exception e)
		{
			_log.warning("could not store accounts HWID:" + e);
		}
		finally
		{
			DatabaseUtils.closeDatabaseCS(con, statement);
		}
	}

	public void updateOnlineStatus(final L2Player player)
	{
		boolean online = player.isOnline();
		if(player.isInOfflineMode())
			online = false;

		ThreadConnection con = null;
		FiltredPreparedStatement statement = null;
		try
		{
			con = L2DatabaseFactory.getInstance().getConnection();
			statement = con.prepareStatement("UPDATE characters SET online=?, lastAccess=? WHERE obj_id=?");
			statement.setInt(1, online ? 1 : 0);
			statement.setLong(2, System.currentTimeMillis() / 1000);
			statement.setInt(3, player.getObjectId());
			statement.execute();
		}
		catch(final Exception e)
		{
			_log.warning("could not set char online status:" + e);
		}
		finally
		{
			DatabaseUtils.closeDatabaseCS(con, statement);
		}
	}

	public void setCharacterAccessLevel(String user, int banLevel)
	{
		ThreadConnection con = null;
		FiltredPreparedStatement statement = null;
		try
		{
			con = L2DatabaseFactory.getInstance().getConnection();

			String stmt = "UPDATE characters SET characters.accesslevel = ? WHERE characters.char_name=?";
			statement = con.prepareStatement(stmt);
			statement.setInt(1, banLevel);
			statement.setString(2, user);
			statement.executeUpdate();
		}
		catch(Exception e)
		{
			_log.warning("Could not set accessLevl:" + e);
		}
		finally
		{
			DatabaseUtils.closeDatabaseCS(con, statement);
		}
	}

	public void setTitle(final L2Character cha, String title)
	{
		ThreadConnection con = null;
		FiltredPreparedStatement statement = null;
		try
		{
			con = L2DatabaseFactory.getInstance().getConnection();
			statement = con.prepareStatement("UPDATE characters SET title=? WHERE obj_Id=?");
			statement.setString(1, title);
			statement.setInt(2, cha.getObjectId());
			statement.execute();
		}
		catch(Exception e)
		{}
		finally
		{
			DatabaseUtils.closeDatabaseCS(con, statement);
		}
	}

	public void restoreInventory(Inventory inv, L2Character owner)
	{
		final int OWNER = owner.getObjectId();

		ThreadConnection con = null;
		FiltredPreparedStatement statement = null;
		ResultSet rset = null;

		try
		{
			con = L2DatabaseFactory.getInstance().getConnection();
			statement = con.prepareStatement("SELECT * FROM items WHERE owner_id=? AND (loc=? OR loc=?) ORDER BY object_id DESC");
			statement.setInt(1, OWNER);
			statement.setString(2, inv.getBaseLocation().name());
			statement.setString(3, inv.getEquipLocation().name());
			rset = statement.executeQuery();

			L2ItemInstance item, newItem;
			while(rset.next())
			{
				if((item = restoreFromDb(rset, con)) == null)
					continue;
				newItem = inv.addItem(item, false, false);
				if(newItem == null)
					continue;
				if(item.isEquipped())
					inv.equipItem(item, false);
			}
		}
		catch(Exception e)
		{
			_log.log(Level.WARNING, "could not restore inventory for player " + owner.getName() + ":", e);
		}
		finally
		{
			DatabaseUtils.closeDatabaseCSR(con, statement, rset);
		}
	}

	public void SetOneTimeQuestFlag(L2Player player, int id, int value)
	{
		ThreadConnection con = null;
		FiltredPreparedStatement statement = null;
		try
		{
			con = L2DatabaseFactory.getInstance().getConnection();
			statement = con.prepareStatement("REPLACE INTO character_quests (char_id,name,var,value) VALUES (?,?,?,?)");
			statement.setInt(1, player.getObjectId());
			statement.setString(2, QuestManager.getQuest(id).getName());
			statement.setString(3, "<state>");
			statement.setString(4, "Completed");
			statement.executeUpdate();
		}
		catch(Exception e)
		{
			_log.warning("could not insert char quest:"+e);
		}
		finally
		{
			DatabaseUtils.closeDatabaseCS(con, statement);
		}
	}

	public L2ItemInstance[] listItems(Warehouse w, ItemClass clss)
	{
		final GArray<L2ItemInstance> items = new GArray<L2ItemInstance>();
		ThreadConnection con = null;
		FiltredPreparedStatement statement = null;
		ResultSet rset = null;

		try
		{
			con = L2DatabaseFactory.getInstance().getConnection();

			statement = con.prepareStatement(clss == ItemClass.ALL ? "SELECT * FROM items WHERE owner_id=? AND loc=? ORDER BY name ASC LIMIT 200" : "SELECT * FROM items WHERE owner_id=? AND loc=? AND class=? ORDER BY name ASC LIMIT 200");
			statement.setInt(1, w.getOwnerId());
			statement.setString(2, w.getLocationType().name());
			if(clss != ItemClass.ALL)
				statement.setString(3, clss.name());
			rset = statement.executeQuery();

			L2ItemInstance item;
			while(rset.next())
				if((item = restoreFromDb(rset, con)) != null)
					items.add(item);
		}
		catch(final Exception e)
		{
			_log.log(Level.SEVERE, "could not restore warehouse:", e);
		}
		finally
		{
			DatabaseUtils.closeDatabaseCSR(con, statement, rset);
		}
		return items.toArray(new L2ItemInstance[items.size()]);
	}

	public L2ItemInstance findItemId(final Warehouse w, final int itemId)
	{
		L2ItemInstance foundItem = null;
		ThreadConnection con = null;
		FiltredPreparedStatement statement = null;
		ResultSet rset = null;
		try
		{
			con = L2DatabaseFactory.getInstance().getConnection();
			statement = con.prepareStatement("SELECT object_id FROM items WHERE owner_id=? AND loc=? AND item_id=?");
			statement.setInt(1, w.getOwnerId());
			statement.setString(2, w.getLocationType().name());
			statement.setInt(3, itemId);
			rset = statement.executeQuery();

			if(rset.next())
				foundItem = restoreFromDb(rset.getInt(1));
		}
		catch(Exception e)
		{
			_log.log(Level.WARNING, "could not list warehouse: ", e);
		}
		finally
		{
			DatabaseUtils.closeDatabaseCSR(con, statement, rset);
		}
		return foundItem;
	}

	public synchronized void removeFromDb(int _objectIds)
	{
		ThreadConnection con = null;
		FiltredPreparedStatement statement = null;
		try
		{
			con = L2DatabaseFactory.getInstance().getConnection();
			statement = con.prepareStatement("DELETE FROM items WHERE object_id = ? LIMIT 1");
			statement.setInt(1, _objectIds);
			statement.executeUpdate();
			Stat.increaseDeleteItemCount();
		}
		catch(Exception e)
		{
			_log.log(Level.SEVERE, "Could not delete item " + _objectIds + " in DB:", e);
		}
		finally
		{
			DatabaseUtils.closeDatabaseCS(con, statement);
		}
	}

	public void updateInDb(int ownerId, String loc, int objectId)
	{
		ThreadConnection con = null;
		FiltredPreparedStatement statement = null;
		try
		{
			con = L2DatabaseFactory.getInstance().getConnection();
			statement = con.prepareStatement("UPDATE items SET owner_id=?,loc=? WHERE object_id = ? LIMIT 1");
			statement.setInt(1, ownerId);
			statement.setString(2, loc);
			statement.setInt(3, objectId);
			statement.executeUpdate();
		}
		catch(Exception e)
		{
			//_log.log(Level.SEVERE, "Could not update item " + getObjectId() + " itemID " + _itemId + " count " + getCount() + " owner " + getOwnerId() + " in DB:", e);
		}
		finally
		{
			DatabaseUtils.closeDatabaseCS(con, statement);
		}
	}

	/**
	 * Returns a L2ItemInstance stored in database from its objectID
	 * @param item_rset : ResultSet
	 * @return L2ItemInstance
	 */
	public synchronized L2ItemInstance restoreFromDb(ResultSet item_rset, ThreadConnection con)
	{
		if(item_rset == null)
			return null;
		int objectId = 0;
		try
		{
			objectId = item_rset.getInt("object_id");

			L2Item item = ItemTemplates.getInstance().getTemplate(item_rset.getInt("item_id"));
			if(item == null)
			{
				_log.severe("Item item_id=" + item_rset.getInt("item_id") + " not known, object_id=" + objectId);
				return null;
			}
			if(item_rset.getBoolean("temporal") && item_rset.getInt("shadow_life_time") <= System.currentTimeMillis() / 1000 && item_rset.getInt("shadow_life_time") > 1381505365)
			{
				//_log.severe("time: "+item_rset.getInt("shadow_life_time")+" time2: "+(System.currentTimeMillis()/1000));
				PlayerData.getInstance().removeFromDb(objectId);
				return null;
			}
			L2ItemInstance inst = new L2ItemInstance(objectId, item, item_rset.getBoolean("temporal"));
			inst._existsInDb = true;
			inst._storedInDb = true;
			inst._lifeTimeRemaining = item_rset.getInt("shadow_life_time");
			inst.setOwnerId(item_rset.getInt("owner_id"));
			inst._count = item_rset.getLong("count");
			inst.setEnchantLevel(item_rset.getInt("enchant_level"), item_rset.getLong("enchant_time"));
			inst._type1 = item_rset.getInt("custom_type1");
			inst._type2 = item_rset.getInt("custom_type2");
			inst.setLocat(ItemLocation.valueOf(item_rset.getString("loc")), true);
			inst._loc_data = item_rset.getInt("loc_data");
			inst._customFlags = item_rset.getInt("flags");
			inst._agathionEnergy = item_rset.getInt("energy");
			inst._visual_item_id = item_rset.getInt("visual_item_id");
			inst._visual_enchant_level = item_rset.getInt("visual_enchant_level");

			// load augmentation and elemental enchant
			if(inst.isEquipable())
				restoreAttributes(inst);

			return inst;
		}
		catch(Exception e)
		{
			_log.log(Level.SEVERE, "Could not restore(2) item " + objectId + " from DB: " + e.getMessage());
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Returns a L2ItemInstance stored in database from its objectID
	 * @param objectId : int designating the objectID of the item
	 * @return L2ItemInstance
	 */
	public synchronized L2ItemInstance restoreFromDb(long objectId)
	{
		L2ItemInstance inst = null;
		ThreadConnection con = null;
		FiltredPreparedStatement statement = null;
		ResultSet item_rset = null;
		try
		{
			con = L2DatabaseFactory.getInstance().getConnection();
			statement = con.prepareStatement("SELECT * FROM items WHERE object_id=? LIMIT 1");
			statement.setLong(1, objectId);
			item_rset = statement.executeQuery();
			if(item_rset.next())
				inst = restoreFromDb(item_rset, con);
			else
				_log.severe("Item object_id=" + objectId + " not found");
		}
		catch(Exception e)
		{
			_log.log(Level.SEVERE, "Could not restore item " + objectId + " from DB: " + e.getMessage());
		}
		finally
		{
			DatabaseUtils.closeDatabaseCSR(con, statement, item_rset);
		}
		return inst;
	}

	/**
	 * Delete item from database
	 */
	public synchronized void removeFromDb(L2ItemInstance item, boolean AllowRemoveAttributes)
	{
		if(item.isWear() || !item._existsInDb)
			return;

		// cancel lazy update task if need
		item.stopLazyUpdateTask(true);

		if(AllowRemoveAttributes)
			removeAttributes(item);

		ThreadConnection con = null;
		FiltredPreparedStatement statement = null;
		try
		{
			con = L2DatabaseFactory.getInstance().getConnection();
			statement = con.prepareStatement("DELETE FROM items WHERE object_id = ? LIMIT 1");
			statement.setInt(1, item.getObjectId());
			statement.executeUpdate();

			item._existsInDb = false;
			item._storedInDb = false;

			Stat.increaseDeleteItemCount();
		}
		catch(Exception e)
		{
			_log.log(Level.SEVERE, "Could not delete item " + item.getObjectId() + " in DB:", e);
		}
		finally
		{
			DatabaseUtils.closeDatabaseCS(con, statement);
		}
	}

	/**
	 * Insert the item in database
	 */
	public synchronized void insertIntoDb(L2ItemInstance item)
	{
		if(item.isWear() || item._is_event > -1 || item._not_save)
			return;

		ThreadConnection con = null;
		FiltredPreparedStatement statement = null;
		try
		{
			con = L2DatabaseFactory.getInstance().getConnection();
			if(item._existsInDb)
				statement = con.prepareStatement("UPDATE items set owner_id=?,item_id=?,count=?,loc=?,loc_data=?,enchant_level=?,custom_type1=?,custom_type2=?,shadow_life_time=?,name=?,class=?,flags=?,energy=?,temporal=?,visual_item_id=?,visual_enchant_level=?,enchant_time=? WHERE object_id=?");
			else
				statement = con.prepareStatement("REPLACE INTO items (owner_id,item_id,count,loc,loc_data,enchant_level,custom_type1,custom_type2,shadow_life_time,name,class,flags,energy,temporal,visual_item_id,visual_enchant_level,enchant_time,object_id) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
			statement.setInt(1, item.getOwnerId());
			statement.setInt(2, item._itemId);
			statement.setLong(3, item._count);
			statement.setString(4, item.getLocation().name());
			statement.setInt(5, item._loc_data);
			statement.setInt(6, item._enchantLevel);
			statement.setInt(7, item._type1);
			statement.setInt(8, item._type2);
			statement.setInt(9, item._lifeTimeRemaining);
			statement.setString(10, item.getName());
			statement.setString(11, item.getItemClass().name());
			statement.setInt(12, item._customFlags);
			statement.setInt(13, item._agathionEnergy);
			statement.setString(14, String.valueOf(item.isTemporalItem()));
			statement.setInt(15, item._visual_item_id);
			statement.setInt(16, item._visual_enchant_level);
			statement.setLong(17, item._enchant_time);
			statement.setInt(18, item.getObjectId());
			statement.executeUpdate();

			item._existsInDb = true;
			item._storedInDb = true;

			Stat.increaseInsertItemCount();
		}
		catch(Exception e)
		{
			_log.warning("Could not insert item " + item.getObjectId() + "; itemID=" + item._itemId + "; count=" + item.getCount() + "; owner=" + item.getOwnerId() + "; exception: " + e);
		}
		finally
		{
			DatabaseUtils.closeDatabaseCS(con, statement);
		}
	}

	/**
	 * Update the database with values of the item
	 * Не вызывать нестандартными способами
	 */
	public synchronized void updateInDb(L2ItemInstance item)
	{
		if(item.isWear() || item._storedInDb || item._not_save)
			return;

		ThreadConnection con = null;
		FiltredPreparedStatement statement = null;
		try
		{
			con = L2DatabaseFactory.getInstance().getConnection();
			statement = con.prepareStatement("UPDATE items SET owner_id=?,count=?,loc=?,loc_data=?,enchant_level=?,shadow_life_time=?,item_id=?,flags=?,energy=?,visual_item_id=?,visual_enchant_level=?,enchant_time=? WHERE object_id = ? LIMIT 1");
			statement.setInt(1, item.getOwnerId());
			statement.setLong(2, item._count);
			statement.setString(3, item.getLocation().name());
			statement.setInt(4, item._loc_data);
			statement.setInt(5, item._enchantLevel);
			statement.setInt(6, item._lifeTimeRemaining);
			statement.setInt(7, item.getItemId());
			statement.setInt(8, item._customFlags);
			statement.setInt(9, item.getAgathionEnergy());
			statement.setInt(10, item._visual_item_id);
			statement.setInt(11, item._visual_enchant_level);
			statement.setLong(12, item._enchant_time);
			statement.setInt(13, item.getObjectId());
			statement.executeUpdate();

			item._existsInDb = true;
			item._storedInDb = true;
		}
		catch(Exception e)
		{
			_log.log(Level.SEVERE, "Could not update item " + item.getObjectId() + " itemID " + item._itemId + " count " + item.getCount() + " owner " + item.getOwnerId() + " in DB:", e);
		}
		finally
		{
			DatabaseUtils.closeDatabaseCS(con, statement);
		}
	}

	
	public synchronized void updateItemAttributes(L2ItemInstance item)
	{
		ThreadConnection con = null;
		FiltredPreparedStatement statement = null;
		try
		{
			con = L2DatabaseFactory.getInstance().getConnection();
			statement = con.prepareStatement("REPLACE INTO item_attributes VALUES(?,?,?,?,?,?,?,?,?,?,?,?)");
			statement.setInt(1, item.getObjectId());
			if(item._augmentation == null)
			{
				statement.setInt(2, -1);
				statement.setInt(3, -1);
				statement.setInt(4, -1);
			}
			else
			{
				statement.setInt(2, item._augmentation.getAugmentationId());
				if(item._augmentation.getSkill() == null)
				{
					statement.setInt(3, 0);
					statement.setInt(4, 0);
				}
				else
				{
					statement.setInt(3, item._augmentation.getSkill().getId());
					statement.setInt(4, item._augmentation.getSkill().getLevel());
				}
			}
			statement.setByte(5, item.attackAttributeElement);
			statement.setInt(6, item.attackAttributeValue);
			for(int i = 0; i < item.defenseAttributes.length; i++)
				statement.setInt(7 + i, item.defenseAttributes[i]);
			statement.executeUpdate();
		}
		catch(Exception e)
		{
			_log.info("Could not remove elemental enchant for item: " + item.getObjectId() + " from DB:");
			e.printStackTrace();
		}
		finally
		{
			DatabaseUtils.closeDatabaseCS(con, statement);
		}
	}

	// TODO: Убрать скил, оно не нужно...
	public synchronized void restoreAttributes(L2ItemInstance item)
	{
		ThreadConnection con = null;
		FiltredPreparedStatement statement = null;
		try
		{
			con = L2DatabaseFactory.getInstance().getConnection();
			statement = con.prepareStatement("SELECT augAttributes,augSkillId,augSkillLevel,elemType,elemValue,elem0,elem1,elem2,elem3,elem4,elem5 FROM item_attributes WHERE itemId=? LIMIT 1");
			statement.setInt(1, item.getObjectId());
			ResultSet rs = statement.executeQuery();
			rs = statement.executeQuery();
			if(rs.next())
			{
				int aug_attributes = rs.getInt(1);
				int aug_skillId = rs.getInt(2);
				int aug_skillLevel = rs.getInt(3);
				byte elem_type = rs.getByte(4);
				int elem_value = rs.getInt(5);
				int[] deffAttr = new int[] { 0, 0, 0, 0, 0, 0 };
				for(int i = 0; i < 6; i++)
					deffAttr[i] = rs.getInt(6 + i);
				item.setAttributeElement(elem_type, elem_value, deffAttr, false);

				// Делаю эту поебень для оверо подобных сборок, что бы аугмент переносился норм...
				if(aug_attributes != -1 && aug_skillId == -2 && aug_skillLevel == -2)
				{
					L2Skill skill1 = null;
					OptionDataTemplate template = XmlOptionDataLoader.getInstance().getTemplate(aug_attributes >> 16);
					if(template != null)
					{
						if(template.getSkills().size() > 0)
							skill1 = template.getSkills().get(0);
						if(skill1 == null && template.getTriggerList().size() > 0)
							skill1 = template.getTriggerList().get(0).getSkill();
					}
					if(skill1 == null)
					{
						aug_skillId = 0;
						aug_skillLevel = 0;
					}
					else
					{
						aug_skillId = skill1.getId();
						aug_skillLevel = skill1.getBaseLevel();
					}

					item._augmentation = new L2Augmentation(aug_attributes, aug_skillId, aug_skillLevel);
					item.setCustomFlags(item._customFlags & ~item.FLAG_PET_EQUIPPED, false);
				}
				else if(aug_attributes != -1 && aug_skillId != -1 && aug_skillLevel != -1)
				{
					item._augmentation = new L2Augmentation(aug_attributes, aug_skillId, aug_skillLevel);
					item.setCustomFlags(item._customFlags & ~item.FLAG_PET_EQUIPPED, false);
				}
			}
		}
		catch(Exception e)
		{
			_log.info("Could not restore augmentation and elemental data for item " + item.getObjectId() + " from DB: " + e.getMessage());
			e.printStackTrace();
		}
		finally
		{
			DatabaseUtils.closeDatabaseCS(con, statement);
		}

		// Очищаем некорректные аугментации из базы
		/*if(_augmentation != null && !_augmentation.isLoaded())
		{
			System.out.println("Remove incorrect augmentation from item objId: " + getObjectId() + ", id: " + getItemId());
			removeAugmentation();
		}*/
	}

	/**
	* Удаляет и аугментации, и элементальные аттрибуты.
	*/
	public synchronized void removeAttributes(L2ItemInstance item)
	{
		item._augmentation = null;

		if(item._enchantAttributeFuncTemplate != null)
			for(FuncTemplate func : item._enchantAttributeFuncTemplate)
			{
				if(func != null)
				{
					item.detachFunction(func);
					func._value = 0; // На всякий случай
				}
			}

		item._enchantAttributeFuncTemplate = null;

		item.attackAttributeElement = L2Item.ATTRIBUTE_NONE;
		item.attackAttributeValue = 0;
		item.defenseAttributes = new int[] { 0, 0, 0, 0, 0, 0 };

		ThreadConnection con = null;
		FiltredPreparedStatement statement = null;
		try
		{
			con = L2DatabaseFactory.getInstance().getConnection();
			statement = con.prepareStatement("DELETE FROM item_attributes WHERE itemId = ?");
			statement.setInt(1, item.getObjectId());
			statement.executeUpdate();
		}
		catch(Exception e)
		{
			_log.info("Could not remove attributes for item: " + item.getObjectId() + " from DB:");
			e.printStackTrace();
		}
		finally
		{
			DatabaseUtils.closeDatabaseCS(con, statement);
		}
	}

	public synchronized void removeAugmentation(L2ItemInstance item)
	{
		item._augmentation = null;
		ThreadConnection con = null;
		FiltredPreparedStatement statement = null;
		try
		{
			con = L2DatabaseFactory.getInstance().getConnection();
			if(item.hasAttribute())
				statement = con.prepareStatement("UPDATE item_attributes SET augAttributes = -1, augSkillId = -1, augSkillLevel = -1 WHERE itemId = ? LIMIT 1");
			else
				statement = con.prepareStatement("DELETE FROM item_attributes WHERE itemId = ? LIMIT 1");
			statement.setInt(1, item.getObjectId());
			statement.executeUpdate();
		}
		catch(Exception e)
		{
			_log.info("Could not remove augmentation for item: " + item.getObjectId() + " from DB:");
			e.printStackTrace();
		}
		finally
		{
			DatabaseUtils.closeDatabaseCS(con, statement);
		}
	}

	public void updateApprentice(L2ClanMember cm)
	{
		ThreadConnection con = null;
		FiltredPreparedStatement statement = null;
		try
		{
			con = L2DatabaseFactory.getInstance().getConnection();
			statement = con.prepareStatement("UPDATE characters SET apprentice=? WHERE obj_Id=?");
			statement.setInt(1, cm._apprentice);
			statement.setInt(2, cm.getObjectId());
			statement.execute();
		}
		catch(Exception e)
		{}
		finally
		{
			DatabaseUtils.closeDatabaseCS(con, statement);
		}
	}

	public void updateFraction(int fraction_id, int obj_id)
	{
		ThreadConnection con = null;
		FiltredPreparedStatement statement = null;
		try
		{
			con = L2DatabaseFactory.getInstance().getConnection();
			statement = con.prepareStatement("UPDATE characters SET fraction=? WHERE obj_Id=?");
			statement.setInt(1, fraction_id);
			statement.setInt(2, obj_id);
			statement.execute();
		}
		catch(Exception e)
		{}
		finally
		{
			DatabaseUtils.closeDatabaseCS(con, statement);
		}
	}

	public void updatePowerGrade(L2ClanMember cm)
	{
		ThreadConnection con = null;
		FiltredPreparedStatement statement = null;
		try
		{
			con = L2DatabaseFactory.getInstance().getConnection();
			statement = con.prepareStatement("UPDATE characters SET pledge_rank=? WHERE obj_Id=?");
			statement.setInt(1, cm._powerGrade);
			statement.setInt(2, cm.getObjectId());
			statement.execute();
		}
		catch(Exception e)
		{}
		finally
		{
			DatabaseUtils.closeDatabaseCS(con, statement);
		}
	}

	public void updatePledgeType(L2ClanMember cm)
	{
		ThreadConnection con = null;
		FiltredPreparedStatement statement = null;
		try
		{
			con = L2DatabaseFactory.getInstance().getConnection();
			statement = con.prepareStatement("UPDATE characters SET pledge_type=? WHERE obj_Id=?");
			statement.setInt(1, cm._pledgeType);
			statement.setInt(2, cm.getObjectId());
			statement.execute();
		}
		catch(Exception e)
		{}
		finally
		{
			DatabaseUtils.closeDatabaseCS(con, statement);
		}
	}

	public void setTitleClanMember(L2ClanMember cm, String title)
	{
		ThreadConnection con = null;
		FiltredPreparedStatement statement = null;
		try
		{
			con = L2DatabaseFactory.getInstance().getConnection();
			statement = con.prepareStatement("UPDATE characters SET title=? WHERE obj_Id=?");
			statement.setString(1, title);
			statement.setInt(2, cm.getObjectId());
			statement.execute();
		}
		catch(Exception e)
		{}
		finally
		{
			DatabaseUtils.closeDatabaseCS(con, statement);
		}
	}

	/**
	 * Включить/выключить
	 */
	public void setNoticeEnabled(L2Clan clan, boolean noticeEnabled)
	{
		ThreadConnection con = null;
		FiltredPreparedStatement statement = null;
		try
		{
			con = L2DatabaseFactory.getInstance().getConnection();
			statement = con.prepareStatement("UPDATE clan_notices SET enabled=? WHERE clanID=?");
			statement.setString(1, noticeEnabled ? "true" : "false");
			statement.setInt(2, clan.getClanId());
			statement.execute();
		}
		catch(Exception e)
		{
			_log.warning("BBS: Error while updating notice status for clan " + clan.getClanId() + "");
			if(e.getMessage() != null)
				_log.warning("BBS: Exception = " + e.getMessage() + "");
		}
		finally
		{
			DatabaseUtils.closeDatabaseCS(con, statement);
		}
	}

	/**
	 * Включено или нет?
	 */
	public boolean isNoticeEnabled(L2Clan clan)
	{
		String result = "";
		ThreadConnection con = null;
		FiltredPreparedStatement statement = null;
		ResultSet rset = null;
		try
		{
			con = L2DatabaseFactory.getInstance().getConnection();
			statement = con.prepareStatement("SELECT enabled FROM clan_notices WHERE clanID=?");
			statement.setInt(1, clan.getClanId());
			rset = statement.executeQuery();

			while(rset.next())
				result = rset.getString("enabled");
		}
		catch(Exception e)
		{
			_log.warning("BBS: Error while reading _noticeEnabled for clan " + clan.getClanId() + "");
			if(e.getMessage() != null)
				_log.warning("BBS: Exception = " + e.getMessage() + "");
		}
		finally
		{
			DatabaseUtils.closeDatabaseCSR(con, statement, rset);
		}
		if(result.isEmpty())
			insertNotice(clan);
		else if(result.compareToIgnoreCase("true") == 0)
			return true;
		return false;
	}

	/**
	 * Назначить новое сообщение
	 */
	public void setNotice(L2Clan clan, String notice)
	{
		notice = StringEscapeUtils.escapeHtml4(notice).replace("\n", "<br1>");

		if(notice.length() > L2Clan.NOTICE_MAX_LENGHT)
			notice = notice.substring(0, L2Clan.NOTICE_MAX_LENGHT - 1);
		ThreadConnection con = null;
		FiltredPreparedStatement statement = null;
		try
		{
			con = L2DatabaseFactory.getInstance().getConnection();
			statement = con.prepareStatement("UPDATE clan_notices SET notice=? WHERE clanID=?");
			statement.setString(1, notice);
			statement.setInt(2, clan.getClanId());
			statement.execute();
			clan._notice = notice;
		}
		catch(Exception e)
		{
			_log.warning("BBS: Error while saving notice for clan " + clan.getClanId() + "");
			if(e.getMessage() != null)
				_log.warning("BBS: Exception = " + e.getMessage() + "");
		}
		finally
		{
			DatabaseUtils.closeDatabaseCS(con, statement);
		}
	}

	public String getNotice(L2Clan clan)
	{
		ThreadConnection con = null;
		FiltredPreparedStatement statement = null;
		ResultSet rset = null;
		try
		{
			con = L2DatabaseFactory.getInstance().getConnection();
			statement = con.prepareStatement("SELECT notice FROM clan_notices WHERE clanID=?");
			statement.setInt(1, clan.getClanId());
			rset = statement.executeQuery();
			while(rset.next())
			{
				clan._notice = rset.getString("notice");
				clan._notice = clan._notice/*.replaceAll("bypass", "nononon")*/;
			}
		}
		catch(Exception e)
		{
			_log.warning("BBS: Error while getting notice from DB for clan " + clan.getClanId() + "");
			if(e.getMessage() != null)
				_log.warning("BBS: Exception = " + e.getMessage() + "");
		}
		finally
		{
			DatabaseUtils.closeDatabaseCSR(con, statement, rset);
		}
		return clan._notice;
	}

	public void insertNotice(L2Clan clan)
	{
		ThreadConnection con = null;
		FiltredPreparedStatement statement = null;
		try
		{
			con = L2DatabaseFactory.getInstance().getConnection();
			statement = con.prepareStatement("INSERT INTO clan_notices (clanID, notice, enabled) values (?,?,?)");
			statement.setInt(1, clan.getClanId());
			statement.setString(2, "Change me");
			statement.setString(3, "false");
			statement.execute();
		}
		catch(Exception e)
		{
			_log.warning("BBS: Error while creating clan notice for clan " + clan.getClanId() + "");
			if(e.getMessage() != null)
				_log.warning("BBS: Exception = " + e.getMessage() + "");
		}
		finally
		{
			DatabaseUtils.closeDatabaseCS(con, statement);
		}
	}

	public void setRankPrivs(L2Clan clan, int rank, int privs)
	{
		if(rank < L2Clan.RANK_FIRST || rank > L2Clan.RANK_LAST)
		{
			_log.warning("Requested set of invalid rank value: " + rank);
			Thread.dumpStack();
			return;
		}

		if(clan._Privs.get(rank) != null)
			clan._Privs.get(rank).setPrivs(privs);
		else
			clan._Privs.put(rank, new RankPrivs(rank, clan.countMembersByRank(rank), privs));

		ThreadConnection con = null;
		FiltredPreparedStatement statement = null;
		try
		{
			//_log.warning("requested store clan privs in db for rank: " + rank + ", privs: " + privs);
			// Retrieve all skills of this L2Player from the database
			con = L2DatabaseFactory.getInstance().getConnection();
			statement = con.prepareStatement("REPLACE INTO clan_privs (clan_id,rank,privilleges) VALUES (?,?,?)");
			statement.setInt(1, clan.getClanId());
			statement.setInt(2, rank);
			statement.setInt(3, privs);
			statement.execute();
		}
		catch(Exception e)
		{
			_log.warning("Could not store clan privs for rank: " + e);
			e.printStackTrace();
		}
		finally
		{
			DatabaseUtils.closeDatabaseCS(con, statement);
		}
	}

	public void restoreRankPrivs(L2Clan clan)
	{
		if(clan._Privs == null)
			clan.InitializePrivs();
		ThreadConnection con = null;
		FiltredPreparedStatement statement = null;
		ResultSet rset = null;
		try
		{
			// Retrieve all skills of this L2Player from the database
			con = L2DatabaseFactory.getInstance().getConnection();
			statement = con.prepareStatement("SELECT privilleges,rank FROM clan_privs WHERE clan_id=?");
			statement.setInt(1, clan.getClanId());
			rset = statement.executeQuery();

			// Go though the recordset of this SQL query
			while(rset.next())
			{
				int rank = rset.getInt("rank");
				//int party = rset.getInt("party"); - unused?
				int privileges = rset.getInt("privilleges");
				//noinspection ConstantConditions
				RankPrivs p = clan._Privs.get(rank);
				if(p != null)
					p.setPrivs(privileges);
				else
					_log.warning("Invalid rank value (" + rank + "), please check clan_privs table");
			}
		}
		catch(Exception e)
		{
			_log.warning("Could not restore clan privs by rank: " + e);
			e.printStackTrace();
		}
		finally
		{
			DatabaseUtils.closeDatabaseCSR(con, statement, rset);
		}
	}

	public void restoreSubPledges(L2Clan clan)
	{
		ThreadConnection con = null;
		FiltredPreparedStatement statement = null;
		ResultSet rset = null;
		try
		{
			con = L2DatabaseFactory.getInstance().getConnection();
			statement = con.prepareStatement("SELECT * FROM clan_subpledges WHERE clan_id=?");
			statement.setInt(1, clan.getClanId());
			rset = statement.executeQuery();

			// Go though the recordset of this SQL query
			while(rset.next())
			{
				int type = rset.getInt("type");
				int leaderId = rset.getInt("leader_id");
				String name = rset.getString("name");
				SubPledge pledge = new SubPledge(clan, type, leaderId, name);
				addSubPledge(clan, pledge, false);
			}
		}
		catch(Exception e)
		{
			_log.warning("Could not restore clan SubPledges: " + e);
			e.printStackTrace();
		}
		finally
		{
			DatabaseUtils.closeDatabaseCSR(con, statement, rset);
		}
	}

	public void restoreSkills(L2Clan clan)
	{
		ThreadConnection con = null;
		FiltredPreparedStatement statement = null;
		ResultSet rset = null;
		try
		{
			con = L2DatabaseFactory.getInstance().getConnection();
			statement = con.prepareStatement("SELECT skill_id, skill_level, squad_index FROM clan_skills WHERE clan_id=?");
			statement.setInt(1, clan.getClanId());
			rset = statement.executeQuery();
			while (rset.next())
			{
				int id = rset.getInt("skill_id");
				int level = rset.getInt("skill_level");
				int pId = rset.getInt("squad_index");
				L2Skill skill = SkillTable.getInstance().getInfo(id, level);
				if(pId > -1)
				{
					FastMap<Integer, L2Skill> oldPSkill = clan._squadSkills.get(pId);
					if(oldPSkill == null)
						oldPSkill = new FastMap<Integer, L2Skill>();
					oldPSkill.put(id, skill);
					clan._squadSkills.put(pId, oldPSkill);
				}
				else
					clan._skills.put(skill.getId(), skill);
			}
		}
		catch (Exception e)
		{
			_log.warning("Could not restore clan skills: " + e);
			e.printStackTrace();
		}
		finally
		{
			DatabaseUtils.closeDatabaseCSR(con, statement, rset);
		}
	}

	public void setSubPledgeLeaderId(L2Clan clan, int leaderId, int type)
	{
		ThreadConnection con = null;
		FiltredPreparedStatement statement = null;
		try
		{
			con = L2DatabaseFactory.getInstance().getConnection();
			statement = con.prepareStatement("UPDATE clan_subpledges SET leader_id=? WHERE clan_id=? and type=?");
			statement.setInt(1, leaderId);
			statement.setInt(2, clan.getClanId());
			statement.setInt(3, type);
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

	public final void addSubPledge(L2Clan clan, SubPledge sp, boolean updateDb)
	{
		clan._SubPledges.put(sp.getType(), sp);

		if(updateDb)
		{
			clan.broadcastToOnlineMembers(new PledgeReceiveSubPledgeCreated(sp));
			ThreadConnection con = null;
			FiltredPreparedStatement statement = null;
			try
			{
				con = L2DatabaseFactory.getInstance().getConnection();
				statement = con.prepareStatement("INSERT INTO `clan_subpledges` (clan_id,type,leader_id,name) VALUES (?,?,?,?)");
				statement.setInt(1, clan.getClanId());
				statement.setInt(2, sp.getType());
				statement.setInt(3, sp.getLeaderId());
				statement.setString(4, sp.getName());
				statement.execute();
			}
			catch(Exception e)
			{
				_log.warning("Could not store clan Sub pledges: " + e);
				e.printStackTrace();
			}
			finally
			{
				DatabaseUtils.closeDatabaseCS(con, statement);
			}
		}
	}

	public void addNewSkill(L2Clan clan, L2Skill newSkill, L2Skill oldSkill, int plId)
	{
		ThreadConnection con = null;
		FiltredPreparedStatement statement = null;

		try
		{
			con = L2DatabaseFactory.getInstance().getConnection();
			if(oldSkill != null)
			{
				statement = con.prepareStatement("UPDATE clan_skills SET skill_level=? WHERE skill_id=? AND clan_id=?");
				statement.setInt(1, newSkill.getLevel());
				statement.setInt(2, oldSkill.getId());
				statement.setInt(3, clan.getClanId());
				statement.execute();
			}
			else
			{
				statement = con.prepareStatement("INSERT INTO clan_skills (clan_id,skill_id,skill_level,skill_name,squad_index) VALUES (?,?,?,?,?)");
				statement.setInt(1, clan.getClanId());
				statement.setInt(2, newSkill.getId());
				statement.setInt(3, newSkill.getLevel());
				statement.setString(4, newSkill.getName());
				statement.setInt(5, plId);
				statement.execute();
			}
		}
		catch(Exception e)
		{
			_log.warning("Error could not store char skills: " + e);
			e.printStackTrace();
		}
		finally
		{
			DatabaseUtils.closeDatabaseCS(con, statement);
		}
	}

	public L2Clan restore(int clanId)
	{
		if(clanId == 0) // no clan
			return null;

		L2Clan clan = null;
		int leaderId = 0;

		ThreadConnection con1 = null;
		FiltredPreparedStatement statement1 = null;
		ResultSet clanData = null;

		try
		{
			con1 = L2DatabaseFactory.getInstance().getConnection();
			statement1 = con1.prepareStatement("SELECT * FROM clan_data where clan_id=?");
			statement1.setInt(1, clanId);
			clanData = statement1.executeQuery();

			if(clanData.next())
			{
				clan = new L2Clan(clanId);
				clan.setName(clanData.getString("clan_name"));
				clan._level = clanData.getByte("clan_level");
				clan.setHasCastle(clanData.getByte("hasCastle"));
				clan.setHasFortress(clanData.getByte("hasFortress"));
				clan.setHasHideout(clanData.getInt("hasHideout"));
				clan.setAllyId(clanData.getInt("ally_id"));
				clan._reputation = clanData.getInt("reputation_score");
				clan.setAuctionBiddedAt(clanData.getInt("auction_bid_at"));
				clan.setExpelledMemberTime(clanData.getLong("expelled_member") * 1000L);
				clan.setLeavedAllyTime(clanData.getLong("leaved_ally") * 1000L);
				clan.setDissolvedAllyTime(clanData.getLong("dissolved_ally") * 1000L);
				clan.setWhBonus(clanData.getInt("warehouse"));
				clan.setAirshipLicense(clanData.getInt("airship") == -1 ? false : true);
				if(clan.isHaveAirshipLicense())
					clan.setAirshipFuel(clanData.getInt("airship"));
				clan.clan_point = clanData.getInt("point");

				leaderId = clanData.getInt("leader_id");

				try
				{
					clan._auto_war = clanData.getInt("auto_war") == 1;
				}
				catch(Exception e)
				{}
			}
			else
			{
				_log.warning("L2Clan.java clan " + clanId + " does't exist");
				return null;
			}

			if(clan.getName() == null)
				_log.info("null name for clan?? " + clanId);

			if(clan.getAuctionBiddedAt() > 0 && AuctionManager.getInstance().getAuction(clan.getAuctionBiddedAt()) == null)
				clan.setAuctionBiddedAt(0);
		}
		catch(Exception e)
		{
			_log.warning("error while restoring clan " + e);
			e.printStackTrace();
		}
		finally
		{
			DatabaseUtils.closeDatabaseCSR(con1, statement1, clanData);
		}

		if(clan == null)
		{
			_log.fine("Clan " + clanId + " does't exist");
			return null;
		}

		if(leaderId == 0)
		{
			_log.fine("Not found leader for clan: " + clanId);
			return null;
		}

		ThreadConnection con2 = null;
		FiltredPreparedStatement statement2 = null;
		ResultSet clanMembers = null;

		try
		{
			con2 = L2DatabaseFactory.getInstance().getConnection();
			statement2 = con2.prepareStatement(//
			"SELECT `c`.`char_name` AS `char_name`," + //
			"`s`.`level` AS `level`," + //
			"`s`.`class_id` AS `classid`," + //
			"`c`.`obj_Id` AS `obj_id`," + //
			"`c`.`title` AS `title`," + //
			"`c`.`pledge_type` AS `pledge_type`," + //
			"`c`.`pledge_rank` AS `pledge_rank`," + //
			"`c`.`apprentice` AS `apprentice`, " + //
			"`c`.`pvpkills` AS `pvpkills`, " + //
			"`c`.`pkkills` AS `pkkills`, " + //
			"`c`.`fraction` AS `fraction`, " + //
			"`c`.`lastAccess` AS `lastAccess` " + //
			"FROM `characters` `c` " + //
			"LEFT JOIN `character_subclasses` `s` ON (`s`.`char_obj_id` = `c`.`obj_Id` AND `s`.`isBase` = '1') " + //
			"WHERE `c`.`clanid`=? ORDER BY `c`.`lastaccess` DESC");

			statement2.setInt(1, clanId);
			clanData = statement2.executeQuery();

			statement2.setInt(1, clan.getClanId());
			clanMembers = statement2.executeQuery();

			while(clanMembers.next())
			{
				L2ClanMember member = new L2ClanMember(clan, clanMembers.getString("char_name"), clanMembers.getString("title"), clanMembers.getInt("level"), clanMembers.getInt("classid"), clanMembers.getInt("obj_id"), null, clanMembers.getInt("pledge_type"), clanMembers.getInt("pledge_rank"), clanMembers.getInt("apprentice"), clanMembers.getInt("obj_id") == leaderId, clanMembers.getLong("lastAccess"), clanMembers.getInt("pvpkills"), clanMembers.getInt("pkkills"), clanMembers.getInt("fraction"));
				if(member.getObjectId() == leaderId)
					clan.setLeader(member, false);
				else
					clan.addClanMember(member);
			}

			if(clan.getLeader() == null)
				_log.severe("Clan " + clan.getName() + " have no leader!");
		}
		catch(Exception e)
		{
			_log.warning("Error while restoring clan members for clan: " + clanId + " " + e);
			e.printStackTrace();
		}
		finally
		{
			DatabaseUtils.closeDatabaseCSR(con2, statement2, clanMembers);
		}
		restoreSkills(clan);
		restoreSubPledges(clan);
		restoreRankPrivs(clan);
		clan.setCrestId(CrestCache.getPledgeCrestId(clanId));
		clan.setCrestLargeId(CrestCache.getPledgeCrestLargeId(clanId));
		return clan;
	}

	public void updateClanInDB(L2Clan clan)
	{
		if(clan.getLeaderId() == 0)
		{
			_log.warning("updateClanInDB with empty LeaderId");
			Thread.dumpStack();
			return;
		}
		else if(clan.getClanId() == 0)
		{
			_log.warning("updateClanInDB with empty ClanId");
			Thread.dumpStack();
			return;
		}
		ThreadConnection con = null;
		FiltredPreparedStatement statement = null;
		try
		{
			con = L2DatabaseFactory.getInstance().getConnection();
			statement = con.prepareStatement("UPDATE clan_data SET leader_id=?,ally_id=?,reputation_score=?,expelled_member=?,leaved_ally=?,dissolved_ally=?,clan_level=?,warehouse=?,clan_name=?,airship=?,point=? WHERE clan_id=?");
			statement.setInt(1, clan.getLeaderId());
			statement.setInt(2, clan.getAllyId());
			statement.setInt(3, clan.getReputationScore());
			statement.setLong(4, clan.getExpelledMemberTime() / 1000);
			statement.setLong(5, clan.getLeavedAllyTime() / 1000);
			statement.setLong(6, clan.getDissolvedAllyTime() / 1000);
			statement.setInt(7, clan.getLevel());
			statement.setInt(8, clan.getWhBonus());
			statement.setString(9, clan.getName());
			statement.setInt(10, clan.isHaveAirshipLicense() ? clan.getAirshipFuel() : -1);
			statement.setInt(11, clan.clan_point);
			statement.setInt(12, clan.getClanId());
			statement.execute();
		}
		catch(Exception e)
		{
			_log.warning("error while updating clan '" + clan.getClanId() + "' data in db");
			e.printStackTrace();
		}
		finally
		{
			DatabaseUtils.closeDatabaseCS(con, statement);
		}
	}

	public void store(L2Clan clan)
	{
		ThreadConnection con = null;
		FiltredPreparedStatement statement = null;
		try
		{
			con = L2DatabaseFactory.getInstance().getConnection();
			statement = con.prepareStatement("INSERT INTO clan_data (clan_id,clan_name,clan_level,hasCastle,hasFortress,hasHideout,ally_id,leader_id,expelled_member,leaved_ally,dissolved_ally,airship) values (?,?,?,?,?,?,?,?,?,?,?,?)");
			statement.setInt(1, clan.getClanId());
			statement.setString(2, clan.getName());
			statement.setInt(3, clan.getLevel());
			statement.setInt(4, clan.getHasCastle());
			statement.setInt(5, clan.getHasFortress());
			statement.setInt(6, clan.getHasHideout());
			statement.setInt(7, clan.getAllyId());
			statement.setInt(8, clan.getLeaderId());
			statement.setLong(9, clan.getExpelledMemberTime() / 1000);
			statement.setLong(10, clan.getLeavedAllyTime() / 1000);
			statement.setLong(11, clan.getDissolvedAllyTime() / 1000);
			statement.setInt(12, clan.isHaveAirshipLicense() ? clan.getAirshipFuel() : -1);
			statement.execute();
			DatabaseUtils.closeStatement(statement);

			statement = con.prepareStatement("UPDATE characters SET clanid=?,pledge_type=0 WHERE obj_Id=?");
			statement.setInt(1, clan.getClanId());
			statement.setInt(2, clan.getLeaderId());
			statement.execute();
		}
		catch(Exception e)
		{
			_log.warning("error while saving new clan to db");
			e.printStackTrace();
		}
		finally
		{
			DatabaseUtils.closeDatabaseCS(con, statement);
		}
	}

	public void removeMemberInDatabase(L2ClanMember member)
	{
		ThreadConnection con = null;
		FiltredPreparedStatement statement = null;
		try
		{
			con = L2DatabaseFactory.getInstance().getConnection();
			statement = con.prepareStatement("UPDATE characters SET clanid=0, pledge_type=0, pledge_rank=0, lvl_joined_academy=0, apprentice=0, title='', leaveclan=? WHERE obj_Id=?");
			statement.setLong(1, System.currentTimeMillis() / 1000);
			statement.setInt(2, member.getObjectId());
			statement.execute();
		}
		catch(Exception e)
		{
			_log.warning("error while removing clan member in db " + e);
			e.printStackTrace();
		}
		finally
		{
			DatabaseUtils.closeDatabaseCS(con, statement);
		}
	}

	public boolean isActivation(L2Player player)
	{
		ThreadConnection con = null;
		FiltredPreparedStatement statement = null;
		ResultSet rset = null;
		try
		{
			con = L2DatabaseFactory.getInstanceLogin().getConnection();
			statement = con.prepareStatement("SELECT login, activated FROM accounts WHERE login='"+player.getAccountName()+"' AND activated = '1'");
			rset = statement.executeQuery();
			if(rset.next())
				return true;
		}
		catch(SQLException e)
		{
			e.printStackTrace();
			return false;
		}
		finally
		{
			DatabaseUtils.closeDatabaseCSR(con, statement, rset);
		}
		return false;
	}

	public int countCharForClan(L2Player player)
	{
		ThreadConnection con = null;
		FiltredPreparedStatement statement = null;
		ResultSet rset = null;
		int number = 0;
		try
		{
			con = L2DatabaseFactory.getInstance().getConnection();
			statement = con.prepareStatement("SELECT COUNT(last_hwid) FROM characters WHERE last_hwid=? AND clanid=?");
			statement.setString(1, player.getHWIDs());
			statement.setInt(2, player.getClanId());

			rset = statement.executeQuery();
			while(rset.next())
				number = rset.getInt(1);
		}
		catch(SQLException e)
		{
			_log.warning("could not check existing char number:" + e.getMessage());
		}
		finally
		{
			DatabaseUtils.closeDatabaseCSR(con, statement, rset);
		}
		return number;
	}

	public void LoadClanDescription(L2Player player)
	{
		L2Clan clan = player.getClan();
		if(clan == null || clan.getLevel() < 2)
			return;

		if(clan.getDescription() == null)
		{
			ThreadConnection con = null;
			FiltredPreparedStatement statement = null;
			ResultSet rset = null;
			String description = null;
			try
			{
				con = L2DatabaseFactory.getInstance().getConnection();
				statement = con.prepareStatement("SELECT description FROM `clan_description` WHERE `clan_id` = ?");
				statement.setInt(1, clan.getClanId());
				rset = statement.executeQuery();
				if(rset.next())
					description = rset.getString("description");
			}
			catch(Exception e)
			{
				_log.warning("CommunityBoard -> Recruitment.onPlayerEnter(): " + e);
			}
			finally
			{
				DatabaseUtils.closeDatabaseCSR(con, statement, rset);
			}

			if(description != null)
			{
				description = description.replace("\n", "<br1>\n");
				clan.setDescription(description);
			}
		}
	}

	public L2PetInstance restore_pet(L2ItemInstance control, L2NpcTemplate template, L2Player owner)
	{
		if(control == null)
			return null;
		ThreadConnection con = null;
		FiltredPreparedStatement statement = null;
		ResultSet rset = null;
		try
		{
			con = L2DatabaseFactory.getInstance().getConnection();
			statement = con.prepareStatement("SELECT objId, name, level, curHp, curMp, exp, sp, fed FROM pets WHERE item_obj_id=?");
			statement.setInt(1, control.getObjectId());
			rset = statement.executeQuery();
			L2PetInstance pet = null;
			if(!rset.next())
			{
				if(PetDataTable.isBabyPet(template.getNpcId()) || PetDataTable.isImprovedBabyPet(template.getNpcId()) || PetDataTable.isPremiumPet(template.getNpcId()))
					pet = new L2PetBabyInstance(IdFactory.getInstance().getNextId(), template, owner, control);
				else
					pet = new L2PetInstance(IdFactory.getInstance().getNextId(), template, owner, control);
				return pet;
			}
			if(PetDataTable.isBabyPet(template.getNpcId()) || PetDataTable.isImprovedBabyPet(template.getNpcId()) || PetDataTable.isPremiumPet(template.getNpcId()))
				pet = new L2PetBabyInstance(rset.getInt("objId"), template, owner, control, rset.getByte("level"), rset.getLong("exp"));
			else
				pet = new L2PetInstance(rset.getInt("objId"), template, owner, control, rset.getByte("level"), rset.getLong("exp"));
			pet.setRespawned(true);
			String name = rset.getString("name");
			pet.setName(name == null || name.isEmpty() ? template.name : name);
			pet.setCurrentHpMp(rset.getDouble("curHp"), rset.getInt("curMp"), true);
			pet.setCurrentCp(pet.getMaxCp());
			pet.setSp(rset.getInt("sp"));
			pet.setCurrentFed(rset.getInt("fed"));
			pet.setNonAggroTime(System.currentTimeMillis() + 15000);
			return pet;
		}
		catch(Exception e)
		{
			_log.warning("could not restore Pet data from item[" + (control == null ? 0 : control.getObjectId()) + "]: " + e);
			e.printStackTrace();
			return null;
		}
		finally
		{
			DatabaseUtils.closeDatabaseCSR(con, statement, rset);
		}
	}

	
	/**
	 * Remove the Pet from DB and its associated item from the player inventory
	 * @param owner The owner from whose inventory we should delete the item
	 */
	public void destroyControlItem(L2Player owner, int item_obj_id)
	{
		if(owner == null)
			return;
		if(item_obj_id == 0)
			return;
		// pet control item no longer exists, delete the pet from the db
		ThreadConnection con = null;
		FiltredPreparedStatement statement = null;
		try
		{
			con = L2DatabaseFactory.getInstance().getConnection();
			statement = con.prepareStatement("DELETE FROM pets WHERE item_obj_id=?");
			statement.setInt(1, item_obj_id);
			statement.execute();
		}
		catch(Exception e)
		{
			_log.warning("could not delete pet:" + e);
		}
		finally
		{
			DatabaseUtils.closeDatabaseCS(con, statement);
		}

		try
		{
			owner.getInventory().destroyItem(item_obj_id, 1, true);
		}
		catch(Exception e)
		{
			_log.warning("Error while destroying control item: " + e);
		}
	}

	public void store_pet(L2PetInstance pet)
	{
		if(pet.getControlItemObjId() == 0 || pet.getExp() == 0)
			return;

		String req;
		if(!pet.isRespawned())
			req = "INSERT INTO pets (name,level,curHp,curMp,exp,sp,fed,objId,item_obj_id) VALUES (?,?,?,?,?,?,?,?,?)";
		else
			req = "UPDATE pets SET name=?,level=?,curHp=?,curMp=?,exp=?,sp=?,fed=?,objId=? WHERE item_obj_id = ?";
		ThreadConnection con = null;
		FiltredPreparedStatement statement = null;
		try
		{
			con = L2DatabaseFactory.getInstance().getConnection();
			statement = con.prepareStatement(req);
			statement.setString(1, pet.getName().equalsIgnoreCase(pet.getTemplate().name) ? "" : pet.getName());
			statement.setInt(2, pet.getLevel());
			statement.setDouble(3, pet.getCurrentHp());
			statement.setDouble(4, pet.getCurrentMp());
			statement.setLong(5, pet.getExp());
			statement.setLong(6, pet.getSp());
			statement.setInt(7, pet.getCurrentFed());
			statement.setInt(8, pet.getObjectId());
			statement.setInt(9, pet.getControlItemObjId());
			statement.executeUpdate();
			pet.setRespawned(true);
		}
		catch(Exception e)
		{
			_log.warning("could not store pet data: " + e);
			e.printStackTrace();
		}
		finally
		{
			DatabaseUtils.closeDatabaseCS(con, statement);
		}
	}

	public synchronized void giveAllToOwnerBD(L2PetInstance pet)
	{
		ThreadConnection con = null;
		FiltredPreparedStatement statement = null;
		try
		{
			con = L2DatabaseFactory.getInstance().getConnection();
			statement = con.prepareStatement("UPDATE items SET owner_id=?, loc=? WHERE owner_id=?");
			statement.setInt(1, pet._ownerObjectId);
			statement.setString(2, "WAREHOUSE");
			statement.setInt(3, pet.getObjectId());
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

	/**
	 * сохраняет эффекты для суммона
	 */
	public void storeEffects(L2PetInstance pet)
	{
		ThreadConnection con = null;
		FiltredStatement statement = null;
		try
		{
			con = L2DatabaseFactory.getInstance().getConnection();
			statement = con.createStatement();
			statement.executeUpdate("DELETE FROM summon_effects_save WHERE char_obj_id = " + pet._ownerObjectId + " AND npc_id=" + pet.getNpcId());

			if(pet.getEffectList().isEmpty())
				return;

			int order = 0;
			SqlBatch b = new SqlBatch("INSERT IGNORE INTO `summon_effects_save` (`char_obj_id`,`npc_id`,`skill_id`,`skill_level`,`effect_count`,`effect_cur_time`,`duration`,`order`) VALUES");
			synchronized (pet.getEffectList())
			{
				StringBuilder sb;
				for(L2Effect effect : pet.getEffectList().getAllEffects())
					if(effect != null && effect.isInUse() && !effect.getSkill().isToggle() && effect.getAbnormalType() != SkillAbnormalType.hp_recover && !effect.isOffensive())
					{
						if(effect.isSaveable())
						{
							sb = new StringBuilder("(");
							sb.append(pet._ownerObjectId).append(",");
							sb.append(pet.getNpcId()).append(",");
							sb.append(effect.getSkill().getId()).append(",");
							sb.append(effect.getSkill().getLevel()).append(",");
							sb.append(effect.getCount()).append(",");
							sb.append(effect.getTime()).append(",");
							sb.append(effect.getPeriod()).append(",");
							sb.append(order).append(")");
							b.write(sb.toString());
						}
						order++;
					}
			}
			if(!b.isEmpty())
				statement.executeUpdate(b.close());
		}
		catch(Exception e)
		{
			_log.log(Level.WARNING, "L2SummonInstance.storeEffects() error: ", e);
		}
		finally
		{
			DatabaseUtils.closeDatabaseCS(con, statement);
		}
	}

	/**
	 * восстанавливает эффекты для суммона
	 */
	public void restoreEffects(L2PetInstance pet)
	{
		ThreadConnection con = null;
		FiltredPreparedStatement statement = null;
        FiltredStatement statement1 = null;
		ResultSet rset = null;
		try
		{
			con = L2DatabaseFactory.getInstance().getConnection();
			statement = con.prepareStatement("SELECT `skill_id`,`skill_level`,`effect_count`,`effect_cur_time`,`duration` FROM `summon_effects_save` WHERE `char_obj_id`=? AND `npc_id`=? ORDER BY `order` ASC");
			statement.setInt(1, pet._ownerObjectId);
			statement.setInt(2, pet.getNpcId());
			rset = statement.executeQuery();
			while (rset.next())
			{
				int skillId = rset.getInt("skill_id");
				int skillLvl = rset.getInt("skill_level");
				int effectCount = rset.getInt("effect_count");
				long effectCurTime = rset.getLong("effect_cur_time");
				long duration = rset.getLong("duration");

				L2Skill skill = SkillTable.getInstance().getInfo(skillId, skillLvl);
				if(skill == null)
					continue;
				else if(!skill.isOffensive() && !skill.checkSkillAbnormal(pet) && !skill.isBlockedByChar(pet, skill))
				{
					for(EffectTemplate et : skill.getEffectTemplates())
					{
						if(et == null)
							continue;
						Env env = new Env(pet, pet, skill);
						L2Effect effect = et.getEffect(env);
						if(effect == null)
							continue;
							
						effect.setCount(effectCount);
						if((effectCount == 1 ? duration - effectCurTime : duration) / 1000 > 86400)
							System.out.println("setPeriod-2: Effect " + effect.getSkill().getName() + " I " + (effectCount == 1 ? duration - effectCurTime : duration) / 2);
						effect.setPeriod(effectCount == 1 ? duration - effectCurTime : duration);

						pet.getEffectList().addEffect(effect);
					}
				}
			}
			statement1 = con.createStatement();
            statement1.executeUpdate("DELETE FROM summon_effects_save WHERE char_obj_id = " + pet._ownerObjectId + " AND npc_id=" + pet.getNpcId());
		}
		catch(Exception e)
		{
			_log.log(Level.WARNING, "L2SummonInstance.restoreEffects() error: ", e);
		}
		finally
		{
			DatabaseUtils.closeDatabaseCSR(con, statement, rset);
		}

		pet.updateEffectIcons();
		pet.broadcastPetInfo(); // обновляем иконки баффов
	}

	public void storePetFood(L2PetInstance pet, int petId)
	{
		if(pet.getControlItemObjId() != 0 && petId != 0)
		{
			try
			{
				ThreadConnection con = L2DatabaseFactory.getInstance().getConnection();
				FiltredPreparedStatement statement = con.prepareStatement("UPDATE pets SET fed=? WHERE item_obj_id = ?");
				statement.setInt(1, pet.getCurrentFed());
				statement.setInt(2, pet.getControlItemObjId());
				statement.executeUpdate();
				pet.setControlItemObjId(0);
			}
			catch (Exception e)
			{
				_log.log(Level.SEVERE, "Failed to store Pet [NpcId: " + petId + "] data", e);
			}
		}
	}

	public void relog(L2Player player)
	{
		if(player.inObserverMode())
		{
			player.sendPacket(Msg.OBSERVERS_CANNOT_PARTICIPATE, Msg.ActionFail);
			return;
		}
		else if(player.isInCombat())
		{
			player.sendPacket(Msg.YOU_CANNOT_RESTART_WHILE_IN_COMBAT, Msg.ActionFail);
			return;
		}
		else if(player.isFishing())
		{
			player.sendPacket(Msg.YOU_CANNOT_DO_ANYTHING_ELSE_WHILE_FISHING, Msg.ActionFail);
			return;
		}
		else if(player.isBlocked() && !player.isFlying() && player.i_ai3 != 46534) // Разрешаем выходить из игры если используется сервис HireWyvern. Вернет в начальную точку.
		{
			player.sendMessage(new CustomMessage("l2open.gameserver.clientpackets.RequestRestart.OutOfControl", player));
			player.sendPacket(Msg.ActionFail);
			return;
		}

		if(player.isFestivalParticipant() && SevenSignsFestival.getInstance().isFestivalInitialized())
		{
			player.sendMessage(new CustomMessage("l2open.gameserver.clientpackets.RequestRestart.Festival", player));
			player.sendPacket(Msg.ActionFail);
			return;
		}

		final L2GameClient client = player.getNetConnection();
		final int slot_id = client.getSlotForObjectId(player.getObjectId());
		CharacterSelectionInfo cl = new CharacterSelectionInfo(client.getLoginName(), client.getSessionId().playOkID1);
		player.sendPacket(RestartResponse.OK, cl);
		client.setCharSelection(cl.getCharInfo());
		if(client != null)
			client.setState(GameClientState.AUTHED);

		player.logout(false, true, false, false, ConfigValue.RelogTime);

		L2Player pl = client.loadCharFromDisk(slot_id);
		if(pl == null)
			return;

		pl.getNetConnection().setState(GameClientState.IN_GAME);

		pl.sendPacket(new CharSelected(pl, pl.getNetConnection().getSessionId().playOkID1));
	}
}