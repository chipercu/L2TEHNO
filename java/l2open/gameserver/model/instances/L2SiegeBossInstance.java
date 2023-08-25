package l2open.gameserver.model.instances;

import javolution.util.FastMap;
import l2open.gameserver.instancemanager.ClanHallSiegeManager;
import l2open.gameserver.model.L2Character;
import l2open.gameserver.model.L2Clan;
import l2open.gameserver.model.L2Playable;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.entity.siege.clanhall.ClanHallSiege;
import l2open.gameserver.templates.L2NpcTemplate;

public class L2SiegeBossInstance extends L2SiegeGuardInstance
{
	public L2SiegeBossInstance(int objectId, L2NpcTemplate template)
	{
		super(objectId, template);
	}

	@Override
	public void doDie(L2Character killer)
	{
		ClanHallSiege siege = ClanHallSiegeManager.getSiege(this);
		if(siege != null)
			siege.killedSiegeBoss(this);
		deleteMe();
	}

	public L2Clan getWinner()
	{
		ClanHallSiege siege = ClanHallSiegeManager.getSiege(this);
		if(siege == null)
			return null;
		int mostdamage = 0;
		L2Player player = null;
		L2Player temp = null;
		FastMap<L2Player, Integer> damagemap = new FastMap<L2Player, Integer>().setShared(true);
		for(AggroInfo info : getAggroList())
		{
			L2Playable killer = info.attacker;
			int damage = info.damage;
			if(killer.isPet() || killer.isSummon())
				temp = killer.getPlayer();
			else if(killer.isPlayer())
				temp = (L2Player) killer;

			if(temp == null || temp.getClan() == null)
				continue;
			if(siege.getAttackerClan(temp.getClan()) == null)
				continue;

			if(!damagemap.containsKey(temp))
				damagemap.put(temp, damage);
			else
			{
				int dmg = damagemap.get(temp) + damage;
				damagemap.put(temp, dmg);
			}
		}

		for(L2Player pl : damagemap.keySet())
		{
			int damage = damagemap.get(pl);
			if(damage > mostdamage)
			{
				mostdamage = damage;
				player = pl;
			}
		}

		return player == null ? null : player.getClan();
	}
}