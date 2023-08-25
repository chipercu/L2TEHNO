package quests._732_ProtectTheReligiousAssociationLeader;

import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.quest.Quest;
import l2open.extensions.scripts.ScriptFile;

/**
 * @author VISTALL
 * @date 8:17/10.06.2011
 */
public class _732_ProtectTheReligiousAssociationLeader extends Quest implements ScriptFile
{
	public _732_ProtectTheReligiousAssociationLeader()
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
