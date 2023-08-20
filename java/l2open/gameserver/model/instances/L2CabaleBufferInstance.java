package l2open.gameserver.model.instances;

import l2open.common.ThreadPoolManager;
import l2open.gameserver.geodata.GeoEngine;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.L2Skill;
import l2open.gameserver.model.L2Summon;
import l2open.gameserver.model.L2World;
import l2open.gameserver.model.entity.SevenSigns;
import l2open.gameserver.serverpackets.MagicSkillUse;
import l2open.gameserver.tables.SkillTable;
import l2open.gameserver.templates.L2NpcTemplate;

import java.util.concurrent.ScheduledFuture;

public class L2CabaleBufferInstance extends L2NpcInstance
{
	ScheduledFuture<?> aiTask;

	class CabalaAI extends l2open.common.RunnableImpl
	{
		L2CabaleBufferInstance _caster;

		CabalaAI(L2CabaleBufferInstance caster)
		{
			_caster = caster;
		}

		public void runImpl()
		{
			int winningCabal = SevenSigns.getInstance().getCabalHighestScore();

			if(winningCabal == SevenSigns.CABAL_NULL)
				return;

			int losingCabal = SevenSigns.CABAL_NULL;

			if(winningCabal == SevenSigns.CABAL_DAWN)
				losingCabal = SevenSigns.CABAL_DUSK;
			else if(winningCabal == SevenSigns.CABAL_DUSK)
				losingCabal = SevenSigns.CABAL_DAWN;

			/**
			 * For each known player in range, cast either the positive or negative buff.
			 * <BR>
			 * The stats affected depend on the player type, either a fighter or a mystic.
			 * <BR>
			 * Curse of Destruction (Loser)
			 *  - Fighters: -25% Accuracy, -25% Effect Resistance
			 *  - Mystics: -25% Casting Speed, -25% Effect Resistance
			 *
			 * Blessing of Prophecy (Winner)
			 *  - Fighters: +25% Max Load, +25% Effect Resistance
			 *  - Mystics: +25% Magic Cancel Resist, +25% Effect Resistance
			 */
			for(L2Player player : L2World.getAroundPlayers(L2CabaleBufferInstance.this, 900, 200))
			{
				if(player == null)
					continue;
				int playerCabal = SevenSigns.getInstance().getPlayerCabal(player);
				if(playerCabal == winningCabal && _caster.getNpcId() == SevenSigns.ORATOR_NPC_ID)
					if(player.isMageClass())
						handleCast(player, 4365);
					else
						handleCast(player, 4364);
				else if(playerCabal == losingCabal && _caster.getNpcId() == SevenSigns.PREACHER_NPC_ID)
					if(player.isMageClass())
						handleCast(player, 4362);
					else
						handleCast(player, 4361);
			}
		}

		private void handleCast(L2Player player, int skillId)
		{
			L2Skill skill = SkillTable.getInstance().getInfo(skillId, 2);
			if(player.getEffectList().getEffectsBySkill(skill) == null && GeoEngine.canAttacTarget(_caster, player, false))
			{
				skill.getEffects(_caster, player, false, false);
				broadcastSkill(new MagicSkillUse(_caster, player, skill.getId(), 2, skill.getHitTime(), 0), true);
			}

			L2Summon summon = player.getPet();
			if(summon != null && summon.getEffectList().getEffectsBySkill(skill) == null && summon.isInRangeZ(_caster, 900) && GeoEngine.canAttacTarget(_caster, summon, false))
			{
				skill.getEffects(_caster, summon, false, false);
				broadcastSkill(new MagicSkillUse(_caster, summon, skill.getId(), 2, skill.getHitTime(), 0), true);
			}
		}
	}

	public L2CabaleBufferInstance(int objectId, L2NpcTemplate template)
	{
		super(objectId, template);
		aiTask = ThreadPoolManager.getInstance().scheduleAtFixedRate(new CabalaAI(this), 3000, 3000);
	}

	@Override
	public void deleteMe()
	{
		if(aiTask != null)
			aiTask.cancel(true);
		super.deleteMe();
	}

	@Override
	public boolean hasRandomAnimation()
	{
		return false;
	}
}