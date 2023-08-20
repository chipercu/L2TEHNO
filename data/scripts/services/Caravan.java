package services;

import l2open.extensions.scripts.Functions;
import l2open.extensions.scripts.ScriptFile;
import l2open.gameserver.instancemanager.HellboundManager;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.util.Location;

import java.util.List;

/**
 * Используется для Hellbound
 */
public class Caravan extends Functions implements ScriptFile
{
	public void onLoad()
	{
		_log.info("Loaded Service: Tower");
	}

	public void onReload()
	{}

	public void onShutdown()
	{}

	private static final int FieryDemonBloodSkill = 2357;
	private static Location TowerofInfinitumLocationPoint = new Location(-22204, 277056, -15045);
	private static Location TullyEntranceLocationPoint = new Location(17947, 283205, -9696);
	private static Location TullyFloor1LocationPoint = new Location(-13400, 272827, -15304);


	public void enterToInfinitumTower()
	{
		L2Player p = (L2Player) getSelf();
		L2NpcInstance n = getNpc();

		if(HellboundManager.getInstance().getLevel() >= 11)
		{
			// Нету партии или не лидер партии
			if(p.getParty() == null || !p.getParty().isLeader(p))
			{
				n.onBypassFeedback(p, "Chat 1");
				return;
			}

			List<L2Player> members = p.getParty().getPartyMembers();

			// Далеко или нету эффекта херба Fiery Demon Blood
			for(L2Player member : members)
				if(member == null || !L2NpcInstance.canBypassCheck(member, n) || member.getEffectList().getEffectsBySkillId(FieryDemonBloodSkill) == null || member.isInOlympiadMode())
				{
					n.onBypassFeedback(p, "Chat 2");
					return;
				}


			// Телепортируем партию на 1 этаж Tower of Infinitum
			for(L2Player member : members)
				member.teleToLocation(TowerofInfinitumLocationPoint);
		}
		else
			n.onBypassFeedback(p, "Chat 3");
	}

	public void enterToTullyEntrance()
	{
		L2Player p = (L2Player) getSelf();
		L2NpcInstance n = getNpc();

		if(!L2NpcInstance.canBypassCheck(p, n))
			return;
		if(p.isInOlympiadMode())
		{
			p.sendMessage("Во время Олимпиады нельзя использовать данную функцию.");
			return;
		}

		// Телепортируем чара в предбанник Tully's Workshop
		if(p.isQuestCompleted("_132_MatrasCuriosity"))
			p.teleToLocation(TullyEntranceLocationPoint);
		else
			n.onBypassFeedback(p, "Chat 1");
	}

	public void enterToTullyFloor1()
	{
		L2Player p = (L2Player) getSelf();
		L2NpcInstance n = getNpc();

		if(!L2NpcInstance.canBypassCheck(p, n))
			return;

		// Нету партии или не лидер партии
		if(p.getParty() == null || !p.getParty().isLeader(p))
		{
			n.onBypassFeedback(p, "Chat 2");
			return;
		}

		List<L2Player> members = p.getParty().getPartyMembers();

		// Далеко или не выполнен 132 квест
		for(L2Player member : members)
			if(member == null || !L2NpcInstance.canBypassCheck(member, n)/* || !member.isQuestCompleted("_132_MatrasCuriosity")*/ || member.isInOlympiadMode())
			{
				n.onBypassFeedback(p, "Chat 1");
				return;
			}

		// Телепортируем партию на 1 этаж Tully's Workshop
		for(L2Player member : members)
			member.teleToLocation(TullyFloor1LocationPoint);
	}
}