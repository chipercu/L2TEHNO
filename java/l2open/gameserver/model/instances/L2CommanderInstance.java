package l2open.gameserver.model.instances;

import l2open.config.*;
import l2open.gameserver.instancemanager.FortressSiegeManager;
import l2open.gameserver.model.L2Character;
import l2open.gameserver.model.L2DropData;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.base.Experience;
import l2open.gameserver.model.entity.residence.ResidenceType;
import l2open.gameserver.model.entity.siege.fortress.FortressSiege;
import l2open.gameserver.templates.L2NpcTemplate;
import l2open.util.Util;

public class L2CommanderInstance extends L2SiegeGuardInstance
{
	public L2CommanderInstance(int objectId, L2NpcTemplate template)
	{
		super(objectId, template);
	}

	private static final L2DropData EPAULETTE = new L2DropData(9912, 50, 150, 1000000, 1, 85);

	@Override
	public void doDie(L2Character killer)
	{
		FortressSiege siege = FortressSiegeManager.getSiege(this);
		if(siege != null)
		{
			siege.killedCommander(this);

			if(ConfigValue.DropEpauleteOnlyReg)
			{
				if(siege.getSiegeUnit().getType() == ResidenceType.Fortress && killer.isPlayable())
				{
					L2Character topdam = getTopDamager(getAggroList());
					if(topdam == null)
						topdam = killer;

					double chancemod = Experience.penaltyModifier(calculateLevelDiffForDrop(topdam.getLevel(), false), 9);

					dropItem(killer.getPlayer(), EPAULETTE.getItemId(), Util.rollDrop(EPAULETTE.getMinDrop(), EPAULETTE.getMaxDrop(), EPAULETTE.getChance() * chancemod * ConfigValue.RateDropEpaulette * killer.getPlayer().getRateItems(), false, killer.getPlayer()));
				}
			}
		}

		super.doDie(killer);
	}

	@Override
	public boolean can_drop_epaulette(L2Player player)
	{
		FortressSiege siege = FortressSiegeManager.getSiege(this);
		if(siege != null && player != null && siege.getSiegeUnit().getType() == ResidenceType.Fortress)
		{
			//L2Clan clan = player.getClan();
			//if(clan != null && siege == clan.getSiege() && !clan.isDefender())
				return true;
		}
		return ConfigValue.AlwaysDropEpaulette;
	}
}