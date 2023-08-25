package npc.model;

import java.util.ArrayList;
import java.util.List;

import l2open.gameserver.model.*;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.gameserver.tables.SkillTable;
import l2open.gameserver.templates.L2NpcTemplate;

/**
 * @author pchayka
 */

public final class ElcardiaAssistantInstance extends L2NpcInstance
{
	private final static int[][] _elcardiaBuff = new int[][]{
			// ID, warrior = 0, mage = 1, both = 2
			{6714, 2}, // Wind Walk of Elcadia
			{6715, 0}, // Haste of Elcadia ?
			{6716, 0}, // Might of Elcadia ?
			{6717, 2}, // Berserker Spirit of Elcadia ?
			{6718, 0}, // Death Whisper of Elcadia ?
			{6719, 0}, // Guidance of Elcadia
			{6720, 0}, // Focus of Elcadia
			{6721, 1}, // Empower of Elcadia
			{6722, 1}, // Acumen of Elcadia
			{6723, 1}, // Concentration of Elcadia
			{6727, 0}, // Vampiric Rage of Elcadia ??
			{6729, 2}, // Resist Holy of Elcadia ??

	};

	public ElcardiaAssistantInstance(int objectId, L2NpcTemplate template)
	{
		super(objectId, template);
	}

	@Override
	public void onBypassFeedback(L2Player player, String command)
	{
		if(!canBypassCheck(player, this))
			return;

		if(command.equalsIgnoreCase("request_blessing"))
		{
			for(int[] buff : _elcardiaBuff)
				if(player.isMageClass() && (buff[1] == 1 || buff[1] == 2) || !player.isMageClass() && (buff[1] == 0 || buff[1] == 2))
					doCast(SkillTable.getInstance().getInfo(buff[0], 1), player, true);
		}
		else
			super.onBypassFeedback(player, command);
	}

	@Override
	public void doCast(L2Skill skill, L2Character aimingTarget, boolean forceUse)
	{
		//System.out.println("ElcardiaAssistantInstance doCast: "+skill.getId());
		onMagicUseTimer(aimingTarget, skill, forceUse);
	}
}
