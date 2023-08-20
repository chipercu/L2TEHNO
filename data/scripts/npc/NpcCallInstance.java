package npc.model;

import l2open.config.ConfigValue;
import l2open.extensions.scripts.Functions;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.gameserver.templates.L2NpcTemplate;
import l2open.util.Location;
import l2open.util.NpcUtils;
import l2open.util.Rnd;

/**
7) Вызов РБ По типу как с вортексов 7рб
	Айди НПЦ с которого вызывать - id 50013 50014 50015
	Итем для призыва РБ - id 24019
	Айди РБ - id 50007 50008 50009 50010
	Конфиг на шанс:
	# Отдельный шанс спауна каждого боса для новых РБ. Шанс не должен привышать 100%!!!!!!!
	VortexBossChance50007 = 8.25
	VortexBossChance50008 = 5.25
	VortexBossChance50009 = 10.25
	VortexBossChance50010 = 10.25

**/
public final class NpcCallInstance extends L2NpcInstance
{
	//private final int[] bosses = { 25718, 25719, 25720, 25721, 25722, 25723, 25724 };
	private L2NpcInstance boss;
	private long _time = 0;
	private int boss_id = 0;

	public NpcCallInstance(int objectId, L2NpcTemplate template)
	{
		super(objectId, template);
	}

	@Override
	public void onBypassFeedback(L2Player player, String command)
	{
		if(!canBypassCheck(player, this))
			return;

		if(command.startsWith("request_boss"))
		{
			if(ConfigValue.NpcCallOneBoss && boss != null && !boss.isDead() || _time > System.currentTimeMillis())
			{
				showChatWindow(player, "data/html/default/50013-3.htm");
				return;
			}

			if(Functions.getItemCount(player, 24019) > 0)
			{
				Functions.removeItem(player, 24019, 1);
				_time = System.currentTimeMillis() + ConfigValue.NpcCallBossTime * 1000;
				int rnd = Rnd.get(100);
				if(rnd <= ConfigValue.NpcCallChance50007)
					boss_id = 50007;
				else if(rnd <= ConfigValue.NpcCallChance50007 + ConfigValue.NpcCallChance50008)
					boss_id = 50008;
				else if(rnd <= ConfigValue.NpcCallChance50007 + ConfigValue.NpcCallChance50008 + ConfigValue.NpcCallChance50009)
					boss_id = 50009;
				else
					boss_id = 50010;
				boss = NpcUtils.spawnSingle(boss_id, Location.coordsRandomize(getLoc(), 300, 600), getReflectionId(), 3600000);
				//if(ConfigValue.DragonVortexRBDrop2Summoner)
				//	boss.c_ai3 = player;
				showChatWindow(player, "data/html/default/50013-1.htm");
			}
			else
				showChatWindow(player, "data/html/default/50013-2.htm");
		}
		else
			super.onBypassFeedback(player, command);
	}
}