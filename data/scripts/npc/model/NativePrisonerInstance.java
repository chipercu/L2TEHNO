package npc.model;

import l2open.extensions.scripts.Functions;
import l2open.gameserver.instancemanager.HellboundManager;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.gameserver.skills.AbnormalVisualEffect;
import l2open.gameserver.taskmanager.DecayTaskManager;
import l2open.gameserver.templates.L2NpcTemplate;

import java.util.StringTokenizer;

/**
 * Данный инстанс используется в городе-инстансе на Hellbound
 * @author SYS
 */
public final class NativePrisonerInstance extends L2NpcInstance
{
	public NativePrisonerInstance(int objectId, L2NpcTemplate template)
	{
		super(objectId, template);
	}

	@Override
	public void onSpawn()
	{
		startAbnormalEffect(AbnormalVisualEffect.ave_paralyze);
		super.onSpawn();
	}

	@Override
	public void onBypassFeedback(L2Player player, String command)
	{
		if(!canBypassCheck(player, this) || isBusy())
			return;

		StringTokenizer st = new StringTokenizer(command);
		if(st.nextToken().equals("rescue"))
		{
			stopAbnormalEffect(AbnormalVisualEffect.ave_paralyze);
			Functions.npcSay(this, "Thank you for saving me! Guards are coming, run!");
			HellboundManager.getInstance().addPoints(20);
			DecayTaskManager.getInstance().addDecayTask(this);
			setBusy(true);
		}
		else
			super.onBypassFeedback(player, command);
	}
}