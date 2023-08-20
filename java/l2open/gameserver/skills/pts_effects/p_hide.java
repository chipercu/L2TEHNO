package l2open.gameserver.skills.pts_effects;

import l2open.gameserver.model.L2Effect;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.L2World;
import l2open.gameserver.model.L2WorldRegion;
import l2open.gameserver.serverpackets.MagicSkillUse;
import l2open.gameserver.skills.Env;
import l2open.gameserver.skills.effects.EffectTemplate;


public final class p_hide extends L2Effect
{
	public p_hide(Env env, EffectTemplate template)
	{
		super(env, template);
		//_cancelOnAction = true;
	}

	@Override
	public boolean checkCondition()
	{
		if(!_effected.isPlayer())
			return false;
		L2Player player = (L2Player) _effected;
		if(player.isInvisible())
			return false;
		if(player.isCombatFlagEquipped() || player.isTerritoryFlagEquipped())
			return false;
		return super.checkCondition();
	}

	@Override
	public void onStart()
	{
		super.onStart();
		L2Player player = (L2Player) _effected;
		player.setInvisible(true);
		player.sendUserInfo(true);
		if(player.getCurrentRegion() != null)
			for(L2WorldRegion neighbor : player.getCurrentRegion().getNeighbors())
				neighbor.removePlayerFromOtherPlayers(player);
		player.clearHateList(true);
	}

	@Override
	public void onExit()
	{
		super.onExit();
		L2Player player = (L2Player) _effected;
		if(!player.isInvisible())
			return;
		player.setInvisible(false);
		player.broadcastUserInfo(true);
		if(player.getPet() != null)
			player.getPet().broadcastPetInfo();
		player.broadcastRelationChanged();
	}

	@Override
	public boolean onActionTime()
	{
		return false;
	}
}