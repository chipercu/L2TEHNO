package npc.model;

import l2open.gameserver.model.L2Character;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.gameserver.serverpackets.SystemMessage;
import l2open.gameserver.templates.L2NpcTemplate;

/**
 * Данный инстанс используется NPC Ballista на осадах фортов
 * @author SYS
 */
public class L2BallistaInstance extends L2NpcInstance
{
	private static final int CLAN_POINTS_REWARD = 30;

	public L2BallistaInstance(int objectId, L2NpcTemplate template)
	{
		super(objectId, template);
	}

	@Override
	public void doDie(L2Character killer)
	{
		if(killer == null || !killer.isPlayer())
			return;

		L2Player player = killer.getPlayer();
		if(player.getClan() == null)
			return;

		player.getClan().incReputation(CLAN_POINTS_REWARD, false, "Ballista " + getTitle());
		player.sendPacket(new SystemMessage(SystemMessage.THE_BALLISTA_HAS_BEEN_SUCCESSFULLY_DESTROYED_AND_THE_CLAN_S_REPUTATION_WILL_BE_INCREASED));
		super.doDie(killer);
	}

	@Override
	public boolean isAutoAttackable(L2Character attacker)
	{
		return true;
	}

	@Override
	public void showChatWindow(L2Player player, int val)
	{}

	@Override
	public boolean isInvul()
	{
		return false;
	}

	@Override
	public boolean isFearImmune()
	{
		return true;
	}
}