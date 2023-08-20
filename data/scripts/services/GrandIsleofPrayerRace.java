package services;

import l2open.extensions.scripts.Functions;
import l2open.extensions.scripts.ScriptFile;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.L2Skill;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.gameserver.tables.SkillTable;
import l2open.util.Files;

/**
 * Сервис для гонок на Isle of Prayer, см. также ai.PrisonGuard 
 * @author SYS
 */
public class GrandIsleofPrayerRace extends Functions implements ScriptFile
{
	private static final int RACE_STAMP = 10013;
	private static final int SECRET_KEY = 9694;

	public void onLoad()
	{
		_log.info("Loaded Service: Grand Isle of Prayer Race");
	}

	public void startRace()
	{
		L2Skill skill = SkillTable.getInstance().getInfo(L2Skill.SKILL_EVENT_TIMER, 1);
		L2Player player = (L2Player) getSelf();
		L2NpcInstance npc = getNpc();
		if(skill == null || player == null || npc == null)
			return;

		getNpc().altUseSkill(skill, player);
		removeItem(player, RACE_STAMP, getItemCount(player, RACE_STAMP));
		show(Files.read("data/html/defautl/32349-2.htm", player), player, npc);
	}

	public String DialogAppend_32349(Integer val)
	{
		L2Player player = (L2Player) getSelf();
		if(player == null)
			return "";

		// Нет бафа с таймером
		if(player.getEffectList().getEffectsBySkillId(L2Skill.SKILL_EVENT_TIMER) == null)
			return "<br>[scripts_services.GrandIsleofPrayerRace:startRace|Start the Race.]";

		// Есть бафф с таймером
		long raceStampsCount = getItemCount(player, RACE_STAMP);
		if(raceStampsCount < 4)
			return "<br>*Race in progress, hurry!*";
		removeItem(player, RACE_STAMP, raceStampsCount);
		addItem(player, SECRET_KEY, 3);
		player.getEffectList().stopEffect(L2Skill.SKILL_EVENT_TIMER);
		return "<br>Good job, here is your keys.";
	}

	public void onReload()
	{}

	public void onShutdown()
	{}
}