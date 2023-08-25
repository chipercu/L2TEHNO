package l2open.gameserver.tables;

import l2open.config.ConfigValue;
import l2open.database.DatabaseUtils;
import l2open.database.FiltredPreparedStatement;
import l2open.database.L2DatabaseFactory;
import l2open.database.ThreadConnection;
import l2open.gameserver.model.L2Character;
import l2open.gameserver.model.L2PetData;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.L2Summon;
import l2open.gameserver.model.items.L2ItemInstance;
import l2open.gameserver.tables.player.PlayerData;

import java.sql.ResultSet;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PetDataTable
{
	private static final Logger _log = Logger.getLogger(PetDataTable.class.getName());

	private static PetDataTable _instance = new PetDataTable();
	private HashMap<Integer, L2PetData> _pets;
	private static HashMap<Integer, Integer> _control_item;
	private static int[] _control_item_;

	public final static int PET_WOLF_ID = 12077;

	public final static int HATCHLING_WIND_ID = 12311;
	public final static int HATCHLING_STAR_ID = 12312;
	public final static int HATCHLING_TWILIGHT_ID = 12313;

	public final static int STRIDER_WIND_ID = 12526;
	public final static int STRIDER_STAR_ID = 12527;
	public final static int STRIDER_TWILIGHT_ID = 12528;

	public final static int RED_STRIDER_WIND_ID = 16038;
	public final static int RED_STRIDER_STAR_ID = 16039;
	public final static int RED_STRIDER_TWILIGHT_ID = 16040;

	public final static int WYVERN_ID = 12621;

	public final static int BABY_BUFFALO_ID = 12780;
	public final static int BABY_KOOKABURRA_ID = 12781;
	public final static int BABY_COUGAR_ID = 12782;

	public final static int IMPROVED_BABY_BUFFALO_ID = 16034;
	public final static int IMPROVED_BABY_KOOKABURRA_ID = 16035;
	public final static int IMPROVED_BABY_COUGAR_ID = 16036;

	public final static int SIN_EATER_ID = 12564;

	public final static int GREAT_WOLF_ID = 16025;
	public final static int WGREAT_WOLF_ID = 16037;
	public final static int FENRIR_WOLF_ID = 16041;
	public final static int WFENRIR_WOLF_ID = 16042;

	public final static int AURA_BIRD_FALCON_ID = 13144;
	public final static int AURA_BIRD_OWL_ID = 13145;

	public final static int FOX_SHAMAN_ID = 16043;
	public final static int WILD_BEAST_FIGHTER_ID = 16044;
	public final static int WHITE_WEASEL_ID = 16045;
	public final static int FAIRY_PRINCESS_ID = 16046;
	public final static int OWL_MONK_ID = 16050;
	public final static int SPIRIT_SHAMAN_ID = 16051;
	public final static int TOY_KNIGHT_ID = 16052;
	public final static int TURTLE_ASCETIC_ID = 16053;
	public final static int DEINONYCHUS_ID = 16067;
	public final static int GUARDIANS_STRIDER_ID = 16068;
	public final static int WILD_MAGUEN_ID = 16071;
	public final static int ELITE_MAGUEN_ID = 16072;

	public final static int ROSE_DESELOPH_ID = 1562;
	public final static int ROSE_HYUM_ID = 1563;
	public final static int ROSE_REKANG_ID = 1564;
	public final static int ROSE_LILIAS_ID = 1565;
	public final static int ROSE_LAPHAM_ID = 1566;
	public final static int ROSE_MAPHUM_ID = 1567;

	public final static int IMPROVED_ROSE_DESELOPH_ID = 1568;
	public final static int IMPROVED_ROSE_HYUM_ID = 1569;
	public final static int IMPROVED_ROSE_REKANG_ID = 1570;
	public final static int IMPROVED_ROSE_LILIAS_ID = 1571;
	public final static int IMPROVED_ROSE_LAPHAM_ID = 1572;
	public final static int IMPROVED_ROSE_MAPHUM_ID = 1573;

	public final static int SUPER_FELINE_QUEEN_Z_ID = 1601;
	public final static int SUPER_KAT_THE_CAT_Z_ID = 1602;
	public final static int SUPER_MEW_THE_CAT_Z_ID = 1603;

	public static PetDataTable getInstance()
	{
		return _instance;
	}

	public static void reload()
	{
		_instance = new PetDataTable();
	}

	private PetDataTable()
	{
		_pets = new HashMap<Integer, L2PetData>(1200, 0.95f);
		_control_item = new HashMap<Integer, Integer>(1200, 0.95f);
		FillPetDataTable();
	}

	public L2PetData getInfo(int petNpcId, int level)
	{
		L2PetData result = null;
		while(result == null && level < 100)
		{
			result = _pets.get(petNpcId * 100 + level);
			level++;
		}

		return result;
	}

	private void FillPetDataTable()
	{
		ThreadConnection con = null;
		FiltredPreparedStatement statement = null;
		ResultSet rset = null;
		L2PetData petData;
		try
		{
			con = L2DatabaseFactory.getInstance().getConnection();
			statement = con.prepareStatement("SELECT * FROM pet_data");
			rset = statement.executeQuery();
			while(rset.next())
			{
				petData = new L2PetData();
				petData.setID(rset.getInt("id"));
				petData.setLevel(rset.getInt("level"));
				petData.setExp(rset.getLong("exp"));
				petData.setHP(rset.getInt("hp"));
				petData.setMP(rset.getInt("mp"));
				petData.setPAtk(rset.getInt("patk"));
				petData.setPDef(rset.getInt("pdef"));
				petData.setMAtk(rset.getInt("matk"));
				petData.setMDef(rset.getInt("mdef"));
				petData.setAccuracy(rset.getInt("acc"));
				petData.setEvasion(rset.getInt("evasion"));
				petData.setCritical(rset.getInt("crit"));
				petData.setSpeed(rset.getInt("speed"));
				petData.setAtkSpeed(rset.getInt("atk_speed"));
				petData.setCastSpeed(rset.getInt("cast_speed"));
				petData.setFeedMax(rset.getInt("max_meal"));
				petData.setFeedBattle(rset.getInt("battle_meal"));
				petData.setFeedNormal(rset.getInt("normal_meal"));
				petData.setMaxLoad(rset.getInt("loadMax"));
				petData.setHpRegen(rset.getDouble("hpregen"));
				petData.setMpRegen(rset.getDouble("mpregen"));
				petData.setExpType(rset.getFloat("get_exp_type"));
				petData.setSpiritshot(rset.getInt("spiritshot_count"));
				petData.setSoulshot(rset.getInt("soulshot_count"));
				petData.setControlItemId(rset.getInt("item"));
				petData.setMountable(rset.getBoolean("mountabe"));
				petData.setMinLevel(rset.getInt("hungry_limit"));

				if(ConfigValue.VitaminPetRegenItemId != -2 && isVitaminPet(petData.getID()))
				{
					petData.setFoodId(ConfigValue.VitaminPetRegenItemId);
					petData.setAddFed(ConfigValue.VitaminPetRegenValue);
				}
				else
				{
					petData.setFoodId(rset.getInt("food"));
					petData.setAddFed(rset.getInt("add_meal"));
				}

				_pets.put(petData.getID() * 100 + petData.getLevel(), petData);

				_control_item.put(petData.getID(), petData.getControlItemId());
			}
		}
		catch(Exception e)
		{
			_log.warning("Cannot fill up PetDataTable: " + e);
			e.printStackTrace();
		}
		finally
		{
			DatabaseUtils.closeDatabaseCSR(con, statement, rset);
		}
		_control_item_ = new int[_control_item.size()];

		int i=0;
		for(Integer value : _control_item.values())
			_control_item_[i++] = value;

		_log.info("PetDataTable: Loaded " + _pets.size() + "["+_control_item_.length+"] pets.");
	}

	public static void deletePet(L2ItemInstance item, L2Character owner)
	{
		int petObjectId = 0;
		ThreadConnection con = null;
		FiltredPreparedStatement statement = null;
		ResultSet rset = null;
		try
		{
			con = L2DatabaseFactory.getInstance().getConnection();
			statement = con.prepareStatement("SELECT objId FROM pets WHERE item_obj_id=?");
			statement.setInt(1, item.getObjectId());
			rset = statement.executeQuery();
			while(rset.next())
				petObjectId = rset.getInt("objId");
			DatabaseUtils.closeDatabaseSR(statement, rset);

			L2Summon summon = owner.getPet();
			if(summon != null && summon.getObjectId() == petObjectId)
				summon.unSummon();

			L2Player player = owner.getPlayer();
			if(player != null && player.isMounted() && player.getMountObjId() == petObjectId)
				player.setMount(0, 0, 0);

			// if it's a pet control item, delete the pet
			statement = con.prepareStatement("DELETE FROM pets WHERE item_obj_id=?");
			statement.setInt(1, item.getObjectId());
			statement.execute();
			DatabaseUtils.closeStatement(statement);

			// сначала возвращаем все итемы хозяйну...
			giveAllToOwner(player, petObjectId, item.getObjectId());

			// Далее, удаляем к чертям из БД все итемы пета...
			statement = con.prepareStatement("DELETE FROM items WHERE owner_id=?");
			statement.setInt(1, petObjectId);
			statement.execute();
		}
		catch(Exception e)
		{
			_log.log(Level.WARNING, "could not restore pet objectid:", e);
		}
		finally
		{
			DatabaseUtils.closeDatabaseCSR(con, statement, rset);
		}
	}

	public static void giveAllToOwner(L2Player player, int OWNER, int item_obj_id)
	{
		ThreadConnection con = null;
		FiltredPreparedStatement statement = null;
		ResultSet rset = null;

		try
		{
			con = L2DatabaseFactory.getInstance().getConnection();
			if(player == null)
			{
				statement = con.prepareStatement("UPDATE `items` AS t1 INNER JOIN(SELECT `owner_id` AS cnt FROM `items` WHERE object_id=?) AS t2 SET t1.owner_id=t2.cnt, t1.loc=? WHERE t1.owner_id=?");
				statement.setInt(1, item_obj_id);
				statement.setString(2, "WAREHOUSE");
				statement.setInt(3, OWNER);
				statement.execute();
			}
			else
			{
				statement = con.prepareStatement("SELECT * FROM items WHERE owner_id=? ORDER BY object_id DESC");
				statement.setInt(1, OWNER);
				rset = statement.executeQuery();

				L2ItemInstance item;
				while(rset.next())
				{
					if((item = PlayerData.getInstance().restoreFromDb(rset, con)) == null)
						continue;
					if(player.getInventoryLimit() * 0.8 > player.getInventory().getSize())
						player.getInventory().addItem(item, true, true, true); // на всякий случай стак итемы записываем сразу в БД.
					else
						player.getWarehouse().addItem(item, player.getName());
				}
				player.getInventory().refreshWeight();
			}
		}
		catch(Exception e)
		{
			_log.log(Level.WARNING, "could not restore pet inventory for player " + player.getName() + ":", e);
		}
		finally
		{
			DatabaseUtils.closeDatabaseCSR(con, statement, rset);
		}
	}

	public static void unSummonPet(L2ItemInstance oldItem, L2Character owner)
	{
		int petObjectId = 0;
		ThreadConnection con = null;
		FiltredPreparedStatement statement = null;
		ResultSet rset = null;
		try
		{
			con = L2DatabaseFactory.getInstance().getConnection();
			statement = con.prepareStatement("SELECT objId FROM pets WHERE item_obj_id=?");
			statement.setInt(1, oldItem.getObjectId());
			rset = statement.executeQuery();

			while(rset.next())
				petObjectId = rset.getInt("objId");

			if(owner == null)
				return;

			L2Summon summon = owner.getPet();
			if(summon != null && summon.getObjectId() == petObjectId)
				summon.unSummon();

			L2Player player = owner.getPlayer();
			if(player != null && player.isMounted() && player.getMountObjId() == petObjectId)
				player.setMount(0, 0, 0);
			DatabaseUtils.closeDatabaseSR(statement, rset);

			// сначала возвращаем все итемы хозяйну...
			giveAllToOwner(player, petObjectId, oldItem.getObjectId());

			// Далее, удаляем к чертям из БД все итемы пета...
			statement = con.prepareStatement("DELETE FROM items WHERE owner_id=?");
			statement.setInt(1, petObjectId);
			statement.execute();
		}
		catch(Exception e)
		{
			_log.log(Level.WARNING, "could not restore pet objectid:", e);
		}
		finally
		{
			DatabaseUtils.closeDatabaseCSR(con, statement, rset);
		}
	}

	public static int getControlItemId(int npcId)
	{
		if(_control_item.containsKey(npcId))
			return _control_item.get(npcId);
		return 1;
	}

	public static int getSummonId(L2ItemInstance item)
	{
		for(Integer id : _control_item.keySet())
		{
			int value = _control_item.get(id);
			if(value == item.getItemId())
				return id;
		}
		return 0;
	}

	public static int[] getPetControlItems()
	{
		return _control_item_;
	}

	public static boolean isPetControlItem(L2ItemInstance item)
	{
		return _control_item.containsValue(item.getItemId());
	}

	public static boolean isBabyPet(int id)
	{
		switch(id)
		{
			case BABY_BUFFALO_ID:
			case BABY_KOOKABURRA_ID:
			case BABY_COUGAR_ID:
				return true;
			default:
				return false;
		}
	}

	public static boolean isImprovedBabyPet(int id)
	{
		switch(id)
		{
			case IMPROVED_BABY_BUFFALO_ID:
			case IMPROVED_BABY_KOOKABURRA_ID:
			case IMPROVED_BABY_COUGAR_ID:
			case FAIRY_PRINCESS_ID:
			case ROSE_DESELOPH_ID:
			case ROSE_HYUM_ID:
			case ROSE_REKANG_ID:
			case ROSE_LILIAS_ID:
			case ROSE_LAPHAM_ID:
			case ROSE_MAPHUM_ID:
			case IMPROVED_ROSE_DESELOPH_ID:
			case IMPROVED_ROSE_HYUM_ID:
			case IMPROVED_ROSE_REKANG_ID:
			case IMPROVED_ROSE_LILIAS_ID:
			case IMPROVED_ROSE_LAPHAM_ID:
			case IMPROVED_ROSE_MAPHUM_ID:
			case SUPER_KAT_THE_CAT_Z_ID:
			case SUPER_MEW_THE_CAT_Z_ID:
				return true;
			default:
				return false;
		}
	}

	public static boolean isWolf(int id)
	{
		return id == PET_WOLF_ID;
	}

	public static boolean isHatchling(int id)
	{
		switch(id)
		{
			case HATCHLING_WIND_ID:
			case HATCHLING_STAR_ID:
			case HATCHLING_TWILIGHT_ID:
				return true;
			default:
				return false;
		}
	}

	public static boolean isStrider(int id)
	{
		switch(id)
		{
			case STRIDER_WIND_ID:
			case STRIDER_STAR_ID:
			case STRIDER_TWILIGHT_ID:
			case RED_STRIDER_WIND_ID:
			case RED_STRIDER_STAR_ID:
			case RED_STRIDER_TWILIGHT_ID:
			case GUARDIANS_STRIDER_ID:
				return true;
			default:
				return false;
		}
	}

	public static boolean isGWolf(int id)
	{
		switch(id)
		{
			case GREAT_WOLF_ID:
			case WGREAT_WOLF_ID:
			case FENRIR_WOLF_ID:
			case WFENRIR_WOLF_ID:
				return true;
			default:
				return false;
		}
	}

	public static boolean isPremiumPet(int id)
	{
		switch(id)
		{
			case FOX_SHAMAN_ID:
			case WILD_BEAST_FIGHTER_ID:
			case WHITE_WEASEL_ID:
			case FAIRY_PRINCESS_ID:
			case OWL_MONK_ID:
			case SPIRIT_SHAMAN_ID:
			case TOY_KNIGHT_ID:
			case TURTLE_ASCETIC_ID:
			case SUPER_FELINE_QUEEN_Z_ID:
			case SUPER_KAT_THE_CAT_Z_ID:
			case SUPER_MEW_THE_CAT_Z_ID:
				return true;
			default:
				return false;
		}
	}

	public static boolean isVitaminPet(int id)
	{
		switch(id)
		{
			case ROSE_DESELOPH_ID:
			case ROSE_HYUM_ID:
			case ROSE_REKANG_ID:
			case ROSE_LILIAS_ID:
			case ROSE_LAPHAM_ID:
			case ROSE_MAPHUM_ID:
			case IMPROVED_ROSE_DESELOPH_ID:
			case IMPROVED_ROSE_HYUM_ID:
			case IMPROVED_ROSE_REKANG_ID:
			case IMPROVED_ROSE_LILIAS_ID:
			case IMPROVED_ROSE_LAPHAM_ID:
			case IMPROVED_ROSE_MAPHUM_ID:
			case FOX_SHAMAN_ID:
			case WILD_BEAST_FIGHTER_ID:
			case WHITE_WEASEL_ID:
			case FAIRY_PRINCESS_ID:
			case OWL_MONK_ID:
			case SPIRIT_SHAMAN_ID:
			case TOY_KNIGHT_ID:
			case TURTLE_ASCETIC_ID:
			case SUPER_FELINE_QUEEN_Z_ID:
			case SUPER_KAT_THE_CAT_Z_ID:
			case SUPER_MEW_THE_CAT_Z_ID:
				return true;
			default:
				return false;
		}
	}

	public static void unload()
	{
		if(_instance != null)
			_instance = null;
	}
}