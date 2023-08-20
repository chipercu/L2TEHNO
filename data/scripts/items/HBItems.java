package items;

import l2open.extensions.scripts.ScriptFile;
import l2open.gameserver.handler.IItemHandler;
import l2open.gameserver.handler.ItemHandler;
import l2open.gameserver.model.L2Character;
import l2open.gameserver.model.L2Playable;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.L2Skill;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.gameserver.model.items.L2ItemInstance;
import l2open.gameserver.model.instances.L2RemnantInstance;
import l2open.gameserver.serverpackets.SystemMessage;
import l2open.gameserver.tables.SkillTable;

public class HBItems implements IItemHandler, ScriptFile
{
	private static final int MAGIC_BOTTLE_ITEM = 9672;
	private static final int MAGIC_BOTTLE_SKILL = 2359;

	private static final int HOLY_WATER_ITEM = 9673;
	private static final int HOLY_WATER_SKILL = 2358;
	private static final int[] ITEM_IDS = {MAGIC_BOTTLE_ITEM, HOLY_WATER_ITEM};

	@Override
	public int[] getItemIds()
	{
		return ITEM_IDS;
	}

	@Override
	public void useItem(L2Playable playable, L2ItemInstance item, Boolean val)
	{
		L2Player p = (L2Player) playable;
		L2NpcInstance targetNPC = (L2NpcInstance) p.getTarget();

		if(playable == null || !playable.isPlayer())
			return;

		if(targetNPC == null)
		{
			p.sendPacket(new SystemMessage(SystemMessage.THAT_IS_THE_INCORRECT_TARGET));
			return;
		}

		int npcId = targetNPC.getNpcId();

		switch(item.getItemId())
		{
			case HOLY_WATER_ITEM:
			{
				if( !(p.getTarget() instanceof L2RemnantInstance) || p.getTarget() == null)
				{
					p.sendPacket(new SystemMessage(SystemMessage.THAT_IS_THE_INCORRECT_TARGET));
					return;
				}
				else if(((L2Character) p.getTarget()).getCurrentHp() == 0.5)
				{
					p.sendPacket(new SystemMessage(SystemMessage.THAT_IS_THE_INCORRECT_TARGET));
					return;
				}
				L2Skill skill = SkillTable.getInstance().getInfo(HOLY_WATER_SKILL, 1);
				p.doCast(skill, (L2Character) p.getTarget(), false);
				break;
			}
			case MAGIC_BOTTLE_ITEM:
			{
				if(p.getTarget() == null || !p.getTarget().isNpc() || npcId < 22349 && npcId > 22353)
				{
					p.sendPacket(new SystemMessage(SystemMessage.THAT_IS_THE_INCORRECT_TARGET));
					return;
				}
				else if(((L2Character) p.getTarget()).getCurrentHpPercents() > 10)
				{
					p.sendPacket(new SystemMessage(SystemMessage.THAT_IS_THE_INCORRECT_TARGET));
					return;
				}
				L2Skill skill = SkillTable.getInstance().getInfo(MAGIC_BOTTLE_SKILL, 1);
				p.doCast(skill, (L2Character) p.getTarget(), false);
			}
		}
	}

	@Override
	public void onLoad()
	{
		ItemHandler.getInstance().registerItemHandler(this);
	}

	@Override
	public void onReload()
	{}

	@Override
	public void onShutdown()
	{}
}