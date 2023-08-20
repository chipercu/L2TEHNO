package l2open.gameserver.model.instances;

import l2open.config.ConfigValue;
import l2open.gameserver.ai.CtrlIntention;
import l2open.gameserver.model.L2Character;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.L2Skill;
import l2open.gameserver.serverpackets.MagicSkillUse;
import l2open.gameserver.serverpackets.MyTargetSelected;
import l2open.gameserver.serverpackets.ValidateLocation;
import l2open.gameserver.tables.SkillTable;
import l2open.gameserver.templates.L2NpcTemplate;
import l2open.util.GArray;

import java.io.File;
import java.util.StringTokenizer;

public class L2OlympiadBufferInstance extends L2NpcInstance
{
	private int buffers_count = 0;

	public L2OlympiadBufferInstance(int objectId, L2NpcTemplate template)
	{
		super(objectId, template);
	}

	@Override
	public void onAction(L2Player player, boolean shift, int addDist)
	{
		if(this != player.getTarget())
		{
			player.setTarget(this);
		}
		else
		{
			MyTargetSelected my = new MyTargetSelected(getObjectId(), player.getLevel() - getLevel());
			player.sendPacket(my);
			if(!isInRange(player, 100+addDist))
				player.getAI().setIntention(CtrlIntention.AI_INTENTION_INTERACT, this, 100);
			else if(buffers_count > 4)
				showChatWindow(player, 1);
			else
				showChatWindow(player, 0);
			player.sendActionFailed();
		}
	}

	@Override
	public void onBypassFeedback(L2Player player, String command)
	{
		if(!canBypassCheck(player, this))
			return;

		if(buffers_count > 4)
			showChatWindow(player, 1);

		if(command.startsWith("Buff"))
		{
			int id = 0;
			int lvl = 0;
			StringTokenizer st = new StringTokenizer(command, " ");
			st.nextToken();
			id = Integer.parseInt(st.nextToken());
			lvl = Integer.parseInt(st.nextToken());
			L2Skill skill = SkillTable.getInstance().getInfo(id, lvl);
			GArray<L2Character> target = new GArray<L2Character>();
			target.add(player);
			broadcastSkill(new MagicSkillUse(this, player, id, lvl, 0, 0));
			callSkill(skill, target, true);
			buffers_count++;
			if(buffers_count > 4)
				showChatWindow(player, 1);
			else
				showChatWindow(player, 0);
		}
		else
			showChatWindow(player, 0);
	}

	@Override
	public String getHtmlPath(int npcId, int val)
	{
		String pom;
		if(val == 0)
			pom = "buffer";
		else
			pom = "buffer-" + val;
		String temp = "/data/html/olympiad/" + pom + ".htm";
		File mainText = new File(temp);
		if(mainText.exists())
			return temp;

		// If the file is not found, the standard message "I have nothing to say to you" is returned
		return "/data/html/npcdefault.htm";
	}

	@Override
	public boolean isAttackable(L2Character attacker)
	{
		return ConfigValue.CanAttackOlyNpc;
	}
}