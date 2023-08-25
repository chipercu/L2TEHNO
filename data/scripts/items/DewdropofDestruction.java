package items;

import l2open.extensions.scripts.ScriptFile;
import l2open.gameserver.handler.IItemHandler;
import l2open.gameserver.handler.ItemHandler;
import l2open.gameserver.model.L2Character;
import l2open.gameserver.model.L2Playable;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.L2Skill;
import l2open.gameserver.model.items.L2ItemInstance;
import l2open.gameserver.serverpackets.SystemMessage;
import l2open.gameserver.tables.SkillTable;

/**
 * @author: Drizzy
 * @date: 03.02.2011
 * @Handler for Dewdrop Of Destruction. Use him on kill picture on frintezza instance.
 */
 
public class DewdropofDestruction implements IItemHandler, ScriptFile
{
	//Item Id
	private static final int[] _itemIds = { 8556 };
	
	public void useItem(L2Playable playable, L2ItemInstance item, Boolean ctrl)
	{
		L2Player p = (L2Player) playable;
		L2Character targetNPC = (L2Character) p.getTarget();

		if(playable == null || !playable.isPlayer())
			return;

		if(targetNPC == null)
		{
			p.sendPacket(new SystemMessage(SystemMessage.THAT_IS_THE_INCORRECT_TARGET));
			return;
		}
		if(p.getTarget().isPlayable())
		{
			p.sendPacket(new SystemMessage(SystemMessage.THAT_IS_THE_INCORRECT_TARGET));
			return;
		}

		int npcId = targetNPC.getNpcId();	

        if(npcId == 29048 || npcId == 29049)
        {
            L2Skill skill = SkillTable.getInstance().getInfo(2276, 1);
			p.doCast(skill, (L2Character) p.getTarget(), false);
            L2Character target = (L2Character) p.getTarget();
            target.doDie(p);
        }
        else
        {
            p.sendPacket(new SystemMessage(SystemMessage.THAT_IS_THE_INCORRECT_TARGET));
            return;
        }

	}

	public final int[] getItemIds()
	{
		return _itemIds;
	}

	public void onLoad()
	{
		ItemHandler.getInstance().registerItemHandler(this);
	}

	public void onReload()
	{}

	public void onShutdown()
	{}
}