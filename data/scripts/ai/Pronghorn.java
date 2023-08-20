package ai;

import l2open.gameserver.ai.CtrlEvent;
import l2open.gameserver.ai.Fighter;
import l2open.gameserver.geodata.GeoEngine;
import l2open.gameserver.model.L2Character;
import l2open.gameserver.model.L2Skill;
import l2open.gameserver.model.L2Spawn;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.gameserver.tables.NpcTable;
import l2open.util.Location;
import l2open.util.Rnd;

/**
 * AI моба Pronghorn для Frozen Labyrinth.<br>
 * - Если был атакован физическим скилом, спавнится миньон-мобы Pronghorn Spirit 22087 в количестве 4 штук.<br>
 * - Не используют функцию Random Walk, если были заспавнены "миньоны"<br>
 * @author SYS
 */
public class Pronghorn extends Fighter
{
	private boolean _mobsNotSpawned = true;
	private static final int MOBS = 22087;
	private static final int MOBS_COUNT = 4;

	public Pronghorn(L2Character actor)
	{
		super(actor);
	}

	@Override
	protected void onEvtSeeSpell(L2Skill skill, L2Character caster)
	{
		L2NpcInstance actor = getActor();
		if(actor == null || skill.isMagic())
			return;
		if(_mobsNotSpawned)
		{
			_mobsNotSpawned = false;
			for(int i = 0; i < MOBS_COUNT; i++)
				try
				{
					Location pos = GeoEngine.findPointToStay(actor.getX(), actor.getY(), actor.getZ(), 100, 120, actor.getReflection().getGeoIndex());
					L2Spawn sp = new L2Spawn(NpcTable.getTemplate(MOBS));
					sp.setLoc(pos);
					L2NpcInstance npc = sp.doSpawn(true);
					if(caster.isPet() || caster.isSummon())
						npc.getAI().notifyEvent(CtrlEvent.EVT_AGGRESSION, caster, Rnd.get(2, 100));
					npc.getAI().notifyEvent(CtrlEvent.EVT_AGGRESSION, caster.getPlayer(), Rnd.get(1, 100));
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}
		}
	}

	@Override
	protected void MY_DYING(L2Character killer)
	{
		_mobsNotSpawned = true;
		super.MY_DYING(killer);
	}

	@Override
	protected boolean randomWalk()
	{
		return _mobsNotSpawned;
	}
}