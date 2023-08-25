package l2open.gameserver.model.entity;

import l2open.gameserver.instancemanager.ZoneManager;
import l2open.gameserver.model.*;
import l2open.gameserver.model.base.ClassId;
import l2open.gameserver.tables.SkillTable;
import l2open.extensions.listeners.L2ZoneEnterLeaveListener;

import java.util.HashMap;
import java.util.Map;

public class DragonValley
{
	private ZoneListener _zoneListener = new ZoneListener();
	private static L2Zone zone;
	private static final Map<ClassId, Double> weight = new HashMap<ClassId, Double>();

	private static DragonValley _instance;

	public static DragonValley getInstance()
	{
		if(_instance == null)
			_instance = new DragonValley();
		return _instance;
	}

	public DragonValley()
	{
		zone = ZoneManager.getInstance().getZoneById(L2Zone.ZoneType.dummy, 777333, false);
		zone.getListenerEngine().addMethodInvokedListener(_zoneListener);
	}

	static
	{
		weight.put(ClassId.duelist, 0.2);
		weight.put(ClassId.dreadnought, 0.7);
		weight.put(ClassId.phoenixKnight, 0.5);
		weight.put(ClassId.hellKnight, 0.5);
		weight.put(ClassId.sagittarius, 0.3);
		weight.put(ClassId.adventurer, 0.4);
		weight.put(ClassId.archmage, 0.3);
		weight.put(ClassId.soultaker, 0.3);
		weight.put(ClassId.arcanaLord, 1.);
		weight.put(ClassId.cardinal, -0.6);
		weight.put(ClassId.hierophant, 0.);
		weight.put(ClassId.evaTemplar, 0.8);
		weight.put(ClassId.swordMuse, 0.5);
		weight.put(ClassId.windRider, 0.4);
		weight.put(ClassId.moonlightSentinel, 0.3);
		weight.put(ClassId.mysticMuse, 0.3);
		weight.put(ClassId.elementalMaster, 1.);
		weight.put(ClassId.evaSaint, -0.6);
		weight.put(ClassId.shillienTemplar, 0.8);
		weight.put(ClassId.spectralDancer, 0.5);
		weight.put(ClassId.ghostHunter, 0.4);
		weight.put(ClassId.ghostSentinel, 0.3);
		weight.put(ClassId.stormScreamer, 0.3);
		weight.put(ClassId.spectralMaster, 1.);
		weight.put(ClassId.shillienSaint, -0.6);
		weight.put(ClassId.titan, 0.3);
		weight.put(ClassId.grandKhauatari, 0.2);
		weight.put(ClassId.dominator, 0.1);
		weight.put(ClassId.doomcryer, 0.1);
		weight.put(ClassId.fortuneSeeker, 0.9);
		weight.put(ClassId.maestro, 0.7);
		weight.put(ClassId.doombringer, 0.2);
		weight.put(ClassId.trickster, 0.5);
		weight.put(ClassId.judicator, 0.1);
		weight.put(ClassId.maleSoulhound, 0.3);
		weight.put(ClassId.femaleSoulhound, 0.3);
	}

	public class ZoneListener extends L2ZoneEnterLeaveListener
	{
		@Override
		public void objectEntered(L2Zone zone, L2Object object)
		{
			if(object.isPlayer())
			{
				int level = getBuffLevel(object.getPlayer());
				if(level > 0)
					object.getPlayer().altOnMagicUseTimer(object.getPlayer(), SkillTable.getInstance().getInfo(6885, level));
				object.getPlayer().is_dv=true;
			}
		}

		@Override
		public void objectLeaved(L2Zone zone, L2Object object)
		{
			if(object.isPlayable())
				((L2Character)object).getEffectList().stopEffect(6885);
			object.getPlayer().is_dv=false;
		}
	}

	public void recheckBuff(L2Player player)
	{
		int level = getBuffLevel(player);
		if(level > 0)
			player.altOnMagicUseTimer(player, SkillTable.getInstance().getInfo(6885, level));
		else
			player.getEffectList().stopEffect(6885);
	}

	private int getBuffLevel(L2Player pc)
	{
		if(pc.getParty() == null)
			return 0;
		L2Party party = pc.getParty();
		// Small party check
		if(party.getMemberCount() < 5)	// toCheck
			return 0;
		// Newbie party or Not in zone member check
		for(L2Player p : party.getPartyMembers())
			if(p.getLevel() < 80 || !p.isInZone(zone))
				return 0;

		double points = 0;
		int count = party.getMemberCount();

		for(L2Player p : party.getPartyMembers())
		{
			if(weight.get(p.getClassId()) != null)
				points += weight.get(p.getClassId());
		}

		return (int) Math.max(0, Math.min(3, Math.round(points * getCoefficient(count))));  // Brutally custom
	}

	private double getCoefficient(int count)
	{
		double cf;
		switch(count)
		{
			case 4:
				cf = 0.7;
				break;
			case 5:
				cf = 0.75;
				break;
			case 6:
				cf = 0.8;
				break;
			case 7:
				cf = 0.85;
				break;
			case 8:
				cf = 0.9;
				break;
			case 9:
				cf = 0.95;
				break;
			default:
				cf = 1;
		}
		return cf;
	}
}
