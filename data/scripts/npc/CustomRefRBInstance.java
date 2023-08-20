package npc.model;

import l2open.config.ConfigValue;
import l2open.gameserver.instancemanager.QuestManager;
import l2open.gameserver.model.*;
import l2open.gameserver.model.instances.L2NpcInstance.AggroInfo;
import l2open.gameserver.model.instances.L2RaidBossInstance;
import l2open.gameserver.model.quest.Quest;
import l2open.gameserver.model.quest.QuestState;
import l2open.gameserver.templates.L2NpcTemplate;
import l2open.util.*;

public class CustomRefRBInstance extends L2RaidBossInstance
{
	public CustomRefRBInstance(int objectId, L2NpcTemplate template)
	{
		super(objectId, template);
	}

	@Override
	public void doDie(L2Character killer)
	{
		killer.getPlayer().setVarInst(getReflection().getName(), String.valueOf(System.currentTimeMillis()));

		unspawnMinions();

		super.doDie(killer);

		clearReflection();
	}

	/**
	 * Удаляет все спауны из рефлекшена и запускает 5ти минутный коллапс-таймер.
	 */
	protected void clearReflection()
	{
		getReflection().clearReflection(5, true);
	}

	@Override
	public void unspawnMinions()
	{
		removeMinions();
	}

	@Override
	public boolean isRaid()
	{
		return false;
	}

	@Override
	public boolean isRefRaid()
	{
		return true;
	}

	@Override
	public boolean isLethalImmune()
	{
		return true;
	}
}