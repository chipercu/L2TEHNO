package quests._731_ProtectTheMilitaryAssociationLeader;

import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.quest.Quest;
import l2open.extensions.scripts.ScriptFile;

/**
 * @author VISTALL
 * @date 8:17/10.06.2011
 */
public class _731_ProtectTheMilitaryAssociationLeader extends Quest implements ScriptFile
{
	public _731_ProtectTheMilitaryAssociationLeader()
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
