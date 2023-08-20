package npc.model;

import l2open.gameserver.idfactory.IdFactory;
import l2open.gameserver.model.L2Character;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.L2Skill;
import l2open.gameserver.model.Reflection;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.gameserver.model.instances.L2ReflectionBossInstance;
import l2open.gameserver.tables.NpcTable;
import l2open.gameserver.templates.L2NpcTemplate;
import l2open.util.Location;

public class LostCaptainInstance extends L2ReflectionBossInstance
{
	private static final int TELE_DEVICE_ID = 4314;

	public LostCaptainInstance(int objectId, L2NpcTemplate template)
	{
		super(objectId, template);
	}

	@Override
	public void doDie(L2Character killer)
	{
		super.doDie(killer);
		L2NpcInstance npc = new L2NpcInstance(IdFactory.getInstance().getNextId(), NpcTable.getTemplate(TELE_DEVICE_ID));
		npc.setSpawnedLoc(new Location(12056, -174888, -9944));
		npc.setReflection(getReflection());
		npc.spawnMe(npc.getSpawnedLoc());
		if(killer != null && killer.isPlayable())
		{
			Reflection ref = killer.getPlayer().getReflection();
			if(ref != null)
			{
				for(L2Player p : ref.getPlayers())
					p.setVarInst(ref.getName(), String.valueOf(System.currentTimeMillis()));
			}
		}
	}
}
