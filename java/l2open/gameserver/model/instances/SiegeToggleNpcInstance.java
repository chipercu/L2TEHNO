package l2open.gameserver.model.instances;

import l2open.gameserver.model.*;
import l2open.gameserver.model.entity.residence.Castle;
import l2open.gameserver.model.entity.siege.territory.TerritorySiege;
import l2open.gameserver.tables.NpcTable;
import l2open.gameserver.templates.L2NpcTemplate;

/**
 * @author VISTALL
 * @date 5:47/07.06.2011
 */
public abstract class SiegeToggleNpcInstance extends L2MonsterInstance // L2MonsterInstance
{
	public SiegeToggleNpcInstance(int objectId, L2NpcTemplate template)
	{
		super(objectId, template);

		hasChatWindow = false;
	}

	public abstract void onDeathImpl(L2Character killer);

	@Override
	public void reduceCurrentHp(double damage, L2Character attacker, L2Skill skill, boolean awake, boolean standUp, boolean directHp, boolean canReflect, boolean isDot, double i2, boolean sendMesseg, boolean bow, boolean crit, boolean tp)
	{
		setCurrentHp(Math.max(getCurrentHp() - damage, 0), false);

		if(getCurrentHp() < 0.5)
		{
			doDie(attacker);

			onDeathImpl(attacker);

			decayMe(); // deleteMe();

			if(36591 > getNpcId())
			{
				L2NpcInstance npc = NpcTable.getTemplate(getNpcId() + 82).getNewInstance();
				npc.hasChatWindow = false;
				npc.setSpawnedLoc(getLoc());
				npc.onSpawn();
				npc.spawnMe(npc.getSpawnedLoc());
			}
		}
	}

	@Override
	public boolean isAutoAttackable(L2Character attacker)
	{
		L2Player player = attacker.getPlayer();
		if(player == null)
			return false;
		if(!TerritorySiege.isInProgress())
			return false;
		Castle castle = getCastle();
		if(player.getTerritorySiege() > -1 && castle != null && player.getTerritorySiege() == castle.getId())
			return false;
		return true;
	}

	@Override
	public boolean isAttackable(L2Character attacker)
	{
		return isAutoAttackable(attacker);
	}

	@Override
	public boolean isInvul()
	{
		return false;
	}

	@Override
	public boolean hasRandomAnimation()
	{
		return false;
	}

	@Override
	public boolean isFearImmune()
	{
		return true;
	}

	@Override
	public boolean isParalyzeImmune()
	{
		return true;
	}

	@Override
	public boolean isLethalImmune()
	{
		return true;
	}

	@Override
	public void onDecay()
	{
		decayMe(); // deleteMe();

		setShowSpawnAnimation(2);
	}
}
