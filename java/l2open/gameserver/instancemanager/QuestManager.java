package l2open.gameserver.instancemanager;

import javolution.util.FastMap;
import l2open.gameserver.model.quest.Quest;

import java.util.Collection;

public class QuestManager
{
	private static FastMap<String, Quest> _questsByName = new FastMap<String, Quest>().setShared(true);
	private static FastMap<Integer, Quest> _questsById = new FastMap<Integer, Quest>().setShared(true);

	public static Quest getQuest(String name)
	{
		return _questsByName.get(name);
	}

	public static Quest getQuest(Class<?> quest)
	{
		return getQuest(quest.getSimpleName());
	}

	public static Quest getQuest(int questId)
	{
		if(questId == 255 && l2open.config.ConfigValue.VidakSystem)
			return null;
		return _questsById.get(questId);
	}

	public static Quest getQuest2(String nameOrId)
	{
		if(_questsByName.containsKey(nameOrId))
			return _questsByName.get(nameOrId);
		try
		{
			int questId = Integer.valueOf(nameOrId);
			return _questsById.get(questId);
		}
		catch(Exception e)
		{
			return null;
		}
	}

	public static void addQuest(Quest newQuest)
	{
		_questsByName.put(newQuest.getName(), newQuest);
		_questsById.put(newQuest.getQuestIntId(), newQuest);
	}

	public static Collection<Quest> getQuests()
	{
		return _questsByName.values();
	}

	public static Quest getBecomeALordForCastle(int castle)
	{
		switch(castle)
		{
			case 1:
				return _questsById.get(708);
			case 2:
				return _questsById.get(709);
			case 3:
				return _questsById.get(710);
			case 4:
				return _questsById.get(712);
			case 5:
				return _questsById.get(713);
			case 6:
				return _questsById.get(711);
			case 7:
				return _questsById.get(715);
			case 8:
				return _questsById.get(716);
			case 9:
				return _questsById.get(714);
			default:
				return null;
		}
	}
}