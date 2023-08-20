package quests._729_ProtectTheTerritoryCatapult;

import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.quest.Quest;
import l2open.extensions.scripts.ScriptFile;

/**
 * @author VISTALL
 * @date 2:15/09.06.2011
 */
public class _729_ProtectTheTerritoryCatapult extends Quest implements ScriptFile
{
	public _729_ProtectTheTerritoryCatapult()
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
