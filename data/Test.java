import l2open.extensions.scripts.Functions;
import l2open.extensions.scripts.ScriptFile;
import l2open.gameserver.cache.Msg;
import l2open.gameserver.instancemanager.QuestManager;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.quest.Quest;
import l2open.gameserver.model.quest.QuestState;

public class Test extends Functions implements ScriptFile
{
	private static int item_id = 4037; // Какой итем забирать.
	private static int item_count = 1; // сколько забирать
	
	private static int item_id_add = 4037; // Какой итем выдать.
	private static int item_count_add = 1; // сколько выдать

	public Test()
	{}

	public void buy()
	{
		L2Player player = (L2Player) getSelf();
		if(player == null)
			return;
		if(Functions.getItemCount(player, item_id) < item_count)
		{
			player.sendPacket(Msg.YOU_DO_NOT_HAVE_ENOUGH_REQUIRED_ITEMS);
			return;
		}

		QuestState hostQuest = player.getQuestState("_242_PossessorOfaPreciousSoul2");
		if(hostQuest == null)
		{
			Quest q = QuestManager.getQuest(242);
			if(q != null)
				q.newQuestState(player, Quest.COMPLETED);
			Functions.removeItem(player, item_id, item_count);
			Functions.addItem(player, item_id_add, item_count_add);
		}
		else if(hostQuest != null && hostQuest.getState() != Quest.COMPLETED)
		{
			hostQuest.setState(Quest.COMPLETED);
			Functions.removeItem(player, item_id, item_count);
			Functions.addItem(player, item_id_add, item_count_add);
		}
		
	}

	public void onLoad()
	{
	}

	public void onReload()
	{
	}

	public void onShutdown()
	{
	}
}