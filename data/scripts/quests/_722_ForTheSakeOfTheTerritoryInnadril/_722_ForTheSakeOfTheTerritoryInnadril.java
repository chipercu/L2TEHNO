package quests._722_ForTheSakeOfTheTerritoryInnadril;

import l2open.extensions.scripts.ScriptFile;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.entity.siege.territory.TerritorySiege;
import l2open.gameserver.model.quest.QuestState;
import quests.Dominion_ForTheSakeOfTerritory;

/**
 * @author pchayka
 */
public class _722_ForTheSakeOfTheTerritoryInnadril extends Dominion_ForTheSakeOfTerritory implements ScriptFile
{
	public _722_ForTheSakeOfTheTerritoryInnadril()
	{
		super(722);
		getDominionId = 6;
		TerritorySiege.setForSakeQuest(this, getDominionId);
		L2Player.addBreakQuest(this);
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
