package l2open.gameserver.tables;

import l2open.database.DatabaseUtils;
import l2open.database.FiltredPreparedStatement;
import l2open.database.L2DatabaseFactory;
import l2open.database.ThreadConnection;
import l2open.gameserver.model.FishData;
import l2open.gameserver.model.FishDropData;
import l2open.gameserver.model.L2Effect;
import l2open.gameserver.model.L2Player;
import l2open.util.GArray;
import l2open.util.Rnd;

import java.sql.ResultSet;
import java.util.logging.Level;
import java.util.logging.Logger;

public class FishTable
{
	private static Logger _log = Logger.getLogger(SkillTreeTable.class.getName());
	private static FishTable _instance = new FishTable();

	private static GArray<FishData> _fishsNormal;
	private static GArray<FishData> _fishsEasy;
	private static GArray<FishData> _fishsHard;
	private static GArray<FishDropData> _fishRewards;

	public static FishTable getInstance()
	{
		return _instance;
	}

	private FishTable()
	{
		//Create table that contains all fish datas
		int count = 0;
		ThreadConnection con = null;
		FiltredPreparedStatement statement = null;
		ResultSet resultSet = null;
		try
		{
			con = L2DatabaseFactory.getInstance().getConnection();
			try
			{
				_fishsEasy = new GArray<FishData>();
				_fishsNormal = new GArray<FishData>();
				_fishsHard = new GArray<FishData>();

				statement = con.prepareStatement("SELECT id, level, name, hp, hpregen, fish_type, fish_group, fish_guts, guts_check_time, wait_time, combat_time FROM fish ORDER BY id");
				resultSet = statement.executeQuery();

				while(resultSet.next())
				{
					int id = resultSet.getInt("id");
					int lvl = resultSet.getInt("level");
					String name = resultSet.getString("name");
					int hp = resultSet.getInt("hp");
					int hpreg = resultSet.getInt("hpregen");
					int type = resultSet.getInt("fish_type");
					int group = resultSet.getInt("fish_group");
					int fish_guts = resultSet.getInt("fish_guts");
					int guts_check_time = resultSet.getInt("guts_check_time");
					int wait_time = resultSet.getInt("wait_time");
					int combat_time = resultSet.getInt("combat_time");
					FishData fish = new FishData(id, lvl, name, hp, hpreg, type, group, fish_guts, guts_check_time, wait_time, combat_time);
					switch(group)
					{
						case 0:
							_fishsEasy.add(fish);
							break;
						case 1:
							_fishsNormal.add(fish);
							break;
						case 2:
							_fishsHard.add(fish);
					}
				}
				count = _fishsEasy.size() + _fishsNormal.size() + _fishsHard.size();
			}
			catch(Exception e)
			{
				_log.log(Level.SEVERE, "error while creating fishes table" + e);
			}
			finally
			{
				DatabaseUtils.closeDatabaseSR(statement, resultSet);
			}
			_log.info("FishTable: Loaded " + count + " Fishes.");

			// Create Table that contains all fish rewards (drop of fish)
			int count2 = 0;
			try
			{
				_fishRewards = new GArray<FishDropData>();
				FishDropData fishreward;
				statement = con.prepareStatement("SELECT fishid, rewardid, min, max, chance FROM fishreward ORDER BY fishid");
				resultSet = statement.executeQuery();

				while(resultSet.next())
				{
					short fishid = resultSet.getShort("fishid");
					short rewardid = resultSet.getShort("rewardid");
					int mindrop = resultSet.getInt("min");
					int maxdrop = resultSet.getInt("max");
					int chance = resultSet.getInt("chance");
					fishreward = new FishDropData(fishid, rewardid, mindrop, maxdrop, chance);
					_fishRewards.add(fishreward);
				}
				count2 = _fishRewards.size();
			}
			catch(Exception e)
			{
				_log.log(Level.SEVERE, "error while creating fish rewards table" + e);
			}
			finally
			{
				DatabaseUtils.closeDatabaseSR(statement, resultSet);
			}
			_log.info("FishRewardsTable: Loaded " + count2 + " FishRewards.");
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			DatabaseUtils.closeDatabaseCSR(con, statement, resultSet);
		}
	}

	/**
	 * @param Fish - lvl
	 * @param Fish - type
	 * @param Fish - group
	 * @return List of Fish that can be fished
	 */
	public GArray<FishData> getfish(int lvl, int type, int group)
	{
		GArray<FishData> result = new GArray<FishData>();
		GArray<FishData> fishs = null;
		switch(group)
		{
			case 0:
				fishs = _fishsEasy;
				break;
			case 1:
				fishs = _fishsNormal;
				break;
			case 2:
				fishs = _fishsHard;
		}
		if(fishs == null)
		{
			// the fish list is empty
			_log.warning("Fish are not defined !");
			return null;
		}
		for(FishData f : fishs)
		{
			if(f.getLevel() != lvl)
				continue;
			if(f.getType() != type)
				continue;

			result.add(new FishData(f.getId(), f.getLevel(), f.getName(), f.getHP(), f.getHpRegen(), f.getType(), f.getGroup(), f.getFishGuts(), f.getGutsCheckTime(), f.getWaitTime(), f.getCombatTime()));
		}
		if(result.size() == 0)
			_log.warning("Cant Find Any Fish!? - Lvl: " + lvl + " Type: " + type);
		return result;
	}

	public GArray<FishDropData> getFishReward(int fishid)
	{
		GArray<FishDropData> result = new GArray<FishDropData>();
		if(_fishRewards == null)
		{
			// the fish list is empty
			_log.warning("FishRewards are not defined !");
			return null;
		}
		for(FishDropData d : _fishRewards)
		{
			if(d.getFishId() != fishid)
				continue;

			result.add(d);
		}
		if(result.size() == 0)
			_log.warning("Cant Find Any Fish Reward for ItemID: " + fishid);

		return result;
	}

