package l2open.gameserver.taskmanager.tasks;

import l2open.config.ConfigValue;
import l2open.gameserver.model.*;
import l2open.gameserver.model.entity.Hero;
import l2open.gameserver.model.entity.residence.Castle;
import l2open.gameserver.instancemanager.CastleManager;
import l2open.gameserver.instancemanager.ServerVariables;
import l2open.gameserver.tables.ClanTable;
import l2open.gameserver.tables.player.PlayerData;
import l2open.gameserver.taskmanager.Task;
import l2open.gameserver.taskmanager.TaskManager;
import l2open.gameserver.taskmanager.TaskManager.ExecutedTask;
import l2open.gameserver.taskmanager.TaskTypes;

import java.util.logging.Logger;

/**
1. Если клан имеет замок - 2 очка каждые 3 часа
2. Если клан имеет флаги - 1 очко каждые 3 часа ( за каждый флаг, если флагов 5 например то получает 5 очков в 3 часа )
3. В Клане есть Герой, каждые 6 часов - 2 очка за ( каждого героя , если их 5 то получается 10 очков в 6 часов )
4. Клан убивает Баюма - 10 очков
5. Клан убивает Антараса - 15 очков
6. Клан убивает Валакаса - 15 очков
7. Клан убивает Закена (85 lvl ) - 1 очко
8. Клан убивает Фринтеззу - 2 очка
9. Клан убивает Фрею обычную - 1 очко
10. Клан убивает Фрею высшую - 2 очка
11. Если игрок выходит из клана - ( -2 очка ) Если у клана 0 очков то будет писать -2 очка ( то есть чтобы у них было 1 очков нужно собрать +3 очка )
12. Если игрок входит в клан - ( -1 очко ) Если у клана 0 очков то будет писать -1 очка ( то есть чтобы у них было 1 очков нужно собрать +2 очка )
13. При создании клана даётся 20 очков.
--------------------------------------
- Возможность в админке повышать или понижать очки клану.
- Топ 10 кланов по очкам. ( в альт Б )
- В систем чате клан лидеру или всему клану пишет например: ( получено 15 очков за убийство Валакаса )
- Команду клан лидеру .points чтобы он видел сколько в данный момент у клана очков.
**/
public class TaskClanPoint extends Task
{
	private static final Logger _log = Logger.getLogger(TaskClanPoint.class.getName());
	public static final String NAME = "clan_point";

	@Override
	public String getName()
	{
		return NAME;
	}

	@Override
	public void onTimeElapsed(ExecutedTask task)
	{
		if(!ConfigValue.EnableClanPoint)
			return;
		int time1 = ServerVariables.getInt("last_update_time_1", 0);
		int time2 = ServerVariables.getInt("last_update_time_2", 0);
		int time3 = ServerVariables.getInt("last_update_time_3", 0);
		int current_time = (int)(System.currentTimeMillis()/1000+180);

		boolean can_update1=current_time - time1 > ConfigValue.ClanPointTime1*60*60;
		boolean can_update2=current_time - time2 > ConfigValue.ClanPointTime2*60*60;
		boolean can_update3=current_time - time3 > ConfigValue.ClanPointTime3*60*60;

		for(L2Clan cl : ClanTable.getInstance().getClans())
		{
			boolean update = false;
			if(cl != null && can_update1 && cl.getHasCastle() > 0 && ConfigValue.ClanPointCount1 > 0)
			{
				update = true;
				cl.clan_point += ConfigValue.ClanPointCount1;
				cl.sendMessageToAll("Начислено "+ConfigValue.ClanPointCount1+" очков рейтинга ( Замок ).");
			}
			if(cl != null && can_update2 && cl.getHasCastle() > 0 && ConfigValue.ClanPointCount2 > 0)
			{
				Castle c = CastleManager.getInstance().getCastleByOwner(cl);
				if(c != null)
				{
					update = true;
					cl.clan_point += ConfigValue.ClanPointCount2*c.getFlags().length;
					cl.sendMessageToAll("Начислено "+ConfigValue.ClanPointCount2*c.getFlags().length+" очков рейтинга ( Флаги ).");
				}
			}
			if(cl != null && can_update3 && ConfigValue.ClanPointCount3 > 0)
				for(L2ClanMember member : cl.getMembers())
					if(Hero.getInstance().isHero(member.getObjectId()))
					{
						update = true;
						cl.clan_point += ConfigValue.ClanPointCount3;
						cl.sendMessageToAll("Начислено "+ConfigValue.ClanPointCount3+" очков рейтинга ( Герои ).");
					}
			if(update)
				PlayerData.getInstance().updateClanInDB(cl);
		}
		if(can_update1)
			ServerVariables.set("last_update_time_1", System.currentTimeMillis()/1000);
		if(can_update2)
			ServerVariables.set("last_update_time_2", System.currentTimeMillis()/1000);
		if(can_update3)
			ServerVariables.set("last_update_time_3", System.currentTimeMillis()/1000);
	}

	@Override
	public void initializate()
	{
		TaskManager.addUniqueTask(NAME, TaskTypes.TYPE_EVERY_HOUR, "", "", "");
	}
}
