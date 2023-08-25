package l2open.gameserver.model.instances;

import l2open.common.ThreadPoolManager;
import l2open.extensions.multilang.CustomMessage;
import l2open.extensions.scripts.Events;
import l2open.gameserver.model.*;
import l2open.gameserver.model.L2ObjectTasks.TrapDestroyTask;
import l2open.gameserver.model.L2Skill.SkillTargetType;
import l2open.gameserver.serverpackets.MyTargetSelected;
import l2open.gameserver.serverpackets.NpcInfo;
import l2open.gameserver.templates.L2NpcTemplate;
import l2open.util.GArray;
import l2open.util.Location;
import l2open.util.reference.*;

import java.util.concurrent.ScheduledFuture;

public final class L2EventTrapInstance extends L2TrapInstance
{
	public L2EventTrapInstance(int objectId, L2NpcTemplate template, L2Character owner, Location loc)
	{
		super(objectId, template, owner, null, loc, true);
	}

	public void detonate(L2Character target)
	{
		if(getOwner() != null)
			getOwner().sendMessage("В вашу ловушку кто-то попался.");
		target.sendMessage("Вы попали в ловушку. Ваше местонахождение известно другим игрокам.");
		destroy();
	}

	public void destroy()
	{
		L2World.removeTerritory(_territory);
		//deleteMe();
		doDie(this);
		if(_destroyTask != null)
			_destroyTask.cancel(false);
		_destroyTask = null;
	}

	public boolean isDetected()
	{
		return false;
	}
}