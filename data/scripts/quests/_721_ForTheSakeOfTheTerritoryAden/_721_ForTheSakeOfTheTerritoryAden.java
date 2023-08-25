package quests._721_ForTheSakeOfTheTerritoryAden;

import l2open.extensions.scripts.ScriptFile;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.entity.siege.territory.TerritorySiege;
import l2open.gameserver.model.quest.QuestState;
import quests.Dominion_ForTheSakeOfTerritory;

/**
 * @author pchayka
 */
public class _721_ForTheSakeOfTheTerritoryAden extends Dominion_ForTheSakeOfTerritory implements ScriptFile
{
	public _721_ForTheSakeOfTheTerritoryAden()
	{
		super(721);
		getDominionId = 5;
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
