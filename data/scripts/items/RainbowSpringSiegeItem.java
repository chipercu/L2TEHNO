package items;

import l2open.extensions.scripts.ScriptFile;
import l2open.gameserver.handler.IItemHandler;
import l2open.gameserver.handler.ItemHandler;
import l2open.gameserver.model.L2Character;
import l2open.gameserver.model.L2Playable;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.L2Skill;
import l2open.gameserver.model.entity.siege.clanhall.RainbowSpringSiege;
import l2open.gameserver.model.items.L2ItemInstance;
import l2open.gameserver.serverpackets.SystemMessage;
import l2open.gameserver.tables.SkillTable;

/**
 * @author: Drizzy
 * @date: 26.10.2011
 * @Handler for Rainbow Spring Siege. Use item on one npc arena.
 */
 
public class RainbowSpringSiegeItem implements IItemHandler, ScriptFile
{
	//Item Id
	private static final int[] _itemIds = { 8030, 8031, 8032, 8033 };
	
	public void useItem(L2Playable playable, L2ItemInstance item, Boolean ctrl)
	{
		L2Player p = (L2Player) playable;
		L2Character target = (L2Character) p.getTarget();

		if(!playable.isPlayer())
			return;

		if(target == null)
		{
			p.sendPacket(new SystemMessage(SystemMessage.THAT_IS_THE_INCORRECT_TARGET));
			return;
		}
		if(p.getTarget().isPlayable())
		{
			p.sendPacket(new SystemMessage(SystemMessage.THAT_IS_THE_INCORRECT_TARGET));
			return;
		}
		if(RainbowSpringSiege.getInstance().usePotion(p, item.getItemId()))
		{
			int id = 0;
			switch (item.getItemId())
			{
				case 8030:
					id = 2240;
				break;
				case 8031:
					id = 2241;
				break;
				case 8032:
					id = 2242;
				break;
				case 8033:
					id = 2243;
				break;
			}
			L2Skill skill = SkillTable.getInstance().getInfo(id, 1);
			p.getAI().Cast(skill, target);
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
