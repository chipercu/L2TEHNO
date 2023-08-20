package items;

import l2open.extensions.scripts.ScriptFile;
import l2open.gameserver.cache.Msg;
import l2open.gameserver.handler.IItemHandler;
import l2open.gameserver.handler.ItemHandler;
import l2open.gameserver.model.L2Playable;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.L2Skill;
import l2open.gameserver.model.instances.L2MonsterInstance;
import l2open.gameserver.model.items.L2ItemInstance;
import l2open.gameserver.tables.SkillTable;

public class Harvester implements IItemHandler, ScriptFile
{
	private static final int[] _itemIds = { 5125 };
	L2Player player;
	L2MonsterInstance target;

	public void useItem(L2Playable playable, L2ItemInstance _item, Boolean ctrl)
	{
		if(playable == null || !playable.isPlayer())
			return;
		L2Player player = (L2Player) playable;

		// Цель не выделена, цель не моб
		if(player.getTarget() == null || !player.getTarget().isMonster())
		{
			player.sendPacket(Msg.THAT_IS_THE_INCORRECT_TARGET);
			return;
		}

		target = (L2MonsterInstance) player.getTarget();

		// Моб не мертвый
		if(!target.isDead() || target.isDying())
		{
			player.sendPacket(Msg.THAT_IS_THE_INCORRECT_TARGET);
			return;
		}

		L2Skill skill = SkillTable.getInstance().getInfo(2098, 1);
		if(skill != null && skill.checkCondition(player, target, false, false, true))
			player.getAI().Cast(skill, target);
		else
			return;
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