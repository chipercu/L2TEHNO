package l2open.gameserver.model;

import l2open.config.*;
import l2open.gameserver.cache.Msg;
import l2open.gameserver.serverpackets.EtcStatusUpdate;
import l2open.gameserver.serverpackets.SystemMessage;
import l2open.gameserver.tables.SkillTable;
import l2open.util.Rnd;

public class DeathPenalty
{
	private static final int _skillId = 5076;
	private static final int _fortuneOfNobleseSkillId = 1325;
	private static final int _charmOfLuckSkillId = 2168;

	private L2Player _player = null;
	private byte _level;
	private boolean _hasCharmOfLuck;

	public DeathPenalty(L2Player player, byte level)
	{
		_player = player;
		_level = player.isGM() ? 0 : level;
	}

	public L2Player getPlayer()
	{
		return _player;
	}

	/*
	 * For common usage
	 */
	public int getLevel()
	{
		// Some checks if admin set incorrect value at database
		if(_level > 15)
			_level = 15;

		if(_level < 0)
			_level = 0;

		return ConfigValue.EnableDeathPenaltyC5 ? _level : 0;
	}

	/*
	 * Used only when saving DB if admin for some reasons disabled it in config after it was enabled.
	 * In if we will use getLevel() it will be reseted to 0
	 */
	public int getLevelOnSaveDB()
	{
		if(_level > 15)
			_level = 15;

		if(_level < 0)
			_level = 0;

		return _level;
	}

	public void notifyDead(L2Character killer)
	{
		if(!ConfigValue.EnableDeathPenaltyC5 || killer == null)
			return;

		if(_hasCharmOfLuck)
		{
			_hasCharmOfLuck = false;
			return;
		}

		L2Player player = getPlayer();
		if(player == null || player.getLevel() <= 9)
			return;

		int karmaBonus = player.getKarma() / ConfigValue.DeathPenaltyC5RateKarma;
		if(karmaBonus < 0)
			karmaBonus = 0;

		if(killer != null && Rnd.chance(ConfigValue.DeathPenaltyC5Chance + karmaBonus) && !killer.isPlayable())
			addLevel();
	}

	public void restore()
	{
		L2Player player = getPlayer();
		if(player == null)
			return;

		L2Skill remove = getCurrentSkill();
		if(remove != null)
			player.removeSkill(remove, true, true);

		if(!ConfigValue.EnableDeathPenaltyC5)
			return;

		if(getLevel() > 0)
		{
			player.addSkill(SkillTable.getInstance().getInfo(_skillId, getLevel()), false);
			player.sendPacket(new SystemMessage(SystemMessage.THE_LEVEL_S1_DEATH_PENALTY_WILL_BE_ASSESSED).addNumber(getLevel()));
		}
		player.sendPacket(new EtcStatusUpdate(player));
		player.broadcastUserInfo(true);
	}

	public void addLevel()
	{
		L2Player player = getPlayer();
		if(player == null || getLevel() >= 15 || player.isGM())
			return;

		if(getLevel() != 0)
		{
			L2Skill remove = getCurrentSkill();
			if(remove != null)
				player.removeSkill(remove, true, true);
		}

		_level++;

		player.addSkill(SkillTable.getInstance().getInfo(_skillId, getLevel()), false);
		player.sendPacket(new EtcStatusUpdate(player), new SystemMessage(SystemMessage.THE_LEVEL_S1_DEATH_PENALTY_WILL_BE_ASSESSED).addNumber(getLevel()));
		player.broadcastUserInfo(true);
	}

	public void reduceLevel()
	{
		L2Player player = getPlayer();
		if(player == null || getLevel() <= 0)
			return;

		L2Skill remove = getCurrentSkill();
		if(remove != null)
			player.removeSkill(remove, true, true);

		_level--;

		if(getLevel() > 0)
		{
			player.addSkill(SkillTable.getInstance().getInfo(_skillId, getLevel()), false);
			player.sendPacket(new EtcStatusUpdate(player), new SystemMessage(SystemMessage.THE_LEVEL_S1_DEATH_PENALTY_WILL_BE_ASSESSED).addNumber(getLevel()));
			player.broadcastUserInfo(true);
		}
		else
		{
			player.sendPacket(new EtcStatusUpdate(player), Msg.THE_DEATH_PENALTY_HAS_BEEN_LIFTED);
			player.broadcastUserInfo(true);
		}
	}

	public L2Skill getCurrentSkill()
	{
		L2Player player = getPlayer();
		if(player != null)
			for(L2Skill s : player.getAllSkills())
				if(s.getId() == _skillId)
					return s;
		return null;
	}

	public void checkCharmOfLuck()
	{
		L2Player player = getPlayer();
		if(player != null)
			for(L2Effect e : player.getEffectList().getAllEffects())
				if(e.getSkill().getId() == _charmOfLuckSkillId || e.getSkill().getId() == _fortuneOfNobleseSkillId)
				{
					_hasCharmOfLuck = true;
					return;
				}

		_hasCharmOfLuck = false;
	}
}