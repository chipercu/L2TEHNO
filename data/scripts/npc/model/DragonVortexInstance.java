package npc.model;

import l2open.config.ConfigValue;
import l2open.extensions.scripts.Functions;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.gameserver.templates.L2NpcTemplate;
import l2open.util.Location;
import l2open.util.NpcUtils;
import l2open.util.Rnd;

public final class DragonVortexInstance extends L2NpcInstance
{
	//private final int[] bosses = { 25718, 25719, 25720, 25721, 25722, 25723, 25724 };
	private L2NpcInstance boss;
	private long _time = 0;
	private int boss_id = 0;

	public DragonVortexInstance(int objectId, L2NpcTemplate template)
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
			if(ConfigValue.VortexOneBoss && boss != null && !boss.isDead() || _time > System.currentTimeMillis())
			{
				showChatWindow(player, "data/html/default/32871-3.htm");
				return;
			}

			if(Functions.getItemCount(player, 17248) > 0)
			{
				Functions.removeItem(player, 17248, 1);
				_time = System.currentTimeMillis() + ConfigValue.VortexBossTime * 1000;
				int rnd = Rnd.get(100);
				if(rnd <= ConfigValue.VortexBossChance25718)
					boss_id = 25718;
				else if(rnd <= ConfigValue.VortexBossChance25718 + ConfigValue.VortexBossChance25719)
					boss_id = 25719;
				else if(rnd <= ConfigValue.VortexBossChance25718 + ConfigValue.VortexBossChance25719 + ConfigValue.VortexBossChance25720)
					boss_id = 25720;
				else if(rnd <= ConfigValue.VortexBossChance25718 + ConfigValue.VortexBossChance25719 + ConfigValue.VortexBossChance25720 + ConfigValue.VortexBossChance25721)
					boss_id = 25721;
				else if(rnd <= ConfigValue.VortexBossChance25718 + ConfigValue.VortexBossChance25719 + ConfigValue.VortexBossChance25720 + ConfigValue.VortexBossChance25721 + ConfigValue.VortexBossChance25722)
					boss_id = 25722;
				else if(rnd <= ConfigValue.VortexBossChance25718 + ConfigValue.VortexBossChance25719 + ConfigValue.VortexBossChance25720 + ConfigValue.VortexBossChance25721 + ConfigValue.VortexBossChance25722 + ConfigValue.VortexBossChance25723)
					boss_id = 25723;
				else
					boss_id = 25724;
				boss = NpcUtils.spawnSingle(boss_id, Location.coordsRandomize(getLoc(), 300, 600), getReflectionId(), 3600000);
				if(ConfigValue.DragonVortexRBDrop2Summoner)
					boss.c_ai3 = player;
				showChatWindow(player, "data/html/default/32871-1.htm");
			}
			else
				showChatWindow(player, "data/html/default/32871-2.htm");
		}
		else
			super.onBypassFeedback(player, command);
	}
}