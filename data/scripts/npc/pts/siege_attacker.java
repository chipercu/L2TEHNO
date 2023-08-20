package npc.pts;

import l2open.gameserver.model.*;
import l2open.gameserver.model.instances.*;
import l2open.gameserver.instancemanager.SiegeManager;
import l2open.gameserver.templates.*;

public class siege_attacker extends L2MonsterInstance
{
	public siege_attacker(int objectId, L2NpcTemplate template)
	{
		super(objectId, template);
	}

	@Override
	public boolean isAttackable(L2Character attacker)
	{
		return isAutoAttackable(attacker);
	}

	@Override
	public boolean isAutoAttackable(L2Character attacker)
	{
		L2Player player = attacker.getPlayer();
		if(player == null)
			return false;
		L2Clan clan = player.getClan();
		if(clan != null && SiegeManager.getSiege(this, true) == clan.getSiege() && clan.isDefender())
			return true;
		return false;
	}

	@Override
	public boolean hasRandomAnimation()
	{
		return false;
	}
}
