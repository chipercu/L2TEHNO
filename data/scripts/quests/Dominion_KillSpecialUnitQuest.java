package quests;

import org.apache.commons.lang3.ArrayUtils;
import l2open.extensions.scripts.ScriptFile;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.base.ClassId;
import l2open.gameserver.model.entity.siege.territory.TerritorySiege;
import l2open.gameserver.model.quest.Quest;
import l2open.gameserver.model.quest.QuestState;
import l2open.gameserver.serverpackets.ExShowScreenMessage;
import l2open.util.Rnd;

/**
 * @author VISTALL
 * @date 15:51/12.04.2011
 */
public abstract class Dominion_KillSpecialUnitQuest extends Quest implements ScriptFile
{
	private final ClassId[] _classIds;

	public Dominion_KillSpecialUnitQuest(int id)
	{
		super(PARTY_ALL, id);

		_classIds = getTargetClassIds();
		for(ClassId c : _classIds)
			L2Player.addClassQuest(c, this);
	}

	protected abstract int startNpcString();

	protected abstract int progressNpcString();

	protected abstract int doneNpcString();

	protected abstract int getRandomMin();

	protected abstract int getRandomMax();

	protected abstract ClassId[] getTargetClassIds();

	@Override
	public String onPlayerKill(L2Player killed, QuestState qs)
	{
		L2Player player = qs.getPlayer();
		if(player == null)
			return null;
		int event1 = player.getTerritorySiege();
		if(event1 == -1)
			return null;
		int event2 = killed.getTerritorySiege();
		if(event2 == -1 || event2 == event1)
			return null;

		if(!ArrayUtils.contains(_classIds, killed.getClassId()))
			return null;

		int max_kills = qs.getInt("max_kills");
		if(max_kills == 0)
		{
			qs.setState(STARTED);
			qs.setCond(1);

			max_kills = Rnd.get(getRandomMin(), getRandomMax());
			qs.set("max_kills", max_kills);
			qs.set("current_kills", 1);

			player.sendPacket(new ExShowScreenMessage(startNpcString(), 2000, ExShowScreenMessage.ScreenMessageAlign.TOP_CENTER, true, false, String.valueOf(max_kills)));
		}
		else
		{
			int current_kills = qs.getInt("current_kills") + 1;
			if(current_kills >= max_kills)
			{
				TerritorySiege.addReward(player, TerritorySiege.STATIC_BADGES, 10, event1);

				qs.setState(COMPLETED);
				IncrementParam(qs);
				qs.unset("max_kills");
				qs.unset("current_kills");
				qs.exitCurrentQuest(false);

				player.sendPacket(new ExShowScreenMessage(doneNpcString(), 2000, ExShowScreenMessage.ScreenMessageAlign.TOP_CENTER, true, false));
			}
			else
			{
				qs.set("current_kills", current_kills);
				player.sendPacket(new ExShowScreenMessage(progressNpcString(), 2000, ExShowScreenMessage.ScreenMessageAlign.TOP_CENTER, true, false, String.valueOf(max_kills), String.valueOf(current_kills)));
			}
		}
		return null;
	}

	// PTS скрипты рулят....
	private static void IncrementParam(QuestState c1)
	{
		if(c1.getPlayer().getLevel() >= 85)
			c1.addExpAndSp(587000,59000);
		else if(c1.getPlayer().getLevel() >= 84)
			c1.addExpAndSp(582000,59000);
		else if(c1.getPlayer().getLevel() >= 83)
			c1.addExpAndSp(576000,58000);
		else if(c1.getPlayer().getLevel() >= 82)
			c1.addExpAndSp(570000,58000);
		else if(c1.getPlayer().getLevel() >= 81)
			c1.addExpAndSp(565000,57000);
		else if(c1.getPlayer().getLevel() >= 80)
			c1.addExpAndSp(559000,57000);
		else if(c1.getPlayer().getLevel() >= 79)
			c1.addExpAndSp(555000,56000);
		else if(c1.getPlayer().getLevel() >= 78)
			c1.addExpAndSp(551000,56000);
		else if(c1.getPlayer().getLevel() >= 77)
			c1.addExpAndSp(548000,55000);
		else if(c1.getPlayer().getLevel() >= 76)
			c1.addExpAndSp(545000,55000);
		else if(c1.getPlayer().getLevel() >= 75)
			c1.addExpAndSp(543000,54000);
		else if(c1.getPlayer().getLevel() >= 70)
			c1.addExpAndSp(534000,51000);
		else if(c1.getPlayer().getLevel() >= 61)
			c1.addExpAndSp(505000,45000);
		else if(c1.getPlayer().getLevel() >= 55)
			c1.addExpAndSp(462000,38000);
		else if(c1.getPlayer().getLevel() >= 50)
			c1.addExpAndSp(413000,32000);
		else if(c1.getPlayer().getLevel() >= 45)
			c1.addExpAndSp(358000,26000);
		else
			c1.addExpAndSp(301000,20000);
	}

	@Override
	public boolean canAbortByPacket()
	{
		return false;
	}

	@Override
	public void onLoad()
	{
	}

	@Override
	public void onReload()
	{
	}

	@Override
	public void onShutdown()
	{
	}
}