	public int GetFishItemCount()
	{
		return _fishRewards.size();
	}

	public short getFishIdfromList(int i)
	{
		return _fishRewards.get(i).getFishId();
	}

	public int GetRandomFishType(int group, int lureId)
	{
		int check = Rnd.get(100);
		int type = 1;
		switch(group)
		{
			case 0: // fish for novices
				switch(lureId)
				{
					case 7807: //green lure, preferred by fast-moving (nimble) fish (type 5)
						if(check <= 54)
							type = 5;
						else if(check <= 77)
							type = 4;
						else
							type = 6;
						break;
					case 7808: //purple lure, preferred by fat fish (type 4)
						if(check <= 54)
							type = 4;
						else if(check <= 77)
							type = 6;
						else
							type = 5;
						break;
					case 7809: //yellow lure, preferred by ugly fish (type 6)
						if(check <= 54)
							type = 6;
						else if(check <= 77)
							type = 5;
						else
							type = 4;
						break;
					case 8486: //prize-winning fishing lure for beginners
						if(check <= 33)
							type = 4;
						else if(check <= 66)
							type = 5;
						else
							type = 6;
						break;
				}
				break;
			case 1: // normal fish
				switch(lureId)
				{
					case 7610:
					case 7611:
					case 7612:
					case 7613:
						type = 3;
						break;
					case 6519: // all theese lures (green) are prefered by fast-moving (nimble) fish (type 1)
					case 8505:
					case 6520:
					case 6521:
					case 8507:
						if(check <= 54)
							type = 1;
						else if(check <= 74)
							type = 0;
						else if(check <= 94)
							type = 2;
						else
							type = 3;
						break;
					case 6522: // all theese lures (purple) are prefered by fat fish (type 0)
					case 8508:
					case 6523:
					case 6524:
					case 8510:
						if(check <= 54)
							type = 0;
						else if(check <= 74)
							type = 1;
						else if(check <= 94)
							type = 2;
						else
							type = 3;
						break;
					case 6525: // all theese lures (yellow) are prefered by ugly fish (type 2)
					case 8511:
					case 6526:
					case 6527:
					case 8513:
						if(check <= 55)
							type = 2;
						else if(check <= 74)
							type = 1;
						else if(check <= 94)
							type = 0;
						else
							type = 3;
						break;
					case 8484: // prize-winning fishing lure
						if(check <= 33)
							type = 0;
						else if(check <= 66)
							type = 1;
						else
							type = 2;
						break;
				}
				break;
			case 2: // upper grade fish, luminous lure
				switch(lureId)
				{
					case 8506: //green lure, preferred by fast-moving (nimble) fish (type 8)
						if(check <= 54)
							type = 8;
						else if(check <= 77)
							type = 7;
						else
							type = 9;
						break;
					case 8509: // purple lure, preferred by fat fish (type 7)
						if(check <= 54)
							type = 7;
						else if(check <= 77)
							type = 9;
						else
							type = 8;
						break;
					case 8512: // yellow lure, preferred by ugly fish (type 9)
						if(check <= 54)
							type = 9;
						else if(check <= 77)
							type = 8;
						else
							type = 7;
						break;
					case 8485: // prize-winning fishing lure
						if(check <= 33)
							type = 7;
						else if(check <= 66)
							type = 8;
						else
							type = 9;
						break;
				}
		}
		return type;
	}

	public int GetRandomFishLvl(L2Player player)
	{
		int skilllvl = 0;

		// Проверка на Fisherman's Potion
		L2Effect effect = player.getEffectList().getEffectBySkillId(2274);
		if(effect != null)
			skilllvl = (int) effect.getSkill().getPower();
		else if((effect = player.getEffectList().getEffectBySkillId(3315)) != null)
			skilllvl = (int) effect.getSkill().getPower();
		else
			skilllvl = player.getSkillLevel(1315);

		if(skilllvl <= 0)
			return 1;

		int randomlvl;
		int check = Rnd.get(100);

		if(check < 50)
			randomlvl = skilllvl;
		else if(check <= 85)
		{
			randomlvl = skilllvl - 1;
			if(randomlvl <= 0)
				randomlvl = 1;
		}
		else
			randomlvl = skilllvl + 1;

		randomlvl = Math.min(27, Math.max(1, randomlvl));

		return randomlvl;
	}

	public int GetGroupForLure(int lureId)
	{
		switch(lureId)
		{
			case 7807: // green for beginners
			case 7808: // purple for beginners
			case 7809: // yellow for beginners
			case 8486: // prize-winning for beginners
				return 0;
			case 8506: // green luminous
			case 8509: // purple luminous
			case 8512: // yellow luminous
			case 8485: // prize-winning luminous
				return 2;
			default:
				return 1;
		}

		/**
		switch(lureId)
		{
			case 7807: // green for beginners
			case 7808: // purple for beginners
			case 7809: // yellow for beginners
			case 8486: // prize-winning for beginners
				return 0;
			case 8485: // Prize-Winning Night Fishing Lure
			case 8505: // Green Luminous Lure - Low Grade
			case 8506: // Green Luminous Lure
			case 8507: // Green Colored Lure - High Grade
			case 8508: // Purple Luminous Lure - Low Grade
			case 8509: // Purple Luminous Lure
			case 8510: // Purple Luminous Lure - High Grade
			case 8511: // Yellow Luminous Lure - Low Grade
			case 8512: // Yellow Luminous Lure
			case 8513: // Yellow Luminous Lure - High Grade
				return 2;
			default:
				return 1;
		}
		*/
	}

	public void reload()
	{
		_instance = new FishTable();
	}
}