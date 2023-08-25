package l2open.gameserver.skills.pts_effects;

import l2open.gameserver.ai.*;
import l2open.gameserver.model.L2Effect;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.gameserver.skills.Env;
import l2open.gameserver.skills.effects.EffectTemplate;

/**
 * {p_set_cloak_slot}
 * @p_set_cloak_slot
 **/
/**
 * @author : Diagod
 **/
public class p_set_cloak_slot extends L2Effect
{
	public p_set_cloak_slot(Env env, EffectTemplate template)
	{
		super(env, template);
	}

	@Override
	public void onStart()
	{
		super.onStart();
		//((L2Player)_effected).set_cloakSlotStatus(true);
	}

	@Override
	public void onExit()
	{
		super.onExit();
		//((L2Player)_effected).set_cloakSlotStatus(false);
		/**
		if (((L2PcInstance) activeChar).getInventory().getPaperdollItem(Inventory.PAPERDOLL_CLOAK) != null)
			((L2PcInstance) activeChar).getInventory().setPaperdollItem(Inventory.PAPERDOLL_CLOAK, null);
		**/
	}

	@Override
	public boolean onActionTime()
	{
		return false;
	}
}