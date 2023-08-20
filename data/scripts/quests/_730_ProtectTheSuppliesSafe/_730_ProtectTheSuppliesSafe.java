package quests._730_ProtectTheSuppliesSafe;

import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.quest.Quest;
import l2open.extensions.scripts.ScriptFile;

/**
 * @author VISTALL
 * @date 8:05/10.06.2011
 */
public class _730_ProtectTheSuppliesSafe extends Quest implements ScriptFile
{
	public _730_ProtectTheSuppliesSafe()
	{
		super(PARTY_NONE);
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
